package com.sombrainc.ether.lottery.dao;

import com.sombrainc.ether.lottery.entity.User;
import java.util.Optional;

public interface IUserDao {

  User insertUser(User user);

  Optional<User> findUserByEmail(String email);

  Optional<User> findUserByEmailWithWallet(String email);

  Optional<User> findUserByWalletAddress(String walletAddress);
}
