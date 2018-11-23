package org.demo.parity;

import org.junit.Before;
import org.junit.Test;
import org.demo.rpc.JsonRpcHttpService;
import org.demo.rpc.ParityJsonRpc;

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
            "http://192.168.5.78:8540",
            "http://192.168.5.78:8541"
        };
    }

    @Test
    public void test() throws Exception {
        String enode = (String) JsonRpcHttpService.requestAndGetResult(urls[0], ParityJsonRpc.parity_enode, null);
        Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(urls[1], ParityJsonRpc.parity_addReservedPeer, null, enode);
        System.out.println(enode);
        System.out.println(result);
    }

    @Test
    public void connectEachNodes() throws Exception {
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
    public void connectSpecifyNode() {
        String enode = "enode://dead745c1dbcde518b48e52aca1e8d5ba666005a2c8804e39826c6080fb11c1e8abe41d1e41896e871f204f790a90fa9781744cccecf492212192a7c56e7673b@121.141.75.103:30303";
        for (int i = 0; i < urls.length; i++) {
            Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(urls[i], ParityJsonRpc.parity_addReservedPeer, null, enode);
            System.out.println(urls[i] + "  ==> " + result);
        }
    }

    @Test
    public void disConnectSpecifyNode() {
        String enode = "enode://dead745c1dbcde518b48e52aca1e8d5ba666005a2c8804e39826c6080fb11c1e8abe41d1e41896e871f204f790a90fa9781744cccecf492212192a7c56e7673b@121.141.75.103:30303";
        for (int i = 0; i < urls.length; i++) {
            Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(urls[i], ParityJsonRpc.parity_removeReservedPeer, null, enode);
            System.out.println(urls[i] + "  ==> " + result);
        }
    }

    @Test
    public void disConnectNodes() {
        disConnectNodes(0, 2);
        disConnectNodes(1, 2);
    }

    private void disConnectNodes(int i, int j) {
        String enode = (String) JsonRpcHttpService.requestAndGetResult(urls[i], ParityJsonRpc.parity_enode, null);
        Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(urls[j], ParityJsonRpc.parity_removeReservedPeer, null, enode);
        System.out.println(result);
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
