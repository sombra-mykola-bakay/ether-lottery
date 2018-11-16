package com.sombrainc.ether.lottery.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

public abstract class GeneralService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);
  private static final String WALLET_ADDRESS_PREFIX = "0x";
  private static final Unit ACCOUNT_BALANCE_SCALE = Unit.KETHER;
  private static final Unit ACCOUNT_DIFF_SCALE = Unit.SZABO;
  private static final String WRONG_VERSION_OF_BLOCK_CHAIN_VERSION_BYZANTIUM = "Wrong version of block chain, version >= byzantium";
  private static final String ACCOUNT = "\nAccount ";
  private static final String BEFORE = " before : ";
  private static final String SPACE = " ";
  private static final String ACCOUNT_AFTER = "Account after : ";
  private static final String DIFF = "\n Diff: ";
  private static final String NEW_LINE = "\n";
  private static final String MULTIPLE = "Multiple \n ";

  protected final Web3j web3j;

  GeneralService(Web3j web3j) {
    this.web3j = web3j;
  }

  private static String balanceDiffMessage(String address, BigDecimal before,
      BigDecimal after) {
    return
        ACCOUNT + address + BEFORE + Convert.fromWei(before, ACCOUNT_BALANCE_SCALE)
        + SPACE
        + ACCOUNT_BALANCE_SCALE.name() + NEW_LINE
        + ACCOUNT_AFTER + Convert.fromWei(after, ACCOUNT_BALANCE_SCALE) + SPACE
        + ACCOUNT_BALANCE_SCALE
        .name()
        + DIFF + Convert.fromWei(before
        .subtract(after), ACCOUNT_DIFF_SCALE).toPlainString() + SPACE + ACCOUNT_DIFF_SCALE.name()
        + NEW_LINE;
  }


  <R> R wrap(Supplier<R> s, String address) {
    final BigDecimal before = balanceOfAddress(address, Unit.WEI);
    R r = null;
    RuntimeException error = null;
    try {
      r = s.get();
    } catch (RuntimeException e) {
      error = e;
    }
    if (r instanceof TransactionReceipt) {
      if (((TransactionReceipt) r).getStatus() == null) {
        throw new RuntimeException(WRONG_VERSION_OF_BLOCK_CHAIN_VERSION_BYZANTIUM);
      }
    }
    final BigDecimal after = balanceOfAddress(address, Unit.WEI);
    LOGGER.debug(balanceDiffMessage(address, before, after));
    if (error != null) {
      throw error;
    }
    return r;
  }

  <R> R wrap(Supplier<R> s, String... addresses) {
    Map<String, BigDecimal> before = Arrays.stream(addresses).collect(Collectors
        .toMap(address -> address, address ->
            balanceOfAddress(address, Unit.WEI)));
    R r = null;
    RuntimeException error = null;
    try {
      r = s.get();
    } catch (RuntimeException e) {
      error = e;
    }
    LOGGER.debug(Arrays.stream(addresses).map(
        address -> {
          final BigDecimal balanceAfter = balanceOfAddress(address, Unit.WEI);
          return balanceDiffMessage(address, before.get(address), balanceAfter);
        })
        .reduce(MULTIPLE, (a, b) -> a + NEW_LINE + b));

    if (error != null) {
      throw error;
    }
    return r;
  }


  protected BigDecimal balanceOfAddress(String address, Unit unit) {
    try {
      String formatterAddress =
          address.startsWith(WALLET_ADDRESS_PREFIX) ? address : WALLET_ADDRESS_PREFIX + address;

      return Convert.fromWei(new BigDecimal(
          web3j.ethGetBalance(formatterAddress, DefaultBlockParameterName.LATEST)
              .send().getBalance()), unit);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
