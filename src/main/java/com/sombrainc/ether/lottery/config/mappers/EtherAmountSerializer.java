package com.sombrainc.ether.lottery.config.mappers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sombrainc.ether.lottery.model.EtherAmount;
import java.io.IOException;

public class EtherAmountSerializer extends JsonSerializer<EtherAmount> {

  @Override
  public void serialize(EtherAmount etherAmount, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    if (etherAmount == null) {
      return;
    }
    jsonGenerator.writeString(etherAmount.toFormattedString());
  }
}
