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
from org.o3project.odenos.remoteobject.manager.event_subscription import EventSubscription


class EventSubscriptionTest(unittest.TestCase):
    def setUp(self):
        self.target = EventSubscription("subscriber_id",
                                        {'k0': 'v0', 'k1': 'v1'})

    def test_constructor_with_default_arg(self):
        empty = EventSubscription()
        self.assertIsNone(empty.subscriber_id)
        self.assertEqual(len(empty.event_filters), 0)

    def test_constructor_without_eventfilters(self):
        es = EventSubscription("subscriber_id")
        self.assertEqual(es.subscriber_id, "subscriber_id")
        self.assertEqual(len(es.event_filters), 0)

    def test_constructor(self):
        self.assertEqual(self.target.subscriber_id, "subscriber_id")
        self.assertEqual(self.target.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1'])})

    def test_clear_filter(self):
        self.target.clear_filter()
        self.assertEqual(len(self.target.event_filters), 0)

    def test_add_filter(self):
        self.target.add_filter("publisher_id", "event_id")
        self.assertEqual(len(self.target.event_filters), 3)
        self.assertEqual(self.target.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1']),
                          'publisher_id': set(['event_id'])})

    def test_add_filter_with_existing_event_id(self):
        self.target.add_filter("publisher_id", "old_event_id")
        self.target.add_filter("publisher_id", "new_event_id")
        self.assertEqual(len(self.target.event_filters), 3)
        self.assertEqual(self.target.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1']),
                          'publisher_id': set(['old_event_id',
                                               'new_event_id'])})

    def test_remove_filter(self):
        self.target.add_filter("publisher_id", "event_id1")
        self.target.add_filter("publisher_id", "event_id2")
        self.target.remove_filter("publisher_id", "event_id1")
        self.assertEqual(len(self.target.event_filters), 3)
        self.assertEqual(self.target.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1']),
                          'publisher_id': set(['event_id2'])})

    def test_remove_filter_with_not_existing_publisher_id(self):
        self.target.add_filter("publisher_id", "event_id1")
        self.target.add_filter("publisher_id", "event_id2")
        self.target.remove_filter("publisher_id_x", "event_id1")
        self.assertEqual(len(self.target.event_filters), 3)
        self.assertEqual(self.target.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1']),
                          'publisher_id': set(['event_id1', 'event_id2'])})

    def test_remove_publisher_id(self):
        self.target.add_filter("publisher_id", "event_id1")
        self.target.add_filter("publisher_id", "event_id2")
        self.target.remove_publisher_id("publisher_id")
        self.assertEqual(len(self.target.event_filters), 2)
        self.assertEqual(self.target.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1'])})

    def test_create_packed(self):
        packed = self.target.packed_object()
        result = EventSubscription.create_from_packed(packed)
        self.assertEqual(result.event_filters,
                         {'k0': set(['v', '0']),
                          'k1': set(['v', '1'])})

    def test_packed_object(self):
        result = self.target.packed_object()
        self.assertEqual(result, {"subscriber_id": "subscriber_id",
                                  "event_filters": {'k0': ['0', 'v'],
                                                    'k1': ['1', 'v']}})

if __name__ == '__main__':
    unittest.main()
