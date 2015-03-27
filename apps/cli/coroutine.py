#!/usr/bin/env python

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

import re
import sys 
import shutil
import pydoc
from time import sleep
from env import get_crud, FILTERS, TRANSFORMS, FANCY_OUTPUTS, PRODUCERS, TRANSACTION_SLEEP
from util import substitute, dict_grep, dict2yaml

default_status_pattern = re.compile('2\d{2}')

# Coroutines
#
# [producer] --> [transform] --> [REST Request]X
#
# [producer] --> [broadcast] --> [transform] --> [REST Request]X
#                     |
#                     +--------> [transform] --> [REST Request]X
#
# [REST Request] --> [filter] --> [filter print]X
#
# [REST Request] --> [filter] --> [transform] --> [REST Request]X
#

def coroutine(func):
    def start(*args,**kwargs):
        cr = func(*args,**kwargs)
        next(cr)
        return cr
    return start

@coroutine
def cr_func(func, cr_next=None):
    """
    coroutine with func as 1st arg
    """
    while True:
        pipe_in = (yield)
        func(pipe_in)
        if cr_next:
            cr_next.send(pipe_in)

@coroutine
def cr_filter(filter_, cr_next=None):
    """
    filter coroutine
    pipe_in: res_body 
    pipe_out: dict_grep() output
    """
    group = False
    regx_path = filter_['path']
    regx_value = filter_['value']
    if re.search('\(', regx_path):
        group = True
    while True:
        pipe_in = (yield)
        generator = dict_grep(pipe_in[1], regx_path, regx_value, descend=False, group=group)
        if cr_next:
            for g in generator:
                value = g[1]
                sub = g[0]
                sub.append(value)
                if cr_next:
                    cr_next.send(sub)  # --> [transform] or [cr_filter_print]
    
@coroutine
def cr_transform(transform, cr_next):
    """
    transform coroutine
    pipe_in: dict_grep output or producer output: python list
    pipe_out: REST request
    """
    while True:
        pipe_in = (yield)
        if cr_next:
            req_data = substitute(transform, *pipe_in)
            cr_next.send(req_data)  # --> [REST request]

@coroutine
def cr_fancy(fancy, cr_next):
    """
    fancy coroutine
    """
    header = fancy['header']
    format_ = fancy['format']
    pipe_in = (yield)
    if cr_next:
        for l in header:
            cr_next.send(l)
        cr_next.send(format_.format(*pipe_in))
    while True:
        pipe_in = (yield)
        if cr_next:
            cr_next.send(format_.format(*pipe_in))

@coroutine
def cr_rest_request(args, rest, status_all=None, cr_next=None):
    """
    sends a rest request
    pipe_in: REST request (req_data)
    pipe_out: response, res_body, out 
    """
    verbose = args.verbose
    print_json = args.json

    while True:
        pipe_in = (yield)

        rest_args = get_crud(pipe_in)
        loop = True 
        while loop: 
            response, res_body, out = rest(*rest_args, verbose=verbose, print_json=print_json)
            if response['status'] != '403':  # 403 Forbidden
                loop = False
            else:
                print('403 Forbidden has been returned -- will retry after 1 sec')
                sleep(1)
        if status_all:
            try:
                assert(re.match(default_status_pattern, response['status']))
                status_all[0] += 1
            except:
                status_all[1] += 1

        sleep(TRANSACTION_SLEEP)

        if cr_next:
            cr_next.send([response, res_body, out])

@coroutine
def cr_broadcast(*cr_out):
    """
    broadcasts send()
    pipe_in: (yield) 
    pipe_out: send()
    """
    while True:
        pipe_in = (yield)
        for cr_next in cr_out:
            cr_next.send(pipe_in)

def _print(x):
    print(x)

def _filter_print(x):
    write = sys.stdout.write
    write('/')
    for p in x[:-1]:
        write('{}/'.format(p))
    write(' {}\n'.format(x[-1]))

def _transform_print(x):
    print(dict2yaml(x))

def _rest_print(x):
    print(x[2])

def _rest_pager(x):
    out = x[2]
    terminal_size = shutil.get_terminal_size(25).lines;
    if terminal_size <= len(out.split('\n')):
        pydoc.pager(out)
    else:
        print(out)

CR_FUNCS = {'print': _print, 'filter_print': _filter_print, 'transform_print': _transform_print, 'rest_print': _rest_print, 'rest_pager': _rest_pager}

def do_pipeline(args, rest, req_data, status_all=None):
    """
    Makes a pipeline and executes it
    """
    def _pipe(rest, req_data):
        c = None
        cr = None
        if not req_data:
            return None
        elif len(req_data) == 1:
            c = req_data[0]
            req_data = None
        else:
            c = req_data[0]
            req_data = req_data[1:]
        if isinstance(c, dict):  # broadcast
            if 'broadcast' in c:
                bcr = []
                for r in c['broadcast']:
                    bcr.append(_pipe(rest, r))
                cr = cr_broadcast(*bcr)
            else:
                raise Exception('Unidentified coroutine in pipeline: {}'.format(c))
        elif c in FILTERS:
            filter_ = FILTERS[c]
            cr = cr_filter(filter_, cr_next=_pipe(rest, req_data))
        elif c in TRANSFORMS:
            transform = TRANSFORMS[c]
            cr = cr_transform(transform, cr_next=_pipe(rest, req_data))
        elif c in FANCY_OUTPUTS:
            fancy = FANCY_OUTPUTS[c]
            cr = cr_fancy(fancy, cr_next=_pipe(rest, req_data))
        elif c == 'rest_request':
            cr = cr_rest_request(args, rest, status_all=status_all, cr_next=_pipe(rest, req_data))
        elif c in CR_FUNCS:
            cr = cr_func(CR_FUNCS[c], cr_next=_pipe(rest, req_data))
        else:
            raise Exception('Malformed req_data')
            
        return cr
        
    c = req_data[0]  # Head coroutine in the pipeline
    if isinstance(c, dict):  # REST GET or producer
        method, path, params, body = get_crud(c) 
        cr = None
        req_data = req_data[1:]
        if method:
            cr = cr_rest_request(args, rest, cr_next=_pipe(rest, req_data))
            cr.send(c)
            cr.close()
        else:
            ope = list(c)[0]
            if ope in PRODUCERS:
                data = c[list(c)[0]]
                cr = PRODUCERS[ope](cr_next=_pipe(rest, req_data))
                cr.send(data)
                cr.close()
            else:
                raise Exception("Malformed req_data")

