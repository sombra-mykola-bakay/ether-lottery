package com.sombrainc.ether.lottery.solidity.v1;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class GoodsLottery extends Contract {
    private static final String BINARY = "6080604052600a8054600160a060020a031916905534801561002057600080fd5b50604051610a31380380610a318339810160409081528151602080840151928401516060850151608086015160a087015160008054600160a060020a0319163317905560018790556002849055600383905594870180519097939592949193929092019161009491600691908901906100ca565b50600482905580516100ad9060059060208401906100ca565b5050600a805460a060020a60ff0219169055506101659350505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061010b57805160ff1916838001178555610138565b82800160010185558215610138579182015b8281111561013857825182559160200191906001019061011d565b50610144929150610148565b5090565b61016291905b80821115610144576000815560010161014e565b90565b6108bd806101746000396000f3006080604052600436106100775763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416631865c57d811461007c57806332bbea26146100b5578063590e1ae3146100cc5780638e7ea5b2146100e1578063971d852f14610112578063d11711a21461012a575b600080fd5b34801561008857600080fd5b50610091610132565b604051808260038111156100a157fe5b60ff16815260200191505060405180910390f35b3480156100c157600080fd5b506100ca610153565b005b3480156100d857600080fd5b506100ca610266565b3480156100ed57600080fd5b506100f6610408565b60408051600160a060020a039092168252519081900360200190f35b34801561011e57600080fd5b506100f6600435610417565b6100ca6105f9565b600a5474010000000000000000000000000000000000000000900460ff1690565b6002600a5474010000000000000000000000000000000000000000900460ff16600381111561017e57fe5b14156101d4576040805160e560020a62461bcd02815260206004820152601960248201527f4c6f747465727920697320616c726561647920636c6f73656400000000000000604482015290519081900360640190fd5b600a54600160a060020a0316331461025c576040805160e560020a62461bcd02815260206004820152602e60248201527f4f6e6c792064656c69766572792073657276696365206f72207265636569766560448201527f722063616e20636f6e6669726d20000000000000000000000000000000000000606482015290519081900360840190fd5b610264610831565b565b60408051808201909152601481527f43616e206f776e65722063616e20726566756e64000000000000000000000000602082015260008054909182918190600160a060020a0316331461033a5760405160e560020a62461bcd0281526004018080602001828103825283818151815260200191508051906020019080838360005b838110156102ff5781810151838201526020016102e7565b50505050905090810190601f16801561032c5780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b50600092505b6008548310156103ce57600880548490811061035857fe5b6000918252602080832090910154600160a060020a031680835260079091526040808320549051919450849281156108fc029290818181858888f193505050501580156103a9573d6000803e3d6000fd5b50600160a060020a038216600090815260076020526040812055600190920191610340565b5050600a805474ff000000000000000000000000000000000000000019167403000000000000000000000000000000000000000017905550565b600a54600160a060020a031690565b60408051606081018252602481527f43616e207472616e7366657220746f2063726561746f72206163636f756e742060208201527f6f6e6c790000000000000000000000000000000000000000000000000000000091810191909152600080549091908190600160a060020a031633146104d65760405160e560020a62461bcd028152600401808060200182810382528381815181526020019150805190602001908083836000838110156102ff5781810151838201526020016102e7565b506001600a5474010000000000000000000000000000000000000000900460ff16600381111561050257fe5b1415610558576040805160e560020a62461bcd02815260206004820152601060248201527f4973206e6f7420636f6d706c6574656400000000000000000000000000000000604482015290519081900360640190fd5b6009839055600880548490811061056b57fe5b600091825260209091200154600a805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911790556105ab610874565b156105e657600a805474ff00000000000000000000000000000000000000001916740100000000000000000000000000000000000000001790555b5050600a54600160a060020a0316919050565b600054600160a060020a0316331415610682576040805160e560020a62461bcd02815260206004820152602260248201527f43616e206e6f7420706172746963697061746520696e206f776e20617563746960448201527f6f6e000000000000000000000000000000000000000000000000000000000000606482015290519081900360840190fd5b33600090815260076020526040902054156106e7576040805160e560020a62461bcd02815260206004820152601260248201527f416c726561647920696e206c6f74746572790000000000000000000000000000604482015290519081900360640190fd5b6003544281116106f657600080fd5b6001543411610775576040805160e560020a62461bcd02815260206004820152602d60248201527f506c6561736520656e746572206d6f7265207468616e206d696e696d616c206c60448201527f6f747465727920616d6f756e7400000000000000000000000000000000000000606482015290519081900360840190fd5b600454600854106107d0576040805160e560020a62461bcd02815260206004820152601e60248201527f4e756d626572206f66207061727469636970616e747320726561636865640000604482015290519081900360640190fd5b503360008181526007602052604081203490556008805460018101825591527ff3f7a9fe364faab93b216da50a3214154f22a0a2b415b23a84c8169e8b636ee301805473ffffffffffffffffffffffffffffffffffffffff19169091179055565b600a805474ff0000000000000000000000000000000000000000191674020000000000000000000000000000000000000000179055600054600160a060020a0316ff5b60045460085460009114801561088c57506002543031115b9050905600a165627a7a7230582028c32d3aef077c93e719813323b03d53d16559611e44e0e5507091c5ddedb4020029";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_CONFIRMDELIVERYUSER = "confirmDeliveryUser";

    public static final String FUNC_REFUND = "refund";

    public static final String FUNC_GETWINNER = "getWinner";

    public static final String FUNC_COMPLETE = "complete";

    public static final String FUNC_PARTICIPATE = "participate";

    protected GoodsLottery(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected GoodsLottery(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<BigInteger> getState() {
        final Function function = new Function(FUNC_GETSTATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> confirmDeliveryUser() {
        final Function function = new Function(
                FUNC_CONFIRMDELIVERYUSER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> refund() {
        final Function function = new Function(
                FUNC_REFUND, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> getWinner() {
        final Function function = new Function(FUNC_GETWINNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> complete(BigInteger winnerIndex) {
        final Function function = new Function(
                FUNC_COMPLETE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(winnerIndex)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> participate(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_PARTICIPATE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public static RemoteCall<GoodsLottery> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _lotDescription, BigInteger _minPayableAmount, BigInteger _minLotteryTotal, BigInteger _biddingEnd, BigInteger _participantsNumber, String _winningHash) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_lotDescription), 
                new org.web3j.abi.datatypes.generated.Uint256(_minPayableAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_minLotteryTotal), 
                new org.web3j.abi.datatypes.generated.Uint256(_biddingEnd), 
                new org.web3j.abi.datatypes.generated.Uint256(_participantsNumber), 
                new org.web3j.abi.datatypes.Utf8String(_winningHash)));
        return deployRemoteCall(GoodsLottery.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<GoodsLottery> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _lotDescription, BigInteger _minPayableAmount, BigInteger _minLotteryTotal, BigInteger _biddingEnd, BigInteger _participantsNumber, String _winningHash) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_lotDescription), 
                new org.web3j.abi.datatypes.generated.Uint256(_minPayableAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_minLotteryTotal), 
                new org.web3j.abi.datatypes.generated.Uint256(_biddingEnd), 
                new org.web3j.abi.datatypes.generated.Uint256(_participantsNumber), 
                new org.web3j.abi.datatypes.Utf8String(_winningHash)));
        return deployRemoteCall(GoodsLottery.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static GoodsLottery load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new GoodsLottery(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static GoodsLottery load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new GoodsLottery(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
