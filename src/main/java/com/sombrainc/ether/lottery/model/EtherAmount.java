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
  private final BigDecimal amount;
  private final Unit unit;

  public EtherAmount(BigDecimal amount, Unit unit) {
    this.amount = amount;
    this.unit = unit;
  }

  public static EtherAmount of(BigDecimal amount, Unit unit) {
    return new EtherAmount(amount, unit);
  }

  public static EtherAmount fromString(String value) {
    if (value == null) {
      throw new RuntimeException(INVALID_FORMAT_OF_ETHER);
    }
    String[] parts = value.split(SLASH);
    if (parts.length != 2) {
      throw new RuntimeException(INVALID_FORMAT_OF_ETHER);
    }
    return new EtherAmount(new BigDecimal(parts[0]), Unit.fromString(parts[1]));
  }

  public String toFormattedString() {
    return this.amount.toPlainString() + SLASH + this.unit;
  }


  public BigDecimal getAmount() {
    return amount;
  }

  public Unit getUnit() {
    return unit;
  }


  public BigInteger toWei() {
    return Convert
        .toWei(amount,
            unit).toBigIntegerExact();
  }

}

