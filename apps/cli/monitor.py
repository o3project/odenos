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

REQUEST = 'REQUEST' 
RESPONSE = 'RESPONSE'
EVENT = 'EVENT' 

r = redis.StrictRedis(host='localhost', port=6379, db=0)
p = r.pubsub(ignore_subscribe_messages=True)
p.psubscribe('*')

message_type = None;

serial = 0;

HEADER_FORMAT = ' '
VERTICAL_LINES_FORMAT = '    ' 
ARROW_DEFAULT = []
REQUEST_RIGHT =     '--- REQUEST -->'
REQUEST_LEFT =      '<-- REQUEST ---'
RESPONSE_RIGHT =    '-- RESPONSE -->'
RESPONSE_LEFT =     '<- RESPONSE ---'
EVENT_RIGHT =       '---- EVENT --->'
EVENT_LEFT =        '<--- EVENT ----'
ARROW_LEFT =  '<'
ARROW_RIGHT = '>'
BAR =         '---------------'  
EMPTY =       '               '  
OBJECT_IDS = None 

def monitor():
    global serial
    while True:  # inifinite loop
        for msg in p.listen(): # blocks untile new message is received.
            type_ = msg['type'] # Redis message type
            dstid = msg['channel'].decode(encoding='utf-8') # Redis pubsub channel
            if type_ != 'message' and type_ != 'pmessage':
                    continue
            else: # subscribed events
                try:
                    bio = BytesIO()
                    bio.write(msg['data'])
                    bio.seek(0)
                    upk = msgpack.Unpacker(bio, encoding='utf-8')
                    tp = upk.unpack()
                    sno = upk.unpack()
                    srcid = upk.unpack()
                    if tp == TYPE_REQUEST:
                        message_type = REQUEST
                    elif tp == TYPE_RESPONSE:
                        message_type = RESPONSE
                    elif tp == TYPE_EVENT:
                        message_type = EVENT
                    body = upk.unpack()
                    #print('serial: {}, type: {}, dstid: {}, srcid: {}: sno: {}, data: {}'.format(serial, message_type, dstid, srcid, sno, body))
                    #print('')
                    write_sequence(serial, message_type, dstid, srcid, sno, body)
                    serial += 1
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


def write_sequence(serial, message_type, dstid, srcid, sno, body):
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
        if (message_type == REQUEST):
            LEFT = REQUEST_LEFT
            RIGHT = REQUEST_RIGHT
        elif (message_type == RESPONSE):
            LEFT = RESPONSE_LEFT
            RIGHT = RESPONSE_RIGHT
        elif (message_type == EVENT):
            LEFT = RESPONSE_LEFT
            RIGHT = RESPONSE_RIGHT

        if (dstid_idx > srcid_idx):  # Drows an arrow to the right
            c = srcid_idx
            for count in range(srcid_idx, dstid_idx):
                if (c == srcid_idx and dstid_idx - srcid_idx > 1):
                    arrow[c*2+1] = BAR
                elif (c == srcid_idx and dstid_idx - srcid_idx == 1):
                    arrow[c*2+1] = RIGHT
                elif (c == dstid_idx - 1):
                    arrow[c*2] = '-'
                    arrow[c*2+1] = RIGHT 
                    break
                else:
                    arrow[c*2] = '-' 
                    arrow[c*2+1] = BAR 
                c += 1 
        else:  # Draws an arrow to the left
            c = dstid_idx
            for count in range(dstid_idx, srcid_idx):
                if (c == dstid_idx):
                    arrow[c*2+1] = LEFT
                else:
                    arrow[c*2] = '-'
                    arrow[c*2+1] = BAR 
                    break
                c += 1 

        print(VERTICAL_LINES_FORMAT.format(*arrow))

if __name__ == '__main__':
    
    kwargs = {}
    kwargs['object_ids'] = ['resttranslator', 'systemmanager', 'romgr1', 'gen', 'network1', 'aggre', 'network0', 'lsw'] 
    setup(**kwargs)
    monitor()


