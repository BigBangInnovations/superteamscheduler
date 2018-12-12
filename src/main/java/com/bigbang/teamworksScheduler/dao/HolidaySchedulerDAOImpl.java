package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.Holidays;

public class HolidaySchedulerDAOImpl implements HolidaySchedulerDAO {
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final String GET_COMPANY_USERS = "Select User_ID, Company_ID from " + MvcConfiguration.masterSchema
			+ ".USER_ROLE where Role_ID " + "NOT IN (:roles) and Company_ID in (:companyID) and Active = true;";

	private final String INSERT_HOLIDAY_INTO_ATTENDANCE = "Insert into Attendance_Master "
			+ "(User_ID,Company_ID, Attendance_Date, Leave_Type_ID, Leave_Day , Present, Reason , Active, Modified_By) values "
			+ "(:userID, :companyID, :attendanceDate, :leaveTypeID, :leaveDay, :present, :reason, :active, :modifiedBy);";

	private final String SELECT_HOLIDAYLIST_OF_COMPANIES = "SELECT * FROM Company_Holidays "
			+ "WHERE IsMandatory = 1 AND Active = 1 AND Company_ID IN (:companyIDs) AND CAST(Date AS DATE) = :date ;";
	
	public final String SELECT_NOT_WORKING_COMPANY_LIST = "SELECT * FROM Company c WHERE c.Working_Days NOT LIKE :day ;";

	/**
	 * Get list of company users
	 * 
	 * @return List
	 */
	@Override
	public Map<Long, Long> getCompanyUsers(List<Long> companyID) {

		List<String> roles = new ArrayList<String>();
		MapSqlParameterSource namedParameter = new MapSqlParameterSource();
		roles.add(Constants.SUPER_ADMIN);
		roles.add(Constants.ADMIN);

		namedParameter.addValue("roles", roles);
		namedParameter.addValue("companyID", companyID);

		Map<Long, Long> map = new HashMap<Long, Long>();

		namedParameterJdbcTemplate.query(GET_COMPANY_USERS, namedParameter, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				map.put(Long.parseUnsignedLong(rs.getString("User_ID")),
						Long.parseUnsignedLong(rs.getString("Company_ID")));
				return null;
			}
		});

		return map;

	}

	/**
	 * This function will add address entry into database for holiday or absent
	 * 
	 * @param userCompanyMap
	 * @param leaveType
	 */
	@Override
	public void addAttendance(Map<Long, Long> userCompanyMap, int leaveType, Date date) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();

		namedParameters.addValue("attendanceDate", date);
		namedParameters.addValue("leaveTypeID", leaveType);
		namedParameters.addValue("leaveDay", Constants.LEAVE_DAY_FULL);
		namedParameters.addValue("present", false);
		namedParameters.addValue("reason", "");
		namedParameters.addValue("active", true);

		for (long userID : userCompanyMap.keySet()) {
			namedParameters.addValue("userID", userID);
			namedParameters.addValue("companyID", userCompanyMap.get(userID));
			namedParameters.addValue("modifiedBy", userID);

			namedParameterJdbcTemplate.update(INSERT_HOLIDAY_INTO_ATTENDANCE, namedParameters);
		}

	}
	
	@Override
	public List<Holidays> getAllHolidaysOfAllCompany(List<Long> companyIDs, Date date) 
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyIDs", companyIDs);
		namedParameters.addValue("date", date);
	
		return namedParameterJdbcTemplate.query(SELECT_HOLIDAYLIST_OF_COMPANIES,namedParameters, new ResultSetExtractor<List<Holidays>>() {
		       @Override
		       public List<Holidays> extractData(ResultSet rs)throws SQLException, DataAccessException {
		    	 List<Holidays> listOfHolidays = new ArrayList<Holidays>();
		         while (rs.next()) 
		         {
		        	 Holidays holiday = new Holidays(date,rs.getString("Name"));
		        	 holiday.setBranchID(rs.getLong("BranchID"));
		        	 holiday.setNationalHoliday(rs.getBoolean("IsNationalHoliday"));
		        	 holiday.setMandatory(rs.getBoolean("IsMandatory"));
		        	 holiday.setCompanyID(rs.getLong("Company_ID"));
		        	 listOfHolidays.add(holiday);
		         }
		         return listOfHolidays;
		        }
			});
		
	}
	
	/**
	 * This method will add Holidays entry into ATTENDANCE
	 */
	@Override
	public void addAttendanceForUser(long userID,long companyID, int leaveType, Date date) 
	{
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("attendanceDate", date);
		namedParameters.addValue("leaveTypeID", leaveType);
		namedParameters.addValue("leaveDay", Constants.LEAVE_DAY_FULL);
		namedParameters.addValue("present", false);
		namedParameters.addValue("reason", "");
		namedParameters.addValue("active", true);
		namedParameters.addValue("userID", userID);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("modifiedBy", userID);
		namedParameterJdbcTemplate.update(INSERT_HOLIDAY_INTO_ATTENDANCE, namedParameters);
	}

	@Override
	public List<Long> getNotWorkingCompanyfromCompanyAttendance(String day) {

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("day", "%" + day + "%");

		List<Long> companyIDList = new ArrayList<Long>();

		companyIDList = namedParameterJdbcTemplate.query(SELECT_NOT_WORKING_COMPANY_LIST, namedParameters,
				new RowMapper<Long>() {
					@Override
					public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						return Long.parseUnsignedLong(rs.getString("Company_ID"));
					}
				});

		return companyIDList;
	}


}
