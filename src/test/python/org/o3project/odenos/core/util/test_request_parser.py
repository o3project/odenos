
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

import unittest
from org.o3project.odenos.remoteobject.message.response import Response
from org.o3project.odenos.remoteobject.message.request import Request
from org.o3project.odenos.core.util.request_parser import RequestParser

class TestRequestParser(unittest.TestCase):

    def dummyFunc0(self):
        self.call_func0 += 1
        return Response(101, None)
    
    def dummyFunc1(self, body, *mached):
        self.call_func1 += 1
        self.func1_body = body
        return Response(102, None)

    def setUp(self):
        self.call_func0 = 0
        self.call_func1 = 0
        self.func1_body = None
        self.target = RequestParser()

    def tearDown(self):
        pass

    def test_constructor(self):
        self.assertEqual(self.target._RequestParser__rules, [])

    def test_add_rule(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0}])
        self.target.add_rule([{"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 1}])
        self.assertEqual(self.target._RequestParser__rules,
                         [{"pattern": "(param1)", "method": "GET",
                           "func": self.dummyFunc0, "params": 0},
                          {"pattern": "(param2)", "method": "PUT",
                           "func": self.dummyFunc1, "params": 1}])

    def test_action(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 1}])
        request = Request("id1", "GET", "path/to/param1", None)
        response = self.target.action(request)
        self.assertEqual(self.call_func0, 1)
        self.assertEqual(response.status_code, 101)
        
    def test_action_with_param_attribute_params_is_1(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 1}])
        request = Request("id1", "PUT", "path/to/param2", "body")
        response = self.target.action(request)
        self.assertEqual(self.call_func1, 1)
        self.assertEqual(self.func1_body, "body")
        self.assertEqual(response.status_code, 102)

    def test_action_with_param_attribute_params_is_1_and_null_body_request(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 1}])
        request = Request("id1", "PUT", "path/to/param2", None)
        response = self.target.action(request)
        self.assertEqual(self.call_func1, 1)
        self.assertEqual(self.func1_body, "param2")
        self.assertEqual(response.status_code, 102)

    def test_action_with_param_attribute_params_is_2(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 2}])
        request = Request("id1", "PUT", "path/to/param2", "body")
        response = self.target.action(request)
        self.assertEqual(self.call_func1, 1)
        self.assertEqual(self.func1_body, "body")
        self.assertEqual(response.status_code, 102)

    def test_action_with_param_attribute_params_is_2_and_null_body_request(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 2}])
        request = Request("id1", "PUT", "path/to/param2", None)
        response = self.target.action(request)
        self.assertEqual(self.call_func1, 1)
        self.assertEqual(self.func1_body, "param2")
        self.assertEqual(response.status_code, 102)

    def test_action_with_pattern_mached_but_method_not_matched(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 1}])
        request = Request("id1", "GET", "path/to/param2", "body")
        response = self.target.action(request)
        self.assertEqual(self.call_func0, 0)
        self.assertEqual(self.call_func1, 0)
        self.assertEqual(self.func1_body, None)
        self.assertEqual(response.status_code, 405)

    def test_action_with_pattern_not_matched(self):
        self.target.add_rule([{"pattern": "(param1)", "method": "GET",
                               "func": self.dummyFunc0, "params": 0},
                              {"pattern": "(param2)", "method": "PUT",
                               "func": self.dummyFunc1, "params": 1}])
        request = Request("id1", "GET", "path/to/param3", "body")
        response = self.target.action(request)
        self.assertEqual(self.call_func0, 0)
        self.assertEqual(self.call_func1, 0)
        self.assertEqual(self.func1_body, None)
        self.assertEqual(response.status_code, 404)

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.test_constructor']
    unittest.main()
