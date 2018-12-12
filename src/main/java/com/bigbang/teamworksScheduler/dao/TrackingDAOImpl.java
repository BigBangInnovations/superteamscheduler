package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.beans.UserDistanceSum;
import com.bigbang.teamworksScheduler.beans.UserLocation;

public class TrackingDAOImpl implements TrackingDAO{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	Logger LOG = LogManager.getLogger(TrackingDAOImpl.class);
	
	private static String GET_COMPANYUSERS_SQL = "Select Distinct(U.ID) from " + MvcConfiguration.masterSchema
			+ ".USERS U, " + MvcConfiguration.masterSchema + ".USER_ROLE R where U.ID = R.User_ID and R.Active  = 1 "
					+ " and  R.Company_ID IN(:companyList)";
	
	private static String GET_USERS_SQL = "Select Distinct(U.ID) from " + MvcConfiguration.masterSchema
			+ ".USERS U, " + MvcConfiguration.masterSchema + ".USER_ROLE R where U.ID = R.User_ID and R.Active  = 1 "
					+ " and  R.Company_ID = :companyList";
	
	private static String GET_TRACKING_MISS_USERS_SQL = "Select Distinct(User_ID) from User_Location l where "
			+ "l.User_ID IN(:userList) and l.DateTime >= :dateTime";
	
	private static String GET_LOGGEDIN_USERS = "Select l.User_ID,l.GCM_ID  from " 
			+ MvcConfiguration.masterSchema + ".USER_APPLICATION_GCM l where "
			+ "l.User_ID IN(:userList) and l.Application_ID = :applicationID";
	
	private static String GET_COMPANY = "Select DISTINCT(c.Company_ID) from Company c where "
			+ "c.Tracking_StartTime <= :currentTime and c.Tracking_EndTime >= :currentTime";
	
	private static String GET_USER_TRACKING = "Select * FROM User_Location Where User_ID = :userID AND "
			+ " (Latitude != 0 AND Longitude != 0) AND "
			+ " DATE(`DateTime`) = :currentDate order by `DateTime` asc"; //'2016-11-17' currentDate
	
	/*private static String GET_USER_TRACKING = "Select * FROM User_Location Where User_ID = :userID AND "
			+ " (Latitude != 0 AND Longitude != 0) AND "
			+ " (DATE(`DateTime`) >= '2017-3-1' AND DATE(`DateTime`) <= '2017-3-31') order by `DateTime` asc";*/
 	
	private static String UPDATE_USER_LOCATION_DISTANCE = "UPDATE User_Location SET "
			+ "Tracking_Distance = :trackingDistance ,Distance_Value = :Distance_Value"
			+ " WHERE User_ID = :userID AND `DateTime` = :dateTime";
	
	private static final String GET_ALL = "SELECT l.User_ID, u.First_Name, u.Last_Name, u.Picture, l.DateTime, "
			+ "l.Location, l.Latitude, l.Longitude, l.GPS_On, l.Tracking_Type, l.Tracking_Distance " + "FROM User_Location l, "
			+ MvcConfiguration.masterSchema
			+ ".USERS u WHERE CAST(l.DateTime AS DATE) = :currentDate AND u.ID IN (:useridlist) and "
			+ "u.ID = l.User_ID AND l.Company_ID = :companyid AND l.Active = 1 "
			+ "AND (l.Latitude != 0 AND l.Longitude != 0) GROUP BY u.First_Name, l.DateTime ASC";
	//'2016-11-17'
	
	private static String GET_ALL_COMPANY = "Select DISTINCT(Company_ID) from Company ";
	
	private static String GET_USER_TRACKING_FIRST_TRACKING = "Select * FROM User_Location Where User_ID = :userID AND "
			 +"(Latitude != 0 AND Longitude != 0) AND "
			 +"(DATE(`DateTime`) >= '2017-3-1' AND DATE(`DateTime`) <= '2017-3-31') GROUP BY CAST(`DateTime` as DATE)";
	
	private static String GET_ALL_TRACKING_COMPANIES = "SELECT * FROM Tracking_Company WHERE Is_Active = :active ";
	
	private static final String GET_USER_TOTAL_DISTANCE_PER_DAY = "select SUM(ul.Distance_Value) AS distance,ul.User_ID,"
			+ "CAST(ul.`DateTime` AS DATE)  AS date,u.First_Name,u.Last_Name from User_Location AS ul JOIN "
			+MvcConfiguration.masterSchema
			+ ".USERS u ON u.ID = ul.User_ID where CAST(`DateTime` AS DATE) = CAST(:currentDate AS DATE) " //'2017/6/1 12:00:00'
			+ " AND Company_ID = :companyID  AND User_ID IN (:userList) "
			+ " GROUP BY User_ID,CAST(`DateTime` AS DATE)";
	
	private static final String GET_USERS_DISTANCE_TRAVELLED_SUM = "select SUM(Distance_Value) AS distance,User_ID,"
			+" Company_ID,CAST(`DateTime` AS DATE)  AS date from User_Location "
			+" where CAST(`DateTime` AS DATE) = :currentDate "
			+" AND Company_ID = :companyID  AND User_ID IN (:userList) "
			+" GROUP BY User_ID,CAST(`DateTime` AS DATE)";
	
	private static final String ADD_USER_DAILY_DISTANCE_SUM = "INSERT INTO User_Daily_Distance_Tracking "
			+ " (User_ID,Company_ID,`DateTime`,Distance_Value,Modified_Date) VALUES "
			+ " (:userID,:companyID,:dateTime,:DistanceValue,:modifiedDate) ";
	
	private static final String GET_USERS_DISTANCE_TRAVELLED_SUM_MONTH = "select SUM(Distance_Value) AS distance,User_ID,"
			+" Company_ID,CAST(`DateTime` AS DATE)  AS date from User_Location "
			+" where CAST(`DateTime` AS DATE) >= :startDate AND "
			+" CAST(`DateTime` AS DATE) <= :endDate AND Company_ID = :companyID  AND User_ID IN (:userList) "
			+" GROUP BY User_ID,CAST(`DateTime` AS DATE)";
	
	@Override
	public List<Long> getCompanyUsers(List<Long> companyIDList) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyList",companyIDList);
		List<Long> List = new ArrayList<Long>();
		try {
			List = namedParameterJdbcTemplate.query(GET_COMPANYUSERS_SQL, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("ID"));
						}
					});

			return List;
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error getting list active companies", e);
			return null;
		}
	}

	@Override
	public List<Long> getCompanyTrackingUsers(List<Long> usersList,Date date) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userList",usersList);
		namedParameters.addValue("dateTime",date);
		
//		LOG.debug("Query "+GET_TRACKING_MISS_USERS_SQL);
//		LOG.debug("userList "+usersList);
//		LOG.debug("datetime "+date);
		List<Long> List = new ArrayList<Long>();
		try {
			List = namedParameterJdbcTemplate.query(GET_TRACKING_MISS_USERS_SQL, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("User_ID"));
						}
					});

			return List;
		} catch (DataAccessException e) {
			LOG.error("Error fetching missed tracking users ", e);
			return null;
		}
	}

	@Override
	public Map<Long, String> getLoggedInUsersList(List<Long> userList) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		Map<Long, String> map = new HashMap<Long, String>();
		namedParameters.addValue("userList", userList);
		namedParameters.addValue("applicationID", 1);

		

		namedParameterJdbcTemplate.query(GET_LOGGEDIN_USERS, namedParameters, new RowMapper<Map<Long, String>>() {

			@Override
			public Map<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rs.getString("GCM_ID") != null)
					map.put(Long.parseUnsignedLong(rs.getString("User_ID")), rs.getString("GCM_ID"));
				else
					map.put(Long.parseUnsignedLong(rs.getString("User_ID")), "");
				return map;
			}
		});
		return map;
	}
	
	@Override
	public List<Long> getCompany(String timeStr) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("currentTime", timeStr);
		List<Long> companyIDList = new ArrayList<Long>();
		companyIDList = namedParameterJdbcTemplate.query(GET_COMPANY, namedParameters,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						return Long.parseUnsignedLong(rs.getString("Company_ID"));
					}
				});

		return companyIDList;
	}

	@Override
	public List<UserLocation> getUserLocationList(long userID,String currentDate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("currentDate", currentDate);
		
		List<UserLocation> userLocationList = new ArrayList<UserLocation>();
		userLocationList = namedParameterJdbcTemplate.query(GET_USER_TRACKING, namedParameters,
				new RowMapper<UserLocation>() {
					@Override
					public UserLocation mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						UserLocation userLoc = new UserLocation();
						userLoc.setLatitude(rs.getDouble("Latitude"));
						userLoc.setLongitude(rs.getDouble("Longitude"));
						userLoc.setUserid(rs.getInt("User_ID"));
						userLoc.setDate(rs.getTimestamp("DateTime"));
						return userLoc;
					}
				});

		return userLocationList;
	}

	@Override
	public int updateUserLocationDistance(List<UserLocation> updatedUserLocation) {
		LOG.debug("In update user location distance");
		
		int i = 0;

		MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[updatedUserLocation.size()];

		for (UserLocation location : updatedUserLocation) {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("userID", location.getUserid());
			namedParameters.addValue("trackingDistance", location.getDistance());
			namedParameters.addValue("Distance_Value", location.getDistanceValue());
			namedParameters.addValue("dateTime", location.getDate());
			namedParametersArray[i] = namedParameters;
			i++;
		}
		
		int[] retVal;

		try {
			retVal = namedParameterJdbcTemplate.batchUpdate(UPDATE_USER_LOCATION_DISTANCE, namedParametersArray);
			return retVal.length;
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error updating user distance from Location" + e);
			return -1;
		}
		
//		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
//		namedParameters.addValue("userID", userID);
//		namedParameters.addValue("trackingDistance", distance);
//		namedParameters.addValue("dateTime", dateTime);
//		
//		int retVal = 0;
//		retVal = namedParameterJdbcTemplate.update(UPDATE_USER_LOCATION_DISTANCE, namedParameters);
//		return retVal;
	}
	
	@Override
	public List<UserLocation> getAll(final List<Long> userIDList, String currentDate,long companyid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);
		namedParameters.addValue("useridlist", userIDList);
		namedParameters.addValue("currentDate", currentDate);
		
		List<UserLocation> listUserLocation = namedParameterJdbcTemplate.query(GET_ALL, namedParameters,
				new RowMapper<UserLocation>() {

					@Override
					public UserLocation mapRow(final ResultSet rs, final int rowNum) throws SQLException {

						Date datetime = new java.util.Date(rs.getTimestamp("DateTime").getTime());

						UserLocation aUserLocation = new UserLocation();

						aUserLocation.setUserid(Long.parseUnsignedLong(rs.getString("User_ID")));
						aUserLocation.setFirstName(rs.getString("First_Name"));
						aUserLocation.setLastName(rs.getString("Last_Name"));
//						aUserLocation.setImageURL((rs.getString("Picture") == null) ? "" : (rs.getString("Picture")));
						aUserLocation.setTime(new SimpleDateFormat("HH:mm:ss").format(datetime));
						aUserLocation.setDate(datetime);
						aUserLocation.setLocation(rs.getString("Location"));
						aUserLocation.setLatitude(rs.getDouble("Latitude"));
						aUserLocation.setLongitude(rs.getDouble("Longitude"));
						aUserLocation.setGpsOn(rs.getBoolean("GPS_On"));
						aUserLocation.setTrackingType(rs.getString("Tracking_Type"));
						aUserLocation.setDistance(rs.getString("Tracking_Distance"));
						return aUserLocation;
					}
				});

//		Collections.sort(listUserLocation, UserLocation.firstNameComparator);
		return listUserLocation;
	}

	@Override
	public List<Long> getUsers(long companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyList",companyID);
		List<Long> List = new ArrayList<Long>();
		try {
			List = namedParameterJdbcTemplate.query(GET_USERS_SQL, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("ID"));
						}
					});

			return List;
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error getting list active companies", e);
			return null;
		}
	}

	@Override
	public List<Long> getAllCompanyIDs() {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		List<Long> companyIDList = new ArrayList<Long>();
		companyIDList = namedParameterJdbcTemplate.query(GET_ALL_COMPANY, namedParameters,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						return Long.parseUnsignedLong(rs.getString("Company_ID"));
					}
				});

		return companyIDList;
	}

	@Override
	public List<UserLocation> getUserLocationList1(long userID,
			String currentDate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("currentDate", currentDate);
		
		List<UserLocation> userLocationList = new ArrayList<UserLocation>();
		userLocationList = namedParameterJdbcTemplate.query(GET_USER_TRACKING_FIRST_TRACKING, namedParameters,
				new RowMapper<UserLocation>() {
					@Override
					public UserLocation mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						UserLocation userLoc = new UserLocation();
						userLoc.setLatitude(rs.getDouble("Latitude"));
						userLoc.setLongitude(rs.getDouble("Longitude"));
						userLoc.setUserid(rs.getInt("User_ID"));
						userLoc.setDate(rs.getTimestamp("DateTime"));
						return userLoc;
					}
				});

		return userLocationList;
	}

	@Override
	public List<Long> getAllTrackingCompanies() {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("active", 1);
		List<Long> List = new ArrayList<Long>();
		
		try {
			List = namedParameterJdbcTemplate.query(GET_ALL_TRACKING_COMPANIES, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("Company_ID"));
						}
					});

			return List;
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error getting list active companies", e);
			return null;
		}
	}
	
	@Override
	public List<UserLocation> getUserDistancePerDay(Date currentDate, long companyID,List<Long> userIDList) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("currentDate", currentDate);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("userList", userIDList);
		
		try{
			List<UserLocation> listUserLocation = namedParameterJdbcTemplate.query(GET_USER_TOTAL_DISTANCE_PER_DAY, namedParameters,
					new RowMapper<UserLocation>() {

				@Override
				public UserLocation mapRow(final ResultSet rs, final int rowNum) throws SQLException {

					UserLocation aUserLocation = new UserLocation();

					aUserLocation.setUserid(Long.parseUnsignedLong(rs.getString("User_ID")));
					aUserLocation.setDate(rs.getTimestamp("date"));
					aUserLocation.setDistanceValue(rs.getInt("distance"));
					aUserLocation.setFirstName(rs.getString("First_Name"));
					aUserLocation.setLastName(rs.getString("Last_Name"));
					return aUserLocation;
				}
			});
			return listUserLocation;
		}catch(Exception e){
			//e.printStackTrace();
			LOG.error("Exception "+e);
			return new ArrayList<UserLocation>();
		}
	}

	@Override
	public List<UserDistanceSum> getUserDistanceSum(List<Long> userList,
			String currentDate, long companyID) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("currentDate", currentDate);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("userList", userList);
		
		try{
			List<UserDistanceSum> listUserLocation = namedParameterJdbcTemplate.query(GET_USERS_DISTANCE_TRAVELLED_SUM, namedParameters,
					new RowMapper<UserDistanceSum>() {

				@Override
				public UserDistanceSum mapRow(final ResultSet rs, final int rowNum) throws SQLException {

					UserDistanceSum aUserLocation = new UserDistanceSum();

					aUserLocation.setUserid(Long.parseUnsignedLong(rs.getString("User_ID")));
					aUserLocation.setDate(rs.getTimestamp("date"));
					aUserLocation.setDistanceValue(rs.getInt("distance"));
					//aUserLocation.setFirstName(rs.getString("First_Name"));
					//aUserLocation.setLastName(rs.getString("Last_Name"));
					aUserLocation.setCompanyid(rs.getInt("Company_ID"));
					return aUserLocation;
				}
			});
			return listUserLocation;
		}catch(Exception e){
			//e.printStackTrace();
			LOG.error("Exception "+e);
			return new ArrayList<UserDistanceSum>();
		}
	}

	@Override
	public int addUserDailyDistanceSum(List<UserDistanceSum> userDailyDistanceSum, Date dateTime) {
		MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[userDailyDistanceSum.size()];
		LOG.debug("companyID :"+userDailyDistanceSum.get(0).getCompanyid());
		
		try{
			int i = 0;
			for (UserDistanceSum userDistance : userDailyDistanceSum) {
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("companyID", userDistance.getCompanyid());
				namedParameters.addValue("userID", userDistance.getUserid());
				namedParameters.addValue("dateTime", userDistance.getDate());
				namedParameters.addValue("DistanceValue", userDistance.getDistanceValue());
				namedParameters.addValue("modifiedDate", userDistance.getTime());
				namedParametersArray[i] = namedParameters;
				i++;
			}
			int[] retVal;
			retVal = namedParameterJdbcTemplate.batchUpdate(ADD_USER_DAILY_DISTANCE_SUM, namedParametersArray);
			return retVal.length;
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<UserDistanceSum> getUserDistanceSumMonth(List<Long> userList,
			long companyID, String startDate, String endDate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("startDate", startDate);
		namedParameters.addValue("endDate", endDate);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("userList", userList);
		
		try{
			List<UserDistanceSum> listUserLocation = namedParameterJdbcTemplate.query(GET_USERS_DISTANCE_TRAVELLED_SUM_MONTH, namedParameters,
					new RowMapper<UserDistanceSum>() {

				@Override
				public UserDistanceSum mapRow(final ResultSet rs, final int rowNum) throws SQLException {

					UserDistanceSum aUserLocation = new UserDistanceSum();

					aUserLocation.setUserid(Long.parseUnsignedLong(rs.getString("User_ID")));
					aUserLocation.setDate(rs.getTimestamp("date"));
					aUserLocation.setDistanceValue(rs.getInt("distance"));
					aUserLocation.setCompanyid(rs.getInt("Company_ID"));
					return aUserLocation;
				}
			});
			return listUserLocation;
		}catch(Exception e){
			//e.printStackTrace();
			LOG.error("Exception "+e);
			return new ArrayList<UserDistanceSum>();
		}
	}
	
}
