### GO-ETHEREUM  

> Summary  

- go 설치
- go-ethereum build
- private network 구축(clique)  

#### Go 설치(Centos7)  
http://snowdeer.github.io/go/2018/01/20/how-to-install-golang-on-centos/  


```
$ wget https://dl.google.com/go/go1.10.linux-amd64.tar.gz  
$ sudo tar -C /usr/local -xvzf go1.10.linux-amd64.tar.gz
$ mkdir -r $HOME/go/bin
$ mkdir -r $HOME/go/bin
$ mkdir -r $HOME/go/bin

$ vi /etc/profile.d/path.sh  
export PATH=$PATH:/usr/local/go/bin

$ vi ~/.bash_profile
export GOBIN="$HOME/go/bin"
export GOPATH="$HOME/go"

$ source /etc/profile
$ source ~/.bash_profile
```  

#### go-ethereum 소스코드 다운로드 (git)  

```
[app@localhost ~]$ pwd
/home/app
[app@localhost ~]$ git clone https://github.com/ethereum/go-ethereum.git
.. download ..
[app@localhost ~]$ cd go-ethereum/
[app@localhost go-ethereum]$ make all
.... install ...
[app@localhost go-ethereum]$ cd build/bin/
[app@localhost bin]$ pwd
/home/app/go-ethereum/build/bin
[app@localhost bin]$ ls
abigen  bootnode  clef  ethkey  evm  examples  faucet  geth  p2psim  puppeth  rlpdump  simulations  swarm  swarm-smoke  wnode

[app@localhost bin]$ vi ~/.bashrc
export GETHPATH="$HOME/go-ethereum/build"  
PATH=$PATH:$GETHPATH/bin
[app@localhost bin]$ source ~/.bashrc
```  

#### 노드 데이터 디렉터리 생성  

```
[app@localhost clique-test]$ mkdir $HOME/clique-test
[app@localhost clique-test]$ cd $HOME/clique-test
[app@localhost clique-test]$ mkdir node1 node2 node3
[app@localhost clique-test]$ ll
합계 0
drwxrwxr-x. 2 app app 6  8월 25 22:57 node1
drwxrwxr-x. 2 app app 6  8월 25 22:57 node2
drwxrwxr-x. 2 app app 6  8월 25 22:57 node3
```  

#### account 생성  

```
[app@localhost clique-test]$ geth --datadir node1/ account new
INFO [08-25|23:05:48.903] Maximum peer count                       ETH=25 LES=0 total=25
Your new account is locked with a password. Please give a password. Do not forget this password.
Passphrase:
Repeat passphrase:
Address: {fa01bfd41c4672b531e0dfbce52d1680a87d5fb0}

[app@localhost clique-test]$ geth --datadir node2/ account new
INFO [08-25|23:05:56.316] Maximum peer count                       ETH=25 LES=0 total=25
Your new account is locked with a password. Please give a password. Do not forget this password.
Passphrase:
Repeat passphrase:
Address: {2d5fde2bae5ee752eed7b0f1a990760c93bd1b27}

[app@localhost clique-test]$ geth --datadir node3/ account new
INFO [08-25|23:06:03.797] Maximum peer count                       ETH=25 LES=0 total=25
Your new account is locked with a password. Please give a password. Do not forget this password.
Passphrase:
Repeat passphrase:
Address: {afda0df71a31f392dae6f07a1b2a911006e05971}

[app@localhost clique-test]$ echo 'pass' > node1/password.txt
[app@localhost clique-test]$ echo 'pass' > node2/password.txt
[app@localhost clique-test]$ echo 'pass' > node3/password.txt
```  

#### genesis 파일 생성  

```
[app@localhost clique-test]$ puppeth

Please specify a network name to administer (no spaces or hyphens, please)
> private

Sweet, you can set this via --network=private next time!

INFO [08-25|23:09:14.572] Administering Ethereum network           name=private
WARN [08-25|23:09:14.572] No previous configurations found         path=/home/app/.puppeth/private

What would you like to do? (default = stats)
 1. Show network stats
 2. Configure new genesis
 3. Track new remote server
 4. Deploy network components
> 2

Which consensus engine to use? (default = clique)
 1. Ethash - proof-of-work
 2. Clique - proof-of-authority
> 2

How many seconds should blocks take? (default = 15)
> 10

Which accounts are allowed to seal? (mandatory at least one)
> 0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0
> 0x2d5fde2bae5ee752eed7b0f1a990760c93bd1b27
> 0xafda0df71a31f392dae6f07a1b2a911006e05971
> 0x

Which accounts should be pre-funded? (advisable at least one)
> 0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0
> 0x2d5fde2bae5ee752eed7b0f1a990760c93bd1b27
> 0xafda0df71a31f392dae6f07a1b2a911006e05971
> 0x

Specify your chain/network ID if you want an explicit one (default = random)
> 1234
INFO [08-25|23:10:08.758] Configured new genesis block

What would you like to do? (default = stats)
 1. Show network stats
 2. Manage existing genesis
 3. Track new remote server
 4. Deploy network components
> 2

 1. Modify existing fork rules
 2. Export genesis configuration
 3. Remove genesis configuration
> 2

Which file to save the genesis into? (default = private.json)
> genesis.json
INFO [08-25|23:10:20.806] Exported existing genesis block

What would you like to do? (default = stats)
 1. Show network stats
 2. Manage existing genesis
 3. Track new remote server
 4. Deploy network components
> ^C

```  

#### 이더리움 노드 초기화  

```
[app@localhost clique-test]$ geth --datadir node1/ init genesis.json
[app@localhost clique-test]$ geth --datadir node2/ init genesis.json
[app@localhost clique-test]$ geth --datadir node3/ init genesis.json
[app@localhost clique-test]$ tree -L 2
.
├── genesis.json
├── node1
│   ├── geth
│   ├── keystore
│   └── password.txt
├── node2
│   ├── geth
│   ├── keystore
│   └── password.txt
└── node3
    ├── geth
    ├── keystore
    └── password.txt
```  

#### Bootnode 생성 및 시작

```
[app@localhost clique-test]$ bootnode -genkey boot.key
[app@localhost clique-test]$ ll
합계 28
-rw-------. 1 app app    64  8월 25 23:15 boot.key
-rw-r--r--. 1 app app 21931  8월 25 23:10 genesis.json
drwxrwxr-x. 4 app app    54  8월 25 23:12 node1
drwxrwxr-x. 4 app app    54  8월 25 23:12 node2
drwxrwxr-x. 4 app app    54  8월 25 23:12 node3  
[app@localhost clique-test]$ bootnode -nodekey boot.key -verbosity 9 -addr :30310
INFO [08-25|23:17:38.078] UDP listener up                          self=enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@[::]:30310
```   

#### 이더리움 노드 시작  

> Node1  


```
[app@localhost clique-test]$ geth --datadir node1/ --ipcpath node1/node1.ipc --syncmode 'full' --port 30311 --rpc --rpcaddr 'localhost' --rpcport 8501 --rpcapi 'personal,db,eth,net,web3,txpool,miner' --bootnodes 'enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@127.0.0.1:30310' --networkid 1234 --gasprice '1' -unlock '0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0' --password node1/password.txt --mine
```

> Node2


```
[app@localhost clique-test]$ geth --datadir node2/ --ipcpath node2/node2.ipc --syncmode 'full' --port 30312 --rpc --rpcaddr 'localhost' --rpcport 8502 --rpcapi 'personal,db,eth,net,web3,txpool,miner' --bootnodes 'enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@127.0.0.1:30310' --networkid 1234 --gasprice '1' -unlock '0x2d5fde2bae5ee752eed7b0f1a990760c93bd1b27' --password node2/password.txt --mine
```

> Node3  


```
[app@localhost clique-test]$ geth --datadir node3/ --ipcpath node1/node1.ipc --syncmode 'full' --port 30313 --rpc --rpcaddr 'localhost' --rpcport 8503 --rpcapi 'personal,db,eth,net,web3,txpool,miner' --bootnodes 'enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@127.0.0.1:30310' --networkid 1234 --gasprice '1' -unlock '0xafda0df71a31f392dae6f07a1b2a911006e05971' --password node3/password.txt --mine
```  


#### dumpconfig 를 이용한 이더리움 노드 시작  

```
[app@localhost clique-test]$ geth --datadir node1/ --ipcpath node1/node1.ipc --syncmode 'full' --port 30311 --rpc --rpcaddr 'localhost' --rpcport 8501 --rpcapi 'personal,db,eth,net,web3,txpool,miner' --bootnodes 'enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@127.0.0.1:30310' --networkid 1234 --gasprice '1' -unlock '0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0' --password node1/password.txt --mine dumpconfig > node1.toml
INFO [08-26|16:47:17.377] Maximum peer count                       ETH=25 LES=0 total=25
[app@localhost clique-test]$ geth --config node1.toml
```  


#### 노드 확인  

```
[app@localhost clique-test]$ geth attach node1/node1.ipc
Welcome to the Geth JavaScript console!

instance: Geth/v1.8.15-unstable-70398d30/linux-amd64/go1.10
coinbase: 0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0
at block: 16 (Sat, 25 Aug 2018 23:33:26 KST)
 datadir: /home/app/clique-test/node1
 modules: admin:1.0 clique:1.0 debug:1.0 eth:1.0 miner:1.0 net:1.0 personal:1.0 rpc:1.0 txpool:1.0 web3:1.0

> eth.blockNumber
16
> admin.peers
[{
    caps: ["eth/62", "eth/63"],
    id: "7a67f6f4452c0329e5bf97bfae117598ec9532803049aa15da576695480823930cae6d5f9f2a71ea141ca68ca7c89fa4130a01fe6d1977389db046e50e94885b",
    name: "Geth/v1.8.15-unstable-70398d30/linux-amd64/go1.10",
    network: {
      inbound: true,
      localAddress: "127.0.0.1:30311",
      remoteAddress: "127.0.0.1:34688",
      static: false,
      trusted: false
    },
    protocols: {
      eth: {
        difficulty: 29,
        head: "0xda44d682047766765e93d010ac03bac31994f8cc579eb57b9f58cf537a2b17f5",
        version: 63
      }
    }
}, {
    caps: ["eth/62", "eth/63"],
    id: "e4423bf5fdf50b78c800dd9b8a7441e04a4b220d4384b6a1623a3ee2fa2daaf95fc2c43b3ecdf8af80ccaf5c206eceb2e9db218ec68806d4d24b57f529310166",
    name: "Geth/v1.8.15-unstable-70398d30/linux-amd64/go1.10",
    network: {
      inbound: true,
      localAddress: "127.0.0.1:30311",
      remoteAddress: "127.0.0.1:34706",
      static: false,
      trusted: false
    },
    protocols: {
      eth: {
        difficulty: 31,
        head: "0xd6f2ea96a87e4561fa22c2035d22ca516cfaf1e89aaf802de2a1c30de078551f",
        version: 63
      }
    }
}]
> eth.blockNumber
29
```

---  

## 윈도우 미스트 실행  


```
dir > & 'C:\Program Files\Mist\Mist.exe' --rpc \\.\pipe\node1/node1.ipc
```  


---  

## Sample of geth  

> Config  

```
[Eth]
NetworkId = 456
SyncMode = "full"
NoPruning = false
LightPeers = 100
DatabaseCache = 768
TrieCache = 256
TrieTimeout = 3600000000000
MinerGasFloor = 8000000
MinerGasCeil = 8000000
MinerGasPrice = 1000000000
MinerRecommit = 3000000000
MinerNoverify = false
EnablePreimageRecording = false

[Eth.Ethash]
CacheDir = "ethash"
CachesInMem = 2
CachesOnDisk = 3
DatasetDir = "/home/app/.ethash"
DatasetsInMem = 1
DatasetsOnDisk = 2
PowMode = 0

[Eth.TxPool]
Locals = []
NoLocals = false
Journal = "transactions.rlp"
Rejournal = 3600000000000
PriceLimit = 1
PriceBump = 10
AccountSlots = 16
GlobalSlots = 4096
AccountQueue = 64
GlobalQueue = 1024
Lifetime = 10800000000000

[Eth.GPO]
Blocks = 20
Percentile = 60

[Shh]
MaxMessageSize = 1048576
MinimumAcceptedPOW = 2e-01
RestrictConnectionBetweenLightClients = true

[Node]
DataDir = "/applications/geth/data"
IPCPath = "geth.ipc"
HTTPHost = "0.0.0.0"
HTTPPort = 9540
HTTPCors = ["*"]
HTTPVirtualHosts = ["*"]
HTTPModules = ["personal", "db", "eth", "net", "web3", "txpool", "miner", "debug", "admin"]
WSHost = "0.0.0.0"
WSPort = 9450
WSModules = ["personal", "db", "eth", "net", "web3", "txpool", "miner", "debug", "admin"]

[Node.P2P]
MaxPeers = 25
NoDiscovery = false
BootstrapNodes = ["enode://a979fb575495b8d6db44f750317d0f4622bf4c2aa3365d6af7c284339968eef29b69ad0dce72a4d8db5ebb4968de0e3bec910127f134779fbcb0cb6d3331163c@52.16.188.185:30303", "enode://3f1d12044546b76342d59d4a05532c14b85aa669704bfe1f864fe079415aa2c02d743e03218e57a33fb94523adb54032871a6c51b2cc5514cb7c7e35b3ed0a99@13.93.211.84:30303", "enode://78de8a0916848093c73790ead81d1928bec737d565119932b98c6b100d944b7a95e94f847f689fc723399d2e31129d182f7ef3863f2b4c820abbf3ab2722344d@191.235.84.50:30303", "enode://158f8aab45f6d19c6cbf4a089c2670541a8da11978a2f90dbf6a502a4a3bab80d288afdbeb7ec0ef6d92de563767f3b1ea9e8e334ca711e9f8e2df5a0385e8e6@13.75.154.138:30303", "enode://1118980bf48b0a3640bdba04e0fe78b1add18e1cd99bf22d53daac1fd9972ad650df52176e7c7d89d1114cfef2bc23a2959aa54998a46afcf7d91809f0855082@52.74.57.123:30303", "enode://979b7fa28feeb35a4741660a16076f1943202cb72b6af70d327f053e248bab9ba81760f39d0701ef1d8f89cc1fbd2cacba0710a12cd5314d5e0c9021aa3637f9@5.1.83.226:30303"]
BootstrapNodesV5 = ["enode://06051a5573c81934c9554ef2898eb13b33a34b94cf36b202b69fde139ca17a85051979867720d4bdae4323d4943ddf9aeeb6643633aa656e0be843659795007a@35.177.226.168:30303", "enode://0cc5f5ffb5d9098c8b8c62325f3797f56509bff942704687b6530992ac706e2cb946b90a34f1f19548cd3c7baccbcaea354531e5983c7d1bc0dee16ce4b6440b@40.118.3.223:30304", "enode://1c7a64d76c0334b0418c004af2f67c50e36a3be60b5e4790bdac0439d21603469a85fad36f2473c9a80eb043ae60936df905fa28f1ff614c3e5dc34f15dcd2dc@40.118.3.223:30306", "enode://85c85d7143ae8bb96924f2b54f1b3e70d8c4d367af305325d30a61385a432f247d2c75c45c6b4a60335060d072d7f5b35dd1d4c45f76941f62a4f83b6e75daaf@40.118.3.223:30307"]
#StaticNodes = []
TrustedNodes = []
ListenAddr = ":30300"
EnableMsgEvents = false

[Node.HTTPTimeouts]
ReadTimeout = 30000000000
WriteTimeout = 30000000000
IdleTimeout = 120000000000

[Dashboard]
Host = "localhost"
Port = 8080
Refresh = 5000000000
```  

> Clique sample  

```
{
  "config": {
    "chainId": 456,
    "homesteadBlock": 1,
    "eip150Block": 2,
    "eip150Hash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "eip155Block": 3,
    "eip158Block": 3,
    "byzantiumBlock": 4,
    "clique": {
      "period": 5,
      "epoch": 30000
    }
  },
  "nonce": "0x0",
  "timestamp": "0x5bd90171",
  "extraData": "0x000000000000000000000000000000000000000000000000000000000000000035b1cf8b9c9499f3af7fc0167223c719aec1bdc87d0e60a4f643552fcb871a7a0b604e5dde8bda96a06208a7bb1e461c79546ab60fa07a39b296e1c9aa4d9e92fe6fbf84e9e8003536b6ee7b42072ac6db903d484a4baca1e7f9111c7e1749e462106c160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "gasLimit": "0x47b760",
  "difficulty": "0x1",
  "mixHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
  "coinbase": "0x0000000000000000000000000000000000000000",
  "alloc": {
    ...
  },
  "number": "0x0",
  "gasUsed": "0x0",
  "parentHash": "0x0000000000000000000000000000000000000000000000000000000000000000"
}
```  

> start.sh  

```
#!/bin/sh
nohup ./geth --config node.toml --gasprice '1' -etherbase '0xaa4d9e92fe6fbf84e9e8003536b6ee7b42072ac6' -unlock '0xaa4d9e92fe6fbf84e9e8003536b6ee7b42072ac6' --password /applications/geth/password.txt --mine  >> /applications/geth/node.log &

# without hitting enter
# nohup $SCRIPTPATH/geth --config $SCRIPTPATH/node.toml --gasprice '1' -etherbase '0xaa4d9e92fe6fbf84e9e8003536b6ee7b42072ac6' -unlock '0xaa4d9e92fe6fbf84e9e8003536b6ee7b42072ac6' --password /applications/geth/password.txt --mine  1>> /applications/geth/node.log 2>&1 &
```  

> stop.sh  

```
#!/bin/bash
echo "Before"
ps -ef | grep geth

ps -ef | grep geth | grep -v grep | awk '{print $2}' | xargs kill -9 $2

echo ""
echo "After"
ps -ef | grep geth
```
