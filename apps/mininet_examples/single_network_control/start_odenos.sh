#!/bin/sh

RUN_DIR=`pwd`
ODENOS_HOME_DIR=../../../
CONF_FILE="${RUN_DIR}/odenos.conf"
DEBUG="${DEBUG:-OFF}"

## please specify the path of trema command
export TREMA="${HOME}/trema-edge/trema"

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
    run killall -9 python
    run cd $ODENOS_HOME_DIR
    [ "$( echo var/log/*.log )" = "var/log/*.log" ] || run rm var/log/*.log
    [ ! -d var/zookeeper ] || run rm -r var/zookeeper
    run cd "${TREMA%/*}"
    [ "$( echo tmp/* )" = "tmp/*" ] || run rm -r tmp/*
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
