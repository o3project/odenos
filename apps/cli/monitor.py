#!/usr/bin/env python3.4
#
# $ sudo pip3 install redis
# $ sudo pip3 install msgpack-python

import redis
import msgpack
from io import BytesIO
import traceback
from copy import copy

TYPE_REQUEST = 0
TYPE_RESPONSE = 1
TYPE_EVENT = 2
TYPE_REFLECTED_EVENT = 99

REQUEST = 'REQUEST' 
RESPONSE = 'RESPONSE'
EVENT = 'EVENT' 

REFLECTED_EVENT_PATTERN = 'reflected_event'
REFLECTED_EVENT_PATTERN_BYTE = b'reflected_event'

r = redis.StrictRedis(host='localhost', port=6379, db=0)
p = r.pubsub(ignore_subscribe_messages=True)
p.psubscribe('*', REFLECTED_EVENT_PATTERN) 

serial = 0;

HEADER_FORMAT = ' '
VERTICAL_LINES_FORMAT = '    ' 
ARROW_DEFAULT = []
GET_RIGHT =     '----- GET ---->'
GET_LEFT =      '<---- GET -----'
POST_RIGHT =    '----- POST --->'
POST_LEFT =     '<--- POST -----'
PUT_RIGHT =     '----- PUT ---->'
PUT_LEFT =      '<---- PUT -----'
DELETE_RIGHT =  '---- DELETE -->'
DELETE_LEFT =   '<-- DELETE ----'
RESPONSE_RIGHT =    '----- {} ---->'
RESPONSE_LEFT =     '<---- {} -----'
EVENT_RIGHT =       '---- EVENT --->'
EVENT_LEFT =        '<--- EVENT ----'
BAR =         '---------------'  
EMPTY =       '               '  
REQUEST_LEFT = {'GET': GET_LEFT,  'POST': POST_LEFT, 'PUT': PUT_LEFT, 'DELETE': DELETE_LEFT}
REQUEST_RIGHT = {'GET': GET_RIGHT, 'POST': POST_RIGHT, 'PUT': PUT_RIGHT, 'DELETE': DELETE_RIGHT}

BODY_SUMMARY = '{0} {1} [body detail]'
BODY_SUMMARY_EVENT = '{0} [body detail]'

OBJECT_IDS = None 

def monitor():
    global serial
    while True:  # inifinite loop
        for msg in p.listen(): # blocks untile new message is received.
            pattern = msg['pattern']
            type_ = msg['type'] # Redis message type
            dstid = msg['channel'].decode(encoding='utf-8') # Redis pubsub channel
            if type_ != 'message' and type_ != 'pmessage':
                    continue
            else: # subscribed events
                try:
                    bio = BytesIO()
                    bio.write(msg['data'])
                    bio.seek(0)
                    upk = msgpack.Unpacker(bio)
                    tp = upk.unpack()
                    sno = upk.unpack()
                    srcid = upk.unpack().decode('ascii')
                    body = upk.unpack()
                    method = None
                    path = None
                    status = None
                    subscriber_id = None
                    publisher_id = None
                    event_type = None
                    message_type = '*'
                    if tp == TYPE_REQUEST:
                        message_type = REQUEST
                        method = body[1].decode('ascii')
                        path = '/{}/{}'.format(body[0].decode('ascii'), body[2].decode('ascii'))
                        write_sequence(serial, message_type, dstid, srcid, sno, path, method, status, event_type, body)
                        serial += 1
                    elif tp == TYPE_RESPONSE:
                        message_type = RESPONSE
                        status = body[0]
                        path = '' 
                        write_sequence(serial, message_type, dstid, srcid, sno, path, method, status, event_type, body)
                        serial += 1
                    elif pattern == REFLECTED_EVENT_PATTERN_BYTE and tp == TYPE_REFLECTED_EVENT:
                        message_type = EVENT
                        dstid = srcid
                        srcid = body[0].decode('ascii')
                        event_type = body[1].decode('ascii')
                        write_sequence(serial, message_type, dstid, srcid, sno, path, method, status, event_type, body)
                        serial += 1
                    #print('serial: {}, type: {}, dstid: {}, srcid: {}: sno: {}'.format(serial, message_type, dstid, srcid, sno))
                    #print('serial: {}, type: {}, dstid: {}, srcid: {}: sno: {}, data: {}'.format(serial, message_type, dstid, srcid, sno, body))
                    #print('')
                except:
                    traceback.print_exc()
                    pass


def setup(*args, **kwargs):
    '''
    Writes sequence diagram header    
   
    0         1         2         3         4
    01234567890123456789012345678901234567890
     4-3             4+16-3          4+16*2-3
     object1         object2         object3
        |               |<- REQUEST ----|   0 REQUEST GET sno path [body]
        |<--------------|               |   1 REQUEST GET sno path [body]
        |-------------->|               |   2 200 OK path sno [body]
        |               |-- RESPONSE -->|   3 200 OK path sno [body]
        |-- EVENT --------------------->|   4 EVENT [body]
         
    <html>
    <header></header>
    <body>
    <a name="0">
    <pre>
    serial: 0
    REQUEST GET (10) [path]
    
    body in JSON
    </pre>
    <a name="1">
    <pre>
    serial: 1
    200 OK (10) [path]
    
    body in JSON
    </pre>
    </body>
               
    '''
    global OBJECT_IDS
    global HEADER_FORMAT
    global VERTICAL_LINES_FORMAT
    global ARROW_DEFAULT
    OBJECT_IDS = kwargs['object_ids']
    object_number = len(OBJECT_IDS)
    c = 0
    cc = 0
    for count in range(object_number):
        HEADER_FORMAT += '{'+str(c)+':<16}'  
        c += 1
        if count < object_number - 1:
            VERTICAL_LINES_FORMAT += '{'+str(cc)+'}{'+str(cc+1)+':<15}'
            ARROW_DEFAULT.append('|')
            ARROW_DEFAULT.append(EMPTY)
        else:
            VERTICAL_LINES_FORMAT += '{'+str(cc)+'}'
            ARROW_DEFAULT.append('|')
        cc += 2
    print(HEADER_FORMAT.format(*OBJECT_IDS))
    #print(VERTICAL_LINES_FORMAT.format('|', BAR, '|', BAR, '|'))


def write_sequence(serial, message_type, dstid, srcid, sno, path, method, status, event_type, body):
    '''
    Writes sequence
    '''
    object_number = len(OBJECT_IDS)
    
    dstid_idx = -1 
    srcid_idx = -1

    if dstid in OBJECT_IDS:
        dstid_idx = OBJECT_IDS.index(dstid)
    if srcid in OBJECT_IDS:
        srcid_idx = OBJECT_IDS.index(srcid)
    #print(dstid)
    #print(srcid)
    #print('--')
    if (dstid_idx < 0) or (srcid_idx < 0):
        pass
    else:

        arrow = copy(ARROW_DEFAULT)
        #print(arrow)

        LEFT = None
        RIGHT = None
        if message_type == REQUEST:
            LEFT = REQUEST_LEFT[method]
            RIGHT = REQUEST_RIGHT[method]
        elif message_type == RESPONSE:
            LEFT = RESPONSE_LEFT.format(status)
            RIGHT = RESPONSE_RIGHT.format(status)
        elif message_type == EVENT:
            LEFT = EVENT_LEFT
            RIGHT = EVENT_RIGHT

        if (dstid_idx > srcid_idx):  # Drows an arrow to the right
            c = srcid_idx
            for count in range(srcid_idx, dstid_idx):
                C = c * 2
                if c == srcid_idx and dstid_idx - srcid_idx > 1:
                    arrow[C+1] = BAR
                elif c == srcid_idx and dstid_idx - srcid_idx == 1:
                    arrow[C+1] = RIGHT
                elif c == dstid_idx - 1:
                    arrow[C] = '-'
                    arrow[C+1] = RIGHT 
                    break
                else:
                    arrow[C] = '-' 
                    arrow[C+1] = BAR 
                c += 1 
        else:  # Draws an arrow to the left
            c = dstid_idx
            for count in range(dstid_idx, srcid_idx):
                C = c * 2
                if c == dstid_idx:
                    arrow[C+1] = LEFT
                elif c == srcid_idx:
                    arrow[C] = '-'
                    arrow[C+1] = BAR 
                    break
                else:
                    arrow[C] = '-'
                    arrow[C+1] = BAR 
                c += 1 

        print(VERTICAL_LINES_FORMAT.format(*arrow), end='')
        if (message_type == EVENT):
            print('  ' + BODY_SUMMARY_EVENT.format(event_type))
        else:
            print('  ' + BODY_SUMMARY.format(sno, path))

if __name__ == '__main__':
    
    kwargs = {}
    kwargs['object_ids'] = ['resttranslator', 'systemmanager', 'romgr1', 'gen', 'network1', 'aggre', 'network0', 'lsw'] 
    setup(**kwargs)
    monitor()


