
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

module Odenos
  module Util
    module MetaHelper
      def self.class_from_string(str)
        str.split('::').reduce(Object) do |mod, class_name|
          mod.const_get(class_name)
        end
      end

      def self.parent_module(cls)
        mod_path = cls.to_s.split('::')
        mod_path.pop
        mod_path.reduce(Object) do |mod, class_name|
          mod.const_get(class_name)
        end
      end

      def self.simple_class(cls)
        cls.to_s.split('::').last
      end
    end
  end
end