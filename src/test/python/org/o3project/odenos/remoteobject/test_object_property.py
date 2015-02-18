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

import unittest
from org.o3project.odenos.remoteobject.object_property import ObjectProperty


class ObjectPropetyTest(unittest.TestCase):
    def setUp(self):
        self.target = ObjectProperty("object_type", "object_id")

    def test_constructor(self):
        self.assertEqual(
            self.target._object_property[ObjectProperty.OBJECT_TYPE],
            "object_type")
        self.assertEqual(
            self.target._object_property[ObjectProperty.OBJECT_ID],
            "object_id")

    def test_object_type(self):
        self.assertEqual(self.target.object_type, "object_type")

    def test_object_id(self):
        self.assertEqual(self.target.object_id, "object_id")

    def test_get_state(self):
        self.target._object_property[ObjectProperty.OBJECT_STATE] =\
            "object_state"
        self.assertEqual(self.target.get_state(), "object_state")
        self.target._object_property[ObjectProperty.OBJECT_STATE] =\
            "OBJECT_STATE"
        self.assertEqual(self.target.get_state(), "OBJECT_STATE")

    def test_set_state(self):
        self.target._object_property[ObjectProperty.OBJECT_STATE] =\
            "no such state"
        self.target.set_state("object_state")
        self.assertEqual(
            self.target._object_property[ObjectProperty.OBJECT_STATE],
            "object_state")

    def test_set_property_with_object_type(self):
        oldValue = self.target.set_property(
            ObjectProperty.OBJECT_TYPE, "new value")
        self.assertEqual(oldValue, None)
        self.assertEqual(
            self.target._object_property[ObjectProperty.OBJECT_TYPE],
            "object_type")

    def test_set_property_with_object_id(self):
        oldValue = self.target.set_property(ObjectProperty.OBJECT_ID,
                                            "new value")
        self.assertEqual(oldValue, None)
        self.assertEqual(
            self.target._object_property[ObjectProperty.OBJECT_ID],
            "object_id")

    def test_set_property_with_new_key(self):
        oldValue = self.target.set_property("new key", "new value")
        self.assertEqual(oldValue, None)
        self.assertEqual(self.target._object_property["new key"], "new value")

    def test_set_property(self):
        self.target._object_property["key"] = "old value"
        oldValue = self.target.set_property("key", "new value")
        self.assertEqual(oldValue, "old value")
        self.assertEqual(self.target._object_property["key"], "new value")

    def test_get_property_with_existing_key(self):
        self.target._object_property["key"] = "value"
        self.assertEqual(self.target.get_property("key"), "value")

    def test_get_property_with_new_key(self):
        self.assertEqual(self.target.get_property("key"), None)

    def test_delete_property(self):
        self.target._object_property["key"] = "value"
        self.target.delete_property("key")
        self.assertEqual(self.target.get_property("key"), None)

    def test_delete_property_with_read_only_key(self):
        self.target._object_property["super_type"] = "super_type"
        self.target._object_property["description"] = "description"
        self.target.delete_property("type")
        self.target.delete_property("id")
        self.target.delete_property("super_type")
        self.target.delete_property("description")
        self.assertEqual(self.target.get_property("type"), "object_type")
        self.assertEqual(self.target.get_property("id"), "object_id")
        self.assertEqual(self.target.get_property("super_type"), "super_type")
        self.assertEqual(self.target.get_property("description"),
                         "description")

    def test_put_property(self):
        prop = {ObjectProperty.OBJECT_TYPE: "new_object_type",
                ObjectProperty.OBJECT_ID: "new_object_id"}
        prop["key1"] = "new_val"
        prop["key3"] = "val3"
        self.target._object_property["key1"] = "val1"
        self.target._object_property["key2"] = "val2"
        self.target.put_property(prop)
        self.assertEqual(self.target.get_property("type"), "object_type")
        self.assertEqual(self.target.get_property("id"), "object_id")
        self.assertEqual(self.target.get_property("key1"), "new_val")
        self.assertEqual(self.target.get_property("key2"), None)
        self.assertEqual(self.target.get_property("key3"), "val3")

    def test_equals(self):
        prop = {ObjectProperty.OBJECT_TYPE: "object_type",
                ObjectProperty.OBJECT_ID: "object_id"}
        self.assertTrue(self.target.equals(prop))

    def test_equals_with_different_property(self):
        prop = ObjectProperty("different_object_type", "object_id")
        self.assertFalse(self.target.equals(prop))

    def test_is_read_only_key(self):
        self.target._object_property["key"] = "value"
        self.assertFalse(self.target._ObjectProperty__is_read_only_key("key"))

    def test_is_read_only_key_with_read_only_key(self):
        self.target._object_property["super_type"] = "super_type"
        self.target._object_property["description"] = "description"
        self.assertTrue(
            self.target._ObjectProperty__is_read_only_key("type"))
        self.assertTrue(
            self.target._ObjectProperty__is_read_only_key("id"))
        self.assertTrue(
            self.target._ObjectProperty__is_read_only_key("super_type"))
        self.assertTrue(
            self.target._ObjectProperty__is_read_only_key("description"))

    def test_packed_object(self):
        self.assertEqual(self.target.packed_object(),
                         {ObjectProperty.OBJECT_TYPE: 'object_type',
                          ObjectProperty.OBJECT_ID: 'object_id'})

if __name__ == '__main__':
    unittest.main()
