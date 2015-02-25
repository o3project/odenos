# Quick Start

## 1. Setup

Basically, ODENOS runs with redis-server and Java. If you want to use
Python or Ruby environment, please see "Appendix A".

### 1-1. redis-server

1. Install redis-server(version >= 2.8.4)

   ```
   $ sudo apt-get install redis-server
   ```

2. Edit the following parameters in */etc/redis/redis.conf*
   * bind 0.0.0.0
   * timeout 0
   * client-output-buffer-limit pubsub 256mb 256mb 60

3. Restart redis-server

   ```
   $ sudo service redis-server restart
   ```

### 1-2. Java Environment

1. Install jdk and maven

   ```
   $ sudo apt-get install maven openjdk-7-jdk
   $ export JAVA_HOME=/usr/lib/jvm/default-java
   ```

## 2. Build, Run, Test

1. Build

   ```
   $ mvn package
   ```

2. Start odenos

   ```
   $ ./odenos start
   ```

3. Stop odenos

   ```
   $ ./odenos stop
  ```

4. run unit tests (if you want)

   ```
   $ ./run-unittests.sh
   ```

5. run examples (if you want)

   ```
   $ cd apps/example
   $ ./run-example.sh all
   ```

## 3. Let's try ODENOS examples with Mininet
* [Single Node](../apps/mininet_examples/single_node_control/README.md)
* [Single Network](../apps/mininet_examples/single_network_control/README.md)
* [Multiple Networks](../apps/mininet_examples/multi_network_control/README.md)


## Appendix A: Setup Additional Environments

### A-1. Python Environment

1. Install python packages

   ```
   $ sudo apt-get install python-setuptools
   $ sudo -E easy_install msgpack-python redis futures mock coverage
   ```

2. Add the following line to *./etc/odenos.conf*

   ```
   PROCESS romgr2,python,apps/python/sample_components
   ```

3. Start ODENOS


### A-2. Ruby Environment & OpenFlowDriver

1. Install rvm, ruby2.0.0 and gem package

   ```
   $ sudo apt-get --purge remove ruby rubygems
   $ curl -L https://get.rvm.io | bash -s stable --ruby
   $ source ~/.rvm/scripts/rvm
   $ rvm install ruby-2.0.0-p576
   $ rvm use --default ruby-2.0.0-p576
   $ gem install bundler
   $ bundle install
   $ bundle update
   ```

2. Install openvswitch and trema-edge

   ```
   $ sudo apt-get install libsqlite3-dev sqlite3 libpcap-dev libssl-dev openvswitch-common openvswitch-switch
   $ git clone http://github.com/trema/trema-edge.git
   $ mv trema-edge ~/
   $ cd ~/trema-edge
   $ git checkout 148acb9cd7f654020098a5e769bfedad273a687b
   $ gem install bundler
   $ bundle install
   $ bundle update
   $ rake
   $ cd -
   ```

3. Start OpenFlowDriver

   ```
   $ ./odenos start
   $ ~/trema-edge/trema run -d "./src/main/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr3"
   ```

4. Stop OpenFlowDriver

   ```
   $ ~/trema-edge/trema killall
   ```


## FAQ

### Q. What kind of options are there for OpenFlowDriver?

You can specify the following options:

```
'--cmpmgr=id'                 : "Trema ComponentManager's object ID"
'--rip=redis_server_id'       : "Redis Server's ip address (default 127.0.0.1)"
'--rport=redis_server_port'   : "Redis Server's port       (default: 6379)"
'--vendor='VENDOR1'           : "set VendorID              (default 'OpenFlow')"
```

### Q. I saw "While executing gem" error. What should I do?

Try the following instruction:

1. Change source site (https --> http)

   ```
   $ gem source -a http://rubygems.org/
   $ gem source -r https://rubygems.org/
   ```

2. Check source site

   ```
   $ gem source -l
   *** URRENT SOURCES ***
   
   http://rubygems.org/
   ```

3. Retry gem install

   ```
   $ gem install bundler
   ```
