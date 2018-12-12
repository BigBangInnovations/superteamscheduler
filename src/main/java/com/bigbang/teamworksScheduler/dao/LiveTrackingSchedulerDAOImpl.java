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
import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.TrackingBean;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.beans.UserLiveTrackingStatus;
import com.bigbang.teamworksScheduler.beans.Users;
import com.bigbang.teamworksScheduler.service.Properties;

public class LiveTrackingSchedulerDAOImpl implements LiveTrackingSchedulerDAO {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	Logger LOG = LogManager.getLogger(LiveTrackingSchedulerDAOImpl.class);

	
	private static String GET_LIVE_TRACKING_STATUS_ON_USERS = "SELECT * FROM "
			+  " User_Live_Tracking_Status WHERE "
			+ " Company_ID IN (:companyList) AND Is_Live_Tracking = :Is_Live_Tracking ";

	private static String GET_USER_GCM_SQL = "Select u.ID, g.GCM_ID from " + "" + MvcConfiguration.masterSchema
			+ ".USERS u, " + MvcConfiguration.masterSchema + ".USER_APPLICATION_GCM g "
			+ "where u.ID IN (:userID) and u.ID = g.User_ID and g.Application_ID = :applicationID;";

	private static final String GET_USER_DETAILS_SQL = "Select s1.First_Name, s1.Last_Name, s1.Email_ID,s1.Mobile_No1, "
			+ "s1.Picture, s1.User_ID, s1.Role_ID, s2.GCM_ID, s1.Company_ID, s1.Employee_Code from "
			+ "(Select First_Name, Last_Name, Picture, User_ID, Role_ID, Company_ID, Email_ID,Mobile_No1,Employee_Code  from "
			+ MvcConfiguration.masterSchema + ".USERS u , " + MvcConfiguration.masterSchema
			+ ".USER_ROLE r where u.ID IN (:userID) and u.ID = r.User_ID and "
			+ "r.Role_ID IN (1,2,3,4) and r.Active = 1) s1 LEFT JOIN " + "(Select GCM_ID, User_ID from "
			+ MvcConfiguration.masterSchema + ".USER_APPLICATION_GCM where User_ID IN (:userID)) "
			+ "s2 ON s1.User_ID = s2.User_ID;";
	
	private static final String UPDATE_USER_LIVE_STATUS = "UPDATE User_Live_Tracking_Status SET "
			+ " Is_Live_Tracking = :isLiveTracking, Last_Modified = :LastModified "
			+ " WHERE ID = :id ";
	
	@Override
	public List<UserLiveTrackingStatus> getLiveTrackingOnUsers(List<Long> companyIdList) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("companyList", companyIdList);
		namedParameters.addValue("Is_Live_Tracking", 1);
		
		List<UserLiveTrackingStatus> UserLiveTrackingList = new ArrayList<UserLiveTrackingStatus>();
		try {
			UserLiveTrackingList = namedParameterJdbcTemplate.query(GET_LIVE_TRACKING_STATUS_ON_USERS, namedParameters,
					new RowMapper<UserLiveTrackingStatus>() {
						@Override
						public UserLiveTrackingStatus mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							UserLiveTrackingStatus liveTracking = new UserLiveTrackingStatus();
							
							liveTracking.setCode(rs.getString("Code"));
							liveTracking.setCompanyID(rs.getInt("Company_ID"));
							liveTracking.setCreatedBy(rs.getInt("Created_By"));
							liveTracking.setCreatedTime(rs.getTimestamp("Created_Time"));
							liveTracking.setID(rs.getInt("ID"));
							liveTracking.setIsLiveTracking(rs.getInt("Is_Live_Tracking"));
							liveTracking.setLastModified(rs.getTimestamp("Last_Modified"));
							liveTracking.setLastModifiedBy(rs.getInt("Last_Modified_By"));
							liveTracking.setUserID(rs.getInt("User_ID"));
							return liveTracking;

						}
					});

			return UserLiveTrackingList;
		} catch (DataAccessException e) {
			LOG.error("Error getting list active companies", e);
			return null;
		}
	}
	
	@Override
	public Users getUserDetails(final long userID) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("applicationId", 1);
		namedParameters.addValue("active", true);

		Users users = null ;
		try
		{
		 users = namedParameterJdbcTemplate.query(GET_USER_DETAILS_SQL, namedParameters,
	        new ResultSetExtractor<Users>() {
	          @Override
	          public Users extractData(ResultSet rs) throws SQLException, DataAccessException {
	        	  if(rs.next())
	        	  {
	            	Users aUser = new Users();

				aUser.setUserId(Long.parseUnsignedLong(rs.getString("User_ID")));
				aUser.setFirstName(rs.getString("First_Name"));
				aUser.setLastName(rs.getString("Last_Name"));
				aUser.setPicture(rs.getString("Picture"));
				if (aUser.getPicture() == null) {
					aUser.setPicture("");
				}
				if (rs.getString("GCM_ID") != null)
					aUser.setDeviceid(rs.getString("GCM_ID"));
				else
					aUser.setDeviceid("");
				
				if(rs.getString("Employee_Code") != null && !rs.getString("Employee_Code").equals(""))
					aUser.setEmployeeCode(rs.getString("Employee_Code"));
				else
					aUser.setEmployeeCode("");
				
//				System.out.println(aUser.getDeviceid());
				aUser.setMobileNo1(rs.getString("Mobile_No1"));
				aUser.setCompanyId(Long.parseUnsignedLong(rs.getString("Company_ID")));
				aUser.setEmailID(rs.getString("Email_ID"));
				return aUser;
	        	  }
	        	  else
	        	  {
	        		  return null;
	        	  }
	          }
	        });
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    return users;
		
	}

	@Override
	public int UpdateUserStatus(long id) {
		
		Date currentDate = new Date();
		int retVal = -1;
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isLiveTracking", 0);
		namedParameters.addValue("LastModified", currentDate);
		namedParameters.addValue("id", id);

		try {
			retVal = namedParameterJdbcTemplate.update(UPDATE_USER_LIVE_STATUS, namedParameters);

			return retVal;
		} catch (Exception e) {
			LOG.error("Error updating user live status e: ", e);
			return retVal;
		}
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

		namedParameterJdbcTemplate.query(GET_USER_GCM_SQL, namedParameters, new RowMapper<Map<Long, String>>() {

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

}
