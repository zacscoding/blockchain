#!/usr/bin/env bash

SCRIPT_PATH=$( cd "$(dirname "$0")" ; pwd -P )

function handleResult() {
  if [[ ${?} != 0 ]]; then
    echo "Received not zero exit value"
    exit 1
  fi
}

########################################################
# Create channel to orderer
########################################################
echo "Try to create channel to orderer"
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer channel create -o orderer1.ordererorg1.example.com:7050 -c channel1 -f /etc/hyperledger/fabric/channel1.tx
'

########################################################
# Fetch channel block & join in peerorg1
########################################################
echo "Try to fetch channel block in peer1.peerorg1.example.com"
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer channel fetch config -c channel1 -o orderer1.ordererorg1.example.com:7050
'

# Join channel to peers
echo "Try to join channel to peer1.peerorg1.example.com"
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer channel join -b /opt/gopath/src/github.com/hyperledger/fabric/peer/channel1_config.block
'

########################################################
# Fetch channel block & join in peerorg2
########################################################
echo "Try to fetch channel block in peer1.peerorg2.example.com"
docker exec peer1.peerorg2.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg2" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" &&\
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
    peer channel fetch config -c channel1 -o orderer1.ordererorg1.example.com:7050
'

# Join channel to peers
echo "Try to join channel to peer1.peerorg2.example.com"
docker exec peer1.peerorg2.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg2" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" \
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051
    peer channel join -b /opt/gopath/src/github.com/hyperledger/fabric/peer/channel1_config.block
'

########################################################
# Update anchor peer in peerorg1
########################################################
echo "Try to update anchor peer in peerorg1"
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer channel update -o orderer1.ordererorg1.example.com:7050 -c channel1 -f /etc/hyperledger/fabric/peerorg1.block
'

########################################################
# Update anchor peer in peerorg2
########################################################
echo "Try to update anchor peer in peerorg2"
docker exec peer1.peerorg2.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg2" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
    peer channel update -o orderer1.ordererorg1.example.com:7050 -c channel1 -f /etc/hyperledger/fabric/peerorg2.block
'


