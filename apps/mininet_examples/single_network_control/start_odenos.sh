#!/bin/sh

RUN_DIR=`pwd`
ODENOS_HOME_DIR=../../../
CONF_FILE="${RUN_DIR}/odenos.conf"
DEBUG="${DEBUG:-OFF}"

start() {
    run cd $ODENOS_HOME_DIR
    run ./odenos start -c "${CONF_FILE}"
    run cd $RUN_DIR
    run eval PYTHONPATH=$ODENOS_HOME_DIR/lib/python/ ./config_odenos.py
}

stop() {
    run cd $ODENOS_HOME_DIR
    run ./odenos stop -c "${CONF_FILE}"
    run cd $RUN_DIR
}

clean() {
    run sudo killall -9 python
    run cd $ODENOS_HOME_DIR
    run rm var/log/*.log
    run cd $RUN_DIR
    run sudo service redis-server restart
}

run() {
    local res
    case "${DEBUG}" in
    ON|on|YES|yes)
	echo "==== $* ====" >&2
	;;
    esac
    "$@"
    res=$?
    case "${DEBUG}" in
    ON|on|YES|yes)
	echo "==== exit=${res} ====" >&2
	;;
    esac
    return ${res}
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
	sleep 2
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
