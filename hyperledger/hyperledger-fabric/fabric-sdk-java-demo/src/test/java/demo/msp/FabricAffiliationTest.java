package demo.msp;

import static org.assertj.core.api.Assertions.assertThat;

import demo.common.LogLevelUtil;
import demo.common.TestCaInfoSupplier;
import demo.common.TestHelper;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation.HFCAAffiliationResp;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.AffiliationException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.junit.Before;
import org.junit.Test;

/**
 * Fabric affiliation list
 */
public class FabricAffiliationTest {

    SampleUser admin;
    HFCAClient caClient;

    @Before
    public void setUp() {
        LogLevelUtil.setInfo();
        admin = TestCaInfoSupplier.getAdmin();
        caClient = TestCaInfoSupplier.getCaClient();
    }


    @Test
    public void getAffiliationsWithRecursively() throws Exception {
        String[] existAffiliations = new String[]{
            "org1",
            "org1.department1",
            "org1.depth1",
            "org1.depth1",
        };

        String[] notExistAffiliations = new String[]{
            "org333"
        };

        for (String affiliation : existAffiliations) {
            assertThat(getAffiliation(affiliation, admin)).isNotNull();
        }

        for (String affiliation : notExistAffiliations) {
            assertThat(getAffiliation(affiliation, admin)).isNull();
        }
    }

    @Test
    public void crudAffiliations() throws Exception {
        String affiliation1 = "newOrg1";

        // affiliation 조회
        HFCAAffiliation aff1 = getAffiliation(affiliation1, admin);
        if (aff1 != null) {
            //  affiliation 삭제
            HFCAAffiliationResp aff1DeleteResponse = aff1.delete(admin, true);
            TestHelper.out("Exist %s affiliation. Will try to remove >> %d"
                , affiliation1, aff1DeleteResponse.getStatusCode());
        }

        //  affiliation 생성
        aff1 = caClient.newHFCAAffiliation(affiliation1);
        HFCAAffiliationResp aff1CreateResponse = aff1.create(admin);
        TestHelper.out("Create %s affiliation >> %d", affiliation1, aff1CreateResponse.getStatusCode());

        // affiliation update
        String updateAffiliation1 = "updateNewOrg1";
        aff1.setUpdateName(updateAffiliation1);
        HFCAAffiliationResp aff1UpdateResponse = aff1.update(admin, true);
        TestHelper.out("Update %s affiliation >> %d", updateAffiliation1, aff1UpdateResponse.getStatusCode());

        // 조회
        boolean existPrevAff = getAffiliation(affiliation1, admin) != null;
        TestHelper.out("Read %s affiliation >> %s", affiliation1, existPrevAff);
        boolean existNewAff = getAffiliation(updateAffiliation1, admin) != null;
        TestHelper.out("Read %s affiliation >> %s", updateAffiliation1, existNewAff);

        HFCAAffiliationResp deleteResponse = aff1.delete(admin, true);
        TestHelper.out("Delete %s affiliation >> %s", aff1.getName(), deleteResponse.getStatusCode());
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
}
