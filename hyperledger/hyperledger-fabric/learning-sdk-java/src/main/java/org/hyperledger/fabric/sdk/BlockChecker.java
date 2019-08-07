package org.hyperledger.fabric.sdk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.hyperledger.fabric.protos.common.Common.Block;

/**
 * Checking for blocks
 */
public class BlockChecker {

    public static Map<String, Object> getData(Block block, int index) throws Exception {
        BlockDeserializer deserializer = new BlockDeserializer(block);
        EnvelopeDeserializer envelopeDeserializer = deserializer.getData(index);

        return ImmutableMap.of(
            "envelop", envelopeDeserializer.getEnvelope()
            , "payload", envelopeDeserializer.getPayload().getPayload()
        );
    }
}
