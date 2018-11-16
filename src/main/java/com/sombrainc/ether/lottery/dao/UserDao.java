package com.sombrainc.ether.lottery.dao;

import com.sombrainc.ether.lottery.entity.User;
import com.sombrainc.ether.lottery.entity.User.Role;
import com.sombrainc.ether.lottery.entity.Wallet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends NamedParameterJdbcDaoSupport implements IUserDao {

  private static final Logger LOGGER = LoggerFactory.getLogger("DAO");

  private static final String ID = "id";
  private static final String WALLET_ID = "wallet_id";
  private static final String EMAIL = "email";
  private static final String ROLE = "role";
  private static final String ADDRESS = "address";
  private static final String PASSWORD = "password";
  private static final String WALLET = "wallet";

  private static final String INSERT = "INSERT INTO user (" + EMAIL
      + ", " + PASSWORD + ", " + ROLE + ") VALUES(:" + EMAIL + ", :" + PASSWORD + ", :" + ROLE
      + ")";

  private static final String FIND_BY_EMAIL =
      "SELECT * FROM user WHERE " + EMAIL + " = :" + EMAIL;

  private static final String FIND_BY_EMAIL_WITH_WALLET =
      "SELECT u.id as id, u.email as " + EMAIL + ", u.role as " + ROLE
          + ", u.password as " + PASSWORD + ","
          + " w.id as " + WALLET_ID + ", w.wallet as " + WALLET + ", w.address as " + ADDRESS
          + " "
          + "FROM user u JOIN wallet w ON u.id = w.user_id WHERE u.email = :" + EMAIL;

  private static final String FIND_BY_ADDRESS =
      "SELECT u.id as id, u.email as " + EMAIL + ", u.role as " + ROLE
          + ", u.password as " + PASSWORD+ ","
          + " w.id as " + WALLET_ID + ", w.wallet as " + WALLET + ", w.address as " + ADDRESS
          + " "
          + "FROM user u JOIN wallet w ON u.id = w.user_id WHERE w.address = :" + ADDRESS;

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public UserDao(DataSource dataSource) {
    setDataSource(dataSource);
    this.namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
  }

  @Override
  public User insertUser(User user) {
    GeneratedKeyHolder holder = new GeneratedKeyHolder();

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(EMAIL, user.getEmail());
    parameters.addValue(PASSWORD, user.getPassword());
    parameters.addValue(ROLE, user.getRole().name());
    namedParameterJdbcTemplate
        .update(INSERT, parameters, holder);
    final Number key = holder.getKey();
    Optional.ofNullable(key).orElseThrow();
    return User.builder(user).setId(key.longValue()).build();
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(EMAIL, email);
    try {
      return Optional.ofNullable(namedParameterJdbcTemplate
          .queryForObject(FIND_BY_EMAIL, parameters, new UserMapper()));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<User> findUserByEmailWithWallet(String email) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(EMAIL, email);
    try {
      return Optional.ofNullable(namedParameterJdbcTemplate
          .queryForObject(FIND_BY_EMAIL_WITH_WALLET, parameters, new UserWithWalletMapper()));
    } catch (EmptyResultDataAccessException e) {
      LOGGER.debug(e.getLocalizedMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public Optional<User> findUserByWalletAddress(String walletAddress) {
    String dbWalletAddress =
        walletAddress.startsWith("0x") ? walletAddress : "0x" + walletAddress;
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(ADDRESS, dbWalletAddress);
    try {
      return Optional.ofNullable(namedParameterJdbcTemplate
          .queryForObject(FIND_BY_ADDRESS, parameters, new UserWithWalletMapper()));
    } catch (EmptyResultDataAccessException e) {
      LOGGER.debug(e.getLocalizedMessage(), e);
      return Optional.empty();
    }
  }


  private static final class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

      return User.builder().setId(rs.getLong(ID)).setEmail(rs.getString(EMAIL))
          .setPassword(rs.getString(PASSWORD)).setRole(
              Role.valueOf(rs.getString(ROLE))).build();
    }
  }

  private static final class UserWithWalletMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      Wallet wallet = Wallet.builder().setId(rs.getLong(WALLET_ID)).setWallet(rs.getBytes(WALLET))
          .setAddress(rs.getString(ADDRESS)).build();
      return User.builder(new UserMapper().mapRow(rs, rowNum)).setWallet(wallet).build();
    }
  }
}

