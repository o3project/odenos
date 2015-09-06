# Control of Multiple Networks (DCNs & WAN)

Let's make one big switch on multi-domain and multi-layer networks.
This example emulates that three data center networks(DCNs) connected by a wide area network(WAN).

*network1-3* express these DCNs and *network4* does the WAN which provides tunnels between DCNs.
(Note that to emulate connected multi-layer networks in single mininet instance, we use four OpenFlowDriver instances which control different switches in the network.)

In ODENOS, we use three Operators to integrate networks like:
 1. *federator1* merges three DCNs(*network1-3*) into *network5*
 2. *linklayerizer1* merges two different layers(*network4 & 5*) into *network6*.
 3. *aggregator1* aggregates *network6* to *network7* which has only single node.

Consequently, there is single node network from the *l2sw1*'s point of view.
So we do not need to modify any code of *l2sw1*.


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
$ ./start_odenos.sh start
```

### 2. Start mininet

```
$ sudo ./start_mininet.py
```

### 3. Check ODENOS status

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


### 4. Stop ODENOS

```
$ ./start_odenos.sh stop
```
