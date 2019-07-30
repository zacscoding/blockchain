#!/usr/bin/env bash

############################################
# - cli-compose.yaml 로 fabric cli 컨테이너 실행
# crypto-config.yaml 기반으로 cert 생성
# crypto-config/ 하위 디렉터리로 다운로드
############################################

NAME=fabric-cli-temp
SCRIPT_PATH=$( cd "$(dirname "$0")" ; pwd -P )

function shutdown() {
  docker-compose -f ./cli-compose.yaml down
}

function handleResult() {
  if [[ ${?} != 0 ]]; then
    echo "Returned not zero exit value"
    shutdown
    exit 1
  fi
}

echo "##### Start fabric cli"
docker-compose -f ./cli-compose.yaml up -d
handleResult

echo "##### re create dir : ${SCRIPT_PATH}/crypto-config"
sudo rm -rf ${SCRIPT_PATH}/crypto-config

echo "##### upload crypto-config.yaml"
docker cp ./crypto-config.yaml ${NAME}:/etc/hyperledger/fabric/crypto-config.yaml
handleResult

echo "##### create certs"
docker exec -it ${NAME} cryptogen generate --config=/etc/hyperledger/fabric/crypto-config.yaml

echo "##### download certs"
docker cp ${NAME}:/crypto-config ./

sudo chown -R ${USER}:${USER} ./crypto-config
shutdown
