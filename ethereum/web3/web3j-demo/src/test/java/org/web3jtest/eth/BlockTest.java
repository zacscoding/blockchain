package org.web3jtest.eth;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthLog.Hash;
import org.web3j.protocol.core.methods.response.EthLog.LogResult;
import org.web3j.protocol.http.HttpService;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.rpc.ParityJsonRpc;
import org.web3jtest.util.GsonUtil;
import org.web3jtest.util.LogLevelUtil;
import org.web3jtest.util.SimpleLogger;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
public class BlockTest extends AbstractTestRunner {

    @Test
    public void gettingLastBlockNumber() throws Exception {
        BigInteger lastBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        SimpleLogger.println("Last block number : {}", lastBlockNumber);
    }

    @Test
    public void temp() throws Exception {
        Subscription subscription = web3j.replayBlocksObservable(
            DefaultBlockParameter.valueOf(BigInteger.ONE),
            DefaultBlockParameter.valueOf(BigInteger.valueOf(3)), false).subscribe(blk -> {
            SimpleLogger.println("## Receive {} => {}", blk.getBlock().getHash(), blk.getBlock().getHash());
        });

        Thread.sleep(3000L);
        subscription.unsubscribe();
    }

    @Test
    public void getBlockByHash() throws Exception {
        String[] hashes = {
            "0x22ac73e4229e4a8bb1ad109eb0942e3902e6e8519f1bdaf06a624f17489e1642",
            "0x22ac73e4229e4a8bb1ad109eb0942e3902e6e8519f1bdaf06a624f17489e16421"
        };

        for (String hash : hashes) {
            Block block = web3j.ethGetBlockByHash(hash, false).send().getBlock();
            System.out.println(block);
        }
    }

    @Test
    public void checkBlocksFromTo() throws Exception {
        LogLevelUtil.setInfo();
        BigInteger lastRangeNumber = BigInteger.valueOf(8L);
        BigInteger startRangeNumber = BigInteger.valueOf(-1L);
        int size = lastRangeNumber.subtract(startRangeNumber).intValue();

        for (int i = 0; i < size; i++) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(lastRangeNumber.subtract(BigInteger.valueOf(i))), true).send().getBlock();
            SimpleLogger.printJSONPretty(block);
        }
    }

    @Test
    public void findUncles() throws Exception {
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        boolean complete = false;
        Block block = null;

        while (!complete) {
            block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send().getBlock();
            if (block.getUncles().size() > 0) {
                complete = true;
            }

            blockNumber = blockNumber.subtract(BigInteger.valueOf(1));
        }

        SimpleLogger.printJSONPretty(block);
    }

    @Test
    public void test() throws Exception {
        System.out.println("## Start to filter block ##");
        LogLevelUtil.setInfo();

        long blockTime = 1000L;
        BigInteger filterId = web3j.ethNewBlockFilter().send().getFilterId();
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = () -> {
            try {
                List<LogResult> logResults = web3j.ethGetFilterChanges(filterId).send().getLogs();
                if (logResults.size() > 0) {
                    // System.out.println(logResults.get(0).getClass().getName());
                    for (LogResult logResult : logResults) {
                        String blockHash = ((Hash) logResult).get();
                        Block block = web3j.ethGetBlockByHash(blockHash, false).send().getBlock();
                        SimpleLogger.println("## Receive new block. num : {}, hash : {}", block.getNumber(), block.getHash());
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
    public void getRawData() throws Exception {
        LogLevelUtil.setInfo();
        BigInteger blockNumber = BigInteger.valueOf(3265130);
        Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false).send().getBlock();
        System.out.println("## Tx Root : " + block.getTransactionsRoot());
        System.out.println("## ================================================");

        block.getTransactions().forEach(result -> {
            String txHash = (String) result.get();
            System.out.println(txHash);
            /*try {
                web3j.ethGetTransactionByHash(txHash).send().getTransaction().ifPresent(tx -> {
                    SimpleLogger.build()
                                .appendln("## Tx hash : " + tx.getHash())
                                .appendln("## Nonce : " + tx.getNonce())
                                .appendln("## Gas price : " + tx.getGasPrice())
                                .appendln("## Gas Limit : ")
                                .appendln("## Receive addr : " + tx.getTo())
                                .appendln("## Value : " + tx.getValue())
                                .appendln("## ===========================================\n")
                                .flush();
                });
            } catch (IOException e) {

            }*/
        });
    }

    @Test
    public void displayBlocks() throws Exception {
        BigInteger start = BigInteger.ZERO;
        BigInteger last = null;
        if (last == null) {
            last = web3j.ethBlockNumber().send().getBlockNumber();
        }

        while (start.compareTo(last) <= 0) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(start), true).send().getBlock();
            SimpleLogger.printJSONPretty(block);
            start = start.add(BigInteger.ONE);
        }
    }
}
