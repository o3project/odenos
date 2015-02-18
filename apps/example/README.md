How to run sample apps
==========================

  $ ./run-example.sh { all | aggregator | simple_l2switch | one_big_l2switch
                        | sliver_network | federated_network | layerized_network }


### [1]. "Aggregator" 

<pre>
  ---------------------------
   * Component & Connection Image :

    [AggregatedNetwork]
          |
      [Aggregator] 
          |
    [OriginalNetwork]
  ---------------------------
</pre>

  1. Create Aggregator[Java]. 
  2. Setting Network(Topology and Flows, In/OutPackets).  
  3. Delete All Components & Connections.  

### [2]. "Simple L2Switch"
<pre>
  ---------------------------
   * Component & Connection Image :

    [LearningSwitch]
          |
      [Network]
          |
    [DummyDriver]
  ---------------------------
</pre>

    1. Create Simple L2Switch[Java]. 
    2. Setting Network(Topology and Flows, In/OutPackets).  
    3. Delete All Components & Connections.  


### [3]. "One Big L2Switch"
<pre>
  ------------------------------------------------------------
   * Component & Connection Image :

      [LearningSwitch]
            |
    [AggregatedNetwork] 
            |
       [Aggregator]
            |
     [OriginalNetwork]
            |
       [DummyDriver]
  ------------------------------------------------------------
</pre>

    1. Create One Big L2Switch[Java]. 
    2. Setting Network(Topology and Flows, In/OutPackets).  
    3. Delete All Components & Connections.  


### [4]. "Sliver Network"
<pre>
  ------------------------------------------------------------
   * Component & Connection Image :

    [LearningSwitch01]   [LearningSwitch02]
            |                   |
    [SliverNetwork01]   [SliverNetwork02] 
            |                   |
            ---------------------
                      |
                  [Slicer]
                      |
              [OriginalNetwork]
                      |
                [DummyDriver]
  ------------------------------------------------------------
</pre>

    1. Create Sliver Network[Java]. 
    2. Setting Network(Topology and Flows, In/OutPackets).  
    3. Delete All Components & Connections.  


### [5]. "Federated Network"
<pre>
  ------------------------------------------------------------
   * Component & Connection Image :

               [LearningSwitch]
                      |
             [FederatedNetwork]
                      |
                 [Federator]
                      |
            ---------------------
            |                   |
   [OriginalNetwork01]   [OriginalNetwork02]
            |                   |
     [DummyDriver01]       [DummyDriver02] 
  ------------------------------------------------------------
</pre>

    1. Create Federated Network[Java]. 
    2. Setting Network(Topology and Flows, In/OutPackets).  
    3. Delete All Components & Connections.  


### [6]. "Layerized Network"
<pre>
  ------------------------------------------------------------
   * Component & Connection Image :

               [LearningSwitch]
                      |
             [LayerizedNetwork]
                      |
               [LinkLayerizer]
                      |
            ---------------------
            |                   |
      [UpperNetwork]      [LowerNetwork]
            |
      [DummyDriver]
  ------------------------------------------------------------
</pre>

    1. Create Layerized Network[Java]. 
    2. Setting Network(Topology and Flows, In/OutPackets).  
    3. Delete All Components & Connections.  


### [7]. "Slice OpenFlowNetwork"

<pre>
  ------------------------------------------------------------
   * Component & Connection Image
        
     [LearningSwitch01]  [LearningSwitch02]
            |                | 
     <SliverNetwork01>   <SliverNetwork02> 
            |                | 
            ----------------- 
                    | 
                [Slicer]
                    | 
            <OriginalNetwork>
                    | 
       [OpenFlowDriver/Controller]
                    | 
             <OpenFlowNetrok> 
  ------------------------------------------------------------
</pre>

    1. Run OpenFlowController, OFComponentManager. 
    ----------------------------------------
        $ cd ("move odenos root directry")
        $ ./odenos start 
        $ ~/trema-edge/trema run -d "./src/main/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=of_comp_mgr --sysmgr=systemmanager" 

    2. Run OpenFlowDriver, Network(Original, Sliver), Slicer, LearningSwitch. 
    ----------------------------------------
        $ cd apps/example/slice_openflow_network/
        $ ruby -I ~/trema-edge/ruby/ start_slice_openflow_network.rb 

    3. Create OpenFlowNetwork in OpenvSwitch. 
    ----------------------------------------
        $ sh ./set_vnet.sh add
            #
            # * Topology Image
            # 
            #  "vhost1" "vhost2" "vhost3" "vhost4"
            #      |      |          |      |
            #      |      |          |      |
            #      < ofs1 >          <<ofs3>>
            #          |                |
            #          ---- <<ofs2>> ---- 
            #
            #  * "vhost1" : 192.168.0.1
            #  * "vhost2" : 192.168.0.2
            #  * "vhost3" : 192.168.0.3
            #  * "vhost4" : 192.168.0.4
            #

    4. Set SliceConditions. 
    ----------------------------------------
        $ python set_slice_condition.py
            #
            # * SliceCondition Image
            #
            #   - SliverNetwork01 : vhost1 <--> vhost3 
            #   - SliverNetwork02 : vhost2 <--> vhost4 
            # 

    5. Ping from "vhost1" to "vhost3". 
    ----------------------------------------
        $ sudo ip netns exec vhost1 ping 192.168.0.3

    6. Ping from "vhost2" to "vhost4". 
    ----------------------------------------
        $ sudo ip netns exec vhost2 ping 192.168.0.4

    7. Dump OpenFlowNetwork Topology and Flows. 
    ----------------------------------------
        $ sh ./set_vnet.sh show_topology
        $ sh ./set_vnet.sh show_flows 

    8. Dump Orininal/Sliver Topology and Flows. 
    ----------------------------------------
        $ sh ./dump_original_topology_and_flows.sh
        $ sh ./dump_sliver_topology_and_flows.sh

    9. Finalizing. 
    ----------------------------------------
        $ ~/trema-edge/trema killall 
        $ sh ./set_vnet.sh delete 


---------------------------------------
