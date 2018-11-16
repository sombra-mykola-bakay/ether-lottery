package com.sombrainc.ether.lottery.config;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
public class DBConfig {
  @Value("${database.url}")
  private String dbURL;

  @Value("${database.username}")
  private String username;

  @Value("${database.password}")
  private String password;

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean("dataSource")
  public DataSource dataSource() {

    ComboPooledDataSource ds = new ComboPooledDataSource();
    ds.setUser(username);
    ds.setPassword(password);

    ds.setJdbcUrl(dbURL);
    return ds;
  }

}
