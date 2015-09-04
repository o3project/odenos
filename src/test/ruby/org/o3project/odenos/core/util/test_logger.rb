
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

$LOAD_PATH.unshift File.expand_path(File.join(File.dirname(__FILE__), "../../../../../main/ruby/org/o3project"))

require 'minitest'
require 'minitest/unit'
require 'minitest/autorun'
require 'mocha/mini_test'

require 'logger'
require 'syslog'
require 'syslog/logger'
require 'yaml'

require 'odenos/core/util/logger'

class TestLogger < MiniTest::Test
  include Odenos::Util::Logger
  @@ident_tmp = ""
  def setup
    Syslog::Logger.new
    @@do_init = true
    @@ident_tmp = @@ident
  end
  
  def teardown
    @@logger = nil
    @@syslogger = nil
    @@ident = @@ident_tmp
  end
  
  def test_maybe_initialize_logger_conf_get_true
    conf = {"Logger" => {"Enabled" => true,
              "Output" => "/dev/null",
              "Level" => "DEBUG"},
            "Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "DEBUG"}}

    File.expects(:expand_path).with(anything, anything).returns(nil).once
    YAML.expects(:load_file).with(anything).returns(conf).once
    expects(:initialize_logger).with(conf).once
    expects(:initialize_syslog).with(conf).once
    
    maybe_initialize()
  end
  
  def test_maybe_initialize_logger_conf_get_false
    File.expects(:expand_path).with(anything, anything).raises().once
    expects(:initialize_logger).with({}).once
    expects(:initialize_syslog).with({}).once
    
    maybe_initialize()
  end
  
  def test_maybe_initialize_logger_not_nil
    @@logger = "123456789"
    @@do_init = false

    maybe_initialize()
    
    assert_equal("123456789", @@logger)
  end

  def test_initialize_logger_logger_level_FATAL
    conf = {"Logger" => {"Enabled" => true, "Output" => "/dev/null",
              "Level" => "FATAL"}}
    initialize_logger(conf)
    assert_equal(::Logger::FATAL, @@logger.level)
  end
  
  def test_initialize_logger_logger_level_ERROR
    conf = {"Logger" => {"Enabled" => true, "Output" => "/dev/null",
      "Level" => "ERROR",}}
      0
      
    initialize_logger(conf)

    assert_equal(::Logger::ERROR, @@logger.level)
  end
  
  def test_initialize_logger_logger_level_WARN
    conf = {"Logger" => {"Enabled" => true, "Output" => "/dev/null",
      "Level" => "WARN",}}

    initialize_logger(conf)

    assert_equal(::Logger::WARN, @@logger.level)
  end
  
  def test_initialize_logger_logger_level_INFO
    conf = {"Logger" => {"Enabled" => true, "Output" => "/dev/null",
      "Level" => "INFO",}}

    initialize_logger(conf)

    assert_equal(::Logger::INFO, @@logger.level)
  end
  
  def test_initialize_logger_logger_level_DEBUG
    conf = {"Logger" => {"Enabled" => true, "Output" => "/dev/null",
      "Level" => "DEBUG",}}

    initialize_logger(conf)

    assert_equal(::Logger::DEBUG, @@logger.level)
  end
  
  def test_initialize_logger_logger_level_else
    conf = {"Logger" => {"Enabled" => true, "Output" => "/dev/null",
      "Level" => "OTHER",}}

    initialize_logger(conf)

    assert_equal(::Logger::WARN, @@logger.level)
  end
  
  def test_initialize_logger_conf_not_include_Logger
    initialize_logger()
    
    assert_equal(::Logger::FATAL, @@logger.level)
  end

  def test_initialize_logger_Enabled_false
    conf = {"Logger" => {"Enabled" => false, "Output" => "/dev/null",
      "Level" => "DEBUG",}}

    initialize_logger(conf)

    assert_equal(::Logger::FATAL, @@logger.level)
  end

  def test_initialize_syslog_facility_LOG_LOCAL0
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL0).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL1
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL1",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL1).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL2
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL2",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL2).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL3
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL3",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL3).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL4
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL4",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL4).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL5
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL5",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL5).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL6
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL6",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL6).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_LOG_LOCAL7
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL7",
              "Level" => "FATAL"}}
              
    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL7).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_facility_else
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "other",
              "Level" => "FATAL"}}

    Syslog::Logger.syslog.expects(:reopen).with(
      @@ident_tmp,
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_USER).once
    
    initialize_syslog(conf)
  end
  
  def test_initialize_syslog_level_FATAL
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "FATAL"}}
              
    initialize_syslog(conf)

    
    assert_equal(Syslog::Logger.syslog.mask,
      Syslog::LOG_UPTO(
        Syslog::Logger::LEVEL_MAP[::Logger::FATAL]))
  end
  
  def test_initialize_syslog_level_ERROR
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "ERROR"}}
              
    initialize_syslog(conf)

    
    assert_equal(Syslog::Logger.syslog.mask,
      Syslog::LOG_UPTO(
        Syslog::Logger::LEVEL_MAP[::Logger::ERROR]))
  end
  
  def test_initialize_syslog_level_WARN
    conf = {"Syslog" => {"Enabled" => false,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "WARN"}}

    Syslog::Logger.syslog.expects(:reopen).with(
      "component_manager_ruby",
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_LOCAL0).never


    initialize_syslog(conf)

  end
  
  def test_initialize_syslog_level_INFO
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "INFO"}}
              
    initialize_syslog(conf)

    
    assert_equal(Syslog::Logger.syslog.mask,
      Syslog::LOG_UPTO(
        Syslog::Logger::LEVEL_MAP[::Logger::INFO]))
  end
  
  def test_initialize_syslog_level_DEBUG
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "DEBUG"}}
              
    initialize_syslog(conf)

    
    assert_equal(Syslog::Logger.syslog.mask,
      Syslog::LOG_UPTO(
        Syslog::Logger::LEVEL_MAP[::Logger::DEBUG]))
  end

  def test_initialize_syslog_level_else
    conf = {"Syslog" => {"Enabled" => true,
              "PROGRAM_NAME" => "component_manager_ruby",
              "Facility" => "LOG_LOCAL0",
              "Level" => "Other"}}
              
    initialize_syslog(conf)

    
    assert_equal(Syslog::Logger.syslog.mask,
      Syslog::LOG_UPTO(Syslog::LOG_WARNING))
  end

  def test_initialize_syslog_not_include_syslog
    conf = {"Logger" => {"Enabled" => true,
              "Output" => "/dev/null",
              "Level" => "DEBUG"}}

    Syslog::Logger.syslog.expects(:reopen).with(
      "component_manager_ruby",
      Syslog::LOG_PID|Syslog::LOG_CONS,
      Syslog::LOG_USER).never
    
    initialize_syslog(conf)
  end

  def test_debug_true_msg
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "DEBUG"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "DEBUG"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:debug).with{"TestLogger"}.once
    @@syslogger.expects(:debug).with{
      "DEBUG TestLogger - debug::false"}.once

    debug("debug::true")
  end
  
  def test_debug_true_msg_block
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "DEBUG"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "DEBUG"}}
    @@do_init = false      
    initialize_logger(conf)
    initialize_syslog(conf)
    msg_block = Proc.new { 
      "msg_block"
    }
    
    @@logger.expects(:debug).with{"TestLogger"}.once
    @@syslogger.expects(:debug).with{
      "DEBUG TestLogger - debug::false"}.once
    
    debug(&msg_block)
  end
  
  def test_debug_logger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "DEBUG"}}
    @@do_init = false      
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:debug).with{"TestLogger"}.never
    @@syslogger.expects(:debug).with{
      "DEBUG TestLogger - debug::false"}.once
    
    debug("debug::true")
  end
  
  def test_debug_syslogger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "DEBUG"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "DEBUG"}}
    @@do_init = false      
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@syslogger.expects(:debug?).returns(false)
    @@logger.expects(:debug).with{"TestLogger"}.once
    @@syslogger.expects(:debug).with{
      "DEBUG TestLogger - debug::false"}.never
    
    debug("debug::true")
  end
  
  def test_info_true_msg
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)

    @@logger.expects(:info).with{"TestLogger"}.once
    @@syslogger.expects(:info).with{
      "INFO TestLogger - info::false"}.once
    
    info("info::true")
  end
  
  def test_info_true_msg_block
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    msg_block = Proc.new { 
      "msg_block"
    }
    
    @@logger.expects(:info).with{"TestLogger"}.once
    @@syslogger.expects(:info).with{
      "INFO TestLogger - info::false"}.once
    
    info(&msg_block)
  end
  
  def test_info_logger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false      
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:info).with{"TestLogger"}.never
    @@syslogger.expects(:info).with{
      "INFO TestLogger - info::false"}.once
    
    info("info::false")
  end
  
  def test_info_syslogger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false      
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@syslogger.expects(:info?).returns(false)
    @@logger.expects(:info).with{"TestLogger"}.once
    @@syslogger.expects(:info).with{
      "INFO TestLogger - info::false"}.never
    
    info("info::false")
  end
  
  def test_warn_true_msg
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "WARN"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "WARN"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
   
    @@logger.expects(:warn).with{"TestLogger"}.once
    @@syslogger.expects(:warn).with{
      "WARN TestLogger - warn::false"}.once

    warn("warn::true")
  end
  
  def test_warn_true_msg_block
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "WARN"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "WARN"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    msg_block = Proc.new { 
      "msg_block"
    }
    
    @@logger.expects(:warn).with{"TestLogger"}.once
    @@syslogger.expects(:warn).with{
      "WARN TestLogger - warn::false"}.once
    
    warn(&msg_block)
  end
  
  def test_warn_logger_false
    conf = {"Logger" => {"Enabled" => false,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "WARN"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:warn).with{"TestLogger"}.never
    @@syslogger.expects(:warn).with{
      "WARN TestLogger - warn::false"}.once
    
    warn("warn::false")
  end
  
  def test_warn_syslogger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "WARN"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "WARN"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)

    @@syslogger.expects(:warn?).returns(false)
    @@logger.expects(:warn).with{"TestLogger"}.once
    @@syslogger.expects(:warn).with{
      "WARN TestLogger - warn::false"}.never
    
    warn("warn::false")
  end
  
  def test_error_true_msg
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "ERROR"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "ERROR"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:error).with{"TestLogger"}.once
    @@syslogger.expects(:error).with{
      "ERROR TestLogger - error::false"}.once
    
    error("error::true")
  end
  
  def test_error_true_msg_block
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "ERROR"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "ERROR"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    msg_block = Proc.new { 
      "msg_block"
    }
    
    @@logger.expects(:error).with{"TestLogger"}.once
    @@syslogger.expects(:error).with{
      "ERROR TestLogger - error::false"}.once
    
    error(&msg_block)
  end
  
  def test_error_logger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "ERROR"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:error).with{"TestLogger"}.never
    @@syslogger.expects(:error).with{
      "ERROR TestLogger - error::false"}.once
    
    error("error::false")
  end
  
  def test_error_syslogger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "ERROR"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "ERROR"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@syslogger.expects(:error?).returns(false)
    @@logger.expects(:error).with{"TestLogger"}.once
    @@syslogger.expects(:error).with{
      "ERROR TestLogger - error::false"}.never
    
    error("error::false")
  end
  
  def test_fatal_true_msg
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "FATAL"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:fatal).with{"TestLogger"}.once
    @@syslogger.expects(:fatal).with{
      "FATAL TestLogger - fatal::false"}.once
    
    fatal("fatal::true")
  end
  
  def test_fatal_true_msg_block
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "FATAL"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    msg_block = Proc.new { 
      "msg_block"
    }
    
    
    @@logger.expects(:fatal).with{"TestLogger"}.once
    @@syslogger.expects(:fatal).with{
      "FATAL TestLogger - fatal::false"}.once
    
    fatal(&msg_block)
  end
  
  def test_fatal_logger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "FATAL"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@logger.expects(:fatal?).returns(false)
    @@syslogger.expects(:fatal?).once
    @@logger.expects(:fatal).with{"TestLogger"}.never
    @@syslogger.expects(:fatal).with{
      "FATAL TestLogger - fatal::false"}.never
    
    fatal("fatal::false")
  end

  def test_fatal_syslogger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "FATAL"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "FATAL"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)
    
    @@syslogger.expects(:fatal?).returns(false)
    @@logger.expects(:fatal).with{"TestLogger"}.once
    @@syslogger.expects(:fatal).with{
      "FATAL TestLogger - fatal::false"}.never
    
    fatal("fatal::false")
  end

  def test_logger_ident_initialize
    logger_ident_initialize("123456789")
    assert_equal("123456789", @@ident)
  end

  def test_logger_true_syslogger_true
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)

    @@logger.expects(:info).with{"TestLogger"}.once
    @@syslogger.expects(:info).with{
      "TestLogger - info::true,true"}.once

    info("info::true,true")
  end

  def test_logger_true_syslogger_false
    conf = {"Logger" => {"Enabled" => true,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => false,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)

    @@syslogger.expects(:info?).returns(false)
    @@logger.expects(:info).with{"TestLogger"}.once
    @@syslogger.expects(:info).with{
      "TestLogger - info::true,false"}.never

    info("info::true,false")
  end

  def test_logger_false_syslogger_true
    conf = {"Logger" => {"Enabled" => false,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => true,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)

    @@logger.expects(:info).with{"TestLogger"}.never
    @@syslogger.expects(:info).with{
      "TestLogger - info::false,true"}.once

    info("info::false,true")
  end

  def test_logger_false_syslogger_false
    conf = {"Logger" => {"Enabled" => false,
      "Output" => "/dev/null",
      "Level" => "INFO"},
    "Syslog" => {"Enabled" => false,
      "PROGRAM_NAME" => "component_manager_ruby",
      "Facility" => "LOG_LOCAL0",
      "Level" => "INFO"}}
    @@do_init = false
    initialize_logger(conf)
    initialize_syslog(conf)

    @@syslogger.expects(:info?).returns(false)
    @@logger.expects(:info).with{"TestLogger"}.never
    @@syslogger.expects(:info).with{
      "TestLogger - info::false,false"}.never

    info("info::false,false")
  end



end
