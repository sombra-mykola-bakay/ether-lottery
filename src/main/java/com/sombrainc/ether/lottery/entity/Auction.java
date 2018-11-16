package com.sombrainc.ether.lottery.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sombrainc.ether.lottery.model.EtherAmount;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class Auction {

  private final String version;
  private final Long id;
  private final User creator;
  private final String address;
  private final String description;
  private final AuctionState state;
  private final Set<User> participants;
  @JsonIgnore
  private final String hash;
  @JsonIgnore
  private final String encryptedWinnerIndex;
  private final User winner;
  private final int expectedParticipants;
  private final EtherAmount minPayableEtherAmount;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private final LocalDateTime validTill;
  @JsonIgnore
  private final String winnerMessage;

  private Auction(String version, Long id, User creator, String address, String description,
      AuctionState state, Set<User> participants, String hash,
      String encryptedWinnerIndex, User winner, int expectedParticipants,
      EtherAmount minPayableEtherAmount, LocalDateTime validTill, String winnerMessage) {
    this.version = version;
    this.id = id;
    this.creator = creator;
    this.address = address;
    this.description = description;
    this.state = state;
    this.participants = participants;
    this.hash = hash;
    this.encryptedWinnerIndex = encryptedWinnerIndex;
    this.winner = winner;
    this.expectedParticipants = expectedParticipants;
    this.minPayableEtherAmount = minPayableEtherAmount;
    this.validTill = validTill;
    this.winnerMessage = winnerMessage;
  }

  public String getVersion() {
    return version;
  }

  public Long getId() {
    return id;
  }

  public User getCreator() {
    return creator;
  }

  public String getAddress() {
    return address;
  }

  public String getDescription() {
    return description;
  }

  public Boolean isActive() {
    return state == AuctionState.ACTIVE;
  }

  public AuctionState getState() {
    return state;
  }

  public Set<User> getParticipants() {
    return new LinkedHashSet<>(participants);
  }

  public String getHash() {
    return hash;
  }

  public String getEncryptedWinnerIndex() {
    return encryptedWinnerIndex;
  }

  public User getWinner() {
    return winner;
  }

  public int getExpectedParticipants() {
    return expectedParticipants;
  }

  public EtherAmount getMinPayableEtherAmount() {
    return minPayableEtherAmount;
  }

  public LocalDateTime getValidTill() {
    return validTill;
  }

  public String getWinnerMessage() {
    return winnerMessage;
  }

  public static AuctionBuilder builder() {
    return new AuctionBuilder();
  }

  public static AuctionBuilder builder(Auction auction) {
    return new AuctionBuilder(auction);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Auction)) {
      return false;
    }
    Auction auction = (Auction) o;
    return Objects.equals(getAddress(), auction.getAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAddress());
  }


  public enum AuctionState {
    ACTIVE, COMPLETED, CLOSED, REFUNDED;

    public static AuctionState fromNumber(BigInteger index) {
      return Arrays.stream(AuctionState.values())
          .filter(state -> index.intValue() == state.ordinal()).findFirst().orElseThrow();
    }

    public static AuctionState fromString(String name) {
      return Arrays.stream(AuctionState.values())
          .filter(state -> name.equalsIgnoreCase(state.name())).findFirst().orElseThrow();
    }
  }

  public static class AuctionBuilder {

    private String version;
    private Long id;
    private User creator;
    private String address;
    private String description;
    private AuctionState state;
    private Set<User> participants;
    private String hash;
    private String encryptedWinnerIndex;
    private User winner;
    private int expectedParticipants;
    private EtherAmount minPayableEtherAmount;
    private LocalDateTime validTill;
    private String winnerMessage;

    private AuctionBuilder() {

    }

    private AuctionBuilder(Auction auction) {
      this.version = auction.version;
      this.id = auction.id;
      this.creator = auction.creator;
      this.address = auction.address;
      this.description = auction.description;
      this.state = auction.state;
      this.participants = auction.participants;
      this.hash = auction.hash;
      this.encryptedWinnerIndex = auction.encryptedWinnerIndex;
      this.winner = auction.winner;
      this.expectedParticipants = auction.expectedParticipants;
      this.minPayableEtherAmount = auction.minPayableEtherAmount;
      this.validTill = auction.validTill;
      this.winnerMessage = auction.winnerMessage;
    }

    public AuctionBuilder setVersion(String version) {
      this.version = version;
      return this;
    }

    public AuctionBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public AuctionBuilder setCreator(User creator) {
      this.creator = creator;
      return this;
    }

    public AuctionBuilder setAddress(String address) {
      this.address = address;
      return this;
    }

    public AuctionBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    public AuctionBuilder setState(AuctionState state) {
      this.state = state;
      return this;
    }

    public AuctionBuilder active() {
      state = AuctionState.ACTIVE;
      return this;
    }

    public AuctionBuilder setParticipants(Set<User> participants) {
      this.participants = new LinkedHashSet<>(participants);
      return this;
    }

    public AuctionBuilder addParticipant(User participant) {
      if (participants == null) {
        this.participants = new LinkedHashSet<>();
      }
      this.participants.add(participant);
      return this;
    }

    public AuctionBuilder setHash(String hash) {
      this.hash = hash;
      return this;
    }

    public AuctionBuilder setEncryptedWinnerIndex(String encryptedLotteryWinnerIndex) {
      this.encryptedWinnerIndex = encryptedLotteryWinnerIndex;
      return this;
    }

    public AuctionBuilder setWinner(User winner) {
      this.winner = winner;
      return this;
    }

    public AuctionBuilder setExpectedParticipants(int expectedParticipants) {
      this.expectedParticipants = expectedParticipants;
      return this;
    }

    public AuctionBuilder setMinPayableEtherAmount(EtherAmount minPayableEtherAmount) {
      this.minPayableEtherAmount = minPayableEtherAmount;
      return this;
    }

    public AuctionBuilder setValidTill(LocalDateTime validTill) {
      this.validTill = validTill;
      return this;
    }

    public AuctionBuilder setWinnerMessage(String winnerMessage) {
      this.winnerMessage = winnerMessage;
      return this;
    }

    public Auction build() {
      return new Auction(version, id, creator, address, description, state,
          Optional.ofNullable(participants).orElseGet(LinkedHashSet::new), hash,
          encryptedWinnerIndex, winner, expectedParticipants, minPayableEtherAmount, validTill,
          winnerMessage);
    }
  }
}
