package com.sombrainc.ether.lottery.solidity;

import com.sombrainc.ether.lottery.service.WalletService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Async;
import rx.Observable;

public class GoodsLotteryAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);
  private static final String NOT_PROPER_CLASS_LOADED = "Not proper class loaded ";

  private static final String LOTTERY_CLASS_NAME = ".GoodsLottery";

  private static final String SOLIDITY_PACKAGE= "com.sombrainc.ether.lottery.solidity.";

  private final Method getStateMethod;
  private final Method confirmDeliveryUser;
  private final Method refund;
  private final Method getWinner;
  private final Method complete;
  private final Method participate;
  private final Method isValid;
  private final Method getContractAddress;
  private final Object lottery;

  private GoodsLotteryAdapter(Object lottery, Class<? extends Contract> contractClass) {
    this.lottery = lottery;

    getStateMethod = getMethod(contractClass, "getState");
    confirmDeliveryUser = getMethod(contractClass, "confirmDeliveryUser");
    refund = getMethod(contractClass, "refund");
    getWinner = getMethod(contractClass, "getWinner");
    complete = getMethod(contractClass, "complete", BigInteger.class);
    participate = getMethod(contractClass, "participate", BigInteger.class);
    isValid = Optional.ofNullable(getMethod(contractClass, "isValid"))
        .orElseThrow(() -> new RuntimeException("Contract should support isValid method"));
    getContractAddress = Optional.ofNullable(getMethod(contractClass, "getContractAddress"))
        .orElseThrow(
            () -> new RuntimeException("Contract should support getContractAddress method"));
  }

  @SuppressWarnings("unchecked")
  public org.web3j.protocol.core.RemoteCall<BigInteger> getState() {
    try {
      return (org.web3j.protocol.core.RemoteCall<BigInteger>) Optional.ofNullable(getStateMethod)
          .orElseThrow(() -> new RuntimeException("Method is not supported by contract version")).
              invoke(lottery);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  @SuppressWarnings("unchecked")
  public org.web3j.protocol.core.RemoteCall<TransactionReceipt> confirmDeliveryUser() {
    try {
      return (org.web3j.protocol.core.RemoteCall<TransactionReceipt>) Optional
          .ofNullable(confirmDeliveryUser)
          .orElseThrow(() -> new RuntimeException("Method is not supported by contract version"))
          .invoke(lottery);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  @SuppressWarnings("unchecked")
  public org.web3j.protocol.core.RemoteCall<TransactionReceipt> refund() {
    try {
      return (org.web3j.protocol.core.RemoteCall<TransactionReceipt>) Optional.ofNullable(refund)
          .orElseThrow(() -> new RuntimeException("Method is not supported by contract version"))
          .invoke(lottery);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  @SuppressWarnings("unchecked")
  public org.web3j.protocol.core.RemoteCall<String> getWinner() {
    try {
      return (org.web3j.protocol.core.RemoteCall<String>) Optional.ofNullable(getWinner)
          .orElseThrow(() -> new RuntimeException("Method is not supported by contract version"))
          .invoke(lottery);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  @SuppressWarnings("unchecked")
  public org.web3j.protocol.core.RemoteCall<TransactionReceipt> complete(BigInteger winnerIndex) {
    try {
      return (org.web3j.protocol.core.RemoteCall<TransactionReceipt>) Optional.ofNullable(complete)
          .orElseThrow(() -> new RuntimeException("Method is not supported by contract version"))
          .invoke(lottery, winnerIndex);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  @SuppressWarnings("unchecked")
  public org.web3j.protocol.core.RemoteCall<TransactionReceipt> participate(BigInteger weiValue) {
    try {
      return (org.web3j.protocol.core.RemoteCall<TransactionReceipt>) Optional
          .ofNullable(participate)
          .orElseThrow(() -> new RuntimeException("Method is not supported by contract version"))
          .invoke(lottery, weiValue);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  public String getContractAddress() {
    try {
      return (String) getContractAddress
          .invoke(lottery);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  public boolean isValid() {
    try {
      return (boolean) isValid
          .invoke(lottery);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Not able to invoke method", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter> deploy(String classVersion,
      Web3j web3j, Credentials credentials,
      BigInteger gasPrice, BigInteger gasLimit, String _lotDescription,
      BigInteger _minPayableAmount, BigInteger _minLotteryTotal, BigInteger _biddingEnd,
      BigInteger _participantsNumber, String _winningHash) {
    try {
      Class<? extends Contract> contractClass = getContractClass(classVersion);

      Method deployMethod = contractClass.getMethod("deploy", Web3j.class, Credentials.class,
          BigInteger.class, BigInteger.class, String.class,
          BigInteger.class, BigInteger.class, BigInteger.class,
          BigInteger.class, String.class);
      final org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter> deploy = (org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter>) deployMethod
          .invoke(null, web3j, credentials, gasPrice, gasLimit, _lotDescription, _minPayableAmount,
              _minLotteryTotal, _biddingEnd, _participantsNumber, _winningHash);

      return new RemoteCall(deploy, contractClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter> deploy(String classVersion,
      Web3j web3j,
      TransactionManager transactionManager,
      BigInteger gasPrice, BigInteger gasLimit, String _lotDescription,
      BigInteger _minPayableAmount, BigInteger _minLotteryTotal, BigInteger _biddingEnd,
      BigInteger _participantsNumber, String _winningHash) {
    try {
      Class<? extends Contract> contractClass = getContractClass(classVersion);

      Method deployMethod = contractClass
          .getMethod("deploy", Web3j.class, TransactionManager.class,
              BigInteger.class, BigInteger.class, String.class,
              BigInteger.class, BigInteger.class, BigInteger.class,
              BigInteger.class, String.class);
      final org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter> deploy = (org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter>) deployMethod
          .invoke(null, web3j, transactionManager, gasPrice, gasLimit, _lotDescription,
              _minPayableAmount,
              _minLotteryTotal, _biddingEnd, _participantsNumber, _winningHash);

      return new RemoteCall(deploy, contractClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static GoodsLotteryAdapter load(String classVersion, String contractAddress, Web3j web3j,
      Credentials credentials,
      BigInteger gasPrice, BigInteger gasLimit) {
    try {
      Class<? extends Contract> contractClass = getContractClass(classVersion);
      Method loadMethod = contractClass
          .getMethod("load", String.class, Web3j.class, Credentials.class,
              BigInteger.class, BigInteger.class);
      return new GoodsLotteryAdapter(loadMethod
          .invoke(null, contractAddress, web3j, credentials,
              gasPrice, gasLimit), contractClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public static GoodsLotteryAdapter load(String classVersion, String contractAddress, Web3j web3j,
      TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
    try {
      Class<? extends Contract> contractClass = getContractClass(classVersion);
      Method loadMethod = contractClass
          .getMethod("load", String.class, Web3j.class, TransactionManager.class,
              BigInteger.class, BigInteger.class);
      return new GoodsLotteryAdapter(loadMethod
          .invoke(null, contractAddress, web3j, transactionManager,
              gasPrice, gasLimit), contractClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Class<? extends Contract> getContractClass(String classVersion)
      throws ClassNotFoundException {
    final String className = SOLIDITY_PACKAGE+ classVersion + LOTTERY_CLASS_NAME;
    return getClass(className);
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends Contract> getClass(String className)
      throws ClassNotFoundException {
    Class contractClass = Class.forName(className);
    if (!Contract.class.isAssignableFrom(
        contractClass)) {
      throw new RuntimeException(NOT_PROPER_CLASS_LOADED + className);
    }
    return contractClass;
  }

  private static Method getMethod(Class<? extends Contract> contractClass,
      String method,
      Class... argumentTypes) {
    try {
      return contractClass.getMethod(method, argumentTypes);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e.getLocalizedMessage(), e);
      return null;
    }
  }

  private static class RemoteCall<T extends Contract> extends
      org.web3j.protocol.core.RemoteCall {

    private final org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter> originalRemoteCall;
    private final Class<T > contractClass;

    private RemoteCall(org.web3j.protocol.core.RemoteCall<GoodsLotteryAdapter> originalRemoteCall,
        Class<T> contractClass) {
      super(null);
      this.originalRemoteCall = originalRemoteCall;
      this.contractClass = contractClass;
    }

    public GoodsLotteryAdapter send() throws Exception {
      return new GoodsLotteryAdapter(originalRemoteCall.send(), contractClass);
    }

    /**
     * Perform request asynchronously with a future.
     *
     * @return a future containing our function
     */
    public CompletableFuture<GoodsLotteryAdapter> sendAsync() {
      return Async.run(this::send);
    }

    /**
     * Provide an observable to emit result from our function.
     *
     * @return an observable
     */
    public Observable<GoodsLotteryAdapter> observable() {
      return Observable.create(
          subscriber -> {
            try {
              subscriber.onNext(send());
              subscriber.onCompleted();
            } catch (Exception e) {
              subscriber.onError(e);
            }
          }
      );
    }
  }


}
