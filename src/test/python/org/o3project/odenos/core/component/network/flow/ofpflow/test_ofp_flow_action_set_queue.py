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

from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_action_set_queue\
    import OFPFlowActionSetQueue

import unittest


class OFPFlowActionSetQueueTest(unittest.TestCase):

    def setUp(self):
        self.target = OFPFlowActionSetQueue("OFPFlowActionSetQueue",
                                                 1234)

    def tearDown(self):
        self.target = None

    def test_constractor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         "OFPFlowActionSetQueue")
        self.assertEqual(self.target._body[self.target.QUEUE_ID],
                         1234)

    def test_queue_id(self):
        self.assertEqual(self.target.queue_id, 1234)

    def test_create_from_packed(self):
        self.value = {self.target.TYPE: "OFPFlowActionSetQueue",
                      self.target.QUEUE_ID: 4321}

        self.result = OFPFlowActionSetQueue.create_from_packed(self.value)

        self.assertEqual(self.result._body[self.target.TYPE],
                         "OFPFlowActionSetQueue")
        self.assertEqual(self.result._body[self.target.QUEUE_ID],
                         4321)

    def test_packed_object(self):
        self.result = self.target.packed_object()

        self.assertEqual(self.result[self.target.TYPE],
                         "OFPFlowActionSetQueue")
        self.assertEqual(self.result[self.target.QUEUE_ID],
                         1234)

if __name__ == '__main__':
    unittest.main()
