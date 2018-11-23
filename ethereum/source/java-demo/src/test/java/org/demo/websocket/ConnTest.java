package org.demo.websocket;

import static org.junit.Assert.assertTrue;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.demo.util.GsonUtil;
import org.demo.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-05-22
 * @GitHub : https://github.com/zacscoding
 */
public class ConnTest {

    @Test
    public void test() throws Exception {
        String uri  = "ws://192.168.5.77:8450";
        WebSocketClient client = new WebSocketClient(new URI(uri)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                SimpleLogger.println("## onOpen : " + GsonUtil.toString(serverHandshake));
            }

            @Override
            public void onMessage(String s) {
                SimpleLogger.println("## onMessage : " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                SimpleLogger.println("## onClose i : {}, s : {}, boolean : {}", i, s, b);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };

        client.connectBlocking();
        Thread.sleep(10 * 1000L);
        assertTrue(client.isOpen());

        // send new block number
        JSONRPC2Request request = new JSONRPC2Request("parity_subscribe", "1");
        List<Object> params = new ArrayList<>();
        params.add("eth_blockNumber");
        List<Object> subParams = new ArrayList<>();
        params.add(subParams);
        request.setPositionalParams(params);

        client.send(request.toJSONString());

        // send pending tx subscribe

        request = new JSONRPC2Request("parity_subscribe", "1");
        params = new ArrayList<>();
        params.add("eth_newPendingTransaction");
        subParams = new ArrayList<>();
        params.add(subParams);
        request.setPositionalParams(params);

        client.send(request.toJSONString());
        new Scanner(System.in).nextLine(); // Don't close immediately
    }
}

