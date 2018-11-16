package com.sombrainc.ether.lottery.controller;

import com.sombrainc.ether.lottery.model.TransferFunds;
import com.sombrainc.ether.lottery.model.TransferFundsToAddress;
import com.sombrainc.ether.lottery.service.WalletService;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert.Unit;


@RestController
@RequestMapping(path = "/api/wallets")
public class WalletController {

  private final WalletService walletService;

  @Autowired
  WalletController(WalletService walletService) {
    this.walletService = walletService;
  }

  @PostMapping("/transfer")
  @ApiOperation(value = "Transfer funds to user")
  public void transfer(@RequestBody @Valid TransferFunds transferFunds, Principal principal)
      throws Exception {
    walletService.transfer(transferFunds, principal.getName());
  }

  @GetMapping("/transferToAddress")
  @ApiOperation(value = "Transfer funds to address")
  public void transferToAddress(@RequestBody @Valid TransferFundsToAddress transferFundsToAddress,
      Principal principal) throws Exception {
    walletService.transferToAddress(transferFundsToAddress, principal.getName());
  }

  @GetMapping("/balance")
  @ApiOperation(value = "Wallet balance")
  public String balance(Principal principal, @RequestParam Unit unit) {
    return String.valueOf(walletService.balance(principal.getName(), unit));
  }


}
