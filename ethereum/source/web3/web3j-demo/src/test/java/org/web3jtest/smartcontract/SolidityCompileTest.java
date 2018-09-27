package org.web3jtest.smartcontract;

import static org.ethereum.solidity.compiler.SolidityCompiler.Options.*;

import java.io.IOException;
import org.ethereum.core.CallTransaction;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.junit.Test;
import org.web3j.protocol.Web3j;

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
}