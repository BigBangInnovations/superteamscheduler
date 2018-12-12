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
import com.bigbang.teamworksScheduler.beans.CompanyInfo;
import com.bigbang.teamworksScheduler.beans.Holidays;


public class CompanyDAOImpl implements CompanyDAO {

	private static final Logger LOG = LogManager.getLogger(CompanyDAOImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	static final String INSERT_HOLIDAYS = "INSERT INTO Company_Holidays (Company_ID, Date, Name, Modified_By, Active)"
			+ " Values (:companyid, :date, :name, :modifiedby, 1)";

	static final String CLEAR_HOLIDAYS = "UPDATE Company_Holidays SET Active = 0 WHERE Company_ID = :companyid";

	static final String GET_HOLIDAYS = "SELECT * FROM Company_Holidays WHERE Company_ID = :companyid AND Active = 1";

	static final String GET_HOLIDAYS_NEW = "SELECT * FROM Company_Holidays WHERE Company_ID = :companyid AND Active = 1 "
			+ "and YEAR( `Date`) >= YEAR(now()) AND CAST(`Date` as DATE) = CAST(:date as DATE) ;";
	
	static final String INSERT = "Insert into Company "
			   + "(Company_ID, Notification_Level, Working_Days, Tracking_StartTime, Tracking_EndTime, Work_StartTime, "
			   + "Work_EndTime,Saturday_Policy,isFifteenMinTracking, Working_Saturdays, "
			   + "Payroll, Auto_Leave_Update, Modified_By, Created_By, Created_Date, Tracking_Interval,"
			   + "isShelfieAllowed,Minimum_Working_Hours,Average_Working_Hours) values "
			   + "(:companyID, :notificationLevel, :workingDays, :trackingStartTime,:trackingEndTime,:workStartTime,  "
			   + ":workEndTime, :saturdayPolicy, :isFifteenMinTracking, :workingSaturdays, "
			   + ":payroll, :autoLeaveUpdate, :modifiedBy, :createdBy, now(), :trackingInterval,"
			   + ":isShelfieAllowed,:minWorkingHrs,:avgWorkingHrs)";

//	 static final String UPDATE = "UPDATE Company set Working_Days = :workingDays, Tracking_StartTime = :trackingStartTime, "
//			   + "Tracking_EndTime = :trackingEndTime, Work_StartTime = :workStartTime, Work_EndTime = :workEndTime, "
//			   + "Saturday_Policy = :saturdayPolicy, isFifteenMinTracking = :isFifteenMinTracking, Working_Saturdays = :workingSaturdays, "
//			   + "Payroll = :payroll, Auto_Leave_Update = :autoLeaveUpdate, Tracking_Interval = :trackingInterval, "
//			   + "Modified_By = :modifiedBy, otherDeviceLogin = :otherDeviceLogin, isShelfieAllowed =:isShelfieAllowed, "
//			   + "Minimum_Working_Hours = :minWorkingHrs,Average_Working_Hours= :avgWorkingHrs " //, isGeoFencing = :isGeoFencing
//			   + "where Company_ID = :companyID;";
	
	static final String UPDATE = "UPDATE Company set Working_Days = :workingDays, Tracking_StartTime = :trackingStartTime, "
			   + "Tracking_EndTime = :trackingEndTime, Work_StartTime = :workStartTime, Work_EndTime = :workEndTime, "
			   + "Saturday_Policy = :saturdayPolicy, isFifteenMinTracking = :isFifteenMinTracking, Working_Saturdays = :workingSaturdays, "
			   + "Payroll = :payroll, Auto_Leave_Update = :autoLeaveUpdate, Tracking_Interval = :trackingInterval, "
			   + "Modified_By = :modifiedBy, isShelfieAllowed =:isShelfieAllowed, "
			   + "Minimum_Working_Hours = :minWorkingHrs,Average_Working_Hours= :avgWorkingHrs " //, isGeoFencing = :isGeoFencing
			   + "where Company_ID = :companyID;";
	
	static final String UPDATE_COMMONSERVICE_COMPANY = "UPDATE "
			+ " COMPANY_MASTER SET Disable_Other_Login = :otherDeviceLogin "
			+ " where ID = :companyID AND Is_Active = :active ";

	static final String GET = "SELECT * from Company where Company_ID = :companyid";

	static final String GET_PRIVILEGE = "SELECT * from Meta_Privilege";

	static final String GET_USER_PRIVILEGE = "SELECT Company_Role_Privilege.Role_ID, "
			+ "Company_Role_Privilege.Privilege_ID, Description "
			+ "FROM Company_Role_Privilege INNER JOIN Meta_Privilege on "
			+ "Company_Role_Privilege.Privilege_ID = Meta_Privilege.Privilege_ID "
			+ "WHERE Company_Role_Privilege.Company_ID = :companyid "
			+ "AND Company_Role_Privilege.Role_ID = :roleid AND Company_Role_Privilege.Active = 1";

	static final String ADD_ROLE_PRIVILEGE = "INSERT INTO Company_Role_Privilege "
			+ "(Company_ID, Role_ID, Privilege_ID, Modified_By, Active)"
			+ " values (:companyid, :roleid, :privilegeid, :modifiedby, 1)";

	static final String GET_ROLE_PRIVILEGE = "SELECT Company_Role_Privilege.Role_ID,"
			+ " Company_Role_Privilege.Privilege_ID, Description FROM Company_Role_Privilege "
			+ " INNER JOIN Meta_Privilege on" + " Company_Role_Privilege.Privilege_ID = Meta_Privilege.Privilege_ID"
			+ " WHERE Company_ID = :companyid AND Company_Role_Privilege.Active = 1";
	
	static final String GET_MANUAL_ATTENDANCE_MANAGER_ROLE_PRIVILEGE = "SELECT Company_Role_Privilege.Role_ID,"
			+ " Company_Role_Privilege.Privilege_ID FROM Company_Role_Privilege "
			+ " WHERE Company_ID = :companyid AND Privilege_ID = :previlegeID AND Company_Role_Privilege.Active = 1";


	static final String CLEAR_ROLE_PRIVILEGE = "UPDATE Company_Role_Privilege SET Active = 0 where Company_ID = :companyid";

	static final String GET_COMPANY_TIME = "SELECT Work_StartTime, Work_EndTime, Working_Days, "
			+ "(TIME_TO_SEC(Work_EndTime) - TIME_TO_SEC(Work_StartTime))/3600 AS `Hours` "
			+ "FROM Company WHERE Company_ID = :companyid";

	private static final String GET_WORKING_DAYS = "Select c.Working_Days "
			+ "from Company c where c.Company_ID = :companyID;";

	static final String GET_COMPANY_CREATE_DATE = "Select Created_Time from "
			+ " COMPANY_MASTER where ID = :companyID;";

	static final String SET_AUTO_LEAVE_UPDATE = "Update Company set Auto_Leave_Update = :active where Company_ID = :companyId;";

	static final String GET_AUTO_LEAVE_UPDATE = "Select Auto_Leave_Update from Company where Company_ID = :companyId;";

	static final String GET_COMPANY_DETAILS = "SELECT COMPANY_MASTER.Name,COMPANY_MASTER.Logo,COMPANY_ADDRESS.Address_Master_ID FROM "
			+ " COMPANY_MASTER,"
			+ " COMPANY_ADDRESS WHERE COMPANY_MASTER.ID = COMPANY_ADDRESS.Company_ID And COMPANY_ADDRESS.Type = 1 "
			+ "And COMPANY_MASTER.ID = :companyID  AND COMPANY_MASTER.Is_Active = 1";

	static final String GET_ALL_COMPANY_DETAILS = "SELECT COMPANY_MASTER.ID,COMPANY_MASTER.Name,COMPANY_MASTER.Logo,COMPANY_ADDRESS.Address_Master_ID FROM "
			+ " COMPANY_MASTER,"
			+ " COMPANY_ADDRESS WHERE COMPANY_MASTER.ID = COMPANY_ADDRESS.Company_ID And COMPANY_ADDRESS.Type = 1 "
			+ "And COMPANY_MASTER.Is_Active = 1";

	
	static final String GET_COMPANY_ADDRESS_DETAILS = "SELECT ADDRESS_MASTER.Address_Line1,ADDRESS_MASTER.Address_Line2 FROM "
			+ "ADDRESS_MASTER WHERE Address_Master_ID = :addressID";
	
	static final String UPDATE_PAYROLL = "UPDATE Company set Payroll = :payroll WHERE Company_ID = :companyid ";

	static final String ADD_MANUAL_ATTENDANCE_ROLE_PRIVILEGE = "INSERT INTO Company_Role_Privilege "
			+ "(Company_ID, Role_ID, Privilege_ID, Modified_By, Active)"
			+ " values (:companyid, :roleid, :privilegeid, :modifiedby, 1)";
	
	static final String GET_COMPANY_RADIUS = "SELECT radius FROM "
			+" COMPANY_MASTER WHERE ID = :companyID ";
	
	static final String GET_COMPANY_GEOFENCING = "SELECT isGeoFencing FROM "
			+" COMPANY_MASTER WHERE ID = :companyID ";
	
	static final String GET_COMPANY_DETAILS_SQL = "SELECT c.* FROM "+ MvcConfiguration.masterSchema 
			+".COMPANY_MASTER cm JOIN Company c ON cm.ID = c.Company_ID WHERE "
			+ "cm.ID IN (:companyIDList) AND cm.Is_Active = :active";
	
	private static final String GET_ALL_ACTIVE_COMPANY_DETAILS = "SELECT Company_ID from  " + MvcConfiguration.masterSchema  + ".ACTIVE_COMPANIES where SuperTeam_App = 1";
	
	/*@Override
	public void updateCompanyInfo(final CompanyInfo company, boolean isUpdate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", company.getCompanyid());
		namedParameters.addValue("notificationLevel", Constants.NOTIFICATION_LEVEL);
		namedParameters.addValue("workingDays", company.getWorkingDays());
		namedParameters.addValue("trackingStartTime", company.getTrackingStartTime());
		namedParameters.addValue("trackingEndTime", company.getTrakingEndTime());
		namedParameters.addValue("workStartTime", company.getStartTime());
		namedParameters.addValue("workEndTime", company.getEndTime());
		namedParameters.addValue("minWorkingHrs", company.getMinimumWorkingTime());
		namedParameters.addValue("avgWorkingHrs", company.getAvgWorkingTime());
		namedParameters.addValue("saturdayPolicy", company.isSaturdayPolicy());
		namedParameters.addValue("workingSaturdays", company.getWorkingSaturday());
		namedParameters.addValue("payroll", (company.isPayrollEnabled()) ? 1 : 0);
		namedParameters.addValue("autoLeaveUpdate", company.isAutoLeaveUpdate());
		namedParameters.addValue("modifiedBy", company.getModifiedBy());
		namedParameters.addValue("createdBy", company.getCreatedBy());
		namedParameters.addValue("trackingInterval", company.getTrackingInteval());
		namedParameters.addValue("isFifteenMinTracking", company.isFifteenMinTracking());
//		namedParameters.addValue("otherDeviceLogin", company.isOtherDeviceLogin());
		namedParameters.addValue("isShelfieAllowed", company.isShelfieAllowed());
//		namedParameters.addValue("isGeoFencing", company.isGeoFencing());
		
		if (isUpdate) {
			namedParameterJdbcTemplate.update(UPDATE, namedParameters);
		} else {
			namedParameterJdbcTemplate.update(INSERT, namedParameters);
		}
		
		//update the company settings in commonservices too
//		MapSqlParameterSource namedParameter = new MapSqlParameterSource();
//		namedParameter.addValue("companyID", company.getCompanyid());
//		namedParameter.addValue("active", 1);
//		namedParameter.addValue("otherDeviceLogin", company.isOtherDeviceLogin());
//		namedParameter.addValue("isGeoFencing", company.isGeoFencing());
		
//		namedParameterJdbcTemplate.update(UPDATE_COMMONSERVICE_COMPANY, namedParameter);
	}*/

	@Override
	public CompanyInfo getCompanybyID(final long companyid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);
		return namedParameterJdbcTemplate.query(GET, namedParameters, new ResultSetExtractor<CompanyInfo>() {
			@Override
			public CompanyInfo extractData(final ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					CompanyInfo company = new CompanyInfo();
					company.setCompanyid(companyid);
					company.setNotificationLevel(rs.getInt("Notification_Level"));
					company.setAutoLeaveUpdate(rs.getBoolean("Auto_Leave_Update"));
					company.setAvgWorkingTime(rs.getString("Average_Working_Hours"));
					company.setEndTime(rs.getString("Work_EndTime"));
					company.setMinimumWorkingTime(rs.getString("Minimum_Working_Hours"));
					company.setPayrollEnabled(rs.getBoolean("Payroll"));
					company.setSaturdayPolicy(rs.getBoolean("Saturday_Policy"));
					company.setWorkingDays(rs.getString("Working_Days"));
					company.setStartTime(rs.getString("Work_StartTime"));
					company.setTrackingStartTime(rs.getString("Tracking_StartTime"));
					company.setTrakingEndTime(rs.getString("Tracking_EndTime"));
					company.setWorkingSaturday(rs.getString("Working_Saturdays"));
					company.setTrackingInteval(rs.getInt("Tracking_Interval"));
					company.setFifteenMinTracking(rs.getBoolean("isFifteenMinTracking"));
//					company.setOtherDeviceLogin(rs.getBoolean("otherDeviceLogin"));
					company.setShelfieAllowed(rs.getBoolean("isShelfieAllowed"));
//					company.setGeoFencing(rs.getBoolean("isGeoFencing"));
					return company;
				}
				return null;
			}
		});
	}

	/**
	 * Read all privileges from meta table
	 */
	/*@Override
	public List<Privilege> getPrivileges() {

		List<Privilege> listprivilege = namedParameterJdbcTemplate.query(GET_PRIVILEGE, new RowMapper<Privilege>() {
			@Override
			public Privilege mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				Privilege privilege = new Privilege();

				privilege.setPrivilegeid(Long.parseUnsignedLong(rs.getString("Privilege_ID")));
				privilege.setPrivilege(rs.getString("Description"));
				privilege.setRoleid(Long.parseUnsignedLong(rs.getString("Role_ID")));

				return privilege;
			}
		});
		return listprivilege;
	}*/

	/*@Override
	public List<Privilege> getUserPrivileges(final long companyid, final long roleid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);
		namedParameters.addValue("roleid", roleid);

		try {
			List<Privilege> listprivilege = namedParameterJdbcTemplate.query(GET_USER_PRIVILEGE, namedParameters,
					new RowMapper<Privilege>() {

						@Override
						public Privilege mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							Privilege privilege = new Privilege();

							privilege.setPrivilegeid(Long.parseUnsignedLong(rs.getString("Privilege_ID")));
							privilege.setPrivilege(rs.getString("Description"));
							privilege.setRoleid(Long.parseUnsignedLong(rs.getString("Role_ID")));

							return privilege;
						}

					});

			return listprivilege;
		} catch (DataAccessException e) {
			LOG.error("Error fetching Privileges", e);
			return null;
		}
	}*/

	/*@Override
	public List<Holidays> getHolidays(final long companyid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);

		try {
			List<Holidays> listHolidays = namedParameterJdbcTemplate.query(GET_HOLIDAYS, namedParameters,
					new RowMapper<Holidays>() {
						@Override
						public Holidays mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							Holidays holiday = new Holidays(rs.getDate("Date"), rs.getString("Name"));
							return holiday;
						}
					});
			return listHolidays;
		} catch (DataAccessException e) {
			LOG.error("Error fetching Holidays", e);
			return null;
		}
	}*/

	/**
	 * Add roles and privileges for the company in database
	 * 
	 * @param companyid
	 * @param modifiedBy
	 * @param privilegeIdList
	 */
	/*@Override
	public void addRolePrivilege(final long companyid, final long modifiedBy, List<Privilege> privilegeIdList) {

		MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[privilegeIdList.size()];
		int i = 0;

		for (Privilege privilege : privilegeIdList) {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("companyid", companyid);
			namedParameters.addValue("roleid", privilege.getRoleid());
			namedParameters.addValue("privilegeid", privilege.getPrivilegeid());
			namedParameters.addValue("modifiedby", modifiedBy);
			namedParametersArray[i] = namedParameters;
			i++;
		}
		namedParameterJdbcTemplate.batchUpdate(ADD_ROLE_PRIVILEGE, namedParametersArray);
	}*/

	/**
	 * Delete all the role privileges for provided companyID from CLEAR_ROLE_PRIVILEGE table
	 */
	@Override
	public void clearRolePrivilege(final long companyid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);
		namedParameterJdbcTemplate.update(CLEAR_ROLE_PRIVILEGE, namedParameters);
	}

	/*@Override
	public List<Privilege> getRolePrivilege(final long companyid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);

		try {
			List<Privilege> listprivilege = namedParameterJdbcTemplate.query(GET_ROLE_PRIVILEGE, namedParameters,
					new RowMapper<Privilege>() {

						@Override
						public Privilege mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							Privilege privilege = new Privilege();

							privilege.setPrivilegeid(Long.parseUnsignedLong(rs.getString("Privilege_ID")));
							privilege.setPrivilege(rs.getString("Description"));
							privilege.setRoleid(Long.parseUnsignedLong(rs.getString("Role_ID")));

							return privilege;
						}

					});

			return listprivilege;
		} catch (DataAccessException e) {
			LOG.error("Error getting Roles and Privileges", e);
			return null;
		}
	}*/

	/*@Override
	public List<Privilege> getManualAttendanceRolePrivilege(final long companyid,int previlageID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);
		namedParameters.addValue("previlegeID", previlageID);

		try {
			List<Privilege> listprivilege = namedParameterJdbcTemplate.query(GET_MANUAL_ATTENDANCE_MANAGER_ROLE_PRIVILEGE, namedParameters,
					new RowMapper<Privilege>() {

						@Override
						public Privilege mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							Privilege privilege = new Privilege();

							privilege.setPrivilegeid(Long.parseUnsignedLong(rs.getString("Privilege_ID")));
							privilege.setRoleid(Long.parseUnsignedLong(rs.getString("Role_ID")));

							return privilege;
						}

					});

			return listprivilege;
		} catch (DataAccessException e) {
			e.printStackTrace();
			LOG.error("Error getting Roles and Privileges", e);
			return null;
		}
	}*/

	
	@Override
	public Map<String, Object> getCompanyTime(final long companyid) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);

		try {
			return namedParameterJdbcTemplate.query(GET_COMPANY_TIME, namedParameters,
					new ResultSetExtractor<Map<String, Object>>() {

						@Override
						public Map<String, Object> extractData(final ResultSet rs) throws SQLException,
								DataAccessException {
							if (rs.next()) {
								Map<String, Object> companyTimeMap = new HashMap<String, Object>();

								companyTimeMap.put("starttime", rs.getTime("Work_StartTime").toString());
								companyTimeMap.put("endtime", rs.getTime("Work_EndTime").toString());
								companyTimeMap.put("hours", String.valueOf(rs.getInt("Hours")));
								companyTimeMap.put("workingdays", rs.getString("Working_Days"));
								/*
								 * companyTimeMap.put("holidays", getHolidays(companyid));
								 */

								return companyTimeMap;
							}

							return null;
						}

					});
		} catch (DataAccessException e) {
			LOG.error("Error fetching Company time", e);
			return null;
		}
	}

	/**
	 * . Get Company holiday list from database
	 * 
	 * @param companyID
	 * @return List
	 */
	@Override
	public List<Date> getHolidayDates(final long companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyID);
		List<Date> dateList = new ArrayList<Date>();

		dateList = namedParameterJdbcTemplate.query(GET_HOLIDAYS, namedParameters, new RowMapper<Date>() {
			@Override
			public Date mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				return rs.getDate("Date");
			}
		});
		return dateList;
	}

	/**
	 * . read company working days
	 * 
	 * @param companyID
	 * @return String
	 */
	@Override
	public String getWorkingDays(final long companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);
		String retVal = namedParameterJdbcTemplate.queryForObject(GET_WORKING_DAYS, namedParameters, String.class);
		return retVal;

	}

	@Override
	public Date getCompanyCreateDate(final long companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);

		try {
			Date date = namedParameterJdbcTemplate.queryForObject(GET_COMPANY_CREATE_DATE, namedParameters, Date.class);
			return date;
		} catch (DataAccessException e) {
			LOG.error("Error getting company creation date", e);
			return null;
		}
	}

	/**
	 * Set6 LEave auto update active/inactive for company id
	 */
	@Override
	public void setAutoLeaveUpdate(long companyId, boolean active) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyId", companyId);
		namedParameters.addValue("active", active);

		namedParameterJdbcTemplate.update(SET_AUTO_LEAVE_UPDATE, namedParameters);
	}

	/**
	 * Get value of auto leave update for company ID
	 * 
	 * @return boolean
	 */
	@Override
	public boolean getAutoLeaveUpdate(long companyId) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyId", companyId);
		boolean autoUpdateLeave = namedParameterJdbcTemplate.queryForObject(GET_AUTO_LEAVE_UPDATE, namedParameters,
				Boolean.class);
		return autoUpdateLeave;
	}

	@Override
	public CompanyInfo getCompanyDetails(long companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);

		try {
			return namedParameterJdbcTemplate.query(GET_COMPANY_DETAILS, namedParameters,
					new ResultSetExtractor<CompanyInfo>() {

						@Override
						public CompanyInfo extractData(final ResultSet rs) throws SQLException, DataAccessException {
							if (rs.next()) {
								CompanyInfo companyInfo = new CompanyInfo();
								companyInfo.setCompanyid(companyID);
								companyInfo.setName(rs.getString("Name"));
								companyInfo.setLogo(rs.getString("Logo"));
								if (companyInfo.getLogo() == null) {
									companyInfo.setLogo("");
								}
								/*AddressBean addressInfo = getCompanyAddressDetails(rs.getLong("Address_Master_ID"));
								companyInfo.setAddressLine1(addressInfo.getAddressLine1());
								companyInfo.setAddressLine2(addressInfo.getAddressLine2());*/

								return companyInfo;
							}

							return null;
						}
					});
		} catch (DataAccessException e) {
			LOG.error("Error fetching Company Information", e);
			return null;
		}
	}
	
	@Override
	public List<CompanyInfo> getAllCompanyDetails() {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		try {
			List<CompanyInfo> listCompany = namedParameterJdbcTemplate.query(GET_ALL_COMPANY_DETAILS, namedParameters,
					new RowMapper<CompanyInfo>() {

						@Override
						public CompanyInfo mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							CompanyInfo companyInfo = new CompanyInfo();
							companyInfo.setCompanyid(rs.getLong("ID"));
							companyInfo.setName(rs.getString("Name"));
							companyInfo.setLogo(rs.getString("Logo"));
							if (companyInfo.getLogo() == null) {
								companyInfo.setLogo("");
							}
							/*Address addressInfo = getCompanyAddressDetails(rs.getLong("Address_Master_ID"));
							companyInfo.setAddressLine1(addressInfo.getAddressLine1());
							companyInfo.setAddressLine2(addressInfo.getAddressLine2());*/
							return companyInfo;
						}
					});

			return listCompany;
		} catch (DataAccessException e) {
			LOG.error("Error getting Roles and Privileges", e);
			return null;
		}
	}

	/*public Address getCompanyAddressDetails(long addressID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("addressID", addressID);

		try {
			return namedParameterJdbcTemplate.query(GET_COMPANY_ADDRESS_DETAILS, namedParameters,
					new ResultSetExtractor<Address>() {

						@Override
						public Address extractData(final ResultSet rs) throws SQLException, DataAccessException {
							if (rs.next()) {
								Address addressDetails = new Address();

								addressDetails.setAddressLine1(rs.getString("Address_Line1"));
								addressDetails.setAddressLine2(rs.getString("Address_Line2"));

								return addressDetails;
							}

							return null;
						}

					});
		} catch (DataAccessException e) {
			LOG.error("Error fetching company address information", e);
			return null;
		}
	}*/

	/**
	 * Update company payroll flag in database
	 */
	/*@Override
	public void updatePayrollDetails(CompanyPayroll companyPayroll) throws AddCompanyPayrollException {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("payroll", companyPayroll.isPayrollEnabled());
		namedParameters.addValue("companyid", companyPayroll.getCompanyID());
		namedParameterJdbcTemplate.update(UPDATE_PAYROLL, namedParameters);
	}*/
	
	/*@Override
	public void addManualRolePrivilege(final long roleid , final long previlegeID , final long modifiedBy, List<Company> companyIDList) {

		MapSqlParameterSource[] namedParametersArray = new MapSqlParameterSource[companyIDList.size()];
		int i = 0;

		for (Company company : companyIDList) {
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("companyid", company.getCompanyid());
			namedParameters.addValue("roleid", roleid);
			namedParameters.addValue("privilegeid", previlegeID);
			namedParameters.addValue("modifiedby", modifiedBy);
			namedParametersArray[i] = namedParameters;
			i++;
		}
		namedParameterJdbcTemplate.batchUpdate(ADD_MANUAL_ATTENDANCE_ROLE_PRIVILEGE, namedParametersArray);
	}*/

	@Override
	public int getCompanyRadius(long companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);
		int retVal = namedParameterJdbcTemplate.queryForObject(GET_COMPANY_RADIUS, namedParameters, Integer.class);
		return retVal;
	}

	@Override
	public boolean getCompanyGeoFencing(long companyID) {
		// TODO Auto-generated method stub GET_COMPANY_GEOFENCING
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyID);
		boolean retVal = namedParameterJdbcTemplate.queryForObject(GET_COMPANY_GEOFENCING, namedParameters, Boolean.class);
		return retVal;
	}

	/**
	 * Get holiday list for company
	 * 
	 * @param companyid
	 * @return List
	 */
	@Override
	public List<Holidays> getCompanyHolidays(final long companyid) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyid);

		List<Holidays> listHolidays = namedParameterJdbcTemplate.query(GET_HOLIDAYS_NEW, namedParameters,
				new RowMapper<Holidays>() {

					@Override
					public Holidays mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						Holidays holiday = new Holidays(rs.getDate("Date"), rs.getString("Name"));
						holiday.setHolidayId(Long.parseUnsignedLong(rs.getString("ID")));
						return holiday;
					}
				});
		return listHolidays;
	}

	@Override
	public boolean checkIfCompanyHoliday(long companyID, Date date) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyID);
		namedParameters.addValue("date", date);
		
		namedParameterJdbcTemplate.query(GET_HOLIDAYS_NEW, namedParameters,
			new RowMapper<Boolean>() {

				@Override
				public Boolean mapRow(final ResultSet rs, final int rowNum) throws SQLException {
					if(rs.next())
						return true;
					else
						return false;
				}
			});
		return false;
	}
	
	@Override
	  public List<Long> getAllActiveCompaniesDetails() {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    try {
	      List<Long> listCompany = namedParameterJdbcTemplate.query(GET_ALL_ACTIVE_COMPANY_DETAILS, namedParameters,
	          new RowMapper<Long>() {

	            @Override
	            public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
	              return rs.getLong("Company_ID");
	            }
	          });

	      return listCompany;
	    } catch (DataAccessException e) {
	      return null;
	    }
	  }
}
