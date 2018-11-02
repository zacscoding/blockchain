package org.web3jtest.geth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.web3jtest.rpc.JsonRpcHttpService;
import org.web3jtest.util.GsonUtil;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-01
 * @GitHub : https://github.com/zacscoding
 */
public class ConnectGethNodes {

    String[] urls;
    List<Pair<String, String>> enodes;

    Pair<String, String> pair = Pair.of(null, null);

    @Before
    public void setUp() throws IOException {
        urls = new String[] {
            "http://192.168.5.78:8501"
            ,"http://192.168.5.78:8502"
            ,"http://192.168.5.78:8503"
        };

        enodes = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            String enode = getEnode(JsonRpcHttpService.request(urls[i], "admin_nodeInfo", null));
            enodes.add(Pair.of(urls[i], enode));
        }
    }
    @Test
    public void displayEnodes() {
        for(Pair<String, String> enode : enodes) {
            SimpleLogger.println("{} -> {}", enode.getKey(), enode.getValue());
        }
    }

    @Test
    public void displayNodeInfo() {
        displayPretty(urls[0], "admin_peers", null);
    }

    @Test
    public void connectEachNodes() throws Exception {
        for (int i = 0; i < urls.length; i++) {
            String enode = enodes.get(i).getRight();

            for(int j=0; j<urls.length; j++) {
                if (i == j) {
                    continue;
                }
                Boolean result = (Boolean) JsonRpcHttpService.requestAndGetResult(urls[j], "admin_addPeer", null, enode);
                SimpleLogger.println("Node{} -> Node{} ==> {}", i, j, result);
            }
        }
    }

    @Test
    public void disConnectNodes() {
        // disConnect(0,1);
    }

    private void disConnect(int n1, int n2) {
        displayPretty(urls[n1], "admin_removePeer", enodes.get(n2).getValue());
        displayPretty(urls[n2], "admin_removePeer", enodes.get(n1).getValue());
    }

    private String getEnode(String json) throws IOException  {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(json).get("result").get("enode").asText();
    }

    private void displayPretty(String url, String method, Object... params) {
        String result = JsonRpcHttpService.request(url, method, null, params);
        System.out.println(GsonUtil.jsonStringToPretty(result));
    }
}