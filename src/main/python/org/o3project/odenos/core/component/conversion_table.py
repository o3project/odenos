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


class ConversionTable(object):

    def __init__(self):
        self.__connection_type_map = {}

        self.__network_conversion_table = {}
        self.__node_conversion_table = {}
        self.__port_conversion_table = {}
        self.__link_conversion_table = {}
        self.__flow_conversion_table = {}

    def get_connection_type(self, connection_id):
        if connection_id in self.__connection_type_map:
            return self.__connection_type_map[connection_id]
        return None

    def get_connection_list(self, connection_type):
        connection_ids = []
        for k_conn_id, v_conn_type in self.__connection_type_map.items():
            if connection_type == v_conn_type:
                connection_ids.append(k_conn_id)

        return connection_ids

    def is_connection_type(self, connection_type):
        if connection_type is None or\
           len(self.get_connection_list(connection_type)) == 0:
            return False

        return True

    def add_entry_connection_type(self, connection_id, connection_type):
        self.__connection_type_map[connection_id] = connection_type

    def del_entry_connection_type(self, connection_id):
        if self.__connection_type_map.has_key(connection_id):
            del self.__connection_type_map[connection_id]

    def get_network(self, network_id):
        networks = []
        if network_id in self.__network_conversion_table:
            networks = self.__network_conversion_table[network_id]

        return networks

    def get_node(self, network_id, node_id):
        nodes = []
        key = network_id + "::" + node_id
        if key in self.__node_conversion_table:
            nodes = self.__node_conversion_table[key]

        return nodes

    def get_port(self, network_id, node_id, port_id):
        ports = []
        key = network_id + "::" + node_id + "::" + port_id
        if key in self.__port_conversion_table:
            ports = self.__port_conversion_table[key]

        return ports

    def get_link(self, network_id, link_id):
        links = []
        key = network_id + "::" + link_id
        if key in self.__link_conversion_table:
            links = self.__link_conversion_table[key]

        return links

    def get_flow(self, network_id, flow_id):
        flows = []
        key = network_id + "::" + flow_id
        if key in self.__flow_conversion_table:
            flows = self.__flow_conversion_table[key]

        return flows

    def add_entry_network(self, nwc_id_1, nwc_id_2):
        self.__add_entry_object(self.__network_conversion_table,
                                nwc_id_1,
                                nwc_id_2)

    def add_entry_node(self, org_nwc_id, org_node_id,
                       rep_nwc_id, rep_node_id):
        key = org_nwc_id + "::" + org_node_id
        value = rep_nwc_id + "::" + rep_node_id
        self.__add_entry_object(self.__node_conversion_table,
                                key,
                                value)

    def add_entry_port(self, org_nwc_id, org_node_id, org_port_id,
                       rep_nwc_id, rep_node_id, rep_port_id):
        key = org_nwc_id + "::" + org_node_id + "::" + org_port_id
        value = rep_nwc_id + "::" + rep_node_id + "::" + rep_port_id
        self.__add_entry_object(self.__port_conversion_table,
                                key,
                                value)

    def add_entry_link(self, org_nwc_id, org_link_id,
                       rep_nwc_id, rep_link_id):
        key = org_nwc_id + "::" + org_link_id
        value = rep_nwc_id + "::" + rep_link_id
        self.__add_entry_object(self.__link_conversion_table,
                                key,
                                value)

    def add_entry_flow(self, org_nwc_id, org_flow_id,
                       rep_nwc_id, rep_flow_id):
        key = org_nwc_id + "::" + org_flow_id
        value = rep_nwc_id + "::" + rep_flow_id
        self.__add_entry_object(self.__flow_conversion_table,
                                key,
                                value)

    def __add_entry_object(self, conv_table_obj, key, value):
        # key setting
        if key not in conv_table_obj:
            conv_table_obj[key] = []
        conv_table_obj[key].append(value)

        # value -> key setting(reverse setting)
        if value not in conv_table_obj:
            conv_table_obj[value] = []
        conv_table_obj[value].append(key)

    def del_entry_network(self, key):
        self.__del_entry_object(self.__network_conversion_table, key)

    def del_entry_node(self, network_id, node_id):
        # delete Port => Node.
        del_port_list = []
        for port_id in self.__port_conversion_table:
            port_list = port_id.split("::")
            if port_list[0] == network_id and\
               port_list[1] == node_id:
                del_port_list.append(port_id)

        for port_id in del_port_list:
            self.__del_entry_object(self.__port_conversion_table,
                                    port_id)

        key = network_id + "::" + node_id
        self.__del_entry_object(self.__node_conversion_table, key)

    def del_entry_port(self, network_id, node_id, port_id):
        key = network_id + "::" + node_id + "::" + port_id
        self.__del_entry_object(self.__port_conversion_table, key)

    def del_entry_link(self, network_id, link_id):
        key = network_id + "::" + link_id
        self.__del_entry_object(self.__link_conversion_table, key)

    def del_entry_flow(self, network_id, flow_id):
        key = network_id + "::" + flow_id
        self.__del_entry_object(self.__flow_conversion_table, key)

    def __del_entry_object(self, conv_table_obj, key):
        if key not in conv_table_obj:
            return

        # value -> key remove(reverse setting remove)
        reverse_keys = conv_table_obj[key]
        for reverse_key in reverse_keys:
            if reverse_key not in conv_table_obj:
                continue
            if len(conv_table_obj[reverse_key]) > 1:
                conv_table_obj[reverse_key].remove(key)
                continue

            del conv_table_obj[reverse_key]

        del conv_table_obj[key]
