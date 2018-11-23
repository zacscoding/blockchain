package org.demo.admin;

import java.math.BigInteger;
import java.util.List;
import org.junit.Test;
import org.demo.AbstractTestRunner;

/**
 * @author zacconding
 * @Date 2018-10-31
 * @GitHub : https://github.com/zacscoding
 */
public class AdminTest extends AbstractTestRunner {

    @Test
    public void personalUnlock() throws Exception {
        List<String> ids = admin.personalListAccounts().send().getAccountIds();
        ids.forEach(
            id -> System.out.println(id)
        );

        String address = "0x60975b4856ef0af3a3627cb70dfb0b84d78266ee";
        String password = "test";
        Boolean result = admin.personalUnlockAccount(address, password, BigInteger.ZERO).send().accountUnlocked();
        System.out.println(">> Result :: " + result);
    }
}
