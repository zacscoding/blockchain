package org.web3jtest.parity;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.rpc.ParityJsonRpc;

/**
 * Connect parity nodes each others
 *
 * @author zacconding
 * @Date 2018-06-07
 * @GitHub : https://github.com/zacscoding
 */
public class ConnectParityNodes {
    String[] urls;
    @Before
    public void setUp() {
        urls = new String[] {
            "http://192.168.5.77:8540"
        };
    }

    @Test
    public void connectEachNodes() {
        Object[] params = new Object[urls.length - 1];
        for (int i = 0; i < urls.length - 1; i++) {
            //public static Object requestAndGetResult(String uri, String method, Long id, Object ... paramsArray)
            String enode = (String) JsonRpcHttpService.requestAndGetResult(urls[i], ParityJsonRpc.parity_enode, null);
            System.out.println(enode);
            params[i] = enode;
            Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(urls[urls.length-1], ParityJsonRpc.parity_addReservedPeer, null, enode);
            System.out.println(result);
        }
    }

    @Test
    public void displayEnodes() {
        for(String url : urls) {
            System.out.println("## URL : " + url);
            String enode = (String) JsonRpcHttpService.requestAndGetResult(url, ParityJsonRpc.parity_enode, null);
            System.out.println(enode);
        }
    }
}
