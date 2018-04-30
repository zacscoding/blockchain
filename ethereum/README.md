# 이더리움 환경 구축  

### index  

- <a href="#geth">Geth</a>
- <a href="https://github.com/zacscoding/blockchain/blob/master/etherieum/03.스마트_컨트랙트_작성.md">스마트 컨트랙트</a>

<div id="geth"></div>

# Geth

> 계정 생성

```
geth account new
```  

> 계정 확인  

```
geth account list
```

> 채굴  

```
geth --mine --minerthreads 16 --etherbase '' --unlock ''
```  

- minerthreads 해시를 계산 할 총 스레드의 개수  
- etherbase 채굴을 통한 보상이 지급 될 주소  

---





<br /><br /><br /><br /><br /><br /><br /><br /><br />

---  
