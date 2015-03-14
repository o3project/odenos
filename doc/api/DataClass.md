## ODENOS data class Objects

 * [Topology](#Topology)
    * [Node](#Node)
    * [Port](#Port)
    * [Link](#Link)
 * [Flow](#Flow)
    * [BasicFlow](#BasicFlow)
    * [OFPFlow](#OFPFlow)
    * [BasicFlowMatch](#BasicFlowMatch)
    * [OFPFlowMatch](#OFPFlowMatch)
    * [FlowAction](#FlowAction)
    * [OFPFlowAction](#OFPFlowAction)
 * [Packet](#Packet)
    * [InPacket(BasicInPacket)](#InPacket)
        * [OFPInPacket](#OFPInPacket)
    * [OutPacket(BasicOutPacket)](#OutPacket)
        * [OFPOutPacket](#OFPOutPacket)
    * [PacketStatus](#PacketStatus)
 * [ObjectProperty](#ObjectProperty)
 * [ObjectSettings](#ObjectSettings)
 *  [ComponentConnection](#ComponentConnection)
    * [ConnectLogicAndNetwork](#ConnectLogicAndNetwork)
 * [Boundary](#Boundary)
    * [FederatorBoundary](#FederatorBoundary)
    * [LinkLayerizerBoundary](#LinkLayerizerBoundary)
 *  [EventSubscription](#EventSubscription)
 *  [Event](#Event)
    * [ObjectPropertyChanged](#ObjectPropertyChanged)
    * [ObjectSettingsChanged](#ObjectSettingsChanged)
    * [ComponentConnectionChanged](#ComponentConnectionChanged)
    * [ComponentManagerChanged](#ComponentManagerChanged)
    * [ComponentChanged](#ComponentChanged)
    * [TopologyChanged](#TopologyChanged)
    * [NodeChanged](#NodeChanged)
    * [PortChanged](#PortChanged)
    * [LinkChanged](#LinkChanged)
    * [FlowChanged](#FlowChanged)
    * [InPacketAdded](#InPacketAdded)
    * [OutPacketAdded](#OutPacketAdded)

----
#### <a name="Topology"> Topology</a>
**This represent the overall topology of Network.**

**Key**   | **Value** |**Description**                                 | POST      | PUT 
----------|-----------|------------------------------------------------|-----------|----------
type      | \<String> | type is "Topology"                             | -         | Mandatory  
version   | \<String>  | NetworkCompnent will assign valid version in response message when the request is success. | - | Mandatory (*1)
nodes     | dict<[Node](#Node).id, [Node](#Node)>                      | [Node](#Node) set. | - | Optional 
links     | dict<[Link](#Link).id, [Link](#Link)>                      | [Link](#Link) set. | - | Optional

  (\*1) version is assigned automatically when creating a new object.  

----
#### <a name="Node"> Node</a>
**Switch Node data class.**

**Key**   | **Value**  |**Description**                             | POST      | PUT 
----------|------------|--------------------------------------------|-----------|------------
type      | \<String>  | type is "Node"                             | Mandatory |  Mandatory
version   | \<String>  | NetworkCompnent will assign valid version in response message when the request is success. | - (*1) | Mandatory (*1)
node_id   | \<String>  | Unique node's Identifier in this topology      | - (*2) |  Mandatory
ports     | dict<[Port](#Port).port_id, [Port](#Port)> | Set of ports in this Node.  | Optional | Optional
attributes|dict{\<String>, \<String>}|See the table attributes.     | Optional | Optional

  (\*1) version is assigned automatically when creating a new object.  
  (\*2) node_id is assigned automatically.


##### Attributes
**Key**      | **Value** |**Description**                                              | init   | Direction of the reflected(*)
-------------|-----------|-------------------------------------------------------------|--------|---------------
admin_status | \<String> | "UP" : normal operation. <br> "DOWN" : failure operation.   | "UP"   | Upper -> Lower 
oper_status  | \<String> | "UP" : Physical device normal. <br> "DOWN" : Physical device failure.    | any    | Lower -> Upper
physical_id  | \<String> |ID of the physical device. Numbering in the driver.<br> For example, openflow driver to set the "dpid" | any         |   Lower  ->  Upper
vendor        | \<String>  |Vendor name is set.<br> For example, It is used for the GUI system. | any | Lower -> Upper

(*) **Lower** : Driver side. **Upper** : Controller side.


----
#### <a name="Port"> Port</a>
**Switch Port data class.**

**Key**   | **Value** |**Description**                          | POST      | PUT 
----------|-----------|-----------------------------------------|-----------|------------
type      | \<String> |type is "Port"                           | Mandatory | Mandatory     
version   | \<String> | NetworkCompnent will assign valid version in response message when the request is success. | -(*1) | Mandatory(*1)
port_id   | \<String> | Unique port's Identifier in this node   | -(*2)     | Mandatory
node_id   | \<String> | Owner of this port                      | Mandatory | Mandatory     
out_link  | \<String> | source  [Link](#Link).id                | -(*3)     | -(*3)
in_link   | \<String> | destination [Link](#Link).id            | -(*3)     | -(*3)
attributes|dict{\<String>, \<String>}|See the table attributes. | Optional  | Optional 

  (\*1) version is assigned automatically when creating a new object.  
  (\*2) port_id is assigned automatically.  
  (\*3) Setting not required because it is set automatically by the Link Configuration


##### Attributes
**Key**       | **Value**  |**Description**                                              | init   | Direction of the reflected(*)
--------------|------------|-------------------------------------------------------------|--------|-----------------
admin_status  | \<String>  | "UP" : normal operation. <br> "DOWN" : failure operation.   | "UP"   | Upper -> Lower 
oper_status   | \<String>  | "UP" : Physical device normal. <br> "DOWN" : Physical device failure.| any    | Lower -> Upper
max_bandwidth | \<Number>  | Unit is Mbps. maximum bandwidth of the port.<br> Driver to set the value.| any    | Lower -> Upper
unreserved_bandwidth | \<Number>  | Unit is Mbps. Current bandwidth of the port.<br > Driver to set the initial value. |max_bandwidth   | Upper -> Lower 
physical_id   | \<String>  |ID of the physical device. Driver may assign this ID.<br> For example, openflow driver will assign like "port_no@dpid" | any         |   Lower  ->  Upper
vendor        | \<String>  |Vendor name is set.<br> For example, It is used for the GUI system. | any | Lower -> Upper
is_boundary   | "true" or "false" |[Boundary](#Boundary) setting is the "true" when enabled. <br> For example, It is used for the GUI system. | "false" |  Upper -> Lower 

(*) **Lower** : Driver side. **Upper** : Controller side.


----
#### <a name="Link"> Link</a>

**Key**   | **Value** |**Description**                            | POST      | PUT 
----------|-----------|-------------------------------------------|-----------|------------
type      | \<String> |type is "Link"                             | Mandatory | Mandatory
version   | \<String> |NetworkCompnent will assign valid version in response message when the request is success. | -(*1) | Mandatory (*1)
link_id   | \<String> |Unique Link's Identifier in this topology. | -(*2)     | Mandatory
src_node  | \<String> |source [Node](#Node).id                    | Mandatory | Mandatory
src_port  | \<String> |source [Port](#Port).id                     | Mandatory | Mandatory
dst_node  | \<String> |destination [Node](#Node).id               | Mandatory | Mandatory
dst_port  | \<String> |destination [Port](#Port).id                | Mandatory | Mandatory
attributes|dict{\<String>, \<String>}|See the table attributes.   | Optional  | Optional 

  (\*1) version is assigned automatically when creating a new object.  
  (\*2) link_id is assigned automatically.  


##### Attributes
**Key**              | **Value** |**Description**                                                                      | init  | Direction of the reflected(*)
---------------------|-----------|-------------------------------------------------------------------------------------|-------|-----------------
oper_status          | \<String> | "UP" : Physical device normal. <br> "DOWN" : Physical device failure.               | any   | Lower -> Upper
cost                 | \<Number> | link cost.                                                                          | 1     | Upper -> Lower              
req_latency          | \<Number> | Unit is msec. Request latency. (There may be different from the actual latency.）   | any   | Upper -> Lower
latency              | \<Number> | Unit is  msec.                                                                      | any   | Lower -> Upper                 
req_bandwidth        | \<Number> | Unit is Mbps. Request bandwidth.(There may be different from the actual bandwidth.) | any   | Upper -> Lower
max_bandwidth        | \<Number> | Unit is Mbps. maximum bandwidth of the port.<br> Driver to set the value.           | any   | Lower -> Upper
unreserved_bandwidth | \<Number> | Unit is Mbps. Current bandwidth of the port.<br > Driver to set the initial value.  |max_bandwidth   | Upper -> Lower 
establishment_status | \<string> |Use LinkLayerizer Only. Link that was generated from Flow. <br> "establishing" :  <br>   "established" :  <br> If it is a "establishing", oper_status is "DOWN" | any    | -

(*) **Lower** : Driver side. **Upper** : Controller side.


----
#### <a name="Flow"> Flow</a>  
Flow is abstract class.  

**Key**    | **Value**  |**Description**                                             | POST      | PUT 
-----------|------------|------------------------------------------------------------|-----------|------------
type       | \<string>  |FlowType (see below)                                        | Mandatory |  Mandatory
version    | \<String>  |NetworkCompnent will assign valid version in response message when the request is success. | -(*1) | Mandatory (*1)
flow_id    | \<string>  |Unique flow's Identifier in this network.                       | -(*1)     |  Mandatory
owner      | \<string>  |Author of flow.                                             | Mandatory |  Mandatory
enabled    | \<boolean> |The value the owner is usually set.  "True":flow is activation.  "False":flow is invalidation. | Mandatory |  Mandatory
priority   | \<number>  |value:0-65535. It is to be 65535 (max priority) if not set. | Mandatory |  Mandatory
status     | \<string>  |see <State Transition Table> Flow status, Lower Layer Component(usually, Driver Component) makes a state transition.  | Optional(*3) |  Mandatory
attributes |dict{\<String>, \<String>}|See the table attributes.                     | Optional  |  Optional  


  (\*1)version is assigned automatically when creating a new object.  
  (\*2)flow_id is assigned automatically.  
  (\*3)If you do not set the state,state set  "none"


  

##### type

**type**                |  **description**
------------------------|-----------------
[BasicFlow](#BasicFlow) | Class that represents a basic flow.
[OFPFlow](#OFPFlow )    | Class that represents a openflow's flow


##### Attributes
**Key**      | **Value**  |**Description**    | init         | Direction of the reflected(*)
-------------|------------|-------------------|--------|------------------
req_bandwidth| \<Number>  | Unit is Mbps. Request bandwidth.(There may be different from the actual bandwidth.) | any    | Upper -> Lower
req_latency  | \<Number>  | Unit is msec. Request latency. (There may be different from the actual latency.） | any    | Upper -> Lower
bandwidth    | \<Number>  | Unit is Mbps.     | any    | Lower  ->  Upper    
latency      | \<Number>  | Unit is msec.     | any    | Lower  ->  Upper    

#### State Transition Table

 * **none**: Definition of Flow is, the state is not enabled.
 * **establishing**:Set during the flow of the physical layer by the driver.
 * **established**: Flow enabled.
 * **teardown**: The removal process during the flow of the physical layer by the driver.
 * **failed**: failure to flow.

**State Current ↓ Next→**| **none** | **establishing** | **established** | **teardown** | **failed** | **(none Flow)**  
---------------------------|----------|------------------|-----------------|--------------|------------|----------------
**none**                   |-         |Creating Flow     |-                |-             |-           |-             
**establishing**           |-         |                  |Created/Updated Flow |-         |failure     |-            
**established**            |-         |Updating Flow     |-                |Deleting Flow |failure     |-             
**teardown**               |Invalid Flow |-              |-                |-             |failure     |Deleted Flow  
**failed**                 |-         |-                 |Updating Flow    |Deleting Flow |-           |-             

### Sequence
----
#### create
<pre>
    [controller/LogicComponent]    [Network]     [Driver]
         |                            |            |
         |POST/PUT flow               |            |
         |--------------------------->|            |
         |         [enabled:true, status:none]     |
         |                            |            |
         |                            |FlowChanged(Add)
         |                            |----------->|
         |                            | PUT flow(stats:establishing)
         |                            |&lt;-----------|
         |  [enabled:true, status:establishing]    |        [Physical Switch]
         |        FlowChanged(UPDATE) |            | Flow setting  |
         |&lt;---------------------------|            |-------------->|
         |                      FlowChanged(UPDATE)|               |
         |                            |----------->|               |
         |                            |            | Flow setting completed
         |                            |            |&lt;--------------|
         |                            | PUT flow(stats:established)
         |                            |&lt;-----------|
         |  [enabled:true, status:established]     |
         |       FlowChanged(UPDATE)  |            | 
         |&lt;---------------------------| FlowChanged(UPDATE)
         |                            |----------->|
         |                            |            |
</pre>

----
#### delete
<pre>

    [controller/LogicComponent]    [Network]     [Driver]
         |                            |            |
         |  [enabled:true, status:established]     |
         |                            |            |
         |DELETE flow                 |            |
         |--------------------------->|            |
         |         FlowChanged(DELETE)|            | 
         |&lt;---------------------------|            |
         |                            |FlowChanged(DELETE)
         |                            |----------->|
         |                            | PUT flow(stats:teardown)
         |                            |&lt;-----------|
         |  [enabled:true, status:teardown]        |         [Physical Switch]
         |        x[FlowChanged(UPDATE)](*)        |               |
         |                        x---|x[FlowChanged(UPDATE)](*)   |
         |                            |-->x        |               |
         |                            |            | Flow delete   |
         |                            |            |-------------->|
         |                            |            | Flow delete completed
         |                            |            |&lt;--------------|
         |                            | PUT flow(stats:none)       |
         |                            |&lt;-----------|
         |      [enabled:true, status:none]        |
         |        x[FlowChanged(UPDATE)](*)        |
         |                        x---|x[FlowChanged(UPDATE)](*)
         |                            |-->x        |
         |                            |            |
         |                     (Flow Deleted)      |
         |                            | x[FlowChanged(DELETE)](*)
         |                            |-->x        |
         |  x[FlowChanged(DELETE)](*) |            | 
         |                        x---|            |

(*) Event notification None.

</pre>
----
#### Invalid
<pre>

    [controller/LogicComponent]    [Network]     [Driver]
         |                            |            |
         |  [enabled:true, status:established]     |
         |                            |            |
         |PUT flow(enabled change)    |            |
         |--------------------------->|            |
         |  [enabled:false, status:established]    |
         |                            |            |
         |                            |FlowChanged(UPDATE)
         |                            |----------->|
         |        FlowChanged(UPDATE) |            | 
         |&lt;---------------------------|            |
         |                            |            |
         |                            | PUT flow(stats:teardown)
         |                            |&lt;-----------|
         |   [enabled:false, status:teardown]      |        [Physical Switch]
         |        FlowChanged(UPDATE) |            | Flow delete   |
         |&lt;---------------------------|            |-------------->|
         |                            |FlowChanged(UPDATE)         |
         |                            |----------->|               |
         |                            |            | Flow delete completed
         |                            |            |&lt;--------------|
         |                            | PUT flow(stats:none)
         |                            |&lt;-----------|
         |       [enabled:false, status:none]      |
         |                            |            |
         |        FlowChanged(UPDATE) |            | 
         |&lt;---------------------------|FlowChanged(UPDATE) 
         |                            |----------->|
         |                            |            |

</pre>
----
#### Valid
<pre>
    [controller/LogicComponent]    [Network]     [Driver]
         |                            |            |
         |       [enabled:false, status:none]      |
         |                            |            |
         |PUT flow(enabled:true)      |            |
         |--------------------------->|            |
         |       [enabled:true, status:none]       |
         |        FlowChanged(UPDATE) |            | 
         |&lt;---------------------------|FlowChanged(UPDATE) 
         |                            |----------->|
         |                            |            |
         |                            | PUT flow(stats:establishing)
         |                            |&lt;-----------|
         |  [enabled:true, status:establishing]    |        [Physical Switch]
         |        FlowChanged(UPDATE) |            | Flow setting  |
         |&lt;---------------------------|            |-------------->|
         |                      FlowChanged(UPDATE)|               |
         |                            |----------->|               |
         |                            |            | Flow setting completed
         |                            |            |&lt;--------------|
         |                            | PUT flow(stats:established)
         |                            |&lt;-----------|
         |  [enabled:true, status:established]     |
         |                            |            |
         |       FlowChanged(UPDATE)  |            | 
         |&lt;---------------------------| FlowChanged(UPDATE)
         |                            |----------->|
         |                            |            |

</pre>
----
#### Failure
<pre>

    [controller/LogicComponent]    [Network]     [Driver]
         |                            |            |
         |  [enabled:true, status:established]     |
         |                            |            |         [Physical Switch]
         |                            |            | Flow Failed   |
         |                            |            |&lt;--------------|
         |                            | PUT flow(stats:failed)     |
         |                            |&lt;-----------|
         |       [enabled:true, status:failed]     |
         |                            |            |
         |       FlowChanged(UPDATE)  |            | 
         |&lt;---------------------------| FlowChanged(UPDATE)
         |                            |----------->|

    [controller/LogicComponent]    [Network]     [Driver]
         |                            |            |
         |  [enabled:true, status:established]     |
         |                            |            |         [Physical Switch]
         |                            |            | topology Failed   |
         |                            |            |&lt;------------------|
         |                            | (topology(*) oper_sts:DOWN)    |
         |                            |            |                   |
         |                            | PUT flow(stats:failed)
         |                            |&lt;-----------|
         |       [enabled:true, status:failed]     |
         |                            |            |
         |       FlowChanged(UPDATE)  |            | 
         |&lt;---------------------------| FlowChanged(UPDATE)
         |                            |----------->|

  * topology : Node, Port, or Link.

</pre>
----
#### <a name="BasicFlow"> BasicFlow</a>  

**Key**      | **Value**   |**Description**            | POST      | PUT 
-------------|-------------|---------------------------|-----------|------------
type         | \<String>   | type is "BasicFlow"       | Mandatory |  Mandatory
matches      | list[[BasicFlowMatch](#BasicFlowMatch)] | One or more of the match conditions | Mandatory |  Mandatory
path         | list[[Link](#Link).link_id ]            | list of [Link](#Link) that [Flow](#Flow) goes through. there is a need for a connected acyclic graph. | Mandatory |  Mandatory
edge_actions | dict<[Node](#Node).node_id, list[[BasicFlowAction](#BasicFlowAction)]> |Action of edge node.  | Mandatory |  Mandatory

##### example(JSON)

    {
    "type": "BasicFlow",
    "version": "XXXXXXXXXXXX",
    "flow_id": "XXXXXXXXXXXX",
    "owner": "XXXXXXXXXXXX",
    "enabled": true,
    "status": "none",
    "matches": [ {...(BasicFlowMatch)...} ]
    "path": ["LINK_ID1", "LINK_ID2"]
        "edge_actions": {
            "NODE_ID_1": [
                {...(BasicFlowAction)...},
                {...(BasicFlowAction)...},]    
        }    
    }



----
#### <a name="OFPFlow"> OFPFlow</a>  

**Key**      | **Value**    |**Description**                             | POST      | PUT 
-------------|--------------|--------------------------------------------|-----------|------------
type         | \<String>    | type is "OFPFlow"                          | Mandatory |  Mandatory
matches      | list[[OFPFlowMatch](#OFPFlowMatch)] | One or more of the match conditions | Mandatory |  Mandatory
idle_timeout | \<number>    | Units sec. Is regarded as 0 specified if it is omitted. | Optional  |  Optional  
hard_timeout | \<number>    | Units sec. Is regarded as 0 specified if it is omitted. | Optional  |  Optional  
path         | list[[Link](#Link).link_id ] | list of [Link](#Link) that [Flow](#Flow) goes through. there is a need for a connected acyclic graph. | Mandatory |  Mandatory
edge_actions | dict<[Node](#Node).node_id, list[[OFPFlowAction](#OFPFlowAction)]> |Action of edge node.  | Mandatory |  Mandatory


----
#### <a name="BasicFlowMatch"> BasicFlowMatch</a> 
represent the flow match of BasicFlow  

**Key**     | **Value**    |**Description**
------------|--------------|----------------
type        | \<string>    |"BasicFlowMatch"                       
in_node     | \<string>    | [Node](#Node).node_id.  **Required**      
in_port     | \<string>    | [Port](#Port).port_id.  optional (if key is not, Treat as ANY.) 


----
#### <a name="OFPFlowMatch"> OFPFlowMatch</a>  
represent the flow match of OpenFlow 1.3  
if key is not, Treat as ANY.  

**Key**          | **Value**  |**Description**
-----------------|------------|----------------
type             | \<string>  | "OFPFlowMatch"                                
in_node          | \<string>  | [Node](#Node).node_id.  **Required**      
in_port          | \<string>  | [Port](#Port).port_id.  Ingress port. This may be a physical or switch-defined logical port.(required support in the OpenFlow 1.3)                                
in_phy_port      | \<integer> |
metadata         | \<string>  | Metadata passed between tables.                
metadata_mask    | \<string>  | Metadata mask                                  
eth_src          | \<string>  | Ethernet source address. format:"XX:XX:XX:XX:XX:XX" (required support in the OpenFlow 1.3)
eth_src_mask     | \<string>  | Ethernet source address mask.             
eth_dst          | \<string>  | Ethernet destination address. format:"XX:XX:XX:XX:XX:XX" (required support in the OpenFlow 1.3)
eth_dst_mask     | \<string>  | Ethernet destination address mask.             
eth_type         | \<integer> | Ethernet frame type.  (required support in the OpenFlow 1.3)                         
vlan_vid         | \<integer> | VLAN id
vlan_vid_mask    | \<integer> | VLAN id mask.                                
vlan_pcp         | \<integer> | VLAN priority.                                 
ip_dscp          | \<integer> | IP DSCP (6 bits in ToS field).                 
ip_ecn           | \<integer> | IP ECN (2 bits in ToS field).                  
ip_proto         | \<integer> | IP protocol.  (required support in the OpenFlow 1.3)                                   
ipv4_src         | \<string>  | IPv4 source address. format : "D.D.D.D"   (required support in the OpenFlow 1.3)      
ipv4_src_mask    | \<string>  | IPv4 source address mask.                      
ipv4_dst         | \<string>  | IPv4 destination address. format : "D.D.D.D"    (required support in the OpenFlow 1.3)
ipv4_dst_mask    | \<string>  | IPv4 destination address mask.                 
tcp_src          | \<integer> | TCP source port.   (required support in the OpenFlow 1.3)                              
tcp_dst          | \<integer> | TCP destination port.    (required support in the OpenFlow 1.3)                       
udp_src          | \<integer> | UDP source port.   (required support in the OpenFlow 1.3)                             
udp_dst          | \<integer> | UDP destination port.  (required support in the OpenFlow 1.3)                         
sctp_src         | \<integer> | SCTP source port.                              
sctp_dst         | \<integer> | SCTP destination port.                         
icmpv4_type      | \<integer> | ICMP type.                                     
icmpv4_code      | \<integer> | ICMP code.                                     
arp_op           | \<integer> | ARP opcode.                                    
arp_spa          | \<string>  | ARP source IPv4 address.                       
arp_spa_mask     | \<string>  | ARP source IPv4 address mask.                  
arp_tpa          | \<string>  | ARP target IPv4 address.                       
arp_tpa_mask     | \<string>  | ARP target IPv4 address mask.                  
arp_sha          | \<string>  | ARP source hardware address.                   
arp_sha_mask     | \<string>  | ARP source hardware address mask.                   
arp_tha          | \<string>  | ARP target hardware address.                                
arp_tha_mask     | \<string>  | ARP target hardware address mask.                      
ipv6_src         | \<string>  | IPv6 source address.  (required support in the OpenFlow 1.3)                          
ipv6_src_mask    | \<string>  | IPv6 source address mask.                      
ipv6_dst         | \<string>  | IPv6 destination address.  (required support in the OpenFlow 1.3)                     
ipv6_dst_mask    | \<string>  | IPv6 destination address mask.                 
ipv6_flabel      | \<integer> | IPv6 Flow Label.                               
ipv6_flabel_mask | \<integer> | IPv6 Flow Label mask.                          
icmpv6_type      | \<integer> | ICMPv6 type.                                   
icmpv6_code      | \<integer> | ICMPv6 code.                                   
ipv6_nd_target   | \<string>  | Target address for ND.                         
ipv6_nd_sll      | \<string>  | Source link-layer for ND.                      
ipv6_nd_tll      | \<string>  | Target link-layer for ND.                      
mpls_label       | \<integer> | MPLS label.                                    
mpls_tc          | \<integer> | MPLS TC.                                       
mpls_bos         | \<integer> | MPLS BoS bit.                                  
pbb_isid         | \<integer> | PBB I-SID.                                     
pbb_isid_mask    | \<integer> | PBB I-SID mask.                                               
tunnel_id        | \<string>  | Logical Port Metadata.                         
tunnel_id_mask   | \<string>  | Logical Port Metadata mask.                    
ipv6_exthdr      | \<integer> | IPv6 Extension Header pseudo-field             
ipv6_exthdr_mask | \<integer> | IPv6 Extension Header pseudo-field mask        

----
#### <a name="FlowAction"> FlowAction</a>  
Action of edge node.(BasicFlow)  

* [FlowActionOutput]

**Key**     | **Value**        |**Description**
------------|------------------|----------------
type        |"FlowActionOutput"| 
output      |Port.port_id      |

----
#### <a name="OFPFlowAction"> OFPFlowAction</a>

Action of edge node.(OpenFlow 1.3)  

* [OFPFlowActionCopyTtlOut]

**Key** | **Value**             |**Description**
--------|-----------------------|----------------
type    |"OFPFlowActionCopyTtlOut" | Copy TTL "outwards" -- from next-to-outermost		


* [OFPFlowActionCopyTtlIn]

**Key** | **Value**            |**Description**
--------|----------------------|----------------
type    |"OFPFlowActionCopyTtlIn" | Copy TTL "inwards" -- from outermost to	

* [OFPFlowActionSetMPLSTTL]

**Key** | **Value**             |**Description**
--------|-----------------------|----------------
type    |"OFPFlowActionSetMPLSTTL" | MPLS TTL		
mpls_ttl  | \<Integer>            | The mpls_ttl field is the MPLS TTL to set.


* [OFPFlowActionDecMPLSTTL]

**Key** | **Value**             |**Description**
--------|-----------------------|----------------
type    |"OFPFlowActionDecMPLSTTL" | Decrement MPLS TTL		


* [OFPFlowActionPushVLAN]

**Key**   | **Value**           |**Description**
----------|---------------------|----------------
type      |"OFPFlowActionPushVLAN" | Push a new VLAN tag		
ethertype | \<Integer>          | VLAN ID


* [OFPFlowActionPopVLAN]

**Key**   | **Value**           |**Description**
----------|---------------------|----------------
type      |"OFPFlowActionPopVLAN"  | Pop the outer VLAN tag		

* [OFPFlowActionPushMPLS]

**Key**   | **Value**           |**Description**
----------|---------------------|----------------
type      |"OFPFlowActionPushMPLS" | Push a new MPLS tag		
ethertype | \<Integer>          |


* [OFPFlowActionPopMPLS]

**Key**   | **Value**          |**Description**
----------|--------------------|----------------
type      |"OFPFlowActionPopMPLS" | Pop the outer MPLS tag		
ethertype | \<Integer>         | Ethertype


* [OFPFlowActionSetQueue]

**Key** | **Value**           |**Description**
--------|---------------------|----------------
type    |"OFPFlowActionSetQueue" | Set queue id when outputting to a port		
queue_id | \<Integer>         | Queue ID


* [OFPFlowActionGroup]

**Key**  | **Value**          |**Description**
---------|--------------------|----------------
type     |"OFPFlowActionGroup"   | Apply group.		
group_id | \<Integer>         | The group_id indicates the group used to process this packet.  


* [OFPFlowActionSetNwTTL]

**Key** | **Value**           |**Description**
--------|---------------------|----------------
type    |"OFPFlowActionSetNwTTL" | IP TTL.		
ip_ttl  | \<Integer>          |  The nw_ttl field is the TTL address to set in the IP header.


* [OFPFlowActionDecNwTTL]

**Key** | **Value**           |**Description**
--------|---------------------|----------------
type    |"OFPFlowActionDecNwTTL" | Decrement IP TTL.		


* [OFPFlowActionSetField]

**Key** | **Value**           |**Description**
--------|---------------------|----------------
type    |"OFPFlowActionSetField" | Set a header field using OXM TLV format.		
field   | \<String>           | T.B.D.


* [OFPFlowActionPushPBB]

**Key**    | **Value**           |**Description**
-----------|---------------------|----------------
type       |"OFPFlowActionPushPBB"  | Push a new PBB service tag (I-TAG)		
ethertype  | \<Integer>          |   


* [OFPFlowActionPopPBB]

**Key** | **Value**             |**Description**
--------|-----------------------|----------------
type    |"OFPFlowActionPopPBB"     | Pop the outer PBB service tag (I-TAG)		



* [OFPFlowActionExperimenter]

**Key** | **Value**               |**Description**
--------|-------------------------|----------------
type    |"OFPFlowActionExperimenter" | 
experimenter |\<Integer>          |Experimenter ID which takes the same form as in struct ofp_experimenter_header
body    | \<Integer>              | Experimenter defined Experimenter-defined arbitrary additional data.

----
#### <a name="Packet"> Packet</a>
Packet of abstract class.

**Key**   | **Value**    |**Description**                               | POST      | PUT 
----------|--------------|----------------------------------------------|-----------|------------
packet_id | \<String>    |Unique packet's Identifier in this network.   |   -       |  -
type      | \<String>    |Packet Type. see blow.                         | Mandatory |  -
attributes|dict{\<String>, \<String>}|                                  | Optional  |  -


##### type

**type**                     |  **description**
-----------------------------|-----------------
[InPacket](#InPacket)        | Packet_in of BasicFlow.  
[OutPacket](#OutPacket)      | Packet_out of BasicFlow.  
[OFPInPacket](#OFPInPacket)  | Packet_in of OFPFlow.  
[OFPOutPacket](#OFPOutPacket)| Packet_out of OFPFlow.  

----
#### <a name="InPacket"> InPacket</a>
represents Packet_in of BasicFlow.  

**Key**   | **Value**    |**Description**                             | POST      | PUT 
----------|--------------|--------------------------------------------|-----------|----------
packet_id | \<String>    |Unique packet's Identifier in this network. |    -      |  -
type      | \<String>    |Type is "InPacket"                          | Mandatory |  -
node      | [Port](#Port).node_id |node_id to input the packet        | Mandatory |  -
port      | [Port](#Port).port_id |port_id to input the packet        | Mandatory |  -
header    | [BasicFlowMatch](#BasicFlowMatch) |header info.           | Mandatory |  -
data      | \<Binary>    |payload                                     | Mandatory |  -
attributes|dict{\<String>, \<String>}|                                | Optional  |  -


----
#### <a name="OFPInPacket"> OFPInPacket</a>  
represents Packet_in of OFPFlow.  

**Key**   | **Value**    |**Description**                             | POST      | PUT 
----------|--------------|--------------------------------------------|-----------|----------
packet_id | \<String>    |Unique packet's Identifier in this network. | - (*1)    |  -
type      | \<String>    |"OFPInPacket"                               | Mandatory |  -
node      | [Port](#Port).node_id |node_id to input the packet        | Mandatory |  -
port      | [Port](#Port).nort_id |port_id to input the packet        | Mandatory |  -  
header    | [OFPFlowMatch](#OFPFlowMatch) |header info.               | Mandatory |  -
data      | \<Binary>    |payload                                     | Mandatory |  -
attributes|dict{\<String>, \<String>}|                                | Optional  |  -


----
#### <a name="OutPacket"> OutPacket</a>  
represents Packet_out of BasicFlow.  

**Key**      | **Value**    |**Description**                             | POST      | PUT 
-------------|--------------|--------------------------------------------|-----------|----------
packet_id    | \<String>    |Unique packet's Identifier in this network. | -         |  -
type         | \<String>    |Type is "OutPacket"                         | Mandatory |  -
node         |[Port](Port).node_id |node_id to output the packet.        | Mandatory |  -
ports        |list[[Port](#port).port_id] |List of port for OutPacket. "ports" and "ports-except" can be specified only either.  | Optional |  -
ports-except |list[[Port](#Port).port_id] |List of  except port for OutPacket. "ports" and "ports-except" can be specified only either.| Optional |  -
header       |[BasicFlowMatch](#BasicFlowMatch) |header info.            | Mandatory |  -
data         | \<Binary>    |payload                                     | Mandatory |  -
attributes   |dict{\<String>, \<String>}|                                | Optional  |  -


----
#### <a name="OFPOutPacket"> OFPOutPacket</a>  
represents Packet_out of OFPFlow.  

**Key**      | **Value**    |**Description**                             | POST      | PUT 
-------------|--------------|--------------------------------------------|-----------|----------
packet_id    | \<String>    |Unique packet's Identifier in this network. | -         |  -
type         | \<String>    |Type is "OutPacket"                         | Mandatory |  -
node         |[Port](Port).node_id |node_id to output the packet.        | Mandatory |  -
ports        |list[[Port](#port).port_id] |List of port for ignore OutPacket. "ports" and "ports-except" can be specified only either.  | Optional |  -
ports-except |list[[Port](#Port).port_id] |List of  except port for OutPacket. "ports" and "ports-except" can be specified only either.| Optional |  -
header       |[BasicFlowMatch](#BasicFlowMatch) |header info.            | Mandatory |  -
data         | \<Binary>    |payload                                     | Mandatory |  -
attributes   |dict{\<String>, \<String>}|                                | Optional  |  -


----
#### <a name="PacketStatus"> PacketStatus</a>  
Stats of network's packet information.  

**Key**                | **Value**      |**Description**
-----------------------|----------------|----------------
type                   | \<String>      |type is "PacketStatus"
in_packet_count        | \<number>      |InPacket total count.
in_packet_bytes        | \<number>      |InPacket total data size(Bytes)
in_packet_queue_count  | \<number>      |count of current InPacket.
in_packets             |list[\<string>] |InPacket list  of  [InPacket](#InPacket).packet_id
out_packet_count       | \<number>      |OutPacket total count.
out_packet_bytes       | \<number>      |OutPacket total data size(Bytes)
out_packet_queue_count | \<number>      |count of current OutPacket.
out_packets            |list[\<string>] |OutPacket list  of  [OutPacket](#OutPacket).packet_id


----
#### <a name="ObjectProperty"> ObjectProperty</a>  
RemoteObject class and RemoteObject subclass property.  

**Key**         | **Value** |**Description**                           | POST      | PUT 
----------------|-----------|------------------------------------------|-----------|------------
id              | \<String> |Unique Identifier in ODENOS.              | -(*1)     | Mandatory 
type            | \<String> |ObjectType (example:ComponentManager,Network,Slicer,,,) | Mandatory | Mandatory 
state           | \<String> |see  "State Transition Table"             | -         | -
description     | \<String> |Object Description simple.                | -         | -
super_type      | \<String> |Component Only. super class ObjectType. (example:ComponentManager,NetworkComponent,Slicer,,,)   | - | -
component_types | \<String> |ComponentManager Only. List of instances that can be generated conponet.         | -     | - 
connection_types | list[\<String>] |Component Only. List of [ComponentConnection](#ComponentConnection).connection_type | -     | - 

  (\*1)id is automatically assigned.  


##### example(JSON)

    {    
    "id": "network1",   
    "super_type" : "Network",   
    "type": "Network",   
    "state": "running",   
    "description" : "NetworkComponent is network topology abstract class"   
    }    

#### State Transition Table

**State Current ↓**  **Next→**  | **initializing** | **running** | **finalizing** | **error** 
----------------------------------|------------------|-------------|----------------|----------
**initializing**                  |-                 |init completion |-            |failure initial
**running**                       |-                 |-            |request Delete  |connection error
**finalizing**                    |-                 |-            |-               |- 
**error**                         |PUT state         |PUT state    |PUT state       |-



----
#### <a name="ObjectSettings"> ObjectSettings</a>

**Key**   | **Value** |**Description**
----------|-----------|----------------
(any)     | \<String> | any key & value 



----
#### <a name="ComponentConnection"> ComponentConnection</a>
Connection information between OperatorComponent and NetworkComponent

**key**          | **value** | **description**                                                | POST         | PUT 
-----------------|-----------|----------------------------------------------------------------|--------------|-----------
id               | \<String> |Unique Identifier in ODENOS.                                    | -(*1)        | Mandatory 
type 　　 　     | \<String> |ObjectType                                                      | Mandatory    | Mandatory
connection_type  | \<String> |Component can be set at any(original, aggregate, sliver, ... )  | Optional     | Optional
state            | \<String> |see  "State Transition Table"                                   | Optional(*2) | Mandatory

  (\*1)id is automatically assigned.  
  (\*2)If you do not set the state,state set  "initializing"

##### type

**type**   |  **description**
-----------|-----------------
[ComponentConnectionLogicAndNetwork](#ConnectLogicAndNetwork) | Connection between Logic Conponent and Network Component.

#### State Transition Table

**State Current ↓**  **Next→**  | **initializing** | **running** | **finalizing** | **error** 
----------------------------------|------------------|-------------|----------------|----------
**initializing**                  |-                 |init completion |-            |failure initial
**running**                       |-                 |-            |request Delete  |connection error
**finalizing**                    |-                 |-            |-               |- 
**error**                         |PUT state         |PUT state    |PUT state       |-

### Sequence
----
#### connection
<pre>
 [controller/LogicComponent]  [systemMgr]    [Logic]            [Component]
     |                            |             |                    |
     |                            |             |                    |
     |POST connections            |             |                    |
     |--------------------------->|             |                    |
     |              [status:initializing]       |                    |
     |                            |             |                    |
     |                            |ConnectionChanged(Add)            |
     |                            |------------>|                    |
     |                            |             |                    |
     |                            |             |onConnectionChangedAddedPre()
     |                            |             |------------------->|
     |                            |             |                    |
     |                            |             |               checks param
     |                            |             |                    |
     |                            |             |onConnectionChangedAdded()
     |                            |             |------------------->|
     |                            |             |                    |
     |                            |             |                subscribe
     |                            |             |                    |
     |                            |             |              Reflect topology
     |                            |             |                    |                 [Network]
     |                            |             |                    | PUT topology      |
     |                            |             |                    |------------------>|
     |                            |             | changestatus(running)                  |
     |                            |             |&lt;-------------------|
     |                            |             |                    |
     |                            |PUT connections(status:running)   |
     |                            |&lt;------------|                    |
     |                      [status:running]    |                    |
     |                            |             |                    |
     |                            |             |
</pre>

----
#### disconnect

<pre>
 [controller/LogicComponent]  [systemMgr]    [Logic]            [Component]
     |                            |             |                    |
     |                      [status:running]    |                    |
     |DEL connections             |             |                    |
     |--------------------------->|             |                    |
     |                            |ConnectionChanged(Del)            |
     |                            |------------>|                    |
     |                            |             |                    |
     |                            |             |onConnectionChangedDelPre()
     |                            |             |------------------->|
     |                            |             |                    |
     |                            |             |                checks param
     |                            |             |                    |
     |                            |             |onConnectionChangedDel()
     |                            |             |------------------->|
     |                            |             |                    |
     |                            |             | changestatus(finalizing)
     |                            |             |&lt;-------------------|
     |                            |             |                    |
     |                            |PUT connections(status:finalizing)|
     |                            |&lt;------------|                    |
     |        x[ConnectionChanged(UPDATE)](*)   |                    |
     |                    x&lt;------|x[ConnectionChanged(UPDATE)](*)   |
     |                            |-->x         |                    |
     |                      [status:finalizing] |                    |
     |                            |             |                    |
     |                            |             |                 unsubscribe
     |                            |             |                    |
     |                            |             |               Reflect topology
     |                            |             |                    |                 [Network]
     |                            |             |                    | DEL topology      |
     |                            |             |                    |------------------>|
     |                            |             |                    |                   |
     |                            |             | changestatus(none) |                   |
     |                            |             |&lt;-------------------|                   |
     |                            |PUT connections(status:none)      |
     |                            |&lt;------------|                    |
     |                     [delete connection]  |
     |                            | x[ConnectionChanged(DELETE)](*)
     |                            |--->x        |
     |  x[ConnectionChanged(DELETE)](*)         |
     |                     x&lt;-----|             |

(*) Event notification None.

</pre>

----
#### <a name="ConnectLogicAndNetwork"> ConnectLogicAndNetwork</a>  
Connection information between Logic Component and Network Component.    

**key**          | **value** | **description**                                  | POST         | PUT 
-----------------|-----------|--------------------------------------------------|--------------|------
id               | \<String> |see [ComponentConnection](#ComponentConnection)   | -(*1)        | Mandatory 
type 　　 　     | \<String> |type is "LogicAndNetwork"                         | Mandatory    | Mandatory
connection_type  | \<String> |see [ComponentConnection](#ComponentConnection)   | Optional     | Optional
state            | \<String> |see [ComponentConnection](#ComponentConnection)   | Optional(*2) | Mandatory
logic_id         | \<String> |LogicComponent ID                                 | Mandatory    | Mandatory
network_id       | \<String> |NetworkComponent ID                               | Mandatory    | Mandatory

  (\*1)id is automatically assigned.  
  (\*2)If you do not set the state,state set  "initializing"  

##### example(JSON)

    {    
    "id": "slicer1_network1"
    "type": "LogicAndNetwork"
    "connection_type": "original"
    "state": "running"
    "logic_id": "slicer1",
    "network_id": "network1",						
    }    


----
#### <a name="Boundary"> Boundary</a>  
Boundary abstract class.  
Connection rule between heterogeneous networks.

**key**  | **value** | **description**             | POST         | PUT 
---------|-----------|-----------------------------|--------------|-----------
id       | \<String> |Unique Identifier in ODENOS. | -(*1)        | Mandatory 
type     | \<String> |Boundary Type                | Mandatory    | Mandatory

  (\*1)id is automatically assigned.


##### Type
 *  [FederatorBoundary](#FederatorBoundary)
 *  [LinkLayerizerBoundary](#LinkLayerizerBoundary)

----
#### <a name="FederatorBoundary"> FederatorBoundary</a>  
Federator Boundary class.  
Connection rule between heterogeneous networks.  

**key**  | **value** | **description**                         | POST        | PUT 
---------|-----------|-----------------------------------------|-------------|-----------
id       | \<String> | Unique Identifier in ODENOS.            | -(*1)       | Mandatory
type     | \<String> | Boundary Type is "Federator"            | Mandatory   | Mandatory
network1 | \<String> | networkComponent ID (ObjectProperty.id) | Mandatory   | Mandatory
node1    | \<String> | Connection [Node](#Node).id             | Mandatory   | Mandatory
port1    | \<String> | Connection [Port](#Port).id             | Mandatory   | Mandatory
network2 | \<String> | networkComponent ID (ObjectProperty.id) | Mandatory   | Mandatory
node2    | \<String> | Connection [Node](#Node).id             | Mandatory   | Mandatory
port2    | \<String> | Connection [Port](#Port).id             | Mandatory   | Mandatory

  (\*1)id is automatically assigned.

##### example(JSON)
<pre>
  {    
    "id"      : "federator1_boundary1",
    "type"    : "Federator",
    "network1": "original_network1",
    "network2": "original_network2",
    "node1"   : "original_network1_node1",
    "node2"   : "original_network2_node1",
    "port1"   : "original_network1_port1",
    "port2"   : "original_network2_port1",
  }    
</pre>



----
#### <a name="LinkLayerizerBoundary"> LinkLayerizerBoundary</a>  
LinkLayerizer Boundary class.  
Connection rule between heterogeneous networks.  

**key**        | **value** | **description**                         | POST        | PUT 
---------------|-----------|-----------------------------------------|-------------|-----------
id             | \<String> | Unique Identifier in ODENOS.            | -(*1)       | Mandatory
type           | \<String> | Boundary Type is "LinkLayerizer"        | Mandatory   | Mandatory
lower_nw       | \<String> | networkComponent ID (ObjectProperty.id) | Mandatory   | Mandatory
lower_nw_node  | \<String> | Connection [Node](#Node).id             | Mandatory   | Mandatory
lower_nw_port  | \<String> | Connection [Port](#Port).id             | Mandatory   | Mandatory
upper_nw       | \<String> | networkComponent ID (ObjectProperty.id) | Mandatory   | Mandatory
upper_nw_node  | \<String> | Connection [Node](#Node).id             | Mandatory   | Mandatory
upper_nw_port  | \<String> | Connection [Port](#Port).id             | Mandatory   | Mandatory

  (\*1)id is automatically assigned.

##### example(JSON)
<pre>
  {    
    "id"           : "linklayerizer1_boundary1",
    "type"         : "LinkLayerizer",
    "lower_nw"     : "lower_nw1",
    "upper_nw"     : "upper_nw2",
    "lower_nw_node": "lower_nw_node1",
    "upper_nw_node": "upper_nw_node1",
    "lower_nw_port": "lower_nw_port1",
    "upper_nw_port": "upper_nw_port1",
  }    
</pre>

----
#### <a name="EventSubscription"> EventSubscription</a>

**key**        | **value** | **description**
---------------|-----------|-----------------
subscriber_id  | \<String> |	subscriber's ObjectProperty.id
event_filters  | dict<ObjectProperty.id, list [EventType.event_type] | 	Subscribe conditions.


----
#### <a name="Event"> Event</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> |event publishing Object's Id.
event_type   | \<String> |event type 
body         |    -      |Different value for each event.


----
#### <a name="ObjectPropertyChanged"> ObjectPropertyChanged</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing RemoteObject.
event_type   | \<String> | "ObjectPropertyChanged"
action       | \<String> | "update"
prev         | [ObjectProperty](#ObjectProperty) |Before the update ObjectProperty.
curr         | [ObjectProperty](#ObjectProperty) |update ObjectProperty.


----
#### <a name="ObjectSettingsChanged"> ObjectSettingsChanged</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing RemoteObject.
event_type   | \<String> | "ObjectSettingsChanged"
action       | \<String> | "update"
prev         | [ObjectSettings](#ObjectSettings) |Before the update ObjectSettings.
curr         | [ObjectSettings](#ObjectSettings) |update ObjectSettings.

----
#### <a name="ComponentConnectionChanged"> ComponentConnectionChanged</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing SystemManager.
event_type   | \<String> | "ComponentConnectionChanged"
action       | \<String> | "add", "delete", or "update" (string)
prev         | [ComponentConnection](#ComponentConnection) |Before the update ComponentConnection. none If action is "add".
curr         | [ComponentConnection](#ComponentConnection) |update ComponentConnection. none If action is "delete".


----
#### <a name="ComponentManagerChanged"> ComponentManagerChanged</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing SystemManager.
event_type   | \<String> | "ComponentManagerChanged"
action       | \<String> | "update"
prev         | [ObjectSettings](#ObjectSettings) |Before the update ObjectSettings.
curr         | [ObjectSettings](#ObjectSettings) |update ObjectSettings.


----
#### <a name="ComponentChanged"> ComponentChanged</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing ComponentManager.
event_type   | \<String> | "ComponentChanged"
action       | \<String> | "add", "delete", or "update"
prev         | [ObjectProperty](#ObjectProperty) |Before the update ObjectProperty. none If action is "add".
curr         | [ObjectProperty](#ObjectProperty) |update ObjectProperty. none If action is "delete".


----
#### <a name="TopologyChanged"> TopologyChanged</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing network.
event_type   | \<String> | "TopologyChanged"
version      | \<String> |[Topology](#Topology).version
prev         | [Topology](#Topology) | none
curr         | [Topology](#Topology) | none


----
#### <a name="NodeChanged"> NodeChanged</a>

**key**      | **value**     | **description**
-------------|---------------|-----------------
publisher_id | \<String>     |event publishing network.
event_type   | \<String>     |"NodeChanged"
id           | \<String>     |[Node](#Node).node_id
action       | \<String>     |"add", "delete", or "update"
version      | \<String>     |curr [Node](#Node).version
prev         | [Node](#Node) |Before the update Node. none If action is "add".
curr         | [Node](#Node) |update Node. none If action is "delete".

----
#### <a name="PortChanged"> PortChanged</a>

**key**      | **value**     | **description**
-------------|---------------|-----------------
publisher_id | \<String>     |event publishing network.
event_type   | \<String>     |"PortChanged"
id           | \<String>     |[Port](#Port).port_id
action       | \<String>     |"add", "delete", or "update"
version      | \<String>     |curr [Port](#Port).version
prev         | [Port](#Port) |Before the update Port. none If action is "add".
curr         | [Port](#Port) |update Port. none If action is "delete".


----
#### <a name="LinkChanged"> LinkChanged</a>

**key**      | **value**     | **description**
-------------|---------------|-----------------
publisher_id | \<String>     | event publishing network.
event_type   | \<String>     | "LinkChanged"
id           | \<String>     | [Link](#Link).link_id
action       | \<String>     | "add", "delete", or "update"
version      | \<String>     |curr [Link](#Link).version
prev         | [Link](#Link) |Before the update Link. none If action is "add".
curr         | [Link](#Link) |update Link. none If action is "delete".


----
#### <a name="FlowChanged"> FlowChanged</a>

**key**      | **value**     | **description**
-------------|---------------|-----------------
publisher_id | \<String>     |event publishing network.
event_type   | \<String>     |"FlowChanged"
id           | \<String>     |[Flow](#Flow).flow_id
action       | \<String>     |"add", "delete", or "update"
version      | \<String>     |curr [Flow](#Flow).version
prev         | [Flow](#Flow) |Before the update Flow. none If action is "add".
curr         | [Flow](#Flow) |update Flow. none If action is "delete".


----
#### <a name="InPacketAdded"> InPacketAdded</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing network.
event_type   | \<String> | "InPacketAdded"
id           | \<String> | [InPacket](#InPacket).packet_id


----
#### <a name="OutPacketAdded"> OutPacketAdded</a>

**key**      | **value** | **description**
-------------|-----------|-----------------
publisher_id | \<String> | event publishing network.
event_type   | \<String> | "OutPacketAdded"
id           | \<String> | [OutPacket](#OutPacket).packet_id

