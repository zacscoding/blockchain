pragma solidity ^0.4.25;

contract proof {
    struct FileDetails {
        uint timestamp;
        string owner;
    }

    mapping(string => FileDetails) files;

    event logFileAddedStatus(bool status, uint timestamp, string owner, string fileHash);

    // 블록 타임스탬프에 파일의 소유자를 저장하기 위해 사용
    function set(string owner, string fileHash) {
        // 키가 이미 존재하는지 확인하는 적절한 방법이X. 따라서 기본값을 확인(모든 비트가0)
        if (files[fileHash].timestamp == 0) {
            files[fileHash] = FileDetails(block.timestamp, owner);
            // 이벤트를 트리거해 프론트엔드 앱이 파일의 존재와 소유권에 대한 상세정보가 저장됐다고 알 수 있게 함
            logFileAddedStatus(true, block.timestamp, owner, fileHash);
        } else {
            // 프론트엔드앱에서 파일의 상세 정보가 이미 저장됐기 때문에 파일 존재 및 소유권에 대한 상세 정보를
            // 저장할 수 없다고 알려줌
            logFileAddedStatus(false, block.timestamp, owner, fileHash);
        }
    }

    // 파일 정보를 얻기 위해 사용
    function get(string fileHash) returns (uint timestamp, string owner) {
        return (files[fileHash].timestamp, files[fileHash].owner);
    }
}
