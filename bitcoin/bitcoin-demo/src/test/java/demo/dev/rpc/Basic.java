package demo.dev.rpc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-09-05
 * @GitHub : https://github.com/zacscoding
 */
public class Basic {

    String host;
    int port;
    String username;
    String password;

    @Before
    public void setUp() {
        host = "192.168.5.78";
        port = 18332;
        username = "bitcoinrpc";
        password = "1db251a768876287efe29e3c33ae7660";
    }

    @Test
    public void defaultRequest() throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(username, password));
        String jsonBody = "{\"jsonrpc\": \"1.0\", \"id\":\"1\", \"method\": \"getblockcount\", \"params\": [] }";
        StringEntity myEntity = new StringEntity(jsonBody);
        HttpPost httppost = new HttpPost("http://" + host + ":" + port);
        httppost.setEntity(myEntity);
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        System.out.println(EntityUtils.toString(entity));
    }
}