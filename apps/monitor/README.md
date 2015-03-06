OdenOS message sequence diagram generator
=========================================

The program "monitor" taps Redis server to monitor messages exchanged among OdenOS RemoteObject instances, then automatically generates a message sequence diagram.

Everything works in an asynchronous manner with coroutines supported by Tornado packege.

Screen shots
------------
- [Monitor start page (firefox)](doc/start.png)
- [Message detail (firefox)](doc/detail.png)
- [Console output mode](doc/console.png)

Dependencies
------------

This program requires Python 3.4 or higher and the following python packages:
```
$ sudo pip3 install msgpack-python
$ sudo pip3 install pyyaml
$ sudo pip3 install tornado
$ sudo pip3 install tornado-redis
```


Architecture
------------
```
+------------------------------++++
| OdenOS RemoteObject instance |||| resttranslator, systemmanager, ...
+------------------------------++++
         | |  |  |
  publish/subscribe/message
         | |  |  |
       +-----------+
       | Redis     |
       | server    |
       +-----------+
          ^      |
 psubscribe'*'   |
          |  pmessage (non-blocking IO: tornado-redis)
          |      |
          |      V
       +-----------+
       | monitor   | Tornado web server (coroutine-based)
       |           |
       +-----------+
          |     ^
     WebSocket  |
          |    GET message detail
          V     |
       +-----------+
       | browser   | index.html (HTML5 + CSS + JavaScript)
       |           |
       +-----------+
```


Usage
-----

IMPORTANT: you have to uncomment "monitor" in etc/odenos.conf to enable the monitoring feature on OdenOS.

```
Show help:
$ monitor -h

Start monitoring:
$ monitor resttranslator systemmanager romgr1 network1 ...

Start monitoring with "default.yaml":
$ monitor

Start monitoring with additional parameters:
$ monitor -i 172.0.0.1 -p 6379 -w 8888 resttranslator systemmanager romgr1 network1 ...

Then open "http://localhost:8888/index.html" with your browser (firefox is recommended).

Start with -I and -P options for the HTTP server:
$ monitor -I 10.10.10.10 -P 10082

You can also force the monitor to output the diagram to your console:
$ monitor -c

Start with -d option to print out each message in YAML format:
$ monitor -c -d

Start with -d and -j options to print out each message in JSON format:
$ monitor -c -d -j
```
