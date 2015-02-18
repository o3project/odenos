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
from org.o3project.odenos.core.component.conversion_table\
    import ConversionTable


class ConversionTableTest(unittest.TestCase):
    value = {}
    result = {}

    def setUp(self):
        self.target = ConversionTable()

    def tearDown(self):
        self.target = None

    def test_constructor(self):
        self.assertEqual(
            self.target._ConversionTable__connection_type_map, {})
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table, {})
        self.assertEqual(
            self.target._ConversionTable__node_conversion_table, {})
        self.assertEqual(
            self.target._ConversionTable__port_conversion_table, {})
        self.assertEqual(
            self.target._ConversionTable__link_conversion_table, {})
        self.assertEqual(
            self.target._ConversionTable__flow_conversion_table, {})

    def test_get_connection_type_hit(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "GetType"}
        self.assertEqual(
            self.target.get_connection_type("123456789"), "GetType")

    def test_get_connection_type_No_hit(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "GetType"}
        self.assertEqual(
            self.target.get_connection_type("987654321"), None)

    def test_get_connection_list(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "Type1", "321654987": "Type2",
             "789456123": "Type3", "987654321": "Type1"}
        self.assertEqual(self.target.get_connection_list("Type1"),
                         ["123456789", "987654321"])

    def test_is_connection_type_True(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "GetType"}
        self.assertEqual(
            self.target.is_connection_type("GetType"), True)

    def test_is_connection_type_len_Zero(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "GetType"}
        self.assertEqual(
            self.target.is_connection_type("Type3"), False)

    def test_add_entry_connection_type(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "GetType"}
        self.target.add_entry_connection_type("789456123", "GetType2")

        self.assertEqual(
            self.target._ConversionTable__connection_type_map,
            {"123456789": "GetType", "789456123": "GetType2"})

    def test_del_entry_connection_type(self):
        self.target._ConversionTable__connection_type_map =\
            {"123456789": "Type1", "321654987": "Type2",
             "789456123": "Type3", "987654321": "Type1"}

        self.target.del_entry_connection_type("321654987")

        self.assertEqual(
            self.target._ConversionTable__connection_type_map,
            {"123456789": "Type1", "789456123": "Type3", "987654321": "Type1"})

    def test_get_network(self):
        self.target._ConversionTable__network_conversion_table =\
            {"123456789": ["GetNetwork"]}
        self.assertEqual(
            self.target.get_network("123456789"), ["GetNetwork"])

    def test_get_node(self):
        self.target._ConversionTable__node_conversion_table =\
            {"123456789::987654321": ["GetNode"]}
        self.assertEqual(
            self.target.get_node("123456789", "987654321"),
            ["GetNode"])

    def test_get_port(self):
        self.target._ConversionTable__port_conversion_table =\
            {"123456789::987654321::789456123": ["GetPort"]}
        self.assertEqual(
            self.target.get_port("123456789", "987654321", "789456123"),
            ["GetPort"])

    def test_get_link(self):
        self.target._ConversionTable__link_conversion_table =\
            {"123456789::987654321": ["GetLink"]}
        self.assertEqual(
            self.target.get_link("123456789", "987654321"),
            ["GetLink"])

    def test_get_flow(self):
        self.target._ConversionTable__flow_conversion_table =\
            {"123456789::987654321": ["GetFlow"]}
        self.assertEqual(
            self.target.get_flow("123456789", "987654321"),
            ["GetFlow"])

    def test_add_entry_network(self):
        self.target.add_entry_network("123456789", "987654321")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"123456789": ["987654321"], "987654321": ["123456789"]})

    def test_add_entry_node(self):
        self.target.add_entry_node("123456789", "987654321",
                                   "321654987", "789456123")
        self.assertEqual(
            self.target._ConversionTable__node_conversion_table,
            {"123456789::987654321": ["321654987::789456123"],
             "321654987::789456123": ["123456789::987654321"]})

    def test_add_entry_port(self):
        self.target.add_entry_port("123456789", "987654321", "789456123",
                                   "123", "456", "789")
        self.assertEqual(
            self.target._ConversionTable__port_conversion_table,
            {"123456789::987654321::789456123": ["123::456::789"],
             "123::456::789": ["123456789::987654321::789456123"]})

    def test_add_entry_link(self):
        self.target.add_entry_link("123456789", "987654321",
                                   "321654987", "789456123")
        self.assertEqual(
            self.target._ConversionTable__link_conversion_table,
            {"123456789::987654321": ["321654987::789456123"],
             "321654987::789456123": ["123456789::987654321"]})

    def test_add_entry_flow(self):
        self.target.add_entry_flow("123456789", "987654321",
                                   "321654987", "789456123")
        self.assertEqual(
            self.target._ConversionTable__flow_conversion_table,
            {"123456789::987654321": ["321654987::789456123"],
             "321654987::789456123": ["123456789::987654321"]})

    def test_add_entry_object_value_key_value_in_not(self):
        self.target._ConversionTable__add_entry_object(
            self.target._ConversionTable__network_conversion_table,
            "123456789", "987654321")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"123456789": ["987654321"], "987654321": ["123456789"]})

    def test_add_entry_object_value_key_value_in(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]}
        self.target._ConversionTable__add_entry_object(
            self.target._ConversionTable__network_conversion_table,
            "123456789", "987654321")

        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"123456789": ["GetNetwork", "987654321"],
             "987654321": ["GetNetwork", "123456789"]})

    def test_del_entry_network(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]}
        self.target.del_entry_network("123456789")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"987654321": ["GetNetwork"]})

    def test_del_entry_node(self):
        self.target._ConversionTable__node_conversion_table =\
            {"NetworkId::NodeId": ["GetNetwork"],
             "123456789::987654321": ["GetNetwork"]}
        self.target.del_entry_node("123456789", "987654321")
        self.assertEqual(
            self.target._ConversionTable__node_conversion_table,
            {"NetworkId::NodeId": ["GetNetwork"]})

    def test_del_entry_node_port_id_del(self):
        self.target._ConversionTable__port_conversion_table =\
            {"NetworkId::NodeId::port_id": ["GetNetwork"],
             "123456789::987654321::321654987": ["GetNetwork"]}
        self.target._ConversionTable__node_conversion_table =\
            {"NetworkId::NodeId": ["GetNetwork"],
             "123456789::987654321": ["GetNetwork"]}
        self.target.del_entry_node("123456789", "987654321")
        self.assertEqual(
            self.target._ConversionTable__port_conversion_table,
            {"NetworkId::NodeId::port_id": ["GetNetwork"]})

    def test_del_entry_port(self):
        self.target._ConversionTable__port_conversion_table =\
            {"NetworkId::NodeId::port_id": ["GetNetwork"],
             "123456789::987654321::321654987": ["GetNetwork"]}
        self.target.del_entry_port("123456789", "987654321", "321654987")
        self.assertEqual(
            self.target._ConversionTable__port_conversion_table,
            {"NetworkId::NodeId::port_id": ["GetNetwork"]})

    def test_del_entry_link(self):
        self.target._ConversionTable__link_conversion_table =\
            {"NetworkId::LinkId": ["GetNetwork"],
             "123456789::987654321": ["GetNetwork"]}
        self.target.del_entry_link("123456789", "987654321")
        self.assertEqual(
            self.target._ConversionTable__link_conversion_table,
            {"NetworkId::LinkId": ["GetNetwork"]})

    def test_del_entry_flow(self):
        self.target._ConversionTable__flow_conversion_table =\
            {"NetworkId::FlowId": ["GetNetwork"],
             "123456789::987654321": ["GetNetwork"]}
        self.target.del_entry_flow("123456789", "987654321")
        self.assertEqual(
            self.target._ConversionTable__flow_conversion_table,
            {"NetworkId::FlowId": ["GetNetwork"]})

    def test__del_entry_object_key_in(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]}
        self.target._ConversionTable__del_entry_object(
            self.target._ConversionTable__network_conversion_table,
            "123456789")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"987654321": ["GetNetwork"]})

    def test__del_entry_object_key_not_in(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]}
        self.target._ConversionTable__del_entry_object(
            self.target._ConversionTable__network_conversion_table, "abcd")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]})

    def test__del_entry_object_reverse_key_not_in(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]}
        self.target._ConversionTable__del_entry_object(
            self.target._ConversionTable__network_conversion_table,
            "GetNetwork")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"987654321": ["GetNetwork"], "123456789": ["GetNetwork"]})

    def test__del_entry_object_object_len_over_1(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"],
             "123456789": ["GetNetwork"],
             "GetNetwork": ["987654321", "123456789"]}
        self.target._ConversionTable__del_entry_object(
            self.target._ConversionTable__network_conversion_table,
            "123456789")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"987654321": ["GetNetwork"], "GetNetwork": ["987654321"]})

    def test__del_entry_object_reverse_key_in_conv_table_obj(self):
        self.target._ConversionTable__network_conversion_table =\
            {"987654321": ["GetNetwork"],
             "123456789": ["GetNetwork"],
             "GetNetwork": ["987654321"]}
        self.target._ConversionTable__del_entry_object(
            self.target._ConversionTable__network_conversion_table,
            "123456789")
        self.assertEqual(
            self.target._ConversionTable__network_conversion_table,
            {"987654321": ["GetNetwork"]})

if __name__ == '__main__':
    unittest.main()
