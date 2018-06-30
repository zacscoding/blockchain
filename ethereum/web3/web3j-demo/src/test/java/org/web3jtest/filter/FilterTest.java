package org.web3jtest.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.junit.Test;
import org.web3j.protocol.core.filters.BlockFilter;
import org.web3j.protocol.core.filters.PendingTransactionFilter;
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

    @Test
    public void blockFilter() throws Exception {
        LogLevelUtil.setInfo();
        System.out.println("## Start to block filter ##");

        BlockFilter filter = new BlockFilter(web3j, s -> {
            try {
                Block block = web3j.ethGetBlockByHash(s, false).send().getBlock();
                SimpleLogger.println("## Receive new block. num : {}, hash : {}", block.getNumber(), block.getHash());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        filter.run(service, 500L);

        TimeUnit.MINUTES.sleep(2);
        System.out.println("## End ##");
    }

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

    private ReentrantLock lock = new ReentrantLock();

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

                    for(String remove : willRemoved) {
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
