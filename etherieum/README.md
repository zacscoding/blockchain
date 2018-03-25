# 이더리움 환경 구축  

### index  

- <a href="#geth">Geth</a>
- <a href="#smart-contract">스마트 컨트랙트</a>


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

<div id="smart-contract-structure"></div>  

# 스마트 컨트렉트  

- <a href=""> </a>

<div id="스마트_컨트렉트_구조"></div>

## 스마트 컨트렉트 구조  
; 컨트랙트 == 클래스와 비슷  
상태 변수, 함수, 함수 변경자(function modifier), 이벤트, 구조체, 열거형을 포함  
상속은 컴파일 시점에 코드를 복사하는 방식으로 구현 + 다형성 지원  

> Sample.sol  

```
contract Sample
{
    // 상태 변수
    uint256 data;
    address owner;

    // 이벤트 정의
    event logData(uint256 dataToLog);

    // 함수 변경자
    modifier onlyOwner () {
        if (msg.sender != owner) throws;
        _;
    }

    // 생성자
    function Sample(uint256 initData, address initOwner) {
        data = initData;
        owner = initOwner;
    }

    // 함수
    function getData() returns (uint256 returnedData) {
        return data;
    }

    function setData(uint256 newData) onlyOwner {
        logData(newData);
        data = newData;
    }
}
```  

- contract 키워드  
=> 컨트랙트 선언  
- 2개의 상태 변수 선언.  
=> data는 데이터를 저장, owner는 소유자의 이더리움 지갑 주소 즉 컨트랙트가 배포된 주소를 저장  
- 이벤트 정의.  
=> 이벤트는 클라이언트에 무언가를 알려주기 위해 사용됨 data가 변경될 때마다  
이 이벤트가 트리거됨. 모든 이벤트는 블록체인 내에 보관  
- 함수 변경자 정의  
=> 변경자는 함수 실행 전에 자동으로 조건을 검사하기 위해 사용  
위의 변경자는 소유자가 함수를 호출했는 지 검사하고 아니면 예외를 전가  
- 컨트랙트 생성자  
=> 컨트랙트를 배포하는 동안 생성자가 호출. 생성자는 상태 변수를 초기화 하기 위해 사용  
- 2개의 메소드 정의  
=> data 상태 변수의 값을 얻기 위한 메소드, data 값을 변경하기 위한 메소드  

## 데이터 위치  
- 솔리디티는 변수가 컨텍스트에 따라 메모리 or 파일시스템에 저장  
- 문자열, 배열, 구조체 등 복합 데이터 유형의 경우 스토리지 OR 메모리를 유형에 추가해  
재정의할 수 있음  
- 함수 매개변수(리턴 매개변수 포함)의 기본 위치는 메모리  
로컬 변수의 기본 위치는 스토리지  
상태 변수의 경우 강제로 스토리지에 저장  

> 데이터의 위치에 따라 할당이 동작하는 방식도 변경 됨  

- 스토리지 변수와 메모리 변수 사이의 할당은 언제나 독립적인 사본을 생성  
BUT 메모리에 저장되는 복잡한 유형으로부터 다른 메모리에 저장되는 복합 유형으로의  
할당은 사본을 생성하지 X  
- 상태 변수(다른 상태변수부터라도)의 할당은 언제나 독립적인 사본을 생성  
- 메모리에 저장된 복합 유형을 로컬 스토리지 변수에 할당할 수 X  
- 상태 변수를 로컬 스토리지에 할당하는 경우, 로컬 스토리지 변수가 상태 변수를 가리킴  
(즉 로컬 스토리지 변수가 포인터가 됨)

## 다른 데이터 유형  
; 솔리디티는 정적인 유형의 언어이며, 변수가 저장하는 데이터 유형은 사전에 정의돼 있어야 함  
=> 기본적으로 변수의 모든 비트는 0으로 할당 & 변수는 함수의 범위를 가지고 있음  

- bool (True , False)  
- uint, int  
uint8, uint16, uint24 ... unit256은 각각 부호 없는 8비트, 16,24, ... 256비트의 정수   
- ufixed, fiexed   
ufixed0x8, ufixed0x16 ... ufixed0x256 8, 16, 256비트의 부호 없는 실수  
- address  
; 16진수를 할당해 최대 20바이트 값을 저장하는데 사용 (이더리움 주소를 저장하는 데 사용)  
address 유형은 balance(계정의 잔액 확인), send(주소로 이더를 송금)라는 2개의 속성을 제공  

## 배열  
; 일반 및 바이트 배열을 모두 지원, 정적 및 동적 배열, 다차원 배열 지원  

> 배열의 문법 예제  

```
pragma solidity ^0.4.0;
contract Sample {
    // 동적 크기 배열
    // 배열 리터럴이 보일 때마다 새로운 배열이 생성됨. 배열 리터멀이 명시돼 있으면  
    // 스토리지에 저장되고, 함수 내부에서 발견되면 메모리에 저장  
    int[] myArray = [0,0];

    function sample(uint index, int value) {
        // 배열의 인덱스는 uint256 유형이야 함
        myArray[index] = value;

        // myArray2는 myArray의 포인터를 저장한다.
        int[] myArray2 = myArray;

        // 메모리 내 고정된 크기의 배열
        // 여기서는 99999가 최대값이며 이 값을 위해 필요한 최대 크기가 2424비트이므로
        // 여기서는 uint24를 사용해야 한다.
        // 메모리에 사용하는 것은 비싸므로 이와 같은 제약은 메모리 내 리터럴에 적용  
        // [1, 2, 99999]는 unit24 유형이므로 포인터를 저장하기 위해 같은 유형이여야 한다
        uint24[3] memory myArray3 = [1,2,99999]; //배열 리터럴

        // myArray4에 메모리 내 복합 유형을 할당할 수 없으므로 예외가 발생
        uint8[2] myArray4= [1,2];
    }
}
```  

- 배열 또한 배열의 크기를 알아내기 위한 length 속성을 가지고 있음  
배열의 크기를 변경하기 위해 length 속성에 값을 할당할 수도 있지만, 메모리 내 배열 및  
동적이 아닌 배열의 크기를 변경할 수는 없음  
- 동적 배열의 설정되지 않은 인덱스에 접근하려고 하는 경우 예외가 발생  
- 배열, 구조체, 맵은 함수의 매개변수, 리턴 값이 될 수 없음  

## 문자열  
; bytes와 string을 이용하는 2가지 방법 존재  
bytes는 원시 문자열(raw string) // string은 UTF-8 문자열을 만드는 데 사용  

> 문자열 문법 예제


```
pragma solidity ^0.4.0;
contract Sample {
    // 문자열 리터럴이 보일 때마다 새로운 문자열이 생성. 문자열 리터럴이 명시돼 있으면
    // 스토리지에 저장되고, 함수 내부에서 발견되면 메모리에 저장
    // myString에 "" 문자열 저장
    string myString = ""; // 문자열 리터럴
    bytes myRawString;

    function sample(string initString, bytes rawStringInit) {
        myString = initString;

        // myString2는 myString으로의 포인터를 저장
        string myString2 = myString;

        // myString3는 메모리 내의 문자열
        string memory myString3 = "ABCDE";

        // 길이 및 내용 변경
        myString3 = "XYZ";
        myRawString = rawStringInit;

        myRawString의 길이 증가
        myRawString.length++;

        // 컴파일 시 예외 발생
        // Type string memory is not implicitly convertible to
        // expected type string storage pointer
        string myString4 = "Example";

        // 컴파일 시 예외 발생
        string myString5 = initString;
    }
}
```  

## 구조체  

```
pragma solidity ^0.4.0;
contract Sample {  
    struct myStruct {
        bool myBool;
        string myString;
    }

    myStruct s1;

    // 구조체 메소드가 보일 때마다 새로운 구조체가 생성. 구조체 메소드가 명시돼
    // 있으면 스토리지에 저장되고, 함수 내부에 있는 경우 메모리에 저장

    myStruct s2 = myStruct(true, ""); // 구조체 메소드 문법

    function sample(bool initBool, string initString) {
        // 구조체 인스턴스 생성
        s1 = myStruct(initBool, initString);

        // 메모리에 인스턴스 생성
        myStruct memory s3 = myStruct(initBool, initString);
    }
}
```  

- 구조체는 함수 매개변수가 될 수 없으며 함수가 구조체를 리턴하지 못함  

## 열거형  

```
pragma solidity ^0.4.0;
contract Sample {
    // 모든 열거형 값을 포함할 수 있는 가장 작은 정수형이 열거 값을
    // 가지기 위해 선택된다.
    enum OS {Windows, Linux, OSX, UNIX}

    OS choice;

    function sample(OS chosen) {
        choice = chosen;
    }

    function setLinixOS() {
        choice = OS.Linux;
    }

    function getChoice() returns (OS chosenOS) {
        return choice;
    }
}
```  

## 매핑  
;매핑 데이터 유형은 해시 테이블. 매핑은 메모리가 아닌 스토리지에만 사용될 수 있음  
=> 오직 상태 변수로만 선언  
매핑은 키-값 쌍으로 구성된 것으로 생각할 수 있음. 키가 실제로 저장되는 대신 키의  
keccak256 해시 값이 값을 검색하기 위해 사용된다. 매핑은 길이를 가지고 있지 않음  

> 매핑 예제  

```
pragma solidity ^0.4.0;
contract Sample {
    mapping (int => string) myMap;

    function sample(int key, string value) {
        myMap[key] = value;

        // myMap2는 myMap의 참조
        mapping(int => string) myMap2 = myMap;
    }
}
```

- 설정되지 않은 키에 접근하려고 하면 모두 0비트인 값을 줌  

## delete 연산자  
; 기본값(모든 비트가 0으로 할당)으로 재설정하기 위해 사용될 수 있다  
- 동적 배열 => 모든 요소를 지우고 길이가 0이 됨  
- 정적 배열 => 모든 인덱스 재설정  
(특정 인덱스에 delete를 적용하면 인덱스가 재설정)  
- 맵 유형 => 아무 일 X / 키 => 키와 연관된 값 삭제  

```
pragma solidity ^0.4.0;
contract Sample {
    struct Struct {
        mapping (int => int) myMap;
        int myNumber;
    }

    int[] myArray;
    Struct myStruct;

    function sample(int key, int value, int number, int[] array) {
        // 맵은 할당될 수 없으므로 구조체를 생성하는 동안 맵은 무시한다.
        myStruct = Struct(number);

        // 맵 키/값을 설정
        myStruct.myMap[key] = value;
        myArray = array;
    }

    function reset() {
        // 이제 myArray의 길이는 0이 됨
        delete myArray;

        // 이제 myNumber는 0이며 myMap은 현재 상태로 남아있는다.        
        delete myStruct;
    }

    function deleteKey(int key) {
        // 키를 삭제
        delete myStruct.myMap[key];
    }
}
```  

## 기본 유형 간의 변환  
; 기본 유형 : 배열, 문자열, 구조체, 열거형, 맵 이외의 모든 것  
=> 서로 다른 유형 간에 연산자가 적용되면, 컴파일러가 묵시적으로 피연산자(operand) 중  
하나를 다른 유형으로 변환  
=> 정보의 유실이 없는 경우 값 유형 간의 묵시적 변환은 가능  
uint8 -> uint16, uint128 -> uint256 BUT int8 -> uint256 X  

> 명시적 형변환  

```
uint32 a = 0x12345678;
uint16 b = uint16(a); // b는 0x5678이 됨
```  

## var 사용  
; 첫번째 할당된 값에 따라 동적으로 유형이 결정. 값이 지정 된 이후로 다른 값을  
할당하면 형 변환이 발생  

```
int256 x = 12;
// y의 유형은 int256
var y = x;
uint256 z = 9;

// 묵시적 형 변환이 불가능하므로 예외 발생
y = z;
```  

## 제어 구조  
; if, else, while, for, break, continue, return, ? 를 지원  

```
pragma solidity ^0.4.0;
contract Sample {
    int a = 12;
    int b;

    function sample() {
        if(a == 12) {

        }
        else if( a = 34) {

        }
        else {

        }

        var temp = 10;        
        while(temp < 20) {
            if(temp == 17) {
                break;
            }
            else {
                continue;
            }

            temp++;
        }

        for(var iii=0; iii<b.length; iii++) {

        }        
    }
}
```  

## new 연산자를 사용해 컨트랙트 생성  
; new 키워들르 사용해 새로운 컨트랙트를 생성할 수 있으며 생성되는 컨트랙트의 전체  
코드에 대해 알고 있어야 함.


```
pragma solidity ^0.4.0;

contract sample1 {
    int a;

    function assign(int b) {
        a = b;
    }
}

contract sample2 {
    function sample2() {
        simple1 s = new sample1();
        s.assign(12);
    }
}
```  

## 예외  
; throw 키워드 사용 && catch로 예외 처리하는 것은 불가능  

```
pragma solidity ^0.4.0;

contract sample {
    function myFunction() {
        throw;
    }
}
```  

## 외부 함수 호출  
- 내부 함수 호출 : 같은 컨트렉트의 다른 함수를 호출  
- 외부 함수 호출 : 다른 컨트렉트의 함수를 호출  

```
pragma solidity ^0.4.0;

contract sample1 {
    int a;

    // "payable"은 기본으로 포함된(built-in) 변경자  
    // 다른 컨트랙트가 메소드를 호출하면서 이더를 전송할 때 이 변경자가 필요
    function sample1(int b) payable {
        a = b;
    }

    function assign(int c) {
        a = c;
    }

    function makePayment(int d) payable {
        a = d;
    }
}

contract sample2 {
    function hello() {
    }

    function sample2(address addressOfContract)  {
        // 컨트랙트 인스턴스를 생성하면서 12 wei 전송
        sample s = (new sample1).value(12)(13);

        s.makePayment(22);

        // 다시 이더를 전송
        s.makePayment.value(45)(12);

        // 사용할 가스의 양 지정
        s.makePayment.gas(895)(12);

        // 이더를 전송하고 가스를 다시 지정
        s.makePayment.value(4).gas(900)(12);

        // hello()는 내부 호출이며 this.hello()는 외부 호출
        this.hello();

        // 이미 배포된 컨트랙트를 지정
        sample1 s2 = sample1(addressOfContract);

        s2.makePayment(112);
    }
}
```  

- this를 사용한 호출을 외부 호출이라 한다. 함수 내 this 키워드는 현재 컨트랙트  
인스턴스를 나타냄

## 컨트랙트 특징  

**가시성**  
; 상태 변수 or 함수의 가시성(visibility)은 누가 볼 수 있는지를 정의  
함수 및 상태 변수는 external, public, internal, private 유형의 가시성이 존재  

- external  
external 함수는 다른 컨트랙트 또는 트랜잭션을 통해서만 호출 될 수 있음  
외부 함수 f는 내부적으로 호출될 수 X. 즉 f()는 동작하지 않지만, this.f()는 동작  
상태 변수에는 외부 가시성 적용 할 수 없음  
- public
public 함수 및 상태 변수는 가능한 모든 방법으로 접근 할 수 있음  
컴파일러에서 생성된 접근자 함수(accessor function)는 모두 public 상태 변수  
자신의 접근자를 생성할 수는 없음.  
(getters만을 생성하고 setters는 생성X)  
- internal  
internal 함수 및 상태변수는 내부적으로만 접근될 수 있음. 즉 현재의 컨트랙트 또는  
이 컨트렉트에서 상속된 컨트랙트에서만 가능. 접근을 위해 this를 사용X  
- private  
private 함수 및 상태는 internal과 비슷하지만 상속된 컨트랙트에서 접근X  

> 가시성 예제  

```
pragma solidity ^0.4.0;

contract sample1 {
    int public b = 78;
    int internal c = 90;

    function sample1() {
        // 외부(external) 접근
        this.a();

        // 컴파일 오류
        a();

        // 내부(internal) 접근
        b = 21;

        // 외부(external) 접근
        this.b;

        // 외부(external) 접근
        this.b();

        // 컴파일 오류
        this.b(8);

        // 컴파일 오류
        this.c();

        // 내부(internal) 접근
        c = 9;
    }    

    function a() external {
    }
}

contract sample2 {
    int internal d = 9;
    int private e = 90;

    // sample3은 sample2를 상속
    contract sample3 is sample2 {      
        sample1 s;

        function sample3() {
            s = new Sample1();

            // 외부(external) 접근
            s.a();

            // 외부(external) 접근
            var f = s.b;

            // 접근자를 통해 값을 할당할 수 없으므로 컴파일 오류
            s.b = 18;

            // 컴파일 오류
            s.c();

            // 내부(internal) 오류
            d = 8;

            // 컴파일 오류
            e = = 7;
        }
    }
}
```  

**함수 변경자(function modifier)**  
; 자식 컨트랙트에 의해 상속되며 자식 컨트랙트는 재정의(override) 할 수 있음  
다수의 변경자는 공백으로 구별되는 목록을 지정하는 방식 순서대로 평가 되고  
변경자에 인자를 전달할 수도 있음  
```_;```이 표시된 곳에는 다음 변경자 바디나 함수 바디 중 먼저 오는 것이 삽입 됨  

> 함수 변경자 예제  

```
contract sample {
    int a = 90;

    modifier myModifier1(int b) {
        int c =  b;
        `_;
        c = a;
        a = 8;
    }

    modifier myModifier2 {
        int c = a;
        _;
    }

    modifier myModifier3 {
        a = 96;
        return;
        _;
        a = 99;
    }

    modifier myModifier4 {
        int c = a;
        _;
    }

    function myFunction() myModifier1(a) myModifier2 myModifier3 returns (int) {
        a = 1;
        return a;
    }
}
```  

> myFunction()이 아래와 같이 실행

```
  int c = b;
      int c = a;
          a = 96;
          return;
              int c = a;
                  a = 1;
                  return a;
          a = 99;
   c = a;
   a = 8;
```   

=> myFunction 함수를 호출하면 0을 리턴 / 그 이후 상태 변수 a에 접근하려고  
할 경우 8 값을 얻음  
=> 함수의 경우 호출자의 코드 실행이 완료되고 나면 리턴 이후 코드가 실행  
=> 변경자의 경우 호출자의 코드 실행이 완료된 이후 기존 변경자의 _;뒤에 오는 코드가 실행  
=> 변경자 내부의 리턴은 연관된 값을 가질 수 X. 항상 0비트를 반환  

## 폴백 함수 fallback function  
;한 개의 이름 없는 함수를 가질 수 있음  
=> 이 함수는 인자 및 리턴 값 X  
=> 다른 함수들이 주어진 함수 식별자와 일치하지 않는 컨트랙트 호출인 경우 실행  

```
contract sample {
  function() payable {
    // 누구로부터 얼마큼의 이더가 전송됐는지 기록
  }
}
```  

## 상속  
; 다형성(polymorphism)을 포함해 코드를 카피하는 방식으로 다중 상속을 지원  
=> 컨트랙트가 다중의 컨트랙트를 상속하더라도 블록체인에는 하나의 컨트랙트만  
생성되며 부모 컨트랙트의 코드가 항상 최종 컨트랙트에 복사됨  

```
contract sample1 {
    function a() {}
    function b() {}
}

// sample2는 sample1을 상속
contract sample2 is sample1 {
    function b() {}
}

contract sample3 {
    function sample3(int b) {
    }
}

// sample4는 sample1과 sample2를 상속
// sample1은 sample2 부모이므로, 오직 하나의 sample1 인스턴스만 존재
contract sample4 is sample1, sample2 {
    function a() {}
    function c() {
        // 그 다음에는 sample3 컨트랙트의 a 메소드 실행
        a();

        // 그 다음에는 sample1 컨트랙트의 a 메소드 실행
        sample1.a();

        // sample2.b()가 부모 컨트랙트 리스트의 마지막에 있으며
        // sample1.b()를 재정의하므로 sample2.b()를 호출
        b();
    }
}

// 생성자가 인자를 받으면, 자식 컨트랙트 생성 시 제공돼야 한다.
// 솔리디티에서는 자식 컨트랙트가 부모의 생성자를 대신 호출하지 않는다.
// 부모가 초기화되고 자식에게 복사
contract sample5 is sample3(122) {
}
```


































































<br /><br /><br /><br /><br /><br /><br /><br /><br />

---  
