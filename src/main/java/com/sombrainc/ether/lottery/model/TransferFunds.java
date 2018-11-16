package com.sombrainc.ether.lottery.model;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class TransferFunds {
  @ApiModelProperty(example = "password", required = true)
  @NotNull(message = "\"password\" should be provided")
  private String password;
  @ApiModelProperty(example = "toUserAccount", required = true)
  @NotNull(message = "\"toUserAccount\" should be provided")
  private String toUserAccount;
  @ApiModelProperty(example = "amount", required = true)
  @NotNull(message = "\"amount\" should be provided")
  private EtherAmount amount;

  public String getToUserAccount() {
    return toUserAccount;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setToUserAccount(String toUserAccount) {
    this.toUserAccount = toUserAccount;
  }

  public EtherAmount getAmount() {
    return amount;
  }

  public void setAmount(EtherAmount amount) {
    this.amount = amount;
  }
}
