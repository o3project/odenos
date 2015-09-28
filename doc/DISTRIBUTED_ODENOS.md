#Distributed ODENOS deployment

##Requirements:
1. redis server running on host1
2. zookeeper server running on host1

You can install zookeeper server as follows:
```
$ sudo apt-get install zookeeperd
```

##Example configuration:
```


   resttranslator
   systemmanager          romgr1
   [host2]                [host3]
      |                      |
      |                      |
      +----------+-----------+
                 |
              [host1] 
              - redis server
              - zookeeper server
```

host2's etc/odenos.conf
```
     :
#manager.diabled
     :
#PROCESS    romgr1,java,apps/java/sample_components/target/classes
#PROCESS    romgr2,python,apps/python/sample_components
     :
#rest.host                      localhost
     :
pubsub.server.host              172.17.42.1
     :
zookeeper.host                  172.17.42.1
#zookeeper.embed
     :
```

Please uncomment the variables, pubsub.server.host and zookeeper.host,
and set IP-address of host1 to those variables.
And comment out the variables, PROCESS and zookeeper.embed.

host3's etc/odenos.conf
```
     :
manager.disabled
     :
PROCESS    romgr1,java,apps/java/sample_components/target/classes
#PROCESS    romgr2,python,apps/python/sample_components
     :
#rest.host                      172.17.42.2
     :
pubsub.server.host              172.17.42.1
     :
zookeeper.host                  172.17.42.1
#zookeeper.embed
```

Please uncomment the variables, manager.disabled,
pubsub.server.host and zookeeper.host, and set IP-address of host1
to pubsub.server.host and zookeeper.host.
And comment out the variables zookeeper.embed.

##Run odenos processes!

[Step 1] Start odenos on host3. The odenos process waits for systemmanager(host2) to be up.

[Step 2] Start odenos on host2. The odenos process connects to server(host1). 

[Step 3] Confirm that the odenos process on host2 outputs the following message:
```
Started Compnent Manager :: romgr1
```
