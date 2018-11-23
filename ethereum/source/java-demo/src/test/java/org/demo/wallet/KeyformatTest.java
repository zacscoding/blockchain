package org.demo.wallet;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-10-19
 * @GitHub : https://github.com/zacscoding
 */
public class KeyformatTest {

    @Test
    public void parityAndGethKeyFileName() {
        System.out.println("## Check geth key file name");
        System.out.println(keystoreFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.nVV'--'"));

        System.out.println("## Check parity key file name");
        System.out.println(keystoreFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ssVV'--'"));
    }

    private String keystoreFormat(String pattern) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return now.format(format);
    }

}
