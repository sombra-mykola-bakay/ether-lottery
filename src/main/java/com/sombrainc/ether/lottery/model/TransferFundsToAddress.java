package com.sombrainc.ether.lottery.model;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class TransferFundsToAddress {
  @ApiModelProperty(example = "password", required = true)
  @NotNull(message = "\"password\" should be provided")
  private String password;
  @ApiModelProperty(example = "toAddress", required = true)
  @NotNull(message = "\"toAddress\" should be provided")
  private String toAddress;
  @ApiModelProperty(example = "amount", required = true)
  @NotNull(message = "\"amount\" should be provided")
  private EtherAmount amount;

  public String getToAddress() {
    return toAddress;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setToAddress(String toAddress) {
    this.toAddress = toAddress;
  }

  public EtherAmount getAmount() {
    return amount;
  }

  public void setAmount(EtherAmount amount) {
    this.amount = amount;
  }
}
