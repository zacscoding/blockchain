pragma Solidity ^0.4.0;

import "github.com/pipermerriam/ethereum-string-utils/contracts/StringLib.sol";

contract Sample {
    using StringLib for *;

    event ping(string status);

    function Sample() {
        uint a = 23;
        bytes32 b = a.uintToBytes();
        bytes32 c = "12";
        uint d = c.bytesToUInt();
        ping("Conversion Done");
    }
}
