var fs = require('fs');
var Web3 = require('web3');
var HookedWeb3Provider = require("hooked-web3-provider");
var ethUtil = require('ethereumjs-util');
const EthereumTx = require('ethereumjs-tx');

var url = fs.readFileSync('../../secret.txt');
var provider = new HookedWeb3Provider({
  host              : url,
  transaction_signer: {
    hasAddress     : function (address, callback) {
      callback(null, true);
    },
    signTransaction: function (tx_params, callback) {
      var rawTx = {
        gasPrice: web3.toHex(tx_params.gasPrice),
        gasLimit: web3.toHex(tx_params.gas),
        value   : web3.toHex(tx_params.value),
        from    : tx_params.from,
        to      : tx_params.to,
        nonce   : web3.toHex(tx_params.nonce)
      };

      var privateKey = ethUtil.toBuffer('0xa80a8959bb5655e1ca34afbbe791218e0b9366294c011d260378009550792c4e', 'hex');
      var tx = new EthereumTx(rawTx);
      tx.sign(privateKey);
      callback(null, '0x' + tx.serialize().toString('hex'));
    }
  }
});

var web3 = new Web3(provider);
web3.eth.sendTransaction({
  from    : "0x3cf8f98cd457d9328734172f4acf3ac63d45e15e",
  to      : "0x22f64153e70577f164244a32a2ec98e4e3cd329f",
  value   : web3.toWei("0.1", "ether"),
  gasPrice: "0",
  gas     : "21000"
}, function (err, result) {
  console.log('> send tx callback \n', err, result);
});
