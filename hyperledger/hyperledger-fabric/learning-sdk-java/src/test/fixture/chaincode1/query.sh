#!/usr/bin/env bash

echo "Query -->"
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer chaincode query -C channel1 -n exampleCC -c '"'{\"Args\":[\"query\",\"b\"]}'"'
'
