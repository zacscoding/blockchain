package org.web3jtest.net;

import org.junit.Test;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-05-15
 * @GitHub : https://github.com/zacscoding
 */
public class NetTest extends AbstractTestRunner  {

    @Test
    public void version() throws Exception {
        /*
        Web3ClientVersion : Parity//v1.10.2-beta-f4ae813-20180423/x86_64-linux-gnu/rustc1.25.0
        Protocol Version : 63
        Net Version : 3
        Shh Version : null
        Net Peer Count : 6
        Net Listening : true
         */
        SimpleLogger.build()
                    .appendln("Web3ClientVersion : {}", web3j.web3ClientVersion().send().getWeb3ClientVersion())
                    .appendln("Protocol Version : {}", web3j.ethProtocolVersion().send().getProtocolVersion())
                    .appendln("Net Version : {}", web3j.netVersion().send().getNetVersion())
                    .appendln("Shh Version : {}", web3j.shhVersion().send().getVersion())
                    .appendln("Net Peer Count : {}", web3j.netPeerCount().send().getQuantity())
                    .appendln("Net Listening : {}", web3j.netListening().send().isListening())
                    .flush();
    }

    @Test
    public void netListening() throws Exception {
        System.out.println(web3j.netListening().send().isListening());
    }
}
