package org.demo.net;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.UUID;
import java.util.stream.IntStream;
import org.demo.util.SimpleLogger;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.net.rlpx.discover.table.NodeEntry;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

/**
 *
 */
public class NodeDiscoveryTest {

    @Test
    public void printNodeId() {
        char ch = 'F';
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < 128; i++) {
            sb.append(ch);
        }
        System.out.println(sb);
    }

    @Test
    public void testDistance() {
        String id1 = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        // String id2 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        String id2 = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        byte[] h1 = Hex.decode(id1);
        byte[] h2 = Hex.decode(id2);

        byte[] hash = new byte[Math.min(h1.length, h2.length)];

        for (int i = 0; i < hash.length; i++) {
            hash[i] = (byte) (((int) h1[i]) ^ ((int) h2[i]));
        }

        System.out.println(NodeEntry.distance(h1, h2));

    }

    @Test
    public void testDistance2() {
        IntStream.range(1, 3).forEach(repeat -> {
            String n1 = generateRandomNodeId();
            String n2 = generateRandomNodeId();

            byte[] h1 = Hex.decode(n1);
            byte[] h2 = Hex.decode(n2);

            byte[] hash = new byte[Math.min(h1.length, h2.length)];

            for (int i = 0; i < hash.length; i++) {
                hash[i] = (byte) (((int) h1[i]) ^ ((int) h2[i]));
            }

            BigInteger hashDecimal = new BigInteger(Hex.toHexString(hash), 16);
            int distance = NodeEntry.distance(h1, h2);
            int left = (int) Math.pow(2, distance);
            int right = (int) Math.pow(2, distance + 1);
            SimpleLogger.println("hashDeciaml : {}", hashDecimal);
            SimpleLogger.println("distance : {}", distance);
            SimpleLogger.println("left : {}", left);
            SimpleLogger.println("right : {}", right);

            /*assertTrue(left <= hashDeciaml);
            assertTrue(hashDeciaml < right);*/
        });
    }

    private String generateRandomNodeId() {
        String pass = UUID.randomUUID().toString();
        final ECKey generatedNodeKey = ECKey.fromPrivate(HashUtil.sha3(pass.getBytes()));
        return Hex.toHexString(generatedNodeKey.getNodeId());
    }

}
