#!/usr/bin/env bash


echo "## Invoke transaction.."
docker exec peer1.peerorg1.example.com /bin/bash -c ' \
    export CORE_PEER_LOCALMSPID="peerorg1" && \
    export CORE_PEER_MSPCONFIGPATH="/etc/hyperledger/admin/msp" && \
    export CORE_PEER_ADDRESS=peer1.peerorg1.example.com:7051 && \
    peer chaincode invoke -C channel1 -n exampleCC -c '"'{\"Args\":[\"invoke\",\"a\", \"b\", \"1\"]}'"'
'
