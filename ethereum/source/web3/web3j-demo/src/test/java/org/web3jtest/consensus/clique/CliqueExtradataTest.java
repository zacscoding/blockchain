package org.web3jtest.consensus.clique;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.transform.Result;
import org.apache.commons.lang3.tuple.Pair;
import org.ethereum.core.Block;
import org.ethereum.core.BlockHeader;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.ECKey.ECDSASignature;
import org.ethereum.crypto.HashUtil;
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
//        result
//        Block number : 1 | signer addr : 0xf4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb
//        0xd683010811846765746886676f312e3130856c696e757800000000000000000075b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e0275561b0b54dea388203d05b00bc78a3d3fc019b4511ca58ef1eb01cd034c5b00
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 75b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e0275561b0b54dea388203d05b00bc78a3d3fc019b4511ca58ef1eb01cd034c5b00
//
//        Block number : 2 | signer addr : 0xf6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8
//        0xd683010811846765746886676f312e3130856c696e75780000000000000000007c58513d36fb22c4a81d98833071113b249b8aa60cd9486efdad951ea13959aa639551464297a7f51508458397d419174ad1fb73cb326d6fcb096a2e92237c7901
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 7c58513d36fb22c4a81d98833071113b249b8aa60cd9486efdad951ea13959aa639551464297a7f51508458397d419174ad1fb73cb326d6fcb096a2e92237c7901
//
//        Block number : 3 | signer addr : 0x55c2a4991130a280a34cb8e73d36eeedc5a10ca9
//        0xd683010811846765746886676f312e3130856c696e75780000000000000000006267ccdab1047b8ff448e3e5e339c4a091b88b96e7991d86a373af091645f95e4e3195e2d81a40e43bb476ce0ebc7462d239f9c603ecd1d443e9d976b2c59a1301
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 6267ccdab1047b8ff448e3e5e339c4a091b88b96e7991d86a373af091645f95e4e3195e2d81a40e43bb476ce0ebc7462d239f9c603ecd1d443e9d976b2c59a1301
//
//        Block number : 4 | signer addr : 0xf4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb
//        0xd683010811846765746886676f312e3130856c696e75780000000000000000006d75e4557a52f4e322a2b5c076133fcd271330067b326abebfa3fc7a16966be04642fdd89eb34a590e84ac807c0cd6281a118d5dec2c3100ce97d03ee7541c1200
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : 6d75e4557a52f4e322a2b5c076133fcd271330067b326abebfa3fc7a16966be04642fdd89eb34a590e84ac807c0cd6281a118d5dec2c3100ce97d03ee7541c1200
//
//        Block number : 5 | signer addr : 0xf6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8
//        0xd683010811846765746886676f312e3130856c696e7578000000000000000000fcb9d86c60e723c854acb232d89f2fe63603f9e250b70034846dc64a9628d4d375d09560e9ee7289823183164a8e3db9090159be070be9c6d4e09375364e323700
//        Vanity : d683010811846765746886676f312e3130856c696e7578000000000000000000
//        Seal : fcb9d86c60e723c854acb232d89f2fe63603f9e250b70034846dc64a9628d4d375d09560e9ee7289823183164a8e3db9090159be070be9c6d4e09375364e323700
    }

    @Test
    public void signExtraSeal() {
        BlockHeader header = new BlockHeader(
            Numeric.hexStringToByteArray("0xa03c5c86476b69b8e39b9b277441cdb663014515b3e9fea89a654ab9173416b2") // parent hash
            ,Numeric.hexStringToByteArray("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347") // unclesHash
            ,Numeric.hexStringToByteArray("0x0000000000000000000000000000000000000000") // coinbase
            ,Numeric.hexStringToByteArray("0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000") // logs bloom
            ,Numeric.hexStringToByteArray("0x2") // difficulty
            , 1L    // number
            ,Numeric.hexStringToByteArray("0x47c94c") // gasLimit
            ,0L // gasUsed
            ,new BigInteger("5bcf0654", 16).longValue() // timestamp
            ,Numeric.hexStringToByteArray("0xd683010811846765746886676f312e3130856c696e7578000000000000000000") // extraData
            //,Numeric.hexStringToByteArray("0xd683010811846765746886676f312e3130856c696e757800000000000000000075b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e0275561b0b54dea388203d05b00bc78a3d3fc019b4511ca58ef1eb01cd034c5b00") // extraData
            ,Numeric.hexStringToByteArray("0x0000000000000000000000000000000000000000000000000000000000000000") // mixHash
            ,Numeric.hexStringToByteArray("0x0000000000000000") // nonce
        );
        header.setStateRoot(Numeric.hexStringToByteArray("0x124ad94289a573ed03b0f48c57a544dfe8141dcdf1d79de5ef85c6aafcd65a04"));

        String privateKey = "bb9969d37683b5d5fe26e51e6bf5ecb7eb1429ac05c4ca9d70c69bf1f09496dc";
        ECKey key = ECKey.fromPrivate(Numeric.hexStringToByteArray(privateKey));
        String signed = key.sign(header.getHash()).toHex();
        String hexSealField = "75b46e00b38dbe35301804d18b0bed0a1f9ee433fc63ce3e7fcd7fa5725b608e0275561b0b54dea388203d05b00bc78a3d3fc019b4511ca58ef1eb01cd034c5b00";

        assertTrue(signed.equals(hexSealField));
    }

    private String getBlockByNumber(String url, String hexBlockNumber, boolean includeTx) {
        return JsonRpcHttpService.request(url, "eth_getBlockByNumber", null, Numeric.prependHexPrefix(hexBlockNumber), includeTx);
    }

    private ECKey.ECDSASignature decodeSignature(String seal) {
        byte[] signature = Numeric.hexStringToByteArray(seal);

        byte[] r = new byte[32];
        byte[] s = new byte[32];
        byte v = signature[64];

        if (v == 1) v = 28;
        if (v == 0) v = 27;

        System.arraycopy(signature, 0, r, 0, 32);
        System.arraycopy(signature, 32, s, 0, 32);

        return ECKey.ECDSASignature.fromComponents(r, s, v);
    }

    /////////////////////////////////////// TEMP CODES  ///////////////////////////////////////
}