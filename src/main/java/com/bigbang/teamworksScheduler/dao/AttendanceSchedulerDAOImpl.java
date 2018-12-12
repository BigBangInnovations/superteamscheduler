package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;







import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.AddressBean;
import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.TrackingBean;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.service.Properties;

public class AttendanceSchedulerDAOImpl implements AttendanceSchedulerDAO {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	Logger LOG = LogManager.getLogger(AttendanceSchedulerDAOImpl.class);

	private static String GET_NOTIFICATION_TYPES = "Select t.Description, t.Type_ID from "
			+ MvcConfiguration.masterSchema + ".META_NOTIFICATION_TYPE t where t.Description IN (:type);";

	private static String GET_START_TIME_COMPANY = "Select Company_ID from Company where Work_StartTime = :time;";

	private static String GET_COMPANY_START_END_TIME = "Select c.Company_ID, c.Work_StartTime, c.Work_EndTime "
			+ "from Company c, " + MvcConfiguration.masterSchema
			+ ".COMPANY_MASTER m where c.Company_ID = m.ID and m.Is_Active = :active;";

	private static String GET_END_TIME_COMPANY = "Select Company_ID from Company where Work_EndTime = :time;";

	private static String GET_COMPANY_NOT_WORKING_DAY = "Select DISTINCT(c.Company_ID) from Company c, Company_Holidays h"
			+ " where c.Company_ID = h.Company_ID and h.`Date` = :date or c.Working_Days NOT LIKE :day;";
	
	private static String GET_PROPERTIES_SQL = "Select PROPERTY_KEY, PROPERTY_VALUE from PROPERTIES;";

	private static String GET_APPLICATION_SQL = "Select a.Application_ID,a.Sender_ID, a.iOSCertPath, a.iOSPassword, a.iOSProductionEnv "
			+ "from "
			+ MvcConfiguration.masterSchema
			+ ".META_APPLICATIONS a where a.Application_Name = :applicationName;";

	private static String GET_CHECKED_IN_USER = "Select a.User_ID from Attendance_Master a "
			+ "where a.Company_ID IN (:companyID) and a.Attendance_Date = :date;"; //:date '2017-06-15'

	private static String GET_AUTO_CHECK_IN_USER = "Select u.ID, g.GCM_ID, r.Company_ID from "
			+ MvcConfiguration.masterSchema + ".USER_ROLE r, " + MvcConfiguration.masterSchema + ".USERS u LEFT JOIN "
			+ MvcConfiguration.masterSchema
			+ ".USER_APPLICATION_GCM g on u.ID = g.User_ID	where r.Company_ID IN (:companyID) "
			+ "and r.User_ID = u.ID and r.Active = :active and u.ID NOT IN (:userID) ;";
	
	private static String GET_AUTO_CHECK_IN_USER_TEST = "Select u.ID, g.GCM_ID, r.Company_ID from "
			+ MvcConfiguration.masterSchema + ".USER_ROLE r, " + MvcConfiguration.masterSchema + ".USERS u LEFT JOIN "
			+ MvcConfiguration.masterSchema
			+ ".USER_APPLICATION_GCM g on u.ID = g.User_ID	where r.Company_ID IN (:companyID) "
			+ "and r.User_ID = u.ID and r.Active = :active and u.ID IN (:userID) ;";

	private static String GET_USERS = "Select u.ID, r.Company_ID " + "from " + MvcConfiguration.masterSchema
			+ ".USERS u, " + MvcConfiguration.masterSchema + ".USER_ROLE r "
			+ "where r.Company_ID IN (:companyID) and r.User_ID = u.ID and r.Active = :active "
			+ "and u.ID NOT IN (:userID);";

	private String GET_NOT_CHECKED_OUT_USER = "Select a.User_ID from Attendance_Master a "
			+ "where a.Company_ID IN (:companyID) and a.Attendance_Date = :date and a.Present = :present;";

	private static String GET_AUTO_CHECK_OUT_USER = "Select u.ID, g.GCM_ID from " + "" + MvcConfiguration.masterSchema
			+ ".USERS u, " + MvcConfiguration.masterSchema + ".USER_APPLICATION_GCM g "
			+ "where u.ID IN (:userID) and u.ID = g.User_ID and g.Application_ID = :applicationID;";

	private static String GET_ACTIVE_COMPANY_SQL = "Select ID from " + MvcConfiguration.masterSchema
			+ ".COMPANY_MASTER where Is_Active = :active;";

	private static String GET_TRACKING_DATA_SQL = "Select u.User_ID, u.`DateTime`, u.Location, u.Latitude, "
			+ "u.Longitude, u.Company_ID from User_Location u "
			+ "where CAST(u.`DateTime` as DATE) = :date order by u.User_ID, u.`DateTime`;";

	private static String GET_COMPANY_PERMANENT_ADD_SQL = "Select c.Company_ID, m.Address_Master_ID,m.Lattitude, "
			+ "m.Longitude from " + MvcConfiguration.masterSchema + ".ADDRESS_MASTER m, "
			+ MvcConfiguration.masterSchema + ".COMPANY_ADDRESS c "
			+ "where m.Address_Master_ID = c.Address_Master_ID and c.Type = :type";

	private static String ADD_AUTO_CHECKIN_ATTENDANCE_SQL = "Insert into Attendance_Master "
			+ "( Attendance_Date,User_ID ,Company_ID ,CheckIn_Time ,CheckIn_Address_ID ,Present ,Reason ,Modified_By) "
			+ "VALUES (:date, :userID, :companyID, :checkInTime, :checkInAdd, :present, :reason, :modifiedBy);";

	private static String UPDATE_AUTO_CHECKOUT_SQL = " Update Attendance_Master a INNER JOIN (Select MAX(am.ID) max_id from "
			+ "Attendance_Master am where am.User_ID = :userID and am.Company_ID = :companyID and "
			+ "am.Present = :present and am.Attendance_Date = :date) b ON a.ID = b.max_id set "
			+ "CheckOut_Time = :checkOutTime, a.CheckOut_Address_ID = :checkOutAddress, a.Present = :updatePresent "
			+ "where (a.TimeIn is not null and a.TimeIn <= :checkOutTime ) OR "
			+ "(a.CheckIn_Time is not null and a.CheckIn_Time <= :checkOutTime ) OR "
			+ "( a.Updated_TimeIn is not null and a.Updated_TimeIn <= :checkOutTime );";

	private static String GET_USER_ON_LEAVE_SQL = "Select l.User_ID from Leave_Master l "
			+ "where l.`Date` = :date and l.Status = :status and Active = :active;";

	static final String GET_USER_FIRST_CHECKIN_SQL = "Select a.*,cio.CheckIn_Latitude,cio.CheckIn_Longitude "
			+ " from Attendance_Master a "
			+ " LEFT JOIN CheckIn_CheckOut_History AS cio ON cio.Attendance_ID = a.ID "
			+ " where a.User_ID=:userID and a.Company_ID = :companyID and a.Attendance_Date = :date "
			+ " AND (a.CheckIn_Time is NOT NULL OR a.Updated_TimeIn is NOT NULL OR a.TimeIn is NOT NULL) AND a.Leave_Type_ID = 0 "
			+ " and a.Manual_Attendance is NULL and a.Active = 1 order by a.ID asc LIMIT 0,1";

	static final String GET_USER_LAST_CHECKIN_SQL = "Select a.CheckOut_Time,a.TimeOut,a.Updated_TimeOut,"
			+ " a.Attendance_CheckOut_Type,a.ID,a.CheckOut_Address_ID, "
			+ " cio.CheckOut_Latitude,cio.CheckOut_Longitude "
			+ " from Attendance_Master a "
			+ " LEFT JOIN CheckIn_CheckOut_History AS cio ON cio.Attendance_ID = a.ID "
			+ " where a.User_ID=:userID and a.Company_ID = :companyID and a.Attendance_Date = :date "
			+ " AND (a.CheckOut_Time is NOT NULL OR a.Updated_TimeOut is NOT NULL OR a.TimeOut is NOT NULL) AND a.Leave_Type_ID = 0 "
			+ " and a.Manual_Attendance is NULL and a.Active = 1 order by a.ID desc LIMIT 0,1";

	static final String CHECK_LEAVE_APPLIED = "Select Count(*) from Leave_Master where User_ID = :userID and"
			+ " Company_ID= :companyID and Date IN (:date) and Status IN (:status) and Leave_Day = :leaveDay;";

	@Override
	public Map<String, Object> getProperties() {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("applicationName", "teamworks");
		List<String> list = new ArrayList<String>();
		list.add("ATTENDANCE_AUTO_CHECKIN");
		list.add("ATTENDANCE_AUTO_CHECKOUT");
		list.add("AUTO_TRACKING");
		list.add("MISSED_USERS_CHECKIN");
		list.add("geocode.location.key");
		list.add("Start_Stop_Live_Tracking");
		namedParameters.addValue("type", list);

		Map<String, Object> properties = new HashMap<String, Object>();
		namedParameterJdbcTemplate.query(GET_PROPERTIES_SQL, namedParameters, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				properties.put(rs.getString("PROPERTY_KEY"), rs.getString("PROPERTY_VALUE"));
				System.out.println(rs.getString("PROPERTY_KEY"));
				System.out.println(rs.getString("PROPERTY_VALUE"));
				return 0;
			}
		});

		namedParameterJdbcTemplate.query(GET_APPLICATION_SQL, namedParameters, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				properties.put("applicationID", rs.getString("Application_ID"));
				properties.put("senderID", rs.getString("Sender_ID"));
				properties.put("iOSCertPath", rs.getString("iOSCertPath"));
				properties.put("iOSPassword", rs.getString("iOSPassword"));
				properties.put("iOSProductionEnv", rs.getBoolean("iOSProductionEnv"));
				return 0;
			}
		});

		namedParameterJdbcTemplate.query(GET_NOTIFICATION_TYPES, namedParameters, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				properties.put(rs.getString("Description"), rs.getString("Type_ID"));
				return 0;
			}
		});
		return properties;
	}

	@Override
	public List<Long> getStartTimeCompany(String timeStr) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("time", timeStr);
		List<Long> companyIDList = new ArrayList<Long>();
		try {
			companyIDList = namedParameterJdbcTemplate.query(GET_START_TIME_COMPANY, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("Company_ID"));
						}
					});

			return companyIDList;
		} catch (DataAccessException e) {
			LOG.error("Error getting list of company for Start Time: " + timeStr + ":" + e);
			return null;
		}
	}

	@Override
	public List<Long> getEndTimeCompany(String timeStr) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("time", timeStr);
		List<Long> companyIDList = new ArrayList<Long>();
		try {
			companyIDList = namedParameterJdbcTemplate.query(GET_END_TIME_COMPANY, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("Company_ID"));
						}
					});

			return companyIDList;
		} catch (DataAccessException e) {
			LOG.error("Error getting list of company for End Time: " + timeStr + ":" + e);
			return null;
		}
	}

	/**
	 * Get list of company which are not working for the date or Day
	 * 
	 * @param day
	 * @param date
	 * @return List
	 */
	@Override
	public List<Long> getNotWorkingCompany(String day, String date) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("day", "%" + day + "%");
		namedParameters.addValue("date", date);

		List<Long> companyIDList = new ArrayList<Long>();

		companyIDList = namedParameterJdbcTemplate.query(GET_COMPANY_NOT_WORKING_DAY, namedParameters,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						return Long.parseUnsignedLong(rs.getString("Company_ID"));
					}
				});

		return companyIDList;
	}

	// Get list of company which are currently active
	@Override
	public List<Long> getActiveCompany() {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("active", true);
		List<Long> companyIDList = new ArrayList<Long>();
		try {
			companyIDList = namedParameterJdbcTemplate.query(GET_ACTIVE_COMPANY_SQL, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("ID"));
						}
					});

			return companyIDList;
		} catch (DataAccessException e) {
			LOG.error("Error getting list active companies", e);
			return null;
		}
	}

	/**
	 * Get list of users who have checked in for the day
	 * 
	 * @param companyID
	 * @param date
	 * @return List
	 */
	@Override
	public List<Long> getcheckedInUser(List<Long> companyID, String date) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("date", date);

		List<Long> userIDList = new ArrayList<Long>();

		userIDList = namedParameterJdbcTemplate.query(GET_CHECKED_IN_USER, namedParameters, new RowMapper<Long>() {
			@Override
			public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				return Long.parseUnsignedLong(rs.getString("User_ID"));
			}
		});

		return userIDList;
	}

	/**
	 * Get list of users who are checked in but not checked-out
	 * 
	 * @param companyID
	 * @param date
	 * @return List
	 */
	@Override
	public List<Long> getUserNotCheckedOut(List<Long> companyID, Date date) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("date", date);
		namedParameters.addValue("present", true);

		List<Long> userIDList = new ArrayList<Long>();

		userIDList = namedParameterJdbcTemplate.query(GET_NOT_CHECKED_OUT_USER, namedParameters, new RowMapper<Long>() {
			@Override
			public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				return Long.parseUnsignedLong(rs.getString("User_ID"));
			}
		});

		return userIDList;
	}

	/**
	 * Get list if users who were expected to check-in but did not check-in
	 * 
	 * @param companyID
	 * @param userID
	 * @return Map
	 */
	@Override
	public Map<Long, String> getNotCheckedInUserList(List<Long> companyID, List<Long> userID) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("active", true);

		Map<Long, String> map = new HashMap<Long, String>();

		namedParameterJdbcTemplate.query(GET_AUTO_CHECK_IN_USER, namedParameters, new RowMapper<Map<Long, String>>() {

			@Override
			public Map<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rs.getString("GCM_ID") != null)
					map.put(Long.parseUnsignedLong(rs.getString("ID")), rs.getString("GCM_ID"));
				else
					map.put(Long.parseUnsignedLong(rs.getString("ID")), "");
				return map;
			}
		});

		return map;
	}
	
	@Override
	public Map<Long, String> getNotCheckedInUserListTest(List<Long> companyID, List<Long> userID) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("active", true);

		Map<Long, String> map = new HashMap<Long, String>();

		namedParameterJdbcTemplate.query(GET_AUTO_CHECK_IN_USER_TEST, namedParameters, new RowMapper<Map<Long, String>>() {

			@Override
			public Map<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rs.getString("GCM_ID") != null)
					map.put(Long.parseUnsignedLong(rs.getString("ID")), rs.getString("GCM_ID"));
				else
					map.put(Long.parseUnsignedLong(rs.getString("ID")), "");
				return map;
			}
		});

		return map;
	}

	/**
	 * Get list if users who were expected to check-in but did not check-in
	 * 
	 * @param companyID
	 * @param userID
	 * @return Map
	 */
	@Override
	public Map<Long, Long> getAbsentUserCompanyMap(List<Long> companyID, List<Long> userID) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("active", true);

		Map<Long, Long> map = new HashMap<Long, Long>();

		namedParameterJdbcTemplate.query(GET_USERS, namedParameters, new RowMapper<Map<Long, Long>>() {

			@Override
			public Map<Long, Long> mapRow(ResultSet rs, int rowNum) throws SQLException {

				map.put(Long.parseUnsignedLong(rs.getString("ID")), Long.parseUnsignedLong(rs.getString("Company_ID")));
				return map;
			}
		});

		return map;
	}

	/**
	 * return userID vs GCM ID map for the userlist
	 * 
	 * @param userID
	 * @return Map
	 */
	@Override
	public Map<Long, String> getGCMUserMap(List<Long> userID) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("applicationID", Properties.get("applicationID"));

		Map<Long, String> map = new HashMap<Long, String>();

		namedParameterJdbcTemplate.query(GET_AUTO_CHECK_OUT_USER, namedParameters, new RowMapper<Map<Long, String>>() {

			@Override
			public Map<Long, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rs.getString("GCM_ID") != null)
					map.put(Long.parseUnsignedLong(rs.getString("ID")), rs.getString("GCM_ID"));
				else
					map.put(Long.parseUnsignedLong(rs.getString("ID")), "");
				return map;
			}
		});

		return map;
	}

	/**
	 * Get start and end time for all companies
	 * 
	 * @return List
	 */
	@Override
	public List<Company> getCompanyStartEndTime() {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("active", true);

		List<Company> companyList = new ArrayList<Company>();

		companyList = namedParameterJdbcTemplate.query(GET_COMPANY_START_END_TIME, namedParameters,
				new RowMapper<Company>() {
					@Override
					public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
						Company company = new Company();
						company.setCompanyID(Long.parseUnsignedLong(rs.getString("Company_ID")));
						company.setWorkingEndTime(rs.getString("Work_EndTime"));
						company.setWorkingStartTime(rs.getString("Work_StartTime"));

						return company;
					}
				});

		return companyList;
	}

	/**
	 * Get users tracking data for date
	 * 
	 * @param date
	 */
	@Override
	public Map<Long, List<TrackingBean>> getTrackingData(String date) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("date", date);

		Map<Long, List<TrackingBean>> map = new HashMap<Long, List<TrackingBean>>();

		namedParameterJdbcTemplate.query(GET_TRACKING_DATA_SQL, namedParameters,
				new RowMapper<Map<Long, List<TrackingBean>>>() {
					@Override
					public Map<Long, List<TrackingBean>> mapRow(final ResultSet rs, final int rowNum)
							throws SQLException {

						TrackingBean tracking = new TrackingBean();
						List<TrackingBean> list = new ArrayList<TrackingBean>();
						tracking.setUserID(Long.parseUnsignedLong(rs.getString("User_ID")));
						tracking.setCompanyID(Long.parseUnsignedLong(rs.getString("Company_ID")));
						tracking.setLatitude(rs.getString("Latitude"));
						tracking.setLongitude(rs.getString("Longitude"));
						tracking.setDateTime(rs.getTimestamp("DateTime"));

						if (map.containsKey(tracking.getUserID())) {
							list = map.get(tracking.getUserID());
						}
						list.add(tracking);
						map.put(tracking.getUserID(), list);
						return map;
					}
				});

		return map;
	}

	/**
	 * Get users tracking data for date
	 * 
	 * @param date
	 */
	@Override
	public Map<Long, AddressBean> getCompanyPermanentLocation() {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("type", 1);
		namedParameters.addValue("active", true);
		Map<Long, AddressBean> map = new HashMap<Long, AddressBean>();

		namedParameterJdbcTemplate.query(GET_COMPANY_PERMANENT_ADD_SQL, namedParameters,
				new RowMapper<Map<Long, AddressBean>>() {
					@Override
					public Map<Long, AddressBean> mapRow(final ResultSet rs, final int rowNum) throws SQLException {

						AddressBean add = new AddressBean();
						add.setAddressID(Long.parseUnsignedLong(rs.getString("Address_Master_ID")));
						add.setLatitude(rs.getString("Lattitude"));
						add.setLongitude(rs.getString("Longitude"));
						map.put(Long.parseUnsignedLong(rs.getString("Company_ID")), add);
						return map;
					}
				});
		return map;
	}

	@Override
	public int addCheckInAttendance(Map<TrackingBean, AddressBean> map) {

		MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[map.size()];
		AddressBean add;
		int i = 0;

		for (TrackingBean tracking : map.keySet()) {
			add = map.get(tracking);
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("userID", tracking.getUserID());
			namedParameters.addValue("companyID", tracking.getCompanyID());
			namedParameters.addValue("checkInTime", tracking.getDateTime());
			namedParameters.addValue("checkInAdd", add.getAddressID());
			namedParameters.addValue("present", true);
			namedParameters.addValue("reason", "Scheduler CheckIn");
			namedParameters.addValue("modifiedBy", 0);
			namedParameters.addValue("date", tracking.getDateTime());
			namedParametersArray[i] = namedParameters;
			i++;
		}
		int[] count = namedParameterJdbcTemplate.batchUpdate(ADD_AUTO_CHECKIN_ATTENDANCE_SQL, namedParametersArray);
		return count.length;
	}

	@Override
	public int updateCheckOutAttendance(Map<TrackingBean, AddressBean> map, Date date) {

		MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[map.size()];
		AddressBean add;
		int i = 0;

		for (TrackingBean tracking : map.keySet()) {
			add = map.get(tracking);
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("userID", tracking.getUserID());
			namedParameters.addValue("companyID", tracking.getCompanyID());
			namedParameters.addValue("checkOutTime", tracking.getDateTime());
			namedParameters.addValue("checkOutAddress", add.getAddressID());
			namedParameters.addValue("present", true);
			namedParameters.addValue("date", date);
			namedParameters.addValue("updatePresent", false);
			namedParametersArray[i] = namedParameters;
			i++;
		}
		int[] count = namedParameterJdbcTemplate.batchUpdate(UPDATE_AUTO_CHECKOUT_SQL, namedParametersArray);
		return count.length;
	}

	/**
	 * Get get list of user who are on leave for date
	 * 
	 * @param date
	 */
	@Override
	public List<Long> getUserOnLeave(Date date) {

		List<Long> userIdList = new ArrayList<Long>();
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("date", date);
		namedParameters.addValue("status", "Approved");
		namedParameters.addValue("active", true);

		userIdList = namedParameterJdbcTemplate.query(GET_USER_ON_LEAVE_SQL, namedParameters, new RowMapper<Long>() {
			@Override
			public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {

				return Long.parseUnsignedLong(rs.getString("User_ID"));
			}
		});
		return userIdList;
	}
	
	public Attendance getLastCheckOutDetails(long userID, long companyID, Date date){
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		Attendance attendance = new Attendance();
		try {
			namedParameters.addValue("userID", userID);
			namedParameters.addValue("companyID", companyID);
			namedParameters.addValue("date", date);
			namedParameters.addValue("active", true);
			
			namedParameterJdbcTemplate.query(GET_USER_LAST_CHECKIN_SQL,
			        namedParameters, new ResultSetExtractor<Attendance>() {
			          @Override
			          public Attendance extractData(ResultSet rs) throws SQLException, DataAccessException {
			            if (rs.next()){
						
						attendance.setID(rs.getInt("ID"));
						attendance.setCheckOutTime(rs.getTimestamp("CheckOut_Time"));
						attendance.setTimeOut(rs.getTimestamp("TimeOut"));
						attendance.setUpdatedTimeOut(rs.getTimestamp("Updated_TimeOut"));
						attendance.setCheckOutAddressID(rs.getInt("CheckOut_Address_ID"));
						
						attendance.setCheckOutAttendanceType(rs.getInt("Attendance_CheckOut_Type"));
						
						if (attendance.getUpdatedTimeOut() == null) {
							attendance.setCheckOutApproved(true);
						} else {
							attendance.setCheckOutApproved(false);
						}

			          }
						return attendance;
			          }
			        });
			 
			if (attendance != null && attendance.getID() > 0)
				return attendance;
			else
				return new Attendance();

		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error getting user last attendance " + e);
			return null;
		}
		
	}

	@Override
	public Attendance getUserAttendance(long userID, long companyID, Date date) 
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		Attendance attendance = new Attendance();
		try {
			namedParameters.addValue("userID", userID);
			namedParameters.addValue("companyID", companyID);
			namedParameters.addValue("date", date);
			namedParameters.addValue("active", true);
			LOG.info("userID : " + userID);
			LOG.info("companyID : " + companyID);
			LOG.info("date : " + date);
			LOG.info("true : " + true);

			namedParameterJdbcTemplate.query(GET_USER_FIRST_CHECKIN_SQL,
			        namedParameters, new ResultSetExtractor<Attendance>() {
			          @Override
			          public Attendance extractData(ResultSet rs) throws SQLException, DataAccessException {
			            if (rs.next()){
			            	LOG.info("inside if");
			            	
							attendance.setAttendanceDate(rs.getDate("Attendance_Date"));
							attendance.setCheckInTime(rs.getTimestamp("CheckIn_Time"));
							attendance.setUpdatedTimeIn(rs.getTimestamp("Updated_TimeIn"));
							attendance.setTimeIn(rs.getTimestamp("TimeIn"));
							attendance.setCheckInAttendanceType(rs.getInt("Attendance_CheckIn_Type"));
							
							Attendance lastCheckOutAttendance = getLastCheckOutDetails(userID, companyID, date);
							if(lastCheckOutAttendance != null && lastCheckOutAttendance.getID() > 0)
							{
								attendance.setCheckOutTime(lastCheckOutAttendance.getCheckOutTime());
								attendance.setTimeOut(lastCheckOutAttendance.getTimeOut());
								attendance.setUpdatedTimeOut(lastCheckOutAttendance.getUpdatedTimeOut());
								attendance.setCheckOutAttendanceType(lastCheckOutAttendance.getCheckOutAttendanceType());
							}
							attendance.setCompanyID(Long.parseUnsignedLong(rs.getString("Company_ID")));
							attendance.setID(Long.parseUnsignedLong(rs.getString("ID")));
							attendance.setPresent(rs.getBoolean("Present"));
							
							if (attendance.getUpdatedTimeIn() == null) {
								attendance.setCheckInApproved(true);
							} else {
								attendance.setCheckInApproved(false);
							}

							if (attendance.getUpdatedTimeOut() == null) {
								attendance.setCheckOutApproved(true);
							} else {
								attendance.setCheckOutApproved(false);
							}
			            }
							return attendance;
						}
					});
			if (attendance != null && attendance.getID() > 0)
				return attendance;
			else
				return new Attendance();
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error getting user last attendance " + e);
			return null;
		}
	}
	
	/**
	 * Check if leave already applied for these dates
	 * 
	 * @param userID
	 * @param companyID
	 * @param dateList
	 * @param status
	 * @return int
	 */
	@Override
	public int isLeaveExisting(long userID, long companyID, List<Date> dateList, List<String> status, String leaveDay) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("date", dateList);
		namedParameters.addValue("status", status);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("leaveDay", leaveDay);

		int retVal = namedParameterJdbcTemplate.queryForObject(CHECK_LEAVE_APPLIED, namedParameters, Integer.class);
		return retVal;
	}


}
