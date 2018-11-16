package com.sombrainc.ether.lottery.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sombrainc.ether.lottery.config.mappers.EtherAmountDeserializer;
import com.sombrainc.ether.lottery.config.mappers.EtherAmountSerializer;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

@JsonDeserialize(using = EtherAmountDeserializer.class)
@JsonSerialize(using = EtherAmountSerializer.class)
public class EtherAmount {

  private static final String SLASH = "/";
  private static final String INVALID_FORMAT_OF_ETHER = "Invalid format of ether";
  private final BigInteger amount;
  private final Unit unit;

  public EtherAmount(BigInteger amount, Unit unit) {
    this.amount = amount;
    this.unit = unit;
  }


  public static EtherAmount fromString(String value) {
    if (value == null) {
      throw new RuntimeException(INVALID_FORMAT_OF_ETHER);
    }
    String[] parts = value.split(SLASH);
    if (parts.length != 2) {
      throw new RuntimeException(INVALID_FORMAT_OF_ETHER);
    }
    return new EtherAmount(new BigInteger(parts[0]), Unit.fromString(parts[1]));
  }

  public String toFormattedString() {
    return this.amount + SLASH + this.unit;
  }


  public BigInteger getAmount() {
    return amount;
  }

  public BigDecimal getAmountDecimal() {
    return new BigDecimal(amount);
  }

  public Unit getUnit() {
    return unit;
  }


  public BigInteger toWei() {
    return Convert
        .toWei(new BigDecimal(amount),
            unit).toBigInteger();
  }

}

