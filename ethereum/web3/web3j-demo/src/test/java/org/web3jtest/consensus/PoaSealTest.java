package org.web3jtest.consensus;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.ethereum.core.BlockHeader;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.ByteUtil;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.rpc.ParityJsonRpc;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-06-08
 * @GitHub : https://github.com/zacscoding
 */
public class PoaSealTest {

    @Test
    public void blockHashCompare() {

    }

    @Test
    public void displaySeals() {
        long start = -1L;
        long end = 42L;
        Object[] params = {
            null,
            false
        };

        while (start <= end) {
            start += 1L;
            params[0] = "0x" + BigInteger.valueOf(start).toString(16);
            Map<String, Object> blockResult  = (Map<String, Object>)JsonRpcHttpService.requestAndGetResult("http://192.168.5.77:8540/", ParityJsonRpc.eth_getBlockByNumber, null, params);
            BlockHeader header = createHeader(blockResult);

            List<String> sealFields = (List<String>)blockResult.get("sealFields");
            String stepHex = sealFields.get(0);
            long step = new BigInteger(stepHex.substring(2), 16).longValue();
            String signatureHex = sealFields.get(1);


            byte[] hash = HashUtil.sha3(header.getEncodedTemp(ByteUtil.hexStringToBytes(signatureHex), ByteUtil.hexStringToBytes(stepHex)));
            // byte[] hash = HashUtil.sha3(header.getEncodedTemp2(ByteUtil.hexStringToBytes(signatureHex), step));

            String originHash = (String) blockResult.get("hash");
            String calcHash = "0x" + Hex.toHexString(hash);
            boolean isSame = originHash.equals(calcHash);

            SimpleLogger.build()
                        .appendln("## Check block : {} ==> {}", start, isSame)
                        .appendln(String.format("%-15s : %s", "Origin Hash", originHash))
                        .appendln(String.format("%-15s : %s", "Calc Hash", calcHash ))
                        .append("==========================================")
                        .flush();
        }
    }

    @Test
    public void test() {
        byte[] bytes = ByteUtil.hexStringToBytes("0xb841bfa8c30fee48155aa6df767680eb509aa7b45eae41986cf3e8fcd8abdd9556df66a458aa1d5d6b1f4f8f01e02b0bbc09066796a32dcc4b39c4b2c0c73b22155701");
        System.out.println(ByteUtil.toHexString(bytes));
    }

    private BlockHeader createHeader(Map<String, Object> blockResult) {
        String parentHash = (String)blockResult.get("parentHash");
        String unclesHash = (String)blockResult.get("sha3Uncles");
        String coinbase = (String)blockResult.get("miner");
        String logsBloom = (String) blockResult.get("logsBloom");
        String difficulty = (String) blockResult.get("difficulty");
        String numberHex = (String) blockResult.get("number");
        long number = new BigInteger(numberHex.substring(2), 16).longValue();
        String gasLimit = (String) blockResult.get("gasLimit");
        String gasUsedHex = (String) blockResult.get("gasUsed");
        long gasUsed = new BigInteger(gasUsedHex.substring(2), 16).longValue();
        String timestampHex = (String) blockResult.get("timestamp");
        long timestamp = new BigInteger(timestampHex.substring(2), 16).longValue();
        String extraData = (String)blockResult.get("extraData");

        BlockHeader header = new BlockHeader(
            ByteUtil.hexStringToBytes(parentHash),   // parentHash
            ByteUtil.hexStringToBytes(unclesHash),   // unclesHash
            ByteUtil.hexStringToBytes(coinbase),     // coinbase
            ByteUtil.hexStringToBytes(logsBloom),    // logsBloom
            ByteUtil.hexStringToBytes(difficulty),   // difficulty
            number,                                  // number
            ByteUtil.hexStringToBytes(gasLimit),     // gasLimit
            gasUsed,                                 // gasUsed
            timestamp,                               // timestamp
            ByteUtil.hexStringToBytes(extraData),    // extraData
            null,
            null
        );

        return header;
    }
}
