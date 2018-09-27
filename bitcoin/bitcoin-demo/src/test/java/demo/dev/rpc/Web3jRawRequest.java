package demo.dev.rpc;

import java.util.Collections;
import okhttp3.Credentials;
import okhttp3.OkHttpClient.Builder;
import org.junit.Test;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.http.HttpService;

/**
 * FAIL@!! && JSON RPC VERSION DIFF
 *
 * @author zacconding
 * @Date 2018-09-05
 * @GitHub : https://github.com/zacscoding
 */
public class Web3jRawRequest {

    @Test
    public void web3j() throws Exception {
        Builder builder = new Builder();
        builder.authenticator((route, response) -> {
            if (response.request().header("Authorization") != null) {
                return null; // Give up, we've already failed to authenticate.
            }
            String credential = Credentials.basic("bitcoinrpc", "1db251a768876287efe29e3c33ae7660");
            return response.request().newBuilder().header("Authorization", credential).build();
        });
        HttpService httpService = new HttpService("http://192.168.5.78:18332", builder.build(), false);
        Object response = new Request("getblockcount", Collections.emptyList(), httpService, Object.class).send();
        System.out.println(response);
    }
}
