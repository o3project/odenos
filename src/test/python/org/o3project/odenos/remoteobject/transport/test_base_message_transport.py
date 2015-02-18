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


from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.transport.base_message_transport\
    import BaseMessageTransport

import unittest


class RemoteMessageTransportTest(unittest.TestCase):
    def setUp(self):
        self.target = BaseMessageTransport(
            "LocalMessageTransport")

    def tearDown(self):
        self.target = None

    def test_Future_constructor(self):
        self.target = self.target.Future()

        self.assertEqual(self.target._Future__response, None)
        self.assertEqual(self.target._Future__response_obtained, False)

    def test_Future_join(self):
        self.target = self.target.Future()

        try:
            result = self.target.join()

        except NotImplementedError:
            pass

    def test_Future_set(self):
        self.target = self.target.Future()
        response = Response(200, "body")

        self.target.set(response)

        self.assertEqual(self.target._Future__response, response)
        self.assertEqual(self.target._Future__response_obtained, True)

    def test_Future_get(self):
        self.target = self.target.Future()
        self.target._Future__response = "response"
        self.target._Future__response_obtaine = True

        try:
            result = self.target.get()

        except NotImplementedError:
            pass

    def test_Future_result_response_obtained_True(self):
        self.target = self.target.Future()
        response = Response(200, "body")
        self.target.set(response)

        self.assertEqual(self.target.result, response)

    def test_Future_result_response_obtained_False(self):
        self.target = self.target.Future()

        self.assertEqual(self.target.result, None)

    def test_constructor(self):
        self.assertEqual(self.target.object_id, "LocalMessageTransport")

    def test_send_request_message(self):
        try:
            self.target.send_request_message("request_obj")

        except NotImplementedError:
            pass

    def test_close(self):
        try:
            self.target.close()

        except NotImplementedError:
            pass

if __name__ == "__main__":
    unittest.main()
