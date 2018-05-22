package org.demo.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.demo.util.SimpleLogger;
import org.ethereum.core.Transaction;
import org.ethereum.trie.Trie;
import org.ethereum.trie.TrieImpl;
import org.ethereum.util.RLP;
import org.junit.Before;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

/**
 * https://ropsten.etherscan.io/block/3265130
 *
 * @author zacconding
 * @Date 2018-05-20
 * @GitHub : https://github.com/zacscoding
 */
public class TxMerkelTreeTest {
    String[] txHashes;
    String[] rawDatas;
    String txRootHash;
    List<Transaction> txns;

    @Before
    public void setUp() {
        txHashes = new String[] {
            "b75dd24835958922a916a02c7f032d18a55706b6b004670b770810b067070523 0xb75dd",
            "b30e268c8e42b168386e5791b6d9aff4aac41fa822f02e9d0291f9a37182c841 0xb30e2",
            "5c33d478cefbd24eef1308d9a0e13f91e977b4fb6ffed2ec14467650b487fc99 0x5c33d",
        };

        rawDatas = new String[] {
            "f8708307d4a385046c7cfe008304cb26944f2ad920a010e2a878f318d58e6208ad71c8ef5d880de0b6b3a7640000801ca05ff1a930e014a019c286ce75bbd20d0f470c86775012065cb92de4d0f4460394a07450f52b744bb46ff993060ff437db596e96c0bef217669523cb1689494b4d6b",
            "f86f837e5f6485046c7cfe008252089406bb06ff7bd42b70cc5c8fd612d52db0d037c4b1880de0b6b3a7640000801ba04ab3f5ddc2288c49b35e60fa8fa6560e5fac7820b59d49c728a50654a19db1b1a03de7003aa80cfbe923271a31a2d2c907743ef1b72bd00ec71b9676d7a13d9474",
            "f8ec826fc485046c7cfe0083015f9094c2aa5111a41c981793935b9271a11437e58443e780b8841e77b2e00000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000c167bb0d40000000000000000000000000000000000000000000000000000000000000000342544300000000000000000000000000000000000000000000000000000000002aa05152a7bba8f59d687f4599facb5783a86826f7af03e24da6a7493fb270340a47a00148199dc5cbef67f9dfca9d070daf969427e998cd25e43ee7fdf474845e0657"
        };

        txRootHash = "8a9121ef4416f3075c8867887e49b3e98e5ce6b4010f6b538c7e3bdbae828d38";
        txns = new ArrayList<>();
        for(int i=0; i<rawDatas.length; i++) {
            txns.add(new Transaction(Hex.decode(rawDatas[i])));
        }
    }

    @Test
    public void displayTxns() {
        for(Transaction tx : txns) {
            SimpleLogger.build()
                        .appendln("## Display tx")
                        .appendln("Hash : 0x" + Hex.toHexString(tx.getHash()))
                        .flush();
        }
    }

    @Test
    public void rootHashGetting() {
        Trie txsState = new TrieImpl();

        for (int i = 0; i < txns.size(); i++) {
            txsState.put(RLP.encodeInt(i), txns.get(i).getEncoded());
        }

        assertThat(txRootHash, is(Hex.toHexString(txsState.getRootHash())));
    }
}
