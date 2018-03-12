package blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.Getter;
import lombok.Setter;

/**
 * http://www.popit.kr/java-%EC%BD%94%EB%93%9C%EB%A1%9C-%EC%9D%B4%ED%95%B4%ED%95%98%EB%8A%94-%EB%B8%94%EB%A1%9D%EC%B2%B4%EC%9D%B8blockchain/
 */
@Getter
@Setter
public class Block {

    // 블록 크기 : 이 필드를 제외한 나머지 데이터들의 크기를 바이트 단위로 표현한 값
    private int blockSize;
    // 해당 블록의 메타 데이터를 담고 있는 객체
    private BlockHeader blockHeader;
    // 거래(transactions)의 수를 저장하는 필드
    private int transactionCount;
    // 거래 정보를 담고 있는 컬렉션(Collection)
    private Object[] transactions;

    public Block() {
    }

    public Block(BlockHeader blockHeader, Object[] transactions) {
        this.blockHeader = blockHeader;
        this.transactions = transactions;
    }

    public String getBlockHash2() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        // Hash twice
        byte[] blockHash = messageDigest.digest(blockHeader.toByteArray());
        blockHash = messageDigest.digest(blockHash);

        return new String(blockHash, 0, blockHash.length);
    }

    public String getBlockHash() {
        String hash = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(blockHeader.toString().getBytes());
            byte[] blockHash = messageDigest.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < blockHash.length; i++) {
                sb.append(Integer.toString((blockHash[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException nse) {
            nse.printStackTrace();
            hash = null;
        }
        return hash;
    }
}