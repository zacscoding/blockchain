package demo.basic;

import demo.common.TestReflectionUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * HFCAClient 테스트
 */
@Slf4j
public class CAClientBasicUsage {

    private String rootDir = "test1";
    private String caDir = rootDir + "/ca";

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
        // 1) acquire root ca
        Enrollment enroll = caClient.enroll("admin@RootCA", "rootcaadmin");
        logger.info("## Display cert : \n{}", enroll.getCert());
        logger.info("## Display PK : \n{}", enroll.getKey().toString());
        System.out.println("---------------------------------------------------------------------");

        Enrollment enroll2 = caClient.enroll("admin2", "adminpw");
        logger.info("## Display cert : \n{}", enroll2.getCert());
        logger.info("## Display PK : \n{}", enroll2.getKey().toString());
        System.out.println("---------------------------------------------------------------------");
    }

    private HFCAClient createHFCAClient() throws Exception {
        String caName = "ca0.testnet.com";
        String caLocation = "http://10.0.164.32:7054";

        HFCAClient caClient = HFCAClient.createNewInstance(caName, caLocation, null);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        return caClient;
    }

    private PrivateKey getCaAdminPrivateKey() throws Exception {
        return bytesToPrivateKey(
            IOUtils.toByteArray(new ClassPathResource(caDir + "/ca.key").getURL())
        );
    }

    private X509Certificate getCaAdminCert() throws Exception {
        return getCert(
            IOUtils.toByteArray(new ClassPathResource(caDir + "/ca.crt").getURL())
        );
    }

    private String getCaPemString() throws Exception {
        return IOUtils.toString(new ClassPathResource(caDir + "/ca.crt").getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     *
     */
    private X509Certificate getCert(byte[] certBytes) throws CertificateException {
        BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(certBytes));
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
        X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);
        return certificate;
    }

    /**
     * org.hyperledger.fabric.sdk.security.CryptoPrimitives`s methods
     */
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
