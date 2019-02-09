#!/bin/bash

commands=("docker-compose up mysqldb" "docker-compose up consul" "docker-compose up vault" "docker-compose up zipkin" "docker-compose up zookeeper" "docker-compose up kafka" "docker-compose up mongodb")

for i in "${commands[@]}"
do
    WID=$(xprop -root | grep "_NET_ACTIVE_WINDOW(WINDOW)"| awk '{print $5}')
    xdotool windowfocus $WID
    xdotool key ctrl+shift+t
    wmctrl -i -a $WID
    sleep 1; xdotool type --delay 1 --clearmodifiers "${i}"; xdotool key Return;
    sleep 5;
done

cd vault_and_consul
./unseal.sh
cd ..
