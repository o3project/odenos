
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

require 'logger'
require 'syslog'
require 'syslog/logger'
require 'yaml'

module Odenos
  module Util
    module Logger
      @@logger = nil
      @@syslogger = nil

      LEVEL = { 'FATAL' => ::Logger::FATAL,
                'ERROR' => ::Logger::ERROR,
                'WARN' => ::Logger::WARN,
                'INFO' => ::Logger::INFO,
                'DEBUG' => ::Logger::DEBUG }

      FACILITY = { 'LOG_LOCAL0' => Syslog::LOG_LOCAL0,
                   'LOG_LOCAL1' => Syslog::LOG_LOCAL1,
                   'LOG_LOCAL2' => Syslog::LOG_LOCAL2,
                   'LOG_LOCAL3' => Syslog::LOG_LOCAL3,
                   'LOG_LOCAL4' => Syslog::LOG_LOCAL4,
                   'LOG_LOCAL5' => Syslog::LOG_LOCAL5,
                   'LOG_LOCAL6' => Syslog::LOG_LOCAL6,
                   'LOG_LOCAL7' => Syslog::LOG_LOCAL7 }

      def maybe_initialize
        return unless @@logger.nil?
        logger_conf = {}
        begin
          # FIXME: load file path
          logger_conf = YAML.load_file(
                        File.expand_path("#{Dir.pwd}/etc/log_ruby.conf", __FILE__))
        rescue
          logger_conf = {}
        end
        initialize_logger(logger_conf)
        initialize_syslog(logger_conf)
      end

      def initialize_logger(conf = {})
        out_put = '/dev/null'
        level = ::Logger::FATAL
        if conf.include?('Logger') &&
           conf['Logger']['Enabled']
          out_put = conf['Logger']['Output']

          if LEVEL.include?(conf['Logger']['Level'])
            level = LEVEL[conf['Logger']['Level']]
          else
            level = ::Logger::WARN
          end
        end
        @@logger = ::Logger.new(out_put)
        @@logger.level = level
      end

      def initialize_syslog(conf = {})
        @@syslogger = Syslog::Logger.new
        Syslog::Logger.syslog.mask = Syslog::LOG_UPTO(Syslog::LOG_EMERG)
        if conf.include?('Syslog') &&
           conf['Syslog']['Enabled']
          ident = conf['Syslog']['PROGRAM_NAME']
          if FACILITY.include?(conf['Syslog']['Facility'])
            facility = FACILITY[conf['Syslog']['Facility']]
          elsif
            facility = Syslog::LOG_USER
          end

          if LEVEL.include?(conf['Syslog']['Level'])
            level = Syslog::Logger::LEVEL_MAP[LEVEL[conf['Syslog']['Level']]]
          elsif
            level = Syslog::LOG_WARNING
          end

          Syslog::Logger.syslog.reopen(ident,
                                       Syslog::LOG_PID | Syslog::LOG_CONS,
                                       facility)
          Syslog::Logger.syslog.mask = Syslog::LOG_UPTO(level)
        end
      end

      def debug(msg = nil, &msg_block)
        maybe_initialize
        return unless @@logger.debug? || @@syslogger.debug?

        progname = self.class.to_s.split('::').last
        msg = msg_block.call if msg_block

        file = caller.first.split(' ')[0].split('/').last.split(':').first
        method = caller.first.split(' ')[1].delete('`').delete("'")

        log_msg = "{#{file}##{method}} #{msg}"
        if @@logger.debug? 
          @@logger.debug(progname) { log_msg }
        end
        log_msg = "DEBUG #{progname} - #{log_msg}"
        if @@syslogger.debug?
          @@syslogger.debug(log_msg)
        end
        return nil
      end

      def info(msg = nil, &msg_block)
        maybe_initialize
        return unless @@logger.info? || @@syslogger.info?

        progname = self.class.to_s.split('::').last
        msg = msg_block.call if msg_block

        file = caller.first.split(' ')[0].split('/').last.split(':').first
        method = caller.first.split(' ')[1].delete('`').delete("'")

        log_msg = "{#{file}##{method}} #{msg}"
        if @@logger.info? 
          @@logger.info(progname) { log_msg }
        end
        log_msg = "INFO #{progname} - #{log_msg}"
        if @@syslogger.info?
          @@syslogger.info(log_msg)
        end
        return nil
      end

      def warn(msg = nil, &msg_block)
        maybe_initialize
        return unless @@logger.warn? || @@syslogger.warn?

        progname = self.class.to_s.split('::').last
        msg = msg_block.call if msg_block

        file = caller.first.split(' ')[0].split('/').last.split(':').first
        method = caller.first.split(' ')[1].delete('`').delete("'")
        log_msg = "{#{file}##{method}} #{msg}"
        if @@logger.warn?
          @@logger.warn(progname) { log_msg }
        end
        log_msg = "WARN #{progname} - #{log_msg}"
        if @@syslogger.warn?
          @@syslogger.warn(log_msg)
        end
        return nil
      end

      def error(msg = nil, &msg_block)
        maybe_initialize
        return unless @@logger.error? || @@syslogger.error?

        progname = self.class.to_s.split('::').last
        msg = msg_block.call if msg_block

        file = caller.first.split(' ')[0].split('/').last.split(':').first
        method = caller.first.split(' ')[1].delete('`').delete("'")
        log_msg = "{#{file}##{method}} #{msg}"
        if @@logger.error?
          @@logger.error(progname) { log_msg }
        end
        log_msg = "ERROR #{progname} - #{log_msg}"
        if @@syslogger.error?
          @@syslogger.error(log_msg)
        end
        return nil
      end

      def fatal(msg = nil, &msg_block)
        maybe_initialize
        return unless @@logger.fatal? || @@syslogger.fatal?

        progname = self.class.to_s.split('::').last
        msg = msg_block.call if msg_block

        if @@logger.fatal?
          @@logger.fatal(progname) { msg }
        end
        log_msg = "FATAL #{progname} - #{log_msg}"
        if @@syslogger.fatal?
          @@syslogger.fatal(log_msg)
        end
        return nil
      end
    end
  end
end
