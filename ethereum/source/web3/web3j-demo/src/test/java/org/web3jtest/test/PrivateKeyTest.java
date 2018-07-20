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
import org.web3jtest.util.GsonUtil;

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
        //initialize();
    }

    @Test
    public void test() throws Exception {
        String addr = "0x00d695cd9b0ff4edc8ce55b493aec495b597e235";
        System.out.println(keystore.get(addr));
    }

    @Test
    public void displayPretty() {
        String parity = "{\"id\":\"22930aae-7799-b70b-59bb-8313a1584107\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"0d7bb532c7a440fc3635df21dd2ad5d3\"},\"ciphertext\":\"5f3c06081a910a57e3ec045a633a8d78b491e8de13f6734ad3064b08ca2eef3d\",\"kdf\":\"pbkdf2\",\"kdfparams\":{\"c\":10240,\"dklen\":32,\"prf\":\"hmac-sha256\",\"salt\":\"67042d30a0741f8050a3df0305da54b6cde66ec4f2377e0ca263008b1bdf4eef\"},\"mac\":\"f8fb67184d39b70b1c36002836aef5c68e06bdbcfdc796435f507bb30cd22343\"},\"address\":\"00bd138abd70e2f00903268f3db08f2d25677c9e\",\"name\":\"\",\"meta\":\"{}\"}";
        String harmony = "{\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"e760567ece04c02826c6a5d7ff2435a969fcd9ee641708d1a01e4f609ac6e420\",\"kdf\":\"scrypt\",\"mac\":\"c689a124725c36e33227709eaaac4469865ac6a06d1c6c33c104c9de9cac8fd3\",\"cipherparams\":{\"iv\":\"d3bd88a4aede9366c1be4c0da7b5fbdc\"},\"kdfparams\":{\"dklen\":32,\"salt\":\"39fb7edf00542e55aae2e44d8ccde3e3966c307bbff9543484c8776d061c0aff\",\"n\":262144,\"p\":1,\"r\":8}},\"id\":\"f3653138-c5c8-45f3-9443-7be2e6f7e82d\",\"version\":3,\"address\":\"7979cf89fd186af3ada3c3cb1b7250d66f626694\"}";

        System.out.println("================== Parity ==================");
        System.out.println(GsonUtil.jsonStringToPretty(parity));
        System.out.println("============================================");
        System.out.println("================== Harmony =================");
        System.out.println(GsonUtil.jsonStringToPretty(harmony));
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
