package org.demo.eth;

import io.reactivex.disposables.Disposable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.demo.AbstractTestRunner;
import org.demo.util.GsonUtil;
import org.demo.util.LogLevelUtil;
import org.demo.util.SimpleLogger;
import org.junit.Test;
import org.reactivestreams.Subscription;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.filters.Callback;
import org.web3j.protocol.core.filters.PendingTransactionFilter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthLog.Hash;
import org.web3j.protocol.core.methods.response.EthLog.LogResult;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

/**
 * @author zacconding
 */
public class TransactionTest extends AbstractTestRunner {

    @Test
    public void findTxByHash() throws Exception {
        List<String> hashes = Arrays.asList("0xd2c178db394957c0bad6e78b5d2c08e263d333cac6f5dd33415f1c9c983beea6");

        for (String hash : hashes) {
            Transaction tx = web3j.ethGetTransactionByHash(hash).send().getResult();
            SimpleLogger.printJSONPretty(tx);
            TransactionReceipt tr = web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt().get();
            SimpleLogger.printJSONPretty(tr);
            System.out.println(web3j.ethGetBlockByHash(tx.getBlockHash(), false).send().getBlock().getTimestamp().longValue());
            System.out.println(tr.getContractAddress());
        }
    }

    @Test
    public void pendingTxns() throws Exception {
        Disposable txSubscription = web3j.pendingTransactionFlowable().subscribe(tx -> {
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
        }, error -> {
            error.printStackTrace();
        });

        TimeUnit.MINUTES.sleep(1);
        SimpleLogger.info("@@ end..");
        txSubscription.dispose();
    }

    @Test
    public void newPendingTransactionFilter() throws Exception {
        System.out.println("## Start to ##");
        LogLevelUtil.setInfo();
        long blockTime = 5000L;
        BigInteger filterId = web3j.ethNewPendingTransactionFilter().send().getFilterId();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = () -> {
            try {
                List<LogResult> results = web3j.ethGetFilterChanges(filterId).send().getLogs();
                if (results.size() > 0) {
                    System.out.println("## Response log result : " + results.size());

                    for (LogResult result : results) {
                        Hash hash = (Hash) result;
                        System.out.println("## Receive hash : " + hash.get());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        service.scheduleAtFixedRate(runnable, 0, blockTime, TimeUnit.MILLISECONDS);

        TimeUnit.MINUTES.sleep(2);
        System.out.println("## Uninstall result : " + web3j.ethUninstallFilter(filterId).send().getResult());
        System.out.println("## end ##");
    }

    @Test
    public void findTransaction() throws Exception {
        BigInteger startNumber = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger until = BigInteger.valueOf(0);

        boolean includeTxOnly = true;
        int txCount = 0;
        SimpleLogger.println("Start number : {} > last number : {} | include tx only : {}"
            , startNumber.toString(10), until.toString(10), includeTxOnly);

        while (startNumber.compareTo(until) > 0) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(startNumber), true).send().getBlock();
            if (includeTxOnly && block.getTransactions().size() < 1) {
                startNumber = startNumber.subtract(BigInteger.ONE);
                continue;
            }

            long blockNumber = block.getNumber().longValue();
            String gasUsed = String.format("%.2f", block.getGasUsed().doubleValue() / block.getGasLimit().doubleValue() * 100.0D);
            SimpleLogger.build()
                .appendln("## number : {} | #tx : {} | index : {} | diff : {} | gas limit : {} | gas used : {} ({}%)"
                    , blockNumber, block.getTransactions().size(), blockNumber % 5, block.getDifficulty().toString(10)
                    , block.getGasLimit().toString(10), block.getGasUsed().toString(10), gasUsed)
                .flush();
            txCount += block.getTransactions().size();
            startNumber = startNumber.subtract(BigInteger.ONE);
        }
        SimpleLogger.println("#Total txns : {}", txCount);
    }

    @Test
    public void sendRawTransaction() throws Exception {
    }

    @Test
    public void decodeRawTransaction() throws Exception {
        String rawTransaction = "0xf86c108506fc23ac00825208947821762b4f712c6e9755df5e0612caba43173a09888ac7230489e80000801ca0a2e0c175758bcfbeec6d9de94e2b8a7cbe9d866451732d4bb46c7b80983a197ea051544e0900a3c40a2461566ac82e510cf31b4c73825f209e7d7cc01ff2b3f4d9";
        org.ethereum.core.Transaction tx = new org.ethereum.core.Transaction(Numeric.hexStringToByteArray(rawTransaction));
        System.out.println(tx);
    }

    /* ===================================================================================================================================================
     * Not yet
     ===================================================================================================================================================*/

    @Test
    public void pendingFilter() throws Exception {
        LogLevelUtil.setInfo();

        Set<String> pendings = new HashSet<>();
        Disposable subscription = web3j.pendingTransactionFlowable().subscribe(pendingTx -> {
            pendings.add(pendingTx.getHash());
            System.out.println("## Receive pending tx : " + pendingTx.getHash());
        });

        PendingTransactionFilter filter = new PendingTransactionFilter(web3j, new Callback<String>() {
            @Override
            public void onEvent(String s) {
                if (pendings.remove(s)) {
                    try {
                        Optional<TransactionReceipt> optional = web3j.ethGetTransactionReceipt(s).send().getTransactionReceipt();
                        if (optional.isPresent()) {
                            SimpleLogger.println("## Pending result : {} => {}", s, optional.get().getStatus());
                        } else {
                            SimpleLogger.println("## Receive filter : {}, but not exist receipt", s);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        filter.run(service, 500L);

        TimeUnit.MINUTES.sleep(2);
        SimpleLogger.info("@@ end..");

        subscription.dispose();
    }

    @Test
    public void test() throws Exception {
        BigInteger filterId = web3j.ethNewPendingTransactionFilter().send().getFilterId();
        System.out.println("## Filter id : " + filterId);
        web3j.ethGetFilterLogs(filterId).sendAsync().thenAccept(action -> {
            List<LogResult> results = action.getLogs();
            System.out.println(results.get(0).getClass().getName());
            for (LogResult result : results) {
                GsonUtil.printGsonPretty(result);
            }
        });

        TimeUnit.MINUTES.sleep(1);
        SimpleLogger.info("@@ end..");
    }

    @Test
    public void sendTx() throws Exception {
        //org.web3j.protocol.core.methods.request.Transaction sendTx = new org.web3j.protocol.core.methods.request.Transaction.createEtherTransaction();
    }

    @Test

    public void newTransactionFilter() throws Exception {
        BigInteger filterId = web3j.ethNewPendingTransactionFilter().send().getFilterId();


    }


}
