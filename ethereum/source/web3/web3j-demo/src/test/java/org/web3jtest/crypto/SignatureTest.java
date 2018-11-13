package org.web3jtest.crypto;

import com.google.common.base.Charsets;
import java.security.SignatureException;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.ECKey.ECDSASignature;
import org.ethereum.crypto.HashUtil;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-13
 * @GitHub : https://github.com/zacscoding
 */
public class SignatureTest {

    @Test
    public void signature() throws Exception {
        // 암호화
        ECKey encryptKey = new ECKey();
        byte[] privateKey = encryptKey.getPrivKeyBytes();
        byte[] addr = encryptKey.getAddress();

        byte[] message = "Hello Blockchain".getBytes();
        byte[] messageHash = HashUtil.sha3(message);

        ECKey.ECDSASignature signature = encryptKey.sign(messageHash);
        String signedHex = signature.toHex();

        SimpleLogger.println("addr : {}\nprivate key : {}\nmessage : {}\nmessage hash : {}\nsignature : {}\n\n"
        , Hex.toHexString(addr), Hex.toHexString(privateKey), message, Hex.toHexString(messageHash), signedHex);

        // 복호화
        ECKey.ECDSASignature recoverSignature = decodeSignature(signedHex);
        ECKey key = ECKey.recoverFromSignature(0, recoverSignature, messageHash);
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