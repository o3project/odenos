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


class BaseMessageTransport:

    class Future:

        def __init__(self):
            self.__response = None
            self.__response_obtained = False

        def join(self):
            """should be implemented on derived classes"""
            raise NotImplementedError

        def set(self, response):
            self.__response_obtained = True
            self.__response = response

        def get(self):
            return self.join().result

        @property
        def result(self):
            if self.__response_obtained:
                return self.__response
            else:
                return None  # TODO: raise error

    def __init__(self, remote_object_id):
        self.object_id = remote_object_id

    def send_request_message(self, request_obj):
        """
        send request object asynchronously.
        should be implemented in derived classes.
        request_obj: Request class instance.
        return: Future object.
        """
        raise NotImplementedError

    def close(self):
        """
        close this transport.
        should be implemented in derived classes.
        """
        raise NotImplementedError
