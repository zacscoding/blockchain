package org.demo.test;

import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import org.demo.util.CompareUtil;
import org.demo.util.LogLevelUtil;
import org.demo.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-05-02
 * @GitHub : https://github.com/zacscoding
 */
public class CompareBlocks {

    Web3j node0;
    Web3j node1;
    Web3j node2;

    @Before
    public void setUp() {
        node0 = Web3j.build(new HttpService("http://192.168.5.77:8540"));
        node1 = Web3j.build(new HttpService("http://192.168.5.77:8541"));
        //node2 = Web3j.build(new HttpService("http://192.168.79.128:8542"));
        LogLevelUtil.setInfo();
    }

    @Test
    public void compareBlocks() throws Exception {
        BigInteger lastRangeNumber = BigInteger.valueOf(10L);
        BigInteger startRangeNumber = BigInteger.valueOf(0L);
        int size = lastRangeNumber.subtract(startRangeNumber).intValue();

        for (int i = 0; i < size; i++) {
            Block b1 = node0.ethGetBlockByNumber(DefaultBlockParameter.valueOf(lastRangeNumber.subtract(BigInteger.valueOf(i))), true).send().getBlock();
            Block b2 = node1.ethGetBlockByNumber(DefaultBlockParameter.valueOf(lastRangeNumber.subtract(BigInteger.valueOf(i))), true).send().getBlock();
            if (!CompareUtil.equals(b1, b2)) {
                SimpleLogger.println("## Node 1 ##");
                SimpleLogger.printJSONPretty(b1);

                SimpleLogger.println("## Node 2 ##");
                SimpleLogger.printJSONPretty(b2);
            } else {
                SimpleLogger.println("## Same block : " + (b1 == null ?  "null" : b1.getNumber()));
            }
        }
    }

}
