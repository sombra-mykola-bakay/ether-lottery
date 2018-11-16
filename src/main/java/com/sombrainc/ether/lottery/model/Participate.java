package com.sombrainc.ether.lottery.model;


import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class Participate {
  @ApiModelProperty(example = "contractAddress", required = true)
  @NotNull(message = "\"contractAddress\" should be provided")
  private String contractAddress;
  @ApiModelProperty(example = "password", required = true)
  @NotNull(message = "\"password\" should be provided")
  private String password;
  @ApiModelProperty(example = "value", required = true)
  @NotNull(message = "\"value\" should be provided")
  private EtherAmount value;

  public String getContractAddress() {
    return contractAddress;
  }

  public void setContractAddress(String contractAddress) {
    this.contractAddress = contractAddress;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public EtherAmount getValue() {
    return value;
  }

  public void setValue(EtherAmount value) {
    this.value = value;
  }
}
