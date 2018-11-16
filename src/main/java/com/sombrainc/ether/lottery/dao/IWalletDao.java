package com.sombrainc.ether.lottery.dao;

import com.sombrainc.ether.lottery.entity.Wallet;
import java.util.Collection;
import java.util.Optional;

public interface IWalletDao {

  Wallet insertWallet(Wallet wallet);

  Optional<Wallet> findUserWallet(String email);

  Collection<String> findOtherAddresses(String email);
}
