package com.sombrainc.ether.lottery.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sombrainc.ether.lottery.dao.IWalletDao;
import com.sombrainc.ether.lottery.entity.User;
import com.sombrainc.ether.lottery.model.AddressBalance;
import com.sombrainc.ether.lottery.model.EtherAmount;
import com.sombrainc.ether.lottery.model.TransferFunds;
import com.sombrainc.ether.lottery.model.TransferFundsToAddress;
import com.sombrainc.ether.lottery.util.LambdaExceptionUtil;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert.Unit;

@Service
public class WalletService extends GeneralService implements IWalletService {


  private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String EXPENSES = ", expenses ";


  private final IWalletDao walletDao;

  public WalletService(Web3j web3j, IWalletDao walletDao) {
    super(web3j);
    this.walletDao = walletDao;
  }


  @Override
  public void transfer(TransferFunds transferFunds, String email)
      throws Exception {

    com.sombrainc.ether.lottery.entity.Wallet fromWallet = findWallet(
        email).orElseThrow();
    com.sombrainc.ether.lottery.entity.Wallet toWallet = findWallet(transferFunds.getToUserAccount())
        .orElseThrow();

    Credentials fromCredential = resolveToCredential(transferFunds.getPassword(), fromWallet);

    TransactionReceipt transactionReceipt =
        wrap(LambdaExceptionUtil.rethrowSupplier(() -> Transfer
                .sendFunds(web3j, fromCredential,
                    toWallet.getAddress(),
                    transferFunds.getAmount().getAmount(),
                    transferFunds.getAmount().getUnit()).send()), fromWallet.getAddress(),
            toWallet.getAddress());
    LOGGER.info(transactionReceipt + EXPENSES + transactionReceipt.getGasUsed());
  }

  @Override
  public WalletFile generateWallet(String password)
      throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    ECKeyPair ecKeyPair = Keys.createEcKeyPair();
    return Wallet.createStandard(password, ecKeyPair);
  }

  @Override
  public WalletFile loadFromFile(File file) throws IOException {
    return OBJECT_MAPPER.readValue(file, WalletFile.class);
  }

  @Override
  public void transferToAddress(TransferFundsToAddress transferFundsToAddress, String email)
      throws Exception {

    com.sombrainc.ether.lottery.entity.Wallet fromWallet = findWallet(
        email).orElseThrow();

    Credentials fromCredential = resolveToCredential(transferFundsToAddress.getPassword(),
        fromWallet);

    TransactionReceipt transactionReceipt =
        wrap(LambdaExceptionUtil.rethrowSupplier(() ->
            Transfer
                .sendFunds(web3j, fromCredential,
                    transferFundsToAddress.getToAddress(),
                    transferFundsToAddress.getAmount().getAmount(),
                    transferFundsToAddress.getAmount().getUnit()).send()

        ), fromCredential.getAddress(), transferFundsToAddress.getToAddress());
    LOGGER.info(transactionReceipt + EXPENSES + transactionReceipt.getGasUsed());
  }

  @Override
  public byte[] transformToBytes(WalletFile walletFile) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsBytes(walletFile);
  }

  @Override
  public WalletFile fromBytes(byte[] wallet) throws IOException {
    return OBJECT_MAPPER.readValue(wallet, WalletFile.class);
  }

  @Override
  public Credentials resolveToCredential(String password,
      com.sombrainc.ether.lottery.entity.Wallet wallet)
      throws IOException, CipherException {
    WalletFile walletFile = fromBytes(wallet.getWallet());

    return Credentials
        .create(Wallet.decrypt(password, walletFile));
  }

  @Override
  public AddressBalance balance(String email, Unit unit) {
    com.sombrainc.ether.lottery.entity.Wallet wallet = findWallet(email).orElseThrow();

    return AddressBalance.of(wallet.getAddress(), EtherAmount.of(super.balanceOfAddress(wallet.getAddress(),
        unit) , unit));
  }

  @Override
  public BigDecimal balanceOfAddress(String address, Unit unit) {
    return super.balanceOfAddress(address, unit);
  }

  @Override
  public Optional<com.sombrainc.ether.lottery.entity.Wallet> findWallet(String email) {
    return walletDao
        .findUserWallet(email);
  }

  @Override
  public void save(com.sombrainc.ether.lottery.entity.Wallet wallet) {
    walletDao.insertWallet(wallet);
  }

  @Override
  public void createWallet(User user, WalletFile walletFile) throws JsonProcessingException {
    byte[] walletBytes = transformToBytes(walletFile);
    com.sombrainc.ether.lottery.entity.Wallet wallet = com.sombrainc.ether.lottery.entity.Wallet.builder()
        .setUserId(user.getId()).setAddress(walletFile.getAddress()).setWallet(walletBytes).build();
    save(wallet);
  }

  @Override
  public void validateOwnership(String password, WalletFile walletFile) throws CipherException {
    org.web3j.crypto.Wallet.decrypt(password, walletFile);
  }

  @Override
  public Collection<String> findOtherAddresses(String email) {
    return walletDao.findOtherAddresses(email);
  }
}
