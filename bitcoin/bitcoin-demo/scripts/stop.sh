#!/usr/bin/env bash

bitcoin-cli -testnet stop
sleep 1
echo "After command, check pid : $(ps -ef | grep bitcoind | grep -v grep | grep -v vi | grep -v PID | awk '{print $2}')"