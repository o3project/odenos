
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

class HashAccessorHandler < YARD::Handlers::Ruby::Legacy::Base
  handles /\Ahash_(?:reader|writer|accessor)(?:\s|\()/
  namespace_only

  def process
    attr_type   = statement.tokens.first.text.to_sym
    read, write = true, false
    params = tokval_list statement.tokens[2..-1], :attr
    # params = statement.parameters(false).dup

    # 1st parameter is instance variable name
    hash_name = params.shift
    # hash_name = validated_attribute_names(params.shift)

    # Change read/write based on attr_reader/writer/accessor
    # case statement.method_name(true)
    case attr_type
    when :hash_accessor
      write = true
    when :hash_reader
      # change nothing
    when :hash_writer
      read, write = false, true
    end

    # Add all attributes
    # validated_attribute_names(params)
    params.each do |name|
      namespace.attributes[scope][name] ||= SymbolHash[read: nil, write: nil]

      # Show their methods as well
      { read: name, write: "#{name}=" }.each do |type, meth|
        if type == :read ? read : write
          o = MethodObject.new(namespace, meth, scope)
          if type == :write
            o.parameters = [['value', nil]]
            src = "def #{meth}(value)"
            full_src = "#{src}\n @#{hash_name}[#{name}] = value\nend"
            doc = "Sets the attribute stored in @#{hash_name}[ :#{name} ]\n@param value the value to set the attribute #{name} to."
          else
            src = "def #{meth}"
            full_src = "#{src}\n @#{hash_name}[#{name}]\nend"
            doc = "Returns the value of attribute stored in @#{hash_name}[ :#{name} ]"
          end
          o.source ||= full_src
          o.signature ||= src
          register(o)
          o.docstring = doc if o.docstring.blank?(false)

          # Regsiter the object explicitly
          namespace.attributes[scope][name][type] = o
        elsif obj = namespace.children.find { |o| o.name == meth.to_sym && o.scope == scope }
          # register an existing method as attribute
          namespace.attributes[scope][name][type] = obj
        end
      end
    end
  end
end

class ArrayAccessorHandler < YARD::Handlers::Ruby::Legacy::Base
  handles /\Aarray_(?:reader|writer|accessor)(?:\s|\()/
  namespace_only

  def process
    attr_type   = statement.tokens.first.text.to_sym
    read, write = true, false
    params = tokval_list statement.tokens[2..-1], :attr, TkINTEGER
    # params = statement.parameters(false).dup

    # 1st parameter is instance variable name
    array_name = params.shift
    # array_name = validated_attribute_names(params.shift)

    # Change read/write based on attr_reader/writer/accessor
    # case statement.method_name(true)
    case attr_type
    when :array_accessor
      write = true
    when :array_reader
      # change nothing
    when :array_writer
      read, write = false, true
    end

    # Add all attributes
    # validated_attribute_names(params)
    params.each_slice(2) do |name, index|
      # raise "Name:#{name}, Index:#{index}"
      namespace.attributes[scope][name] ||= SymbolHash[read: nil, write: nil]

      # Show their methods as well
      { read: name, write: "#{name}=" }.each do |type, meth|
        if type == :read ? read : write
          o = MethodObject.new(namespace, meth, scope)
          if type == :write
            o.parameters = [['value', nil]]
            src = "def #{meth}(value)"
            full_src = "#{src}\n @#{array_name}[#{name}] = value\nend"
            doc = "Sets the attribute #{name} stored in @#{array_name}[ #{index} ]\n@param value the value to set the attribute #{name} to."
          else
            src = "def #{meth}"
            full_src = "#{src}\n @#{array_name}[#{name}]\nend"
            doc = "Returns the value of attribute #{name} stored in @#{array_name}[ #{index} ]"
          end
          o.source ||= full_src
          o.signature ||= src
          register(o)
          o.docstring = doc if o.docstring.blank?(false)

          # Regsiter the object explicitly
          namespace.attributes[scope][name][type] = o
        elsif obj = namespace.children.find { |o| o.name == meth.to_sym && o.scope == scope }
          # register an existing method as attribute
          namespace.attributes[scope][name][type] = obj
        end
      end
    end
  end
end
