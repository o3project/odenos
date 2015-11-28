#!/bin/sh +x

RUN_DIR=`pwd`
ODENOS_HOME_DIR="../../../"

## please specify the path of trema command
TREMA1="${HOME}/trema-edge/trema"
TREMA2="${HOME}/trema-edge2/trema"
TREMA3="${HOME}/trema-edge3/trema"
TREMA4="${HOME}/trema-edge4/trema"

TREMA1_ROMGR="romgr3"
TREMA2_ROMGR="romgr4"
TREMA3_ROMGR="romgr5"
TREMA4_ROMGR="romgr6"

TREMA1_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=${TREMA1_ROMGR} --vendor=VENDOR1"
TREMA2_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=${TREMA2_ROMGR} --vendor=VENDOR2"
TREMA3_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=${TREMA3_ROMGR} --vendor=VENDOR3"
TREMA4_PARAM="lib/ruby/org/o3project/odenos/core/odenos.rb --cmpmgr=${TREMA4_ROMGR} --vendor=VENDOR4"

DRV_CONF1=

MAX_WAIT_COUNT=30
DEBUG="${DEBUG:-OFF}"
REST_HOST=localhost
REST_PORT=10080

start() {
  local res
  cd $ODENOS_HOME_DIR
  ./odenos start
  sleep 1
  ${TREMA1} run -d -c ./apps/mininet_examples/multi_network_control/driver_dc1.conf "${TREMA1_PARAM}"
  ${TREMA2} run -d -c ./apps/mininet_examples/multi_network_control/driver_dc2.conf "${TREMA2_PARAM}"
  ${TREMA3} run -d -c ./apps/mininet_examples/multi_network_control/driver_dc3.conf "${TREMA3_PARAM}"
  ${TREMA4} run -d -c ./apps/mininet_examples/multi_network_control/driver_wan.conf "${TREMA4_PARAM}"
  check_start_process ${TREMA1_ROMGR} ${TREMA2_ROMGR} ${TREMA3_ROMGR} ${TREMA4_ROMGR}
  res=$?
  if [ ${res} -ne 0 ] ;then
    return 1
  fi
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
  killall -9 python
  cd $ODENOS_HOME_DIR
  rm var/log/*.log
  rm -r var/zookeeper
  cd $RUN_DIR
  sudo service redis-server restart
}

# following function is same as one in odenos
check_start_process()
{
    local ret_val proc_list proc msg reply_code cnt

    proc_list=" $* "
    cnt=0
    while [ -n "${proc_list% }" -a ${cnt} -lt ${MAX_WAIT_COUNT} ] ;do
	cnt=$(( cnt + 1 ))
	ret_val=0
	for proc in ${proc_list} ;do
	    msg="$( curl http://${REST_HOST}:${REST_PORT}/systemmanager/component_managers/${proc} -w "\n%{response_code}\n" 2>/dev/null )"
	    reply_code="$( echo "${msg}" | grep '^[0-5][0-9][0-9]$' )"
	    msg="${msg%${reply_code}}" ; msg="$( echo "${msg}" )"
	    case "${DEBUG}-${reply_code}" in
	    *-200)
		;;
	    ON-*|on-*|YES-*|yes-*)
		echo "DEBUG: ${proc}: reply=${reply_code}, msg=<${msg%${reply_code}}>, cnt=${cnt}" >&2
		;;
	    esac
	    case "${reply_code}" in
	    200)
		echo "Started Compnent Manager :: ${proc}" >&2
		proc_list="${proc_list% ${proc} *} ${proc_list#* ${proc} }"
		;;
	    000|404)	# 000 is not connected
		ret_val=10
		;;
	    *)
		echo "Failed Compnent Manager :: ${proc}: ${reply_code}" >&2
		ret_val=1
		break
		;;
	    esac
	done
	if [ ${ret_val} -eq 10 ] ;then
	    #echo "Starting ODENOS Please wait ...${proc_list}" >&2
	    sleep 1
	elif [ ${ret_val} -ne 0 ] ;then
	    break
	fi
    done

    if [ ${ret_val} -eq 10 ] ;then
	echo "Met Timeout Starting ODENOS" >&2
    fi
    return ${ret_val}
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
	;;
esac
