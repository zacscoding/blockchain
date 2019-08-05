package demo.cert1;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import demo.common.TestHelper;
import demo.fabric.dto.FabricUserContext;
import java.io.File;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Properties;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation.HFCAAffiliationResp;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.AffiliationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Cert1Tests {

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
        caClient = HFCAClient.createNewInstance(name, url, properties);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        caAdmin = FabricUserContext.builder()
            .name("admin@RootCA")
            .enrollmentSecret("adminpw")
            .build();

        TestHelper.out("> Try to register ca admin..");
        Enrollment enroll = caClient.enroll(caAdmin.getName(), caAdmin.getEnrollmentSecret());
        caAdmin.setEnrollment(enroll);
        TestHelper.out("> Success to register..");
    }

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

    @Test
    public void registerAndModify() throws Exception {
        FabricUserContext peerorg1Admin = FabricUserContext
            .builder()
            .name("peerorg1")
            .enrollmentSecret("passpw")
            .affiliation("peerorg1")
            .build();

        HFCAAffiliation caAffiliations = caClient.getHFCAAffiliations(caAdmin);
        HFCAAffiliation affiliation = getAffiliationByName(caAffiliations, peerorg1Admin.getAffiliation());

        TestHelper.out("> Check affiliation %s exist or not", peerorg1Admin.getAffiliation());

        if (affiliation == null) {
            TestHelper.out(">> Not exist. so create new affiliation.");
            HFCAAffiliation hfcaAffiliation = caClient.newHFCAAffiliation(peerorg1Admin.getAffiliation());
            HFCAAffiliationResp response = hfcaAffiliation.create(caAdmin);
            if (!is2xxSuccessful(response)) {
                throw new Exception("Failed to create new affiliation : " + response.getStatusCode());
            }
            TestHelper.out(">> success");
        } else {
            TestHelper.out(">> already exist");
        }
    }

    private HFCAAffiliation getAffiliationByName(HFCAAffiliation affiliation, String name) throws AffiliationException {
        if (name == null || name.length() == 0 || affiliation == null) {
            return null;
        }

        if (name.equals(affiliation.getName())) {
            return affiliation;
        }

        for (HFCAAffiliation child : affiliation.getChildren()) {
            HFCAAffiliation found = getAffiliationByName(child, name);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private boolean is2xxSuccessful(HFCAAffiliationResp response) {
        if (response == null) {
            return false;
        }

        return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }

}
