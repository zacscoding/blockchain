package org.web3jtest.consensus;

import java.math.BigInteger;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.rpc.ParityJsonRpc;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-06-13
 * @GitHub : https://github.com/zacscoding
 */
public class PoaDifficultyTest {

    BigInteger maxValue = new BigInteger("340282366920938463463374607431768211455");
    long stepDuration = 5L;
    Web3j web3j;
    String uri;

    @Before
    public void setUp() {
        uri = "http://192.168.5.77:8540";
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
