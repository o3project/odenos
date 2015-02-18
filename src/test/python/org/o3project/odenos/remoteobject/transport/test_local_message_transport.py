# -*- coding:utf-8 -*-

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


from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.transport.local_message_transport\
    import LocalMessageTransport

import unittest
from mock import MagicMock


class RemoteMessageTransportTest(unittest.TestCase):
    Dispatcher = MagicMock()

    def setUp(self):
        self.target = LocalMessageTransport(
            "LocalMessageTransport",
            self.Dispatcher)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target.object_id, "LocalMessageTransport")
        self.assertEqual(self.target.dispatcher, self.Dispatcher)

    def test_send_request_message(self):
        request = Request("object_id", "method", "path")
        response = Response(200, "body")

        self.target.dispatcher.dispatch_request =\
            MagicMock(return_value=response)

        result = self.target.send_request_message(request)

        self.assertEqual(result.status_code, 200)
        self.assertEqual(result.body, "body")

    def test_close(self):
        self.target.close()

if __name__ == "__main__":
    unittest.main()
