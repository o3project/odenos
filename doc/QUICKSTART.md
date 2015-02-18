Quick Start
==========================

ODENOS uses redis-server, Java, Python and Ruby. Please setup
mandatory environments, Redis and Java environment. If you want to use
Python or Ruby environment, especially OpenFlowDriver which is written
in Ruby, please setup the environments.

1. Setup
--------------------------
[Redis Environment]
    Redis server version 2.8.4 higher.
    $ sudo apt-get install redis-server

    Please edit redis.conf:
    $ sudo vi /etc/redis/redis.conf
      >"bind 0.0.0.0"
      >"timeout 0"
      >"client-output-buffer-limit pubsub 256mb 256mb 60"

    Restart redis-server:
    $ sudo service redis-server restart

[Java Environment]
    $ sudo apt-get install maven openjdk-7-jdk
    $ export JAVA_HOME=/usr/lib/jvm/default-java

[Python Environment]
    $ sudo apt-get install python-setuptools
    $ sudo -E easy_install msgpack-python redis futures mock coverage

    Please edit odenos.conf:
    $ sudo vi etc/odenos.conf
      >PROCESS romgr2,python,apps/python/sample_components

[Ruby Environment]
 --> See [Appendix A: Setup Ruby & OpenFlowDriver]


2. Build ODENOS
--------------------------
   $ mvn package

   
3. Start ODENOS
--------------------------
   $ ./odenos start

   
4. Run Tests
--------------------------
[Unit Tests]
   $ ./run-unittests.sh

[Integration test]
   $ cd apps/example
   $ ./run-example.sh all

			
Appendix A: Setup Ruby & OpenFlowDriver
----------------------------------------
  [Install rvm, ruby2.0.0, gem package.]
  ----------------------------------------
    $ sudo apt-get --purge remove ruby rubygems
    $ curl -L https://get.rvm.io | bash -s stable --ruby
    $ source ~/.rvm/scripts/rvm
    $ rvm install ruby-2.0.0-p576
    $ rvm use --default ruby-2.0.0-p576
    $ gem install bundler
    $ bundle install
    $ bundle update 

  [install openvswitch, trema-edge.]
  ----------------------------------------
    $ sudo apt-get install libsqlite3-dev sqlite3 libpcap-dev libssl-dev openvswitch-common openvswitch-switch
    $ git clone http://github.com/trema/trema-edge.git
    $ mv trema-edge ~/
    $ pushd ~/trema-edge
    $ git checkout 148acb9cd7f654020098a5e769bfedad273a687b
    $ gem install bundler
    $ bundle install
    $ bundle update
    $ rake
    $ popd

  [Run OpenFlowDriver]
  ---------------------------------------------
    $ ./odenos start
    $ ~/trema-edge/trema run -d "./src/main/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=of_comp_mgr"

  [Stop OpenFlowDriver]
  ----------------------------------------
    $ ~/trema-edge/trema killall

    
  [FAQ]
    * Command Options for OpenFlowDriver
      '--cmpmgr=id'			: "Trema ComponentManager's object ID"
      '--rip=redis_server_id'		: "Redis Server's ip address(default 127.0.0.1)"
      '--rport=redis_server_port'	: "Redis Server's port(default: 6379)"
      '--vendor'			: "set VendorID (default 'OpenFlow')"
    
    * if "While executing gem" occurs, change the protocol of the source site into "http".
        (1) show souce site.
          $ gem source -l 
            > *** URRENT SOURCES ***
            > 
            > https://rubygems.org/
        (2) set souce site. (https --> http)
          $ gem source -a http://rubygems.org/
          $ gem source -r https://rubygems.org/
        (3) check souce site.
          $ gem source -l 
            > *** URRENT SOURCES ***
            > 
            > http://rubygems.org/
        (4) retry gem install
          $ gem install bundler
