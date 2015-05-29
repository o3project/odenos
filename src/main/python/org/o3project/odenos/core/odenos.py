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
#import kazoo.client 
import logging
import os
from optparse import OptionParser
import sys
import time

from org.o3project.odenos.core.component.dummy_driver import DummyDriver
from org.o3project.odenos.core.manager.component_manager import ComponentManager
from org.o3project.odenos.core.util.logger import Logger
from org.o3project.odenos.core.util.system_manager_interface import SystemManagerInterface
from org.o3project.odenos.remoteobject.object_property import ObjectProperty
from org.o3project.odenos.remoteobject.remote_object import RemoteObject
from org.o3project.odenos.remoteobject.transport.message_dispatcher import MessageDispatcher

try:
    import kazoo.client
except:
    logging.error('*** Install kazoo! (pip install kazoo) ***')
    sys.exit(1)

class Parser(object):

    def __init__(self):
        pass

    def parse(self):
        parser = OptionParser()
        parser.add_option("-r", dest="rid", help="ComponentManager ID")
        parser.add_option("-d", dest="dir", help="Directory of Components")
        parser.add_option("-i", dest="ip", help="Pubsub server host name or ip address", default="localhost")
        parser.add_option("-p", dest="port", help="Pubsub server port number", type=int, default=6379)
        parser.add_option("-m", dest="monitor", help="Toggle monitor", default="false")
        parser.add_option("-S", dest="manager", help="System Manager ID", default="systemmanager")
        parser.add_option("-z", dest="zookeeper_host", help="ZooKeeper host name or IP address", default="localhost")
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

    dispatcher = MessageDispatcher(system_manager_id=options.manager,
                                   redis_server=options.ip,
                                   redis_port=options.port,
                                   enable_monitor=(options.monitor=="true"))
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
                logging.info("Loading... " + str(clazz))

    classes.append(DummyDriver)
    
    # ZooKeeper client start
    zk = kazoo.client.KazooClient(hosts=options.zookeeper_host)
    zk.start()

    # Wait for the system manager to be up
    while True:
        if zk.exists('/system_manager/{}'.format(options.manager)):
            break
        else:
            logging.info("Waiting for system manager to be up...")
            time.sleep(2.0)
    
    component_manager.register_components(classes)
    sysmgr = SystemManagerInterface(dispatcher, options.rid)
    sysmgr.add_component_manager(component_manager)
    component_manager.set_state(ObjectProperty.State.RUNNING)
   
    # Registers the component manager's object ID with ZooKeeper server. 
    zk.ensure_path('/component_managers')
    zk.create(path='/component_managers/{}'.format(component_manager.object_id), ephemeral=True)

    dispatcher.join()
