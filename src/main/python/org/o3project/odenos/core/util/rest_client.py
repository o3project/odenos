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

import logging
import httplib
import json

from org.o3project.odenos.remoteobject.message.response import Response

class RestClient:
  client = None
  address = None
  port    = None
  timeout = 0
  headers = {'Content-Type': 'application/json'}

  def __init__(self, address, port, timeout=30):
   self.client = None
   self.address = address
   self.port = port
   self.timeout = timeout

  def set_auth(self, auth):
    self.set_header("Authorization", auth)

  def set_header(self, key, val):
    self.headers[key] = val

  def del_header(self, key):
    del self.headers[key]

  def connect(self):
    logging.debug("connect %s[%s]",self.address,self.port)
    try:
      if not self.client:
        self.client = httplib.HTTPConnection(self.address, self.port, self.timeout)
    except Exception:
      self.client = None
      return None
    return self.client

  def close(self):
    if self.client:
      self.client.close()
      self.client = None
    return

  def post(self, path, body):
    if not self.connect():
      self.close()
      raise
    resp = self.request("POST", path, body)
    self.close()
    return resp

  def put(self, path, body):
    if not self.connect():
      self.close()
      raise
    resp = self.request("PUT", path, body)
    self.close()
    return resp

  def delete(self, path, body):
    if not self.connect():
      self.close()
      raise
    resp = self.request("DELETE", path, body)
    self.close()
    return resp

  def request(self, method, path, body, retry=3, retry_interval=1):
    logging.debug(self.__class__.__name__ + "::" + "request:%s %s [%s]",method,path,body)
    retrying = True
    r_counter = 0 

    try:
      if not body is None:
        body = json.dumps(body)
    except Exception:
      logging.error(self.__class__.__name__ + "::" + "json transrate error.")
      return Response(500, None)

    while retrying:
      try:
        self.client.request(method, path, body, self.headers)
      except Exception:
        logging.error(self.__class__.__name__ + "::" + "rest error[request]")
        return Response(500, None)

      try:
        resp = self.client.getresponse()
      except Exception:
        logging.error(self.__class__.__name__ + "::" + "rest error[response]")
        return Response(500, None)

      code = resp.status
      respheaders = resp.getheaders()
      respbody = resp.read()

      logging.debug(self.__class__.__name__ + "::" + "response code = %d" % code)
      logging.debug(self.__class__.__name__ + "::" + "response header = %s" % respheaders)
      logging.debug(self.__class__.__name__ + "::" + "response body = %s" % respbody)

      # Request is not allowed at this moment since an update is in progress.
      if (code == 486) and (r_counter <=retry):
        time.sleep(retry_interval)
        r_counter += 1
        logging.debug("send retry = %s interval:%s", r_counter, retry_interval)
      else:
        retrying = False
    #return code, body
    return Response(code, respbody)
