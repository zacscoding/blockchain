package org.web3jtest.eth;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthLog.Hash;
import org.web3j.protocol.core.methods.response.EthLog.LogResult;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.LogLevelUtil;
import org.web3jtest.util.SimpleLogger;

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
    public void checkBlocksFromTo() throws Exception {
        BigInteger lastRangeNumber = BigInteger.valueOf(10L);
        BigInteger startRangeNumber = BigInteger.valueOf(0L);
        int size = lastRangeNumber.subtract(startRangeNumber).intValue();

        for (int i = 0; i < size; i++) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(lastRangeNumber.subtract(BigInteger.valueOf(i))), true).send()
                               .getBlock();
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
                    for(LogResult logResult : logResults) {
                        String blockHash = ((Hash)logResult).get();
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
}
