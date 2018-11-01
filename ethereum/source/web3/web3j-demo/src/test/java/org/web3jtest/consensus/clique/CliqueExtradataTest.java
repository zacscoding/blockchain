package org.web3jtest.consensus.clique;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.transform.Result;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.PrecompiledContracts.ECRecover;
import org.ethereum.vm.PrecompiledContracts.Sha256;
import org.junit.Before;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.util.JsonUtil;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-01
 * @GitHub : https://github.com/zacscoding
 */
public class CliqueExtradataTest {

    String[] signers;

    @Before
    public void setUp() {
        signers = new String[] {
            "0xf4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb"
            ,"0x55c2a4991130a280a34cb8e73d36eeedc5a10ca9"
            ,"0xf6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8"
        };
        Arrays.sort(signers, Comparator.comparing(o -> new BigInteger(Numeric.cleanHexPrefix(o), 16)));
    }

    @Test
    public void displayExtradata() throws IOException {
        String url = "http://192.168.5.78:8501";
        /*
        for (int i = 1; i <= 5; i++) {
            String blockInfo = getBlockByNumber(url, BigInteger.valueOf(i).toString(16), true);
            String difficulty = JsonUtil.findValue(blockInfo, "result.difficulty");
            if (!"0x2".equals(difficulty)) {
                continue;
            }

            String extradataHex = JsonUtil.findValue(blockInfo, "result.extraData");
            byte[] extraData = Numeric.hexStringToByteArray(extradataHex);

            byte[] extraVanity = new byte[32];
            byte[] extraSeal = new byte[65];

            // Object src, int  srcPos, Object dest, int destPos, int length
            System.arraycopy(extraData, 0,extraVanity, 0, 32);
            System.arraycopy(extraData, extraData.length - 65, extraSeal, 0, 65);

            // return toHexString(input, 0, input.length, true);
            int expectedSigner = i % signers.length;
            SimpleLogger.println("Block number : {} | signer addr : {}\n{}\nVanity : {}\nSeal : {}\n"
            , i, signers[expectedSigner]
            , extradataHex
            , Numeric.toHexString(extraVanity, 0, extraVanity.length, false)
            , Numeric.toHexString(extraSeal, 0, extraVanity.length, false));
        }
        */
//        result
//        Block number : 1 | signer addr : 0xf4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb
//        0xd683010811846765746886676f312e3130856c696e757800000000000000000075b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e0275561b0b54dea388203d05b00bc78a3d3fc019b4511ca58ef1eb01cd034c5b00
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 75b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e
//
//        Block number : 2 | signer addr : 0xf6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8
//        0xd683010811846765746886676f312e3130856c696e75780000000000000000007c58513d36fb22c4a81d98833071113b249b8aa60cd9486efdad951ea13959aa639551464297a7f51508458397d419174ad1fb73cb326d6fcb096a2e92237c7901
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 7c58513d36fb22c4a81d98833071113b249b8aa60cd9486efdad951ea13959aa
//
//        Block number : 3 | signer addr : 0x55c2a4991130a280a34cb8e73d36eeedc5a10ca9
//        0xd683010811846765746886676f312e3130856c696e75780000000000000000006267ccdab1047b8ff448e3e5e339c4a091b88b96e7991d86a373af091645f95e4e3195e2d81a40e43bb476ce0ebc7462d239f9c603ecd1d443e9d976b2c59a1301
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 6267ccdab1047b8ff448e3e5e339c4a091b88b96e7991d86a373af091645f95e
//
//        Block number : 4 | signer addr : 0xf4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb
//        0xd683010811846765746886676f312e3130856c696e75780000000000000000006d75e4557a52f4e322a2b5c076133fcd271330067b326abebfa3fc7a16966be04642fdd89eb34a590e84ac807c0cd6281a118d5dec2c3100ce97d03ee7541c1200
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 6d75e4557a52f4e322a2b5c076133fcd271330067b326abebfa3fc7a16966be0
//
//        Block number : 5 | signer addr : 0xf6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8
//        0xd683010811846765746886676f312e3130856c696e7578000000000000000000fcb9d86c60e723c854acb232d89f2fe63603f9e250b70034846dc64a9628d4d375d09560e9ee7289823183164a8e3db9090159be070be9c6d4e09375364e323700
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : fcb9d86c60e723c854acb232d89f2fe63603f9e250b70034846dc64a9628d4d3

        Set<String> sealSet = new HashSet<>();
        for (int i = 1; i <= 1; i++) {
            String blockInfo = getBlockByNumber(url, BigInteger.valueOf(i).toString(16), true);
            String difficulty = JsonUtil.findValue(blockInfo, "result.difficulty");
            if (!"0x2".equals(difficulty)) {
                continue;
            }

            String extradataHex = JsonUtil.findValue(blockInfo, "result.extraData");
            byte[] extraData = Numeric.hexStringToByteArray(extradataHex);
            byte[] extraSeal = new byte[65];

            int expectedSigner = i % signers.length;
            System.arraycopy(extraData, extraData.length - 65, extraSeal, 0, 65);
            String sealHex =  Numeric.toHexString(extraSeal, 0, extraSeal.length, false);
            sealSet.add(sealHex);
            SimpleLogger.println("{}\n{}\n\n", signers[expectedSigner], sealHex);
            System.out.println("Recover :: " + Hex.toHexString(ECKey.fromPublicOnly(extraSeal).getAddress()));
        }
        System.out.println(">>> Check seal size : " + sealSet.size());
    }

    private String getBlockByNumber(String url, String hexBlockNumber, boolean includeTx) {
        return JsonRpcHttpService.request(url, "eth_getBlockByNumber", null, Numeric.prependHexPrefix(hexBlockNumber), includeTx);
    }

    @Test
    public void checkByte() {
        String hex = "";
        System.out.println(Numeric.hexStringToByteArray(hex).length);
    }

    @Test
    public void recover() {
        String sig = "75b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e0275561b0b54dea388203d05b00bc78a3d3fc019b4511ca58ef1eb01cd034c5b00";
    }
}