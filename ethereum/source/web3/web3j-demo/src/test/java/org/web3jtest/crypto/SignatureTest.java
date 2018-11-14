package org.web3jtest.crypto;

import com.google.common.base.Charsets;
import java.nio.charset.Charset;
import java.security.SignatureException;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.ECKey.ECDSASignature;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.FastByteComparisons;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.web3j.protocol.parity.methods.response.VMTrace.VMOperation.Ex;
import org.web3j.utils.Numeric;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-13
 * @GitHub : https://github.com/zacscoding
 */
public class SignatureTest {

    @Test
    public void sig() throws Exception {
        ECKey ecKey = ECKey.fromPrivate(Numeric.hexStringToByteArray("01749d817d867d164b192e8a8563f806e68c5e79d3055eb6e3ca8bd3a033db26"));
        byte[] message = "Hello22".getBytes("UTF-8");
        byte[] messageSha = HashUtil.sha3(message);

        for(int i=0; i<5; i++) {
            ECKey.ECDSASignature sig = ecKey.sign(messageSha);
            System.out.println(sig.toHex());
        }
    }

    @Test
    public void signature() throws Exception {
        // 암호화
        ECKey encryptKey = new ECKey();
        byte[] privateKey = encryptKey.getPrivKeyBytes();
        byte[] addr = encryptKey.getAddress();

        byte[] message = "Hello Blockchain".getBytes("UTF-8");
        byte[] messageHash = HashUtil.sha3(message);

        ECKey.ECDSASignature signature = encryptKey.sign(messageHash);
        String signedHex = signature.toHex();

        SimpleLogger.println("addr : {}\nprivate key : {}\nmessage : {}\nmessage hash : {}\nsignature : {}\n\n"
        , Hex.toHexString(addr), Hex.toHexString(privateKey), message, Hex.toHexString(messageHash), signedHex);
    }

    @Test
    public void verifyTest() throws Exception {
        String signedHex = "c0497ddec2f7bd5ea76d57f891977b9845237cb67ce6632a32d9f6ae9f1bf2fb59dfd443e3f15e3392ee926d67353a0db7a0b75d0bbb5e95887d3306e09a468001";
        String messageHashHex = "d049f5ec1ec1a9d3806f37b4a8292f26bc8271515d8eea94553e69f4abd8c95e";
        String addrHex = "755ce08ce1e20e5a5eaad309bef05d795c0ce868";
        // byte[] signed = Numeric.hexStringToByteArray(signedHex);
        ECDSASignature signature = decodeSignature(signedHex);

        ECKey recover = ECKey.signatureToKey(Numeric.hexStringToByteArray(messageHashHex), signature);
        // boolean result = ECKey.verify(Numeric.hexStringToByteArray(messageHashHex), signature);
        // System.out.println(result);

        boolean result = recover.verify(Numeric.hexStringToByteArray(messageHashHex), signature);
        System.out.println(result);
    }

    @Test
    public void verify2Test() {
        String message = "Hello Blockchain";
        String address = "48308b23342f02590192e6f055aed715524ad6c3";
        String signature = "ddd79da4f80c99eb44b701cb797fe63c6365bca8b378aac7946320217f59ecc303b8e6ac0ac3948603a8e62d61c1abdcbc37cbb4a2be9a1b6a05b101f952b4a501";
        System.out.println(verify2(message, address, signature));
    }

    public boolean verify2(String message, String address, String signature) {
        try {
            byte[] messageSha = HashUtil.sha3(message.getBytes("UTF-8"));
            // byte[] sig = Hex.decode(Numeric.cleanHexPrefix(signature));
            // ECKey.ECDSASignature decodedSig = ECKey.ECDSASignature.decodeFromDER(sig);
            ECKey.ECDSASignature decodedSig = decodeSignature(signature);

            byte[] addr = ECKey.signatureToAddress(messageSha, decodedSig);
            byte[] addressByte = Hex.decode(Numeric.cleanHexPrefix(address));

            return FastByteComparisons.equal(addr, addressByte);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean verify(String message, String addr, String signature) throws SignatureException {
        byte[] messageHash = HashUtil.sha3(message.getBytes(Charsets.UTF_8));
        byte[] signatureByte = Numeric.hexStringToByteArray(signature);

        ECKey recover = ECKey.signatureToKey(messageHash, signature);
        return recover.verify(messageHash, signatureByte);
    }

    private ECKey.ECDSASignature decodeSignature(String signatureHex) {
        byte[] signature = Numeric.hexStringToByteArray(signatureHex);

        byte[] r = new byte[32];
        byte[] s = new byte[32];
        byte v = signature[64];

        if (v == 1) v = 28;
        if (v == 0) v = 27;

        System.arraycopy(signature, 0, r, 0, 32);
        System.arraycopy(signature, 32, s, 0, 32);

        return ECKey.ECDSASignature.fromComponents(r, s, v);
    }
}