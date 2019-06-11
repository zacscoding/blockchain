package demo.msp;

import demo.common.LogLevelUtil;
import demo.common.TestCaInfoSupplier;
import demo.common.TestHelper;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Base64.Decoder;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * CA와 직접적으로 관련 된 MSP 를 체크
 */
@Slf4j
public class CaMspTest {

    SampleUser admin;
    HFCAClient caClient;

    @Before
    public void setUp() {
        LogLevelUtil.setInfo();
        admin = TestCaInfoSupplier.getAdmin();
        caClient = TestCaInfoSupplier.getCaClient();
    }

    @Test
    public void temp() throws Exception {
        byte[] buff = new byte[1024];
        IOUtils.read(new ClassPathResource("samples/IssuerPublicKey").getInputStream(), buff);
        String readValue = Base64.getEncoder().encodeToString(buff).substring(0, 1124);
        System.out.println(readValue);
        System.out.println(readValue.length());
        String read = "CgJPVQoEUm9sZQoMRW5yb2xsbWVudElEChBSZXZvY2F0aW9uSGFuZGxlEkQKIPXO6pJYgZki2XKzo85qUbCuo8+A8pcP8o3WCEP0lSNfEiDpXG6DpZJWpGtjQ/9oV06sKloMJHQQbOBwKMwbr6M6xxpECiCLk0iBupTe3cTgc7gX4nqdb7dpcZzFIxmBMd43yBLPFhIggXRSD33Qt/yPOcZHck6tHB9QRyoy9Bko2N1NKhsf+TEiRAogMf6Cb9/kCTR4mBi7T30CgVc+b2mDsSZ9NxERScZ6gHESIPNEM/f+fGj7of7nnLuGYv0rdhpuDsVHPFIl7Rz57NSbIkQKIKW2P43FZjLxYpAo5NKH40gGe/2k8rcZ9ZCGBZqKFgueEiBsZh+cNedrd1h7Ze8i9ijUXOm/jECmsd608V0HgaPudCJECiCdCS6Q9Ix/kTzADOV8ztyN6DjxF3eaBze/XCOqqh4jZhIgz181zkBCdbp9JV3EX8CHIXSEnsYnIkYqy/YMx0Ri+gIiRAogHyCmVvSsAxpDjZPebL9WIweiP+T/LKn1vYy6CYXKzTkSIFOztv2FTFKEnblibI9iInxQ31MPzegLTRhJu//FB7XGKogBCiCp4F/eejqg2PLR5lBAXZmD3UIMz7IoW+kmvpIi3OcMqRIg4wf7tDJAHuZil8BvHowllxUrFABajqYQzP1uRCRWEfEaICMM5dSrw0I32V/WjOyYpS6MZXPSlTn0RtPte9plzp7QIiBI/Dmk+gVUP/HLCVdzg3Sr4XQliJDPKrX6xdNyYQe0IjJECiBYCjUXK5Oc17MJ2Qaf5gd6pWKfvMvrlVyZtRd9VoLZgxIgLdr5gWOwqyw8FZ6UVUByTA5rXeizusS/MYslEgxqU2Q6RAoggmlHgrvR0fUiqYA5E7/kevTRUxzVD/UTptI6n7MiS/wSIMZIapcqh1IKhqfI7+wWwiWiA/MEdOoe+en7nxZ/chQeQiBuYnMf4q+ErCTY1MNcBEVAisF0NnvbPTwNepBcp1QXEkognUuw9OJsuGDhE/zvP19ePt2RAd8XMIYm+tmm4UO5p7hSIHEOr7SRVVqNiaCDef5+iUXCQ0KfV7xFI2qwuWxRHmrc";
        System.out.println(read.length());
    }

    // 1) ca info
    @Test
    public void findCaInfo() throws Exception {
        HFCAInfo info = caClient.info();

        TestHelper.out("## ca name : \n%s", info.getCAName());
        TestHelper.out("-----------------------------------------------------------------------------");
        TestHelper.out("ca certificate chain : \n%s", info.getCACertificateChain());
        TestHelper.out("-----------------------------------------------------------------------------");
        TestHelper.out("idemix issuer public key : \n%s", info.getIdemixIssuerPublicKey());
        TestHelper.out("-----------------------------------------------------------------------------");
        TestHelper.out("idemix issuer revocation public key : \n%s", info.getIdemixIssuerRevocationPublicKey());
        TestHelper.out("-----------------------------------------------------------------------------");
        TestHelper.out("version : \n%s", info.getVersion());
        TestHelper.out("-----------------------------------------------------------------------------");

        byte[] decode = Base64.getDecoder().decode(info.getCACertificateChain());
        System.out.println(new String(decode));
        BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(decode));
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(pem);
        /* Output
        ## ca name :
        ca0.testnet.com
        -----------------------------------------------------------------------------
        ca certificate chain :
        LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUI1VENDQVl1Z0F3SUJBZ0lVWlBTWi9MQnJXdmZvWmtISlNsQ3czczJIdC9Fd0NnWUlLb1pJemowRUF3SXcKVHpFTE1Ba0dBMVVFQmhNQ1MxSXhGREFTQmdOVkJBb1RDMGg1Y0dWeWJHVmtaMlZ5TVE4d0RRWURWUVFMRXdaRwpZV0p5YVdNeEdUQVhCZ05WQkFNVEVHWmhZbkpwWXkxallTMXpaWEoyWlhJd0hoY05NVGt3TmpFeE1EWXhPREF3CldoY05NelF3TmpBM01EWXhPREF3V2pCUE1Rc3dDUVlEVlFRR0V3SkxVakVVTUJJR0ExVUVDaE1MU0hsd1pYSnMKWldSblpYSXhEekFOQmdOVkJBc1RCa1poWW5KcFl6RVpNQmNHQTFVRUF4TVFabUZpY21sakxXTmhMWE5sY25abApjakJaTUJNR0J5cUdTTTQ5QWdFR0NDcUdTTTQ5QXdFSEEwSUFCT0dZcWMzNjExZTlEUXRrVkJWTlhMam1lSmRzCjNSeDNITzZKVVkvUEljamhvdkVCYXpTSy80YzBpNE95WVhtbHdFa2luL0ZEOHZ4T25vemtTSkE4UkpXalJUQkQKTUE0R0ExVWREd0VCL3dRRUF3SUJCakFTQmdOVkhSTUJBZjhFQ0RBR0FRSC9BZ0VCTUIwR0ExVWREZ1FXQkJRVApraTZxbmFoZlZ6eXdQTnZLRHg5QzBQN29WekFLQmdncWhrak9QUVFEQWdOSUFEQkZBaUVBenVKLzNjVktvN1BtCmo2cmNHQ1BzVEY5Z2JPeFdUU0pVTFQ5RlZWdWFIRlFDSUVTVEs5VkNuSkxEQVhpbnRhRzFiZlR0OUtockhWc2gKR3liSkNZK2RsMEZxCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K
        -----------------------------------------------------------------------------
        idemix issuer public key :
        CgJPVQoEUm9sZQoMRW5yb2xsbWVudElEChBSZXZvY2F0aW9uSGFuZGxlEkQKIPXO6pJYgZki2XKzo85qUbCuo8+A8pcP8o3WCEP0lSNfEiDpXG6DpZJWpGtjQ/9oV06sKloMJHQQbOBwKMwbr6M6xxpECiCLk0iBupTe3cTgc7gX4nqdb7dpcZzFIxmBMd43yBLPFhIggXRSD33Qt/yPOcZHck6tHB9QRyoy9Bko2N1NKhsf+TEiRAogMf6Cb9/kCTR4mBi7T30CgVc+b2mDsSZ9NxERScZ6gHESIPNEM/f+fGj7of7nnLuGYv0rdhpuDsVHPFIl7Rz57NSbIkQKIKW2P43FZjLxYpAo5NKH40gGe/2k8rcZ9ZCGBZqKFgueEiBsZh+cNedrd1h7Ze8i9ijUXOm/jECmsd608V0HgaPudCJECiCdCS6Q9Ix/kTzADOV8ztyN6DjxF3eaBze/XCOqqh4jZhIgz181zkBCdbp9JV3EX8CHIXSEnsYnIkYqy/YMx0Ri+gIiRAogHyCmVvSsAxpDjZPebL9WIweiP+T/LKn1vYy6CYXKzTkSIFOztv2FTFKEnblibI9iInxQ31MPzegLTRhJu//FB7XGKogBCiCp4F/eejqg2PLR5lBAXZmD3UIMz7IoW+kmvpIi3OcMqRIg4wf7tDJAHuZil8BvHowllxUrFABajqYQzP1uRCRWEfEaICMM5dSrw0I32V/WjOyYpS6MZXPSlTn0RtPte9plzp7QIiBI/Dmk+gVUP/HLCVdzg3Sr4XQliJDPKrX6xdNyYQe0IjJECiBYCjUXK5Oc17MJ2Qaf5gd6pWKfvMvrlVyZtRd9VoLZgxIgLdr5gWOwqyw8FZ6UVUByTA5rXeizusS/MYslEgxqU2Q6RAoggmlHgrvR0fUiqYA5E7/kevTRUxzVD/UTptI6n7MiS/wSIMZIapcqh1IKhqfI7+wWwiWiA/MEdOoe+en7nxZ/chQeQiBuYnMf4q+ErCTY1MNcBEVAisF0NnvbPTwNepBcp1QXEkognUuw9OJsuGDhE/zvP19ePt2RAd8XMIYm+tmm4UO5p7hSIHEOr7SRVVqNiaCDef5+iUXCQ0KfV7xFI2qwuWxRHmrc
        -----------------------------------------------------------------------------
        idemix issuer revocation public key :
        LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUhZd0VBWUhLb1pJemowQ0FRWUZLNEVFQUNJRFlnQUU4NXFnVjRrV1RSUlVhbUlNamc0ZEY5VnR6UUZPejJXQQprUG9PdGJ6NzkxNGMvMFA4YTBVTGFVSVVpZzJtVnA4NzRZa2doNG9jV2l4dXRRQ2lST1RQa0RIbnlvTVJjWmtoCmdKalduaWovaXhIUlU5U1RLMVUrcUZ2RDhCR1JVR3VFCi0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLQo=
        -----------------------------------------------------------------------------
        version :
        1.4.0
        -----------------------------------------------------------------------------
         */
    }


    // 2) IssuerPublicKey | issuerRevocationPublicKey | ca cert
    @Test
    public void findCaIssuerPublicKeyAndCaCert() throws Exception {
        HFCAInfo info = caClient.info();

        // IssuerPublicKey | issuerRevocationPublicKey
        FileWriter issuerPublicKeyWriter = new FileWriter(new File("src/test/resources/samples/IssuerPublicKeyGen"));
        FileWriter issuerRevocationPublicKeyWriter = new FileWriter(
            new File("src/test/resources/samples/IssuerRevocationPublicKeyGen"));

        Decoder decoder = Base64.getDecoder();
        byte[] issuerPublicKey = decoder.decode(info.getIdemixIssuerPublicKey().getBytes());
        byte[] issuerRevocationPublicKey = decoder.decode(info.getIdemixIssuerRevocationPublicKey().getBytes());

        IOUtils.write(issuerPublicKey, issuerPublicKeyWriter, StandardCharsets.UTF_8);
        IOUtils.write(issuerRevocationPublicKey, issuerRevocationPublicKeyWriter, StandardCharsets.UTF_8);

        issuerPublicKeyWriter.flush();
        issuerRevocationPublicKeyWriter.flush();

        // ca cert
        byte[] decode = Base64.getDecoder().decode(info.getCACertificateChain());
        System.out.println(new String(decode));
    }

    // 3) cacerts/ca-cert.pem

    // 4) keystore/key.pem

    // 5) signcerts/cert.pem
}
