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
Shows help:
$ monitor -h

Starts monitoring:
$ monitor resttranslator systemmanager romgr1 network1 ...

Starts monitoring with "default.yaml":
$ monitor

Starts monitoring with additional parameters:
$ monitor -i 10.10.10.10 -p 6379 -w 8888 resttranslator systemmanager romgr1 network1 ...

Then open "http://localhost:8888/index.html" with your browser (firefox is recommended).

You can also force the monitor to output the diagram to your console:
$ monitor -c
```
