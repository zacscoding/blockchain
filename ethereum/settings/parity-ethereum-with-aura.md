# Parity POA Tutorial  
(https://wiki.parity.io/Demo-PoA-tutorial.html)

## Build  

- dependencies

```
$ curl https://sh.rustup.rs -sSf | sh
$ apt-get install build-essential cmake libudev-dev
```  

- download source & build  

```
# download Parity Ethereum code
$ git clone https://github.com/paritytech/parity-ethereum
$ cd parity-ethereum

# build in release mode
$ cargo build --release --features final
```  


## Configure network  

> $vi demo-spec.json  

```
{
    "name": "DemoPoA",
    "engine": {
        "authorityRound": {
            "params": {
                "stepDuration": "5",
                "validators" : {
                    "list": []
                }
            }
        }
    },
    "params": {
        "gasLimitBoundDivisor": "0x400",
        "maximumExtraDataSize": "0x20",
        "minGasLimit": "0x1388",
        "networkID" : "0x2323"
    },
    "genesis": {
        "seal": {
            "authorityRound": {
                "step": "0x0",
                "signature": "0x0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
            }
        },
        "difficulty": "0x20000",
        "gasLimit": "0x5B8D80"
    },
    "accounts": {
        "0x0000000000000000000000000000000000000001": { "balance": "1", "builtin": { "name": "ecrecover", "pricing": { "linear": { "base": 3000, "word": 0 } } } },
        "0x0000000000000000000000000000000000000002": { "balance": "1", "builtin": { "name": "sha256", "pricing": { "linear": { "base": 60, "word": 12 } } } },
        "0x0000000000000000000000000000000000000003": { "balance": "1", "builtin": { "name": "ripemd160", "pricing": { "linear": { "base": 600, "word": 120 } } } },
        "0x0000000000000000000000000000000000000004": { "balance": "1", "builtin": { "name": "identity", "pricing": { "linear": { "base": 15, "word": 3 } } } }
    }
}
```  

> $vi node0.toml  

```
[parity]
chain = "/home/app/private_test/demo-spec.json"
base_path = "/home/app/private_test/parity0"
keys_path = "/home/app/private_test/keys"

[network]
port = 30300

[rpc]
interface = "all"
port = 8540
apis = ["web3", "eth", "net", "personal", "parity", "parity_set", "traces", "rpc", "parity_accounts"]
cors = ["*"]


[ui]
port = 8180

[websockets]
port = 8450

[misc]
logging = "miner=trace,-lown_tx=trace,rpc=trace,own_tx=trace"
log_file = "/home/app/private_test/logs/node0.log"
```

>  $cp node0.toml node1.toml  
=> change base_path & ports

> $mkdir parity0  

> $mkdir parity1  

> Run parity  

```
$parity --config /home/app/private_test/node0.toml  
```

> Create miners  

```
$curl --data '{"jsonrpc":"2.0","method":"parity_newAccountFromPhrase","params":["node0", "node0"],"id":0}' -H "Content-Type: application/json" -X POST localhost:8540  
{"jsonrpc":"2.0","result":"0x00bd138abd70e2f00903268f3db08f2d25677c9e","id":0}

$curl --data '{"jsonrpc":"2.0","method":"parity_newAccountFromPhrase","params":["node1", "node1"],"id":0}' -H "Content-Type: application/json" -X POST localhost:8540  
{"jsonrpc":"2.0","result":"0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2","id":0}  

$curl --data '{"jsonrpc":"2.0","method":"parity_newAccountFromPhrase","params":["user1", "user1"],"id":0}' -H "Content-Type: application/json" -X POST localhost:8540  
{"jsonrpc":"2.0","result":"0x00d695cd9b0ff4edc8ce55b493aec495b597e235","id":0}  

$curl --data '{"jsonrpc":"2.0","method":"parity_newAccountFromPhrase","params":["user2", "user2"],"id":0}' -H "Content-Type: application/json" -X POST localhost:8540  
{"jsonrpc":"2.0","result":"0x001ca0bb54fcc1d736ccd820f14316dedaafd772","id":0}  
```

> == stop parity ==

> $vi demo-spec.json  

```
{
    "name": "DemoPoA",
    "engine": {
        "authorityRound": {
            "params": {
                "gasLimitBoundDivisor": "0x400",
                "stepDuration": "5",
                "validators" : {
                    "list": [
                        "0x00Bd138aBD70e2F00903268F3Db08f2D25677C9e",
                        "0x00Aa39d30F0D20FF03a22cCfc30B7EfbFca597C2"
                    ]
                }
            }
        }
    },
    "params": {
        "maximumExtraDataSize": "0x20",
        "minGasLimit": "0x1388",
        "networkID" : "0x2323"
    },
    "genesis": {
        "seal": {
            "authorityRound": {
                "step": "0x0",
                "signature": "0x0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
            }
        },
        "difficulty": "0x20000",
        "gasLimit": "0x5B8D80"
    },
    "accounts": {
        "0x0000000000000000000000000000000000000001": { "balance": "1", "builtin": { "name": "ecrecover", "pricing": { "linear": { "base": 3000, "word": 0 } } } },
        "0x0000000000000000000000000000000000000002": { "balance": "1", "builtin": { "name": "sha256", "pricing": { "linear": { "base": 60, "word": 12 } } } },
        "0x0000000000000000000000000000000000000003": { "balance": "1", "builtin": { "name": "ripemd160", "pricing": { "linear": { "base": 600, "word": 120 } } } },
        "0x0000000000000000000000000000000000000004": { "balance": "1", "builtin": { "name": "identity", "pricing": { "linear": { "base": 15, "word": 3 } } } },
        "0x00d695cd9b0ff4edc8ce55b493aec495b597e235": { "balance": "10000000000000000000000" }
    }
}
```

> $vi node.pwds  

```
node0
node1
```


> $vi node0.toml (node1.toml also)    

```
...
[account]
password = ["node.pwds"]
[mining]
engine_signer = "0x00bd138aBD70e2F00903268F3Db08f2D25677C9e"
reseal_on_txs = "none"
```  

> $parity --config /home/app/private_test/node0.toml  

> $parity --config /home/app/private_test/node1.toml

# add node

> Check current node  

```
$curl --data '{"jsonrpc":"2.0","method":"parity_enode","params":[],"id":0}' -H "Content-Type: application/json" -X POST localhost:8540
{"jsonrpc":"2.0","result":"enode://8670caf5aa3042ef52009845730632c900823b3649af9b6bd44c3d4a216be9c701e14b5702932babab64198bdfbaf876df103b75ce4660579cd008341fa78223@192.168.79.128:30300","id":0}
```  

> Add node by using json rpc  

```
 $curl --data '{"jsonrpc":"2.0","method":"parity_addReservedPeer","params":["enode://8670caf5aa3042ef52009845730632c900823b3649af9b6bd44c3d4a216be9c701e14b5702932babab64198bdfbaf876df103b75ce4660579cd008341fa78223@192.168.79.128:30300"],"id":0}' -H "Content-Type: application/json" -X POST localhost:8541
{"jsonrpc":"2.0","result":true,"id":0}
```  

> Add node in config file  

```
in node0.toml  
[network]
port = 30303
bootnodes = [
"enode://8670caf5aa3042ef52009845730632c900823b3649af9b6bd44c3d4a216be9c701e14b5702932babab64198bdfbaf876df103b75ce4660579cd008341fa78223@192.168.79.128:30300",
"enode://4a0c0b945784b4acf2b5d939260e12cfc41c69302c294ea5aba883ff70b6dba360db08af23d09a00ded08444875b0130a9091fea09b05912930cc6c020facf95@192.168.79.128:30301",
"enode://1548cf336d41a90205802af09d00e38bc0caa39bee2e2e8075c805aae1f034cf3d8a5833edf698b0dd6e3cf88c0838c03994cefb14dbc6e346f6a442e08e8313@192.168.79.128:30302"
]
```  

# Send transaction & confirm  

```
$curl --data '{"jsonrpc":"2.0","method":"personal_sendTransaction","params":[{"from":"0x00d695cd9b0ff4edc8ce55b493aec495b597e235","to":"0x001ca0bb54fcc1d736ccd820f14316dedaafd772","value":"0x3B9ACA00"}, "user1"],"id":0}' -H "Content-Type: application/json" -X POST localhost:8540  
{"jsonrpc":"2.0","result":"0xb0aabf415e97169cffff1defa30dcd73fcc86de2374519e145c197ed860e54b9","id":0}

$curl --data '{"jsonrpc":"2.0","method":"eth_getBalance","params":["0x001ca0bb54fcc1d736ccd820f14316dedaafd772", "latest"],"id":1}' -H "Content-Type: application/json" -X POST localhost:8540
{"jsonrpc":"2.0","result":"0x3b9aca00","id":1}
```  
