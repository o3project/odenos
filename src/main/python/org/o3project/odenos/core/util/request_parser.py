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

import re

from org.o3project.odenos.remoteobject.message.response import Response


class RequestParser(object):
    PATTERN = "pattern"
    METHOD = "method"
    FUNC = "func"
    PARAMS = "params"

    def __init__(self):
        self.__rules = []

    # rule : [{"pattern": "<regex path>", "method": "<method>",
    #         "func": callable obj, "params": num of argument}, ...]
    def add_rule(self, rule):
        self.__rules.extend(rule)

    def action(self, request):
        path_match = False

        for rule in self.__rules:
            matched = re.search(rule[self.PATTERN], request.path)
            if not matched:
                continue

            path_match = True
            if request.method != rule[self.METHOD]:
                continue

            if rule[self.PARAMS] == 0:
                return rule[self.FUNC]()
            elif rule[self.PARAMS] == 1:
                if request.body is None:
                    return rule[self.FUNC](*matched.groups())
                else:
                    return rule[self.FUNC](request.body)
            else:
                if request.body is None:
                    return rule[self.FUNC](*matched.groups())
                else:
                    return rule[self.FUNC](request.body, *matched.groups())

        if path_match:
            return Response(Response.StatusCode.METHOD_NOT_ALLOWED, None)

        return Response(Response.StatusCode.NOT_FOUND, None)
