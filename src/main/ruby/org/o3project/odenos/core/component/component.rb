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

require 'odenos/remoteobject/remote_object'
require 'odenos/remoteobject/event'

module Odenos
  module Component
    class Component < Odenos::Core::RemoteObject
      include Odenos::Core

      def initialize(remote_object_id, dispatcher)
        debug 'Component#initialize'
        super
        @property.super_type = @super_type
        reset_event_subscription
      end

      def reset_event_subscription
        event_subscription.clear_filter
        event_subscription.add_filter(dispatcher.system_manager_id,
                                      ComponentConnectionChanged::TYPE)
        apply_event_subscription
      end
    end
  end
end
