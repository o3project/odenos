
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

import yaml
import re
import copy
import sys

write = sys.stdout.write

def dict_grep(body, path='/^.*$/', value='^.*$', descend=False, group=False):
    _path = [1]
    _path.extend(path.split('/')[1:-1])
    value = re.compile(value)
    parent = None
    if group:
        parent = []
    else:
        parent = '/'
    return _dict_grep(body=body, parent=parent, value=value, path=_path, descend=descend, group=group)
    
def _dict_grep(body, parent, path, value, descend, group):
    ik = 0
    k = v = None
    for x in body:
        if isinstance(body, dict):
            k = x
            v = body[x]
        elif isinstance(body, list):
            k = str(ik)
            v = x
            ik += 1
        cursor = path[0]
        g = None
        if cursor < len(path):
            g = re.match(path[cursor], k)
        if not descend and cursor >= len(path):
            pass  # out
        elif descend and cursor >= len(path) or g:
            if group:
                key = copy.copy(parent) 
                if g.lastindex:
                    key.extend(g.groups())
            else:
                key = parent + k + '/'
            if isinstance(v, dict) or isinstance(v, list):
                path[0] += 1
                yield from _dict_grep(v, parent=key, path=path, value=value, descend=descend, group=group)
            else:
                if not value:
                    value = '_none'
                if value.match(str(v)):
                    if not descend and cursor == len(path) - 1:
                        yield [key, v]  # hit
                    elif descend:
                        yield [key, v]  # hit
        else:
            pass  # out

    path[0] -= 1

def print_dict_grep_output(grep_output):
    for l in grep_output:
        write('/')
        for p in l[0]:
            write('{}/'.format(p))
        write(' {}\n'.format(l[1]))
    
def _substitute_list(data, *substitutes):
    rv = []
    for item in data:
        if isinstance(item, list):
            item = _substitute_list(item, *substitutes)
        elif isinstance(item, dict):
            item = _substitute_dict(item, *substitutes)
        elif isinstance(item, str):
            item = item.format(*substitutes)       
            if item.startswith('_int'):
                item = int(item[4:])
        rv.append(item)
    return rv

def _substitute_dict(data, *substitutes):
    rv = {}
    for key, value in iter(data.items()):
        if isinstance(key, str):
            key = key.format(*substitutes)
        if isinstance(value, list):
            value = _substitute_list(value, *substitutes)
        elif isinstance(value, dict):
            value = _substitute_dict(value, *substitutes)
        elif isinstance(value, str):
            value = value.format(*substitutes)
            if value.startswith('_int'):
                value = int(value[4:])
        rv[key] = value
    return rv

def substitute(data, *substitutes):
    """
    Substitutes values into dict or list data by using Python format
    """
    if isinstance(data, dict):
        return _substitute_dict(data, *substitutes)
    elif isinstance(data, list):
        return _substitute_list(data, *substitutes)
    else:
        return data

# Python dict => YAML
dict2yaml = lambda d: yaml.dump(d, default_flow_style=False).rstrip('\n')

if __name__ == '__main__':

    conf = """
    root:
        a:
            b:
                c1:
                    d: alice 

                c2:
                    d: cindy
            x:
                y:
                    z: bob 
            xx:
                yy: 3
            zz: 4

    """

    data = yaml.load(conf)

    path = '/root/.*/.*/'
    print(path)
    print([x for x in dict_grep(body=data, path=path)])

    path = '/.*/.*/^x+$/'
    print(path)
    print([x for x in dict_grep(body=data, path=path, descend=True)])

    path = '/root/.*/.*/.*/'
    print(path)
    print([x for x in dict_grep(data, path=path)])

    path = '/root/.*/.*/.*/.*/'
    print(path)
    print([x for x in dict_grep(data, path=path)])

    path = '/root/^a$/.*/.*/^z$/'
    print(path)
    print([x for x in dict_grep(data, path=path)])

    path = '/root/.*/^x+$/.*/'
    print(path)
    print([x for x in dict_grep(data,  path=path)])

    path = '/root/(.*)/.*/'
    print(path)
    print([x for x in dict_grep(data,  path=path, group=True)])

    path = '/root/(.*)/.*/(.*)/'
    print(path)
    print([x for x in dict_grep(data,  path=path, group=True)])

    path = '/root/(.*)/.*/.*/(.*)/'
    print(path)
    print([x for x in dict_grep(data,  path=path, group=True)])

    path = '/root/(\w+)/.*/(c\w+)/.*/'
    print(path)
    print([x for x in dict_grep(data,  path=path, group=True)])

    path = '/root/a/.*/.*/.*/'
    value = 'alice'
    print(path, value)
    print([x for x in dict_grep(data,  path=path, value=value)])

    path = '/root/a/.*/.*/(.*)/'
    value = 'bob'
    print(path, value)
    print([x for x in dict_grep(data,  path=path, value=value, group=True)])

    json_data = """
    node001:
      attributes:
        oper_status: UP
        physical_id: '001'
        vendor: VENDOR_A
      node_id: node001
      ports:
        port0011:
          attributes:
            physical_id: 0011@001
          in_link: null
          node_id: node001
          out_link: null
          port_id: port0011
          type: Port
          version: '1'
        port0012:
          attributes:
            physical_id: 0012@001
          in_link: link0021
          node_id: node001
          out_link: link0012
          port_id: port0012
          type: Port
          version: '3'
        port0013:
          attributes:
            physical_id: 0013@001
          in_link: link0031
          node_id: node001
          out_link: link0013
          port_id: port0013
          type: Port
          version: '3'
      type: Node
      version: '1'
    node002:
      attributes:
        oper_status: UP
        physical_id: '002'
        vendor: VENDOR_A
      node_id: node002
      ports:
        port0021:
          attributes:
            physical_id: 0021@002
          in_link: link0012
          node_id: node002
          out_link: link0021
          port_id: port0021
          type: Port
          version: '3'
        port0022:
          attributes:
            physical_id: 0022@002
          in_link: link0032
          node_id: node002
          out_link: link0023
          port_id: port0022
          type: Port
          version: '3'
        port0023:
          attributes:
            physical_id: 0023@002
          in_link: null
          node_id: node002
          out_link: null
          port_id: port0023
          type: Port
          version: '1'
        port0024:
          attributes:
            physical_id: 0024@002
          in_link: null
          node_id: node002
          out_link: null
          port_id: port0024
          type: Port
          version: '1'
      type: Node
      version: '1'
    node003:
      attributes:
        oper_status: UP
        physical_id: '003'
        vendor: VENDOR_A
      node_id: node003
      ports:
        port0031:
          attributes:
            physical_id: 0031@003
          in_link: link0023
          node_id: node003
          out_link: link0032
          port_id: port0031
          type: Port
          version: '3'
        port0032:
          attributes:
            physical_id: 0032@003
          in_link: null
          node_id: node003
          out_link: null
          port_id: port0032
          type: Port
          version: '1'
        port0033:
          attributes:
            physical_id: 0033@003
          in_link: link0013
          node_id: node003
          out_link: link0031
          port_id: port0033
          type: Port
          version: '3'
      type: Node
      version: '1'
    """
    json_data = yaml.load(json_data)

    path = '/(node\d+)/attributes/physical_id/'
    value = '\d+'
    print(path, value)
    print([x for x in dict_grep(json_data, path=path, value=value, group=True)])

    path = '/(node\d+)/ports/(port\d+)/attributes/physical_id/'
    value = '\d+@\d+'
    print(path, value)
    print([x for x in dict_grep(json_data, path=path, value=value, group=True)])
    
    substitutes = ['aaa', 'bbb', 'ccc', 'ddd']
    substitutes = tuple(substitutes)
    data = {
            'a': 'ddd{0}',
            'b': '{1}eee',
            'c': ['fff{2}', {'d': 'ggg{3}'}]
            }
    print(substitute(data, *substitutes))

    conf = """
    - a:
        xx: dog 
        yy: cat
      b:
        - aa: cow
          bb: sheep
    - a:
        xx: tiger
        yy: hipo
      b:
        - aa: snake
          bb: bird
      c:
        - aa: bear
          bb: rabbit
    """
    json_data = yaml.load(conf)

    path = '/1/.*/.*/'
    value = '.*'
    print(path, value)
    print([x for x in dict_grep(json_data, path=path, value=value, descend=True)])

    path = '/1/(.*)/(.*)/(.*)/'
    value = '.*'
    print(path, value)
    print([x for x in dict_grep(json_data, path=path, value=value, group=True)])

