#!/usr/bin/env python3.4

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

# $ sudo pip3 install tornado

import tornado.httpserver
import tornado.websocket
import tornado.ioloop
import tornado.web

import monitor

from concurrent.futures import ThreadPoolExecutor

class WSHandler(tornado.websocket.WebSocketHandler):

    # TODO: security check
    def check_origin(self, origin):
        return True

    def open(self):
        kwargs = {}
        kwargs['output'] = self.write_message
        kwargs['object_ids'] = ['resttranslator', 'systemmanager', 'romgr1', 'gen', 'network1', 'aggre', 'network0', 'lsw']  
        kwargs['hyperlink'] = True
        self.executor = ThreadPoolExecutor(max_workers=1)
        self.executor.submit(self.run, **kwargs)
      
    def on_close(self):
        self.executor.shutdown()

    def run(self, **kwargs):
        monitor.Monitor(**kwargs).start()



if __name__ == "__main__":
    application = tornado.web.Application([
        (r'/server', WSHandler),
    ])
    http_server = tornado.httpserver.HTTPServer(application)
    http_server.listen(8888)
    tornado.ioloop.IOLoop.instance().start()
