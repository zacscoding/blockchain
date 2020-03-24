package org.demo;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.crypto.SignatureDataOperations;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.parity.methods.response.VMTrace.VMOperation.Ex;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

/**
 *
 */
public class Temp {

    @Test
    public void decodeTx() throws Exception {
        String rawTxHex = "0xf8690280829c4094f92804a85f703131d235afff98792ab091f6fc4a8447868c00800000845cb7e505a0433a712b6668912643c557c84734ef77256d64bdec32e430fa3b515ce6898a78a05d335b3384dbd2d3c1541974201dea4c4b283c80d8f94933dc252852bacd6425";
        TxDecoder.decode(rawTxHex);
    }

    @Test
    public void signTx() throws Exception {
        // curl --data '{"jsonrpc":"2.0","method":"berith_getBalance","params":["Bxd8a25ff31c6174ce7bce74ca4a91c2e816dbf91e", "latest"],"id":1}' -H "Content-Type: application/json" -X POST localhost:8545
        // curl --data '{"jsonrpc":"2.0","method":"berith_getTransactionCount","params":["Bxd8a25ff31c6174ce7bce74ca4a91c2e816dbf91e", "latest"],"id":1}' -H "Content-Type: application/json" -X POST localhost:8545
        String from = "d8a25ff31c6174ce7bce74ca4a91c2e816dbf91e";
        String to = "bb926bbb0b15ca54d4a19dcdf44fc8940e3f6da3";

        Credentials credentials = WalletUtils.loadCredentials("pass", new File(
            "/home/zaccoding/workspaces/berith/node1/keystore/UTC--2019-09-16T01-44-39.776266747Z--d8a25ff31c6174ce7bce74ca4a91c2e816dbf91e"));

        RawTx rawTx = new RawTx(
            new BigInteger("1cea", 16),// nonce
            BigInteger.ONE, // gasPrice
            new BigInteger("5208", 16), // gasLimit
            to, // to
            BigInteger.ONE, // value
            "0x", //data
            0, // main
            0  // target
        );

        byte[] signedMessage = RawTxEncoder.signMessage(rawTx, 36435, credentials);
        String rawTxHash = Numeric.toHexString(signedMessage);
        System.out.println(rawTxHash);
        System.out.println("curl --data '{\"jsonrpc\":\"2.0\",\"method\":\"berith_sendRawTransaction\",\"params\":[\""
            + rawTxHash
            + "\"],\"id\":1}' -H \"Content-Type: application/json\" -X POST localhost:8545");
        // curl --data '{"jsonrpc":"2.0","method":"berith_sendRawTransaction","params":["0xf864821ce90182520894bb926bbb0b15ca54d4a19dcdf44fc8940e3f6da3018081caa0e6b88a0546d925be5fa1b6bfdb2fc9dba954063951ab6bb7556d478c521e87c4a0401ae6cbdf1670e0edbdedcd02eb1947ea4a8881537a91ac48d2fb0a5dc346310101"],"id":1}' -H "Content-Type: application/json" -X POST localhost:8545
    }


    public static class RawTx {

        private BigInteger nonce;
        private BigInteger gasPrice;
        private BigInteger gasLimit;
        private String to;
        private BigInteger value;
        private String data;
        private int base;
        private int target;

        public RawTx(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data, int base, int target) {
            this.nonce = nonce;
            this.gasPrice = gasPrice;
            this.gasLimit = gasLimit;
            this.to = to;
            this.value = value;

            if (data != null) {
                this.data = Numeric.cleanHexPrefix(data);
            }

            this.base = base;
            this.target = target;
        }

        public BigInteger getNonce() {
            return nonce;
        }

        public BigInteger getGasPrice() {
            return gasPrice;
        }

        public BigInteger getGasLimit() {
            return gasLimit;
        }

        public String getTo() {
            return to;
        }

        public BigInteger getValue() {
            return value;
        }

        public String getData() {
            return data;
        }

        public int getBase() {
            return base;
        }

        public int getTarget() {
            return target;
        }
    }

    public static class RawTxEncoder {

        private static final int CHAIN_ID_INC = 35;
        private static final int LOWER_REAL_V = 27;

        public static byte[] signMessage(RawTx rawTransaction, Credentials credentials) {
            byte[] encodedTransaction = encode(rawTransaction);
            Sign.SignatureData signatureData =
                Sign.signMessage(encodedTransaction, credentials.getEcKeyPair());

            return encode(rawTransaction, signatureData);
        }

        public static byte[] signMessage(RawTx rawTransaction, long chainId, Credentials credentials) {
            byte[] encodedTransaction = encode(rawTransaction, chainId);
            Sign.SignatureData signatureData =
                Sign.signMessage(encodedTransaction, credentials.getEcKeyPair());
            Sign.SignatureData eip155SignatureData = createEip155SignatureData(signatureData, chainId);

            return encode(rawTransaction, eip155SignatureData);
        }

        @Deprecated
        public static byte[] signMessage(RawTx rawTransaction, byte chainId, Credentials credentials) {
            return signMessage(rawTransaction, (long) chainId, credentials);
        }

        public static Sign.SignatureData createEip155SignatureData(
            Sign.SignatureData signatureData, long chainId) {
            BigInteger v = Numeric.toBigInt(signatureData.getV());
            v = v.subtract(BigInteger.valueOf(LOWER_REAL_V));
            v = v.add(BigInteger.valueOf(chainId * 2));
            v = v.add(BigInteger.valueOf(CHAIN_ID_INC));

            return new Sign.SignatureData(v.toByteArray(), signatureData.getR(), signatureData.getS());
        }

        @Deprecated
        public static Sign.SignatureData createEip155SignatureData(
            Sign.SignatureData signatureData, byte chainId) {
            return createEip155SignatureData(signatureData, (long) chainId);
        }

        public static byte[] encode(RawTx rawTransaction) {
            return encode(rawTransaction, null);
        }

        public static byte[] encode(RawTx rawTransaction, long chainId) {
            Sign.SignatureData signatureData =
                new Sign.SignatureData(longToBytes(chainId), new byte[]{}, new byte[]{});
            return encode(rawTransaction, signatureData);
        }

        @Deprecated
        public static byte[] encode(RawTx rawTransaction, byte chainId) {
            return encode(rawTransaction, (long) chainId);
        }

        private static byte[] encode(RawTx rawTransaction, Sign.SignatureData signatureData) {
            List<RlpType> values = asRlpValues(rawTransaction, signatureData);
            RlpList rlpList = new RlpList(values);
            return RlpEncoder.encode(rlpList);
        }

        private static byte[] longToBytes(long x) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(x);
            return buffer.array();
        }

        public static List<RlpType> asRlpValues(RawTx rawTransaction, Sign.SignatureData signatureData) {
            List<RlpType> result = new ArrayList<>();

            result.add(RlpString.create(rawTransaction.getNonce()));
            result.add(RlpString.create(rawTransaction.getGasPrice()));
            result.add(RlpString.create(rawTransaction.getGasLimit()));

            // an empty to address (contract creation) should not be encoded as a numeric 0 value
            String to = rawTransaction.getTo();
            if (to != null && to.length() > 0) {
                // addresses that start with zeros should be encoded with the zeros included, not
                // as numeric values
                result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
            } else {
                result.add(RlpString.create(""));
            }

            result.add(RlpString.create(rawTransaction.getValue()));

            // value field will already be hex encoded, so we need to convert into binary first
            byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
            result.add(RlpString.create(data));

            // result.add(RlpString.create((byte) rawTransaction.getBase()));
            // result.add(RlpString.create((byte) rawTransaction.getTarget()));
            result.add(RlpString.create("" + rawTransaction.getBase()));
            result.add(RlpString.create("" + rawTransaction.getTarget()));

            if (signatureData != null) {
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getV())));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));

                System.out.println("r : " + Numeric.toHexString(signatureData.getR()));
                System.out.println("s : " + Numeric.toHexString(signatureData.getS()));
                System.out.println("v : " + Numeric.toHexString(signatureData.getV()));
            }

            return result;
        }
    }

    public static class SignedRawTx extends RawTransaction implements SignatureDataOperations {

        private final SignatureData signatureData;

        public SignedRawTx(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data,
            SignatureData signatureData) {
            super(nonce, gasPrice, gasLimit, to, value, data);
            this.signatureData = signatureData;
        }

        public SignatureData getSignatureData() {
            return this.signatureData;
        }

        public byte[] getEncodedTransaction(Long chainId) {
            return null == chainId ? TransactionEncoder.encode(this) : TransactionEncoder.encode(this, chainId);
        }
    }

    public static class TxDecoder {

        public TxDecoder() {
        }

        public static RawTx decode(String hexTransaction) {
            byte[] transaction = Numeric.hexStringToByteArray(hexTransaction);
            RlpList rlpList = RlpDecoder.decode(transaction);
            RlpList values = (RlpList) rlpList.getValues().get(0);
            BigInteger nonce = ((RlpString) values.getValues().get(0)).asPositiveBigInteger();
            BigInteger gasPrice = ((RlpString) values.getValues().get(1)).asPositiveBigInteger();
            BigInteger gasLimit = ((RlpString) values.getValues().get(2)).asPositiveBigInteger();
            String to = ((RlpString) values.getValues().get(3)).asString();
            BigInteger value = ((RlpString) values.getValues().get(4)).asPositiveBigInteger();
            String data = ((RlpString) values.getValues().get(5)).asString();
            String base = ((RlpString) values.getValues().get(6)).asString();
            String target = ((RlpString) values.getValues().get(7)).asString();

            System.out.println("## Base : " + base);
            System.out.println("## Target : " + target);

            return null;
        }
    }

}
