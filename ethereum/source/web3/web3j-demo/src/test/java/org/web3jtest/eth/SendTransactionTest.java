package org.web3jtest.eth;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.junit.Before;
import org.junit.Test;
import org.keystore.KeystoreFormat;
import org.spongycastle.util.encoders.Hex;
import org.springframework.core.io.ClassPathResource;
import org.web3jtest.AbstractTestRunner;

/**
 * @author zacconding
 * @Date 2018-06-01
 * @GitHub : https://github.com/zacscoding
 */
public class SendTransactionTest extends AbstractTestRunner {

    private Map<String, String> passwordMap;
    private Map<String, ECKey> ecKeyMap;

    @Test
    public void test() throws Exception {
        initialize();
        String addr = "a78551fd36be52f0829afc6b9843d74119780c16";

        final Transaction tx = new Transaction(
            ByteUtil.hexStringToBytes("0x0"),   // nonce
            ByteUtil.hexStringToBytes("0x123"),   // gasPrice
            ByteUtil.hexStringToBytes("0xe57e0"),   // gas
            ByteUtil.hexStringToBytes("7979cf89fd186af3ada3c3cb1b7250d66f626694"),    // to
            ByteUtil.hexStringToBytes("0x3b9aca00"),    //value
            new byte[0],   // data
            8995    // chain id
        );

        System.out.println(ecKeyMap.get(addr));
        tx.sign(ecKeyMap.get(addr));

        String raw = Hex.toHexString(tx.getEncoded());
        System.out.println("0x" + raw);
    }

    private void initialize() throws Exception {
        KeystoreFormat keystoreFormat = new KeystoreFormat();

        passwordMap = new HashMap<>();
        ecKeyMap = new HashMap<>();
        passwordMap.put("7979cf89fd186af3ada3c3cb1b7250d66f626694", "user1");
        passwordMap.put("a78551fd36be52f0829afc6b9843d74119780c16", "user2");

        File dir = new ClassPathResource("keys/harmony").getFile();

        for (String file : dir.list()) {
            if (!file.startsWith("UTC")) {
                continue;
            }

            String content = Files.readAllLines(new File(dir, file).toPath()).stream().collect(Collectors.joining(""));
            int dashIdx = file.lastIndexOf('-');
            String addr = file.substring(dashIdx + 1);
            String password = passwordMap.get(addr);

            ECKey eckey = keystoreFormat.fromKeystore(content, password);

            ecKeyMap.put(addr, eckey);
        }
    }
}
