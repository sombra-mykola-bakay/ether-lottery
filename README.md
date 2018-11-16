## Project setup

### Configuration
 
 Create `src/main/resources/application.properties` file from 
 `application-template.properties`
  
 Specify DB configuration
  

### Ehter miner setup
Install geth : 
https://github.com/ethereum/go-ethereum/wiki/Installing-Geth

#### Development (private network) setup
Setup local Private Ethereum Blockchain

Create new account  
    
    geth account new
    
Enter password

Record generated `Address`

Use Address in genesis json
 
Create CustomGenesis.json file:

    nano CustomGenesis.json
    
Insert following lines:
```
{
    "config": {
        "chainId": 15,
        "homesteadBlock": 0,
        "eip155Block": 0,
        "eip158Block": 0,
        "byzantiumBlock": 0
    },
    "difficulty": "0x400",
    "gasLimit": "0x2100000",
    "alloc": {
        "**Address**": 
         { "balance": "0x1555000000000000000000" },
  
 
    }
}
```

Generate datadir 

     geth --datadir pathToTestNetDataDir init CustomGenesis.json
     
Start node on local 

    geth --rpc --rpcapi="eth,web3,personal" --datadir pathToTestNetDataDir
Specify Etherbase 

    miner.setEtherbase("Address")
    
Start miner
    
    miner.start(4)
       
    
Find generated wallet in  ~/.ethereum/keystore/ and specify complete
 path it in config file property `admin.wallet.file`
 
 
Restore db structure from `db/db.sql`

Start project


### Smart contract version update

Create new version of solidity smart contract
 
Compile it with  :

`solc  path/to/solidity/source/GoodsLottery.sol --bin --abi --optimize  --overwrite -o target/`

Generate java wrapper under respective version package :

`web3j solidity generate  --javaTypes target/GoodsLottery.bin target/GoodsLottery.abi -o src/main/java/ -p com.sombrainc.web3jtest.solidity."version"`

Change `application.properties` property `contracts.current.version` to new version.

App will handle smart contract updates which does not change interface and basic flow.

So bug fixes are possible.
 
App will handle smart contract backward compatibility if interface does not change.


#### Swagger

API documentation is available at `http://host:8080/swagger-ui.html#/` 