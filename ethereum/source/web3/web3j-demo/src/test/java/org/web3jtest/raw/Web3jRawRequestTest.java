package org.web3jtest.raw;

import java.util.Collections;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
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
        HttpService httpService = new HttpService("http://192.168.5.77:8540", true);
        Object response = new Request("eth_blockNumber", Collections.emptyList(), httpService, Object.class).send();

        // String rawResponse =
        // System.out.println(rawResponse);
        System.out.println(response);
    }

    @Test
    public void raw() throws Exception {
        String rawRequest = "{\"method\":\"web3_clientVersion\",\"params\":[],\"id\":1,\"jsonrpc\":\"2.0\"}";
        Web3j web3j = Web3j.build(new HttpService("http://192.168.5.77:8540", true));
        HttpService httpService = new HttpService("http://192.168.5.77:8540", true);
    }
}