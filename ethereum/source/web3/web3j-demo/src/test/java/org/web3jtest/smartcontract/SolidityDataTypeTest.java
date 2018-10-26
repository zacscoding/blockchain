package org.web3jtest.smartcontract;

import java.math.BigInteger;
import java.util.Arrays;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes2;
import org.web3j.abi.datatypes.generated.Int8;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.utils.Numeric;
import org.web3jtest.util.SimpleLogger;

/**
 * @author zacconding
 * @Date 2018-10-26
 * @GitHub : https://github.com/zacscoding
 */
public class SolidityDataTypeTest {

    @Test
    @Ignore
    public void bytesTypeTest() {
        // byte1 check
        for (int i = 0; i < 257; i++) {
            String hexValue = Integer.toHexString(i);
            try {
                byte[] rawInputBytes = Numeric.hexStringToByteArray(hexValue);
                new Bytes1(rawInputBytes);
            } catch(Exception e) {
                SimpleLogger.println("Failed {}({}) --> {}", i, hexValue, e.getMessage());
            }
        }
//        result
//        Failed 256(100) --> Input byte array must be in range 0 < M <= 32 and length must match type
    }

    @Test
    @Ignore
    public void int8Test() {
        for (int i = 0; i >= Integer.MIN_VALUE; i--) {
            BigInteger bi = BigInteger.valueOf(i);
            if (!possibleToInt8(bi)) {
                SimpleLogger.println("Fail... i : {} | bit count : {} | bit length : {}", i, bi.bitCount(), bi.bitLength());
                break;
            }
        }

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            BigInteger bi = BigInteger.valueOf(i);
            if (!possibleToInt8(bi)) {
                SimpleLogger.println("Fail... i : {} | bit count : {} | bit length : {}", i, bi.bitCount(), bi.bitLength());
                break;
            }
        }

//         result
//         Fail... i : -257 | bit count : 1 | bit length : 9
//         Fail... i : 256 | bit count : 1 | bit length : 9
    }

    @Test
    @Ignore
    public void uint8Test() {
        for (int i = 0; i >= Integer.MIN_VALUE; i--) {
            BigInteger bi = BigInteger.valueOf(i);
            if (!possibleToUint8(bi)) {
                SimpleLogger.println("Fail... i : {}({}) | bit count : {} | bit length : {}", i, "0x" + bi.toString(16), bi.bitCount(), bi.bitLength());
                break;
            }
        }

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            BigInteger bi = BigInteger.valueOf(i);
            if (!possibleToUint8(bi)) {
                SimpleLogger.println("Fail... i : {}({}) | bit count : {} | bit length : {}", i, "0x" + bi.toString(16), bi.bitCount(), bi.bitLength());
                break;
            }
        }

//        result
//        Fail... i : -1(0x-1) | bit count : 0 | bit length : 0
//        Fail... i : 256(0x100) | bit count : 1 | bit length : 9
    }

    @Test
    @Ignore
    public void bitLengthTest() {
        for (int i = 253; i < 258; i++) {
            BigInteger bi = BigInteger.valueOf(i);
            String hexValue = bi.toString(16);
            SimpleLogger.println("Int : {} | Hex : {}, bit length : {} | bytes : {}"
                , i, hexValue, bi.bitLength(), Arrays.toString(Numeric.hexStringToByteArray(hexValue)));
        }

//        result
//        Int : 253 | Hex : 0xfd, bit length : 8 | bytes : [-3]
//        Int : 254 | Hex : 0xfe, bit length : 8 | bytes : [-2]
//        Int : 255 | Hex : 0xff, bit length : 8 | bytes : [-1]
//        Int : 256 | Hex : 0x100, bit length : 9 | bytes : [1, 0]
//        Int : 257 | Hex : 0x101, bit length : 9 | bytes : [1, 1]
    }

    @Test
    @Ignore
    public void byte1() {
        System.out.println("각".getBytes().length);
        new Bytes1("각".getBytes());
        // new Bytes1("aa".getBytes());

        new Bytes2("aa".getBytes());
    }

    private boolean possibleToInt8(BigInteger value) {
        try {
            new Int8(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean possibleToUint8(BigInteger value) {
        try {
            new Uint8(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void temp() throws Exception {
        new Uint8(new BigInteger("100", 16));
    }
}