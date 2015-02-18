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

from org.o3project.odenos.core.component.network.flow.basic.flow_action_output\
    import FlowActionOutput
import unittest


class FlowActionOutputTest(unittest.TestCase):
    Type = "FlowActionOutput"
    Output = "ANY"

    def setUp(self):
        self.target = FlowActionOutput(self.Type, self.Output)

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE], self.Type)
        self.assertEqual(self.target._body[self.target.OUTPUT], self.Output)

    def test_output(self):
        self.assertEqual(self.target.output, self.Output)

    def test_create_from_packed(self):
        self.value = {"type": self.Type,
                      "output": "Output01"}
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         "FlowActionOutput")
        self.assertEqual(self.result._body[self.target.OUTPUT], "Output01")

    def test_packed_object(self):
        self.result = self.target.packed_object()
        self.assertEqual(self.result[self.target.TYPE], self.Type)
        self.assertEqual(self.result[self.target.OUTPUT], self.Output)

if __name__ == '__main__':
    unittest.main()
