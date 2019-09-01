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
echo "================================================="
echo "## Start fabric cli"
echo "================================================="
docker-compose -f ./cli-compose.yaml up --force-recreate -d
handleResult
echo ">>>> Success"
echo ""

# Move crypto-config
echo "================================================="
echo "## Move crypto config"
echo "================================================="
docker exec -it ${NAME} cp -r /opt/gopath/src/github.com/hyperledger/fabric/msp/crypto-config /etc/hyperledger/fabric/
handleResult
echo ">>>> Success"
echo ""

# Upload configtx.yaml
echo "================================================="
echo "## Upload configtx.yaml"
echo "================================================="
docker cp ${SCRIPT_PATH}/configtx.yaml ${NAME}:/etc/hyperledger/fabric/configtx.yaml
handleResult
echo ">>>> Success"
echo ""


# Generate materials
echo "================================================="
echo "## Generate materials { genesis.block, channel1.tx, peerorg1.block, peerorg2.block}"
echo "================================================="
docker exec -it ${NAME} configtxgen  -profile channel1 -channelID channel1 -outputCreateChannelTx /etc/hyperledger/fabric/channel1.tx
handleResult
docker exec -it ${NAME} configtxgen -profile OrdererGenesis -channelID test-system-channel -outputBlock /etc/hyperledger/fabric/genesis.block
handleResult
docker exec -it ${NAME} configtxgen -profile channel1 -channelID channel1 -asOrg peerorg1 -outputAnchorPeersUpdate /etc/hyperledger/fabric/peerorg1.block
handleResult
docker exec -it ${NAME} configtxgen -profile channel1 -channelID channel1 -asOrg peerorg2 -outputAnchorPeersUpdate /etc/hyperledger/fabric/peerorg2.block
handleResult
echo ">>>> Success"
echo ""

# download materials
echo "================================================="
echo "## Download materials { genesis.block, channel1.tx}"
echo "================================================="
rm -rf ${SCRIPT_PATH}/configtx/genesis.block
docker cp ${NAME}:/etc/hyperledger/fabric/genesis.block ${SCRIPT_PATH}/configtx/genesis.block
echo "> Success to download genesis.block"
handleResult

rm -rf ${SCRIPT_PATH}/channel1.tx
docker cp ${NAME}:/etc/hyperledger/fabric/channel1.tx ${SCRIPT_PATH}/configtx/channel1.tx
handleResult

rm -rf ${SCRIPT_PATH}/peerorg1.block
docker cp ${NAME}:/etc/hyperledger/fabric/peerorg1.block ${SCRIPT_PATH}/configtx/peerorg1.block
handleResult

rm -rf ${SCRIPT_PATH}/peerorg2.block
docker cp ${NAME}:/etc/hyperledger/fabric/peerorg2.block ${SCRIPT_PATH}/configtx/peerorg2.block
handleResult
echo "> Success to download channel1.tx"
echo ">>>> Success"
shutdown


