package org.demo.eth;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.Test;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;
import org.demo.AbstractTestRunner;
import org.demo.util.FindKeyFile;

/**
 * @author zacconding
 * @Date 2018-10-19
 * @GitHub : https://github.com/zacscoding
 */
public class Web3jTransactionTest extends AbstractTestRunner {

    private String keyDir = "C:\\git\\zaccoding\\blockchain\\ethereum\\source\\parity\\default-keys\\default-keys\\default-keys\\keys";

    @Test
    public void createSendTx() throws IOException, CipherException {
        // BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, BigInteger value, String init
        RawTransaction rawTransaction = RawTransaction.createContractTransaction(
            BigInteger.ZERO, BigInteger.ZERO, BigInteger.valueOf(109392), BigInteger.ZERO,
            "0x60c0604052600660808190527f68656c6c6f3f000000000000000000000000000000000000000000000000000060a090815261003e9160009190610051565b5034801561004b57600080fd5b506100ec565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b6100e991905b808211156100cb57600081556001016100d5565b90565b6102a7806100fb6000396000f30060806040526004361061004b5763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663615ea8998114610050578063ef5fb05b146100ab575b600080fd5b34801561005c57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100a99436949293602493928401919081908401838280828437509497506101359650505050505050565b005b3480156100b757600080fd5b506100c061014c565b6040805160208082528351818301528351919283929083019185019080838360005b838110156100fa5781810151838201526020016100e2565b50505050905090810190601f1680156101275780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b80516101489060009060208401906101e3565b5050565b60008054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156101d85780601f106101ad576101008083540402835291602001916101d8565b820191906000526020600020905b8154815290600101906020018083116101bb57829003601f168201915b505050505090505b90565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061022457805160ff1916838001178555610251565b82800160010185558215610251579182015b82811115610251578251825591602001919060010190610236565b5061025d929150610261565b5090565b6101e091905b8082111561025d57600081556001016102675600a165627a7a72305820180f3a92c171b1a41aa2c362464dd71a265aef5330eaf0601d70c443125d94500029"
        );

        String password = "user1";
        File keyFile = FindKeyFile
            .findKeyFile(keyDir, content -> content.contains("00d695cd9b0ff4edc8ce55b493aec495b597e235"));
        Credentials credentials = WalletUtils.loadCredentials(password, keyFile);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String rawTx = Numeric.toHexString(signedMessage);

        EthSendTransaction txResult = web3j.ethSendRawTransaction(rawTx).send();
        System.out.println(txResult.getTransactionHash());
    }

    @Test
    public void createTx2() throws Exception {
        String privateKey = "Private key..";
        Credentials credentials = Credentials.create(privateKey);

        RawTransaction rawTransaction = RawTransaction.createTransaction(
            new BigInteger("13", 16),
            new BigInteger("0", 16),
            new BigInteger("e57e0", 16),
            "0x00a43494672eac4ea96e33c8fa049e019e5b0ed9",
            new BigInteger("DE0B6B3A7640000", 16),
            "0x"
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        System.out.println(signedMessage);
        System.out.println(Numeric.toHexString(signedMessage));
    }
}
