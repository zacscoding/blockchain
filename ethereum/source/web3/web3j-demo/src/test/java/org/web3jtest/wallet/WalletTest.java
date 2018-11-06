package org.web3jtest.wallet;

import static org.web3j.crypto.Hash.sha256;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.ethereum.crypto.ECKey;
import org.junit.Test;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;
import org.web3jtest.util.GsonUtil;

/**
 * @author zacconding
 * @Date 2018-07-22
 * @GitHub : https://github.com/zacscoding
 */
public class WalletTest {

    @Test
    public void genearteKeyFile() throws Exception {
        String password = "pass";
        ECKeyPair ecKeyPair = org.web3j.crypto.Keys.createEcKeyPair();
        ecKeyPair.getPrivateKey();

        WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
        GsonUtil.printGsonPretty(walletFile);

        ECKeyPair ecKeyPair2 = Keys.createEcKeyPair();
        WalletFile walletFile2 = Wallet.createLight(password, ecKeyPair2);
        GsonUtil.printGsonPretty(walletFile2);
    }

    @Test
    public void temp() throws Exception {
        String password = "test";
        File keyDirectory = new File("F:\\test");
        String keyfile = WalletUtils.generateNewWalletFile(new String(password), keyDirectory, true);

        System.out.println("Key file: " + keyfile);
        File keyFile = new File(keyDirectory, keyfile);
        Credentials credentials = WalletUtils.loadCredentials(new String(password), keyFile);
        String address = credentials.getAddress();
        System.out.println("Address: " + address);
        String checksumAddress = Keys.toChecksumAddress(address);
        System.out.println("Address (checksum): " + checksumAddress);
    }

    @Test
    public void testGenerateBip39Wallets() throws Exception {
        String PASSWORD = "pass";
        File tempDir = new File("E:\\test");
        Bip39Wallet wallet = WalletUtils.generateBip39Wallet(PASSWORD, tempDir);
        byte[] seed = MnemonicUtils.generateSeed(wallet.getMnemonic(), PASSWORD);
        Credentials credentials = Credentials.create(ECKeyPair.create(sha256(seed)));

        System.out.println(credentials.getAddress());
    }

    @Test
    public void loadParityContent() throws Exception {
        String content = "{\"id\":\"06013340-6068-e53d-b2c7-d699e43bb62d\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"cipherparams\":{\"iv\":\"3d5bd1b8fcfaebc021f821acea788438\"},\"ciphertext\":\"e094b1037729fcabfb1ae95242ee768cba1dd761cb613447ae6b872c9b5ecee4\",\"kdf\":\"pbkdf2\",\"kdfparams\":{\"c\":10240,\"dklen\":32,\"prf\":\"hmac-sha256\",\"salt\":\"c961a99a534b81a5a5e53835af592dcee70f89a36446bd769b4900a12e35cc09\"},\"mac\":\"ef5b1c8a535ccc55a89bf308ccd8d16f215f952d8d6e9314e8fc5df26430e229\"},\"address\":\"00d695cd9b0ff4edc8ce55b493aec495b597e235\",\"name\":\"\",\"meta\":\"{}\"}";

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            WalletFile walletFile = objectMapper.readValue(content, WalletFile.class);
            Credentials credentials = Credentials.create(Wallet.decrypt("user1", walletFile));
            System.out.println("Addr : " + credentials.getAddress());
            System.out.println("Private key : " + credentials.getEcKeyPair().getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ECKey key = new ECKey();

    }
}
