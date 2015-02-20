#!/bin/sh

## please specify the path of trema command
TREMA="${HOME}/trema-edge/trema"

TREMA_PARAM="src/main/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr3"

start() {
    ./odenos start
    sleep 2
    ${TREMA} run -d "${TREMA_PARAM}"
}

stop() {
    ${TREMA} killall
    ./odenos stop
}

clean() {
    sudo killall -9 python
    rm var/log/*.log
    sudo service redis-server restart
}

case "$1" in
    start)
	start
	;;
    stop)
	stop
	;;
    clean)
	clean
	;;
    restart)
	stop
	start
	;;
    crestart)
	stop
	clean
	start
	;;
    *)
	echo >&2 "$0 [start|stop|clean|restart]"
esac
