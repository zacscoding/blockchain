package org.web3jtest.test;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.ethereum.crypto.ECKey;
import org.junit.Before;
import org.junit.Test;
import org.keystore.KeystoreFormat;
import org.springframework.core.io.ClassPathResource;

/**
 * @author zacconding
 * @Date 2018-06-25
 * @GitHub : https://github.com/zacscoding
 */
public class PrivateKeyTest {

    Map<String, ECKey> keystore;
    Map<String, String> passwords;

    @Before
    public void setUp() throws Exception {
        initialize();
    }

    @Test
    public void test() throws Exception {
        String addr = "0x00d695cd9b0ff4edc8ce55b493aec495b597e235";
        System.out.println(keystore.get(addr));
    }

    private void initialize() throws Exception {
        KeystoreFormat keystoreFormat = new KeystoreFormat();
        keystore = new HashMap<>();
        passwords = new HashMap<>();

        List<Pair<String, String>> passwordPairs = Arrays.asList(
            Pair.of("0x00bd138abd70e2f00903268f3db08f2d25677c9e", "node0"),
            Pair.of("0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2", "node1"),
            Pair.of("0x002e28950558fbede1a9675cb113f0bd20912019", "node2"),
            Pair.of("0x00a94ac799442fb13de8302026fd03068ba6a428", "node3"),
            Pair.of("0x00d4f0e12020c15487b2a525abcb27de647c12de", "node4"),
            Pair.of("0x00d695cd9b0ff4edc8ce55b493aec495b597e235", "user1"),
            Pair.of("0x001ca0bb54fcc1d736ccd820f14316dedaafd772", "user2"),
            Pair.of("0x00cb25f6fd16a52e24edd2c8fd62071dc29a035c", "user3"),
            Pair.of("0x0046f91449e4b696d48c9dd10703cb589649c265", "user4"),
            Pair.of("0x00cc5a03e7166baa2df1d449430581d92abb0a1e", "user5"),
            Pair.of("0x0095e961b3a00f882326bbc8f0a469e5b56e858a", "user6"),
            Pair.of("0x0008fba8d298de8f6ea7385d447f4d3252dc0880", "user7"),
            Pair.of("0x0094bc2c3b585928dfeaf85e96ba57773c0673c1", "user8"),
            Pair.of("0x0002851146112cef5d360033758c470689b72ea7", "user9"),
            Pair.of("0x002227d6a35ed31076546159061bd5d3fefe9f0a", "user10")
        );

        for(Pair<String, String> passwordPair : passwordPairs) {
            String addr = passwordPair.getKey();
            if (addr.startsWith("0x")) {
                addr = addr.substring(2);
            }

            passwords.put(addr, passwordPair.getValue());
        }

        File dir = new ClassPathResource("keys/parity").getFile();

        for (String file : dir.list()) {
            if (!file.startsWith("UTC")) {
                continue;
            }

            String content = Files.readAllLines(new File(dir, file).toPath()).stream().collect(Collectors.joining(""));
            int addrIndex = content.indexOf("address");
            String addr = content.substring(addrIndex + 10, addrIndex + 50);
            String password = passwords.get(addr);
            ECKey eckey = keystoreFormat.fromKeystore(content, password);

            keystore.put(addr, eckey);
        }
    }
}
