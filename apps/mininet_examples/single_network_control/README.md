# Single Network Control

This example changes single openflow **network** into **one big layer2 switch**,
so the following scripts configure ODENOS as below:

```
 [LearningSwitch l2sw1]
          |
  [Network network2]
          |
[Aggregator aggregator1]
          |
  [Network network1]
          |
[OpenFlowDriver driver1]
          |
          |
          | (OpenFlow Secure Channel)
          |
          |	  
      (mininet)
 h1 -- s1 -- s4
       |     |
       |     |
      s2 -- s3 -- h2
```


## Setup

1. mininet

   Install ODENOS with python and ruby environments. see [Getting Started with ODENOS](https://github.com/o3project/odenos/blob/master/doc/QUICKSTART.md).

2. mininet

   ```
   $ sudo apt-get install mininet
   ```


## Run

### 1. Start ODENOS

```
$ ./start_odenos.sh start
```

### 2. Start Mininet

```
$ sudo ./start_mininet.py
```

### 3. Check ODENOS status

1. check components

   ```
   $ curl http://localhost:10080/systemmanager/components
   ```

```
Response
{
    "network2": {
        "id": "network2",
        "super_type": "Network",
        "connection_types": "",
        "description": "Network Component",
        "state": "running",
        "flow_type": "BasicFlow",
        "type": "Network"
    },
    "network1": {
        "id": "network1",
        "super_type": "Network",
        "connection_types": "",
        "description": "Network Component",
        "state": "running",
        "flow_type": "BasicFlow",
        "type": "Network"
    },
    "aggregator1": {
        "id": "aggregator1",
        "super_type": "Aggregator",
        "connection_types": "aggregated:1,original:1",
        "description": "Aggregator Component",
        "state": "running",
        "type": "Aggregator"
    },
    "l2sw1": {
        "id": "l2sw1",
        "super_type": "LearningSwitch",
        "connection_types": "original:1",
        "description": "Learning Switch",
        "state": "running",
        "type": "LearningSwitch"
    }
}
```

2. check connections

   ```
   $ curl http://localhost:10080/systemmanager/connections
   ```

```
Response
{
    "aggregator1-network1": {
        "id": "aggregator1-network1",
        "network_id": "network1",
        "logic_id": "aggregator1",
        "state": "running",
        "type": "LogicAndNetwork",
        "connection_type": "original"
    },
    "aggregator1-network2": {
        "id": "aggregator1-network2",
        "network_id": "network2",
        "logic_id": "aggregator1",
        "state": "running",
        "type": "LogicAndNetwork",
        "connection_type": "aggregated"
    },
    "driver1-network1": {
        "id": "driver1-network1",
        "network_id": "network1",
        "logic_id": "driver1",
        "state": "initializing",
        "type": "LogicAndNetwork",
        "connection_type": "original"
    },
    "l2sw1-network2": {
        "id": "l2sw1-network2",
        "network_id": "network2",
        "logic_id": "l2sw1",
        "state": "running",
        "type": "LogicAndNetwork",
        "connection_type": "original"
    }
}
```

3. check networks' topology

   ```
   $ curl http://localhost:10080/network1/topology
   $ curl http://localhost:10080/network2/topology
   ```

```
Response
{
    "type": "Topology",
    "version": "12",
    "nodes": {
        "node0x1": {
            "type": "Node",
            "version": "1",
            "node_id": "node0x1",
            "ports": {

     :
     :
     
}

```


4. start ping from h1 to h2

   ```
   mininet> h1 ping h2
   ```

```
Response
64 bytes from 10.0.0.2: icmp_seq=100 ttl=64 time=0.224 ms
64 bytes from 10.0.0.2: icmp_seq=101 ttl=64 time=0.135 ms
64 bytes from 10.0.0.2: icmp_seq=102 ttl=64 time=0.001 ms
  :
  :
```

5. check networks' flows

   ```
   $ curl http://localhost:10080/network1/flows
   $ curl http://localhost:10080/network2/flows
   ```

```
Response
{
    "priority": {
        "0": [
            "l2sw1_0",
            "l2sw1_1"
        ]
    },
    "type": "FlowSet",
    "flows": {
        "l2sw1_1": {
            "enabled": true,
            "matches": [
                {
                    "eth_src": "c2:26:d9:72:fc:90",
                    "in_port": "port3@0x1",
                    "eth_dst": "62:6d:ae:13:e3:d9",
                    "in_node": "node0x1",
                    "type": "OFPFlowMatch"
                }
            ],
  :
  :

}
```

6. check networks' packets

   ```
   $ curl http://localhost:10080/network1/packets/
   $ curl http://localhost:10080/network2/packets/
   ```

```
Response
{
    "type": "PacketStatus",
    "in_packet_count": 3,
    "in_packet_bytes": 2574,
    "in_packet_queue_count": 0,
    "in_packets": [],
    "out_packet_count": 3,
    "out_packet_bytes": 2574,
    "out_packet_queue_count": 0,
    "out_packets": []
}
```

7. check l2sw1's fdb

   ```
   $ curl http://localhost:10080/l2sw1/fdb
   ```

```
Response
{
    "62:6d:ae:13:e3:d9": {
        "port_id": "node0x3_port3@0x3",
        "node_id": "aggregator1"
    },
    "c2:26:d9:72:fc:90": {
        "port_id": "node0x1_port3@0x1",
        "node_id": "aggregator1"
    }
}
```


### 4. Stop ODENOS

```
$ ./start_odenos.sh stop
```
