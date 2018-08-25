## Blockchain Private network 구축  

1. <a href="#install-go-eth">go-ethereum private network</a>
2. <a href="#install-parity">parity private network</a>


<div id="install-go-eth"></div>  

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

[app@localhost bin]$ vi ~/.bash_profile
export GETHPATH="$HOME/go-ethereum/build"  
PATH=$PATH:$GETHPATH/bin
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
[app@localhost clique-test]$ geth --datadir node1/ --ipcpath node1/node1.ipc --syncmode 'full' --port 30311 --rpc --rpcaddr 'localhost' --rpcport 8501 --rpcapi 'personal,db,eth,net,web3,txpool,miner' --bootnodes 'enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@127.0.0.1:30310' --networkid 1234 --gasprice '1' -unlock '0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0' --password node1/password.txt --mine
```

> Node1  


```
[app@localhost clique-test]$ geth --datadir node1/ --ipcpath node1/node1.ipc --syncmode 'full' --port 30311 --rpc --rpcaddr 'localhost' --rpcport 8501 --rpcapi 'personal,db,eth,net,web3,txpool,miner' --bootnodes 'enode://fc8b830dd799aa0ea409bd6735170fedcb6ad323c2fdf82497636bd7eaf398f37e8610b0b1edd136a756a593a732710940c1fa460140f72ee5475f257c7df207@127.0.0.1:30310' --networkid 1234 --gasprice '1' -unlock '0xfa01bfd41c4672b531e0dfbce52d1680a87d5fb0' --password node1/password.txt --mine
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
