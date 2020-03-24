package org.demo.websocket;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.demo.util.SimpleLogger;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketListener;
import org.web3j.protocol.websocket.WebSocketService;

/**
 * @author zacconding
 */
public class Web3jWebsocketTest {

    private WebSocketClient client;

    @Test
    public void connTest() throws Exception {
        client = new WebSocketClient(new URI("ws://192.168.5.78:9540"));
        client.setListener(new WebSocketListener() {
            @Override
            public void onMessage(String s) throws IOException {
                SimpleLogger.println("## onMessage : {}", s);
            }

            @Override
            public void onError(Exception e) {
                SimpleLogger.println("onError : " + e.getMessage());
            }

            @Override
            public void onClose() {
                SimpleLogger.println("onClose");
            }
        });

        try {
            WebSocketService webSocketService = new WebSocketService(client, false);
            webSocketService.connect();

            Web3j web3j = Web3j.build(webSocketService);

            web3j.blockFlowable(false).subscribe(onNext -> {
                SimpleLogger.println("## Receive new block : {}({})", onNext.getBlock().getNumber(), onNext.getBlock().getHash());
            }, onError -> {
                System.out.println("observer error");
                onError.printStackTrace();
            });

            TimeUnit.MINUTES.sleep(5);
        } catch (ConnectException e) {
            SimpleLogger.error("Failed to connect websocket", e);
        }
    }

    @Test
    public void connTest2() throws Exception {
        /*String[] urls = {
        };*/
        String[] urls = {
            ""
        };

        for (String url : urls) {
            conn(url);
        }
    }

    private void conn(String url) {
        WebSocketClient client = null;
        try {
            System.out.println("## Try to connect : " + url);
            client = new WebSocketClient(new URI(url));
            WebSocketService webSocketService = new WebSocketService(client, false);
            webSocketService.connect();
            Web3j web3j = Web3j.build(webSocketService);
            System.out.println("## best block : " + web3j.ethBlockNumber().send().getBlockNumber());
        } catch (Exception e) {
            System.out.println("## Failed to connect : " + url + " ===> " + e.getMessage());
            if (client != null) {
                client.close();
            }
        }
    }
}
