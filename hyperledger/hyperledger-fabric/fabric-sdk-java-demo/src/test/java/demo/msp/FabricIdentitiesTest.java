package demo.msp;

import demo.common.LogLevelUtil;
import demo.common.TestCaInfoSupplier;
import demo.common.TestHelper;
import java.util.Collection;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;

/**
 *
 */
public class FabricIdentitiesTest {

    SampleUser admin;
    HFCAClient caClient;

    @Before
    public void setUp() {
        LogLevelUtil.setInfo();
        admin = TestCaInfoSupplier.getAdmin();
        caClient = TestCaInfoSupplier.getCaClient();
    }

    @Test
    public void crudIdentities() throws Exception {
        // 1) identity 추가
        RegistrationRequest rr = new RegistrationRequest("test-identity2", "PeerOrg1");
        String ordererAdminPassword = "passwd";
        rr.setSecret(ordererAdminPassword);
        rr.addAttribute(new Attribute("hf.Registrar.Roles", "*"));
        rr.addAttribute(new Attribute("hf.Registrar.DelegateRoles", "*"));
        rr.addAttribute(new Attribute("hf.Registrar.Attributes", "*"));
        rr.addAttribute(new Attribute("hf.GenCRL", "1"));
        rr.addAttribute(new Attribute("hf.Revoker", "1"));
        rr.addAttribute(new Attribute("hf.AffiliationMgr", "1"));
        rr.addAttribute(new Attribute("hf.IntermediateCA", "1"));

        String secret = caClient.register(rr, admin);
        System.out.println("## First register :: " + secret);

        rr.setSecret("newPasswd");
        String newSecret = caClient.register(rr, admin);
        System.out.println("## Secod register :: " + newSecret);
    }
}
