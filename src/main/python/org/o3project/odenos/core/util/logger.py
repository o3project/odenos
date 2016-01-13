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

import os
import sys
import logging
import logging.config


class ContextFilter(logging.Filter):
    def fileter(self, record):
        # get basename from filename by removing '.py'
        #record.module = ".".join(record.filename.split(".")[:-1])
        pass


class Logger(object):

    @classmethod
    def file_config(cls, filename=None):
        try:
            if filename is None:
                filename = os.environ['LOGGING_CONF']
            logging.config.fileConfig(filename)
        except IOError:
            print >> sys.stderr, "*** WARN: Logger: may not output log in this time (continued) ***"

        for handler in logging.root.handlers:
            handler.addFilter(ContextFilter())

    @classmethod
    def set_level_debug(cls):
        logging.basicConfig(
            level=logging.DEBUG,
            format="%(asctime)s, %(levelname)s, %(message)s")

    @classmethod
    def set_level_trace(cls):
        logging.basicConfig(
            level=logging.DEBUG,
            format="%(asctime)s, %(levelname)s, %(module)s, %(module)s,"
            + " %(funcName)s, %(message)s")

    @classmethod
    def set_level_info(cls):
        logging.basicConfig(
            level=logging.INFO,
            format="%(asctime)s %(levelname)s %(message)s")
