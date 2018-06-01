package org.web3jtest.eth;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.LogLevelUtil;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
public class AccountTest extends AbstractTestRunner {

    @Test
    public void createAccounts() throws Exception {
        LogLevelUtil.setInfo();
        int size = 20000;
        IntStream.range(1, size).forEach(i-> {
            try {
                admin.personalNewAccount("acc" + i).send().getAccountId();
            } catch(Exception e) {
            }
        });
    }

    @Test
    public void getAccountsAndBalance() throws Exception {
        LogLevelUtil.setInfo();
        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        SimpleLogger.println("## Account size : " + accounts.size());

        for(String acc : accounts) {
            BigInteger balance = web3j.ethGetBalance(acc, DefaultBlockParameterName.LATEST).send().getBalance();
            SimpleLogger.println("Account : {} ==> Balance : {}", acc,balance);
        }

        SimpleLogger.println("## Account size : " + accounts.size());
    }
}
