package demo.fabric.dto;

import java.util.EnumSet;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hyperledger.fabric.sdk.Peer.PeerRole;

/**
 * Fabric peer 관련 context
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FabricPeerContext {

    private String name;                    // peer 이름
    private String location;                // peer 주소 e.g) grpc://localhost:7051
    private Properties properties;          // peer properties
    private String hostAndPort;             // peer host e.g) peer0.peerorg1.testnet.com:7051
    private EnumSet<PeerRole> peerRoles;    // peer roles

    public static Properties appendDefaultProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        Object[] keyValues = new Object[]{
            "grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000
        };

        for (int i = 0; i < keyValues.length; i += 2) {
            if (properties.get(keyValues[i]) == null) {
                properties.put(keyValues[i], keyValues[i + 1]);
            }
        }

        return properties;
    }

    public static EnumSet<PeerRole> createDefaultPeerRoles() {
        return EnumSet.of(PeerRole.ENDORSING_PEER, PeerRole.LEDGER_QUERY, PeerRole.CHAINCODE_QUERY, PeerRole.EVENT_SOURCE);
    }
}
