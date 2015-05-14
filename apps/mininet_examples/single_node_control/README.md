# Single Node Control

This example changes single openflow node into an layer2 switch,
so the following scripts configure ODENOS as below:

```
 [LearningSwitch l2sw1]
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
    h1 -- s1 -- h2
```


## Setup

1. mininet

   Install ODENOS with python and ruby environments. see [Getting Started with ODENOS](https://github.com/o3project/odenos/blob/master/doc/QUICKSTART.md).

2. mininet

```
$ sudo apt-get install mininet
```


## Run

### 1. start odenos

```
$ ./start_odenos.sh start
```

### 2. start mininet

```
$ sudo ./start_mininet.py
```

### 3. check odenos status

####1. check components

   ```
   $ curl http://localhost:10080/systemmanager/components
   ```

```
Response
{
    "network1": {
        "id": "network1",
        "super_type": "Network",
        "connection_types": "",
        "description": "Network Component",
        "state": "running",
        "flow_type": "BasicFlow",
        "type": "Network"
    },
    "driver1": {
        "id": "driver1",
        "super_type": "Driver",
        "connection_types": "original:1",
        "description": "OpenFlowDriver for ruby",
        "state": "running",
        "type": "OpenFlowDriver"
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


####2. check connections

   ```
   $ curl http://localhost:10080/systemmanager/connections
   ```

```
Response
{
    "driver1-network1": {
        "id": "driver1-network1",
        "network_id": "network1",
        "logic_id": "driver1",
        "state": "running",
        "type": "LogicAndNetwork",
        "connection_type": "original"
    },
    "l2sw1-network1": {
        "id": "l2sw1-network1",
        "network_id": "network1",
        "logic_id": "l2sw1",
        "state": "running",
        "type": "LogicAndNetwork",
        "connection_type": "original"
    }
}
```

####3. check network1's topology

   ```
   $ curl http://localhost:10080/network1/topology
   ```


```
Response
{
    "type": "Topology",
    "version": "1",
    "nodes": {
        "node0x1": {
            "type": "Node",
            "version": "1",
            "node_id": "node0x1",
            "ports": {
                "port1@0x1": {
                    "type": "Port",
                    "version": "1",
                    "port_id": "port1@0x1",
                    "node_id": "node0x1",
                    "out_link": null,
                    "in_link": null,
                    "attributes": {
                        "oper_status": "UP",
                        "unreserved_bandwidth": "10000",
                        "physical_id": "1@0x1",
                        "max_bandwidth": "10000",
                        "admin_status": "UP",
                        "hw_addr": "de:4d:25:65:c4:7a",
                        "vendor": "OpenFlow"
                    }
                },
                "port2@0x1": {
                    "type": "Port",
                    "version": "1",
                    "port_id": "port2@0x1",
                    "node_id": "node0x1",
                    "out_link": null,
                    "in_link": null,
                    "attributes": {
                        "oper_status": "UP",
                        "unreserved_bandwidth": "10000",
                        "physical_id": "2@0x1",
                        "max_bandwidth": "10000",
                        "admin_status": "UP",
                        "hw_addr": "fa:c4:65:f3:6c:8e",
                        "vendor": "OpenFlow"
                    }
                }
            },
            "attributes": {
                "oper_status": "UP",
                "admin_status": "UP",
                "vendor": "OpenFlow",
                "physical_id": "0x1"
            }
        }
    },
    "links": {}
}
```


####4. start ping from h1 to h2

   ```
   mininet> h1 ping h2
   ```

```
Response
64 bytes from 10.0.0.2: icmp_seq=129 ttl=64 time=0.161 ms
64 bytes from 10.0.0.2: icmp_seq=130 ttl=64 time=0.346 ms
64 bytes from 10.0.0.2: icmp_seq=131 ttl=64 time=0.202 ms
 :
 :
 :
```


####5. check network1's flows

   ```
   $ curl http://localhost:10080/network1/flows
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
                    "eth_src": "6a:d0:11:72:9e:a5",
                    "in_port": "port1@0x1",
                    "eth_dst": "1e:55:e2:1f:fd:10",
                    "in_node": "node0x1",
                    "type": "OFPFlowMatch"
                }
            ],
            "flow_id": "l2sw1_1",
            "status": "established",
            "edge_actions": {
                "node0x1": [
                    {
                        "output": "port2@0x1",
                        "type": "FlowActionOutput"
                    }
                ]
            },
            "priority": "0",
            "path": [],
            "owner": "l2sw1",
            "hard_timeout": 300,
            "attributes": {},
            "type": "OFPFlow",
            "idle_timeout": 60,
            "version": "3"
        },
        "l2sw1_0": {
            "enabled": true,
            "matches": [
                {
                    "eth_src": "1e:55:e2:1f:fd:10",
                    "in_port": "port2@0x1",
                    "eth_dst": "6a:d0:11:72:9e:a5",
                    "in_node": "node0x1",
                    "type": "OFPFlowMatch"
                }
            ],
            "flow_id": "l2sw1_0",
            "status": "established",
            "edge_actions": {
                "node0x1": [
                    {
                        "output": "port1@0x1",
                        "type": "FlowActionOutput"
                    }
                ]
            },
            "priority": "0",
            "path": [],
            "owner": "l2sw1",
            "hard_timeout": 300,
            "attributes": {},
            "type": "OFPFlow",
            "idle_timeout": 60,
            "version": "3"
        }
    },
    "version": "6"
}
```

####6. check network1's packets

   ```
   $ curl http://localhost:10080/network1/packets/
   ```



```
Response
{
    "type": "PacketStatus",
    "in_packet_count": 3,
    "in_packet_bytes": 2563,
    "in_packet_queue_count": 0,
    "in_packets": [],
    "out_packet_count": 3,
    "out_packet_bytes": 2563,
    "out_packet_queue_count": 0,
    "out_packets": []
}
```


####7. check l2sw1's fdb

   ```
   $ curl http://localhost:10080/l2sw1/fdb
   ```

```
Response
{
    "6a:d0:11:72:9e:a5": {
        "port_id": "port1@0x1",
        "node_id": "node0x1"
    },
    "1e:55:e2:1f:fd:10": {
        "port_id": "port2@0x1",
        "node_id": "node0x1"
    }
}
```


### 4. stop odenos

```
$ ./start_odenos.sh stop
```
