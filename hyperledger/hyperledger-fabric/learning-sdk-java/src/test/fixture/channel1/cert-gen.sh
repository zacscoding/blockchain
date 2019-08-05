#!/usr/bin/env bash

NAME=fabric-cli-temp
SCRIPT_PATH=$( cd "$(dirname "$0")" ; pwd -P )
FABRIC_PATH=/etc/hyperledger/fabric

function shutdown() {
  docker-compose -f ./cli-compose.yaml down
}

function handleResult() {
  if [[ ${?} != 0 ]]; then
    echo "Received not zero exit value"
    shutdown
    exit 1
  fi
}

# Start cli
echo "##### Start fabric cli"
docker-compose -f ./cli-compose.yaml up --force-recreate -d
handleResult
echo ">>>> Success"

# Move materials
echo "##### Move crypto-config"
docker exec -t ${NAME} cp -r /opt/gopath/src/github.com/hyperledger/fabric/msp/crypto-config ${FABRIC_PATH}/
handleResult
echo ">>>> Success"

# Generate certs
echo "##### Generate certs from crypto-config.yaml"
docker exec -t ${NAME} cryptogen generate --config=${FABRIC_PATH}/crypto-config/crypto-config.yaml
handleResult
echo ">>>> Success"

# Download certs
echo "##### Download certs"
sudo rm -rf ${SCRIPT_PATH}/crypto-config/ordererOrganizations
sudo rm -rf ${SCRIPT_PATH}/crypto-config/peerOrganizations

docker cp ${NAME}:/crypto-config/ordererOrganizations ${SCRIPT_PATH}/crypto-config/ordererOrganizations
docker cp ${NAME}:/crypto-config/peerOrganizations ${SCRIPT_PATH}/crypto-config/peerOrganizations
handleResult
echo ">>>> Success"

shutdown
