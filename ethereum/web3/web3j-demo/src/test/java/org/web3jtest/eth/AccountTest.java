package org.web3jtest.eth;

import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3jtest.AbstractTestRunner;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
public class AccountTest extends AbstractTestRunner {

    @Test
    public void getAccountsAndBalance() throws Exception {
        for(String acc : web3j.ethAccounts().send().getAccounts()) {
            web3j.ethGetBalance(acc, DefaultBlockParameterName.LATEST).send().getBalance();
        }
    }
}
