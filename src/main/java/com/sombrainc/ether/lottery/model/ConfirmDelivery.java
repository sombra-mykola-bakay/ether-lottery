package com.sombrainc.ether.lottery.model;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class ConfirmDelivery {
  @ApiModelProperty(example = "password", required = true)
  @NotNull(message = "\"password\" should be provided")
  private String password;
  @ApiModelProperty(example = "contractAddress", required = true)
  @NotNull(message = "\"contractAddress\" should be provided")
  private String contractAddress;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getContractAddress() {
    return contractAddress;
  }

  public void setContractAddress(String contractAddress) {
    this.contractAddress = contractAddress;
  }
}
