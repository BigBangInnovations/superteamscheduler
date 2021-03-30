package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import notification.SendNotifications;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.beans.UserAttendance;
import com.bigbang.teamworksScheduler.service.AdvanceSchedularForAttendanceServiceImpl;
import com.bigbang.teamworksScheduler.service.Properties;

public class UserAttendanceDAOImpl implements UserAttendanceDAO
{

	Logger LOG = LogManager.getLogger(UserAttendanceDAOImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public static String INSERT_QUERY_INTO_USER_ATTENDANCE = "INSERT INTO USER_ATTENDANCE_TABLE (userID,companyID,Date,inTime,outTime,"
			+ "onLeave,isHoliday,isSaturday,isSunday,hourWorked,status,shorthours,lateComing,earlyGoing,isAbsent) "
			+ "VALUES (:pUserID, :pCompanyID, :pDate, :pInTime, :pOutTime, :pOnLeave, :pIsHoliday, :pIsSaturday, :pIsSunday, :pHourWorked, :pStatus, :pShorthours, :pLateComing, :pEarlyGoing ,:isAbsent)";

//	public static String INSERT_QUERY_INTO_USER_ATTENDANCE_DUMMY = "INSERT INTO USER_ATTENDANCE_TABLE_DUMMY (userID,companyID,Date,inTime,outTime,"
//			+ "onLeave,isHoliday,isSaturday,isSunday,hourWorked,status,shorthours,lateComing,earlyGoing,isAbsent) "
//			+ "VALUES (:pUserID, :pCompanyID, :pDate, :pInTime, :pOutTime, :pOnLeave, :pIsHoliday, :pIsSaturday, :pIsSunday, :pHourWorked, :pStatus, :pShorthours, :pLateComing, :pEarlyGoing ,:isAbsent)";

	public static String SELECT_DATA_FROM_USER_ATTENDANCE = "SELECT * FROM USER_ATTENDANCE_TABLE "
			+ "WHERE CAST(Date AS DATE) >= :startDate AND CAST(Date AS DATE) <= :endDate "
			+ "AND companyID = :companyID AND userID = :userID;";
	
	public static String UPDATE_USER_ATTENDANCE_DATA_WEEKLY_REVERSE = "UPDATE USER_ATTENDANCE_TABLE SET status = :status "
			+ "WHERE companyID = :pCompanyID AND userID = :pUserID AND ID = :pID;";

	public static String UPDATE_USER_ATTENDANCE_DATA_MONTHLY_REVERSE = "UPDATE USER_ATTENDANCE_TABLE SET status = :status "
			+ "WHERE companyID = :pCompanyID AND userID = :pUserID AND ID = :pID;";

	private static final String ADD_NOTIFICATION = "Insert into "
			+ MvcConfiguration.masterSchema
			+ ".NOTIFICATIONS "
			+ "( User_ID, Message, Application_ID, Type, Is_Notified, Data, Transaction_ID, Status, Company_ID) values "
			+ "(:userID, :message, :applicationID , :type, :isNotified, :data, :transactionID, :status, :companyID);";

	public static String CHECK_DATA_EXIST_IN_USER_ATTENDANCE = "SELECT * FROM USER_ATTENDANCE_TABLE "
			+ "WHERE CAST(Date AS DATE) = :date AND companyID = :companyID AND userID = :userID;";
	
	public static String UPDATE_INTO_USER_ATTENDANCE_RECORD = "UPDATE USER_ATTENDANCE_TABLE SET inTime = :pInTime ,outTime = :pOutTime ,"
			+ "onLeave = :pOnLeave ,isHoliday = :pIsHoliday , isSaturday = :pIsSaturday , isSunday = :pIsSunday , hourWorked = :pHourWorked , "
			+ "status = :pStatus , shorthours = :pShorthours, lateComing = :pLateComing , earlyGoing = :pEarlyGoing , isAbsent = :isAbsent "
			+ "WHERE ID = :pID;";

	@Override
	public int insertUserAttendanceIntoTable(long userID, long companyID, Date date, 
			Date inTime, Date outTime, boolean onLeave, boolean isHoliday, boolean isSaturday,
			boolean isSunday, String hourWorked, String status, long shorthours, boolean lateComing, boolean earlyGoing , boolean isAbsent)
	{
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("pUserID",userID);
		namedParameters.addValue("pCompanyID",companyID);
		namedParameters.addValue("pDate",date);
		namedParameters.addValue("pInTime",inTime);
		namedParameters.addValue("pOutTime",outTime);
		namedParameters.addValue("pOnLeave",onLeave);
		namedParameters.addValue("pIsHoliday",isHoliday);
		namedParameters.addValue("pIsSaturday",isSaturday);
		namedParameters.addValue("pIsSunday",isSunday);
		namedParameters.addValue("pHourWorked",hourWorked);
		namedParameters.addValue("pStatus",status);
		namedParameters.addValue("pShorthours",shorthours);
		namedParameters.addValue("pLateComing",lateComing);
		namedParameters.addValue("pEarlyGoing",	earlyGoing);
		namedParameters.addValue("isAbsent",isAbsent);
		
		namedParameterJdbcTemplate.update(INSERT_QUERY_INTO_USER_ATTENDANCE, namedParameters ,keyHolder);
		//namedParameterJdbcTemplate.update(INSERT_QUERY_INTO_USER_ATTENDANCE_DUMMY, namedParameters ,keyHolder);
		return keyHolder.getKey().intValue();
	}

	  @Override
	  public List<UserAttendance> getUserAttendanceData(Date startDate,Date endDate,long companyID,long userID) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("startDate", startDate);
	    namedParameters.addValue("endDate", endDate);
	    namedParameters.addValue("companyID", companyID);
	    namedParameters.addValue("userID", userID);
	    return namedParameterJdbcTemplate.query(SELECT_DATA_FROM_USER_ATTENDANCE, namedParameters, new ResultSetExtractor<List<UserAttendance>>() {
	      @Override
	      public List<UserAttendance> extractData(ResultSet rs) throws SQLException, DataAccessException {
	       List<UserAttendance> userAttendanceList = new ArrayList();
	        while (rs.next()) {
	          UserAttendance userAttendance = new UserAttendance();
	          userAttendance.setID(rs.getInt("ID"));
	          userAttendance.setUserID(rs.getLong("userID"));
	          userAttendance.setCompanyID(rs.getLong("companyID"));
	          userAttendance.setDate(rs.getDate("Date"));
	          userAttendance.setInTime(rs.getTimestamp("inTime"));
	          userAttendance.setOutTime(rs.getTimestamp("outTime"));
	          userAttendance.setOnLeave(rs.getBoolean("onLeave"));
	          userAttendance.setHoliday(rs.getBoolean("isHoliday"));
	          userAttendance.setSaturday(rs.getBoolean("isSaturday"));
	          userAttendance.setSunday(rs.getBoolean("isSunday"));
	          userAttendance.setHourWorked(rs.getString("hourWorked"));
	          userAttendance.setStatus(rs.getString("status"));
	          userAttendance.setShorthours(rs.getInt("shorthours"));
	          userAttendance.setLateComing(rs.getBoolean("lateComing"));
	          userAttendance.setEarlyGoing(rs.getBoolean("earlyGoing"));
	          userAttendance.setAbsent(rs.getBoolean("isAbsent"));
	          userAttendanceList.add(userAttendance);
	        }
	        return userAttendanceList;
	      }
	    });
	  }
	  
		/**
		 * user Attendance HalfDay Reverse
		 * 
		 * @param companyid
		 * @param modifiedBy
		 * @param holidays
		 */
	  	@Override
		public int[] userAttendanceHalfDayReverse(long companyid, long modifiedBy, List<UserAttendance> userAttendanceList) 
		{
			MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[userAttendanceList.size()];
			int i = 0;

			for (UserAttendance userAttendance : userAttendanceList) {
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("status", userAttendance.getStatus());
				namedParameters.addValue("pCompanyID", userAttendance.getCompanyID());
				namedParameters.addValue("pUserID", userAttendance.getUserID());
				namedParameters.addValue("pID", userAttendance.getID());
				namedParametersArray[i] = namedParameters;
				i++;
			}
			return namedParameterJdbcTemplate.batchUpdate(UPDATE_USER_ATTENDANCE_DATA_WEEKLY_REVERSE, namedParametersArray);
		}

		/**
		 * Add notification to database
		 */
		@Override
		public int addNotifications(SendNotifications notifications) 
		{
			int retVal = 0;
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource namedParameter = new MapSqlParameterSource();
			try
			{
			namedParameter.addValue("userID", notifications.getUserID());
			namedParameter.addValue("companyID", notifications.getCompanyID());
			namedParameter.addValue("message", notifications.getMessage());
			namedParameter.addValue("applicationID", Properties.get("applicationID"));
			namedParameter.addValue("type", notifications.getType());
			namedParameter.addValue("isNotified", notifications.isNotified());
			namedParameter.addValue("data", notifications.getData());
			namedParameter.addValue("transactionID", notifications.getTransactionID());
			namedParameter.addValue("status", notifications.getStatus());
			
			retVal = namedParameterJdbcTemplate.update(ADD_NOTIFICATION, namedParameter ,keyHolder);
			if (retVal > 0) {
				LOG.debug("Notification added successfully: " + keyHolder.getKey().intValue());
			} else {
				LOG.error("Error saving notification" + keyHolder.getKey().intValue());
			}
			return retVal;
			}
			catch (DataAccessException e) 
			{
			e.printStackTrace();
			LOG.error("Error adding notification " + e);
			return retVal;
			}
		}

		@Override
		public Integer checkRecordExistInUserAttendanceTable(long userID,long companyID,Date date) 
		{
			Integer i = 0;
		    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		    namedParameters.addValue("date", date);
		    namedParameters.addValue("companyID", companyID);
		    namedParameters.addValue("userID", userID);
		    return namedParameterJdbcTemplate.query(CHECK_DATA_EXIST_IN_USER_ATTENDANCE, namedParameters, new ResultSetExtractor<Integer>() 
		    {
		      @Override
		      public Integer extractData(ResultSet rs) throws SQLException, DataAccessException 
		      {
		        if (rs.next())
		        {
		        	return rs.getInt("ID");
		        }
				return i;
		      }
		    });
		}
		
		@Override
		public void updateUserAttendanceRecordIntoTable(UserAttendance userAttendance)
		{
			//GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("pInTime",userAttendance.getInTime());
			namedParameters.addValue("pOutTime",userAttendance.getOutTime());
			namedParameters.addValue("pOnLeave",userAttendance.isOnLeave());
			namedParameters.addValue("pIsHoliday",userAttendance.isHoliday());
			namedParameters.addValue("pIsSaturday",userAttendance.isSaturday());
			namedParameters.addValue("pIsSunday",userAttendance.isSunday());
			namedParameters.addValue("pHourWorked",userAttendance.getHourWorked());
			namedParameters.addValue("pStatus",userAttendance.getStatus());
			namedParameters.addValue("pShorthours",userAttendance.getShorthours());
			namedParameters.addValue("pLateComing",userAttendance.isLateComing());
			namedParameters.addValue("pEarlyGoing",	userAttendance.isEarlyGoing());
			namedParameters.addValue("isAbsent",userAttendance.isAbsent());
			namedParameters.addValue("pID",userAttendance.getID());
			namedParameterJdbcTemplate.update(UPDATE_INTO_USER_ATTENDANCE_RECORD, namedParameters);
			//return keyHolder.getKey().intValue();
		}

		/**
		 * User Attendance Full Day Reverse
		 * 
		 * @param companyid
		 * @param modifiedBy
		 * @param holidays
		 */
	  	@Override
		public int[] userAttendanceFullDayReverse(long companyid, long modifiedBy, List<UserAttendance> userAttendanceList) 
		{
			MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[userAttendanceList.size()];
			int i = 0;

			for (UserAttendance userAttendance : userAttendanceList) {
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				namedParameters.addValue("status", userAttendance.getStatus());
				namedParameters.addValue("pCompanyID", userAttendance.getCompanyID());
				namedParameters.addValue("pUserID", userAttendance.getUserID());
				namedParameters.addValue("pID", userAttendance.getID());
				namedParametersArray[i] = namedParameters;
				i++;
			}
			return namedParameterJdbcTemplate.batchUpdate(UPDATE_USER_ATTENDANCE_DATA_MONTHLY_REVERSE, namedParametersArray);
		}

}

