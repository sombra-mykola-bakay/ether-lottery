package com.sombrainc.ether.lottery.service;


import com.sombrainc.ether.lottery.dao.IUserDao;
import com.sombrainc.ether.lottery.entity.User;
import com.sombrainc.ether.lottery.entity.User.Role;
import com.sombrainc.ether.lottery.model.SignUp;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletFile;

@Service
public class UserService implements IUserService,
    UserDetailsService {

  private static final String DEFAULT_ROLE_PREFIX = "ROLE_";
  @Value("${admin.email}")
  private String adminEmail;
  @Value("${admin.wallet.file}")
  private String systemWallet;

  private final IUserDao userDao;
  private final IWalletService walletService;

  @Autowired
  UserService(IUserDao userDao, IWalletService walletService) {
    this.userDao = userDao;
    this.walletService = walletService;
  }

  @Transactional
  @Override
  public void registerUser(SignUp signUp)
      throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
    if (adminEmail.equals(signUp.getEmail())) {
      throw new RuntimeException();
    }
    User user = userDao.insertUser(createUser(signUp, Role.USER));

    final WalletFile walletFile = walletService.generateWallet(signUp.getPassword());
    walletService.createWallet(user, walletFile);
  }


  private User createUser(SignUp signUp, Role role) {
    String hashedPassword = BCrypt.hashpw(signUp.getPassword(), BCrypt.gensalt());

    return User.builder().setEmail(signUp.getEmail()).setRole(role).setPassword(hashedPassword)
        .build();
  }


  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userDao
        .findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(""));

    return org.springframework.security.core.userdetails.User.builder().password(user.getPassword())
        .username(user.getEmail()).accountExpired(false).accountLocked(false)
        .authorities(DEFAULT_ROLE_PREFIX + user.getRole().name()).credentialsExpired(false)
        .disabled(false).build();
  }

  @Transactional
  @Override
  public void createAdmin(SignUp signUp) throws IOException, CipherException {

    if (!adminEmail.equals(signUp.getEmail())) {
      throw new RuntimeException();
    }
    if (userDao.findUserByEmail(adminEmail).isPresent()) {
      return;
    }

    final WalletFile walletFile = walletService.loadFromFile(new File(
        systemWallet));
    walletService.validateOwnership(signUp.getPassword(), walletFile);

    User admin = userDao.insertUser(createUser(signUp, Role.ADMIN));

    walletService.createWallet(admin, walletFile);
  }


}
