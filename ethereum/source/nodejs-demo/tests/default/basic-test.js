var fs = require('fs');
var Web3 = require('web3');

var url = fs.readFileSync('../../secret.txt');
web3 = new Web3(new Web3.providers.HttpProvider(url));

// 연결 체크
// web3 connected : true
console.log('> web3 connected : ' + web3.isConnected());

// 동기식 요청
// Success to get block with sync :   0x1447ca3f2cd5692fc8192053e1b833856da2eac96410474188789e712474ee86
try {
  console.log('Success to get block with sync :  ', web3.eth.getBlock(48).hash);
} catch (e) {
  console.log(e);
}

// 비동기식요청

// Success to get block with async :  0x1447ca3f2cd5692fc8192053e1b833856da2eac96410474188789e712474ee86
web3.eth.getBlock(48, function (err, res) {
  if (!err) {
    console.log('Success to get block with async : ', res.hash);
  } else {
    console.log(err);
  }
});

// 단위 변환
// balance wei :  2.993155353253689176481146537402947624255349848014187e+52
// balance eth :  29931553532536891764811465374029476.24255349848014187
try {
  var addr = '0x3cf8f98cd457d9328734172f4acf3ac63d45e15e';
  var balanceWei = web3.eth.getBalance(addr).toString(); // BigNumber -> toString()
  console.log('balance wei : ', balanceWei);
  console.log('balance eth : ', web3.fromWei(balanceWei, 'ether'));

} catch (e) {
  console.log(e);
}


