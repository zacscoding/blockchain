#!/usr/bin/env bash

BITCOIN_DIR="/home/app/.bitcoin"

function check_running() {
  local bitcoind_pid_file="$BITCOIN_DIR/testnet3/bitcoind.pid"
  if [ -f "$bitcoind_pid_file" ]; then
        if [ -s "$bitcoind_pid_file" ]; then
            local bitcoin_pid=$(cat $bitcoind_pid_file)
            PID=$(ps -p $bitcoin_pid | tail -1 | grep -v grep | grep -v vi | grep -v PID | awk '{print $1}')
        fi
  else
        PID=$(ps -ef | grep bitcoind | grep -v grep | grep -v vi | grep -v PID | awk '{print $2}')
  fi
}
check_running

if [ $PID ]; then
  echo "Already bitcoind running > pid : $PID"
else
  bitcoind -testnet -conf=$BITCOIN_DIR/bitcoin.conf -datadir=$BITCOIN_DIR -daemon
  echo "Success to start bitcoind and will display debug.log"
  sleep 1
  tail -100f $BITCOIN_DIR/testnet3/debug.log
fi