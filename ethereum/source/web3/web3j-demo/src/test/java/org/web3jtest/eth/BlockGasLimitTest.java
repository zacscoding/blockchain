package org.web3jtest.eth;

import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-06
 * @GitHub : https://github.com/zacscoding
 */
public class BlockGasLimitTest extends AbstractTestRunner {

    // chain spec
    BigInteger gasLimitBoundDivisor;
    BigInteger minGasLimit;

    // miners config?
    BigInteger gasFloor;
    BigInteger gasCeil;

    @Before
    public void setUp() {
        // https://github.com/ethereum/go-ethereum/blob/ee92bc537f159c2202058a33e015684cc2bca04c/params/protocol_params.go
        gasLimitBoundDivisor = BigInteger.valueOf(1024);
        minGasLimit = BigInteger.valueOf(5000);

        // my geth`s node config
        gasFloor = new BigInteger("800");
        gasCeil = new BigInteger("47000000");
    }

    @Test
    public void checkGasLimit() throws Exception {
        int start = 0;
        int last = web3j.ethBlockNumber().send().getBlockNumber().intValue();

        BigInteger parentGasLimit = null;
        BigInteger parentGasUsed = null;

        int includeTxnsBlockNumber = -1;

        for (int i = start; i <= last; i++) {
            if (includeTxnsBlockNumber > 0 && i - 10 > includeTxnsBlockNumber) {
                break;
            }

            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(i)), false).send().getBlock();

            if (i != start) {
                BigInteger gasLimit = block.getGasLimit();
                BigInteger expectedGasLimit = calcGasLimit(parentGasLimit, parentGasUsed, gasFloor, gasCeil);
                SimpleLogger.println("# Check {} > gas limit : {} | expected : {} | diff : {}"
                ,i , gasLimit, expectedGasLimit, gasLimit.subtract(expectedGasLimit));
            }

            parentGasLimit = block.getGasLimit();
            parentGasUsed = block.getGasUsed();

            if (includeTxnsBlockNumber < 0 && block.getTransactions().size() > 0) {
                includeTxnsBlockNumber = i;
            }
        }
    }

    @Test
    public void displayGasLimit() throws Exception {
        int start = 0;
        int last = web3j.ethBlockNumber().send().getBlockNumber().intValue();

        for (int i = start; i <= last; i++) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(i)), false).send().getBlock();
            SimpleLogger.println("# Block {} > gas limit : {} | gas used : {}", i, block.getGasLimit(), block.getGasUsed());
        }
    }

    /**
     * Calculate gasLimit at go-ethereum
     * https://github.com/ethereum/go-ethereum/blob/e29c2e43640445e743bb69ab48d6c862d3e964d9/core/block_validator.go
     */
    public BigInteger calcGasLimit(BigInteger parentGasLimit, BigInteger parentGasUsed, BigInteger gasFloor, BigInteger gasCeil) {
        // contrib := (parent.GasUsed() + parent.GasUsed()/2) / params.GasLimitBoundDivisor
        BigInteger contrib = parentGasUsed.add(parentGasUsed.divide(BigInteger.valueOf(2L))).divide(gasLimitBoundDivisor);

        // decay := parent.GasLimit()/params.GasLimitBoundDivisor - 1
        BigInteger decay = parentGasLimit.divide(gasLimitBoundDivisor).subtract(BigInteger.ONE);

        // limit := parent.GasLimit() - decay + contrib
        BigInteger limit = parentGasLimit.subtract(decay).add(contrib);

        if (limit.compareTo(minGasLimit) < 0) {
            limit = minGasLimit;
        }

        if (limit.compareTo(gasFloor) < 0) {
            limit = parentGasLimit.add(decay);

            if (limit.compareTo(gasFloor) > 0) {
                limit = gasFloor;
            }
        } else if(limit.compareTo(gasCeil) > 0) {
            limit = parentGasLimit.subtract(decay);

            if(limit.compareTo(gasCeil) < 0) {
                limit = gasCeil;
            }
        }

        return limit;
    }

    /**
     * Calculate gasLimit at Ethereumj
     *
     * https://github.com/ethereum/ethereumj/blob/develop/ethereumj-core/src/main/java/org/ethereum/mine/Miner.java
     */
    public long calcGasLimit2(int parentGasLimit, int parentGasUsed) {
        long newGasLimit = Math.max(125000, (BigInteger.valueOf(parentGasLimit).longValue() * (1024 - 1) + (parentGasUsed * 6 / 5)) / 1024);
        return newGasLimit;
    }
}