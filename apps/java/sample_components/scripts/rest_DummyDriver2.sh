#!/bin/sh

###
MYNAME="${0##*/}"
#BUILD_OPTS="-DskipTests"
BUILD_OPTS="-Dmaven.test.skip"
FORMAT="\n%{url_effective}, %{response_code}\n"
ADDRESS=127.0.0.1
BUILD="${BUILD:-OFF}"


###
run()
{
	local res
	echo "--- $( date "+%Y-%m-%d %H:%M:%S" ): $* ---" | \
	  sed -n -e '1h ; 2,$H ; x ; s/\n/\\n/g ; h ; $p' >&2
	"$@"
	res=$?
	echo "--- res=$?" >&2
	return ${res}
}

restart_system()
{
	local res

	case "${BUILD}" in
	ON|on)
		res=0
		if ./odenos status | grep "is running" > /dev/null
		then
			./odenos stop
			sleep 1
		fi
		( mvn ${BUILD_OPTS} install ) || res=$?
		if [ ${res} -ne 0 ] ;then
			return ${res}
		fi
		( cd ./apps/java/sample_components && mvn ${BUILD_OPTS} package ) || res=$?
		if [ ${res} -ne 0 ] ;then
			return ${res}
		fi
		sleep 1
		;;
	esac

	if ./odenos status | grep "is not running" > /dev/null
	then
		./odenos start
		echo "--------------------------------" >&2
		echo "" >&2
	fi
	return 0
}

waiting()
{
	local sec cnt
	sec="$1"
	case "${sec}" in
	1)
		echo -n "waiting ${sec} second: "
		;;
	[2-9]|[1-9][0-9]|[1-9][0-9][0-9])
		echo -n "waiting ${sec} seconds: "
		;;
	0|*)
		return 2
		;;
	esac
	cnt=0
	while [ ${cnt} -lt ${sec} ] ;do
		sleep 1
		cnt=$(( cnt + 1 ))
		echo -n "..${cnt}"
	done
	echo ""
	return 0
}

###
case "$1" in
-b)
	BUILD="ON"
	shift
	;;
esac

restart_system
res=$?
if [ ${res} -ne 0 ] ;then
	exit 1
fi

# create Network Component and DummyDriver2 Component
run curl http://$ADDRESS:10080/systemmanager/component_managers | python -mjson.tool
run curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network2 -X PUT -d '{"type": "Network", "id": "network2", "cm_id": "romgr1"}'
run curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/driver2 -X PUT -d '{"type": "DummyDriver2", "id": "driver2", "cm_id": "romgr1"}'
run curl http://$ADDRESS:10080/systemmanager/components | python -mjson.tool

# connect Components
run curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/conn2 -X PUT -d '{"id": "conn2", "type": "LogicAndNetwork", "connection_type": "original", "logic_id": "driver2", "network_id": "network2"}'
run curl http://$ADDRESS:10080/systemmanager/connections | python -mjson.tool
sleep 1

# create Topology
# --- (0)node01(1) ---- (2)node02(0) ---
#         (2)               (1)
#          |                 |
#          |                (2)
#          +----------- (1)node03(0) ---
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node01 -X PUT -d '{"node_id": "node01", "type": "Node", "ports": {}, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node02 -X PUT -d '{"node_id": "node02", "type": "Node", "ports": {}, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node03 -X PUT -d '{"node_id": "node03", "type": "Node", "ports": {}, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node01/ports/port010 -X PUT -d '{"type": "Port", "node_id": "node01", "port_id": "port010", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node01/ports/port011 -X PUT -d '{"type": "Port", "node_id": "node01", "port_id": "port011", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node01/ports/port012 -X PUT -d '{"type": "Port", "node_id": "node01", "port_id": "port012", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node02/ports/port020 -X PUT -d '{"type": "Port", "node_id": "node02", "port_id": "port020", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node02/ports/port021 -X PUT -d '{"type": "Port", "node_id": "node02", "port_id": "port021", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node02/ports/port022 -X PUT -d '{"type": "Port", "node_id": "node02", "port_id": "port022", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node03/ports/port030 -X PUT -d '{"type": "Port", "node_id": "node03", "port_id": "port030", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node03/ports/port031 -X PUT -d '{"type": "Port", "node_id": "node03", "port_id": "port031", "out_link": null, "in_link": null, "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/nodes/node03/ports/port032 -X PUT -d '{"type": "Port", "node_id": "node03", "port_id": "port032", "out_link": null, "in_link": null, "attributes": {}}'
#run curl http://$ADDRESS:10080/systemmanager/components/network2/topology/nodes -X GET | python -mjson.tool

run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/links/link012 -X PUT -d '{"type": "Link", "link_id": "link012", "src_node": "node01", "src_port": "port011", "dst_node": "node02", "dst_port": "port022", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/links/link021 -X PUT -d '{"type": "Link", "link_id": "link021", "src_node": "node02", "src_port": "port022", "dst_node": "node01", "dst_port": "port011", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/links/link023 -X PUT -d '{"type": "Link", "link_id": "link023", "src_node": "node02", "src_port": "port021", "dst_node": "node03", "dst_port": "port032", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/links/link032 -X PUT -d '{"type": "Link", "link_id": "link032", "src_node": "node03", "src_port": "port032", "dst_node": "node02", "dst_port": "port021", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/links/link031 -X PUT -d '{"type": "Link", "link_id": "link031", "src_node": "node03", "src_port": "port031", "dst_node": "node01", "dst_port": "port012", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/topology/links/link013 -X PUT -d '{"type": "Link", "link_id": "link013", "src_node": "node01", "src_port": "port012", "dst_node": "node03", "dst_port": "port031", "attributes": {}}'
#run curl http://$ADDRESS:10080/systemmanager/components/network2/topology/links -X GET | python -mjson.tool

sleep 1
#run curl http://$ADDRESS:10080/systemmanager/components/network2/topology -X GET | python -mjson.tool

if ps -ef | grep neo4j > /dev/null
then
	PYTHONPATH=lib/python apps/neo4j/neo4jsync.py topology
fi

# set Flows
run curl http://$ADDRESS:10080/systemmanager/components/network2/flows -X GET | python -mjson.tool

# Send and Receive Packets
echo "------ OutPacket  -------"
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out -X POST -d '{"type": "OutPacket", "node": "node01", "ports":["port010"] , "header":{"type": "BasicFlowMatch", "in_node": "node01", "in_port": "port010"}, "data": "ABCDEFGHIJKLM", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000000 -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000000 -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out -X POST -d '{"type": "OutPacket", "node": "node01", "ports":["port010"] , "header":{"type": "BasicFlowMatch", "in_node": "node01", "in_port": "port010"}, "data": "NOPQRSTUBWXYZ", "attributes": {}}'
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000001 -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000001 -X GET
# deleted on DummyDriver2
#run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000000 -X DELETE
# status codes are 404 for following two requests
sleep 1
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000000 -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/out/0000000001 -X GET

echo "------ InPacket -------"
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET
# status code is 404 for a following request
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000000 -X GET
waiting 8
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000000 -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000000 -X GET
# status code is 404 for a following request
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000001 -X GET
waiting 10
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000000 -X DELETE
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000000 -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000001 -X GET
waiting 10
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in/0000000003 -X DELETE
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET
waiting 10
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X DELETE
run curl -w "$FORMAT" http://$ADDRESS:10080/network2/packets/in -X GET

# delete network
sleep 1
run curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/connections/conn2 -X DELETE
run curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/driver2 -X DELETE
run curl -w "$FORMAT" http://$ADDRESS:10080/systemmanager/components/network2 -X DELETE

# EOF
