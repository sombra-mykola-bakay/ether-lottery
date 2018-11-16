package com.sombrainc.ether.lottery.dao;

import static java.util.Objects.requireNonNull;

import com.sombrainc.ether.lottery.entity.Wallet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class WalletDao extends NamedParameterJdbcDaoSupport implements IWalletDao {


  private static final String WALLET = "wallet";
  private static final String USER_ID = "user_id";
  private static final String ADDRESS = "address";
  private static final String ID = "id";
  private static final String EMAIL = "email";
  private static final String INSERT = "INSERT INTO wallet (address, wallet, user_id) VALUES(:address, :wallet, :user_id)";
  private static final String FIND_BY_EMAIL =
      "SELECT w.* FROM wallet w join user u on u.id = w.user_id WHERE u.email = :email";
  private static final String FIND_OTHER_ADDRESSES =
      "SELECT w.address FROM wallet w join user u on u.id = w.user_id WHERE u.email != :email";
  private static final String X = "0x";
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Autowired
  public WalletDao(DataSource dataSource) {
    setDataSource(dataSource);
    namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
  }

  @Override
  public Wallet insertWallet(Wallet wallet) {
    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    final String address =
        wallet.getAddress().startsWith(X) ? wallet.getAddress() : X + wallet.getAddress();
    parameters.addValue(ADDRESS, address);
    parameters.addValue(WALLET, wallet.getWallet());
    parameters.addValue(USER_ID, wallet.getUserId());
    namedParameterJdbcTemplate
        .update(INSERT, parameters, generatedKeyHolder);
    final Number key = generatedKeyHolder.getKey();
    Optional.ofNullable(key).orElseThrow();
    return Wallet.builder(wallet).setId(key.longValue()).build();
  }


  @Override
  public Optional<Wallet> findUserWallet(String email) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(EMAIL, email);
    try {
      return Optional.of(requireNonNull(namedParameterJdbcTemplate
          .queryForObject(FIND_BY_EMAIL, parameters, new WalletMapper())));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Collection<String> findOtherAddresses(String email) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(EMAIL, email);

    return namedParameterJdbcTemplate
        .query(FIND_OTHER_ADDRESSES, parameters, (rs, rowNum) -> rs.getString(ADDRESS));

  }


  private static final class WalletMapper implements RowMapper<Wallet> {

    public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {

      return Wallet.builder().setId(rs.getLong(ID)).setAddress(rs.getString(ADDRESS))
          .setWallet(rs.getBytes(WALLET)).setUserId(rs.getLong(USER_ID)).build();
    }
  }


}
