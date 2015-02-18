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

from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.manager.system.event.component_connection_changed\
    import ComponentConnectionChanged


class Component(RemoteObject):
    _super_type = ""

    def __init__(self, object_id, dispatcher):
        RemoteObject.__init__(self, object_id, dispatcher)
        self._set_super_type()
        self._reset_event_subscription()

    def _set_super_type(self):
        self._object_property.set_property(ObjectProperty.OBJECT_SUPER_TYPE,
                                           self._super_type)

    def _reset_event_subscription(self):
        if self.dispatcher is None:
            return
        self._event_subscription.clear_filter()
        self._event_subscription.add_filter(self.dispatcher.system_manager_id,
                                            ComponentConnectionChanged.TYPE)
        self._apply_event_subscription()
