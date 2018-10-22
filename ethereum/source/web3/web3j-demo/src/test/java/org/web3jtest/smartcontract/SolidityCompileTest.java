package org.web3jtest.smartcontract;

import static org.ethereum.solidity.compiler.SolidityCompiler.Options.ABI;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options.BIN;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options.INTERFACE;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options.METADATA;

import java.io.IOException;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.CallTransaction.Contract;
import org.ethereum.core.CallTransaction.Function;
import org.ethereum.core.CallTransaction.FunctionType;
import org.ethereum.core.CallTransaction.Param;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.junit.Test;
import org.web3jtest.util.SimpleLogger;

/**
 * https://github.com/ether-camp/solcJ
 */
public class SolidityCompileTest {

    @Test
    public void test() throws IOException {
        String contractSrc =
            "pragma solidity ^0.4.18;\n"
                + "contract Greeter {\n"
                + "    function sayHello() public pure returns (string) {\n"
                + "        return \"hello?\";\n"
                + "    }\n"
                + "}";

        SolidityCompiler.Result res = SolidityCompiler.compile(
            contractSrc.getBytes(), true, ABI, BIN, INTERFACE, METADATA);
        System.out.println("Out: '" + res.output + "'");
        System.out.println("Err: '" + res.errors + "'");
        CompilationResult result = CompilationResult.parse(res.output);

        CompilationResult.ContractMetadata a = result.getContract("Greeter");
        System.out.println(a.abi);
        CallTransaction.Contract contract = new CallTransaction.Contract(a.abi);
        System.out.printf(contract.functions[0].toString());

        System.out.println(a.bin);
        System.out.println(a.abi);
    }

    @Test
    public void constructor() throws IOException {
        String contractSrc =
            "pragma solidity ^0.4.24;\n"
                + "contract Greeter {\n"
                + "    string message = \"hello?\";\n"
                + "    address owner;\n"
                + "    bytes32 name;\n"
                + "    constructor(bytes32 _name) public {\n"
                + "        owner = msg.sender;\n"
                + "        name = _name;\n"
                + "    }\n"
                + "    function sayHello() public view returns (string) {\n"
                + "         return message;\n"
                + "    }\n"
                + "    function changeHello(string _message) public {\n"
                + "        message = _message;\n"
                + "    }\n"
                + "}";

        SolidityCompiler.Result res = SolidityCompiler.compile(
            contractSrc.getBytes(), true, ABI, BIN, INTERFACE, METADATA
        );

        CompilationResult result = CompilationResult.parse(res.output);
        CompilationResult.ContractMetadata contractMetadata = result.getContract("Greeter");

        CallTransaction.Contract contract = new CallTransaction.Contract(contractMetadata.abi);
        for (Function function : contract.functions) {
            SimpleLogger.build()
                        .appendln("## Check functions.. {}", function.toString())
                        .appendln("function.type : {} | function.inputs.length : {}", function.type, function.inputs.length)
                        .flush();

            int i = 0;
            for (Param input : function.inputs) {
                SimpleLogger.println("#{} ==> input.name : {} | input.type : {} | input.indexed : {} | input.getType() : {}"
                    , i++,input.name, input.type, input.indexed, input.getType());
            }
        }
    }
}