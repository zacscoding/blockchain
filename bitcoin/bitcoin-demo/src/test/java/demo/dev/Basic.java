package demo.dev;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-09-05
 * @GitHub : https://github.com/zacscoding
 */
public class Basic {

    @Test
    public void test() throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCredentialsProvider().setCredentials(new AuthScope("192.168.5.78", 18332), new UsernamePasswordCredentials("bitcoinrpc", "1db251a768876287efe29e3c33ae7660"));
        String jsonBody  = "{\"jsonrpc\": \"1.0\", \"id\":\"1\", \"method\": \"getblockcount\", \"params\": [] }";
        StringEntity myEntity = new StringEntity(jsonBody);
        HttpPost httppost = new HttpPost("http://192.168.5.78:18332");
        httppost.setEntity(myEntity);
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        System.out.println(EntityUtils.toString(entity));
    }
}