package org.web3jtest.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.filters.BlockFilter;
import org.web3j.protocol.core.filters.PendingTransactionFilter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3jtest.AbstractTestRunner;
import org.web3jtest.util.LogLevelUtil;
import org.web3jtest.util.SimpleLogger;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-05-15
 * @GitHub : https://github.com/zacscoding
 */
public class FilterTest extends AbstractTestRunner {

    private static final Logger logger = LoggerFactory.getLogger(FilterTest.class);
    private ReentrantLock lock = new ReentrantLock();

    // block event filter
    @Test
    public void blockFilter() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        // LogLevelUtil.setInfo();
        System.out.println("## Start to block filter ##");
        BlockFilter filter = new BlockFilter(web3j, s -> {
            try {
                Block block = web3j.ethGetBlockByHash(s, false).send().getBlock();
                SimpleLogger.println("## Receive new block. num : {}, hash : {}", block.getNumber(), block.getHash());
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        filter.run(service, 5000L);
        // TimeUnit.MINUTES.sleep(2);
        countDownLatch.await();
        System.out.println("## End ##");
    }

    // pending tx event filter
    @Test
    public void pendingTxFilter() throws Exception {
        LogLevelUtil.setInfo();
        System.out.println("## Start to filter test ##");

        Set<String> pendingSets = new HashSet<>();
        PendingTransactionFilter filter = new PendingTransactionFilter(web3j, s -> {
            System.out.println("## Receive new pending tx : " + s);
            if (pendingSets.contains(s)) {
                System.out.println("Receive twice : " + s);
            } else {
                pendingSets.add(s);
            }
        });
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        filter.run(service, 500L);

        TimeUnit.MINUTES.sleep(2);
        System.out.println("## End ##");
    }

    // smart contract event filter
    @Test
    public void ethFilter() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(3);

        String contractAddr = "0x61e205daf693c5e5fd61e906c1a6e1d17e17af6c";
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, contractAddr);

        Map<String, Event> eventMap = new HashMap<>();
        Event changeUserEvent = new Event("ChangeUserEvent",
            Arrays.asList(
                new TypeReference<Utf8String>() {}
                , new TypeReference<Address>() {}
                , new TypeReference<Utf8String>() {}
                , new TypeReference<Address>() {}
                , new TypeReference<Utf8String>() {}
                , new TypeReference<Utf8String>() {}
            ));
        eventMap.put(EventEncoder.encode(changeUserEvent), changeUserEvent);

        Event changeOwnerEvent = new Event("ChangeOwnerEvent",
            Arrays.asList(
                new TypeReference<Utf8String>() {}
                , new TypeReference<Address>() {}
                , new TypeReference<Utf8String>() {}
                , new TypeReference<Address>() {}
            ));
        eventMap.put(EventEncoder.encode(changeOwnerEvent), changeOwnerEvent);

        Event testLogEvent = new Event("TestLog",
            Arrays.asList(
                new TypeReference<Address>() {}
            ));
        eventMap.put(EventEncoder.encode(testLogEvent), testLogEvent);

        Subscription subscription = web3j.ethLogObservable(filter).subscribe((log -> {
            StringBuilder topics = new StringBuilder();
            SimpleLogger.println("## New block : {}({}) -> Tx : {}"
                , log.getBlockNumber().toString(16), log.getBlockHash(), log.getTransactionHash());

            for (String topic : log.getTopics()) {
                Event event = eventMap.get(topic);
                List<Type> results = FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());
                String title = "\t===================================" + "(" + event.getName() + ")  " + topic + "===================================";
                String titleEnd = String.format("\t%" + title.length() + "s\n\n", "").replace(' ', '=');
                System.out.println(title);
                for (Type result : results) {
                    System.out.println("\t" + result.getTypeAsString() + "\t" + result.getValue());
                }
                System.out.println(titleEnd);
            }

            countDownLatch.countDown();
        }), error -> error.printStackTrace());

        countDownLatch.await();
        subscription.unsubscribe();
    }

    @Test
    public void pendingTxAndNewBlockFilter() throws Exception {
        LogLevelUtil.setInfo();
        long blockTime = 500L;
        Set<String> pendingSets = new HashSet<>();
        PendingTransactionFilter pendingTxFilter = new PendingTransactionFilter(web3j, s -> {
            long start = System.currentTimeMillis();
            try {
                lock.lock();
                pendingSets.add(s);
            } finally {
                lock.unlock();
                long end = System.currentTimeMillis();
                System.out.println("## Elapsed in tx : " + (end - start));
            }
        });

        BlockFilter blockFilter = new BlockFilter(web3j, s -> {
            long start = System.currentTimeMillis();
            lock.lock();
            System.out.println("## Receive new block filter");
            try {
                if (!pendingSets.isEmpty()) {
                    System.out.println("## Before filter : " + pendingSets.size());
                    List<String> willRemoved = new ArrayList<>();
                    pendingSets.forEach(txHash -> {
                        try {
                            web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt().ifPresent(tr -> {
                                SimpleLogger.println("## Hash : {} ==> Result : {}", tr.getTransactionHash(), tr.getStatus());
                                willRemoved.add(txHash);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    for (String remove : willRemoved) {
                        pendingSets.remove(remove);
                    }

                    System.out.println("## After filter : " + pendingSets.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                long end = System.currentTimeMillis();
                System.out.println("## Elapsed in block : " + (end - start));
            }
        });

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

        pendingTxFilter.run(service, blockTime);
        blockFilter.run(service, blockTime);

        TimeUnit.MINUTES.sleep(2);
        System.out.println("## End ##");
    }

    @Test
    public void comparePendingAndFilter() throws Exception {
        LogLevelUtil.setInfo();
        long blockTime = 500L;
        Set<String> filterSets = new HashSet<>();
        Set<String> observeSets = new HashSet<>();
        PendingTransactionFilter pendingTxFilter = new PendingTransactionFilter(web3j, s -> {
            filterSets.add(s);
            System.out.print("## [" + Thread.currentThread().getName() + "] ");
            System.out.println("Receive pending tx from filter : " + filterSets.size());
        });

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        pendingTxFilter.run(service, blockTime);

        Subscription subscription = web3j.pendingTransactionObservable().subscribe(tx -> {
            observeSets.add(tx.getHash());
            System.out.print("## [" + Thread.currentThread().getName() + "] ");
            System.out.println(" Receive pending tx from observe : " + observeSets.size());
        });

        TimeUnit.MINUTES.sleep(1);
        SimpleLogger.println("filter : {} , observe : {}", filterSets.size(), observeSets.size());
        System.out.println("## End ##");
        subscription.unsubscribe();
    }

    @Test
    public void pendingTxTest() throws Exception {
        PendingTransactionFilter pendingTxFilter = new PendingTransactionFilter(web3j, s -> {
            System.out.println("## Receive pending tx : " + s);
        });

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
        pendingTxFilter.run(service, 10L);

        TimeUnit.MINUTES.sleep(2L);
    }
}