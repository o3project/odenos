#Distributed ODENOS deployment

##Requirements:
1. redis server running on host1
2. zookeeper server running on host1

You can install zookeeper server as follows:
```
$ sudo apt-get install zookeeper-server
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
pubsub.server.host              172.17.42.1
     :
zookeeper.host                  172.17.42.1
#zookeeper.embed
     :
```

host3's etc/odenos.conf
```
PROCESS    romgr1,java,apps/java/sample_components/target/classes
#PROCESS    romgr2,python,apps/python/sample_components
     :
pubsub.server.host              172.17.42.1
     :
zookeeper.host                  172.17.42.1
```

##Run odenos processes!

[Step 1] Start odenos on host3. The odenos process waits for systemmanager(host2) to be up.

[Step 2] Start odenos on host2. Confirm that that the odenos process on host1 outputs the following messages: 
```
Start-up completion: systemmanager
Start-up completion: resttranslator
```
[Step 3] Confirm that the odenos process on host2 outputs the following message:
```
Start-up completion: romgr1
```
