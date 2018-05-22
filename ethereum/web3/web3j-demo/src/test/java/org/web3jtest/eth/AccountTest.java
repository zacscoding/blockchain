package org.web3jtest.eth;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
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
    public void getAccountsAndBalance() throws Exception {
        LogLevelUtil.setInfo();
        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        SimpleLogger.println("## Account size : " + accounts.size());
        for(String acc : accounts) {
            SimpleLogger.println("Account : {} ==> Balance : {}", acc, web3j.ethGetBalance(acc, DefaultBlockParameterName.LATEST).send().getBalance());
        }
    }
}
