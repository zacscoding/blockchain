package org.web3jtest.consensus.aura;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.rpc.ParityJsonRpc;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-06-07
 * @GitHub : https://github.com/zacscoding
 */
public class PoaAuraMinerViewTest {
    String uri = "http://192.168.5.77:8540/";
    Map<String, String> miners;

    @Before
    public void setUp() {
        miners = new HashMap<>();
        miners.put("0x00bd138abd70e2f00903268f3db08f2d25677c9e", "node0");
        miners.put("0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2", "node1");
    }

    /*
    ## Latest block number : 9
    ## Num : 0, Miner : 0x0000000000000000000000000000000000000000, Step : 0, Timestamp : 0 ===> null || dif : 0 || Primary : 0
    ## Num : 1, Miner : 0x002e28950558fbede1a9675cb113f0bd20912019, Step : 305668622, Timestamp : 1528343110 ===> node2 || timestamp / step : 5 || Primary : 2
    ## Num : 2, Miner : 0x00bd138abd70e2f00903268f3db08f2d25677c9e, Step : 305668629, Timestamp : 1528343145 ===> node0 || timestamp / step : 5 || Primary : 0
    ## Num : 3, Miner : 0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2, Step : 305668630, Timestamp : 1528343150 ===> node1 || timestamp / step : 5 || Primary : 1
    ## Num : 4, Miner : 0x002e28950558fbede1a9675cb113f0bd20912019, Step : 305668649, Timestamp : 1528343245 ===> node2 || timestamp / step : 5 || Primary : 2
    ## Num : 5, Miner : 0x00bd138abd70e2f00903268f3db08f2d25677c9e, Step : 305668653, Timestamp : 1528343265 ===> node0 || timestamp / step : 5 || Primary : 0
    ## Num : 6, Miner : 0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2, Step : 305668654, Timestamp : 1528343270 ===> node1 || timestamp / step : 5 || Primary : 1
    ## Num : 7, Miner : 0x002e28950558fbede1a9675cb113f0bd20912019, Step : 305668676, Timestamp : 1528343380 ===> node2 || timestamp / step : 5 || Primary : 2
    ## Num : 8, Miner : 0x00bd138abd70e2f00903268f3db08f2d25677c9e, Step : 305668680, Timestamp : 1528343400 ===> node0 || timestamp / step : 5 || Primary : 0
    ## Num : 9, Miner : 0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2, Step : 305668681, Timestamp : 1528343405 ===> node1 || timestamp / step : 5 || Primary : 1
     */
    @Test
    public void getBlockByNumber() {
        String latestBlockNumberHex = (String) JsonRpcHttpService.requestAndGetResult(uri, ParityJsonRpc.eth_blockNumber, null);
        int latestBLockNumber = Integer.parseInt(hexToDecimal(latestBlockNumberHex));
        System.out.println("## Latest block number : " + latestBLockNumber);

        for(int i=0; i<= latestBLockNumber; i++) {
            String blockResult = getBlockByNumber(BigInteger.valueOf(i));
            displayBlockMiner(blockResult);
        }
    }

    @Test
    public void displayStepAndPrimary() {
        List<Integer> issuesed = Arrays.asList(
            1528347200, 1528347210, 1528347215, 1528347225, 1528347230, 1528347240, 1528347245, 1528347255,
            1528347260, 1528347270, 1528347275, 1528347285, 1528347290
        );

        long start = 1528347200L;
        long stepDuration = 5L;
        long max = 1528347290L;

        while(start <= max) {
            long step = (start /stepDuration);
            int primary = (int) step % 3;
            String highlight = issuesed.indexOf((int) start) > 0 ? "## " : "";
            SimpleLogger.println("{} Timestamp : {} , Step : {}, Primary : {}",
                highlight, start, step, primary);
            start += stepDuration;
        }
    }

    @Test
    public void test() {
        long timestamp = 1529320639;
        // double divided = ((double) timestamp) / 10;
        double divided = (double) timestamp / 10;
        System.out.println(Math.round(divided));
        System.out.println(new BigDecimal(divided).toPlainString());
    }



    private void displayBlockMiner(String blockResult) {
        Map<String, Object> map = (Map<String, Object>) JsonRpcHttpService.extractResult(blockResult);
        final int nodeCount = 3;

        String blockNumberHex = (String) map.get("number");
        String miner = (String) map.get("author");
        String stepVal = (String) map.get("step");
        String timestampHex = (String) map.get("timestamp");

        long step = Long.parseLong(stepVal);
        long timestamp = new BigInteger(timestampHex.substring(2), 16).longValue();

        //SimpleLogger.println("{} {} {} {}", blockNumberHex, miner, step, timestampHex);
        SimpleLogger.println("## Num : {}, Miner : {}, Step : {}, Timestamp : {} ===> {} || timestamp / step : {} || Primary : {}",
            hexToDecimal(blockNumberHex), miner, step, timestamp, miners.get(miner), ((step == 0L) ? 0 : (timestamp / step)), (step % nodeCount));
    }

    private String hexToDecimal(String hexValue) {
        if(hexValue.startsWith("0x")) {
            hexValue = hexValue.substring(2);
        }

        return new BigInteger(hexValue, 16).toString(10);
    }

    private String getBlockByNumber(BigInteger blockNumber) {
        return JsonRpcHttpService.request(uri, ParityJsonRpc.eth_getBlockByNumber, null, "0x" + blockNumber.toString(16), false);
    }
}
