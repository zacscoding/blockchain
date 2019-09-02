#!/usr/bin/env bash

SCRIPT_PATH=$( cd "$(dirname "$0")" ; pwd -P )

if [[ ! -e "docker-compose.yaml" ]];then
  echo "docker-compose.yaml not found."
  exit 8
fi

function clean(){
  docker-compose  -f docker-compose.yaml down
  lines=`docker images | grep 'dev-peer' | grep 'dev-peer' | wc -l`
  if [[ "$lines" -gt 0 ]]; then
    docker images | grep 'dev-peer' | awk '{print $1}' | xargs docker rmi -f
  fi
  deleteZookeeperAndKafkaMeta
}

function up(){
  deleteZookeeperAndKafkaMeta
  docker-compose up --force-recreate
}

function down(){
  docker-compose down;
}

function stop (){
  docker-compose stop;
}

function start (){
  deleteZookeeperAndKafkaMeta
  docker-compose start;
}

function deleteZookeeperAndKafkaMeta() {
  sudo rm -rf ./temp-zookeeper
  sudo rm -rf ./temp-kafka
}


for opt in "$@"
do
    case "$opt" in
        up)
            up
            ;;
        down)
            down
            ;;
        stop)
            stop
            ;;
        start)
            start
            ;;
        clean)
            clean
            ;;
        restart)
            down
            clean
            up
            ;;

        *)
            echo $"Usage: $0 {up|down|start|stop|clean|restart}"
            exit 1

esac
done
