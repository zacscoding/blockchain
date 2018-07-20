package org.web3jtest.util;

import java.math.BigInteger;
import org.ethereum.core.BlockHeader;
import org.ethereum.util.ByteUtil;
import org.springframework.util.StringUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-06-18
 * @GitHub : https://github.com/zacscoding
 */
public class BlockParser {

    public static org.ethereum.core.BlockHeader toBlockHeader(org.web3j.protocol.core.methods.response.EthBlock.Block block) {
        byte[] mixHashOrStep = null;
        byte[] nonceOrSignature = null;
        if (StringUtils.hasText(block.getNonceRaw())) {
            mixHashOrStep = ByteUtil.hexStringToBytes(block.getMixHash());
            nonceOrSignature = ByteUtil.hexStringToBytes(block.getNonceRaw());
        } else {
            mixHashOrStep = ByteUtil.hexStringToBytes(block.getSealFields().get(0));
            nonceOrSignature = ByteUtil.hexStringToBytes(block.getSealFields().get(1));
        }

        return new BlockHeader(
            ByteUtil.hexStringToBytes(block.getParentHash()),       // parentHash
            ByteUtil.hexStringToBytes(block.getSha3Uncles()),       // unclesHash
            ByteUtil.hexStringToBytes(block.getMiner()),            // coinbase
            ByteUtil.hexStringToBytes(block.getLogsBloom()),        // logsBloom
            block.getDifficulty().toByteArray(),                    // difficulty
            block.getNumber().longValue(),                          // number
            ByteUtil.hexStringToBytes(block.getGasUsedRaw()),       // gasLimit
            block.getGasUsed().longValue(),                         // gasUsed
            block.getTimestamp().longValue(),                       // timestamp
            ByteUtil.hexStringToBytes(block.getExtraData()),        // extraData
            mixHashOrStep,
            nonceOrSignature
        );
    }
}
