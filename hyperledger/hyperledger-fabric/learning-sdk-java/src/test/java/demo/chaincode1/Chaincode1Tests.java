package demo.chaincode1;

import static java.lang.String.format;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import demo.common.TestHelper;
import demo.fabric.client.FabricChannelClient;
import demo.fabric.client.FabricClientFactory;
import demo.fabric.dto.FabricOrdererContext;
import demo.fabric.dto.FabricOrgContext;
import demo.fabric.dto.FabricOrgType;
import demo.fabric.dto.FabricPeerContext;
import demo.fabric.dto.FabricUserContext;
import demo.fabric.util.FabricCertParser;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
public class Chaincode1Tests {

    // test commons
    static final String HOST = "127.0.0.1";
    static final String TEST_FIXTURES_PATH = "src/test/fixture";

    // channel
    static final String CHANNEL1 = "channel1";

    // chain code
    // fabric component
    static final String ORDERER1 = "orderer1";
    static final String ORDERER2 = "orderer2";
    static final String PEER1 = "peer1";
    static final String PEER2 = "peer2";
    static final String ADMIN = "Admin";

    Channel channel1;

    FabricChannelClient channelClient = FabricChannelClient.INSTANCE;

    FabricOrgContext ordererorg1;
    FabricOrgContext ordererorg2;
    FabricOrgContext peerorg1;
    FabricOrgContext peerorg2;

    @BeforeClass
    public static void classSetUp() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.WARN);
    }

    @Before
    public void setUp() throws Exception {
        setUpContexts();
        setUpChannel();
    }

    @Test
    public void regexTest() {
        Pattern all = Pattern.compile(".*");
        for (int i = 0; i < 10; i++) {
            System.out.println(
                    all.matcher(UUID.randomUUID().toString()).matches()

            );
        }
    }

    @Test
    public void runSubscribe() throws Exception {
        channel1.registerBlockListener((event) -> {
            TestHelper.out("[%s]Listen block %d from %s",
                           Thread.currentThread().getName(), event.getBlockNumber(), event.getPeer().getName());

            for (TransactionEvent txEvent : event.getTransactionEvents()) {
                TestHelper.out("tx id : %s -> %d", txEvent.getTransactionID()
                        , txEvent.getTransactionActionInfoCount());

                for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo info
                        : txEvent.getTransactionActionInfos()) {
                    System.out.println("tx info :: " + info);

                    ChaincodeEvent chaincodeEvent = info.getEvent();
                    if (null != chaincodeEvent) {
                        System.out.println("Found chaincode event :: " + chaincodeEvent);
                    }
                }

            }
        });

        // Pattern chaincodeId, Pattern eventName, ChaincodeEventListener chaincodeEventListener
        Pattern all = Pattern.compile(".*");
        channel1.registerChaincodeEventListener(all, all, new ChaincodeEventListener() {
            @Override
            public void received(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
                TestHelper.out("[%s]Listen chain code event.. : %s -> %s",
                               Thread.currentThread().getName(), handle, chaincodeEvent.getEventName());
            }
        });
        TimeUnit.MINUTES.sleep(30);
    }

    /**
     * Setup a "channel1"
     */
    private void setUpChannel() throws Exception {
        HFClient client = FabricClientFactory.createHFClient(peerorg1.getAdmin());
        channel1 = channelClient.buildChannel(
                client, CHANNEL1,
                Arrays.asList(
                        ordererorg1.getOrderers().get(ORDERER1)
                        , ordererorg2.getOrderers().get(ORDERER1)
                ),
                Arrays.asList(
                        peerorg1.getPeers().get(PEER1),
                        peerorg2.getPeers().get(PEER1)
                )
        );
        channel1.initialize();
    }

    /**
     * Setup test contexts
     */
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
                                                     readEnrollment(FabricOrgType.ORDERER,
                                                                    ordererorg1.getDomain(), ADMIN)
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
                                                     readEnrollment(FabricOrgType.ORDERER,
                                                                    ordererorg2.getDomain(), ADMIN)
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
                                 .name(PEER1)
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
                                                  readEnrollment(FabricOrgType.PEER, peerorg1.getDomain(),
                                                                 ADMIN)
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
                                 .name(PEER2)
                                 .location("grpc://" + HOST + ":8051")
                                 .properties(FabricPeerContext.appendDefaultProperties(null))
                                 .hostAndPort("peer1." + peerorg2.getDomain() + ":8051")
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
                                                  readEnrollment(FabricOrgType.PEER, peerorg2.getDomain(),
                                                                 ADMIN)
                                          )
                                          .build()
        );
    }

    private Enrollment readEnrollment(FabricOrgType orgType, String org, String name)
            throws Exception {

        String dirPath = "src/test/fixture/chaincode1/crypto-config/{ORG_TYPE}"
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
