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


from org.o3project.odenos.remoteobject.manager.component.component_type\
    import ComponentType
import unittest

class ComponentTypeTest(unittest.TestCase):
    _type = "DummyDriver"
    _super_type = "Driver"
    _description = "Description"
    _connection_types = {}

    def setUp(self):
        self.target = ComponentType(self._type, self._super_type,
                                    self._connection_types, self._description) 
        self.target2 = ComponentType(None, None, None, None) 

    def tearDown(self):
        self.target = None
        self.target2 = None

    def test_constructor(self):
        self.assertEqual(self.target._body[self.target.TYPE],
                         self._type)
        self.assertEqual(self.target._body[self.target.SUPER_TYPE],
                         self._super_type)
        self.assertEqual(self.target._body[self.target.CONNECTION_TYPES],
                         self._connection_types)
        self.assertEqual(self.target._body[self.target.DESCRIPTION],
                         self._description)
        self.assertEqual(self.target2._body[self.target.TYPE], "")
        self.assertEqual(self.target2._body[self.target.SUPER_TYPE], "")
        self.assertEqual(len(self.target2._body[self.target.CONNECTION_TYPES]), 0)
        self.assertEqual(self.target2._body[self.target.DESCRIPTION], "")

    def test_type_property(self):
        self.assertEqual(self.target.type, self._type)

    def test_super_type_property(self):
        self.assertEqual(self.target.super_type, self._super_type)

    def test_connection_types_property(self):
        self.assertEqual(self.target.connection_types, self._connection_types)

    def test_description_property(self):
        self.assertEqual(self.target.description, self._description)

    def test_create_from_packed_Version_NotNone_And_packed_object(self):
        self.value = {"type": self._type,
                      "super_type": self._super_type, 
                      "connection_types": self._connection_types, 
                      "description": self._description
                      }
        # create_from_packed_test
        self.result = self.target.create_from_packed(self.value)
        self.assertEqual(self.result._body[self.target.TYPE],
                         self._type)
        self.assertEqual(self.result._body[self.target.SUPER_TYPE],
                         self._super_type)
        self.assertEqual(self.result._body[self.target.CONNECTION_TYPES],
                         self._connection_types)
        self.assertEqual(self.result._body[self.target.DESCRIPTION],
                         self._description)

        # packed_object test
        self.result2 = self.result.packed_object()
        self.assertEqual(self.result2[self.target.TYPE],
                         self._type)
        self.assertEqual(self.result2[self.target.SUPER_TYPE],
                         self._super_type)
        self.assertEqual(self.result2[self.target.CONNECTION_TYPES],
                         self._connection_types)
        self.assertEqual(self.result2[self.target.DESCRIPTION],
                         self._description)

if __name__ == '__main__':
    unittest.main()
