## Bitcoin json rpc

- <a href="#infos">Infos </a>
- <a href="#wallet">Wallet(코인 받고 테스트 다시)</a>
- <a href="#tx">Transaction</a>
- <a href="#block">Block</a>

<div id="infos"></div>  

### Infos  

> getblockchaininfo

```
app@ubuntu:~$ bitcoin-cli getblockchaininfo
{
  "chain": "test",
  "blocks": 1412459,
  "headers": 1412459,
  "bestblockhash": "0000000000000044263492b91368e6ac1260def49d10d2c698edf133d480403d",
  "difficulty": 57453818.69755466,
  "mediantime": 1536188595,
  "verificationprogress": 0.9999997180442964,
  "initialblockdownload": false,
  "chainwork": "0000000000000000000000000000000000000000000000bc2227858ea9a6e54d",
  "size_on_disk": 21963531847,
  "pruned": false,
  "softforks": [
    {
      "id": "bip34",
      "version": 2,
      "reject": {
        "status": true
      }
    },
    {
      "id": "bip66",
      "version": 3,
      "reject": {
        "status": true
      }
    },
    {
      "id": "bip65",
      "version": 4,
      "reject": {
        "status": true
      }
    }
  ],
  "bip9_softforks": {
    "csv": {
      "status": "active",
      "startTime": 1456790400,
      "timeout": 1493596800,
      "since": 770112
    },
    "segwit": {
      "status": "active",
      "startTime": 1462060800,
      "timeout": 1493596800,
      "since": 834624
    }
  },
  "warnings": "Warning: unknown new rules activated (versionbit 28)"
}
```  

> getnetworkinfo  


```
app@ubuntu:~$ bitcoin-cli getnetworkinfo
{
  "version": 160200,
  "subversion": "/Satoshi:0.16.2/",
  "protocolversion": 70015,
  "localservices": "000000000000040d",
  "localrelay": true,
  "timeoffset": 0,
  "networkactive": true,
  "connections": 8,
  "networks": [
    {
      "name": "ipv4",
      "limited": false,
      "reachable": true,
      "proxy": "",
      "proxy_randomize_credentials": false
    },
    {
      "name": "ipv6",
      "limited": false,
      "reachable": true,
      "proxy": "",
      "proxy_randomize_credentials": false
    },
    {
      "name": "onion",
      "limited": true,
      "reachable": false,
      "proxy": "",
      "proxy_randomize_credentials": false
    }
  ],
  "relayfee": 0.00001000,
  "incrementalfee": 0.00001000,
  "localaddresses": [
  ],
  "warnings": "Warning: unknown new rules activated (versionbit 28)"
}
```  

> getwalletinfo

```
app@ubuntu:~$ bitcoin-cli getwalletinfo
{
  "walletname": "wallet.dat",
  "walletversion": 159900,
  "balance": 0.00000000,
  "unconfirmed_balance": 0.00000000,
  "immature_balance": 0.00000000,
  "txcount": 0,
  "keypoololdest": 1536024293,
  "keypoolsize": 1000,
  "keypoolsize_hd_internal": 1000,
  "paytxfee": 0.00000000,
  "hdmasterkeyid": "a4dedd27bf392da6f2d5c1e9aea7d5a36d7abcdb"
}
```  


<div id="wallet"></div>    

### 지갑 설정 암호화  
; encryptwallet , walletpassphrase  

> encryptwallet  

```
app@ubuntu:~$ bitcoin-cli encryptwallet passphrase
wallet encrypted; Bitcoin server stopping, restart to run with encrypted wallet. The keypool has been flushed and a new HD seed was generated (if you are using HD). You need to make a new backup.
```  

> walletpassphrase  
(180 seconds)

```
app@ubuntu:~$ bitcoin-cli walletpassphrase passphrase 180
```  

### 지갑 백업하기, 일반 텍스트 덤프하기, 복원하기  
;backupwallet, importwallet, dumpwallet  

> backupwallet

```
app@ubuntu:~$ bitcoin-cli backupwallet wallet.backup
app@ubuntu:~$ ll
total 1056
drwxr-xr-x  6 app  app     4096 Sep  5 17:50 ./
drwxr-xr-x  3 root root    4096 Sep  3 16:51 ../
-rw-------  1 app  app    15291 Sep  5 07:29 .bash_history
-rw-r--r--  1 app  app      220 Sep  3 16:51 .bash_logout
-rw-r--r--  1 app  app     3771 Sep  3 16:51 .bashrc
drwxrwxr-x 13 app  app     4096 Sep  3 17:58 bitcoin/
drwxrwxr-x  6 app  app     4096 Sep  5 04:27 .bitcoin/
drwxrwxr-x  3 app  app     4096 Sep  3 17:14 bitcoin-tar/
drwx------  2 app  app     4096 Sep  3 16:54 .cache/
-rw-r--r--  1 app  app      655 Sep  3 16:51 .profile
-rw-r--r--  1 app  app        0 Sep  3 16:56 .sudo_as_admin_successful
-rw-------  1 app  app     5489 Sep  5 04:27 .viminfo
-rw-------  1 app  app  1019904 Sep  5 17:50 wallet.backup
```  

> importwallet

```
app@ubuntu:~$ bitcoin-cli walletpassphrase test 180
app@ubuntu:~$ bitcoin-cli importwallet wallet.backup
```  

> dumpwallet  

```
app@ubuntu:~$ bitcoin-cli dumpwallet wallet.txt
{
  "filename": "/home/app/wallet.txt"
}
```

### 지갑 주소 생성하기 & 거래 수신하기  
; getnewaddress , getreceivedbyaddress

> getnewaddress

```
app@ubuntu:~$ bitcoin-cli getnewaddress
2N3RRyFEuMsE3RSXew4bGprXiNutGKcsiYE
```  

> getreceivedbyaddress  
0 컨펌(기본 minconf 설정 값)

```
app@ubuntu:~$ bitcoin-cli getreceivedbyaddress 2N3RRyFEuMsE3RSXew4bGprXiNutGKcsiYE 0
0.00000000
```  

---  

<div id="tx"></div>    

### 거래내역 살펴 보기 & 디코딩  
; gettransaction

> gettransaction

```
app@ubuntu:~$ bitcoin-cli gettransaction txid
```  

> getrawtransaction  

```
app@ubuntu:~$ bitcoin-cli getrawtransaction c72016ac750c631c43e14f3778313e88ecef53a0273e845212396ec82716d4f2
0100000001de4d9354f3a88305066acce67d079b3d5aa1e52155f92a368d8ca49033a4b32d000000006a47304402204244161b25f61f6db616d02019d74f26e731bf314fab46d6b6747ce59ad5f98102206eb6ce22ed2766c7e5e59618167d7d9167462ff9d3ade28e9c7f81633dc718d9012103c51d6bb891a15a64b423ffab949e78e7256628d8e0f8b4cec45f1d180b91c262ffffffff021b7af902000000001976a9149ae41817c8fc190eb45187143b5ece34fb647a6788ac0000000000000000536a4c500002584d00014458305688b164abb84a2374a08ebdb76488feec5b753f1a4d718d74fc9c8ea8a4965972e8b1c433af459dfc17455b907e43060846ccfc3b3a39e5aaf1f399695acf909b8c9ebb9a8bd800000000
```  

> decoderawtransaction

```
app@ubuntu:~$ bitcoin-cli decoderawtransaction 0100000001de4d9354f3a88305066acce67d079b3d5aa1e52155f92a368d8ca49033a4b32d000000006a47304402204244161b25f61f6db616d02019d74f26e731bf314fab46d6b6747ce59ad5f98102206eb6ce22ed2766c7e5e59618167d7d9167462ff9d3ade28e9c7f81633dc718d9012103c51d6bb891a15a64b423ffab949e78e7256628d8e0f8b4cec45f1d180b91c262ffffffff021b7af902000000001976a9149ae41817c8fc190eb45187143b5ece34fb647a6788ac0000000000000000536a4c500002584d00014458305688b164abb84a2374a08ebdb76488feec5b753f1a4d718d74fc9c8ea8a4965972e8b1c433af459dfc17455b907e43060846ccfc3b3a39e5aaf1f399695acf909b8c9ebb9a8bd800000000
{
  "txid": "c72016ac750c631c43e14f3778313e88ecef53a0273e845212396ec82716d4f2",
  "hash": "c72016ac750c631c43e14f3778313e88ecef53a0273e845212396ec82716d4f2",
  "version": 1,
  "size": 283,
  "vsize": 283,
  "locktime": 0,
  "vin": [
    {
      "txid": "2db3a43390a48c8d362af95521e5a15a3d9b077de6cc6a060583a8f354934dde",
      "vout": 0,
      "scriptSig": {
        "asm": "304402204244161b25f61f6db616d02019d74f26e731bf314fab46d6b6747ce59ad5f98102206eb6ce22ed2766c7e5e59618167d7d9167462ff9d3ade28e9c7f81633dc718d9[ALL] 03c51d6bb891a15a64b423ffab949e78e7256628d8e0f8b4cec45f1d180b91c262",
        "hex": "47304402204244161b25f61f6db616d02019d74f26e731bf314fab46d6b6747ce59ad5f98102206eb6ce22ed2766c7e5e59618167d7d9167462ff9d3ade28e9c7f81633dc718d9012103c51d6bb891a15a64b423ffab949e78e7256628d8e0f8b4cec45f1d180b91c262"
      },
      "sequence": 4294967295
    }
  ],
  "vout": [
    {
      "value": 0.49904155,
      "n": 0,
      "scriptPubKey": {
        "asm": "OP_DUP OP_HASH160 9ae41817c8fc190eb45187143b5ece34fb647a67 OP_EQUALVERIFY OP_CHECKSIG",
        "hex": "76a9149ae41817c8fc190eb45187143b5ece34fb647a6788ac",
        "reqSigs": 1,
        "type": "pubkeyhash",
        "addresses": [
          "mudwbrQmm9UBmdKC78P2sUJtAtoUdM8WYE"
        ]
      }
    },
    {
      "value": 0.00000000,
      "n": 1,
      "scriptPubKey": {
        "asm": "OP_RETURN 0002584d00014458305688b164abb84a2374a08ebdb76488feec5b753f1a4d718d74fc9c8ea8a4965972e8b1c433af459dfc17455b907e43060846ccfc3b3a39e5aaf1f399695acf909b8c9ebb9a8bd8",
        "hex": "6a4c500002584d00014458305688b164abb84a2374a08ebdb76488feec5b753f1a4d718d74fc9c8ea8a4965972e8b1c433af459dfc17455b907e43060846ccfc3b3a39e5aaf1f399695acf909b8c9ebb9a8bd8",
        "type": "nulldata"
      }
    }
  ]
}
```

---  

<div id="block"></div>    

###  블록 살펴보기  
;getblock


> getblock  

```
$ bitcoin-cli getblock 0000000000029d36613abea66c6a7c20544442aeb93bca292163c109dddcf231
{
  "hash": "0000000000029d36613abea66c6a7c20544442aeb93bca292163c109dddcf231",
  "confirmations": 2,
  "strippedsize": 998055,
  "size": 998530,
  "weight": 3992695,
  "height": 1412470,
  "version": 536870912,
  "versionHex": "20000000",
  "merkleroot": "3e46162cd8e4144ca8bdf720189bd882657b76037e605b46e5247820aa25676d",
  "tx": [
    "aa01a2d2f49c233a333bc4c1fd35092993d6499d6352421a9a48d0f6c2e3e8f9",
    "83e93cba941033364f5f752296c50a99f4ae9a7d21299a22c3d83105ca313c22",
  ],
  "time": 1536205469,
  "mediantime": 1536200957,
  "nonce": 1851081728,
  "bits": "1d00ffff",
  "difficulty": 1,
  "chainwork": "0000000000000000000000000000000000000000000000bc2c6d96ccd29de1f7",
  "nTx": 3522,
  "previousblockhash": "00000000000000225f07a347b1fe9db5434c7eab2815e66a68300bb2708905f1",
  "nextblockhash": "000000005637c0fedd9201916e3d44301314dff8b2bb87ec1af9231f3dfba4d1"
}                                                                           
```

























---  

<br /><br /><br />
