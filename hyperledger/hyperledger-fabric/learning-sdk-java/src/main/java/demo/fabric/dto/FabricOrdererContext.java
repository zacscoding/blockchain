package demo.fabric.dto;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Fabric orderer 관련 context
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FabricOrdererContext {

    private String name;
    private String location;
    private Properties properties;

    public static Properties appendDefaultProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        Object[] keyValues = new Object[]{
            "grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES}
            , "grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS}
            , "grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[]{true}
        };

        for (int i = 0; i < keyValues.length; i += 2) {
            if (properties.get(keyValues[i]) == null) {
                properties.put(keyValues[i], keyValues[i + 1]);
            }
        }

        return properties;
    }
}
