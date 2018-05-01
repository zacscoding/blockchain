package org.web3jtest.eth;

import java.math.BigInteger;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
public class BlockTest  extends AbstractTestRunner {

    @Test
    public void gettingLastBlockNumber() throws Exception {
        BigInteger lastBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        SimpleLogger.println("Last block number : {}", lastBlockNumber);
    }

    @Test
    public void checkBlocksFromTo() throws Exception{
        BigInteger lastRangeNumber = BigInteger.valueOf(10L);
        BigInteger startRangeNumber = BigInteger.valueOf(0L);
        int size = lastRangeNumber.subtract(startRangeNumber).intValue();

        for(int i=0; i<size; i++) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(lastRangeNumber.subtract(BigInteger.valueOf(i))), true).send().getBlock();
            SimpleLogger.printJSONPretty(block);
        }
    }


}
