# 블록체인 사내 가이드를 위한 문서!  

해당 Directory는 사내 이더리움 가이드를 위한 문서로 대부분의 내용은 [코어 이더리움 프로그래밍](https://book.naver.com/bookdb/book_detail.nhn?bid=13496085)   
책의 내용에서 정리.  

## 1. 블록 체인 개요  

[1.blockchain.md](./1.blockchain.md)  
;블록체인 개요 및 P2P에 대한 설명  

## 2. 데이터 계층  

[2.1.account.md](./2.1.account.md)   
; 어카운트 정의 및 필드 설명, 상태 전이, 생성  

[2.2.transaction.md](./2.2.transaction.md)   
; 트랜잭션 필드 설명 및 Web3j를 이용한 서명

[2.3.block.md](./2.3.block.md)   
; 블록 필드 설명(ethash, clique, aura), 동기화  

[3.consensus.md](./3.consensus.md)   
; 합의 엔진(PoW, Clique, Aura) 설명  

[4.1.RLP.md](./4.1.RLP.md)   
; RLP 인코딩 규칙 설명 및 web3j를 이용한 예제  

[4.2.trie.md](./4.2.trie.md)   
; 머클 트리(Merkel tree), 기수 트리(Radix tree), 패트리시아 트리(Patricia tree), 이더리움 머클 패트리시아 트리에 대한 설명  

[4.3.rpc.md](./4.3.rpc.md)   
; web3j를 이용한 기본 RPC 사용법

[5.application.md](./5.application.md)   
; 블록체인의 Finality, Nonce, Node state  

# Reference  

- 코어 이더리움 프로그래밍  
https://book.naver.com/bookdb/book_detail.nhn?bid=13496085  


---  

## TEMPORARY

4. 공통계층
- p2p  
- 데이터 저장  

## TODO  

- 2.3.block.md에서 parity field 설명
- 3.consensus.md에서 parity finality empty step 설명  
