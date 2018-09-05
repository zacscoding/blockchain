//package demo.dev;
//
//import okhttp3.Authenticator;
//import okhttp3.OkHttpClient;
//import okhttp3.OkHttpClient.Builder;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.junit.Test;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.http.HttpService;
//
///**
// * FAIL!!!
// * @author zacconding
// * @Date 2018-09-05
// * @GitHub : https://github.com/zacscoding
// */
//public class Web3jRawRequest {
//
//    @Test
//    public void web3j() {
//        Builder builder = new Builder();
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        httpclient.getCredentialsProvider().setCredentials(new AuthScope("192.168.5.78", 18332), new UsernamePasswordCredentials("bitcoinrpc", "1db251a768876287efe29e3c33ae7660"));
//        HttpService httpService = new HttpService("http://192.168.5.78:18332", httpclient);
//
//    }
//}
