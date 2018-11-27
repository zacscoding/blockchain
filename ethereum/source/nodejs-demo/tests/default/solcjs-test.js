var solc = require('solc');

console.log('>> Default test');

var input = 'contract x { function g() {} }';
// Setting 1 as second paramateractivates the optimiser
var output = solc.compile(input, 1);
for (var contractName in output.contracts) {
  // code and ABI that are needed by web3
  console.log(contractName + ': ' + output.contracts[contractName].bytecode);
  console.log(contractName + '; ' + JSON.parse(output.contracts[contractName].interface));
}

/*
x: 6060604052346000575b605d8060166000396000f300606060405263ffffffff60e060020a600035041663e2179b8e81146022575b6000565b34600057602c602e565b005b5b5600a165627a7a72305820686ccb19afb1089a94c403bbef1c50117601224abebee233f6feebb22877b2ce00
29
x; [object Object]
*/

console.log('>> Import test');
input2 = {
  "lib.sol" : "library L { function f() returns (uint) { return 7; } }",
  "cont.sol": "import 'lib.sol'; contract x { function g() { L.f(); } }"
};
var output2 = solc.compile({sources: input2}, 1);
for (var contractName in output2.contracts) {
  console.log(contractName + " : " + output2.contracts[contractName].bytecode);
}

/*
L : 6060604052346000575b606c8060166000396000f300606060405263ffffffff60e060020a60003504166326121ff081146022575b6000565b6028603a565b60408051918252519081900360200190f35b60075b905600a165627a7a72305820cf0508022c906b8e62fed41a5df81436a03
0cbe3bd6c772deddba8cd90ca48210029
x : 606060405234610000575b60b4806100186000396000f300606060405263ffffffff60e060020a600035041663e2179b8e81146022575b6000565b34600057602c602e565b005b73__L_____________________________________6326121ff06000604051602001526040518163fffff
fff1660e060020a02815260040180905060206040518083038186803b1560005760325a03f4156000575050505b5600a165627a7a72305820603e03d147e2eaebffca23d1d1a8f0503db5e3e2715778936511c62b82ff173c0029
 */

console.log(">> File import test");
var input3 = {
  "cont.sol": "import 'lib.sol'; contract x { function g() { L.f(); } }"
};

function findImports(path) {
  if (path === 'lib.sol') {
    return {contents: "library L { function f() returns (uint) { return 7; } }"};
  } else {
    return {error: "File not found"};
  }
}

var output3 = solc.compile(input3, 1, findImports);
console.log(output3);
for (var contractName in output3.contracts) {
  console.log(contractName + " : " + output3.contracts[contractName].bytecode);
}

//
var solcV047 = solc.useVersion("v0.4.7.commit.822622cf");
var output4 = solcV047.compile("contract t { function g() {} }", 1);
solc.loadRemoteVersion('soljson-v0.4.5.commit.b318366e', function (err, solcV047) {
  if (err) {
    console.log('error ', err);
    return;
  }
  var output = solcV047.compile("contract t { function g() {} }", 1);
});
