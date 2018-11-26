var keythereum = require("keythereum");
var datadir = "F:\\test-sample\\";


var address = "3cf8f98cd457d9328734172f4acf3ac63d45e15e";
const password = "test";

var keyObject = keythereum.importFromFile(address, datadir);
var privateKey = keythereum.recover(password, keyObject);
console.log(privateKey.toString('hex'));
