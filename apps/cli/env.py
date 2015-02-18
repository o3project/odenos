
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
import yaml
import copy

import producer

init_file = 'init.yaml'
if 'CLIO_INIT' in os.environ:
    init_file = os.environ['CLIO_INIT']
with open(init_file, 'r') as f:
    init = yaml.load(f.read())

URL = init['url'].rstrip('/')
TYPE = init['type']
TRANSACTION_SLEEP = init['transaction_sleep']
BOOTSTRAP = init['bootstrap']
METHODS = init['methods']
SCHEMA = init['schema']
EDITING_MODE = init['editing_mode']
HISTORY = os.path.expanduser(init['history'])
HISTORY_LENGTH = init['history_length']
CMD = METHODS.keys()

FILTERS = {}
TRANSFORMS = {}
PRODUCERS = {}
FANCY_OUTPUTS = {}

# macros
MACROS = {}

def set_filter(req_data):
    """
    Sets a filter
    """
    name = req_data['filter']
    path = req_data['path']
    value = req_data['value']
    FILTERS[name] = {'path': path, 'value': value}

def set_transform(req_data):
    """
    Sets a transform
    """
    name = req_data['transform']
    transaction = copy.copy(req_data)
    del transaction['transform']
    TRANSFORMS[name] = transaction

def set_producer(req_data):
    """
    Sets a producer
    """
    name = req_data['producer']
    classname = req_data['class']
    kwargs = copy.copy(req_data)
    del kwargs['producer']
    del kwargs['class']
    PRODUCERS[name] = getattr(producer, classname)(**kwargs)

def set_fancy(req_data):
    """
    Sets a fancy output format
    """
    name = req_data['fancy']
    fancy = copy.copy(req_data)
    del fancy['fancy']
    FANCY_OUTPUTS[name] = fancy

def set_macro(req_data):
    """
    Sets a macro
    """
    name = req_data['macro']
    del req_data['macro']
    MACROS[name] = req_data

def get_crud(req_data):
    """
    Gets CRUD data
    """
    method = path = params = body = None

    for ope in METHODS:
        if ope in req_data:
            method = METHODS[ope]
            path = req_data[ope]
            if 'body' in req_data:
                body = req_data['body']
            if 'params' in req_data:
                params = req_data['params']
            break
    if path:
        # method, path, params, body
        return (method, path, params, body)
    else:
        return (None, None, None, None)
