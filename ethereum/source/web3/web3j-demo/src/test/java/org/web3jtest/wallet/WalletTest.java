package org.web3jtest.wallet;

import static org.web3j.crypto.Hash.sha256;

import java.io.File;
import org.junit.Test;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3jtest.util.GsonUtil;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-07-22
 * @GitHub : https://github.com/zacscoding
 */
public class WalletTest {

    @Test
    public void genearteKeyFile() throws Exception {
        String password = "pass";
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
        GsonUtil.printGsonPretty(walletFile);

        ECKeyPair ecKeyPair2 = Keys.createEcKeyPair();
        WalletFile walletFile2 = Wallet.createLight(password, ecKeyPair2);
        GsonUtil.printGsonPretty(walletFile2);
    }

    @Test
    public void temp() throws Exception {
        String password = "test";
        File keyDirectory = new File("E:\\test");
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
}
