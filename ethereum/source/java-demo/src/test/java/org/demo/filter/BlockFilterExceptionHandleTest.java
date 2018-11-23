package org.demo.filter;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.filters.BlockFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Async;

/**
 * @author zacconding
 * @Date 2018-11-13
 * @GitHub : https://github.com/zacscoding
 */
public class BlockFilterExceptionHandleTest {

    WebSocketService webSocketService;
    Web3j web3j;

    @Before
    public void setUp() throws ConnectException {
        String wsUrl = "ws://192.168.5.78:9540";
        webSocketService = new WebSocketService(wsUrl, false);
        webSocketService.connect();
        web3j = Web3j.build(webSocketService);
    }

    @After
    public void tearDown() {
        if (webSocketService != null) {
            webSocketService.close();
        }
    }

    @Test
    public void handleEthFilterException() throws InterruptedException {
        BlockFilter blockFilter = new BlockFilter(web3j, hash -> {
            try {
                Request<?, EthBlock> request = new Request("eth_getBlockByHash", Arrays.asList(hash, false), webSocketService, EthBlock.class);
                Block block = request.send().getBlock();
                System.out.println("## " + block.getNumber());
            } catch (IOException e) {
                System.out.println("IOException occur");
            } catch (Exception e) {
                System.out.println("Exception occur");
            }
        });
        blockFilter.run(Async.defaultExecutorService(), 5000L);

        TimeUnit.MINUTES.sleep(5L);
    }
}
