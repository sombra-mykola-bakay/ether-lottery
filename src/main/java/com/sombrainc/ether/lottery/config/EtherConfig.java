package com.sombrainc.ether.lottery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;


@Configuration
public class EtherConfig {

  private static final String COLON = ":";

  @Value("${geth.port}")
  private String port = "8545";
  @Value("${geth.host}")
  private String host = "http://localhost";

  @Bean
  public Web3j web3j() {
    return Web3j.build(new HttpService(host + COLON + port, false));
  }

}
