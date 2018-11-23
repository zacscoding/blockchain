package org.demo.rpc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Parity JSON RPC method name constants
 *
 * @author zacconding
 * @Date 2018-06-07
 * @GitHub : https://github.com/zacscoding
 */
public class ParityJsonRpc {

    // web3
    public static String web3_clientVersion;
    public static String web3_sha3;

    // net
    public static String net_listening;
    public static String net_peerCount;
    public static String net_version;

    // eth
    public static String eth_estimateGas;
    public static String eth_getBalance;
    public static String eth_getCode;
    public static String eth_getTransactionCount;
    public static String eth_getStorageAt;
    public static String eth_call;
    public static String eth_accounts;
    public static String eth_blockNumber;
    public static String eth_coinbase;
    public static String eth_gasPrice;
    public static String eth_getBlockByHash;
    public static String eth_getBlockByNumber;
    public static String eth_getBlockTransactionCountByHash;
    public static String eth_getBlockTransactionCountByNumber;
    public static String eth_getFilterChanges;
    public static String eth_getFilterLogs;
    public static String eth_getLogs;
    public static String eth_getTransactionByBlockHashAndIndex;
    public static String eth_getTransactionByBlockNumberAndIndex;
    public static String eth_getTransactionByHash;
    public static String eth_getTransactionReceipt;
    public static String eth_getUncleByBlockHashAndIndex;
    public static String eth_getUncleByBlockNumberAndIndex;
    public static String eth_getUncleCountByBlockHash;
    public static String eth_getUncleCountByBlockNumber;
    public static String eth_getWork;
    public static String eth_hashrate;
    public static String eth_mining;
    public static String eth_newBlockFilter;
    public static String eth_newFilter;
    public static String eth_newPendingTransactionFilter;
    public static String eth_protocolVersion;
    public static String eth_sendRawTransaction;
    public static String eth_sendTransaction;
    public static String eth_sign;
    public static String eth_signTransaction;
    public static String eth_submitHashrate;
    public static String eth_submitWork;
    public static String eth_syncing;
    public static String eth_uninstallFilter;

    // eth_pubsub
    public static String eth_subscribe;
    public static String eth_unsubscribe;

    // personal
    public static String personal_listAccounts;
    public static String personal_newAccount;
    public static String personal_sendTransaction;
    public static String personal_signTransaction;
    public static String personal_unlockAccount;
    public static String personal_sign;
    public static String personal_ecRecover;

    // parity
    public static String parity_cidV0;
    public static String parity_composeTransaction;
    public static String parity_consensusCapability;
    public static String parity_decryptMessage;
    public static String parity_encryptMessage;
    public static String parity_futureTransactions;
    public static String parity_allTransactions;
    public static String parity_getBlockHeaderByNumber;
    public static String parity_listOpenedVaults;
    public static String parity_listStorageKeys;
    public static String parity_listVaults;
    public static String parity_localTransactions;
    public static String parity_releasesInfo;
    public static String parity_signMessage;
    public static String parity_versionInfo;

    // parity_accounts
    public static String parity_changeVault;
    public static String parity_changeVaultPassword;
    public static String parity_closeVault;
    public static String parity_getVaultMeta;
    public static String parity_newVault;
    public static String parity_openVault;
    public static String parity_setVaultMeta;

    public static String parity_accountsInfo;
    public static String parity_checkRequest;
    public static String parity_defaultAccount;
    public static String parity_generateSecretPhrase;
    public static String parity_hardwareAccountsInfo;
    public static String parity_listAccounts;
    public static String parity_phraseToAddress;
    public static String parity_postSign;
    public static String parity_postTransaction;

    public static String parity_defaultExtraData;
    public static String parity_extraData;
    public static String parity_gasCeilTarget;
    public static String parity_gasFloorTarget;
    public static String parity_minGasPrice;
    public static String parity_transactionsLimit;

    public static String parity_devLogs;
    public static String parity_devLogsLevels;

    public static String parity_chain;
    public static String parity_chainId;
    public static String parity_chainStatus;
    public static String parity_gasPriceHistogram;
    public static String parity_netChain;
    public static String parity_netPeers;
    public static String parity_netPort;
    public static String parity_nextNonce;
    public static String parity_pendingTransactions;
    public static String parity_pendingTransactionsStats;
    public static String parity_registryAddress;
    public static String parity_removeTransaction;
    public static String parity_rpcSettings;
    public static String parity_unsignedTransactionsCount;

    public static String parity_dappsUrl;
    public static String parity_enode;
    public static String parity_mode;
    public static String parity_nodeKind;
    public static String parity_nodeName;
    public static String parity_wsUrl;

    public static String parity_allAccountsInfo;
    public static String parity_changePassword;
    public static String parity_deriveAddressHash;
    public static String parity_deriveAddressIndex;
    public static String parity_exportAccount;
    public static String parity_getDappAddresses;
    public static String parity_getDappDefaultAddress;
    public static String parity_getNewDappsAddresses;
    public static String parity_getNewDappsDefaultAddress;
    public static String parity_importGethAccounts;
    public static String parity_killAccount;
    public static String parity_listGethAccounts;
    public static String parity_listRecentDapps;
    public static String parity_newAccountFromPhrase;
    public static String parity_newAccountFromSecret;
    public static String parity_newAccountFromWallet;
    public static String parity_removeAddress;
    public static String parity_setAccountMeta;
    public static String parity_setAccountName;
    public static String parity_setDappAddresses;
    public static String parity_setDappDefaultAddress;
    public static String parity_setNewDappsAddresses;
    public static String parity_setNewDappsDefaultAddress;
    public static String parity_testPassword;

    // parity_set
    public static String parity_acceptNonReservedPeers;
    public static String parity_addReservedPeer;
    public static String parity_dappsList;
    public static String parity_dropNonReservedPeers;
    public static String parity_executeUpgrade;
    public static String parity_hashContent;
    public static String parity_removeReservedPeer;
    public static String parity_setAuthor;
    public static String parity_setChain;
    public static String parity_setEngineSigner;
    public static String parity_setExtraData;
    public static String parity_setGasCeilTarget;
    public static String parity_setGasFloorTarget;
    public static String parity_setMaxTransactionGas;
    public static String parity_setMinGasPrice;
    public static String parity_setMode;
    public static String parity_setTransactionsLimit;
    public static String parity_upgradeReady;

    // pubsub
    public static String parity_subscribe;
    public static String parity_unsubscribe;

    // signer
    public static String signer_confirmRequest;
    public static String signer_confirmRequestRaw;
    public static String signer_confirmRequestWithToken;
    public static String signer_generateAuthorizationToken;
    public static String signer_generateWebProxyAccessToken;
    public static String signer_rejectRequest;
    public static String signer_requestsToConfirm;
    public static String signer_subscribePending;
    public static String signer_unsubscribePending;

    // trace
    public static String trace_call;
    public static String trace_rawTransaction;
    public static String trace_replayTransaction;
    public static String trace_replayBlockTransactions;

    public static String trace_block;
    public static String trace_filter;
    public static String trace_get;
    public static String trace_transaction;

    // shh
    public static String shh_info;
    public static String shh_newKeyPair;
    public static String shh_addPrivateKey;
    public static String shh_newSymKey;
    public static String shh_addSymKey;
    public static String shh_getPublicKey;
    public static String shh_getPrivateKey;
    public static String shh_getSymKey;
    public static String shh_deleteKey;
    public static String shh_post;
    public static String shh_newMessageFilter;
    public static String shh_getFilterMessages;
    public static String shh_deleteMessageFilter;
    public static String shh_subscribe;
    public static String shh_unsubscribe;


    static {
        initialize();
    }

    private static void initialize() {
        for (Field field : ParityJsonRpc.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) {
                try {
                    field.set(null, field.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
