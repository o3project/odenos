#!/usr/bin/python
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

import sys
import os
import requests
import json
import traceback
import time


HOST = "127.0.0.1"
PORT = "10080" 
SYSMGR_ID = "systemmanager"
ORIGINAL_NW_ID = "original-nw"
SLIVER_NW_ID_01 = "sliver-nw01"
SLIVER_NW_ID_02 = "sliver-nw02"
SLICER_ID = "slicer"
CONNECT_ID_01 = "%s_%s" % (SLICER_ID, SLIVER_NW_ID_01)
CONNECT_ID_02 = "%s_%s" % (SLICER_ID, SLIVER_NW_ID_02)

# no_proxy
os.environ["no_proxy"] = "127.0.0.1, localhost"

BASE_URI = "http://%s:%s" % (HOST, PORT)

class SetSliceCondions(object):
    def __init__(self):
        self._req_session = requests.session()

    def resp_print(self, resp):
        print "%s : %s" % (resp.request, resp.url)
        print "[%s] %s" % (resp.status_code , resp.content)

    def set_slice_condition(self):

        #
        # set SliceCondition (vhost1 --> vhost3)
        #
        uri = "%s/%s/topology/physical_ports/%s" % (
            BASE_URI, ORIGINAL_NW_ID, "2@0x91011950")
        resp = self._req_session.get(uri)
        port01 = json.loads(resp.content)
        slice_cond_id = "sliceconditon1"
        _condition = {"id": slice_cond_id,
                      "type":"BasicSliceCondition",
                      "connection": CONNECT_ID_01,
                      "in_node": port01["node_id"],
                      "in_port": port01["port_id"]}
        uri = "%s/%s/settings/slice_condition_table/60/conditions/%s" % (
                BASE_URI, SLICER_ID, slice_cond_id)
        self.resp_print(self._req_session.put(uri, data=json.dumps(_condition)))

        #
        # set SliceCondition (vhost3 --> vhost1)
        #
        uri = "%s/%s/topology/physical_ports/%s" % (
            BASE_URI, ORIGINAL_NW_ID, "2@0x93011950")
        resp = self._req_session.get(uri)
        port02 = json.loads(resp.content)
        slice_cond_id = "sliceconditon2"
        _condition = {"id": slice_cond_id,
                      "type":"BasicSliceCondition",
                      "connection": CONNECT_ID_01,
                      "in_node": port02["node_id"],
                      "in_port": port02["port_id"]}
        uri = "%s/%s/settings/slice_condition_table/60/conditions/%s" % (
                BASE_URI, SLICER_ID, slice_cond_id)
        self.resp_print(self._req_session.put(uri, data=json.dumps(_condition)))

        #
        # set SliceCondition (vhost2 --> vhost4)
        #
        uri = "%s/%s/topology/physical_ports/%s" % (
            BASE_URI, ORIGINAL_NW_ID, "3@0x91011950")
        resp = self._req_session.get(uri)
        port03 = json.loads(resp.content)
        slice_cond_id = "sliceconditon3"
        _condition = {"id": slice_cond_id,
                      "type":"BasicSliceCondition",
                      "connection": CONNECT_ID_02,
                      "in_node": port03["node_id"],
                      "in_port": port03["port_id"]}
        uri = "%s/%s/settings/slice_condition_table/50/conditions/%s" % (
                BASE_URI, SLICER_ID, slice_cond_id)
        self.resp_print(self._req_session.put(uri, data=json.dumps(_condition)))

        #
        # set SliceCondition (vhost4 --> vhost2)
        #
        uri = "%s/%s/topology/physical_ports/%s" % (
            BASE_URI, ORIGINAL_NW_ID, "3@0x93011950")
        resp = self._req_session.get(uri)
        port04 = json.loads(resp.content)
        slice_cond_id = "sliceconditon4"
        _condition = {"id": slice_cond_id,
                      "type":"BasicSliceCondition",
                      "connection": CONNECT_ID_02,
                      "in_node": port04["node_id"],
                      "in_port": port04["port_id"]}
        uri = "%s/%s/settings/slice_condition_table/50/conditions/%s" % (
                BASE_URI, SLICER_ID, slice_cond_id)
        self.resp_print(self._req_session.put(uri, data=json.dumps(_condition)))

        #
        # check slice_condition_table
        #
        print "========== [SliceConditions]  =========="
        uri = "%s/%s/settings/slice_condition_table/conditions/%s" % (
                BASE_URI, SLICER_ID, "sliceconditon1")
        self.resp_print(self._req_session.get(uri))
        print "  >> node_id:%s port_id:%s ==> physical_port: %s" % (
                port01["node_id"], port01["port_id"], port01["attributes"]["physical_id"])
        uri = "%s/%s/settings/slice_condition_table/conditions/%s" % (
                BASE_URI, SLICER_ID, "sliceconditon2")
        print "  >> node_id:%s port_id:%s ==> physical_port: %s" % (
                port02["node_id"], port02["port_id"], port02["attributes"]["physical_id"])
        self.resp_print(self._req_session.get(uri))
        uri = "%s/%s/settings/slice_condition_table/conditions/%s" % (
                BASE_URI, SLICER_ID, "sliceconditon3")
        print "  >> node_id:%s port_id:%s ==> physical_port: %s" % (
                port03["node_id"], port03["port_id"], port03["attributes"]["physical_id"])
        self.resp_print(self._req_session.get(uri))
        uri = "%s/%s/settings/slice_condition_table/conditions/%s" % (
                BASE_URI, SLICER_ID, "sliceconditon4")
        self.resp_print(self._req_session.get(uri))
        print "  >> node_id:%s port_id:%s ==> physical_port: %s" % (
                port04["node_id"], port04["port_id"], port04["attributes"]["physical_id"])
        print "========================================"

def run():
    _set_cond = SetSliceCondions()
    _set_cond.set_slice_condition()
     
    return True

# main
def main():
  
  try:
    run()

  except:
    print "Unexpected error:", sys.exc_info()[0]
    print "= stack trace ============"
    traceback.print_exc()
    print "= stack trace ============"
    exit_code = 1

if __name__ == '__main__':
  main()
