package com.sombrainc.ether.lottery.controller;

import com.sombrainc.ether.lottery.model.SignUp;
import com.sombrainc.ether.lottery.service.IUserService;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {


  private final IUserService userService;

  @Autowired
  MainController(IUserService userService) {
    this.userService = userService;
  }

  @PostMapping("/signUp")
  @ApiOperation(value = "Sign up")
  public void signUp(@RequestBody @Valid SignUp signUp) throws Exception {
    userService.registerUser(signUp);
  }

  @PostMapping("/signUp/admin")
  @ApiOperation(value = "Admin sign up")
  public void signUpAdmin(@RequestBody @Valid SignUp signUp) throws Exception {
    userService.createAdmin(signUp);
  }

}
