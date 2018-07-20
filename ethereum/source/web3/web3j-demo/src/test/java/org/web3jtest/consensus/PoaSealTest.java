package org.web3jtest.consensus;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.ethereum.core.BlockHeader;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.ByteUtil;
import org.ethereum.util.RLP;
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
        System.out.println(JsonRpcHttpService.request("http://192.168.5.77:8540/", ParityJsonRpc.eth_getBlockByNumber, null, "0x1", true));
    }

    /**
     * Parity SealFields에서
     * POW          =>  {"mixHash", "nonce"}
     * POA(AURA)    =>  {"step", "signature"}
     */
    @Test
    public void displaySeals() {
        long start = -1L;
        long end = 7L;
        Object[] params = {
            null,
        };

        while (start <= end) {
            start += 1L;
            params[0] = "0x" + BigInteger.valueOf(start).toString(16);
            Map<String, Object> blockResult  = (Map<String, Object>)JsonRpcHttpService.requestAndGetResult("http://192.168.5.77:8540/", ParityJsonRpc.parity_getBlockHeaderByNumber, null, params);
            String parentHashVal = (String)blockResult.get("parentHash");
            String unclesHashVal = (String)blockResult.get("sha3Uncles");
            String coinbaseVal = (String)blockResult.get("miner");
            String stateRootVal = (String) blockResult.get("stateRoot");
            String txTrieRootVal = (String) blockResult.get("transactionsRoot");
            String receiptRootVal = (String) blockResult.get("receiptsRoot");

            String logsBloomVal = (String) blockResult.get("logsBloom");
            String difficultyVal = (String) blockResult.get("difficulty");
            String gasLimitVal = (String) blockResult.get("gasLimit");
            String gasUsedHexVal = (String) blockResult.get("gasUsed");
            System.out.println(gasUsedHexVal);
            String timestampHexVal = (String) blockResult.get("timestamp");
            String extraDataVal = (String)blockResult.get("extraData");

            byte[] parentHash = RLP.encodeElement(ByteUtil.hexStringToBytes(parentHashVal));

            byte[] unclesHash = RLP.encodeElement(ByteUtil.hexStringToBytes(unclesHashVal));
            byte[] coinbase = RLP.encodeElement(ByteUtil.hexStringToBytes(coinbaseVal));

            byte[] stateRoot = RLP.encodeElement(ByteUtil.hexStringToBytes(stateRootVal));

            byte[] txTrieRoot = RLP.encodeElement(ByteUtil.hexStringToBytes(txTrieRootVal));
            byte[] receiptTrieRoot = RLP.encodeElement(ByteUtil.hexStringToBytes(receiptRootVal));

            byte[] logsBloom = RLP.encodeElement(ByteUtil.hexStringToBytes(logsBloomVal));
            //byte[] difficulty = RLP.encodeBigInteger(new BigInteger("f3a00",16));
            byte[] difficulty = RLP.encodeBigInteger(new BigInteger(1, new BigInteger(difficultyVal.substring(2), 16).toByteArray()));
            byte[] number = RLP.encodeBigInteger(BigInteger.valueOf(start));
            byte[] gasLimit = RLP.encodeElement(ByteUtil.hexStringToBytes(gasLimitVal));
            byte[] gasUsed = RLP.encodeBigInteger(new BigInteger(gasUsedHexVal.substring(2), 16));
            byte[] timestamp = RLP.encodeBigInteger(new BigInteger(timestampHexVal.substring(2), 16));

            byte[] extraData = RLP.encodeElement(ByteUtil.hexStringToBytes(extraDataVal));

            List<String> sealFields = (List<String>)blockResult.get("sealFields");
            String stepHex = sealFields.get(0);
            String signatureHex = sealFields.get(1);

            if (start == 0L) {
                System.out.println("## Step hex : " + stepHex);
                System.out.println("## Signature hex : " + signatureHex);
            }

            byte[] seal1 = RLP.encodeElement(ByteUtil.hexStringToBytes(stepHex));
            byte[] seal2 = RLP.encodeElement(ByteUtil.hexStringToBytes(signatureHex));

            byte[] encoded = RLP.encodeList(parentHash, unclesHash, coinbase,
                stateRoot, txTrieRoot, receiptTrieRoot, logsBloom, difficulty, number,
                gasLimit, gasUsed, timestamp, extraData, seal1, seal2);

            byte[] hashByte = HashUtil.sha3(encoded);

            // byte[] hash = HashUtil.sha3(header.getEncodedTemp2(ByteUtil.hexStringToBytes(signatureHex), step));

            String originHash = (String) blockResult.get("hash");
            String calcHash = "0x" + Hex.toHexString(hashByte);
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
    public void pow() {
        String expectedHash = "41800b5c3f1717687d85fc9018faac0a6e90b39deaa0b99e7fe4fe796ddeb26a";
        byte[] parentHash = RLP.encodeElement(ByteUtil.hexStringToBytes("0x41941023680923e0fe4d74a34bdac8141f2540e3ae90623718e47d66d1ca4a2d"));

        byte[] unclesHash = RLP.encodeElement(ByteUtil.hexStringToBytes("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347"));
        byte[] coinbase = RLP.encodeElement(ByteUtil.hexStringToBytes("0xd1aeb42885a43b72b518182ef893125814811048"));

        byte[] stateRoot = RLP.encodeElement(ByteUtil.hexStringToBytes("0xc7b01007a10da045eacb90385887dd0c38fcb5db7393006bdde24b93873c334b"));

        byte[] txTrieRoot = RLP.encodeElement(ByteUtil.hexStringToBytes("0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421"));
        byte[] receiptTrieRoot = RLP.encodeElement(ByteUtil.hexStringToBytes("0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421"));

        byte[] logsBloom = RLP.encodeElement(ByteUtil.hexStringToBytes("0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));
        //byte[] difficulty = RLP.encodeBigInteger(new BigInteger("f3a00",16));
        byte[] difficulty = RLP.encodeBigInteger(new BigInteger(1, new BigInteger("f3a00", 16).toByteArray()));
        byte[] number = RLP.encodeBigInteger(BigInteger.valueOf(1L));
        byte[] gasLimit = RLP.encodeElement(ByteUtil.hexStringToBytes("0xffc001"));
        byte[] gasUsed = RLP.encodeBigInteger(BigInteger.valueOf(0L));
        byte[] timestamp = RLP.encodeBigInteger(new BigInteger("58318da2", 16));

        byte[] extraData = RLP.encodeElement(ByteUtil.hexStringToBytes("0xd883010503846765746887676f312e372e318664617277696e"));


        byte[] seal1 = ByteUtil.hexStringToBytes("0x0f98b15f1a4901a7e9204f3c500a7bd527b3fb2c3340e12176a44b83e414a69e");
        byte[] seal2 = ByteUtil.hexStringToBytes("0x0ece08ea8c49dfd9");

        byte[] encoded = RLP.encodeList(parentHash, unclesHash, coinbase,
            stateRoot, txTrieRoot, receiptTrieRoot, logsBloom, difficulty, number,
            gasLimit, gasUsed, timestamp, extraData, RLP.encodeElement(seal1), RLP.encodeElement(seal2));

        byte[] hashByte = HashUtil.sha3(encoded);
        System.out.println("Expected : " + expectedHash);
        System.out.println("==>>");
        System.out.println(Hex.toHexString(hashByte));
        System.out.println("Equals : " + expectedHash.equals(Hex.toHexString(hashByte)));
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
            new BigInteger(1, new BigInteger(difficulty.substring(2), 16).toByteArray()).toByteArray(),   // difficulty
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
