package org.demo.consensus.aura;

import io.reactivex.disposables.Disposable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.bouncycastle.util.encoders.Hex;
import org.demo.AbstractTestRunner;
import org.demo.util.BlockParser;
import org.demo.util.SimpleLogger;
import org.ethereum.core.BlockHeader;
import org.ethereum.crypto.HashUtil;
import org.ethereum.util.RLP;
import org.junit.Test;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 */
public class AuraSignatureTest extends AbstractTestRunner {

    @Test
    public void displaySIignature() throws Exception {
        // BigInteger bestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger bestBlockNumber = BigInteger.valueOf(10L);
        BigInteger start = BigInteger.ONE;

        Set<String> miner1 = new HashSet<>();
        Set<String> miner2 = new HashSet<>();

        CountDownLatch completeLatch = new CountDownLatch(bestBlockNumber.subtract(start).intValue());

        Disposable subscription = web3j.replayPastBlocksFlowable(
            DefaultBlockParameter.valueOf(start), DefaultBlockParameter.valueOf(bestBlockNumber), false, false)
            .subscribe(onNext -> {
                    Block block = onNext.getBlock();
                    String miner = block.getMiner();
                    String signature = block.getSealFields().get(1);
                    if ("0x00bd138abd70e2f00903268f3db08f2d25677c9e".equals(miner)) {
                        miner1.add(signature);
                    } else {
                        miner2.add(signature);
                    }

                    BlockHeader header = BlockParser.toBlockHeader(block);
                    byte[] bareHash = HashUtil.sha3(header.getEncoded(false));
                 /*RLPList decoded = RLP.decode2(Hex.decode(signature.substring(2)));
                 RLPElement rlpElement = decoded.get(0);*/
                    //System.out.println(Hex.toHexString(rlpElement.getRLPData()));
                    SimpleLogger.println("{} | {} => {}", block.getNumber().toString(10), miner, signature);
                    System.out.println("bareHash : " + Hex.toHexString(RLP.encodeElement(bareHash)));
                    System.out.println();
                    completeLatch.countDown();
                }
            );

        completeLatch.await();
        subscription.dispose();
        SimpleLogger.println("Miner1 : {} / Miner2 : {} ==> Sum : {}", miner1.size(), miner2.size(), (miner1.size() + miner2.size()));
    }
}
