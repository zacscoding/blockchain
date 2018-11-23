package org.demo.smartcontract;

import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.demo.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class SmartContractSearchTest {

    static HttpService httpService;
    static Web3j web3j;

    @BeforeClass
    public static void startSetUp() throws Exception {
        String url = Files.readLines(new File("src/test/resources/secret.txt"), StandardCharsets.UTF_8).get(0);
        httpService = new HttpService(url);
        web3j = Web3j.build(httpService);
    }


    @Test
    public void checkSmartContractCreate() throws Exception {
        String[] hashes = new String[] {
            "0x4a2cb85b7a22e65334f3538723d93288a5e59a008e4b899791d85cf06c3bdae2",
            "0x32520b3522b9c99ede5731ac3d6354aa7980d1353d013d57c865d45bd3442186",
            "0x9b7b8f05cdc6f8a134802f3c435c328e19ade85e1793fcb93fa3ef44c08547e5"
        };

        // 1) check by transaction. to is null or not
        Consumer<String> checkFromTransaction = hash -> {
            try {
                Optional<Transaction> optional = web3j.ethGetTransactionByHash(hash).send().getTransaction();
                SimpleLogger.print("## Check hash : {} from transaction", hash);
                if (!optional.isPresent()) {
                    SimpleLogger.println(" >>> Not exist");
                } else {
                    boolean isContractCreate = optional.get().getTo() == null;
                    SimpleLogger.println(">> is contract create : {}", isContractCreate);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // 2) check by transaction receipt. contract address field
        Consumer<String> checkFromTransactionReceipt = hash -> {
            try {
                Optional<TransactionReceipt> optional = web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt();
                SimpleLogger.print("## Check hash : {} from transaction receipt", hash);
                if (!optional.isPresent()) {
                    SimpleLogger.println(" >>> Not exist");
                } else {
                    TransactionReceipt receipt = optional.get();
                    boolean isContractCreate = receipt.getTo() == null && receipt.getContractAddress() != null;
                    SimpleLogger.println(">> is contract create : {}", isContractCreate);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };


        for (String hash : hashes) {
            checkFromTransaction.accept(hash);
            checkFromTransactionReceipt.accept(hash);
        }
    }
}
