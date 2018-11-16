package com.sombrainc.ether.lottery.entity;


public class Wallet {

  private final Long id;
  private final Long userId;
  private final byte[] wallet;
  private final String address;

  private Wallet(Long id, Long userId, byte[] wallet, String address) {
    this.id = id;
    this.userId = userId;
    this.wallet = wallet;
    this.address = address;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public byte[] getWallet() {
    return wallet;
  }

  public String getAddress() {
    return address;
  }

  public static WalletBuilder builder() {
    return new WalletBuilder();
  }

  public static WalletBuilder builder(Wallet wallet) {
    return new WalletBuilder(wallet);
  }


  public static class WalletBuilder {

    private Long id;
    private Long userId;
    private byte[] wallet;
    private String address;

    private WalletBuilder() {

    }

    private WalletBuilder(Wallet wallet) {
      this.id = wallet.id;
      this.userId = wallet.userId;
      this.wallet = wallet.wallet;
      this.address = wallet.address;
    }


    public WalletBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public WalletBuilder setUserId(Long userId) {
      this.userId = userId;
      return this;
    }

    public WalletBuilder setWallet(byte[] wallet) {
      this.wallet = wallet;
      return this;
    }

    public WalletBuilder setAddress(String address) {
      this.address = address;
      return this;
    }

    public Wallet build() {
      return new Wallet(id, userId, wallet, address);
    }
  }
}
