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


class Request:

    class Method:
        GET = "GET"
        PUT = "PUT"
        POST = "POST"
        DELETE = "DELETE"

    def __init__(self, object_id, method, path, body=None):
        self.object_id = object_id
        self.method = method
        self.path = path
        if hasattr(body, "packed_object"):
            body = body.packed_object()
        self.body = body

    @classmethod
    def create_from_packed(cls, packed):
        return cls(*packed)

    def packed_object(self):
        return (self.object_id, self.method, self.path, self.body)
