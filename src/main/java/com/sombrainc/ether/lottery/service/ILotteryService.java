package com.sombrainc.ether.lottery.service;

import com.sombrainc.ether.lottery.entity.Auction;
import com.sombrainc.ether.lottery.entity.Auction.AuctionState;
import com.sombrainc.ether.lottery.model.CompleteLottery;
import com.sombrainc.ether.lottery.model.ConfirmDelivery;
import com.sombrainc.ether.lottery.model.CreateLottery;
import com.sombrainc.ether.lottery.model.Participate;
import com.sombrainc.ether.lottery.model.Refund;
import java.util.Collection;
import java.util.List;

public interface ILotteryService {

  Auction create(CreateLottery createLottery, String email)
        throws Exception;

  Collection<Auction> find(List<AuctionState> states);

  Collection<Auction> findCreated(String email, List<AuctionState> auctionStates);

  Collection<Auction> findWinning(String email);

  Collection<Auction> findParticipating(String email, List<AuctionState> states);

  void participate(Participate participate, String email)
      throws Exception;

  Auction complete(CompleteLottery completeLottery, String email)
      throws Exception;

  Auction confirmDelivery(ConfirmDelivery confirmDelivery, String email)
      throws Exception;

  Auction refund(Refund refund, String email)
      throws Exception;
}
