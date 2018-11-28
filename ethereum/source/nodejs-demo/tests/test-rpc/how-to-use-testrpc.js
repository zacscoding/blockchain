var TestRPC = require('ethereumjs-testrpc');
var Web3 = require('web3');

web3 = new Web3(TestRPC.provider());

web3.eth.getBlock(1, function (err, result) {
  console.log(err, result);
});
