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


class Event:

    def __init__(self, publisher_id, event_type, event_body):
        self.publisher_id = publisher_id
        self.event_type = event_type
        self.body = event_body

    @classmethod
    def create_from_packed(cls, packed):
        return cls(*packed)

    def packed_object(self):
        body = self.body
        if hasattr(body, "packed_object"):
            body = self.body.packed_object()
        return (self.publisher_id, self.event_type, body)
