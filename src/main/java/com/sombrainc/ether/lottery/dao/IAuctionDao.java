package com.sombrainc.ether.lottery.dao;

import com.sombrainc.ether.lottery.entity.Auction;
import com.sombrainc.ether.lottery.entity.Auction.AuctionState;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IAuctionDao {

  Auction insertAuction(Auction auction);

  void addAuctionParticipant(Long auctionId, Long userId);

  Collection<Auction> findCreatedByUserInStates(Long userId, List<AuctionState> states);

  Collection<Auction> findCreatedByUser(Long userId);

  Collection<Auction> findInStates(List<AuctionState> states);

  Optional<Auction> findByAddress(String auctionAddress);

  Collection<Auction> findParticipatingInStates(Long userId, List<AuctionState> states);

  Collection<Auction> findParticipating(Long userId);

  Collection<Auction> findWinning(Long userId);

  void setState(Long auctionId, AuctionState state);

  void setStateAndWinner(Long auctionId, AuctionState active, Long userId);

  Collection<Auction> findAll();
}
