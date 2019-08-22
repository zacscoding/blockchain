package demo.fabric.client;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import org.hyperledger.fabric.protos.common.Common.Block;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Channel.PeerOptions;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Peer.PeerRole;
import org.hyperledger.fabric.sdk.UpdateChannelConfiguration;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import demo.fabric.dto.FabricOrdererContext;
import demo.fabric.dto.FabricPeerContext;
import demo.fabric.dto.FabricUserContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class FabricChannelClient {

    public static final FabricChannelClient INSTANCE = new FabricChannelClient();

    private FabricChannelClient() {
    }

    /**
     * 채널 생성 요청 (to orderer)
     */
    public Channel createChannel(HFClient client, String name, byte[] config,
                                 List<FabricOrdererContext> ordererContexts,
                                 List<FabricUserContext> signers) throws Exception {

        requireNonNull(client, "client");
        requireNonNull(name, "name");
        requireNonNull(config, "config");
        Assert.isTrue(!CollectionUtils.isEmpty(ordererContexts), "ordererContexts must be not empty");
        Assert.isTrue(!CollectionUtils.isEmpty(signers), "signers must be not empty");

        ordererContexts = new ArrayList<>(ordererContexts);

        ChannelConfiguration channelConfiguration = new ChannelConfiguration(config);

        FabricOrdererContext ordererContext = ordererContexts.remove(0);
        Orderer orderer = client.newOrderer(ordererContext.getName(), ordererContext.getLocation(),
                                            ordererContext.getProperties());

        byte[][] configSignatures = getChannelConfigurationSignatures(client, channelConfiguration, signers);

        if (client.getUserContext() == null) {
            client.setUserContext(randomPick(signers));
        }

        Channel channel = client.newChannel(name, orderer, channelConfiguration, configSignatures);

        for (FabricOrdererContext ordererCtx : ordererContexts) {
            channel.addOrderer(convertOrderer(client, ordererCtx));
        }

        return channel;
    }

    /**
     * 이미 생성 된 채널에 대하여 sdk.Channel 인스턴스 빌드
     */
    public Channel buildChannel(HFClient client, String name, List<FabricOrdererContext> ordererContexts
            , List<FabricPeerContext> peerContexts) throws Exception {

        requireNonNull(client, "client");

        Channel channel = client.getChannel(requireNonNull(name, "name"));

        if (channel != null) {
            throw new IllegalArgumentException("Already exist channel " + name + " in client");
        }

        channel = client.newChannel(name);

        if (!CollectionUtils.isEmpty(ordererContexts)) {
            for (FabricOrdererContext ordererContext : ordererContexts) {
                channel.addOrderer(convertOrderer(client, ordererContext));
            }
        }

        if (!CollectionUtils.isEmpty(peerContexts)) {
            for (FabricPeerContext peerContext : peerContexts) {
                channel.addPeer(
                        convertPeer(client, peerContext),
                        PeerOptions.createPeerOptions().setPeerRoles(peerContext.getPeerRoles())
                );
            }
        }

        return channel;
    }

    /**
     * Channel에 피어 참여
     */
    public Channel joinPeer(HFClient client, Channel channel, FabricPeerContext peerContext) throws Exception {
        requireNonNull(client, "client must be not null");
        requireNonNull(channel, "channel must be not null");
        requireNonNull(peerContext, "peerContext must be not null");

        // 기본 PeerRoles 세팅
        if (CollectionUtils.isEmpty(peerContext.getPeerRoles())) {
            peerContext.setPeerRoles(createDefaultPeerRoles());
        }

        Peer peer = convertPeer(client, peerContext);
        return channel.joinPeer(peer, PeerOptions.createPeerOptions().setPeerRoles(peerContext.getPeerRoles()));
    }

    /**
     * 오더러로 부터 last config block 조회
     */
    public Block getLastConfigBlock(Channel channel) throws Exception {
        requireNonNull(channel, "channel must be not null");

        Method method = channel.getClass().getDeclaredMethod("getConfigurationBlock");
        method.setAccessible(true);

        Object result = method.invoke(channel);

        if (!result.getClass().isAssignableFrom(Block.class)) {
            throw new IllegalStateException("Cannot cast to Block after invoke getConfigurationBlock()");
        }

        return (Block) result;
    }

    public Channel updateAnchorPeers(HFClient client, Channel channel, List<FabricUserContext> signers,
                                     List<FabricPeerContext> addPeerContexts,
                                     List<FabricPeerContext> removePeerContexts) throws Exception {

        // 유효성 체크
        requireNonNull(client, "client must be not null");
        requireNonNull(channel, "channel must be not null");
        requireNonNull(signers, "signers must be not null");
        ensureChannelInitializedState(channel, "Must initialized channel before update anchor peers");

        List<String> peersToAdd = null;
        List<String> peersToRemove = null;

        if (!CollectionUtils.isEmpty(addPeerContexts)) {
            peersToAdd = new ArrayList<>(addPeerContexts.size());

            for (FabricPeerContext peerContext : addPeerContexts) {
                peersToAdd.add(peerContext.getHostAndPort());
            }
        }

        if (!CollectionUtils.isEmpty(removePeerContexts)) {
            peersToRemove = new ArrayList<>(removePeerContexts.size());

            for (FabricPeerContext peerContext : removePeerContexts) {
                peersToRemove.add(peerContext.getHostAndPort());
            }
        }

        // Channel 에 속한 Peer 대상으로 AnchorPeerConfigUpdateResult 가져오기
        Channel.AnchorPeersConfigUpdateResult configUpdateAnchorPeers = null;
        for (Peer peer : channel.getPeers()) {
            logger.debug("Try to get config update anchor peers from : {}", peer.getName());
            try {
                configUpdateAnchorPeers = channel.getConfigUpdateAnchorPeers(peer, signers.get(0), peersToAdd,
                                                                             peersToRemove);
                break;
            } catch (Exception e) {
                logger.debug("Failed to get config update anchor peers.");
            }
        }

        if (configUpdateAnchorPeers == null) {
            throw new Exception("Failed to getting config update anchor in channel " + channel.getName());
        }

        // update 할 피어가 존재하지 않는 경우
        if (configUpdateAnchorPeers.getUpdateChannelConfiguration() == null) {
            logger.debug("Skip update anchor peers because empty update peers");
            return channel;
        }

        // UpdateChannelConfiguration 서명
        byte[][] configSignatures = getUpdateChannelConfigurationSignatures(
                client, configUpdateAnchorPeers.getUpdateChannelConfiguration(), signers
        );

        channel.updateChannelConfiguration(configUpdateAnchorPeers.getUpdateChannelConfiguration(),
                                           configSignatures);

        return channel;
    }

    /**
     * channelConfiguration에 대하여 signer들의 서명
     */
    private byte[][] getChannelConfigurationSignatures(HFClient client,
                                                       ChannelConfiguration channelConfiguration,
                                                       List<FabricUserContext> signers) throws Exception {

        byte[][] configSignatures = new byte[signers.size()][];

        for (int i = 0; i < signers.size(); i++) {
            configSignatures[i] = client.getChannelConfigurationSignature(channelConfiguration, signers.get(i));
        }

        return configSignatures;
    }

    /**
     * UpdateChannelConfiguration 다중 Signer로 서명
     */
    private byte[][] getUpdateChannelConfigurationSignatures(HFClient client,
                                                             UpdateChannelConfiguration updateChannelConfiguration
            , List<FabricUserContext> signers) throws Exception {

        byte[][] configSignatures = new byte[signers.size()][];

        for (int i = 0; i < signers.size(); i++) {
            configSignatures[i] = client.getUpdateChannelConfigurationSignature(updateChannelConfiguration,
                                                                                signers.get(i));
        }

        return configSignatures;
    }

    /**
     * Channel 초기화 상태 체크
     */
    private void ensureChannelInitializedState(Channel channel, String message) {
        if (!channel.isInitialized()) {
            throw new IllegalArgumentException(message);
        }
    }

    private static <T> T randomPick(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(new Random().nextInt(list.size()));
    }

    private Orderer convertOrderer(HFClient client, FabricOrdererContext ctx) throws InvalidArgumentException {
        return client.newOrderer(ctx.getName(), ctx.getLocation(), ctx.getProperties());
    }

    private Peer convertPeer(HFClient client, FabricPeerContext ctx) throws InvalidArgumentException {
        return client.newPeer(ctx.getName(), ctx.getLocation(), ctx.getProperties());
    }

    private EnumSet<PeerRole> createDefaultPeerRoles() {
        return EnumSet.of(PeerRole.ENDORSING_PEER, PeerRole.LEDGER_QUERY, PeerRole.CHAINCODE_QUERY, PeerRole.EVENT_SOURCE);
        //return EnumSet.of(PeerRole.ENDORSING_PEER, PeerRole.LEDGER_QUERY, PeerRole.CHAINCODE_QUERY);
    }
}
