package com.sombrainc.ether.lottery.service;

import com.sombrainc.ether.lottery.dao.IAuctionDao;
import com.sombrainc.ether.lottery.dao.IUserDao;
import com.sombrainc.ether.lottery.entity.Auction;
import com.sombrainc.ether.lottery.entity.Auction.AuctionState;
import com.sombrainc.ether.lottery.entity.User;
import com.sombrainc.ether.lottery.entity.Wallet;
import com.sombrainc.ether.lottery.model.CompleteLottery;
import com.sombrainc.ether.lottery.model.ConfirmDelivery;
import com.sombrainc.ether.lottery.model.CreateLottery;
import com.sombrainc.ether.lottery.model.Participate;
import com.sombrainc.ether.lottery.model.Refund;
import com.sombrainc.ether.lottery.solidity.GoodsLotteryAdapter;
import com.sombrainc.ether.lottery.util.LambdaExceptionUtil;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert.Unit;


@Service
public class LotteryService extends GeneralService implements ILotteryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);
  private static final String COMPLETED = "Completed ";
  private static final String SPACE = " ";
  private static final String INDEX_IS_A_WINNER_OF_LOTTERY = " - index is a winner of lottery  | ";
  private static final String ONLY_OWNER_CAN_MARK_AUCTION_AS_COMPLETED = "Only owner can mark auction as completed";
  private static final String ONLY_OWNER_CAN_REFUND = "Only owner can refund";
  private static final String ONLY_WINNER_CAN_CONFIRM_DELIVERY = "Only winner can confirm delivery";
  private static final String CONTRACT_IS_NOT_VALID = "Contract is not valid";
  private static final String COMPLETION_STATUS = "Completion status ";
  private static final String CAN_NOT_PARTICIPATE_IN_OWN_AUCTION = "Can not participate in own auction";
  private static final String ALREADY_IN_AUCTION = "Already in auction";
  private static final String AUCTION_IS_STILL_IN_PROGRESS = "Auction is still in progress";
  private static final String UNABLE_TO_REFUND = "Unable to refund";
  private static final String AUCTION_IS_ALREADY_CLOSED = "Auction is already closed";
  private static final String AUCTION_IS_ALREADY_COMPLETED = "Auction is already completed";
  private static final String EXPECTED_PARTICIPANTS_COUNT_DOES_NOT_MATCH_WITH_CURRENT = "Expected participants count does not match with current";
  private static final String AUCTION_EXPIRED = "Auction expired";
  private static final String AUCTION_IS_NOT_ACTIVE = "Auction is not active";
  private static final String AUCTION_SHOULD_BE_VALID_AT_LEAST_AN_HOUR = "Auction should be valid at least an hour";
  private static final String AUCTION_IS_CLOSED_WITH_PAYMENTS_REFUNDED = "Auction is closed with payments refunded";
  private static final String ALREADY_MAXIMUM_PARTICIPANTS = "Already maximum participants";
  private static final int ONE = 1;

  private final IWalletService walletService;
  private final IAuctionDao auctionDao;
  private final IUserDao userDao;
  @Value("${contracts.current.version}")
  private String currentVersion;

  @Autowired
  protected LotteryService(Web3j web3j,
      IWalletService walletService, IAuctionDao auctionDao,
      IUserDao userDao) {
    super(web3j);
    this.walletService = walletService;
    this.auctionDao = auctionDao;
    this.userDao = userDao;
  }

  @Override
  @Transactional
  public Auction create(CreateLottery createLottery, String email)
      throws Exception {

    if (createLottery.getValidTill().isBefore(LocalDateTime.now().plusHours(ONE))) {
      throw new RuntimeException(AUCTION_SHOULD_BE_VALID_AT_LEAST_AN_HOUR);
    }

    User user = userDao.findUserByEmailWithWallet(email).orElseThrow();
    Wallet wallet = user.getWallet();

    final Credentials credentials = walletService
        .resolveToCredential(createLottery.getPassword(), wallet);
    return create(credentials, user, createLottery);
  }

  @Override
  public Collection<Auction> find(final List<AuctionState> states) {
    if (states == null || states.isEmpty()) {
      return auctionDao.findAll();
    }
    return auctionDao.findInStates(states);
  }

  //TODO  join user
  @Override
  public Collection<Auction> findCreated(String email, List<AuctionState> auctionStates) {
    User user = userDao.findUserByEmail(email).orElseThrow();
    if (auctionStates == null || auctionStates.isEmpty()) {
      return auctionDao.findCreatedByUser(user.getId());
    }
    return auctionDao.findCreatedByUserInStates(user.getId(), auctionStates);
  }

  @Override
  public Collection<Auction> findWinning(String email) {
    User user = userDao.findUserByEmail(email).orElseThrow();
    return auctionDao.findWinning(user.getId());
  }

  @Override
  public Collection<Auction> findParticipating(String email, List<AuctionState> states) {
    User user = userDao.findUserByEmail(email).orElseThrow();
    if (states == null || states.isEmpty()) {
      return auctionDao.findParticipating(user.getId());
    }
    return auctionDao.findParticipatingInStates(user.getId(), states);
  }

  @Override
  @Transactional
  public void participate(Participate participate, String email)
      throws Exception {
    Auction auction = auctionDao.findByAddress(participate.getContractAddress()).orElseThrow();

    validateParticipateState(auction);
    validateAuction(email, auction);
    validateParticipantsLimit(auction);

    User user = userDao.findUserByEmailWithWallet(email).orElseThrow();
    validateUser(auction, user);

    Wallet wallet = user.getWallet();
    final Credentials credentials = walletService
        .resolveToCredential(participate.getPassword(), wallet);

    GoodsLotteryAdapter goodsLottery = GoodsLotteryAdapter
        .load(auction.getVersion(), participate.getContractAddress(), web3j, credentials,
            getGasPrice(),
            DefaultGasProvider.GAS_LIMIT);

    validateLottery(goodsLottery);

    AuctionState state = getState(wallet, goodsLottery);
    LOGGER
        .info(COMPLETED + state + SPACE + balanceOfAddress(participate.getContractAddress(),
            Unit.KETHER) + SPACE + Unit.KETHER.name());

    TransactionReceipt transactionReceipt = wrap(
        LambdaExceptionUtil
            .rethrowSupplier(() ->
                goodsLottery.participate(participate.getValue().toWei()).send()
            ),
        wallet.getAddress());
    if (!transactionReceipt.isStatusOK()) {
      throw new RuntimeException();
    }
    auctionDao.addAuctionParticipant(auction.getId(), user.getId());
    state = getState(wallet, goodsLottery);
    LOGGER
        .info(COMPLETED + state + SPACE + walletService
            .balanceOfAddress(participate.getContractAddress(), Unit.KETHER));
  }

  @Override
  @Transactional
  public Auction complete(CompleteLottery completeLottery, String email)
      throws Exception {

    Auction auction = auctionDao.findByAddress(completeLottery.getContractAddress()).orElseThrow();
    closedCheck(auction);

    if (auction.getExpectedParticipants() != auction.getParticipants().size()) {
      throw new RuntimeException(EXPECTED_PARTICIPANTS_COUNT_DOES_NOT_MATCH_WITH_CURRENT);
    }

    User user = userDao.findUserByEmailWithWallet(email).orElseThrow();

    if (!user.getId().equals(auction.getCreator().getId())) {
      throw new RuntimeException(ONLY_OWNER_CAN_MARK_AUCTION_AS_COMPLETED);
    }

    final Wallet wallet = user.getWallet();
    final Credentials credentials = walletService
        .resolveToCredential(completeLottery.getPassword(), wallet);
    GoodsLotteryAdapter goodsLottery = GoodsLotteryAdapter
        .load(auction.getVersion(), completeLottery.getContractAddress(), web3j, credentials,
            getGasPrice(),
            DefaultGasProvider.GAS_LIMIT);

    AuctionState state = getState(wallet, goodsLottery);

    validateLottery(goodsLottery);
    LOGGER.info(
        COMPLETED + state + SPACE + balanceOfAddress(completeLottery.getContractAddress(),
            Unit.KETHER) + SPACE + Unit.KETHER.name());
    if (auction.getState() == state) {
      TransactionReceipt transactionReceipt = goodsLottery.complete(new BigInteger(
          Encryptors.text(completeLottery.getPassword(), auction.getHash())
              .decrypt(auction.getEncryptedWinnerIndex())))
          .send();
      LOGGER.info(COMPLETION_STATUS + transactionReceipt.isStatusOK());
    }

    state = getState(wallet, goodsLottery);
    String address = goodsLottery.getWinner().send();
    User winner = userDao.findUserByWalletAddress(address).orElseThrow();
    auctionDao.setStateAndWinner(auction.getId(), state, winner.getId());
    LOGGER.info(
        COMPLETED + state + SPACE + balanceOfAddress(completeLottery.getContractAddress(),
            Unit.KETHER) + SPACE + Unit.KETHER.name());
    return Auction.builder(auction).setState(state)
        .setWinner(winner).build();
  }

  @Override
  @Transactional
  public Auction confirmDelivery(ConfirmDelivery confirmDelivery, String email)
      throws Exception {
    Auction auction = auctionDao.findByAddress(confirmDelivery.getContractAddress()).orElseThrow();

    validateDeliveryAcceptanceState(auction);
    User user = userDao.findUserByEmailWithWallet(email).orElseThrow();

    if (!user.getId().equals(auction.getWinner().getId())) {
      throw new RuntimeException(ONLY_WINNER_CAN_CONFIRM_DELIVERY);
    }

    final Wallet wallet = user.getWallet();
    final Credentials credentials = walletService
        .resolveToCredential(confirmDelivery.getPassword(), wallet);

    GoodsLotteryAdapter goodsLottery = GoodsLotteryAdapter
        .load(auction.getVersion(), confirmDelivery.getContractAddress(), web3j, credentials,
            getGasPrice(),
            DefaultGasProvider.GAS_LIMIT);

    AuctionState state = getState(wallet, goodsLottery);
    if (auction.isActive() || state == AuctionState.ACTIVE) {
      throw new RuntimeException();
    }

    validateLottery(goodsLottery);
    LOGGER.info(
        COMPLETED + state + SPACE + balanceOfAddress(confirmDelivery.getContractAddress(),
            Unit.KETHER) + SPACE + Unit.KETHER.name());

    if (auction.getState() == state) {
      TransactionReceipt transactionReceipt = goodsLottery.confirmDeliveryUser()
          .send();
      LOGGER.info(COMPLETION_STATUS + transactionReceipt.isStatusOK());
    }
    auctionDao.setState(auction.getId(), AuctionState.CLOSED);
    return auction;
  }

  @Override
  @Transactional
  public Auction refund(Refund refund, String email)
      throws Exception {
    Auction auction = auctionDao.findByAddress(refund.getContractAddress()).orElseThrow();
    closedCheck(auction);
    User user = userDao.findUserByEmailWithWallet(email).orElseThrow();

    if (!user.getId().equals(auction.getCreator().getId())) {
      throw new RuntimeException(ONLY_OWNER_CAN_REFUND);
    }

    final Wallet wallet = user.getWallet();
    final Credentials credentials = walletService
        .resolveToCredential(refund.getPassword(), wallet);

    GoodsLotteryAdapter goodsLottery = GoodsLotteryAdapter
        .load(auction.getVersion(), refund.getContractAddress(), web3j, credentials,
            getGasPrice(),
            DefaultGasProvider.GAS_LIMIT);

    AuctionState state = getState(wallet, goodsLottery);

    if (auction.getState() == state) {
      TransactionReceipt transactionReceipt = goodsLottery.refund().send();
      if (!transactionReceipt.isStatusOK()) {
        throw new RuntimeException(UNABLE_TO_REFUND);
      }
      LOGGER.info(COMPLETION_STATUS + transactionReceipt.isStatusOK());
    }

    auctionDao.setState(auction.getId(), AuctionState.REFUNDED);
    return Auction.builder(auction).setState(AuctionState.REFUNDED).build();
  }


  private Auction create(Credentials credential, User user, CreateLottery createLottery)
      throws Exception {
    int randomInt = lotteryRandom(createLottery.getParticipants());
    final String message = generateMessage();
    String lotteryString = assembleMessage(randomInt, message);
    String hash = DigestUtils.sha256Hex(lotteryString);

    LocalDateTime validTillTime = createLottery.getValidTill();
    final long validTill = validTillTime.toEpochSecond(ZoneOffset.UTC);

    GoodsLotteryAdapter goodsLottery = wrap(LambdaExceptionUtil.rethrowSupplier(() ->
        GoodsLotteryAdapter
            .deploy(currentVersion, web3j, credential, getGasPrice(), DefaultGasProvider.GAS_LIMIT,
                createLottery.getDescription(), createLottery.getMinPayableAmount().toWei(),
                createLottery.getMinLotteryTotal().toWei(),
                BigInteger.valueOf(validTill),
                BigInteger.valueOf(createLottery.getParticipants()), hash)
            .send()
    ), credential.getAddress());
    validateLottery(goodsLottery);

    Auction auction = Auction.builder().active().setCreator(user).setVersion(currentVersion)
        .setDescription(createLottery.getDescription())
        .setAddress(goodsLottery.getContractAddress()).setHash(hash).setEncryptedWinnerIndex(
            Encryptors.text(createLottery.getPassword(), hash).encrypt(String.valueOf(randomInt)))
        .setExpectedParticipants(createLottery.getParticipants())
        .setMinPayableEtherAmount(createLottery.getMinPayableAmount()).setValidTill(validTillTime)
        .setWinnerMessage(message)
        .build();

    return auctionDao.insertAuction(auction);
  }

  private void validateDeliveryAcceptanceState(Auction auction) {
    if (auction.getState() == AuctionState.ACTIVE) {
      throw new RuntimeException(AUCTION_IS_STILL_IN_PROGRESS);
    }
    if (auction.getState() == AuctionState.CLOSED) {
      throw new RuntimeException(AUCTION_IS_ALREADY_CLOSED);
    }
  }


  private void validateParticipateState(Auction auction) {
    if (auction.getState() == AuctionState.ACTIVE) {
      throw new RuntimeException(AUCTION_IS_NOT_ACTIVE);
    }

    if (auction.getValidTill().isAfter(LocalDateTime.now())) {
      throw new RuntimeException(AUCTION_EXPIRED);
    }
  }

  private void closedCheck(Auction auction) {
    if (auction.getState() == AuctionState.COMPLETED) {
      throw new RuntimeException(AUCTION_IS_ALREADY_COMPLETED);
    }
    if (auction.getState() == AuctionState.CLOSED) {
      throw new RuntimeException(AUCTION_IS_ALREADY_CLOSED);
    }
    if (auction.getState() == AuctionState.REFUNDED) {
      throw new RuntimeException(AUCTION_IS_CLOSED_WITH_PAYMENTS_REFUNDED);
    }
  }

  private void validateParticipantsLimit(Auction auction) {
    if (auction.getExpectedParticipants() <= auction.getParticipants().size()) {
      throw new RuntimeException(ALREADY_MAXIMUM_PARTICIPANTS);
    }
  }

  private void validateLottery(GoodsLotteryAdapter goodsLottery) {
    if (!goodsLottery.isValid()) {
      throw new RuntimeException(CONTRACT_IS_NOT_VALID);
    }
  }

  private void validateUser(Auction auction, User user) {
    if (auction.getCreator().getId().equals(user.getId())) {
      throw new RuntimeException(CAN_NOT_PARTICIPATE_IN_OWN_AUCTION);
    }
  }

  private void validateAuction(String email, Auction auction) {
    validateAuction(auction);
    if (auction.getParticipants().stream().map(User::getEmail)
        .anyMatch(userEmail -> userEmail.equals(email))) {
      throw new RuntimeException(ALREADY_IN_AUCTION);
    }
  }

  private void validateAuction(Auction auction) {
    if (!auction.isActive()) {
      throw new RuntimeException(AUCTION_IS_NOT_ACTIVE);
    }
  }

  private BigInteger getGasPrice() throws java.io.IOException {
    return web3j.ethGasPrice().send().getGasPrice();
  }

  private AuctionState getState(Wallet wallet, GoodsLotteryAdapter goodsLottery) throws Exception {
    return AuctionState.fromNumber(wrap(LambdaExceptionUtil.rethrowSupplier(() ->
        goodsLottery.getState().send()), wallet.getAddress()));
  }

  private int lotteryRandom(int bound) throws NoSuchAlgorithmException {
    return SecureRandom.getInstanceStrong().nextInt(bound);
  }


  private String assembleMessage(int randomInt, String message) {
    return String.valueOf(randomInt) + message;
  }

  private String generateMessage() {
    final byte[] bytes = new byte[10];
    new SecureRandom().nextBytes(bytes);
    Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    return INDEX_IS_A_WINNER_OF_LOTTERY + encoder.encodeToString(bytes);
  }


}
