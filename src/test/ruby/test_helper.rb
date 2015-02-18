
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

target_lib = File.join(File.dirname(__FILE__),
  '../../main/ruby/org', 'o3project')
dummy_trema_lib = File.join(File.dirname(__FILE__),
  'org/o3project/odenos/component/driver/of_driver/dummy_trema/')
coverage_dir = './../../../../test/ruby/coverage'

$LOAD_PATH.unshift(File.dirname(__FILE__))
$LOAD_PATH.unshift(target_lib)
$LOAD_PATH.unshift(dummy_trema_lib)

require 'simplecov'
SimpleCov.root(target_lib)
SimpleCov.coverage_dir(coverage_dir)

#SimpleCov.start do
  #FIXME:delete
  #add_filter '/of_driver/'
#end

require 'minitest'
require 'minitest/unit'
require 'minitest/autorun'

Dir.glob(File.join(File.dirname(__FILE__),
  'org/o3project/**/test_*.rb')) do |test_file|
  require File.expand_path(test_file)
end
