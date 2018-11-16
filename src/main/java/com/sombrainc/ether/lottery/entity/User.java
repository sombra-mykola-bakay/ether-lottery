package com.sombrainc.ether.lottery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

public class User {

  private final Long id;
  private final String email;
  @JsonIgnore
  private final String password;
  @JsonIgnore
  private final Role role;
  @JsonIgnore
  private final Wallet wallet;

  private User(Long id, String email, String password, Role role, Wallet wallet) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.role = role;
    this.wallet = wallet;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  public Wallet getWallet() {
    return wallet;
  }

  public enum Role {
    USER, ADMIN
  }


  public static UserBuilder builder() {
    return new UserBuilder();
  }

  public static UserBuilder builder(User user) {
    return new UserBuilder(user);
  }

  public static class UserBuilder {

    private Long id;
    private String email;
    private String password;
    private Role role;
    private Wallet wallet;

    private UserBuilder() {

    }

    private UserBuilder(User user) {
      this.id = user.id;
      this.email = user.email;
      this.password = user.password;
      this.role = user.role;
      this.wallet = user.wallet;
    }

    public UserBuilder setId(Long id) {
      this.id = id;
      return this;
    }

    public UserBuilder setEmail(String email) {
      this.email = email;
      return this;
    }

    public UserBuilder setPassword(String password) {
      this.password = password;
      return this;
    }

    public UserBuilder setRole(Role role) {
      this.role = role;
      return this;
    }

    public UserBuilder setWallet(Wallet wallet) {
      this.wallet = wallet;
      return this;
    }

    public User build() {
      return new User(id, email, password, role, wallet);
    }

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(getEmail(), user.getEmail()) &&
        getRole() == user.getRole();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getEmail(), getRole());
  }
}
