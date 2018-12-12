package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import notification.SendNotifications;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.CompanyLeaves;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.service.Properties;

public class LeaveSchedulerDAOImpl implements LeaveSchedulerDAO {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	Logger LOG = LogManager.getLogger(LeaveSchedulerDAOImpl.class);

	private static final String GET_AUTO_LEAVE_UPDATE_COMPANY = "Select Company_ID from Company where Auto_Leave_Update = true;";

	private static final String GET_COMPANY_LEAVES = "Select  Company_ID, Leave_Type_ID, No_Of_Leaves "
			+ "from Company_Leaves where Company_ID in (:companyID) and Leave_Update_Cycle in (:cycle) and Active = :active;";

	/*
	 * private static final String UPDATE_AUTO_LEAVE_LOG = "Insert into Leave_Log " +
	 * "( User_ID, Company_ID, Month, `Year`, Leave_Type_ID, Existing_Leaves, Balance, Acrrued, Updated_By ) " +
	 * "Select bal.User_ID, bal.Company_ID, :month, :year, bal.Leave_Type_ID, bal.Leave_Balance, " +
	 * "bal.Leave_Balance + cl.No_Of_Leaves as Leave_Balance, cl.No_Of_Leaves , 0 from Leave_Balance bal, Company_Leaves cl "
	 * +
	 * "where bal.Company_ID = cl.Company_ID and bal.Active = true and cl.Active = true and cl.Leave_Update_Cycle in (:cycle) "
	 * + "and bal.Leave_Type_ID = cl.Leave_Type_ID;";
	 */

	private static final String UPDATE_AUTO_LEAVE_LOG = "Update Leave_Log set Accrued = :accrued, Balance = Balance + :accrued "
			+ "where Company_ID = :companyId and Month = :month and `Year` = :year and Leave_Type_ID = :leavetypeID;";

	private static final String AUTO_LEAVE_LOG = "Insert into Leave_Log "
			+ "( User_ID, Company_ID, Month, `Year`, Leave_Type_ID, Existing_Leaves, Balance, Accrued, Updated_By ) "
			+ "Select bal.User_ID, bal.Company_ID, :month, :year, bal.Leave_Type_ID, bal.Leave_Balance, "
			+ "bal.Leave_Balance as Leave_Balance, 0, 0 from Leave_Balance bal	where bal.Active = true;";

	private static final String UPDATE_AUTO_LEAVE_BAL = "Update Leave_Balance set Leave_Balance = Leave_Balance + :leave, "
			+ "Total_Leaves = Total_Leaves + :leave where Company_ID = :companyID and Active = true and Leave_Type_ID = :leaveTypeID;";

	private static final String GET_UPDATED_LEAVE_USER = "Select DISTINCT(User_ID) from Leave_Log where Month = :month and Year = :year;";

	private static final String GET_USER_FROM_LEAVE_BAL = "Select DISTINCT(User_ID) from Leave_Balance where Active = 1;";

	private static final String GET_USER_COMPANY_MAP = "Select User_ID, Company_ID from "
			+ MvcConfiguration.masterSchema + ".USER_ROLE where " + "Active = 1 and Role_ID NOT IN (Select ID from "
			+ MvcConfiguration.masterSchema + ".META_ROLES where Description IN (:roles));";

	private static final String INSERT_LEAVE_BALANCE = "Insert into Leave_Balance "
			+ "( User_ID, Leave_Type_ID, Leave_Balance, Total_Leaves, Company_ID, Active ) values "
			+ "(:userID, :leaveTypeID, :leaveBalance, :totalLeaves, :companyID, :active);";

	private static final String GET_LEAVE_TYPE = "Select Leave_Type_ID from Meta_Leave_Type;";

	private static final String GET_USER_GCM = "Select r.User_ID, r.Company_ID, g.GCM_ID "
			+ "from MasterSchema.USER_ROLE r, " + MvcConfiguration.masterSchema + ".USER_APPLICATION_GCM g "
			+ "where r.User_ID in (Select DISTINCT(User_ID) from Leave_Log where Month = :month and Year = :year) and "
			+ "r.User_ID = g.User_ID and g.Application_ID = 1 and r.Active = true;";

	private static final String ADD_NOTIFICATION = "Insert into "
			+ MvcConfiguration.masterSchema
			+ ".NOTIFICATIONS "
			+ "( User_ID, Message, Application_ID, Type, Is_Notified, Data, Transaction_ID, Status, Company_ID) values "
			+ "(:userID, :message, :applicationID , :type, :isNotified, :data, :transactionID, :status, :companyID);";

	/**
	 * Get list of company Id for which auto leave update is active
	 * 
	 * @return List
	 */
	@Override
	public List<Long> getAutoLeaveUpdateCompany() {
		List<Long> companyIDList = namedParameterJdbcTemplate.query(GET_AUTO_LEAVE_UPDATE_COMPANY,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						// TODO Auto-generated method stub
						return Long.parseUnsignedLong(rs.getString("Company_ID"));
					}
				});
		return companyIDList;
	}

	/**
	 * Get list of users who have leave balance added in database
	 * 
	 * @return List
	 */
	@Override
	public List<Long> getUserFromLeaveBal() {
		List<Long> userIDList = namedParameterJdbcTemplate.query(GET_USER_FROM_LEAVE_BAL, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				return Long.parseUnsignedLong(rs.getString("User_ID"));
			}
		});
		return userIDList;
	}

	/**
	 * Insert leave balance as 0 for users who do not have nay entry for leave balance
	 */
	@Override
	public String addLeaveBalance(Map<Long, Long> userCompanyMap) {
//		System.out.println("add leave balance called");
		StringBuffer str = new StringBuffer();
		try
		{
		
		List<Integer> leaveTypeList = namedParameterJdbcTemplate.query(GET_LEAVE_TYPE, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("Leave_Type_ID");
			}
		});
//		System.out.println("add leave balance called");
		MapSqlParameterSource[] namedParameters = new MapSqlParameterSource[userCompanyMap.size()
				* leaveTypeList.size()];
//		System.out.println("add leave balance called");
		int i = 0;

		for (Long userID : userCompanyMap.keySet()) {
			for (int leaveType : leaveTypeList) {
				MapSqlParameterSource namedParameter = new MapSqlParameterSource();
				namedParameter.addValue("userID", userID);
				namedParameter.addValue("companyID", userCompanyMap.get(userID));
				namedParameter.addValue("leaveTypeID", leaveType);
				namedParameter.addValue("leaveBalance", 0);
				namedParameter.addValue("totalLeaves", 0);
				namedParameter.addValue("active", true);
				namedParameters[i] = namedParameter;
				i++;
			}
			str.append(userID + ",");
		}
//		System.out.println("add leave balance called" +str);
//		System.out.println(INSERT_LEAVE_BALANCE);
		namedParameterJdbcTemplate.batchUpdate(INSERT_LEAVE_BALANCE, namedParameters);
//		System.out.println(INSERT_LEAVE_BALANCE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str.toString();
	}

	/**
	 * Add notification to database
	 */
	@Override
	public void addNotifications(List<SendNotifications> notifications) {
		MapSqlParameterSource[] namedParameters = new MapSqlParameterSource[notifications.size()];
		int i = 0;
		for (SendNotifications not : notifications) {
			MapSqlParameterSource namedParameter = new MapSqlParameterSource();
			namedParameter.addValue("userID", not.getUserID());
			namedParameter.addValue("companyID", not.getCompanyID());
			namedParameter.addValue("message", not.getMessage());
			namedParameter.addValue("applicationID", Properties.get("applicationID"));
			namedParameter.addValue("type", not.getType());
			namedParameter.addValue("isNotified", not.isNotified());
			namedParameter.addValue("data", not.getData());
			namedParameter.addValue("transactionID", not.getTransactionID());
			namedParameter.addValue("status", not.getStatus());
			namedParameters[i] = namedParameter;
			i++;
		}
		namedParameterJdbcTemplate.batchUpdate(ADD_NOTIFICATION, namedParameters);
	}

	/**
	 * Get map of users vs company
	 * 
	 * @return List
	 */
	@Override
	public Map<Long, Long> getMemberCompanyMap() {

		List<String> roles = new ArrayList<String>();
		MapSqlParameterSource namedParameter = new MapSqlParameterSource();
		roles.add(Constants.SUPER_ADMIN);
		roles.add(Constants.ADMIN);

		namedParameter.addValue("roles", roles);

		Map<Long, Long> usercompanyMap = new HashMap<Long, Long>();

		namedParameterJdbcTemplate.query(GET_USER_COMPANY_MAP, namedParameter, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				usercompanyMap.put(Long.parseUnsignedLong(rs.getString("User_ID")),
						Long.parseUnsignedLong(rs.getString("Company_ID")));
				return null;
			}
		});
		return usercompanyMap;
	}

	/**
	 * Get user GCM ID
	 * 
	 * @return List
	 */
	@Override
	public List<User> getUser(int month, int year) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("month", month);
		namedParameters.addValue("year", year);
		List<User> userList = namedParameterJdbcTemplate.query(GET_USER_GCM, namedParameters, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setUserID(Long.parseUnsignedLong(rs.getString("User_ID")));
				user.setGcmID(rs.getString("GCM_ID"));
				Company comp = new Company();
				comp.setCompanyID(Long.parseUnsignedLong(rs.getString("Company_ID")));
				user.setCompany(comp);
				return user;
			}
		});
		return userList;
	}

	/**
	 * Get list of company Id for cycle
	 * 
	 * @return List
	 */

	@Override
	public List<CompanyLeaves> getCompanyLeaves(List<Long> companyIDs, List<String> cycles) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyID", companyIDs);
		namedParameters.addValue("cycle", cycles);
		namedParameters.addValue("active", true);
		List<CompanyLeaves> companyIDList = namedParameterJdbcTemplate.query(GET_COMPANY_LEAVES, namedParameters,
				new RowMapper<CompanyLeaves>() {
					@Override
					public CompanyLeaves mapRow(ResultSet rs, int rowNum) throws SQLException {

						CompanyLeaves companyLeaves = new CompanyLeaves();
						companyLeaves.setCompanyId(Long.parseUnsignedLong(rs.getString("Company_ID")));
						companyLeaves.setLeaveTypeId(rs.getInt("Leave_Type_ID"));
						companyLeaves.setNoOfLeaves(rs.getDouble("No_Of_Leaves"));
						// TODO Auto-generated method stub
						return companyLeaves;
					}
				});
		return companyIDList;
	}

	/**
	 * Update auto leave log for all users
	 * 
	 * @param month
	 * @param year
	 * @param cycle
	 */
	@Override
	public void addAutoLeaveLog(int month, int year) {
		
		try
		{
System.out.println(AUTO_LEAVE_LOG);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("month", month);
		namedParameters.addValue("year", year);

		namedParameterJdbcTemplate.update(AUTO_LEAVE_LOG, namedParameters);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Update auto leave log for all users
	 * 
	 * @param month
	 * @param year
	 * @param cycle
	 */
	@Override
	public void updateAutoLeaveLog(int month, int year, CompanyLeaves companyLeaves) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("month", month);
		namedParameters.addValue("year", year);
		namedParameters.addValue("leavetypeID", companyLeaves.getLeaveTypeId());
		namedParameters.addValue("companyId", companyLeaves.getCompanyId());
		namedParameters.addValue("accrued", companyLeaves.getNoOfLeaves());

		namedParameterJdbcTemplate.update(UPDATE_AUTO_LEAVE_LOG, namedParameters);
	}

	/**
	 * Update auto leave balance for all company users for leave type users
	 * 
	 * @param leave
	 * @param companyID
	 * @param leaveTypeID
	 */
	@Override
	public void updateAutoLeaveBal(double leave, long companyID, int leaveTypeID) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("leave", leave);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("leaveTypeID", leaveTypeID);

		namedParameterJdbcTemplate.update(UPDATE_AUTO_LEAVE_BAL, namedParameters);
	}

	/**
	 * Get list of users for which auto leave has been updated
	 * 
	 * @param month
	 * @param year
	 * @return
	 */
	@Override
	public List<Long> getUpdatedLeaveUser(int month, int year) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("month", month);
		namedParameters.addValue("year", year);

		List<Long> userIDList = namedParameterJdbcTemplate.query(GET_UPDATED_LEAVE_USER, namedParameters,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						// TODO Auto-generated method stub
						return Long.parseUnsignedLong(rs.getString("User_ID"));
					}
				});
		return userIDList;
	}

}
