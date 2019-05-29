package demo.basic;

import demo.common.TestReflectionUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * HFCAClient 테스트
 */
@Slf4j
public class CAClientBasicUsage {

    @Test
    public void getCaInfo() throws Exception {
        HFCAClient caClient = createHFCAClient();
        HFCAInfo info = caClient.info();
        String message = TestReflectionUtils.displayGetterMethods(info);
        logger.info("\n{}", message);

        /* Output
            // ===================================## Display getter methods [HFCAInfo]
            getCAName() : ca0.testnet.com
            getVersion() : 1.4.0
            getIdemixIssuerRevocationPublicKey() : LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tL...
            getCACertificateChain() : LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUI1VEND...
            getIdemixIssuerPublicKey() : CgJPVQoEUm9sZQoMRW5yb2xsbWVudElEChBSZXZvY...
         */
    }

    @Test
    public void enrollUser() throws Exception {
        HFCAClient caClient = createHFCAClient();
        EnrollmentRequest req = new EnrollmentRequest();
        req.setCsr(getCaPem());
        Enrollment enroll = caClient.enroll("admin", "rootcaadmin", req);
    }

    private HFCAClient createHFCAClient() throws Exception {
        String caName = "ca0.testnet.com";
        String caLocation = "http://10.0.164.32:7054";

        HFCAClient caClient = HFCAClient.createNewInstance(caName, caLocation, null);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        return caClient;
    }

    @Test
    public void temp() throws Exception {
        // CryptoSuite.Factory.getCryptoSuite()
        // TODO : key pair !!!!
    }

    private X509Certificate getCaAdminCert() throws Exception {
        return getCert(
            IOUtils.toByteArray(new ClassPathResource("ca/ca.crt").getURL())
        );
    }

    private String getCaPem() throws Exception {
        return IOUtils.toString(new ClassPathResource("ca/ca.crt").getInputStream(), StandardCharsets.UTF_8);
    }

    private X509Certificate getCert(byte[] certBytes) throws CertificateException {
        BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(certBytes));
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);
        return certificate;
    }

}
