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


class Response:

    class StatusCode:
        OK = 200
        CREATED = 201
        ACCEPTED = 202
        BAD_REQUEST = 400
        FORBIDDEN = 403
        NOT_FOUND = 404
        METHOD_NOT_ALLOWED = 405
        CONFLICT = 409
        INTERNAL_SERVER_ERROR = 500

    def __init__(self, status_code, body):
        self.status_code = status_code
        if hasattr(body, "packed_object"):
            body = body.packed_object()
        self.body = body

    def is_error(self, method):
        if method == "GET" or method == "DELETE":
            return (self.status_code != self.StatusCode.OK)
        if method == "PUT":
            return (self.status_code != self.StatusCode.OK
                    and self.status_code != self.StatusCode.CREATED)
        else:
            return (self.status_code != self.StatusCode.OK
                    and self.status_code != self.StatusCode.CREATED)

    @classmethod
    def create_from_packed(cls, packed):
        return cls(*packed)

    def packed_object(self):
        return (self.status_code, self.body)
