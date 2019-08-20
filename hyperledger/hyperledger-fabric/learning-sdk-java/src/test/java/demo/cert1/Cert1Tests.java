package demo.cert1;

import static demo.fabric.constant.FabricConstants.ADMIN_ATTRIBUTES;
import static demo.fabric.constant.FabricConstants.CLIENT_IDENTITY_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuite.Factory;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCACertificateRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.HFCAX509Certificate;
import org.hyperledger.fabric_ca.sdk.helper.Config;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import demo.common.TestHelper;
import demo.fabric.client.FabricCertClient;
import demo.fabric.client.FabricClientFactory;
import demo.fabric.dto.FabricUserContext;

/**
 *
 */
public class Cert1Tests {

    FabricCertClient certClient;
    HFCAClient caClient;
    FabricUserContext caAdmin;

    @BeforeClass
    public static void classSetUp() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.WARN);
    }

    @Before
    public void setUp() throws Exception {
        String name = "ca0";
        String url = "https://127.0.0.1:7054";
        Properties properties = new Properties();
        File certFile = new File("src/test/fixture/cert1/ca-msp/ca-cert.pem");
        properties.setProperty("pemFile", certFile.getAbsolutePath());
        properties.setProperty("allowAllHostNames", "true"); //testing environment only NOT FOR PRODUCTION!

        caClient = FabricClientFactory.createCaClient(name, url, properties);

        certClient = new FabricCertClient();

        caAdmin = FabricUserContext.builder()
                                   .name("admin@RootCA")
                                   .enrollmentSecret("adminpw")
                                   .build();

        TestHelper.out("> Try to register ca admin..");
        Enrollment enroll = caClient.enroll(caAdmin.getName(), caAdmin.getEnrollmentSecret());
        caAdmin.setEnrollment(enroll);
        TestHelper.out("> Success to register..");
    }

    // ca info 파싱
    @Test
    public void displayCaInfo() throws Exception {
        HFCAInfo info = caClient.info();
        Decoder decoder = Base64.getDecoder();
        byte[] issuerPublicKey = decoder.decode(info.getIdemixIssuerPublicKey().getBytes());
        byte[] issuerRevocationPulicKey = decoder.decode(info.getIdemixIssuerRevocationPublicKey().getBytes());
        String certChain = new String(decoder.decode(info.getCACertificateChain()));
        TestHelper.out("> issuer public key len : %d", issuerPublicKey.length);
        TestHelper.out("> issuer revocation public key len : %d", issuerRevocationPulicKey.length);
        TestHelper.out("> cert chain : \n%s", certChain);
    }

    // affiliation 추가 | identity 추가 | enroll | reenroll | revoke | reenroll
    @Test
    public void registerAffAndIdentityAndEnrollment() throws Exception {
        FabricUserContext peerorg1Admin = FabricUserContext.builder()
                                                           .name("peerorg1Admin")
                                                           .password("passpw")
                                                           .affiliation("peerorg1")
                                                           .build();

        // 1) affiliation 추가
        TestHelper.out("## Check affilication > %s", peerorg1Admin.getAffiliation());
        Optional<HFCAAffiliation> affOptional = certClient.getAffiliationByName(caClient,
                                                                                caAdmin.getEnrollment(),
                                                                                peerorg1Admin.getAffiliation());

        if (!affOptional.isPresent()) {
            TestHelper.out("> Try to create new affiliation");
            certClient.createNewAffiliation(caClient, caAdmin.getEnrollment(), peerorg1Admin.getAffiliation());
        } else {
            TestHelper.out("> Already exist");
        }

        // 2) identity 추가
        TestHelper.out("## Try to check identity %s", peerorg1Admin.getName());
        Optional<HFCAIdentity> identityOptional = certClient.getIdentityByName(caClient,
                                                                               caAdmin.getEnrollment(),
                                                                               peerorg1Admin.getName());
        if (!identityOptional.isPresent()) {
            TestHelper.out("> not exist. will register");
            boolean result = certClient.registerNewIdentity(caClient, caAdmin.getEnrollment(),
                                                            CLIENT_IDENTITY_TYPE,
                                                            peerorg1Admin.getName(),
                                                            peerorg1Admin.getPassword(),
                                                            peerorg1Admin.getAffiliation(), ADMIN_ATTRIBUTES);

            if (result) {
                peerorg1Admin.setEnrollmentSecret(peerorg1Admin.getPassword());
            }

            TestHelper.out(">> Identity result > " + result);
        } else {
            TestHelper.out("> Already exist identity");
            peerorg1Admin.setEnrollmentSecret(peerorg1Admin.getPassword());
        }

        // 3) enrollment
        TestHelper.out("## Try to check enrollments with filter with enrollment id : %s",
                       peerorg1Admin.getName());
        HFCACertificateRequest requestFilter = caClient.newHFCACertificateRequest();
        requestFilter.setEnrollmentID(peerorg1Admin.getName());
        List<HFCAX509Certificate> certs = certClient.getCertificates(caClient, caAdmin.getEnrollment(),
                                                                     requestFilter);
        if (!certs.isEmpty()) {
            TestHelper.out("> Already exist certs. size : %d", certs.size());
            HFCAX509Certificate cert = certs.get(0);
            displayX509Cert(cert.getX509());
        }

        TestHelper.out("> Try to enroll %s(%s)", peerorg1Admin.getName(), peerorg1Admin.getEnrollmentSecret());
        Enrollment enrollment = certClient.enroll(caClient, peerorg1Admin.getName(),
                                                  peerorg1Admin.getEnrollmentSecret());

        peerorg1Admin.setEnrollment(enrollment);
        TestHelper.out(">> Success to enroll");
        TestHelper.out(peerorg1Admin.getEnrollment().getCert());
        displayX509CertString(enrollment.getCert());

        // 5) reenroll
        TestHelper.out("## Try to reenroll cert");
        Enrollment tempCert = caClient.reenroll(peerorg1Admin);
        displayX509CertString(tempCert.getCert());
        TestHelper.out("> Success to reenroll");

        // 4) revoke
        caClient.revoke(caAdmin, tempCert, "Reason from revoke request");
        TestHelper.out("> Success to revoke..");

        // 5) reenroll
        TestHelper.out("## Try to reenroll after revoke another cert");
        Enrollment reenroll = caClient.reenroll(peerorg1Admin);
        TestHelper.out("> Success to reenroll");
        displayX509CertString(reenroll.getCert());
    }

    // affiliation 추가 | identity 추가 | enroll1 | enroll2 | revoke enroll2 | enroll3
    @Test
    public void registerAndEnrollAndRevoke() throws Exception {
        FabricUserContext peerorg1Admin = FabricUserContext.builder()
                                                           .name("peerorg1Admin")
                                                           .password("passpw")
                                                           .affiliation("peerorg1")
                                                           .build();

        createAffiliationAndRegister(peerorg1Admin);
        // 3) enroll
        Enrollment enrollment = certClient.enroll(caClient, peerorg1Admin.getName(),
                                                  peerorg1Admin.getEnrollmentSecret());
        peerorg1Admin.setEnrollment(enrollment);
        TestHelper.out(">> First enroll..");
        displayX509CertString(enrollment.getCert());

        // 4) enroll again
        Enrollment enrollment2 = certClient.enroll(caClient, peerorg1Admin.getName(),
                                                   peerorg1Admin.getEnrollmentSecret());
        TestHelper.out(">> Second enroll..");
        displayX509CertString(enrollment2.getCert());

        // 4) revoke
        caClient.revoke(caAdmin, enrollment, "Reason from revoke request");
        TestHelper.out("> Success to revoke..");

        Enrollment enrollment3 = certClient.enroll(caClient, peerorg1Admin.getName(),
                                                   peerorg1Admin.getEnrollmentSecret());
        TestHelper.out(">> Third enroll..");
        displayX509CertString(enrollment3.getCert());
    }

    // enroll1 | enroll2(fail) | reenroll1
    @Test
    public void testMaxEnrollments() throws Exception {
        FabricUserContext peerorg1Admin = FabricUserContext.builder()
                                                           .name("peerorg1Admin")
                                                           .password("passpw")
                                                           .affiliation("peerorg1")
                                                           .build();

        createAffiliationAndRegister(peerorg1Admin);

        // enroll
        TestHelper.out("## Try to enroll");
        Enrollment enroll = certClient.enroll(caClient, peerorg1Admin.getName(),
                                              peerorg1Admin.getEnrollmentSecret());
        peerorg1Admin.setEnrollment(enroll);
        TestHelper.out("> Success to enroll");
        displayX509CertString(enroll.getCert());

        try {
            certClient.enroll(caClient, peerorg1Admin.getName(),
                              peerorg1Admin.getEnrollmentSecret());
            fail();
        } catch (Exception e) {
            TestHelper.out("Failed to enroll");
            e.printStackTrace();
        }

        TestHelper.out("## Try to reenroll");
        Enrollment reenroll = caClient.reenroll(peerorg1Admin);
        TestHelper.out("## success to reenroll");
        displayX509CertString(reenroll.getCert());
    }

    @Test
    public void compareEnrollAndReEnroll() throws Exception {
        FabricUserContext peerorg1Admin = FabricUserContext.builder()
                                                           .name("peerorg1Admin")
                                                           .password("passpw")
                                                           .affiliation("peerorg1")
                                                           .build();

        createAffiliationAndRegister(peerorg1Admin);

        TestHelper.out("## Try to enroll");
        Enrollment enroll = certClient.enroll(caClient, peerorg1Admin.getName(),
                                              peerorg1Admin.getEnrollmentSecret());
        peerorg1Admin.setEnrollment(enroll);
        Optional<HFCAIdentity> identityOptional = certClient.getIdentityByName(
                caClient,
                peerorg1Admin.getEnrollment(),
                peerorg1Admin.getName()
        );
        assertThat(identityOptional.isPresent()).isTrue();

        TestHelper.out("> success to enroll");
        displayX509CertString(enroll.getCert());

        TestHelper.out("## Try to enroll again");
        Enrollment enroll2 = certClient.enroll(caClient, peerorg1Admin.getName(),
                                               peerorg1Admin.getEnrollmentSecret());
        TestHelper.out("> success to enroll");
        displayX509CertString(enroll2.getCert());

        TestHelper.out("## Try to reenroll");
        Enrollment enroll3 = caClient.reenroll(peerorg1Admin);
        TestHelper.out("> success to reenroll");
        displayX509CertString(enroll3.getCert());
    }

    private void createAffiliationAndRegister(FabricUserContext fabricUserContext) throws Exception {
        // 1) affiliation 추가
        TestHelper.out("## Check affilication > %s", fabricUserContext.getAffiliation());
        Optional<HFCAAffiliation> affOptional = certClient.getAffiliationByName(
                caClient,
                caAdmin.getEnrollment(),
                fabricUserContext.getAffiliation()
        );

        if (!affOptional.isPresent()) {
            TestHelper.out("> Try to create new affiliation");
            certClient.createNewAffiliation(
                    caClient, caAdmin.getEnrollment(), fabricUserContext.getAffiliation()
            );
        } else {
            TestHelper.out("> Already exist");
        }

        // 2) identity 추가
        TestHelper.out("## Try to check identity %s", fabricUserContext.getName());
        Optional<HFCAIdentity> identityOptional = certClient.getIdentityByName(
                caClient,
                caAdmin.getEnrollment(),
                fabricUserContext.getName());

        if (!identityOptional.isPresent()) {
            TestHelper.out("> not exist. will register");
            boolean result = certClient.registerNewIdentity(caClient,
                                                            caAdmin.getEnrollment(),
                                                            CLIENT_IDENTITY_TYPE,
                                                            fabricUserContext.getName(),
                                                            fabricUserContext.getPassword(),
                                                            fabricUserContext.getAffiliation(),
                                                            ADMIN_ATTRIBUTES);

            if (result) {
                fabricUserContext.setEnrollmentSecret(fabricUserContext.getPassword());
            }

            TestHelper.out(">> Identity result > " + result);
        } else {
            TestHelper.out("> Already exist identity");
            fabricUserContext.setEnrollmentSecret(fabricUserContext.getPassword());
        }
    }

    private void displayX509Cert(X509Certificate cert) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd [HH:mm:ss,SSS]");

        TestHelper.out("///////////////////////////////////////////////");
        TestHelper.out("> not before : %s, not after : %s",
                       sdf.format(cert.getNotBefore()), sdf.format(cert.getNotAfter()));
        TestHelper.out("> issuer dn : %s", cert.getIssuerDN());// sun.security.x509.X500Name
    }

    private void displayX509CertString(String cert) throws Exception {
        try {
            // openssl x509 -text -in cert1.pem
            BufferedInputStream pem = new BufferedInputStream(new ByteArrayInputStream(cert.getBytes()));
            CertificateFactory certFactory = CertificateFactory.getInstance(
                    Config.getConfig().getCertificateFormat());
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(pem);

            displayX509Cert(certificate);
            // check Subject Alternative Names
            Collection<List<?>> altNames = certificate.getSubjectAlternativeNames();
            TestHelper.out("Display alt names : %s", (altNames == null ? "NULL" : "Size " + altNames.size()));

            if (altNames != null) {
                StringBuilder subAlts = new StringBuilder();

                for (List<?> item : altNames) {
                    int type = (Integer) item.get(0);
                    if (type == 2) {
                        subAlts.append((String) item.get(1));
                    }
                }

                TestHelper.out("> sub alts : %s", subAlts.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void temp() throws Exception {
        CryptoSuite cryptoSuite = Factory.getCryptoSuite();
        // String csr = cryptoSuite.generateCertificationRequest(user, keypair);
    }
}
