package org.demo.raw;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-06-05
 * @GitHub : https://github.com/zacscoding
 */
public class RawRequest {

    CloseableHttpClient client;
    String uri;

    @Before
    public void setUp() {
        uri = "http://192.168.5.77:8540/";
        client = HttpClients.createDefault();
    }

    @After
    public void tearDown() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test() {
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
