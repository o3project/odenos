#!/bin/sh +x

RUN_DIR=`pwd`
ODENOS_HOME_DIR=../../../

## please specify the path of trema command
TREMA1="${HOME}/trema-edge/trema"
TREMA2="${HOME}/trema-edge2/trema"
TREMA3="${HOME}/trema-edge3/trema"
TREMA4="${HOME}/trema-edge4/trema"

TREMA1_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr3 --vendor=VENDOR1"
TREMA2_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr4 --vendor=VENDOR2"
TREMA3_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr5 --vendor=VENDOR3"
TREMA4_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=romgr6 --vendor=VENDOR4"

DRV_CONF1=

start() {
  cd $ODENOS_HOME_DIR
  ./odenos start
  sleep 4
  ${TREMA1} run -d -c ./apps/mininet_examples/multi_network_control/driver_dc1.conf "${TREMA1_PARAM}"
  ${TREMA2} run -d -c ./apps/mininet_examples/multi_network_control/driver_dc2.conf "${TREMA2_PARAM}"
  ${TREMA3} run -d -c ./apps/mininet_examples/multi_network_control/driver_dc3.conf "${TREMA3_PARAM}"
  ${TREMA4} run -d -c ./apps/mininet_examples/multi_network_control/driver_wan.conf "${TREMA4_PARAM}"
  cd $RUN_DIR
  PYTHONPATH=$ODENOS_HOME_DIR/lib/python/ ./config_odenos.py
}

stop() {
  ${TREMA1} killall
  ${TREMA2} killall
  ${TREMA3} killall
  ${TREMA4} killall
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
