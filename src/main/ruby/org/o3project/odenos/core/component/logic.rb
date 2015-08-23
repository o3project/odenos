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

require 'odenos/remoteobject'
require 'odenos/core/component/component'

module Odenos
  module Component
    class Logic < Odenos::Component::Component
      attr_reader :network_interfaces
      attr_reader :system_manager_interface

      def initialize(remote_object_id, dispatcher)
        debug 'Logic#initialize'
        @system_manager_interface = SystemManagerInterface.new(dispatcher)
        @network_interfaces = {}
        @subscription_table = {}
        super
      end

      def on_component_connection_changed(message)
        debug "Receive ComponentConnectionChanged action: #{message.action}"
        case message.action
        when :add
          if on_connection_changed_added_pre(message)
            nwc_id = message.curr.network_id
            if @network_interfaces.include?(nwc_id)
              return
            end
            network_interface = NetworkInterface.new(dispatcher, nwc_id)
            @network_interfaces[nwc_id] = network_interface
            on_connection_changed_added(message)
          end
        when :update
          if on_connection_changed_update_pre(message)
            on_connection_changed_update(message)
          end
        when :delete
          if on_connection_changed_delete_pre(message)
            nwc_id = message.prev.network_id
            on_connection_changed_delete(message)
            @network_interfaces.delete(nwc_id)
          end
        else
          debug 'Message Action is unexpected.'
        end
      end

      def on_connection_changed_added_pre(_message)
        debug ">> #{__method__}"
        true
      end

      def on_connection_changed_update_pre(_message)
        debug ">> #{__method__}"
        true
      end

      def on_connection_changed_delete_pre(_message)
        debug ">> #{__method__}"
        true
      end

      def on_connection_changed_added(_message)
        debug ">> #{__method__}"
      end

      def on_connection_changed_update(_message)
        debug ">> #{__method__}"
      end

      def on_connection_changed_delete(_message)
        debug ">> #{__method__}"
      end

      def add_entry_event_subscription(event_type, nwc_id)
        if event_type.nil? ||
           nwc_id.nil?
          return
        end

        debug "Add EventSubscription Type: #{event_type} NWC ID: #{nwc_id}"
        case event_type
        when NodeChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::%s', NodeChanged::TYPE, nwc_id)] = nil
        when PortChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::%s', PortChanged::TYPE, nwc_id)] = nil
        when LinkChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::%s', LinkChanged::TYPE, nwc_id)] = nil
        when FlowChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::%s', FlowChanged::TYPE, nwc_id)] = nil
        when InPacketAdded::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::%s', InPacketAdded::TYPE, nwc_id)] = nil
        when OutPacketAdded::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::%s', OutPacketAdded::TYPE, nwc_id)] = nil
        end
      end

      def remove_entry_event_subscription(event_type, nwc_id)
        if event_type.nil? ||
           nwc_id.nil?
          return
        end

        debug "Remove EventSubscription Type: #{event_type} NWC ID: #{nwc_id}"
        case event_type
        when NodeChanged::TYPE
          event_subscription.remove_filter(nwc_id, event_type)
          @subscription_table.delete(format('%s::%s', NodeChanged::TYPE, nwc_id))
        when PortChanged::TYPE
          event_subscription.remove_filter(nwc_id, event_type)
          @subscription_table.delete(format('%s::%s', PortChanged::TYPE, nwc_id))
        when LinkChanged::TYPE
          event_subscription.remove_filter(nwc_id, event_type)
          @subscription_table.delete(format('%s::%s', LinkChanged::TYPE, nwc_id))
        when FlowChanged::TYPE
          event_subscription.remove_filter(nwc_id, event_type)
          @subscription_table.delete(format('%s::%s', FlowChanged::TYPE, nwc_id))
        when InPacketAdded::TYPE
          event_subscription.remove_filter(nwc_id, event_type)
          @subscription_table.delete(format('%s::%s', InPacketAdded::TYPE, nwc_id))
        when OutPacketAdded::TYPE
          event_subscription.remove_filter(nwc_id, event_type)
          @subscription_table.delete(format('%s::%s', OutPacketAdded::TYPE, nwc_id))
        end
      end

      def update_entry_event_subscription(event_type, nwc_id, attributes)
        if event_type.nil? ||
           nwc_id.nil? ||
           attributes.nil?
          return
        end

        debug "Update EventSubscription Type: #{event_type} NWC ID: #{nwc_id} Attr: #{attributes}"
        case event_type
        when NodeChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::UPDATE::%s', NodeChanged::TYPE, nwc_id)] = attributes
        when PortChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::UPDATE::%s', PortChanged::TYPE, nwc_id)] = attributes
        when LinkChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::UPDATE::%s', LinkChanged::TYPE, nwc_id)] = attributes
        when FlowChanged::TYPE
          event_subscription.add_filter(nwc_id, event_type)
          @subscription_table[format('%s::UPDATE::%s', FlowChanged::TYPE, nwc_id)] = attributes
        end
      end

      # @param [Event] event
      def on_event(event)
        debug "Receive Event: #{event.event_type}"
        begin
          case event.event_type
          when ComponentConnectionChanged::TYPE
            msg = ComponentConnectionChanged.new(event.body)
            on_component_connection_changed(msg)
          when NodeChanged::TYPE
            msg = NodeChanged.new(event.body)
            on_node_changed(event.publisher_id, msg)
          when PortChanged::TYPE
            msg = PortChanged.new(event.body)
            on_port_changed(event.publisher_id, msg)
          when LinkChanged::TYPE
            msg = LinkChanged.new(event.body)
            on_link_changed(event.publisher_id, msg)
          when FlowChanged::TYPE
            msg = FlowChanged.new(event.body)
            on_flow_changed(event.publisher_id, msg)
          when InPacketAdded::TYPE
            msg = InPacketAdded.new(event.body)
            on_in_packet_added(event.publisher_id, msg)
          when OutPacketAdded::TYPE
            msg = OutPacketAdded.new(event.body)
            on_out_packet_added(event.publisher_id, msg)
          end
        rescue => ex
          error 'Exception: Receive Invalid Event Message'
          error "#{ex.message} #{ex.backtrace}"
          return
        end
      end

      def on_node_changed(nwc_id, message)
        debug "Recieve NodeChanged [#{message.action}] NWC ID: #{nwc_id}"
        case message.action
        when :add
          key = format('%s::%s', NodeChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_node_added(nwc_id, message.curr)
          end
        when :update
          key = format('%s::UPDATE::%s', NodeChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_node_update(nwc_id,
                           message.prev,
                           message.curr,
                           @subscription_table[key])
          end
        when :delete
          key = format('%s::%s', NodeChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_node_delete(nwc_id, message.prev)
          end
        end
      end

      def on_node_added(nwc_id, message)
        debug ">> #{__method__}"
        if on_node_added_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_node_added_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_node_update(nwc_id, prev, curr, attributes)
        debug ">> #{__method__}"
        if on_node_update_pre(nwc_id, prev, curr, attributes)
          # TODO: coversion
        end
      end

      def on_node_update_pre(_nwc_id, _prev, _curr, _attributes)
        debug ">> #{__method__}"
        true
      end

      def on_node_delete(nwc_id, message)
        debug ">> #{__method__}"
        if on_node_delete_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_node_delete_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_port_changed(nwc_id, message)
        case message.action
        when :add
          key = format('%s::%s', PortChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_port_added(nwc_id, message.curr)
          end
        when :update
          key = format('%s::UPDATE::%s', PortChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_port_update(nwc_id,
                           message.prev,
                           message.curr,
                           @subscription_table[key])
          end
        when :delete
          key = format('%s::%s', PortChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_port_delete(nwc_id, message.prev)
          end
        end
      end

      def on_port_added(nwc_id, message)
        debug ">> #{__method__}"
        if on_port_added_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_port_added_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_port_update(nwc_id, prev, curr, attributes)
        debug ">> #{__method__}"
        if on_port_update_pre(nwc_id, prev, curr, attributes)
          # TODO: coversion
        end
      end

      def on_port_update_pre(_nwc_id, _prev, _curr, _attributes)
        debug ">> #{__method__}"
        true
      end

      def on_port_delete(nwc_id, message)
        debug ">> #{__method__}"
        if on_port_delete_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_port_delete_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_link_changed(nwc_id, message)
        case message.action
        when :add
          key = format('%s::%s', LinkChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_link_added(nwc_id, message.curr)
          end
        when :update
          key = format('%s::UPDATE::%s', LinkChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_link_update(nwc_id,
                           message.prev,
                           message.curr,
                           @subscription_table[key])
          end
        when :delete
          key = format('%s::%s', LinkChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_link_delete(nwc_id, message.prev)
          end
        end
      end

      def on_link_added(nwc_id, message)
        debug ">> #{__method__}"
        if on_link_added_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_link_added_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_link_update(nwc_id, prev, curr, attributes)
        debug ">> #{__method__}"
        if on_link_update_pre(nwc_id, prev, curr, attributes)
          # TODO: coversion
        end
      end

      def on_link_update_pre(_nwc_id, _prev, _curr, _attributes)
        debug ">> #{__method__}"
        true
      end

      def on_link_delete(nwc_id, message)
        debug ">> #{__method__}"
        if on_link_delete_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_link_delete_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_flow_changed(nwc_id, message)
        case message.action
        when :add
          key = format('%s::%s', FlowChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_flow_added(nwc_id, message.curr)
          end
        when :update
          key = format('%s::UPDATE::%s', FlowChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_flow_update(nwc_id,
                           message.prev,
                           message.curr,
                           @subscription_table[key])
          end
        when :delete
          key = format('%s::%s', FlowChanged::TYPE, nwc_id)
          if @subscription_table.include?(key)
            on_flow_delete(nwc_id, message.prev)
          end
        end
      end

      def on_flow_added(nwc_id, message)
        debug ">> #{__method__}"
        if on_flow_added_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_flow_added_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_flow_update(nwc_id, prev, curr, attributes)
        debug ">> #{__method__}"
        if on_flow_update_pre(nwc_id, prev, curr, attributes)
          # TODO: coversion
        end
      end

      def on_flow_update_pre(_nwc_id, _prev, _curr, _attributes)
        debug ">> #{__method__}"
        true
      end

      def on_flow_delete(nwc_id, message)
        debug ">> #{__method__}"
        if on_flow_delete_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_flow_delete_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_in_packet_added(nwc_id, message)
        debug ">> #{__method__}"
        if on_in_packet_added_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_in_packet_added_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end

      def on_out_packet_added(nwc_id, message)
        debug ">> #{__method__}"
        if on_out_packet_added_pre(nwc_id, message)
          # TODO: coversion
        end
      end

      def on_out_packet_added_pre(_nwc_id, _message)
        debug ">> #{__method__}"
        true
      end
    end
  end
end
