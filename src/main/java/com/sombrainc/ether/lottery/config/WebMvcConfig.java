package com.sombrainc.ether.lottery.config;


import com.sombrainc.ether.lottery.entity.Auction.AuctionState;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.web3j.utils.Convert.Unit;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

  @Override
  public FormattingConversionService mvcConversionService() {
    FormattingConversionService formattingConversionService = super.mvcConversionService();
    formattingConversionService.addConverter(new EtherUnitConverter());
    formattingConversionService.addConverter(new AuctionStateConverter());
    return formattingConversionService;
  }

  static class EtherUnitConverter implements Converter<String, Unit> {

    @Override
    public Unit convert(String source) {
      return Unit.fromString(source);
    }
  }

  static class AuctionStateConverter implements Converter<String, AuctionState> {

    @Override
    public AuctionState convert(String source) {
      return AuctionState.fromString(source);
    }
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

}
