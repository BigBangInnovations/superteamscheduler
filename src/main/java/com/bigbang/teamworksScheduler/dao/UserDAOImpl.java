package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.CompanyAttendance;
import com.bigbang.teamworksScheduler.beans.Holidays;
import com.bigbang.teamworksScheduler.beans.Users;

public class UserDAOImpl implements UserDAO {

	private static final Logger LOG = LogManager.getLogger(UserDAOImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private DataSource dataSource;

	private static final String GET_USER_DETAILS_SQL = "Select s1.First_Name, s1.Last_Name, s1.Email_ID,s1.Mobile_No1, "
			+ "s1.Picture, s1.User_ID, s1.Role_ID, s2.GCM_ID, s1.Company_ID, s1.Employee_Code from "
			+ "(Select First_Name, Last_Name, Picture, User_ID, Role_ID, Company_ID, Email_ID,Mobile_No1,Employee_Code  from "
			+ MvcConfiguration.masterSchema + ".USERS u , " + MvcConfiguration.masterSchema
			+ ".USER_ROLE r where u.ID IN (:userID) and u.ID = r.User_ID and "
			+ "r.Role_ID IN (1,2,3,4) and r.Active = :active) s1 LEFT JOIN " + "(Select GCM_ID, User_ID from "
			+ MvcConfiguration.masterSchema + ".USER_APPLICATION_GCM where User_ID IN (:userID) AND Application_ID = '1') "
			+ "s2 ON s1.User_ID = s2.User_ID;";

	private static final String GET_FROM_ROLE_USER_LIST = "Select Distinct(r.User_ID) from " 
	        + MvcConfiguration.masterSchema
			+ ".USER_ROLE r "
			+ "where r.User_ID IN (:userID) and r.Company_ID = :companyID and r.Role_ID IN (3,4);";
	
	private static final String GET_USER_ROLE_LIST = "Select Distinct(r.User_ID) from " + MvcConfiguration.masterSchema
			+ ".USER_ROLE r "
			+ "where r.User_ID IN (:userID) and r.Company_ID = :companyID and r.Role_ID IN (1,2,3);";

	private static final String GET_USER_DETAILS_SQL_NEW = "Select u.First_Name, u.Last_Name, u.Mobile_No1,m.Manager_ID,m.User_ID from "
		      + MvcConfiguration.masterSchema+".USERS u , " + MvcConfiguration.masterSchema
		      + ".MANAGER m where m.User_ID IN (:userID) "
		      + " and m.Application_ID = :applicationId and u.ID = m.Manager_ID and m.Active = :active";
	
	private static final String GET_COMPANY_SUPERADMIN = "SELECT User_ID FROM "+ MvcConfiguration.masterSchema
			  + ".USER_ROLE WHERE Company_ID = :companyID AND Active = :active AND Role_ID = :role ";
	
	private static final String GET_COMPANY_SUPERADMIN_ADMIN = "SELECT User_ID FROM "+ MvcConfiguration.masterSchema
			  + ".USER_ROLE WHERE Company_ID = :companyID AND Active = :active AND Role_ID IN (1,2) ";
	
	private static final String GET_USER_DETAILS = "SELECT * FROM "+MvcConfiguration.masterSchema
			  + ".USERS WHERE ID = :ID";

	private static final String GET_USER_SHIFTID = "SELECT companyShiftDetailID FROM "+MvcConfiguration.masterSchema
			  + ".USERS WHERE ID = :ID";

	static final String GET_COMPANY_SHIFT_DETAIL_BY_COMPANYID_AND_SHIFTID = "SELECT * from COMPANY_SHIFT_DETAILS where companyID = :pCompanyID AND ID = :pShiftID ";

	static final String GET_COMPANY_DEFAULT_SHIFT_DETAIL_BY_COMPANYID = "SELECT * from COMPANY_SHIFT_DETAILS where companyID = :pCompanyID AND isDefaultShift = :isDefaultShift limit 0,1 ";

	static final String GET_COMPANY_USER_BRANCHID = "SELECT Branch_ID,User_ID FROM "+MvcConfiguration.masterSchema+".USERS u, "+MvcConfiguration.masterSchema+".USER_ROLE ur "
			+ "WHERE u.ID = ur.User_ID AND ur.Active = 1 AND ur.Company_ID IN (:companyIDs);";

	static final String GET_DEVICEID = "SELECT User_ID, GCM_ID FROM " + MvcConfiguration.masterSchema
			+ ".USER_APPLICATION_GCM WHERE User_ID IN (:userid) and Application_ID = :applicationid";

	@Override
	public List<Long> removeAdminUserList(List<Long> userList, final long companyId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		LOG.info("userIDList : " + userList.size());
		LOG.info("companyID : " + companyId);
		namedParameters.addValue("userID", userList);
		namedParameters.addValue("companyID", companyId);
//		namedParameters.addValue("roleID", Application.roleIDs.get(Constants.ADMIN));

//		System.out.println("userID "+userList);
//		System.out.println("companyID "+companyId);
//		System.out.println("Query "+GET_FROM_ROLE_USER_LIST);
		try {
			List<Long> userIDList = namedParameterJdbcTemplate.query(GET_FROM_ROLE_USER_LIST, namedParameters,
					new RowMapper<Long>() {

						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("User_ID"));
						}

					});
			if (userIDList == null)
				return new ArrayList<Long>();
			else
				return userIDList;
		} catch (DataAccessException e) {
			LOG.error("Error getting user ID discarding Admin role", e);
			return null;
		}
	}

	/**
	 * . Call function to get lover hierarchy user id
	 * 
	 * @param userid
	 * @return String
	 */
	@Override
	public String getLowerHierarchy(final long userid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String memberIDs = "";
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(dataSource).withFunctionName("getLowerLevelHierarchy13TeamPro");
			namedParameters.addValue("userid", String.valueOf(userid));
			memberIDs = String.valueOf(call.executeFunction(Integer.class, namedParameters)).trim();
		} catch (DataAccessException e) {
			LOG.error("Error executing function to read manager lower hierarchy");
			return null;
		}
		return memberIDs;
	}

	@Override
	public List<Users> getUser(final List<Long> id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		List<Users> listUser = new ArrayList<Users>();
		namedParameters.addValue("userID", id);
		namedParameters.addValue("applicationId", 1);
//		namedParameters.addValue("role", Application.roles.keySet());
		namedParameters.addValue("active", true);
		try {
			listUser = namedParameterJdbcTemplate.query(GET_USER_DETAILS_SQL, namedParameters, new RowMapper<Users>() {

				@Override
				public Users mapRow(final ResultSet rs, final int rowNum) throws SQLException {
					Users aUser = new Users();

					aUser.setUserId(Long.parseUnsignedLong(rs.getString("User_ID")));
					aUser.setDeviceid(rs.getString("GCM_ID"));
					if (aUser.getDeviceid() == null) {
						aUser.setDeviceid("");
					}
					aUser.setFirstName(rs.getString("First_Name"));
					aUser.setLastName(rs.getString("Last_Name"));
					if (aUser.getLastName() == null) {
						aUser.setLastName("");
					}
					if (aUser.getPicture() == null) {
						aUser.setPicture("");
					}
					if(rs.getString("Employee_Code") != null && !rs.getString("Employee_Code").equals(""))
						aUser.setEmployeeCode(rs.getString("Employee_Code"));
					else
						aUser.setEmployeeCode("");
					
					aUser.setMobileNo1(rs.getString("Mobile_No1"));
					aUser.setEmailID(rs.getString("Email_ID"));
					aUser.setRoleID(rs.getInt("Role_ID"));
					return aUser;
				}
			});
			return listUser;
		} catch (DataAccessException e) {
			LOG.error("Error fetching users " + e);
			return null;
		}
	}

	@Override
	public List<Users> getManager(List<Long> id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		List<Users> listUser = new ArrayList<Users>();
		namedParameters.addValue("userID", id);
		namedParameters.addValue("applicationId", 1);
//		namedParameters.addValue("role", Application.roles.keySet());
		namedParameters.addValue("active", true);
		
		try {
			listUser = namedParameterJdbcTemplate.query(GET_USER_DETAILS_SQL_NEW, namedParameters, new RowMapper<Users>() {

				@Override
				public Users mapRow(final ResultSet rs, final int rowNum) throws SQLException {
					Users aUser = new Users();

					aUser.setUserId(Long.parseUnsignedLong(rs.getString("User_ID")));
					aUser.setManagerName(rs.getString("First_Name")+" "+rs.getString("Last_Name") );
					aUser.setManagerContact(rs.getString("Mobile_No1"));
					return aUser;
				}
			});
			return listUser;
		} catch (DataAccessException e) {
			LOG.error("Error fetching users " + e);
			return null;
		}
	}

	@Override
	public Long getCompanySuperAdmin(long companyID) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("active", 1);
		namedParameters.addValue("role",1);

		long superAdminId = 0;
		try {
			
			superAdminId = namedParameterJdbcTemplate.queryForObject(GET_COMPANY_SUPERADMIN, namedParameters, Long.class);
		} catch (DataAccessException e) {
			LOG.debug("Exception in get Company super admin : " + e);
		}
		return superAdminId;
	}

	@Override
	public List<Long> removeTeamMemberList(List<Long> userList, long companyId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userID", userList);
		namedParameters.addValue("companyID", companyId);

		try {
			List<Long> userIDList = namedParameterJdbcTemplate.query(GET_USER_ROLE_LIST, namedParameters,
					new RowMapper<Long>() {

						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("User_ID"));
						}

					});
			if (userIDList == null)
				return new ArrayList<Long>();
			else
				return userIDList;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error getting user ID discarding Admin role", e);
			return null;
		}
	}

	@Override
	public Users getUserDetails(long userID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("ID", userID);
		Users aUser = new Users();
		try {
			 namedParameterJdbcTemplate.query(GET_USER_DETAILS, namedParameters,
					new RowMapper<Users>() {

						@Override
						public Users mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							aUser.setUserId(Long.parseUnsignedLong(rs.getString("ID")));
							aUser.setManagerName(rs.getString("First_Name")+" "+rs.getString("Last_Name") );
							aUser.setManagerContact(rs.getString("Mobile_No1"));
							if(rs.getString("Email_ID") != null){
								aUser.setEmailID(rs.getString("Email_ID"));	
							}else{
								aUser.setEmailID("");
							}
							
							return aUser;
							
						}

					});
			 return aUser;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error getting user ID discarding Admin role", e);
			return null;
		}
	}

	@Override
	public List<Long> getAdminSuperAdmin(long companyID) {
		// TODO Auto-generated method stub GET_COMPANY_SUPERADMIN_ADMIN
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("active", 1);

		try {
			List<Long> userIDList = namedParameterJdbcTemplate.query(GET_COMPANY_SUPERADMIN_ADMIN, namedParameters,
					new RowMapper<Long>() {

						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("User_ID"));
						}

					});
			if (userIDList == null)
				return new ArrayList<Long>();
			else
				return userIDList;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error getting user ID discarding Admin role", e);
			return null;
		}
	}
	
	@Override
	public long getShiftIDOfUser(long userID)
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("ID", userID);

		long shiftID = 0;
		try {
			
			shiftID = namedParameterJdbcTemplate.queryForObject(GET_USER_SHIFTID, namedParameters, Long.class);
		} catch (DataAccessException e) {
			LOG.debug("Exception in get Shift ID of User : " + e);
		}
		return shiftID;
	}
		
	@Override
	public CompanyAttendance getShiftDetailOfUser(long shiftID,long companyID) 
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String QUERY = "";
		if(shiftID != 0)
		{
			namedParameters.addValue("pShiftID", shiftID);
			QUERY = QUERY+GET_COMPANY_SHIFT_DETAIL_BY_COMPANYID_AND_SHIFTID;
		}
		else
		{
			namedParameters.addValue("isDefaultShift", 1);
			QUERY = QUERY+GET_COMPANY_DEFAULT_SHIFT_DETAIL_BY_COMPANYID;			
		}
		namedParameters.addValue("pCompanyID", companyID);
		return namedParameterJdbcTemplate.query(QUERY, namedParameters, new ResultSetExtractor<CompanyAttendance>() {
			@Override
			public CompanyAttendance extractData(final ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					CompanyAttendance companyAttendance = new CompanyAttendance();
					companyAttendance.setCompanyID(companyID);
					companyAttendance.setId(rs.getLong("ID"));
					companyAttendance.setCompanyShiftName(rs.getString("companyShiftName"));
					companyAttendance.setIsDefaultShift(rs.getLong("isDefaultShift"));
					companyAttendance.setFlexibleHoursAllowed(rs.getBoolean("flexibleHoursAllowed"));
					companyAttendance.setCountWorkingHoursBeforeCheckInFrom(rs.getBoolean("countWorkingHoursBeforeCheckInFrom"));
					companyAttendance.setCheckInFrom(rs.getTime("checkInFrom"));
					companyAttendance.setCheckInTo(rs.getTime("CheckInTo"));
					companyAttendance.setCheckOutFrom(rs.getTime("CheckOutFrom"));
					companyAttendance.setCheckOutTo(rs.getTime("CheckOutTo"));
					companyAttendance.setCountWorkingHoursAfterCheckOutTo(rs.getBoolean("countWorkingHoursAfterCheckOutTo"));
					companyAttendance.setIsMinimumWorkingHourForPresence(rs.getBoolean("isMinimumWorkingHourForPresence"));
					companyAttendance.setMinimumWorkingHourForPresence(rs.getInt("minimumWorkingHourForPresence"));
					companyAttendance.setIsHalfDayWorkingHour(rs.getBoolean("isHalfDayWorkingHour"));
					companyAttendance.setHalfDayWorkingHour(rs.getInt("halfDayWorkingHour"));
					companyAttendance.setShiftHour(rs.getInt("shiftHour"));
					companyAttendance.setMaxShiftHour(rs.getInt("maxShiftHour"));
					companyAttendance.setIsMinShiftHour(rs.getBoolean("isMinShiftHour"));
					companyAttendance.setMinShiftHour(rs.getInt("minShiftHour"));
					companyAttendance.setIsEarlyGoingHalfDay(rs.getBoolean("isEarlyGoingHalfDay"));
					companyAttendance.setIsLateComingHalfDay(rs.getBoolean("isLateComingHalfDay"));
					companyAttendance.setMaxEarlyGoTime(rs.getTime("maxEarlyGoTime"));
					companyAttendance.setMaxLateComeTime(rs.getTime("maxLateComeTime"));
					companyAttendance.setSaturdayCountedInWorkingHours(rs.getBoolean("saturdayCountedInWorkingHours"));
					companyAttendance.setSundayCountedInWorkingHours(rs.getBoolean("sundayCountedInWorkingHours"));
					companyAttendance.setCalculateDaily(rs.getBoolean("isCalculateDaily"));
					companyAttendance.setCalculateWeekly(rs.getBoolean("isCalculateWeekly"));
					companyAttendance.setCalculateMonthly(rs.getBoolean("isCalculateMonthly"));
					companyAttendance.setLateleavingNextDayLateAllowed(rs.getBoolean("lateleavingNextDayLateAllowed"));
					companyAttendance.setLateleavingNextDateLatetime(rs.getTime("lateleavingNextDateLatetime"));
					companyAttendance.setMaximumLateAllowedForLateLeaving(rs.getTime("maximumLateAllowedForLateLeaving"));
					companyAttendance.setWeeklyShortHoursHalfDay(rs.getInt("weeklyShortHoursHalfDay"));
					companyAttendance.setAftermidnightMaxAllowedTime(rs.getString("aftermidnightMaxAllowedTime"));
					companyAttendance.setMinimumExtraMonthHours(rs.getInt("minimumExtraMonthHours"));
					companyAttendance.setHoursForEveryHalfDayReversal(rs.getInt("hoursForEveryHalfDayReversal"));
					companyAttendance.setModifiedBy(rs.getInt("modifiedBy"));
					companyAttendance.setCompanyStartTime(rs.getString("Work_StartTime"));
					companyAttendance.setCompanyEndTime(rs.getString("Work_EndTime"));
					companyAttendance.setWorkingSaturday(rs.getString("Working_Saturdays"));
					companyAttendance.setWorkingDays(rs.getString("Working_Days"));
					companyAttendance.setSaturdayPolicy(rs.getBoolean("Saturday_Policy"));
					companyAttendance.setApprovalmaxworkinghours(rs.getBoolean("approvalMaxWorkingHours"));
					companyAttendance.setAftermidnightCheckoutAllow(rs.getBoolean("aftermidnightCheckoutAllow"));
					return companyAttendance;
				}
				return null;
			}
		});
	}
	
	@Override
	public Map<Long,Long> getBranchIDOfAllActiveCompanies(List<Long> companyIDs) 
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyIDs", companyIDs);
	
		return namedParameterJdbcTemplate.query(GET_COMPANY_USER_BRANCHID,namedParameters, new ResultSetExtractor<Map<Long,Long>>() {
		       @Override
		       public Map<Long,Long> extractData(ResultSet rs)throws SQLException, DataAccessException {
		    	 Map<Long,Long> map = new HashMap<Long,Long>();
		         while (rs.next()) 
		         {
		        	 map.put(rs.getLong("User_ID"), rs.getLong("Branch_ID"));
		         }
		         return map;
		        }
			});
		
	}

	@Override
	public Users getUserDetail(long userID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("ID", userID);
		Users aUser = new Users();
		try {
			 namedParameterJdbcTemplate.query(GET_USER_DETAILS, namedParameters,
					new RowMapper<Users>() {
						@Override
						public Users mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							aUser.setUserId(Long.parseUnsignedLong(rs.getString("ID")));
							aUser.setFirstName(rs.getString("First_Name"));
							aUser.setLastName(rs.getString("Last_Name"));
							aUser.setPicture(rs.getString("Picture"));
							if (aUser.getPicture() == null) {
								aUser.setPicture("");
							}
							return aUser;
						}
					});
			 return aUser;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error getting user ID discarding Admin role", e);
			return null;
		}
	}
	
	@Override
	public String getUpperHierarchyDetails(final long userid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		String managerIDs = "";
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(dataSource).withFunctionName("getUpperLevelHierarchy");
			namedParameters.addValue("userid", String.valueOf(userid));
			managerIDs = String.valueOf(call.executeFunction(Integer.class, namedParameters)).trim();
		} catch (DataAccessException e) {
			LOG.error("Error executing function to read manager upper hierarchy" + e);
			return null;
		}
		return managerIDs;
	}
	
	@Override
	public String getDeviceID(final long userid)
	{
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("userid", userid);
		namedParameters.addValue("applicationid", 1);
		try 
		{
			return namedParameterJdbcTemplate.query(GET_DEVICEID, namedParameters, new ResultSetExtractor<String>() {
				@Override
				public String extractData(final ResultSet rs) throws SQLException, DataAccessException
				{
					if (rs.next())
					{
						if (rs.getString("GCM_ID") != null)
							return rs.getString("GCM_ID");
						else
							return "";
					}
				return "";
			}
		});
		} catch (EmptyResultDataAccessException e) {
			return "";
		} catch (DataAccessException e) {
			return null;
		}
	}


}