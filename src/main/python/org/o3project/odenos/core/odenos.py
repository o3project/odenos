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


import imp
import inspect
import logging
import os
from optparse import OptionParser

from org.o3project.odenos.core.component.dummy_driver import DummyDriver
from org.o3project.odenos.core.manager.component_manager import ComponentManager
from org.o3project.odenos.core.util.logger import Logger
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.remoteobject.transport.message_dispatcher import MessageDispatcher


class Parser(object):

    def __init__(self):
        pass

    def parse(self):
        parser = OptionParser()
        parser.add_option("-r", dest="rid", help="ComponentManager ID")
        parser.add_option("-d", dest="dir", help="Directory of Components")
        parser.add_option("-i", dest="ip", help="Pubsub server host name or ip address", default="localhost")
        parser.add_option("-p", dest="port", help="Pubsub server port number", type=int, default=6379)
        (options, args) = parser.parse_args()
        return options


def load(module_name, path):
    f, n, d = imp.find_module(module_name, [path])
    return imp.load_module(module_name, f, n, d)


def load_modules(path):
    modules = []
    for fdn in os.listdir(path):
        try:
            if fdn.endswith(".py"):
                m = load(fdn.replace(".py", ""), path)
                modules.append(m)
            elif os.path.isdir(fdn):
                m = load_module(fdn)
                modules.append(m)
        except ImportError:
            pass
    return modules


if __name__ == '__main__':
    Logger.file_config()

    options = Parser().parse()
    logging.info("python ComponentManager options: %s", options)

    dispatcher = MessageDispatcher(redis_server=options.ip, redis_port=options.port)
    dispatcher.start()

    component_manager = ComponentManager(options.rid, dispatcher)

    classes = []

    cwd = os.getcwd()
    directory = os.path.join(cwd, options.dir)
    modules = load_modules(directory)

    for m in modules:
        for name, clazz in inspect.getmembers(m, inspect.isclass):
            if options.dir not in inspect.getsourcefile(clazz):
                continue
            if issubclass(clazz, RemoteObject):
                classes.append(clazz)
                print "Loading... " + str(clazz)

    classes.append(DummyDriver)
    component_manager.register_components(classes)
    sysmgr = SystemManagerInterface(dispatcher)
    sysmgr.add_component_manager(component_manager)
    component_manager.set_state(ObjectProperty.State.RUNNING)

    dispatcher.join()
