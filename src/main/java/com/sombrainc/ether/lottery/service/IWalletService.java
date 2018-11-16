package com.sombrainc.ether.lottery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sombrainc.ether.lottery.entity.User;
import com.sombrainc.ether.lottery.entity.Wallet;
import com.sombrainc.ether.lottery.model.TransferFunds;
import com.sombrainc.ether.lottery.model.TransferFundsToAddress;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.Optional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Convert.Unit;

public interface IWalletService {

  void transfer(TransferFunds transferFunds, String email)
      throws Exception;

  WalletFile generateWallet(String password)
      throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;

  WalletFile loadFromFile(File file) throws IOException;

  void transferToAddress(TransferFundsToAddress transferFundsToAddress, String email)
      throws Exception;

  byte[] transformToBytes(WalletFile walletFile) throws JsonProcessingException;

  WalletFile fromBytes(byte[] wallet) throws IOException;

  Credentials resolveToCredential(String password,
      Wallet wallet)
      throws IOException, CipherException;

  BigDecimal balance(String email);

  BigDecimal balance(String email, Unit unit);

  BigDecimal balanceOfAddress(String address, Unit unit);

  Optional<Wallet> findWallet(String email);

  void save(Wallet wallet);

  void createWallet(User user, WalletFile walletFile) throws JsonProcessingException;

  void validateOwnership(String password, WalletFile walletFile) throws CipherException;

  Collection<String> findOtherAddresses(String email);
}
