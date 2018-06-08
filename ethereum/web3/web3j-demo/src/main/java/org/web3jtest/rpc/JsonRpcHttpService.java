package org.web3jtest.rpc;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.web3jtest.util.GsonUtil;

/**
 * @author zacconding
 * @Date 2018-06-07
 * @GitHub : https://github.com/zacscoding
 */
public class JsonRpcHttpService {

    private static final Logger logger = LoggerFactory.getLogger(JsonRpcHttpService.class);

    public static String request(String uri, String method, Long id, Object... paramsArray) {
        CloseableHttpClient client = null;

        try {
            client = HttpClients.createDefault();

            String requestBody = generateJsonRequestString(method, id, paramsArray);
            //logger.info("## Request body : " + requestBody);
            CloseableHttpResponse response = client.execute(generateHttpPost(uri, requestBody));

            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object requestAndGetResult(String uri, String method, Long id, Object... paramsArray) {
        return extractResult(request(uri, method, id, paramsArray));
    }

    public static Object extractResult(String resultJson) {
        if (!StringUtils.hasText(resultJson)) {
            return null;
        }

        Map<String, Object> resultMap = GsonUtil.GsonFactory.createDefaultGson().fromJson(resultJson, Map.class);
        if (resultMap.containsKey("result")) {
            return resultMap.get("result");
        }

        if (resultMap.containsKey("error")) {
            logger.error("failed to response : " + resultJson);
            throw new RuntimeException(resultJson);
        }

        throw new RuntimeException(resultJson);
    }

    private static String generateJsonRequestString(String method, Long id, Object... paramsArray) {
        List<Object> params = null;

        if (id == null) {
            id = Long.valueOf(0L);
        }

        if (paramsArray == null) {
            params = Collections.emptyList();
        } else {
            params = Arrays.asList(paramsArray);
        }

        JSONRPC2Request request = new JSONRPC2Request(method, id);
        request.setPositionalParams(params);

        return request.toJSONString();
    }

    private static HttpPost generateHttpPost(String uri, String body) {
        HttpPost httpPost = new HttpPost(uri);

        httpPost.setEntity(new StringEntity(body, "UTF-8"));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json;charset=UTF-8");

        return httpPost;
    }
}
