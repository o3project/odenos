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
$ cd {ODENOS HOME}
$ ./apps/mininet_examples/single_node_control/start_odenos.sh start
```

### 2. start mininet

```
$ sudo ./apps/mininet_examples/single_node_control/start_mininet.py
```

### 3. config odenos

```
$ sudo PYTHONPATH=lib/python/ ./apps/mininet_examples/single_node_control/config_odenos.py
```

### 4. check odenos status

1. check components

   ```
   $ curl http://localhost:10080/systemmanager/components
   ```

2. check connections

   ```
   $ curl http://localhost:10080/systemmanager/connections
   ```

3. check network1's topology

   ```
   $ curl http://localhost:10080/network1/topology
   ```

4. start ping from h1 to h2

   ```
   mininet> h1 ping h2
   ```

5. check network1's flows

   ```
   $ curl http://localhost:10080/network1/flows
   ```

6. check network1's packets

   ```
   $ curl http://localhost:10080/network1/packets/
   ```

7. check l2sw1's fdb

   ```
   $ curl http://localhost:10080/l2sw1/fdb
   ```


### 5. stop odenos

```
$ ./apps/mininet_examples/single_node_control/start_odenos.sh stop
```
