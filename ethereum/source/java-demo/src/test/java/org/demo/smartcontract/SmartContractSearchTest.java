package org.demo.smartcontract;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import org.codehaus.jackson.node.BigIntegerNode;
import org.demo.util.GsonUtil;
import org.demo.util.SimpleLogger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.methods.response.VMTrace.VMOperation.Ex;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-11-23
 * @GitHub : https://github.com/zacscoding
 */
public class SmartContractSearchTest {

    static HttpService httpService;
    static Web3j web3j;

    @BeforeClass
    public static void startSetUp() throws Exception {
        String url = Files
            .readLines(new File("src/test/resources/secret.txt"), StandardCharsets.UTF_8).get(0);
        httpService = new HttpService(url);
        web3j = Web3j.build(httpService);
    }


    @Test
    public void checkSmartContractCreate() throws Exception {
        String[] hashes = new String[]{
            "0x4a2cb85b7a22e65334f3538723d93288a5e59a008e4b899791d85cf06c3bdae2",
            "0x32520b3522b9c99ede5731ac3d6354aa7980d1353d013d57c865d45bd3442186",
            "0x9b7b8f05cdc6f8a134802f3c435c328e19ade85e1793fcb93fa3ef44c08547e5"};

        // 1) check by transaction. to is null or not
        Consumer<String> checkFromTransaction = hash -> {
            try {
                Optional<Transaction> optional = web3j.ethGetTransactionByHash(hash).send()
                    .getTransaction();
                SimpleLogger.print("## Check hash : {} from transaction", hash);
                if (!optional.isPresent()) {
                    SimpleLogger.println(" >>> Not exist");
                } else {
                    boolean isContractCreate = optional.get().getTo() == null;
                    // boolean isContractCreate = optional.get().getCreates() != null;
                    SimpleLogger.println(">> is contract create : {}", isContractCreate);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // 2) check by transaction receipt. contract address field
        Consumer<String> checkFromTransactionReceipt = hash -> {
            try {
                Optional<TransactionReceipt> optional = web3j.ethGetTransactionReceipt(hash).send()
                    .getTransactionReceipt();
                SimpleLogger.print("## Check hash : {} from transaction receipt", hash);
                if (!optional.isPresent()) {
                    SimpleLogger.println(" >>> Not exist");
                } else {
                    TransactionReceipt receipt = optional.get();
                    boolean isContractCreate =
                        receipt.getTo() == null && receipt.getContractAddress() != null;
                    SimpleLogger.println(">> is contract create : {}", isContractCreate);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        for (String hash : hashes) {
            checkFromTransaction.accept(hash);
            checkFromTransactionReceipt.accept(hash);
        }
    }

    @Test
    public void checkEOAorCA() throws IOException {
        String[] addrs = new String[]{
            "0x087a1fb515e01a91c48efcd7341eb2a468353c14"
            , "0xa"
            , "0x087a1fb515e01a91c48efcd7341eb2a468353c15"
            , "0x68a4396489eca3df739fe43bf23e4976f2f164fb"
        };

        for (String addr : addrs) {
            String code = web3j.ethGetCode(addr, DefaultBlockParameterName.LATEST).send().getCode();
            if (code == null) {
                SimpleLogger.println("## Check addr : {} is invalid address", addr);
            } else {
                boolean isCA = code != null && !code.equals("0x");
                SimpleLogger
                    .println("## Check addr : {} >> {} (code : {})", addr, (isCA ? "CA" : "EOA "),
                        code);
            }
        }
        /*
        output
        ## Check addr : 0x087a1fb515e01a91c48efcd7341eb2a468353c14 >> EOA  (code : 0x)
        ## Check addr : 0xa is invalid address
        ## Check addr : 0x087a1fb515e01a91c48efcd7341eb2a468353c15 >> EOA  (code : 0x)
        ## Check addr : 0x68a4396489eca3df739fe43bf23e4976f2f164fb >> CA (code : 0x608060405260043610...
         */
    }


    @Test
    public void topicFilters() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(5);

        EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST, "0x282d74cC2203E5C19b522876cA6FdeE59d3b800b");
        //ethFilter.addSingleTopic("logFileAddedStatus");

        Subscription subscription = web3j.ethLogObservable(ethFilter).subscribe(log -> {
            countDownLatch.countDown();
            SimpleLogger.build()
                .appendln("block number : {}", log.getBlockNumber())
                .appendln("data : {}", log.getData())
                .appendln("type : {}", log.getType())
                .appendln("log index : {}", log.getLogIndex())
                .flush();
        });

        countDownLatch.await();
        subscription.isUnsubscribed();
    }

    @Test
    public void eventLogs() throws Exception {
        // https://etherscan.io/tx/0x4c48cec285fdd005947a6493b2f25e66db8d2627af5726bd8cd63c7c0e4bfc7f#eventlog
        TransactionReceipt receipt = web3j.ethGetTransactionReceipt(
            "0x4c48cec285fdd005947a6493b2f25e66db8d2627af5726bd8cd63c7c0e4bfc7f")
            .send().getTransactionReceipt().get();

        receipt.getLogs().forEach(log -> {
            SimpleLogger.build()
                .appendln("block number : {}", log.getBlockNumber())
                .appendln("data : {}", log.getData())
                .appendln("type : {}", log.getType())
                .appendln("log index : {}", log.getLogIndex())
                .appendln("topics : {}", log.getTopics())
                .flush();
        });
//        Output
//        block number : 6907280
//        data : 0x00000000000000000000000000000000000000000000003c3a38e5ab72fc0000
//        type : mined
//        log index : 96
//        topics : [0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef, 0x000000000000000000000000ff78ebf96d0c915f24b65b26eb4d9579fb0c2ef0, 0x00000000000000000000000078c0f1e1833f8ba1d6eadc51ca78109ff244ee7a]
    }

    //////////////////////////////////////////////////////////////

    @Test
    public void temp5() {
        List<String> a = Arrays.asList(
            "000000000000000000000000000000000000000000000000000000005c19d2d3"
            , "516d524774756757704b6e675a6b326a563539365442647872314b3738465a6e"
            , "566d6934664d7769764b41365355000000000000000000000000000000000000"
            , "00000000000000000000000000000000000000000000000000000000784a18ba"
        );

        for (String b : a) {
            System.out.printf("%s > %d\n", b, b.length());
        }
    }

    @Test
    public void temp4() throws Exception {
        TransactionReceipt receipt = web3j.ethGetTransactionReceipt(
            "0x11fc52b08a5f330cc008879ce619079b987df49c9048fc5b07d196c93ed30457")
            .send().getTransactionReceipt().get();

        receipt.getLogs().forEach(log -> {
            SimpleLogger.build()
                .appendln("block number : {}", log.getBlockNumber())
                .appendln("data : {}", log.getData())
                .appendln("type : {}", log.getType())
                .appendln("log index : {}", log.getLogIndex())
                .appendln("topics : {}", log.getTopics())
                .flush();
        });
//        Output
//        block number : 6907280
//        data : 0x00000000000000000000000000000000000000000000003c3a38e5ab72fc0000
//        type : mined
//        log index : 96
//        topics : [0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef, 0x000000000000000000000000ff78ebf96d0c915f24b65b26eb4d9579fb0c2ef0, 0x00000000000000000000000078c0f1e1833f8ba1d6eadc51ca78109ff244ee7a]
    }

    @Test
    public void temp3() {
        BigDecimal wei = new BigDecimal("100000000000000000000000000000000000000000");
        System.out.println(Convert.fromWei(wei, Unit.ETHER));
    }

    @Test
    public void temp() throws Exception {
        Block block = web3j
            .ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("312678", 10)), true)
            .send().getBlock();
        Transaction tx = (Transaction) block.getTransactions().get(0).get();
        TransactionReceipt tr = web3j.ethGetTransactionReceipt(tx.getHash()).send()
            .getTransactionReceipt().get();
        System.out.println(GsonUtil.toStringPretty(tx));
        System.out.println("------------------------------");
        System.out.println(GsonUtil.toStringPretty(tr));
        System.out.println("------------------------------");
        System.out.println(GsonUtil.toStringPretty(block));
        System.out.println("------------------------------");
    }

    @Test
    public void temp2() throws Exception {
        String hash = "0x2330373884427dc4095b16ace4dba0c2f8c5ab3425d0e2c409c0dff3317d3fff".trim();
        Transaction tx = web3j.ethGetTransactionByHash(hash).send().getTransaction().get();
        TransactionReceipt tr = web3j.ethGetTransactionReceipt(tx.getHash()).send()
            .getTransactionReceipt().get();
        System.out.println(GsonUtil.toStringPretty(tx));
        System.out.println("------------------------------");
        System.out.println(GsonUtil.toStringPretty(tr));
        System.out.println("------------------------------");
    }
}
