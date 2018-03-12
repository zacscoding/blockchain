package blockchain;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * http://www.popit.kr/java-%EC%BD%94%EB%93%9C%EB%A1%9C-%EC%9D%B4%ED%95%B4%ED%95%98%EB%8A%94-%EB%B8%94%EB%A1%9D%EC%B2%B4%EC%9D%B8blockchain/
 */
@Getter
@Setter
public class BlockHeader {

    // 소프트웨어 or 프로토콜 등의 업그레이드를 추적하기 위해 사용되는 버전 정보
    private int version;
    // 블록체인 상의 이전 블록(부모 블록)의 해시값
    private byte[] previousBlockHash;
    // 머클트리의 루트에 대한 해시값
    private int merkleRootHash;
    // 해당 블록의 생성 시각
    private int timestamp;
    // 채굴과정에서 필요한 작업 증명(Proof of Work) 알고리즘의 난이도 목표
    private int difficultyTarget;
    // 채굴과정의 작업 증명에서 사용되는 카운터
    private int nonce;

    public BlockHeader(byte[] previousBlockHash, Object[] transactions) {
        this.previousBlockHash = previousBlockHash;
        this.merkleRootHash = this.someMethod(transactions);
    }

    public byte[] toByteArray() {
        String tmpStr = "";
        if (previousBlockHash != null) {
            tmpStr += new String(previousBlockHash, 0, previousBlockHash.length);
        }
        tmpStr += merkleRootHash;
        return tmpStr.getBytes(StandardCharsets.UTF_8);
    }

    // 머클 루트 해시값 대체
    private int someMethod(Object[] transations) {
        return Arrays.hashCode(transations);
    }
}
