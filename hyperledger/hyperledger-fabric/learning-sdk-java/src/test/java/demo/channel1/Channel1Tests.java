package demo.channel1;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import demo.common.TestHelper;
import demo.fabric.client.FabricChannelClient;
import demo.fabric.dto.FabricOrdererContext;
import demo.fabric.dto.FabricOrgContext;
import demo.fabric.dto.FabricOrgType;
import demo.fabric.dto.FabricPeerContext;
import demo.fabric.dto.FabricUserContext;
import demo.fabric.util.FabricCertParser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.protos.common.Common.Block;
import org.hyperledger.fabric.protos.common.Common.Envelope;
import org.hyperledger.fabric.sdk.BlockChecker;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Channel1Tests {

    static final String HOST = "127.0.0.1";
    static final String CHANNEL1 = "channel1";

    static final String ORDERER1 = "orderer1";
    static final String ORDERER2 = "orderer2";
    static final String PEER1 = "peer1";
    static final String PEER2 = "peer2";
    static final String ADMIN = "Admin";

    FabricChannelClient channelClient = FabricChannelClient.INSTANCE;

    FabricOrgContext ordererorg1;
    FabricOrgContext ordererorg2;
    FabricOrgContext peerorg1;
    FabricOrgContext peerorg2;

    byte[] channel1Config;

    @BeforeClass
    public static void classSetUp() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.WARN);
    }

    @Before
    public void setUp() throws Exception {
        setUpContexts();
        setUpMaterials();
    }

    @Test
    public void runTests() throws Exception {
        /**
         * "channel1" 요청 (to orderers)
         */
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(peerorg1.getAdmin());

        Channel channel1 = channelClient.createChannel(
            client, CHANNEL1, channel1Config,
            Arrays.asList(
                ordererorg1.getOrderers().get(ORDERER1)
                , ordererorg2.getOrderers().get(ORDERER1)
            ),
            // 컨소시엄 멤버 중 아무나 하나만 서명 해도 채널은 생성 가능 한 듯
            Arrays.asList(
                peerorg1.getAdmin()
            )
        );

        /**
         * ordererorg1 의 orderer1 로 sdk.Channel 인스턴스 빌드 후 config check
         */
        HFClient orderer1Client = HFClient.createNewInstance();
        orderer1Client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        orderer1Client.setUserContext(ordererorg1.getAdmin());

        Channel loadCh1 = channelClient.buildChannel(orderer1Client, CHANNEL1
            , Arrays.asList(ordererorg1.getOrderers().get(ORDERER1)), null
        );
        displayLastConfigBlock(loadCh1);

        /**
         * ordererorg2 의 orderer1 로 sdk.Channel 인스턴스 빌드 후 config check
         */
        HFClient orderer2Client = HFClient.createNewInstance();
        orderer2Client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        orderer2Client.setUserContext(ordererorg2.getAdmin());

        Channel loadCh2 = channelClient.buildChannel(orderer2Client, CHANNEL1
            , Arrays.asList(ordererorg2.getOrderers().get(ORDERER1)), null
        );
        displayLastConfigBlock(loadCh2);
    }

    @Test
    public void loadSystemChannel() throws Exception {
        HFClient orderer1Client = HFClient.createNewInstance();
        orderer1Client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        orderer1Client.setUserContext(ordererorg1.getAdmin());

        Channel loadCh1 = channelClient.buildChannel(orderer1Client, "test-system-channel"
            , Arrays.asList(ordererorg1.getOrderers().get(ORDERER1)), null
        );
        Block configBlock = channelClient.getLastConfigBlock(loadCh1);
        for (int i = 0; i < 1000; i++) {
            try {
                Map<String, Object> result = BlockChecker.getData(configBlock, i);
                Envelope envelope = (Envelope) result.get("envelop");
            } catch (Exception e) {
                System.out.println("## Exception occur at " + i);
                break;
            }
        }
    }

    private void displayLastConfigBlock(Channel channel) throws Exception {
        Block configBlock = channelClient.getLastConfigBlock(channel);
        TestHelper.out("# Check last config block.");
        TestHelper.out(">> block number : %d", configBlock.getHeader().getNumber());
        TestHelper.out(">> prev hash : %s", Hex.toHexString(configBlock.getHeader().getPreviousHash().toByteArray()));
    }

    private void setUpMaterials() throws Exception {
        channel1Config = IOUtils.toByteArray(
            new File("src/test/fixture/channel1/configtx/channel1.tx").toURI()
        );
    }

    private void setUpContexts() throws Exception {
        /**
         * ordererorg1
         */
        ordererorg1 = FabricOrgContext.builder()
            .domain("ordererorg1.example.com")
            .name("ordererorg1")
            .orgType(FabricOrgType.ORDERER)
            .build();

        ordererorg1.addOrderer(
            ORDERER1,
            FabricOrdererContext.builder()
                .name("orderer1." + ordererorg1.getDomain())
                .location("grpc://" + HOST + ":7050")
                .properties(FabricOrdererContext.appendDefaultProperties(null))
                .build()
        );

        ordererorg1.addUser(ADMIN,
            FabricUserContext.builder()
                .name("Admin")
                .affiliation("ordererorg1")
                .mspId("ordererorg1")
                .isAdmin(true)
                .orgType(FabricOrgType.ORDERER)
                .enrollment(
                    readEnrollment(FabricOrgType.ORDERER, ordererorg1.getDomain(), ADMIN)
                )
                .build()
        );

        /**
         * ordererorg2
         */
        ordererorg2 = FabricOrgContext.builder()
            .domain("ordererorg2.example.com")
            .name("ordererorg2")
            .orgType(FabricOrgType.ORDERER)
            .build();

        ordererorg2.addOrderer(
            ORDERER1,
            FabricOrdererContext.builder()
                .name("orderer1." + ordererorg2.getDomain())
                .location("grpc://" + HOST + ":8050")
                .properties(FabricOrdererContext.appendDefaultProperties(null))
                .build()
        );

        ordererorg2.addUser(ADMIN,
            FabricUserContext.builder()
                .name("Admin")
                .affiliation("ordererorg2")
                .mspId("ordererorg2")
                .isAdmin(true)
                .orgType(FabricOrgType.ORDERER)
                .enrollment(
                    readEnrollment(FabricOrgType.ORDERER, ordererorg2.getDomain(), ADMIN)
                )
                .build()
        );

        /**
         * peerorg1
         */
        peerorg1 = FabricOrgContext.builder()
            .domain("peerorg1.example.com")
            .name("peerorg1")
            .orgType(FabricOrgType.PEER)
            .build();

        peerorg1.addPeer(
            PEER1,
            FabricPeerContext.builder()
                .name("peer1")
                .location("grpc://" + HOST + ":7051")
                .properties(FabricPeerContext.appendDefaultProperties(null))
                .hostAndPort("peer1." + peerorg1.getDomain() + ":7051")
                .peerRoles(FabricPeerContext.createDefaultPeerRoles())
                .build()
        );

        peerorg1.addUser(ADMIN,
            FabricUserContext.builder()
                .name("Admin")
                .affiliation("peerorg1")
                .mspId("peerorg1")
                .isAdmin(true)
                .orgType(FabricOrgType.PEER)
                .enrollment(
                    readEnrollment(FabricOrgType.PEER, peerorg1.getDomain(), ADMIN)
                )
                .build()
        );

        /**
         * peerorg1
         */
        peerorg2 = FabricOrgContext.builder()
            .domain("peerorg2.example.com")
            .name("peerorg2")
            .orgType(FabricOrgType.PEER)
            .build();

        peerorg2.addPeer(
            PEER1,
            FabricPeerContext.builder()
                .name("peer2")
                .location("grpc://" + HOST + ":8051")
                .properties(FabricPeerContext.appendDefaultProperties(null))
                .hostAndPort("peer1." + peerorg2.getDomain() + ":7051")
                .peerRoles(FabricPeerContext.createDefaultPeerRoles())
                .build()
        );

        peerorg2.addUser(ADMIN,
            FabricUserContext.builder()
                .name("Admin")
                .affiliation("peerorg2")
                .mspId("peerorg2")
                .isAdmin(true)
                .orgType(FabricOrgType.PEER)
                .enrollment(
                    readEnrollment(FabricOrgType.PEER, peerorg2.getDomain(), ADMIN)
                )
                .build()
        );
    }

    private Enrollment readEnrollment(FabricOrgType orgType, String org, String name)
        throws Exception {

        String dirPath = "src/test/fixture/channel1/crypto-config/{ORG_TYPE}"
            + "/{org}/users/{name}@{org}/msp";

        String orgTypeValue = orgType == FabricOrgType.ORDERER
            ? "ordererOrganizations" : "peerOrganizations";

        String replaced = dirPath.replace("{ORG_TYPE}", orgTypeValue)
            .replace("{org}", org)
            .replace("{name}", name);

        Path mspPath = Paths.get(replaced);

        // 1) keystore
        File[] keyFiles = mspPath.resolve("keystore").toFile().listFiles();
        if (keyFiles.length != 1) {
            throw new IllegalStateException("Failed to read enrollment because multiple key file."
                + "org : " + org + ", name : " + name);
        }

        byte[] key = IOUtils.toByteArray(keyFiles[0].toURI());

        // 2) signcerts
        File[] certFiles = mspPath.resolve("signcerts").toFile().listFiles();
        if (certFiles.length != 1) {
            throw new IllegalStateException("Failed to read enrollment because multiple cert file."
                + "org : " + org + ", name : " + name);
        }

        byte[] cert = IOUtils.toByteArray(certFiles[0].toURI());

        return FabricCertParser.x509EnrollmentOf(key, cert);
    }
}
