package com.sombrainc.ether.lottery.model;

public class AddressBalance {

  private final String address;
  private final EtherAmount balance;

  private AddressBalance(String address, EtherAmount balance) {
    this.address = address;
    this.balance = balance;
  }

  public static AddressBalance of(String address, EtherAmount amount) {
    return new AddressBalance(address, amount);
  }

  public String getAddress() {
    return address;
  }

  public EtherAmount getBalance() {
    return balance;
  }

}
