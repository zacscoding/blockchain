package org.demo.consensus.aura;

import java.math.BigInteger;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.demo.rpc.JsonRpcHttpService;
import org.demo.rpc.ParityJsonRpc;
import org.demo.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-06-13
 * @GitHub : https://github.com/zacscoding
 */
public class PoaAuraDifficultyTest {

    BigInteger maxValue = new BigInteger("340282366920938463463374607431768211455");
    long stepDuration = 5L;
    Web3j web3j;
    String uri;

    @Before
    public void setUp() {
        // uri = "http://192.168.5.77:8540";
        uri = "http://192.168.5.78:8540";
        HttpService httpService = new HttpService(uri);
        web3j = Web3j.build(httpService);
    }

    @Test
    public void displayDiff() throws Exception {
        BigInteger last = BigInteger.valueOf(50440L);
        BigInteger start = BigInteger.ZERO;
        BlockResult parent = null;

        int success = 0;

        while (start.compareTo(last) <= 0) {
            Map<String, Object> blockMap = (Map<String, Object>) JsonRpcHttpService.requestAndGetResult(
                uri,ParityJsonRpc.parity_getBlockHeaderByNumber, null, "0x" + start.toString(16));
            BlockResult block = new BlockResult(blockMap);

            if (parent == null) {
                parent = block;
                start = start.add(BigInteger.ONE);
                continue;
            }


            BigInteger curDiff = block.difficulty;
            BigInteger calcDiff = calculateDifficulty(parent, block);

            if (!calcDiff.subtract(curDiff).equals(BigInteger.ZERO)) {
                SimpleLogger.println("{} ==> diff : {} || calc : {}", block.number.toString(10), curDiff, calcDiff);
            } else {
                success++;
            }

            if (block.number.longValue() % 1000L == 0L) {
                SimpleLogger.println("## Checked : {} , success : {}", block.number.toString(10), success);
            }

            parent = block;
            start = start.add(BigInteger.ONE);
        }
    }

    @Test
    public void displayDifficulty() throws Exception {
        int bestBlock = web3j.ethBlockNumber().send().getBlockNumber().intValue();
        long parentStep = 0L;

        for (int i = 0; i < bestBlock; i++) {
            String blockNumber = "0x" + BigInteger.valueOf(i).toString(16);
            Map<String, Object> blockMap = (Map<String, Object>) JsonRpcHttpService.requestAndGetResult(
                uri,ParityJsonRpc.parity_getBlockHeaderByNumber, null, blockNumber);
            BlockResult block = new BlockResult(blockMap);

            BigInteger emptyStep = block.difficulty.subtract(maxValue).subtract(BigInteger.valueOf(parentStep)).add(BigInteger.valueOf(block.step));
            BigInteger calcDiff = maxValue.add(BigInteger.valueOf(parentStep)).subtract(BigInteger.valueOf(block.step));

            SimpleLogger.println("block : {} --> diff : {} | parent step : {} | current step : {} ===> empty step : {}"
                , i, block.difficulty, parentStep, block.step, emptyStep.toString(10), calcDiff.subtract(block.difficulty));

            parentStep = block.step;
        }

//        result
//        block : 0 --> diff : 131072 | parent step : 0 | current step : 0 ===> empty step : -340282366920938463463374607431768080383
//        block : 1 --> diff : 340282366920938463463374607430997720049 | parent step : 0 | current step : 770491406 ===> empty step : 0
//        block : 2 --> diff : 340282366920938463463374607431768211438 | parent step : 770491406 | current step : 770491423 ===> empty step : 0
//        block : 3 --> diff : 340282366920938463463374607431768211410 | parent step : 770491423 | current step : 770491468 ===> empty step : 0
//        block : 4 --> diff : 340282366920938463463374607431768211438 | parent step : 770491468 | current step : 770491485 ===> empty step : 0
//        block : 5 --> diff : 340282366920938463463374607431768211410 | parent step : 770491485 | current step : 770491530 ===> empty step : 0
//        block : 6 --> diff : 340282366920938463463374607431768211438 | parent step : 770491530 | current step : 770491547 ===> empty step : 0
//        block : 7 --> diff : 340282366920938463463374607431768211410 | parent step : 770491547 | current step : 770491592 ===> empty step : 0
//        block : 8 --> diff : 340282366920938463463374607431768211438 | parent step : 770491592 | current step : 770491609 ===> empty step : 0
//        block : 9 --> diff : 340282366920938463463374607431768211408 | parent step : 770491609 | current step : 770491656 ===> empty step : 0
//        block : 10 --> diff : 340282366920938463463374607431768211440 | parent step : 770491656 | current step : 770491671 ===> empty step : 0
//        block : 11 --> diff : 340282366920938463463374607431768211410 | parent step : 770491671 | current step : 770491716 ===> empty step : 0
    }

    private BigInteger calculateDifficulty(BlockResult parent, BlockResult block) {
        long parentStep = getStep(parent);
        long step = getStep(block);

        return maxValue.add(BigInteger.valueOf(parentStep)).subtract(BigInteger.valueOf(step));
    }

    private long getStep(BlockResult blockResult) {
        return Math.round((double)blockResult.timestmp / stepDuration);
    }

    private static class BlockResult {
        private BigInteger number;
        private long step;
        private BigInteger difficulty;
        private long timestmp;

        private BlockResult(Map<String, Object> blockMap) {
            this.number = hexToBI((String) blockMap.get("number"));
            this.step = Long.valueOf((String) blockMap.get("step"));

            this.difficulty = hexToBI((String) blockMap.get("difficulty"));
            this.timestmp = hexToLong((String) blockMap.get("timestamp"));

        }

        private BigInteger hexToBI(String input) {
            if(input.startsWith("0x")) {
                input = input.substring(2);
            }

            return new BigInteger(input, 16);
        }

        private long hexToLong(String input) {
            return hexToBI(input).longValue();
        }
    }
}
