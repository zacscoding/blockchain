package org.web3jtest;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.FastByteComparisons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Numeric;

/**
 * @author zacconding
 * @Date 2018-11-13
 * @GitHub : https://github.com/zacscoding
 */
@RestController
@RequestMapping("/signature/**")
public class SignatureController {

    private static final Logger logger = LoggerFactory.getLogger(SignatureController.class);

    /**
     * - request
     * {
     *      "privateKey" : "e7b4fab756fd58adac20138dd6060c1172a96835b41411e2f2e2f53e06ff62ef",
     *      "msg" : "Something message"
     * }
     *
     * - response
     * {
     *     "address" : "0x755ce08ce1e20e5a5eaad309bef05d795c0ce868",
     *     "msg" : "Something message",
     *     "sig" : "0xaa9ef2cd4f747cb40f38b48ff6a7bc5809da17216699383147c5f7b2e398209a20dfda322f06a3a646bf49bc90b141df9785a18b0048a83aad7160814f609a271b",
     * }
     */
    @PostMapping(value = "sign")
    public ResponseEntity<Map<String, String>> signMessage(@RequestBody Map<String, Object> request) {
        try {
            String privateKey = (String) request.get("privateKey");
            String message = (String) request.get("msg");

            Map<String, String> result = new HashMap<>();

            ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
            byte[] messageHash = HashUtil.sha3(message.getBytes(Charset.forName("UTF-8")));

            ECKey.ECDSASignature signature = ecKey.sign(messageHash);

            result.put("address", Numeric.prependHexPrefix(Hex.toHexString(ecKey.getAddress())));
            result.put("msg", message);
            result.put("sig", Numeric.prependHexPrefix(signature.toHex()));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.warn("Failed to sign message", e);
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * - request
     * {
     *   "address": "0x755ce08ce1e20e5a5eaad309bef05d795c0ce868",
     *   "msg": "adsfadsfasdf",
     *   "sig": "0xaa9ef2cd4f747cb40f38b48ff6a7bc5809da17216699383147c5f7b2e398209a20dfda322f06a3a646bf49bc90b141df9785a18b0048a83aad7160814f609a271b"
     * }
     *
     * - response
     */
    @PostMapping(value = "verify")
    public ResponseEntity<Boolean> verify(@RequestBody Map<String, Object> request) {
        try {
            String address = (String) request.get("address");
            String signature = (String) request.get("sig");
            String message = (String) request.get("msg");

            ECKey.ECDSASignature decodedSig = decodeSignature(signature);

            byte[] messageSha = HashUtil.sha3(message.getBytes(Charset.forName("UTF-8")));

            byte[] addr = ECKey.signatureToAddress(messageSha, decodedSig);
            byte[] requestAddr = Numeric.hexStringToByteArray(address);

            return ResponseEntity.ok(FastByteComparisons.equal(addr, requestAddr));
        } catch (Exception e) {
            logger.warn("Failed to verify.", e);
            return ResponseEntity.badRequest().build();
        }
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