package org.web3jtest;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */

// @RunWith(SpringRunner.class)
// @SpringBootTest
public class AbstractTestRunner {

    public static Web3j web3j;
    public static Admin admin;
    public static Parity parity;
    public static HttpService httpService;

    @BeforeClass
    public static void classSetUp() {
        // web3j = Web3j.build(new HttpService("http://192.168.79.128:8540"));
        // web3j = Web3j.build(new HttpService("http://192.168.5.15:8540"));
        // HttpService httpService = new HttpService("http://192.168.5.50:8540");
        // HttpService httpService = new HttpService("http://192.168.5.50:8543");
        // HttpService httpService = new HttpService("http://192.168.79.128:8540");
        // HttpService httpService = new HttpService("http://192.168.5.77:8540");
        // httpService = new HttpService("http://54.180.40.81:9540");

        //HttpService httpService = new HttpService("http://192.168.5.78:9540");

        try {
            String url = Files.readLines(new File("src/test/resources/secret.txt"), StandardCharsets.UTF_8).get(0);
            httpService = new HttpService(url);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        web3j = Web3j.build(httpService);
        admin = Admin.build(httpService);
        parity = Parity.build(httpService);
    }

    @AfterClass
    public static void classTearDown() {
        if (httpService != null) {
            try {
                httpService.close();
            } catch (IOException e) {

            }
        }
    }

    @Test
    public void contextLoad() throws Exception {
        System.out.println(web3j.web3ClientVersion().send().getWeb3ClientVersion());
    }

    @Test
    public void sample() {

    }
}
