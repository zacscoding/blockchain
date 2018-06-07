package org.web3jtest.consensus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.web3jtest.AbstractTestRunner;

/**
 * @author zacconding
 * @Date 2018-06-05
 * @GitHub : https://github.com/zacscoding
 */
public class PoaResultVeiwTest extends AbstractTestRunner {

    static CloseableHttpClient client;
    static String uri;

    @Before
    public static void setUp() {
        uri = "http://192.168.5.77:8540/";
        client = HttpClients.createDefault();
    }

    @After
    public static void tearDown() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger start = BigInteger.ONE;

        while (start.compareTo(latestBlock) <= 0) {
            String blockResult = getBlockByNumber(start, false);
            // Map<String, Object>
            start = start.add(BigInteger.ONE);
        }

        System.out.println(getBlockByNumber(BigInteger.ONE, false));
    }


    private String getBlockByNumber(BigInteger blockNumber, boolean includeTxs) {
        JSONRPC2Request request = new JSONRPC2Request("eth_getBlockByNumber", "0");

        List<Object> params = new ArrayList<>();
        params.add("0x" + blockNumber.toString(16));
        params.add(true);
        request.setPositionalParams(params);

        try {
            CloseableHttpResponse response = client.execute(createHttpPost(uri, request.toJSONString()));
            return EntityUtils.toString(response.getEntity());
        } catch(Exception e) {

        }

        return null;
    }

    private HttpPost createHttpPost(String uri, String body) {
        HttpPost post = new HttpPost(uri);

        post.setEntity(new StringEntity(body, "UTF-8"));
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json;charset=UTF-8");

        return post;
    }

}
