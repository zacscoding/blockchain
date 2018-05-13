package org.demo.crypto;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.tomcat.util.buf.HexUtils;
import org.demo.util.SimpleLogger;
import org.ethereum.crypto.HashUtil;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */
public class HashUtilTest {

    /**
     * 임의의 크기 값을 입력했을 때 고정 크기 값을 생성해내는 해시 함수
     * => input의 길이가 다 달라도 output은 65바이트 고정된 암호화된 문자열 값 생성
     */
    @Test
    public void sha256() {
        /*
        Input : Core
        Output : 70ea1983c983deacc1b61805aea3d43648afd932f346fb2e5d9b15facd4035c2
        Input : Core Ethereum
        Output : c455abc9d19e2927ee650fa1b39e9278e63c3bb8cea63ba82e4eae5203d8a61f
        Input : Core Ethereum Programming
        Output : 811588e59dcdb157bc630a64e90acf644d02be3c4d067822bc55d9741de3f678
         */
        String[] inputs = {
            "Core",
            "Core Ethereum",
            "Core Ethereum Programming"
        };

        for(String input : inputs) {
            byte[] output = HashUtil.sha256(input.getBytes());
            String outputStr  = Hex.toHexString(output);
            SimpleLogger.println("Input : {}\nOutput : {}", input, outputStr);
        }
    }
}
