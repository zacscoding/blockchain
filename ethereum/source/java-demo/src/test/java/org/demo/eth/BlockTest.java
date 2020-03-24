package org.demo.eth;

import io.reactivex.disposables.Disposable;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.demo.AbstractTestRunner;
import org.demo.util.LogLevelUtil;
import org.demo.util.SimpleLogger;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthLog.Hash;
import org.web3j.protocol.core.methods.response.EthLog.LogResult;

/**
 * @author zacconding
 */
public class BlockTest extends AbstractTestRunner {

    @Test
    public void gettingLastBlockNumber() throws Exception {
        BigInteger lastBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        SimpleLogger.println("Last block number : {}", lastBlockNumber);
    }

    @Test
    public void temp() throws Exception {
        Disposable subscribe = web3j.replayPastBlocksFlowable(
            DefaultBlockParameter.valueOf(BigInteger.ONE),
            DefaultBlockParameter.valueOf(BigInteger.valueOf(3)), false).subscribe(blk -> {
            SimpleLogger.println("## Receive {} => {}", blk.getBlock().getHash(), blk.getBlock().getHash());
        });

        Thread.sleep(3000L);
        subscribe.dispose();
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
        BigInteger lastRangeNumber = BigInteger.valueOf(7L);
        lastRangeNumber = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger startRangeNumber = BigInteger.valueOf(-1L);
        int size = lastRangeNumber.subtract(startRangeNumber).intValue();

        for (int i = 0; i < size; i++) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(lastRangeNumber.subtract(BigInteger.valueOf(i))), true)
                .send().getBlock();
            // SimpleLogger.println("{} => {}", block.getNumber().toString(10), block.getUncles().size());
            SimpleLogger.println("{} | {} => {}", block.getNumber(), block.getMiner(), block.getDifficulty().toString(10));
            //SimpleLogger.printJSONPretty(block);
        }
    }

    @Test
    public void findUncles() throws Exception {
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        boolean complete = false;
        Block block = null;

        System.out.println(">> Start :: " + blockNumber.toString(10));

        while (!complete && blockNumber.compareTo(BigInteger.ZERO) >= 0) {
            block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), true).send().getBlock();
            if (block.getUncles().size() > 0) {
                complete = true;
            }
            blockNumber = blockNumber.subtract(BigInteger.valueOf(1));
        }
        if (complete) {
            SimpleLogger.printJSONPretty(block);
        } else {
            System.out.println("Not exist uncles..");
        }
    }

    @Test
    public void checkGasLimit() throws Exception {
        int start = 563;
        int last = web3j.ethBlockNumber().send().getBlockNumber().intValue();
        // int last = 100;
        BigInteger prevGasLimit = BigInteger.ZERO;
        BigInteger prevGasUsed = BigInteger.ZERO;

        // long newGasLimit = Math.max(125000, (new BigInteger(1, newBlock.getGasLimit()).longValue() * (1024 - 1) + (newBlock.getGasUsed() * 6 / 5)) / 1024);

        for (int i = start; i <= last; i++) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(i)), false).send().getBlock();
            BigInteger gasLimit = block.getGasLimit();
            String gasUsed = String.format("%.2f", (block.getGasUsed().doubleValue() / block.getGasLimit().doubleValue()) * 100.0D);
            BigInteger delta = gasLimit.subtract(prevGasLimit);
            long newGasLimit = Math.max(125000, (prevGasLimit.longValue() * (1024 - 1) + (prevGasUsed.intValue() * 6 / 5)) / 1024);
            SimpleLogger.println("Block {} > gas limit : {} | gas used : {} ({}%) | delta : {} > expected : {} | diff : {}"
                , i, gasLimit.toString(10), block.getGasUsed().toString(10), gasUsed, delta.toString(10), newGasLimit,
                gasLimit.longValue() - newGasLimit);
            prevGasLimit = gasLimit;
            prevGasUsed = block.getGasUsed();
        }
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
