package org.web3jtest.eth;

import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
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
    public void getAccountsAndBalance() throws Exception {
        LogLevelUtil.setInfo();
        for(String acc : web3j.ethAccounts().send().getAccounts()) {
            SimpleLogger.println("Account : {} ==> Balance : {}", acc, web3j.ethGetBalance(acc, DefaultBlockParameterName.LATEST).send().getBalance());
        }
    }
}
