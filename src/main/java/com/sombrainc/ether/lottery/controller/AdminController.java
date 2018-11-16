package com.sombrainc.ether.lottery.controller;

import com.sombrainc.ether.lottery.service.IWalletService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert.Unit;

@RestController
@RequestMapping(path = "/api/admin")
public class AdminController {

  private final IWalletService walletService;

  @Autowired
  AdminController(IWalletService walletService) {
    this.walletService = walletService;
  }

  @GetMapping("/wallets/balance")
  @ApiOperation(value = "Wallet balance")
  public String balance(@RequestParam("address") String address, @RequestParam("unit") Unit unit) {
    return String.valueOf(walletService.balanceOfAddress(address, unit));
  }
}
