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
   $ sudo apt-get install git maven openjdk-7-jdk curl
   $ export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
   ```

   If using java-1.8, please use Oracle JDK instead of OpenJDK.
   The first, download a JDK-8 archive from
   [Java SE - Downloads](http://www.oracle.com/technetwork/java/javase/downloads/index.html),
   and then install its.

   ```
   $ tar xf jdk-8u25-linux-x64.tar.gz -C /usr/lib/jvm
   $ ln -s jdk1.8.0_25 /usr/lib/jvm/java-8-oracle
   $ export JAVA_HOME=/usr/lib/jvm/java-8-oracle
   ```

## 2. Build, Run, Test

1. git clone

   ```
   $ git clone https://github.com/o3project/odenos.git
   $ cd odenos
   ```

2. Build

   ```
   $ mvn package
   ```

3. Start odenos

   ```
   $ ./odenos start
   ```

4. Stop odenos

   ```
   $ ./odenos stop
  ```

5. run unit tests (if you want)

   ```
   $ ./run-unittests.sh
   ```

6. run examples (if you want)

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
   $ sudo apt-get install python-setuptools python-dev
   $ sudo -E easy_install msgpack-python redis futures mock coverage kazoo
   ```

2. Add the following line to *./etc/odenos.conf*

   ```
   PROCESS romgr2,python,apps/python/sample_components
   ```

3. Start ODENOS


### A-2. Ruby Environment & OpenFlowDriver

1. Install rvm, ruby2.0.0 and gem package

   ```
   $ cd ./odenos
   $ sudo apt-get --purge remove ruby rubygems
   $ curl -L https://get.rvm.io | bash -s stable --ruby
   $ source ~/.rvm/scripts/rvm
   $ rvm install ruby-2.0.0-p643
   $ rvm use --default ruby-2.0.0-p643
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


### A-3. Syslog Setting

If you want to output syslog, please configure syslog.

1. Install rsyslog package

   ```
   $ sudo apt-get install rsyslog
   ```

2. Add the file *odenos/etc/80-odenos.conf* into */etc/rsyslog.d/*,
   and customize this file.

3. To suppress ODENOS log output to default file, change the file
   */etc/rsyslog.d/50-default.conf*.  For example as following:

   ```
   *.*;auth,authpriv.none,local1.none -/var/log/syslog
   ```

4. Enable *ModLoad* parameter in */etc/rsyslog.conf* for java logging.

   ```
   $ModLoad imudp
   $UDPServerRun 514
   ```

5. And then, restart rsyslog service

   ```
   $ sudo service rsyslog restart
   ```

6. Enable syslog configuration in the following files of ODENOS.

   - *odenos/etc/log4j2_java.yaml*
   - *odenos/etc/log_java.conf*
   - *odenos/etc/log_python.conf*
   - *odenos/etc/log_ruby.conf*


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

### Q. Where could I find other technical documents (API, etc.)?

Please start looking from here [doc/api](/doc/api/index.md)
