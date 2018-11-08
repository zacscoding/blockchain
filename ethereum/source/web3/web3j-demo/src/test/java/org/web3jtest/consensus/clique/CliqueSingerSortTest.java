package org.web3jtest.consensus.clique;

import java.math.BigInteger;
import java.util.PriorityQueue;
import org.junit.Test;
import org.web3j.utils.Numeric;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-11-01
 * @GitHub : https://github.com/zacscoding
 */
public class CliqueSingerSortTest {

    @Test
    public void getSignerFromGenesis() {
        String extraDataString = "0x000000000000000000000000000000000000000000000000000000000000000035b1cf8b9c9499f3af7fc0167223c719aec1bdc87d0e60a4f643552fcb871a7a0b604e5dde8bda96a06208a7bb1e461c79546ab60fa07a39b296e1c9aa4d9e92fe6fbf84e9e8003536b6ee7b42072ac6db903d484a4baca1e7f9111c7e1749e462106c160000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        byte[] extraData = Numeric.hexStringToByteArray(extraDataString);
        System.out.println(extraData.length);
    }
    
    @Test
    public void sortAccounts() {
        String[] addrs = {
            "f4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb"
            ,"55c2a4991130a280a34cb8e73d36eeedc5a10ca9"
            ,"f6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8"
        };

        PriorityQueue<BigInteger> que = new PriorityQueue<>();
        for (String addr : addrs) {
            BigInteger bi = new BigInteger(addr, 16);
            SimpleLogger.println("addr : {} -> {}", addr, bi.toString(10));
            que.offer(bi);
        }

        while (!que.isEmpty()) {
            System.out.println(que.poll().toString(10));
        }
//        result
//        addr : f4a98b035bda9dfea0b8c7e0cf574f6da66f0bbb -> 1396774683770255007504495890295769463658944334779
//        addr : 55c2a4991130a280a34cb8e73d36eeedc5a10ca9 -> 489604898589485650152962758279745218847411604649
//        addr : f6c1c2231b2e5e6b6fad4d3420c5ddd021b748d8 -> 1408732685175464453548529261582932310166022342872
//        489604898589485650152962758279745218847411604649
//        1396774683770255007504495890295769463658944334779
//        1408732685175464453548529261582932310166022342872
    }
}
