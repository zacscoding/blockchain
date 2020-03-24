package org.demo.smartcontract;

import static org.ethereum.solidity.compiler.SolidityCompiler.Options.ABI;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options.BIN;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options.INTERFACE;
import static org.ethereum.solidity.compiler.SolidityCompiler.Options.METADATA;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.demo.util.SimpleLogger;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.CallTransaction.Function;
import org.ethereum.core.CallTransaction.Param;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.AbiTypes;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.utils.Numeric;

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
                    , i++, input.name, input.type, input.indexed, input.getType());
            }
        }
    }

    @Test
    public void encodeConstructor() throws Exception {
        // bytes2 : "0xaa", bool : "true"
        String bin = "0x6080604052348015600f57600080fd5b5060405160408060ce83398101604052805160209091015160008054600160a060020a031916331790558015608a576000805460a060020a61ffff021916740100000000000000000000000000000000000000007e010000000000000000000000000000000000000000000000000000000000008504021790555b505060358060996000396000f3006080604052600080fd00a165627a7a72305820e141680af6db3da50a424971566a03dd00ac3e33c28c42c6911e98af6eaeca400029";
        String binWithEncoded = "0x6080604052348015600f57600080fd5b5060405160408060ce83398101604052805160209091015160008054600160a060020a031916331790558015608a576000805460a060020a61ffff021916740100000000000000000000000000000000000000007e010000000000000000000000000000000000000000000000000000000000008504021790555b505060358060996000396000f3006080604052600080fd00a165627a7a72305820e141680af6db3da50a424971566a03dd00ac3e33c28c42c6911e98af6eaeca400029aa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001";

        String binWithEncodedFromWeb3j = FunctionEncoder.encodeConstructor(
            Arrays.asList(
                // new Bytes2(new byte[]{(byte)0xaa, 0}),
                new Bytes2(Arrays.copyOf(new BigInteger("aa", 16).toByteArray(), 2)),
                new Bool(true)
            )
        );

        SimpleLogger.println("At remix \n{}\nAt web3j \n{}", binWithEncoded.substring(bin.length()), binWithEncodedFromWeb3j);
//        result
//        At remix
//        aa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001
    }

    @Test
    public void bytes() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<String> hexValues = Arrays.asList(
            "aa",
            "123"
        );

        for (String hexValue : hexValues) {
            byte[] bytes = Numeric.hexStringToByteArray(hexValue);
            Bytes wrapper = (Bytes) AbiTypes.getType("bytes" + bytes.length).getConstructor(byte[].class).newInstance(bytes);
            SimpleLogger
                .println("## Check : {} >> Bytes : {} >> wrapper : {}", hexValue, Arrays.toString(bytes), wrapper.getClass().getName());
        }

//        output
//        ## Check : aa >> Bytes : [-86] >> wrapper : org.web3j.abi.datatypes.generated.Bytes1
//        ## Check : 123 >> Bytes : [1, 35] >> wrapper : org.web3j.abi.datatypes.generated.Bytes2

    }

    @Test
    public void biToArray() throws Exception {
        BigInteger bi = new BigInteger("aa", 16);
        System.out.println(Arrays.toString(bi.toByteArray()));
    }
}
