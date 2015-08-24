
# Copyright 2015 NEC Corporation.                                          #
#                                                                          #
# Licensed under the Apache License, Version 2.0 (the "License");          #
# you may not use this file except in compliance with the License.         #
# You may obtain a copy of the License at                                  #
#                                                                          #
#   http://www.apache.org/licenses/LICENSE-2.0                             #
#                                                                          #
# Unless required by applicable law or agreed to in writing, software      #
# distributed under the License is distributed on an "AS IS" BASIS,        #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
# See the License for the specific language governing permissions and      #
# limitations under the License.                                           #

import httplib2
from io import StringIO
import json
import traceback
import sys
import pprint

from util import dict2yaml

json_loads = json.loads

def build_send_request_function(url, headers=None):
    """
    REST Transaction
    returns send_request
    """
    if not headers:
        headers = {'Content-type': 'application/json', 'Accept': 'application/json'}
    headers['Connection'] = 'Keep-Alive'
    conn = httplib2.Http()

    def send_request(method, path, params, body, verbose=False, print_json=False):
        """
        REST Transaction
        """
        
        buf = StringIO()
        out = lambda s: buf.write(s+'\n')
        nl = lambda: buf.write('\n') 
        body_json = None
        
        print('{}: {}'.format(method, path))
        if params:
            path = "{}?{}".format(path, params)
            if verbose:
                print(dict2yaml(params))
                print('')
        if body:
            body_json = json.dumps(body)
            if verbose:
                print(dict2yaml(body))
                print('')

        if verbose:
            buf.write('- - - Request - - -\n')
            buf.write('{} {}\n'.format(method, path))
            if body:
                nl()
                out(str(dict2yaml(body)))
        try:
            response = res_body = None

            response, _res_body = conn.request(url+path, method, body_json, headers) 
            res_body = _res_body.decode('ascii')

            length = None 
            encoding = None
            if 'content-length' in response:
                length = response['content-length']
            if 'transfer-encoding' in response:
                encoding = response['transfer-encoding']
                
            if verbose:
                out('- - - Response - - -')
                out('status: {}'.format(response.status))
                out('reason: {}'.format(response.reason))
                if length:
                    out('content-length: {}'.format(length))
                if encoding:
                    out('transfer-encoding: {}'.format(encoding))
                nl()
            else:
                if not response['status'].startswith('2'):  # 2XX
                    out('[Error] status: {}, reason: {}'.format(response.status, response.reason))

            # TODO: REST API responses should include Content-Type
            if length or encoding == 'chunked':
                if res_body.startswith('{') or res_body.startswith('['):  # Must be application/json
                    if length and int(length) > 1 or encoding == 'chunked':
                        if print_json:
                            #out(str(res_body))
                            out(pprint.pformat(json_loads(res_body)))
                        else:  # YAML
                            out(str(dict2yaml(json_loads(res_body))))
                else:  # Must be text/plain
                    if int(length) > 0:
                        out(res_body)
                        res_body = '{}'  # for json.loads
                    else:
                        res_body = '{}'  # for json.loads
            else:
                res_body = '{}'

            buf.seek(0)
            output = buf.read()
            buf.close()
            return (response, json_loads(res_body), output)
        except Exception:
            traceback.print_exc()
            print('[Error] REST transaction failure')
            print('[Hint] check if the target REST server is running')
            print('[Hint] you might need to unset http_proxy')
            sys.exit(1)

    
    return send_request
