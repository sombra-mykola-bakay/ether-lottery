package com.sombrainc.ether.lottery.dao;

import static java.util.Objects.requireNonNull;

import com.sombrainc.ether.lottery.entity.Auction;
import com.sombrainc.ether.lottery.entity.Auction.AuctionState;
import com.sombrainc.ether.lottery.entity.User;
import com.sombrainc.ether.lottery.model.EtherAmount;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AuctionDao extends NamedParameterJdbcDaoSupport implements IAuctionDao {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm");
  private static final String VERSION = "version";
  private static final String ID = "id";
  private static final String HASH = "hash";
  private static final String ADDRESS = "address";
  private static final String CREATOR_ID = "creator_id";
  private static final String CREATOR_EMAIL = "creator_email";
  private static final String STATE = "state";
  private static final String STATES = "states";
  private static final String DESCRIPTION = "description";
  private static final String WINNER_INDEX = "winner_index";
  private static final String USER_ID = "user_id";
  private static final String AUCTION_ID = "auction_id";
  private static final String WINNER = "winner_id";
  private static final String EMAIL = "email";
  private static final String EXPECTED_PARTICIPANTS = "expected_participants";
  private static final String MIN_PAYABLE_ETHER_AMOUNT = "min_payable_ether_amount";
  private static final String VALID_TILL = "valid_till";
  private static final String WINNER_MESSAGE = "winner_message";

  private static final String INSERT =
      "INSERT INTO auction (" + VERSION + "," + ADDRESS + "," + CREATOR_ID
          + ", state, " + DESCRIPTION + ", " + HASH + " , " + WINNER_INDEX + ", "
          + MIN_PAYABLE_ETHER_AMOUNT
          + ", " + EXPECTED_PARTICIPANTS + ", " + VALID_TILL + ", "+WINNER_MESSAGE+")"
          + " VALUES(:" + VERSION + ", :" + ADDRESS + ",:" + CREATOR_ID
          + ",:state, :" + DESCRIPTION + ", :" + HASH + " , :" + WINNER_INDEX + ", :"
          + MIN_PAYABLE_ETHER_AMOUNT
          + ", :" + EXPECTED_PARTICIPANTS + ", :" + VALID_TILL
          + ", :" + WINNER_MESSAGE +")";
  private static final String INSERT_PARTICIPANT =
      "INSERT INTO auction_participant (auction_id, user_id)"
          + " VALUES(:auction_id, :user_id)";

  private static final String FIND_PARTICIPATING =
      "SELECT a." + VERSION + " as " + VERSION + ", a." + ID + " as " + ID + ", a." + ADDRESS
          + " as " + ADDRESS + ", a." + CREATOR_ID
          + " as "
          + CREATOR_ID + ", cu." + EMAIL + " as " + CREATOR_EMAIL
          + ", a.description as description,"
          + " a.state as state, a.hash as hash, a.winner_index as winner_index,"
          + " a.winner_id as winner_id, a." + MIN_PAYABLE_ETHER_AMOUNT
          + " as min_payable_ether_amount,"
          + " a." + EXPECTED_PARTICIPANTS + " as expected_participants, "
          + " a." + VALID_TILL + " as valid_till, "
          + " ap.user_id as user_id, u.email as " + EMAIL
          + " FROM auction a LEFT JOIN auction_participant ap on a." + ID
          + " = ap.auction_id LEFT JOIN user u ON ap.user_id= u." + ID
          + "  LEFT JOIN user cu ON a.creator_id= cu.id WHERE ap.user_id = :user_id";
  private static final String FIND_PARTICIPATING_IN_STATES =FIND_PARTICIPATING
          + " AND " + STATE + " IN (:" + STATES + ")";


  private static final String FIND_BY_CREATOR =
      "SELECT a." + VERSION + " as " + VERSION + ", a." + ID + " as " + ID + ", a." + ADDRESS
          + " as " + ADDRESS + ", a." + CREATOR_ID
          + " as "
          + CREATOR_ID + ", cu." + EMAIL + " as " + CREATOR_EMAIL
          + ",a.description as description,"
          + " a.state as state, a.hash as hash, a.winner_index as winner_index,"
          + " a.winner_id as winner_id, a." + MIN_PAYABLE_ETHER_AMOUNT
          + " as min_payable_ether_amount,"
          + " a." + EXPECTED_PARTICIPANTS + " as expected_participants, "
          + " a." + VALID_TILL + " as valid_till, "
          + " ap.user_id as user_id, u.email as " + EMAIL
          + " FROM auction a LEFT JOIN auction_participant ap ON a." + ID
          + " = ap.auction_id LEFT JOIN user u ON ap.user_id= u." + ID
          + " LEFT JOIN user cu ON a.creator_id= cu.id WHERE a."
          + CREATOR_ID + " = :" + CREATOR_ID;

  private static final String FIND_BY_CREATOR_IN_STATES = FIND_BY_CREATOR + " AND"
      + " a." + STATE + " IN (:" + STATES + ")";

  private static final String FIND_WINNING =
      "SELECT a." + VERSION + " as " + VERSION + ", a." + ID + " as " + ID + ", a." + ADDRESS
          + " as " + ADDRESS + ", a." + CREATOR_ID
          + " as "
          + CREATOR_ID + ", cu." + EMAIL + " as " + CREATOR_EMAIL
          + ", a.description as description,"
          + " a.state as state, a.hash as hash, a.winner_index as winner_index,"
          + " a.winner_id as winner_id, a." + MIN_PAYABLE_ETHER_AMOUNT
          + " as min_payable_ether_amount,"
          + " a." + EXPECTED_PARTICIPANTS + " as expected_participants, "
          + " a." + VALID_TILL + " as valid_till, "
          + " ap.user_id as user_id, u.email as " + EMAIL
          + " FROM auction a LEFT JOIN auction_participant ap on a." + ID
          + " = ap.auction_id LEFT JOIN user u ON ap.user_id= u." + ID
          + " LEFT JOIN user cu ON a.creator_id= cu.id WHERE a.winner_id = :user_id";

  private static final String FIND_ALL =
      "SELECT a." + VERSION + " as " + VERSION + ", a." + ID + " as " + ID + ", a." + ADDRESS
          + " as " + ADDRESS + ", a." + CREATOR_ID
          + " as "
          + CREATOR_ID + ", cu." + EMAIL + " as " + CREATOR_EMAIL
          + ", a.description as description,"
          + " a.state as state, a.hash as hash, a.winner_index as winner_index, "
          + " a.winner_id as winner_id, a." + MIN_PAYABLE_ETHER_AMOUNT
          + " as min_payable_ether_amount,"
          + " a." + EXPECTED_PARTICIPANTS + " as expected_participants, "
          + " a." + VALID_TILL + " as valid_till "
          + " FROM auction a LEFT JOIN user cu ON a.creator_id= cu.id";

  private static final String FIND_IN_STATES =
      FIND_ALL + " WHERE " + STATE + " IN (:" + STATES + ")";

  private static final String FIND_BY_ADDRESS =
      "SELECT a." + VERSION + " as " + VERSION + ", a." + ID + " as " + ID + ", a." + ADDRESS
          + " as " + ADDRESS + ", a." + CREATOR_ID
          + " as "
          + CREATOR_ID + ", cu." + EMAIL + " as " + CREATOR_EMAIL
          + ",a.description as description,"
          + " a.state as state, a.hash as hash, a.winner_index as winner_index,"
          + " a.winner_id as winner_id, a." + MIN_PAYABLE_ETHER_AMOUNT
          + " as min_payable_ether_amount,"
          + " a." + EXPECTED_PARTICIPANTS + " as expected_participants, "
          + " a." + VALID_TILL + " as valid_till, "
          + " ap.user_id as user_id, u.email as " + EMAIL
          + " FROM auction a LEFT JOIN auction_participant ap ON a." + ID
          + " = ap.auction_id LEFT JOIN user u ON ap.user_id= u." + ID
          + " LEFT JOIN user cu ON a.creator_id= cu.id WHERE a."
          + ADDRESS + " = :" + ADDRESS;

  private static final String SET_STATE = "UPDATE auction SET state=:state"
      + " WHERE " + ID + "=:" + ID + "";

  private static final String SET_ACTIVE_AND_WINNER_ID =
      "UPDATE auction SET state=:state, winner_id=:winner_id"
          + " WHERE " + ID + "=:" + ID + "";

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public AuctionDao(DataSource dataSource) {
    setDataSource(dataSource);
    namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
  }

  @Override
  public Auction insertAuction(Auction auction) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(VERSION, auction.getVersion());
    parameters.addValue(ADDRESS, auction.getAddress());
    parameters.addValue(CREATOR_ID, auction.getCreator().getId());
    parameters.addValue(DESCRIPTION, auction.getDescription());
    parameters.addValue(STATE, auction.getState().name());
    parameters.addValue(HASH, auction.getHash());
    parameters.addValue(WINNER_INDEX, auction.getEncryptedWinnerIndex());
    parameters.addValue(EXPECTED_PARTICIPANTS, auction.getExpectedParticipants());
    parameters
        .addValue(MIN_PAYABLE_ETHER_AMOUNT, auction.getMinPayableEtherAmount().toFormattedString());
    parameters.addValue(VALID_TILL, FORMATTER.format(auction.getValidTill()));
    parameters.addValue(WINNER_MESSAGE, auction.getWinnerMessage());


    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate
        .update(INSERT, parameters, generatedKeyHolder);
    return Auction.builder(auction)
        .setId(requireNonNull(generatedKeyHolder.getKey()).longValue()).build();
  }

  @Override
  public void addAuctionParticipant(Long auctionId, Long userId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(AUCTION_ID, auctionId);
    parameters.addValue(USER_ID, userId);

    namedParameterJdbcTemplate
        .update(INSERT_PARTICIPANT, parameters);
  }

  @Override
  public Collection<Auction> findCreatedByUserInStates(Long userId, List<AuctionState> states) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(CREATOR_ID, userId);
    parameters.addValue(STATES, states.stream().map(Enum::name).collect(Collectors.toList()));
    return namedParameterJdbcTemplate
        .query(FIND_BY_CREATOR_IN_STATES, parameters, new AuctionParticipantMapper());
  }

  @Override
  public Collection<Auction> findCreatedByUser(Long userId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(CREATOR_ID, userId);
    return namedParameterJdbcTemplate
        .query(FIND_BY_CREATOR, parameters, new AuctionParticipantMapper());
  }

  @Override
  public Collection<Auction> findInStates(List<AuctionState> states) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(STATES, states.stream().map(Enum::name).collect(Collectors.toList()));
    return namedParameterJdbcTemplate.query(FIND_IN_STATES, parameters, new AuctionMapper());
  }

  @Override
  public Collection<Auction> findAll() {
    return namedParameterJdbcTemplate.query(FIND_ALL, new AuctionMapper());
  }

  @Override
  public Optional<Auction> findByAddress(String auctionAddress) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(ADDRESS, auctionAddress);

    final Collection<Auction> result = namedParameterJdbcTemplate
        .query(FIND_BY_ADDRESS, parameters, new AuctionParticipantMapper());
    if (result == null) {
      throw new RuntimeException();
    }
    if (result.size() > 1) {
      throw new IncorrectResultSizeDataAccessException(1);
    }
    return result.stream().findFirst();
  }

  @Override
  public Collection<Auction> findParticipatingInStates(Long userId, List<AuctionState> states) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(USER_ID, userId);
    parameters.addValue(STATES, states.stream().map(Enum::name).collect(Collectors.toList()));
    return namedParameterJdbcTemplate
        .query( FIND_PARTICIPATING_IN_STATES, parameters, new AuctionParticipantMapper());
  }

  @Override
  public Collection<Auction> findParticipating(Long userId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(USER_ID, userId);
    return namedParameterJdbcTemplate
        .query(FIND_PARTICIPATING, parameters, new AuctionParticipantMapper());
  }


  @Override
  public Collection<Auction> findWinning(Long userId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(USER_ID, userId);
    return namedParameterJdbcTemplate
        .query(FIND_WINNING, parameters, new AuctionParticipantMapper());
  }

  @Override
  public void setState(Long auctionId, AuctionState state) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(ID, auctionId);
    parameters.addValue(STATE, state.name());
    namedParameterJdbcTemplate
        .update(SET_STATE, parameters);
  }

  @Override
  public void setStateAndWinner(Long auctionId, AuctionState active, Long userId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue(ID, auctionId);
    parameters.addValue(STATE, active.name());
    parameters.addValue(WINNER, userId);
    namedParameterJdbcTemplate
        .update(SET_ACTIVE_AND_WINNER_ID, parameters);
  }


  private static final class AuctionParticipantMapper implements
      ResultSetExtractor<Collection<Auction>> {

    @Override
    public Collection<Auction> extractData(ResultSet resultSet)
        throws SQLException, DataAccessException {
      Map<Long, Auction> map = new HashMap<>();
      while (resultSet.next()) {
        long id = resultSet.getLong(ID);
        Auction auction = map
            .getOrDefault(id, new AuctionMapper().mapRow(resultSet, resultSet.getRow()));

        final long userId = resultSet.getLong(USER_ID);
        if (userId != 0) {
          auction = Auction.builder(auction)
              .addParticipant(User.builder().setId(userId).setEmail(
                  resultSet.getString(EMAIL)).build()).build();
        }

        map.putIfAbsent(id, auction);
      }
      return map.values().stream().map(auction -> Optional.ofNullable(auction.getWinner())
          .map(winner -> Auction.builder(auction).setWinner(
              auction.getParticipants().stream()
                  .filter(ap -> ap.getId().equals(winner.getId())).findFirst()
                  .orElseThrow())
              .build()).orElse(auction)).collect(Collectors.toList());
    }
  }

  private static final class AuctionMapper implements RowMapper<Auction> {

    public Auction mapRow(ResultSet resultSet, int rowNum) throws SQLException {

      final long winnerId = resultSet.getLong(WINNER);
      return Auction.builder().setVersion(resultSet.getString(VERSION))
          .setId(resultSet.getLong(ID))
          .setAddress(resultSet.getString(ADDRESS))
          .setCreator(User.builder().setId(resultSet.getLong(CREATOR_ID))
              .setEmail(resultSet.getString(CREATOR_EMAIL)).build())
          .setState(AuctionState.valueOf(resultSet.getString(STATE)))
          .setDescription(resultSet.getString(DESCRIPTION))
          .setHash(resultSet.getString(HASH))
          .setEncryptedWinnerIndex(resultSet.getString(WINNER_INDEX))
          .setWinner(winnerId != 0 ? User.builder().setId(winnerId).build() : null)
          .setExpectedParticipants(resultSet.getInt(EXPECTED_PARTICIPANTS))
          .setMinPayableEtherAmount(
              EtherAmount.fromString(resultSet.getString(MIN_PAYABLE_ETHER_AMOUNT)))
          .setValidTill(LocalDateTime.parse(resultSet.getString(VALID_TILL), FORMATTER))
          .build();
    }
  }
}
