var fs = require('fs');
var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io")(server);

server.listen(8081);

app.use(express.static("public"));

app.get("/", function (req, res) {
  res.sendFile(__dirname + "/public/html/index.html");
})

var Web3 = require("web3");

var url = fs.readFileSync('secret.txt');
web3 = new Web3(new Web3.providers.HttpProvider(url));

var proofContract = web3.eth.contract([{
  "constant"                                       : false, "inputs": [{"name": "fileHash", "type": "string"}], "name": "get", "outputs": [{"name": "timestamp", "type": "uint256"},
    {"name": "owner", "type": "string"}], "payable": false, "type": "function"
}, {
  "constant"                                       : false, "inputs": [{"name": "owner", "type": "string"},
    {"name": "fileHash", "type": "string"}], "name": "set", "outputs": [], "payable": false, "type": "function"
}, {
  "anonymous"                                                        : false, "inputs": [{"indexed": false, "name": "status", "type": "bool"},
    {"indexed": false, "name": "timestamp", "type": "uint256"}, {"indexed": false, "name": "owner", "type": "string"},
    {"indexed": false, "name": "fileHash", "type": "string"}], "name": "logFileAddedStatus", "type": "event"
}]);

var proof = proofContract.at('0x92e2b8c8c175d2a0771608ede2fac18cb516dd17');

app.get("/submit", function (req, res) {
  var fileHash = req.query.hash;
  var owner = req.query.owner;

  console.log('receive submit. hash : ' + fileHash + ' , owner : ', owner);

  proof.set.sendTransaction(owner, fileHash, {
    from: "0x22f64153e70577f164244a32a2ec98e4e3cd329f",
  }, function (error, transactionHash) {
    if (!error) {
      console.log('success to sendTx.');
      res.send(transactionHash);
    } else {
      console.log('Failed to send tx', error);
      res.send("Error");
    }
  })
});

app.get("/getInfo", function (req, res) {
  var fileHash = req.query.hash;
  var details = proof.get.call(fileHash);
  res.send(details);
});

proof.logFileAddedStatus().watch(function (err, result) {
  if (!err) {
    if (result.args.status == true) {
      io.send(result);
    }
  }
});
