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

from org.o3project.odenos.core.util.logger import Logger
from org.o3project.odenos.core.util.logger import ContextFilter
from io import BytesIO
import sys
import logging
import unittest
from mock import patch
from contextlib import nested
import os.path


class ContextFilterTest(unittest.TestCase):

    def test_fileter(self):
        record = "test.py"
        self.target = ContextFilter()
        self.target.fileter(record)

class LoggerTest(unittest.TestCase):

    def test_file_config_filename_NotNone(self):
        with nested(
                patch("logging.config.fileConfig"),
                patch("os.environ", {'LOGGING_CONF': "Test_Mock"}
                      )) as(m_ileConfig, Environ):
                Logger.file_config("filename")
                self.assertEqual(m_ileConfig.call_count, 1)
                self.assertEqual(m_ileConfig.call_args[0][0], "filename")
                m_ileConfig.assert_any_call("filename")

    def test_file_config_filename_None(self):
        with nested(
                patch("logging.config.fileConfig"),
                patch("os.environ", {'LOGGING_CONF': "Test_Mock"}
                      )) as(m_ileConfig, Environ):
                Logger.file_config(None)
                self.assertEqual(m_ileConfig.call_count, 1)
                self.assertEqual(m_ileConfig.call_args[0][0], "Test_Mock")
                m_ileConfig.assert_any_call("Test_Mock")

    def test_file_config_Exception(self):
        stderr_msg = "*** WARN: Logger: may not output log in this time (continued) ***\n"
        with nested(
                patch("logging.config.fileConfig"),
                patch("os.environ", {'LOGGING_CONF': "Test_Mock"}),
                patch("sys.stderr", new=BytesIO())
                      ) as(m_ileConfig, Environ, fake_out):
            m_ileConfig.side_effect = IOError
            Logger.file_config('filename')
            self.assertEqual(fake_out.getvalue(), stderr_msg)
            self.assertEqual(m_ileConfig.call_count, 1)

    def test_set_level_debug(self):
        with patch("logging.basicConfig") as m_basicConfig:
            Logger.set_level_debug()
            m_basicConfig.assert_any_call(
                level=logging.DEBUG,
                format="%(asctime)s, %(levelname)s, %(message)s")
            self.assertEqual(m_basicConfig.call_count, 1)

    def test_set_level_trace(self):
        with patch("logging.basicConfig") as m_basicConfig:
            Logger.set_level_trace()
            m_basicConfig.assert_any_call(
                level=logging.DEBUG,
                format="%(asctime)s, %(levelname)s, %(module)s, %(module)s,"
                       + " %(funcName)s, %(message)s")
            self.assertEqual(m_basicConfig.call_count, 1)

    def test_set_level_info(self):
        with patch("logging.basicConfig") as m_basicConfig:
            Logger.set_level_info()
            m_basicConfig.assert_any_call(
                level=logging.INFO,
                format="%(asctime)s %(levelname)s %(message)s")
            self.assertEqual(m_basicConfig.call_count, 1)

if __name__ == '__main__':
    unittest.main()
