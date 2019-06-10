package demo.common;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdkintegration.SampleStore;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

/**
 *
 */
@Slf4j
public class TestCaInfoSupplier {

    public static final String TEST_ADMIN_ORG = "org1";
    public static final String TEST_ADMIN_NAME = "admin@RootCA";
    public static final String TEST_ADMIN_PW = "rootcaadmin";
    public static final String SAMPLE_STORE_PATH1 = "src/test/resources/HFCSampletest.properties";
    public static final String CA_NAME1 = "ca0.testnet.com";

    public static final SampleStore SAMPLE_STORE;
    public static final HFCAClient CA_CLIENT;
    public static final SampleUser ADMIN;
    public static CryptoSuite CRYPTO;

    static {
        try {
            logger.info("## Test info supplier setup...");
            CRYPTO = CryptoSuite.Factory.getCryptoSuite();
            SAMPLE_STORE = new SampleStore(new File(getSampleStorePath()));
            CA_CLIENT = HFCAClient.createNewInstance(
                getCaName(),
                getCaLocation(),
                null
            );
            CA_CLIENT.setCryptoSuite(CRYPTO);

            ADMIN = SAMPLE_STORE.getMember(TEST_ADMIN_NAME, TEST_ADMIN_ORG);
            if (!ADMIN.isEnrolled()) {
                ADMIN.setEnrollment(CA_CLIENT.enroll(ADMIN.getName(), TEST_ADMIN_PW));
            }
            logger.info("////////////////////////////////////////////////////////////////////////////////////////////");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HFCAClient getCaClient() {
        return CA_CLIENT;
    }

    public static SampleUser getAdmin() {
        return ADMIN;
    }

    private static String getSampleStorePath() {
        return SAMPLE_STORE_PATH1;
    }

    private static String getCaName() {
        return CA_NAME1;
    }

    private static String getCaLocation() {
        return "http://10.0.164.32:7054";
    }
}
