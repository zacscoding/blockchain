package demo.fabric.constants;

import java.util.Arrays;
import java.util.List;
import org.hyperledger.fabric_ca.sdk.Attribute;

/**
 * Fabric constants
 */
public class FabricConstants {

    public static final List<Attribute> ADMIN_ATTRIBUTES = Arrays.asList(
        new Attribute("hf.Registrar.Roles", "*")
        , new Attribute("hf.Registrar.DelegateRoles", "*")
        , new Attribute("hf.Registrar.Attributes", "*")
        , new Attribute("hf.GenCRL", "1")
        , new Attribute("hf.Revoker", "1")
        , new Attribute("hf.AffiliationMgr", "1")
        , new Attribute("hf.IntermediateCA", "1")
    );

    public static final String ORDERER_IDENTITY_TYPE = "orderer";
    public static final String PEER_IDENTITY_TYPE = "peer";
    public static final String CLIENT_IDENTITY_TYPE = "client";
}
