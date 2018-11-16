package com.sombrainc.ether.lottery.model;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;

public class SignUp {

  @ApiModelProperty(example = "email", required = true)
  @NotNull(message = "\"email\" should be provided")
  private String email;
  @ApiModelProperty(example = "password", required = true)
  @NotNull(message = "\"password\" should be provided")
  private String password;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
