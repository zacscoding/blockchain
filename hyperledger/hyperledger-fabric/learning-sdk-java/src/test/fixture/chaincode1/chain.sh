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

echo "Try to install chain code to peer1.peerorg1"
#docker exec peer1.peerorg1.example.com /bin/bash -c ' \
#    export GOPATH=/opt/gopath && \
#    export CORE_PEER_LOCALMSPID="peerorg1" && \
#    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
#    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
#    peer chaincode install -n exampleCC -v 1.0 -p github.com/example01
#'
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer chaincode install -n exampleCC -v 1.0 -l golang -p chaincode/go/example01
'

echo "Try to install chain code to peer1.peerorg2"
#docker exec peer1.peerorg2.example.com /bin/bash -c ' \
#    export GOPATH=/opt/gopath && \
#    export CORE_PEER_LOCALMSPID="peerorg2" && \
#    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
#    export CORE_PEER_ADDRESS=peer1.peerorg2.example.com:8051 && \
#    peer chaincode install -n exampleCC -v 1.0 -p github.com/example01
#'
