const Tx = require('ethereumjs-tx');
var key = new Buffer('My private key..', 'hex');

var txParams = {
  gasPrice: "0x0",
  nonce   : "0x13",
  gas     : "0xe57e0",
  to      : "0x00a43494672eac4ea96e33c8fa049e019e5b0ed9",
  value   : "0x29A2241AF62C0000",
  chainId : 870117
};

var tx = new Tx(txParams);
tx.sign(key);

var rawTx = tx.serialize().toString('hex');
console.log(rawTx);
