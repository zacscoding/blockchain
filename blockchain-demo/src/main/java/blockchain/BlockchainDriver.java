package blockchain;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * http://www.popit.kr/java-%EC%BD%94%EB%93%9C%EB%A1%9C-%EC%9D%B4%ED%95%B4%ED%95%98%EB%8A%94-%EB%B8%94%EB%A1%9D%EC%B2%B4%EC%9D%B8blockchain/
 */
public class BlockchainDriver {

    List<Block> blockchain = new ArrayList<>();

//    public static void main(String[] args) throws Exception {
//        // Genesis block
//        String[] transactions = {"Hosang send 1k Bitcoins to Zuckerberg."};
//        Block genesisBlock = new Block(new BlockHeader(null, transactions), transactions);
//        System.out.println("Block Hash : " + genesisBlock.getBlockHash());
//
//        //
//        transactions[0] = "Hosang sent 10k Bitcoins to Zuckerbug.";
//        genesisBlock = new Block(new BlockHeader(null, transactions), transactions);
//        System.out.println("Block Hash : " + genesisBlock.getBlockHash());
//    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String[] firstTransactions = {
            "Hosang sent 1k Bitcoins to Zuckerberg.",
            "Hosang sent 10k Bitcoins to Zuckerberg."
        };

        for (String firstTransaction : firstTransactions) {
            System.out.println("First transaction : " + firstTransaction);
            // Genesis block
            String[] transactions = {firstTransaction};
            Block genesisBlock = new Block(new BlockHeader(null, transactions), transactions);
            System.out.println("Block Hash : " + genesisBlock.getBlockHash());

            // Second block
            String[] secondTransactions = {"Zuckerberg sent 500 Bitcoins to Hosang."};
            Block secondBlock = new Block(new BlockHeader(genesisBlock.getBlockHash().getBytes(), secondTransactions), secondTransactions);
            System.out.println("Second Block Hash : " + secondBlock.getBlockHash());

            // Third block
            String[] thirdTransactions = {"Hosang sent 500 Bitcoins to Moon."};
            Block thirdBlock = new Block(new BlockHeader(secondBlock.getBlockHash().getBytes(), thirdTransactions), thirdTransactions);
            System.out.println("Third Block Hash : " + thirdBlock.getBlockHash());

            System.out.println("===========================================================================================================");
        }
    }
}
