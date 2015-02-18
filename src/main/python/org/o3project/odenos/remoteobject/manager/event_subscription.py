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


class EventSubscription(object):
    SUBSCRIBER_ID = "subscriber_id"
    EVENT_FILTERS = "event_filters"

    def __init__(self, subscriber_id=None, event_filters={}):
        self.subscriber_id = subscriber_id
        self.event_filters = {k: set(v) for k, v in event_filters.items()}

    def clear_filter(self):
        self.event_filters.clear()

    def add_filter(self, publisher_id, event_id):
        if publisher_id not in self.event_filters:
            self.event_filters[publisher_id] = set()
        self.event_filters[publisher_id].add(event_id)

    def remove_filter(self, publisher_id, event_id):
        if publisher_id in self.event_filters:
            self.event_filters[publisher_id].remove(event_id)

    def remove_publisher_id(self, publisher_id):
        self.event_filters.pop(publisher_id, None)

    @classmethod
    def create_from_packed(cls, packed):
        return cls(packed[EventSubscription.SUBSCRIBER_ID],
                   packed[EventSubscription.EVENT_FILTERS])

    def packed_object(self):
        event_filters = {k: list(v) for k, v in self.event_filters.items()}
        return {self.SUBSCRIBER_ID: self.subscriber_id,
                self.EVENT_FILTERS: event_filters}
