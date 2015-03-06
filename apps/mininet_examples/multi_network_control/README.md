# Control of Multiple Networks

This example makes one big layer2 switch on multi-domain and multi-layer networks. 
In this example, the entire network consists of three domains(*network1*, *network2*, 
*network3*) as an overlay network, and one domain(*network4*) as an underlay network. 
First, three networks, *network1*, *network2*, and *network3*, are merged into *network5* 
by *federator1*. Second, two different layers(*network5* and *network4*) are merged into 
*network6* by *linklayerizer1*. And finally, *network6* is aggregated(*network7*). So 
from *l2sw1*'s point of view, the network has only one node.

The following scripts configure ODENOS as below:

```
[LearningSwitch l2sw1]
          |
[Network network7]
          |
[Aggregator aggregator1]
          |
[Network network6]
          |
[LinkLayerizerr linklayerizer1]
          |
          +-------------------------+
          |                         |
[Network network4]         [Network network5]
          |                         |
          |                [Federator federator1]
          |                         |
          |                         +--------------------+---------------------+
          |                         |                    |                     |
          |                [Network network1]    [Network network2]    [Network network3]
          |                         |                    |                     |
[OFDriver driver_wan]      [OFDriver driver_dc1] [OFDriver driver_dc2] [OFDriver driver_dc3]
          |                         |                    |                     |
          |                         |                    |                     |
          |                         |                    |                     |
          |                         |                    |                     |
          |                         |                    |                     |
      (mininet)                 (mininet)            (mininet)             (mininet)
```


## Setup

1. mininet

   Install ODENOS with python and ruby environments(please setup 4 trema-edge directories).
   see [Getting Started with ODENOS](https://github.com/o3project/odenos/blob/master/doc/QUICKSTART.md).
     * ~/trema-edge
     * ~/trema-edge2
     * ~/trema-edge3
     * ~/trema-edge4

2. mininet

```
$ sudo apt-get install mininet
```


## Run

### 1. Start ODENOS

```
$ cd {ODENOS HOME}
$ ./apps/mininet_examples/multi_network_control/start_odenos.sh start
```

### 2. Start mininet

```
$ sudo ./apps/mininet_examples/multi_network_control/start_mininet.py
```

### 3. Configure ODENOS

```
$ sudo PYTHONPATH=lib/python/ ./apps/mininet_examples/multi_network_control/config_odenos.py
```

### 4. Check ODENOS status

1. check components

   ```
   $ curl http://localhost:10080/systemmanager/components
   ```

2. check connections

   ```
   $ curl http://localhost:10080/systemmanager/connections
   ```

3. check networks' topology

   ```
   $ curl http://localhost:10080/network1/topology
   $ curl http://localhost:10080/network2/topology
   $ curl http://localhost:10080/network3/topology
   $ curl http://localhost:10080/network4/topology
   $ curl http://localhost:10080/network5/topology
   $ curl http://localhost:10080/network6/topology
   $ curl http://localhost:10080/network7/topology
   ```

4. start ping from h1 to h2, and h1 to h3

   ```
   mininet> h1 ping h2
   mininet> h1 ping h3
   ```

5. check networks' flows

   ```
   $ curl http://localhost:10080/network7/flows
   $ curl http://localhost:10080/network6/flows
   $ curl http://localhost:10080/network5/flows
   $ curl http://localhost:10080/network4/flows
   $ curl http://localhost:10080/network3/flows
   $ curl http://localhost:10080/network2/flows
   $ curl http://localhost:10080/network1/flows
   ```

6. check networks' packets

   ```
   $ curl http://localhost:10080/network1/packets/
   $ curl http://localhost:10080/network2/packets/
   $ curl http://localhost:10080/network3/packets/
   $ curl http://localhost:10080/network5/packets/
   $ curl http://localhost:10080/network6/packets/
   $ curl http://localhost:10080/network7/packets/
   ```

7. check l2sw1's fdb

   ```
   $ curl http://localhost:10080/l2sw1/fdb
   ```


### 5. Stop ODENOS

```
$ ./apps/mininet_examples/multi_network_control/start_odenos.sh stop
```
