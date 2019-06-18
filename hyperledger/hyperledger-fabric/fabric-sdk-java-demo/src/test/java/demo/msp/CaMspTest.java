package demo.msp;

import demo.common.LogLevelUtil;
import demo.common.TestCaInfoSupplier;
import demo.common.TestHelper;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Base64.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdkintegration.SampleStore;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation.HFCAAffiliationResp;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.AffiliationException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
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
    SampleStore sampleStore;


    @Before
    public void setUp() {
        LogLevelUtil.setInfo();
        admin = TestCaInfoSupplier.getAdmin();
        caClient = TestCaInfoSupplier.getCaClient();
        sampleStore = TestCaInfoSupplier.SAMPLE_STORE;
    }

    //////////////////////////////////////////////////////////////////////////////////
    // ca info
    // - cainfo 응답 출력
    //////////////////////////////////////////////////////////////////////////////////
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


    //////////////////////////////////////////////////////////////////////////////////
    // IssuerPublicKey | issuerRevocationPublicKey | ca cert
    // - cainfo 결과를 통해 위의 3가지 파일 내용 추출
    //////////////////////////////////////////////////////////////////////////////////
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

    //////////////////////////////////////////////////////////////////////////////////
    // keystore/key.pem | signcerts/cert.pem
    // - cainfo 결과를 통해 위의 3가지 파일 내용 추출
    //////////////////////////////////////////////////////////////////////////////////
    @Test
    public void findSignCerts() throws Exception {
        TestHelper.out("-----------------------------------------------------------------------------");
        TestHelper.out("display sign certs : \n%s", admin.getEnrollment().getCert());
        TestHelper.out("-----------------------------------------------------------------------------");

        TestHelper.out("-----------------------------------------------------------------------------");
        TestHelper.out("display pem string from private key : \n%s"
            , getPEMStringFromPrivateKey(admin.getEnrollment().getKey()));
        TestHelper.out("-----------------------------------------------------------------------------");
    }

    //////////////////////////////////////////////////////////////////////////////////
    // OrdererOrg1
    // - Orderer 관련 affiliation 추가 | user,admin identity + enroll
    // - peer의 경우 .setType("peer") 로 변경
    //////////////////////////////////////////////////////////////////////////////////
    @Test
    public void createOrdererMsp() throws Exception {
        String ordererAffiliationName = "OrdererOrg2";

        // setup affiliation
        TestHelper.out("## Check affiliation : %s", ordererAffiliationName);
        HFCAAffiliation affiliation = getAffiliation(ordererAffiliationName, admin);
        if (affiliation == null) {
            TestHelper.out(">>> Add affiliation : %s", ordererAffiliationName);
            affiliation = caClient.newHFCAAffiliation(ordererAffiliationName);
            HFCAAffiliationResp affiliationResp = affiliation.create(admin);
            if (affiliationResp.getStatusCode() >= 400) {
                logger.warn("Failed to create affiliation {}", affiliation);
                return;
            }
        } else {
            TestHelper.out(">>> Skip adding %s", ordererAffiliationName);
        }

        // orderer admin user 생성
        SampleUser ordererAdmin = new SampleUser(
            "adminOrderer@" + ordererAffiliationName
            , ordererAffiliationName
            , sampleStore
            , caClient.getCryptoSuite()
        );

        // (1) identity 추가
        RegistrationRequest ordererAdminRR = new RegistrationRequest(ordererAdmin.getName(), ordererAffiliationName);
        String ordererAdminPassword = "passwd";
        ordererAdminRR.setSecret(ordererAdminPassword);
        ordererAdminRR.addAttribute(new Attribute("hf.Registrar.Roles", "*"));
        ordererAdminRR.addAttribute(new Attribute("hf.Registrar.DelegateRoles", "*"));
        ordererAdminRR.addAttribute(new Attribute("hf.Registrar.Attributes", "*"));
        ordererAdminRR.addAttribute(new Attribute("hf.GenCRL", "1"));
        ordererAdminRR.addAttribute(new Attribute("hf.Revoker", "1"));
        ordererAdminRR.addAttribute(new Attribute("hf.AffiliationMgr", "1"));
        ordererAdminRR.addAttribute(new Attribute("hf.IntermediateCA", "1"));

        ordererAdmin.setEnrollmentSecret(caClient.register(ordererAdminRR, admin));
        if (!ordererAdmin.getEnrollmentSecret().equals(ordererAdminPassword)) {
            logger.warn("Failed to register orderer admin");
            return;
        }
        ordererAdmin.setAffiliation(ordererAffiliationName);

        // (2) enrollment
        ordererAdmin.setEnrollment(caClient.enroll(ordererAdmin.getName(), ordererAdmin.getEnrollmentSecret()));

        // orderer node 생성
        SampleUser ordererNode = new SampleUser(
            "orderer0@" + ordererAffiliationName
            , ordererAffiliationName
            , sampleStore
            , caClient.getCryptoSuite()
        );
        String ordererNodePassword = "passwd";

        HFCAIdentity ordererNodeIdentity = caClient.newHFCAIdentity(ordererNode.getName());
        ordererNodeIdentity.setSecret(ordererNodePassword);

        // (1) identity 추가
        RegistrationRequest ordererNodeRR = new RegistrationRequest(ordererNode.getName(), ordererAffiliationName);
        ordererNodeRR.setType("orderer");

        ordererNodeRR.setSecret(ordererNodePassword);

        ordererNode.setEnrollmentSecret(caClient.register(ordererNodeRR, admin));
        // ordererAdmin
        if (!ordererNode.getEnrollmentSecret().equals(ordererNodePassword)) {
            logger.warn("Failed to register orderer node");
            return;
        }
        ordererNode.setAffiliation(ordererAffiliationName);

        // (2) enrollment
        ordererNode.setEnrollment(caClient.enroll(ordererNode.getName(), ordererNode.getEnrollmentSecret()));

        // cert 콘솔 출력
        displayMSP(ordererAdmin);
        displayMSP(ordererNode);
    }


    //////////////////////////////////////////////////////////////////////////////////
    // cert, pk 파일로 부터 Enrollment 인스턴스 생성 테스트
    //////////////////////////////////////////////////////////////////////////////////
    @Test
    public void createEnrollmentFromString() throws Exception {
        // pem, private key 파일 read (File -> String)
        String signedPemPath = "test1/ca/ca.crt";
        String privateKeyPath = "test1/ca/ca.key";
        String pemString = IOUtils.toString(
            new ClassPathResource(signedPemPath).getInputStream(), StandardCharsets.UTF_8
        );
        String privateKeyString = IOUtils.toString(
            new ClassPathResource(privateKeyPath).getInputStream(), StandardCharsets.UTF_8
        );

        // cert -> Enrollment
        PrivateKey privateKey = bytesToPrivateKey(privateKeyString.getBytes());
        Enrollment enrollment = new X509Enrollment(privateKey, pemString);
        System.out.println(enrollment.getCert());
        System.out.println(enrollment.getKey());
    }

    //////////////////////////////////////////////////////////////////////////////////
    // helpers
    //////////////////////////////////////////////////////////////////////////////////
    private String getPEMStringFromPrivateKey(PrivateKey privateKey) throws IOException {
        StringWriter pemStrWriter = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(pemStrWriter);
        pemWriter.writeObject(privateKey);
        pemWriter.close();
        return pemStrWriter.toString();
    }

    private void displayMSP(SampleUser user) throws Exception {
        TestHelper.out("========================================================");
        TestHelper.out("Display MSP [ %s (%s) ]", user.getName(), user.getAffiliation());
        TestHelper.out("========================================================");
        TestHelper.out("keystore/key.pem\n%s", getPEMStringFromPrivateKey(user.getEnrollment().getKey()));
        TestHelper.out("========================================================");
        TestHelper.out("signcerts/cert.pem\n%s", user.getEnrollment().getCert());
        TestHelper.out("========================================================");
    }

    /**
     * user 에 affiliation 이 존재하는 지 체크
     */
    private HFCAAffiliation getAffiliation(String name, SampleUser user)
        throws InvalidArgumentException, AffiliationException {

        HFCAAffiliation caAffiliations = caClient.getHFCAAffiliations(user);

        if (caAffiliations == null) {
            return null;
        }

        return getAffiliation(caAffiliations, name);
    }

    private HFCAAffiliation getAffiliation(HFCAAffiliation affiliation, String name) throws AffiliationException {
        if (affiliation.getName().equals(name)) {
            return affiliation;
        }

        for (HFCAAffiliation child : affiliation.getChildren()) {
            HFCAAffiliation find = getAffiliation(child, name);
            if (find != null) {
                return find;
            }
        }

        return null;
    }

    public PrivateKey bytesToPrivateKey(byte[] pemKey) throws CryptoException {
        PrivateKey pk = null;
        CryptoException ce = null;

        try {
            PemReader pr = new PemReader(new StringReader(new String(pemKey)));
            PemObject po = pr.readPemObject();
            PEMParser pem = new PEMParser(new StringReader(new String(pemKey)));

            if (po.getType().equals("PRIVATE KEY")) {
                pk = new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) pem.readObject());
            } else {
                logger.trace("Found private key with type " + po.getType());
                PEMKeyPair kp = (PEMKeyPair) pem.readObject();
                pk = new JcaPEMKeyConverter().getPrivateKey(kp.getPrivateKeyInfo());
            }
        } catch (Exception e) {
            throw new CryptoException("Failed to convert private key bytes", e);
        }
        return pk;
    }
}
