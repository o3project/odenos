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

from org.o3project.odenos.core.component.network.flow.ofpflow.ofp_flow_action_experimenter\
    import OFPFlowActionExperimenter

import unittest


class OFPFlowActionExperimenterTest(unittest.TestCase):

    def setUp(self):
        self.target = OFPFlowActionExperimenter("OFPFlowActionExperimenter",
                                          123456789,
                                          147258369)

    def tearDown(self):
        self.target = None

    def test_constractor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         "OFPFlowActionExperimenter")
        self.assertEqual(self.target._body[self.target.EXPERIMENTER_ID],
                         123456789)
        self.assertEqual(self.target._body[self.target.BODY],
                         147258369)

    def test_experimenter_id(self):
        self.assertEqual(self.target.experimenter_id, 123456789)

    def test_body(self):
        self.assertEqual(self.target.body, 147258369)

    def test_create_from_packed(self):
        self.value = {self.target.TYPE: "OFPFlowActionExperimenter",
                      self.target.EXPERIMENTER_ID: 987654321,
                      self.target.BODY: 963852741}

        self.result = OFPFlowActionExperimenter.create_from_packed(self.value)

        self.assertEqual(self.result._body[self.target.TYPE],
                         "OFPFlowActionExperimenter")
        self.assertEqual(self.result._body[self.target.EXPERIMENTER_ID],
                         987654321)
        self.assertEqual(self.result._body[self.target.BODY],
                         963852741)

    def test_packed_object(self):
        self.result = self.target.packed_object()

        self.assertEqual(self.result[self.target.TYPE], "OFPFlowActionExperimenter")
        self.assertEqual(self.result[self.target.EXPERIMENTER_ID], 123456789)
        self.assertEqual(self.result[self.target.BODY], 147258369)

if __name__ == '__main__':
    unittest.main()
