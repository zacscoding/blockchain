#!/usr/bin/env bash

function handleResult() {
  if [[ ${?} != 0 ]]; then
    echo "Received not zero exit value"
    shutdown
    exit 1
  fi
}

########################################################
# Install chain code
########################################################

echo ">> Try to install chain code to peer1.peerorg1"
docker exec configtxlator /bin/bash -c ' \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/fabric/crypto-config/peerOrganizations/peerorg1.example.com/users/Admin@peerorg1.example.com/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer chaincode install -n exampleCC -v v1 -p github.com/example01
'

echo ">> Try to install chain code to peer1.peerorg2"
docker exec configtxlator /bin/bash -c ' \
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
    export CORE_PEER_LOCALMSPID="peerorg2" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/fabric/crypto-config/peerOrganizations/peerorg2.example.com/users/Admin@peerorg2.example.com/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
    peer chaincode install -n exampleCC -v v1 -p github.com/example01
'

echo ">> Test install state in peer1.peerorg1"
docker exec configtxlator /bin/bash -c ' \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/fabric/crypto-config/peerOrganizations/peerorg1.example.com/users/Admin@peerorg1.example.com/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer chaincode list -C channel1 --installed
'

echo ">> Test install state in peer1.peerorg1"
docker exec configtxlator /bin/bash -c ' \
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
    export CORE_PEER_LOCALMSPID="peerorg2" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/fabric/crypto-config/peerOrganizations/peerorg2.example.com/users/Admin@peerorg2.example.com/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
    peer chaincode list -C channel1 --installed
'

echo ">> Try to instantiate chain code to peer1.peerorg1"
docker exec configtxlator /bin/bash -c ' \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/fabric/crypto-config/peerOrganizations/peerorg1.example.com/users/Admin@peerorg1.example.com/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer chaincode instantiate -v v1 -C channel1 -n exampleCC -o orderer1.ordererorg1.example.com:7050 \
    -c '"'{\"Args\":[\"init\",\"a\",\"100\",\"b\",\"200\"]}'"' -l golang -P '"\"OR('peerorg1.member', 'peerorg2.member')\""'
'
