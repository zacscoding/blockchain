# 채널1 Fixture  

> cert 생성  

$ ./cert-gen.sh  

> channel material 생성  

$ ./ch-gen.sh  

> fabric network 구축  

$ ./compose.sh restart  

---  

## Certs

- orderers
    - ordererorg1.example.com
        - orderer1
        - orderer2
    - ordererorg2.example.com
        - orderer1
        - orderer2
- peers
    - peerorg1.example.com
        - peer1
    - peerorg2.example.com
        - peer2


## Channel  

- channel1
    - orderers
        - orderer1.ordererorg1.example.com
        - orderer2.ordererorg1.example.com
        - orderer1.ordererorg2.example.com
        - orderer2.ordererorg2.example.com
        
    - peers
        - peer1.peerorg1.example.com
        - peer2.peerorg2.example.com

