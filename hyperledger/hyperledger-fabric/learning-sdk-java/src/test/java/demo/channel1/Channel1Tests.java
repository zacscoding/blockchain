package demo.channel1;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.protos.common.Common.Block;
import org.hyperledger.fabric.protos.common.Common.Envelope;
import org.hyperledger.fabric.sdk.BlockChecker;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
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
 * Modify from hyperledger fabric-sdk-java
 * https://github.com/hyperledger/fabric-sdk-java
 */
public class Channel1Tests {

    // test commons
    static final String HOST = "127.0.0.1";
    static final String TEST_FIXTURES_PATH = "src/test/fixture";

    // channel
    static final String CHANNEL1 = "channel1";

    // chain code
    static final String CHAIN_CODE_FILEPATH = "channel1/gocc/sample1";
    static final String CHAIN_CODE_NAME = "example_cc_go";
    static final String CHAIN_CODE_VERSION = "1";
    static final String CHAIN_CODE_PATH = "github.com/example01/cmd";
    static final Type CHAIN_CODE_LANG = Type.GO_LANG;

    // fabric component
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
        // - "channel1" 생성 (to orderers)
        // - peer 참여 in peerorg1, peerorg2
        createChannelAndJoinPeers();

        // - peer1.peerorg1 | peer1.peerorg2 chaincode install
        // - instantiate chaincode
        runChannels();
    }

    private void runChannels() throws Exception {
        /**
         * load channel
         */
        TestHelper.out("## load channel");
        HFClient client = FabricClientFactory.createHFClient(peerorg1.getAdmin());
        Channel channel = channelClient.buildChannel(
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
        channel.initialize();
        TestHelper.out("> success to load");

        /**
         * Setup chain code
         */
        //A test class to capture chaincode events
        class ChaincodeEventCapture {
            final String handle;
            final BlockEvent blockEvent;
            final ChaincodeEvent chaincodeEvent;

            ChaincodeEventCapture(String handle, BlockEvent blockEvent, ChaincodeEvent chaincodeEvent) {
                this.handle = handle;
                this.blockEvent = blockEvent;
                this.chaincodeEvent = chaincodeEvent;
            }
        }

        Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>();

        String chaincodeEventListenerHandle = channel.registerChaincodeEventListener(
                Pattern.compile(".*"),  // chaincode pattern
                Pattern.compile(Pattern.quote("event")), // event pattern
                (handle, blockEvent, chaincodeEvent) -> {
                    chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

                    String es =
                            blockEvent.getPeer() != null ? blockEvent.getPeer().getName() : "peer was null!!!";
                    TestHelper.out("RECEIVED Chaincode event with handle: %s, chaincode Id: %s, "
                                   + "chaincode event name: %s, "
                                   + "transaction id: %s, event payload: \"%s\", from event source: %s",
                                   handle, chaincodeEvent.getChaincodeId(),
                                   chaincodeEvent.getEventName(),
                                   chaincodeEvent.getTxId(),
                                   new String(chaincodeEvent.getPayload()), es);

                });

        /**
         * Install chain code
         */
        HFClient peerOrg1Client = FabricClientFactory.createHFClient(peerorg1.getAdmin());
        Channel loadChannel1 = channelClient.buildChannel(
                peerOrg1Client, CHANNEL1,
                Arrays.asList(
                        ordererorg1.getOrderers().get(ORDERER1)
                        , ordererorg2.getOrderers().get(ORDERER1)
                ),
                Arrays.asList(
                        peerorg1.getPeers().get(PEER1)
                )
        );
        loadChannel1.initialize();
        installChainCode(
                ChaincodeID.newBuilder()
                           .setName(CHAIN_CODE_NAME)
                           .setVersion(CHAIN_CODE_VERSION)
                           .setPath(CHAIN_CODE_PATH)
                           .build(),
                loadChannel1, peerorg1.getAdmin()
        );

        HFClient peerOrg2Client = FabricClientFactory.createHFClient(peerorg2.getAdmin());
        Channel loadChannel2 = channelClient.buildChannel(
                peerOrg2Client, CHANNEL1,
                Arrays.asList(
                        ordererorg1.getOrderers().get(ORDERER1)
                        , ordererorg2.getOrderers().get(ORDERER1)
                ),
                Arrays.asList(
                        peerorg2.getPeers().get(PEER1)
                )
        );
        loadChannel2.initialize();
        installChainCode(
                ChaincodeID.newBuilder()
                           .setName(CHAIN_CODE_NAME)
                           .setVersion(CHAIN_CODE_VERSION)
                           .setPath(CHAIN_CODE_PATH)
                           .build(),
                loadChannel2, peerorg2.getAdmin()
        );
    }

    private void installChainCode(ChaincodeID chaincodeID, Channel channel, FabricUserContext admin)
            throws Exception {

        HFClient client = FabricClientFactory.createHFClient(admin);

        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(
                Paths.get(TEST_FIXTURES_PATH, CHAIN_CODE_FILEPATH).toFile());
        installProposalRequest.setChaincodeVersion(CHAIN_CODE_VERSION);
        installProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);

        TestHelper.out("## Try to send install proposal");
        Collection<Peer> peers = channel.getPeers();
        int numInstallProposal = peers.size();

        Collection<ProposalResponse> responses = client.sendInstallProposal(installProposalRequest, peers);
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                TestHelper.out("Successful install proposal response Txid: %s from peer %s",
                               response.getTransactionID(), response.getPeer().getName());
                successful.add(response);
            } else {
                TestHelper.out("Fail message : %s, peer : %s", response.getMessage(), response.getPeer());
                failed.add(response);
            }
        }

        TestHelper.out("> try : %d | success : %d | fail : %d",
                       numInstallProposal, successful.size(), failed.size());
    }

    private void createChannelAndJoinPeers() throws Exception {
        /**
         * "channel1" 요청 (to orderers)
         */
        TestHelper.out("## Try to create channel");
        HFClient client = FabricClientFactory.createHFClient(peerorg1.getAdmin());
        Channel channel1 = channelClient.createChannel(
                client, CHANNEL1, channel1Config,
                Arrays.asList(
                        ordererorg1.getOrderers().get(ORDERER1)
                        , ordererorg2.getOrderers().get(ORDERER1)
                ),
                // 컨소시엄 멤버 중 아무나 하나만 서명 해도 채널은 생성 가능 한 듯
                Arrays.asList(
                        peerorg1.getAdmin(), peerorg2.getAdmin()
                )
        );
        TestHelper.out("> success");

        /**
         * ordererorg1 의 orderer1 로 sdk.Channel 인스턴스 빌드 후 config check
         */
        TestHelper.out("# try to get last config block");
        HFClient orderer1Client = FabricClientFactory.createHFClient(ordererorg1.getAdmin());

        Channel loadCh1 = channelClient.buildChannel(orderer1Client, CHANNEL1
                , Arrays.asList(ordererorg1.getOrderers().get(ORDERER1)), null
        );
        displayLastConfigBlock(loadCh1);
        TestHelper.out("> success");

        /**
         * ordererorg2 의 orderer1 로 sdk.Channel 인스턴스 빌드 후 config check
         */
        HFClient orderer2Client = FabricClientFactory.createHFClient(ordererorg2.getAdmin());
        Channel loadCh2 = channelClient.buildChannel(
                orderer2Client, CHANNEL1, Arrays.asList(ordererorg2.getOrderers().get(ORDERER1)), null
        );
        displayLastConfigBlock(loadCh2);

        /**
         * 채널에 피어 참여
         */
        client.setUserContext(peerorg1.getAdmin());
        channel1 = channelClient.joinPeer(client, channel1, peerorg1.getPeers().get(PEER1));
        channel1.initialize();
        channel1 = channelClient.updateAnchorPeers(client, channel1, Arrays.asList(peerorg1.getAdmin()),
                                                   Arrays.asList(peerorg1.getPeers().get(PEER1)), null);

        client.setUserContext(peerorg2.getAdmin());
        channel1 = channelClient.joinPeer(client, channel1, peerorg2.getPeers().get(PEER1));
        channel1 = channelClient.updateAnchorPeers(client, channel1, Arrays.asList(peerorg2.getAdmin()),
                                                   Arrays.asList(peerorg2.getPeers().get(PEER1)), null);
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
        TestHelper.out(">> prev hash : %s",
                       Hex.toHexString(configBlock.getHeader().getPreviousHash().toByteArray()));
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
                                 .name("peer2")
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
