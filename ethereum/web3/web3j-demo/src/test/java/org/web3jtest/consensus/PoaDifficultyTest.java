package org.web3jtest.consensus;

import java.math.BigInteger;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-06-13
 * @GitHub : https://github.com/zacscoding
 */
public class PoaDifficultyTest extends AbstractTestRunner  {
    BigInteger maxValue = new BigInteger("340282366920938463463374607431768211455");
    long stepDuration = 10L;

    @Test
    public void displayDiff() throws Exception {
        BigInteger latest = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger start = BigInteger.ONE;

        while (start.compareTo(latest) <= 0) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(start), false).send().getBlock();
            Block parent = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(start.subtract(BigInteger.ONE)), false).send().getBlock();

            BigInteger curDiff = block.getDifficulty();
            BigInteger calcDiff = calculateDifficulty(parent, block);

            SimpleLogger.println("{} ==> diff : {} || calc : {} || isSame : {}"
                , block.getNumber().toString(10), curDiff, calcDiff, calcDiff.subtract(curDiff).equals(BigInteger.ZERO));
            start = start.add(BigInteger.ONE);
        }
    }

    private BigInteger calculateDifficulty(Block parent, Block block) {
        long parentStep = getStep(parent);
        long step = getStep(block);

        return maxValue.add(BigInteger.valueOf(parentStep)).subtract(BigInteger.valueOf(step));
    }

    private long getStep(Block block) {
        return block.getTimestamp().longValue() / stepDuration;
    }
}
