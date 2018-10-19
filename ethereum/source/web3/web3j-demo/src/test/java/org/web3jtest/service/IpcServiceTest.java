package org.web3jtest.service;

import java.io.IOException;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.ipc.IpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

/**
 * @author zacconding
 * @Date 2018-10-19
 * @GitHub : https://github.com/zacscoding
 */
public class IpcServiceTest {

    @Test
    public void gethIpcTest() throws IOException {
        String path = "\\\\.\\pipe\\node.ipc";
        IpcService ipcService = new WindowsIpcService(path, true);
        Web3j web3j = Web3j.build(ipcService);
        System.out.println(web3j.web3ClientVersion().send().getWeb3ClientVersion());
    }

}
