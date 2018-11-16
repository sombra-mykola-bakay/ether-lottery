package com.sombrainc.ether.lottery.config;

import com.sombrainc.ether.lottery.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
public class WebSecurityConfig extends
    WebSecurityConfigurerAdapter {

  private final UserDetailsService userService;

  WebSecurityConfig(UserDetailsService userService) {
    super();
    this.userService = userService;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService);
  }


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
        .authorizeRequests()
        .antMatchers("/resources/**", "/signUp").permitAll()
        .antMatchers("/api/admin/**").hasRole(User.Role.ADMIN.name())
        .antMatchers("/api/**").authenticated()
        .and()
        .formLogin()
        .defaultSuccessUrl("/api/lotteries/current?state=active")
        .failureUrl("/login.html?error=true")
        .and()
        .logout().logoutUrl("/logout").logoutSuccessUrl("/");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}