#!/bin/sh

RUN_DIR=`pwd`
ODENOS_HOME_DIR=../../../

## please specify the path of trema command
TREMA="${HOME}/trema-edge/trema"

TREMA_PARAM="src/main/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr3"

start() {
  cd $ODENOS_HOME_DIR
  ./odenos start
  sleep 2
  ${TREMA} run -d "${TREMA_PARAM}"
  cd $RUN_DIR
  PYTHONPATH=$ODENOS_HOME_DIR/lib/python/ ./config_odenos.py
}

stop() {
    ${TREMA} killall
    cd $ODENOS_HOME_DIR
    ./odenos stop
    cd $RUN_DIR
}

clean() {
    sudo killall -9 python
    cd $ODENOS_HOME_DIR
    rm var/log/*.log
    cd $RUN_DIR
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
