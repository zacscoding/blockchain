package org.web3jtest.eth;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.SimpleLogger;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
public class TransactionTest extends AbstractTestRunner {

    @Test
    public void findTxByHash() throws Exception {
        List<String> hashes = Arrays.asList(
            "0xbcd49c7464c9474a27f1d293d7398e2be5f497387007dd3c3dfafd9b6ad0c537"
        );

        for (String hash : hashes) {
            Transaction tx = web3j.ethGetTransactionByHash(hash).send().getResult();
            SimpleLogger.printJSONPretty(tx);
        }
    }

    @Test
    public void pendingTxns() throws Exception {
        Subscription txSubscription = web3j.transactionObservable().subscribe(
            tx -> {
                try {
                    SimpleLogger logger = SimpleLogger.build();
                    logger.appendRepeat(10, "==").append(" Receive tx : {} ", tx.getHash()).appendRepeat(10, "==").newLine();
                    TransactionReceipt tr = web3j.ethGetTransactionReceipt(tx.getHash()).send().getTransactionReceipt().get();
                    logger.appendln("@@ Receipt status : {}, contract addr : {}", tr.getStatus(), tr.getContractAddress());
                    if (tr.getStatus().equals("0x0")) {
                        Transaction search = web3j.ethGetTransactionByHash(tx.getHash()).send().getTransaction().get();
                        logger.appendln("Exist : " + (search != null)).flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },
            error -> {
                error.printStackTrace();
            },
            () -> {
                SimpleLogger.println("Complete...");
            }
        );

        TimeUnit.MINUTES.sleep(1);
        SimpleLogger.info("@@ end..");
        txSubscription.unsubscribe();
    }

    @Test
    public void sendTx() throws Exception {
        //org.web3j.protocol.core.methods.request.Transaction sendTx = new org.web3j.protocol.core.methods.request.Transaction.createEtherTransaction();
    }


}
