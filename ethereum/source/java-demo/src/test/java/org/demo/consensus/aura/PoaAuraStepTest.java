package org.demo.consensus.aura;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.demo.rpc.JsonRpcHttpService;
import org.demo.rpc.ParityJsonRpc;
import org.demo.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-06-18
 * @GitHub : https://github.com/zacscoding
 */
public class PoaAuraStepTest {

    List<Spec> specs;

    @Test
    public void checkSteps() throws Exception {
        specs = Arrays.asList(
                new Spec("http://192.168.5.77:8540", 7L)
                , new Spec("http://192.168.5.77:8541", 8L)
                , new Spec("http://192.168.5.77:8542", 4L)
                , new Spec("http://192.168.5.77:8543", 6L));

        for (Spec spec : specs) {
            System.out.println("## Start to check : " + spec.getUri() + " | " + spec.stepDuration);
            Web3j web3j = Web3j.build(new HttpService(spec.uri));
            BigInteger bestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = BigInteger.ZERO;
            Object[] params = new Object[2];
            params[1] = false;
            while (startBlockNumber.compareTo(bestBlockNumber) <= 0) {
                params[0] = "0x" + startBlockNumber.toString(16);
                Map<String ,Object> block = (Map<String ,Object>)JsonRpcHttpService.requestAndGetResult(spec.getUri(), ParityJsonRpc.eth_getBlockByNumber, null, params);

                long timestamp = new BigInteger(((String) block.get("timestamp")).substring(2), 16).longValue();
                long calcStep = calcStep(timestamp, spec.getStepDuration());
                long parityStep = Long.valueOf((String)block.get("step"));
                SimpleLogger.println("#{} => timestamp : {} / step : {} | result : {}"
                    , startBlockNumber.toString(10), timestamp, parityStep, calcStep == parityStep);
                startBlockNumber = startBlockNumber.add(BigInteger.ONE);
            }
        }
    }

    public long calcStep(long timestamp, long stepDuration) {
        return timestamp / stepDuration; // ==> true
        // return Math.round((float) timestamp / stepDuration); ==> false
    }

    @Getter
    @Setter
    @ToString
    @Builder
    private static class Spec {

        private String uri;
        private long stepDuration;
    }
}


