#!/bin/bash

if [ -z "$1" ]; then
    echo ""
    echo "--- message sequence diagram ---"
    echo ""
    echo 'IMPORTANT: uncomment "manager" in odenos.conf before starting OdenOS.'
    echo ""
    echo "Usage:"
    echo "$ ./monitor.sh <test case>"
    echo "Example:  ./monitor.sh  federated_network"
    echo ""
    echo "Or, you may start a WebSocket server to show the diagram on your firefox browser."
    echo "$ ./monitor.sh <test case> w"
    echo "Example:  ./monitor.sh  federated_network w"
    echo ""
    echo 'Add or edit files in "monitor_preset" folder'
    exit 1
fi   

PRESET=monitor_preset/$1.yaml

if [ -z "$2" ]; then
    ../monitor/monitor -f $PRESET -c
else
    ../monitor/monitor -f $PRESET 
fi
   

