package com.sombrainc.ether.lottery.controller;

import com.sombrainc.ether.lottery.entity.Auction;
import com.sombrainc.ether.lottery.entity.Auction.AuctionState;
import com.sombrainc.ether.lottery.model.CompleteLottery;
import com.sombrainc.ether.lottery.model.ConfirmDelivery;
import com.sombrainc.ether.lottery.model.CreateLottery;
import com.sombrainc.ether.lottery.model.Participate;
import com.sombrainc.ether.lottery.model.Refund;
import com.sombrainc.ether.lottery.service.ILotteryService;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/lotteries")
public class LotteryController {

  private final ILotteryService lotteryService;

  @Autowired
  LotteryController(ILotteryService lotteryService) {
    this.lotteryService = lotteryService;
  }

  @PostMapping("/participate")
  @ApiOperation(value = "Participate in lottery")
  public void participate(@RequestBody @Valid Participate participate, Principal principal)
      throws Exception {
    lotteryService.participate(participate, principal.getName());
  }

  @GetMapping("/current")
  @ApiOperation(value = "List lotteries with state")
  public Collection<Auction> findWithState(
      @RequestParam(value = "state", required = false) List<AuctionState> states) {
    return lotteryService.find(states);
  }

  @GetMapping("/created")
  @ApiOperation(value = "List own lotteries")
  public Collection<Auction> listCreatedLotterySmartContracts(Principal principal,
      @RequestParam(value = "state", required = false) List<AuctionState> states) {
    return lotteryService.findCreated(principal.getName(), states);
  }


  @GetMapping("/participating")
  @ApiOperation(value = "List participating in lotteries")
  public Collection<Auction> listParticipatingInLotterySmartContracts(Principal principal,
      @RequestParam(value = "state", required = false) List<AuctionState> states) {
    return lotteryService.findParticipating(principal.getName(), states);
  }

  @GetMapping("/winning")
  @ApiOperation(value = "List winning lotteries")
  public Collection<Auction> listWinningLotterySmartContracts(Principal principal) {
    return lotteryService.findWinning(principal.getName());
  }

  @PostMapping("/create")
  @ApiOperation(value = "Create lottery")
  public Auction createLotterySmartContract(@RequestBody @Valid CreateLottery createLottery,
      Principal principal) throws Exception {
    return lotteryService.create(createLottery, principal.getName());
  }

  @PostMapping("/complete")
  @ApiOperation(value = "Complete lottery")
  public Auction completeLotterySmartContract(@RequestBody @Valid CompleteLottery completeLottery,
      Principal principal)
      throws Exception {
    return lotteryService.complete(completeLottery, principal.getName());
  }

  @PostMapping("/delivery/confirm")
  @ApiOperation(value = "Confirm delivery of lottery item")
  public Auction confirmDelivery(@RequestBody @Valid ConfirmDelivery confirmDelivery,
      Principal principal)
      throws Exception {
    return lotteryService.confirmDelivery(confirmDelivery, principal.getName());
  }

  @PostMapping("/refund")
  @ApiOperation(value = "Refund")
  public Auction refund(@RequestBody @Valid Refund refund, Principal principal)
      throws Exception {
    return lotteryService.refund(refund, principal.getName());
  }
}
