package demo.fabric.client;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperledger.fabric.protos.common.Common.Block;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Channel.PeerOptions;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
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
}
