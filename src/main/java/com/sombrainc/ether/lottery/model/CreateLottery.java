package com.sombrainc.ether.lottery.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class CreateLottery {

  @ApiModelProperty(example = "description", required = true)
  @NotNull(message = "\"description\" should be provided")
  private String description;
  @ApiModelProperty(example = "password", required = true)
  @NotNull(message = "\"password\" should be provided")
  private String password;
  @ApiModelProperty(example = "participants", required = true)
  @NotNull(message = "\"participants\" should be provided")
  private Integer participants;
  @ApiModelProperty(example = "minPayableAmount", required = true)
  @NotNull(message = "\"minPayableAmount\" should be provided")
  private EtherAmount minPayableAmount;
  @ApiModelProperty(example = "minLotteryTotal", required = true)
  @NotNull(message = "\"minLotteryTotal\" should be provided")
  private EtherAmount minLotteryTotal;
  @ApiModelProperty(example = "validTill", required = true)
  @NotNull(message = "\"validTill\" should be provided")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime validTill;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getParticipants() {
    return participants;
  }

  public void setParticipants(Integer participants) {
    this.participants = participants;
  }

  public EtherAmount getMinPayableAmount() {
    return minPayableAmount;
  }

  public void setMinPayableAmount(EtherAmount minPayableAmount) {
    this.minPayableAmount = minPayableAmount;
  }

  public EtherAmount getMinLotteryTotal() {
    return minLotteryTotal;
  }

  public void setMinLotteryTotal(EtherAmount minLotteryTotal) {
    this.minLotteryTotal = minLotteryTotal;
  }

  public LocalDateTime getValidTill() {
    return validTill;
  }

  public void setValidTill(LocalDateTime validTill) {
    this.validTill = validTill;
  }
}
