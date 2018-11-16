package com.sombrainc.ether.lottery.config.mappers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sombrainc.ether.lottery.model.EtherAmount;
import java.io.IOException;

public class EtherAmountDeserializer extends JsonDeserializer<EtherAmount> {

  @Override
  public EtherAmount deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    final String value = node.textValue();
    return EtherAmount.fromString(value);

  }
}
