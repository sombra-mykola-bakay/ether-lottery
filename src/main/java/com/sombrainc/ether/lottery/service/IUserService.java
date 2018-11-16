package com.sombrainc.ether.lottery.service;

import com.sombrainc.ether.lottery.model.SignUp;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;

public interface IUserService {

  @Transactional
  void registerUser(SignUp signUp)
      throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException;

  @Transactional
  void createAdmin(SignUp signUp) throws IOException, CipherException;
}
