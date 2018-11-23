package org.demo.consensus;

import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import org.demo.rpc.JsonRpcHttpService;
import org.demo.rpc.ParityJsonRpc;
import org.demo.util.SimpleLogger;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-06-18
 * @GitHub : https://github.com/zacscoding
 */
public class BetterThanChainTest {

    String[] nodes;

    @Before
    public void setUp() {
        nodes = new String[] {
            "http://192.168.5.77:8540/",
            "http://192.168.5.77:8541/"
        };
    }

    @Test
    public void displayTotalDiff() throws Exception {
        Subscription sub1 = subscribe(nodes[0]);
        Subscription sub2 = subscribe(nodes[1]);
        TimeUnit.MINUTES.sleep(5);
        sub1.unsubscribe();
        sub2.unsubscribe();
    }

    @Test
    public void connect() throws Exception {

        Object[] params = new Object[nodes.length - 1];
        for (int i = 0; i < nodes.length - 1; i++) {
            //public static Object requestAndGetResult(String uri, String method, Long id, Object ... paramsArray)
            String enode = (String) JsonRpcHttpService.requestAndGetResult(nodes[i], ParityJsonRpc.parity_enode, null);
            params[i] = enode;
            Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(nodes[nodes.length-1], ParityJsonRpc.parity_addReservedPeer, null, enode);

            SimpleLogger.println("{} => {}", enode, result);
        }
    }

    @Test
    public void totalDifficulty() throws Exception {

    }



    private Subscription subscribe(String url) {
        Web3j web3j = Web3j.build(new HttpService(url));
        final String port = url.substring(url.lastIndexOf(':') + 1);

        Subscription subscription = web3j.blockObservable(false).subscribe(onNext -> {
            Block block = onNext.getBlock();
            SimpleLogger.println("[{}] {} => {}", port, block.getNumber().toString(10), block.getTotalDifficulty().toString(10));
        });

        return subscription;
    }

}
