package demo.channel;

import static org.junit.Assert.assertEquals;

import demo.common.TestCaInfoSupplier;
import java.io.IOException;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.junit.Test;

/**
 * 채널 생성 테스트
 */
public class ChannelTest {

    String channelName = "ch1";
    String localhost = "10.0.164.32";
    HFCAClient caClient = TestCaInfoSupplier.getCaClient();
    SampleUser caAdmin = TestCaInfoSupplier.getAdmin();
    SampleUser peerAdmin = TestCaInfoSupplier.PEER_ORG1_ADMIN;
    String configtxlatorLocation = "http://" + localhost + ":7059";

    @Test
    public void channelConfigurationTests() throws Exception {
        long start = System.currentTimeMillis();
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(peerAdmin);
        Channel channel = client.newChannel(channelName);

        // orderer
        channel.addOrderer(
            client.newOrderer("orderer0-testnet", "grpc://" + localhost + ":7050")
        );

        // peers
        Peer peer = client.newPeer("peer0-PeerOrg1-testnet", "grpc://" + localhost + ":7051");
        channel.addPeer(peer);
        channel.initialize();
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("## elapsed (reconstruct channel) :: " + elapsed);

        // config block 가져오기 ( peers with shuffle + gRpc)
        byte[] channelConfigurationBytes = channel.getChannelConfigurationBytes();

        HttpClient httpclient = HttpClients.createDefault();
        String responseAsString = configTxlatorDecode(httpclient, channelConfigurationBytes);
        System.out.println(responseAsString);

        start = System.currentTimeMillis();
        channel.serializeChannel();
        elapsed = System.currentTimeMillis() - start;
        System.out.println("## elapsed (serialized channel) :: " + elapsed);
    }

    private String configTxlatorDecode(HttpClient httpclient, byte[] channelConfigurationBytes) throws IOException {
        HttpPost httppost = new HttpPost(configtxlatorLocation + "/protolator/decode/common.Config");
        httppost.setEntity(new ByteArrayEntity(channelConfigurationBytes));
        HttpResponse response = httpclient.execute(httppost);
        int statuscode = response.getStatusLine().getStatusCode();
        //  out("Got %s status for decoding current channel config bytes", statuscode);
        assertEquals(200, statuscode);
        return EntityUtils.toString(response.getEntity());
    }
}
