package org.web3jtest.raw;

import java.util.Collections;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-07-30
 * @GitHub : https://github.com/zacscoding
 */
public class Web3jRawRequestTest {

    @Test
    public void test() throws Exception {
        // Request(String method, List<S> params, Web3jService web3jService, Class<T> type)
        HttpService httpService = new HttpService("http://192.168.5.77:8541");
        Object response = new Request("eth_blockNumber", Collections.emptyList(), httpService, Object.class).send();
        // String rawResponse =
        // System.out.println(rawResponse);
        System.out.println(response);
    }

    public static class RawResponse extends Response<Void> {

    }

}
