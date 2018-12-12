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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bigbang.teamworksScheduler.MvcConfiguration;
import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.Holidays;
import com.bigbang.teamworksScheduler.beans.Users;

public class MissedCheckInUserDAOImpl implements MissedCheckInUserDAO{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	Logger LOG = LogManager.getLogger(MissedCheckInUserDAOImpl.class);
	
	private static String GET_COMPANY = "Select DISTINCT(c.Company_ID) from Company c "
			+ " where c.Tracking_StartTime <= :currentTime and c.Tracking_EndTime >= :currentTime";
	
	private static String GET_COMPANYSUPERADMIN_SQL = "Select Distinct(U.ID) from " + MvcConfiguration.masterSchema
			+ ".USERS U, " + MvcConfiguration.masterSchema + ".USER_ROLE R where U.ID = R.User_ID and R.Active  = 1 "
					+ " and  R.Company_ID IN(:companyList) and R.Role_ID IN(1,2,3)";
	
	static final String GET_EXPORT_LIST_USER_ATTENDANCE_SQL = "Select a.ID, a.Attendance_Date, a.User_ID, a.Company_ID, "
			+ "a.CheckIn_Time, a.CheckIn_Address_ID, a.CheckOut_Time, a.CheckOut_Address_ID, a.Present, "
			+ "a.Manual_Attendance, a.Manual_Approved, a.TimeIn, a.TimeOut, a.Reason,a.Leave_Type_ID, a.Leave_Day, a.Updated_TimeIn, "
			+ "a.Updated_TimeOut, a.Late, a.Early, a.MinutesLate, a.MinutesEarly, a.Modified_By, a.Modified_Time "
			+ "from Attendance_Master a where a.User_ID IN (:userID) and a.Company_ID = :companyID and "
			+ "CAST(a.Attendance_Date AS DATE) >= :startDate and CAST(a.Attendance_Date AS DATE) <= :endDate and Active = :active "
			+ "order by a.CheckIn_Time IS NULL DESC, a.CheckIn_Time DESC;";
	
	static final String GET_ALL_COMPANY_HOLIDAYS = "SELECT * FROM Company_Holidays WHERE "
			+ " Company_ID IN (:companyid) AND Active = 1";
	
	private static final String GET_WORKING_DAYS = "Select c.Working_Days,c.Company_ID "
			+ "from Company c where c.Company_ID IN (:companyID);";
	
//	static final String GET_EXPORT_LIST_USER_ATTENDANCE_SQL = "Select a.ID, a.Attendance_Date, a.User_ID, a.Company_ID, "
//			+ "a.CheckIn_Time, a.CheckIn_Address_ID, a.CheckOut_Time, a.CheckOut_Address_ID, a.Present, "
//			+ "a.Manual_Attendance, a.Manual_Approved, a.TimeIn, a.TimeOut, a.Reason,a.Leave_Type_ID, a.Leave_Day, a.Updated_TimeIn, "
//			+ "a.Updated_TimeOut, a.Late, a.Early, a.MinutesLate, a.MinutesEarly, a.Modified_By, a.Modified_Time "
//			+ "from "+ MvcConfiguration.masterSchema
//			+ ".USERS u LEFT JOIN Attendance_Master a  ON a.User_ID = u.ID AND a.Company_ID = :companyID AND "
//			+ " CAST(a.Attendance_Date AS DATE) >= :startDate and CAST(a.Attendance_Date AS DATE) <= :endDate and Active = :active "
//			+ " where u.ID IN (:userID) "
//			+ "order by a.CheckIn_Time IS NULL DESC, a.CheckIn_Time DESC;";
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
	public List<Long> getCompanySuperAdmin(List<Long> companyIDList) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		System.out.println("companylist : " + companyIDList);
		namedParameters.addValue("companyList", companyIDList);
		List<Long> List = new ArrayList<Long>();
		try {
			List = namedParameterJdbcTemplate.query(GET_COMPANYSUPERADMIN_SQL, namedParameters,
					new RowMapper<Long>() {
						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							return Long.parseUnsignedLong(rs.getString("ID"));
						}
					});

			return List;
		} catch (DataAccessException e) {
			LOG.error("Error getting list active companies", e);
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
	public Map<Long,List<Date>> getHolidayDates(List<Long> companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("companyid", companyID);
		Map<Long,List<Date>> dateList = new HashMap<Long,List<Date>>();

		namedParameterJdbcTemplate.query(GET_ALL_COMPANY_HOLIDAYS, namedParameters, new RowMapper<Date>() {
			@Override
			public Date mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				if(dateList.containsKey(rs.getInt("Company_ID"))){
					List<Date> holidayList = dateList.get(rs.getInt("Company_ID"));
					holidayList.add(rs.getDate("Date"));
					dateList.put(rs.getLong("Company_ID"), holidayList);
				}else{
					List<Date> holidayList = new ArrayList<Date>();
					holidayList.add(rs.getDate("Date"));
					dateList.put(rs.getLong("Company_ID"), holidayList);
				}
				
				return null;
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
	public Map<Long,String> getWorkingDays(List<Long> companyID) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		Map<Long,String> dataList = new HashMap<Long,String>();
		
		namedParameters.addValue("companyID", companyID);
		namedParameterJdbcTemplate.query(GET_WORKING_DAYS, namedParameters, new RowMapper<Date>() {
			@Override
			public Date mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				dataList.put(rs.getLong("Company_ID"), rs.getString("Working_Days"));
				
				return null;
			}
		});
//			return dataList.put(rs.getLong("Company_ID"), rs.getDate("Date"));
//		}
//		String retVal = namedParameterJdbcTemplate.queryForObject(GET_WORKING_DAYS, namedParameters, String.class);
		return dataList;

	}
	
	@Override
	public List<Attendance> getExportNewAttendanceHistory(
			List<Long> userIdList, long companyID, String startDate, String endDate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		List<Attendance> list = new ArrayList<Attendance>();

		namedParameters.addValue("userID", userIdList);
		namedParameters.addValue("companyID", companyID);
		namedParameters.addValue("active", true);
		
		String SQL;
		namedParameters.addValue("startDate", startDate);
		namedParameters.addValue("endDate", endDate);
		SQL = GET_EXPORT_LIST_USER_ATTENDANCE_SQL;
	
		list = namedParameterJdbcTemplate.query(SQL, namedParameters,
				new RowMapper<Attendance>() {

					@Override
					public Attendance mapRow(ResultSet rs, int rowNum) throws SQLException {
						Attendance attendance = new Attendance();
						attendance.setAttendanceDate(rs.getDate("Attendance_Date"));
						if (rs.getString("CheckIn_Address_ID") != null)
							attendance.setCheckInAddressID(Long.parseUnsignedLong(rs.getString("CheckIn_Address_ID")));

						attendance.setCheckInTime(rs.getTimestamp("CheckIn_Time"));

						if (rs.getString("CheckOut_Address_ID") != null)
							attendance.setCheckOutAddressID(Long.parseUnsignedLong(rs.getString("CheckOut_Address_ID")));

						attendance.setCheckOutTime(rs.getTimestamp("CheckOut_Time"));
						attendance.setCompanyID(Long.parseUnsignedLong(rs.getString("Company_ID")));
						attendance.setID(Long.parseUnsignedLong(rs.getString("ID")));
						attendance.setManualAttendance(rs.getString("Manual_Attendance"));
						attendance.setManualApproved(rs.getBoolean("Manual_Approved"));
						attendance.setModifiedBy(Long.parseUnsignedLong(rs.getString("Modified_By")));
						attendance.setPresent(rs.getBoolean("Present"));
						attendance.setReason(rs.getString("Reason"));
						attendance.setTimeIn(rs.getTimestamp("TimeIn"));
						attendance.setTimeOut(rs.getTimestamp("TimeOut"));
						attendance.setUpdatedTimeIn(rs.getTimestamp("Updated_TimeIn"));
                        attendance.setLeaveTypeID(rs.getInt("Leave_Type_ID"));

                        if(rs.getInt("Leave_Type_ID") > 0){
                        	attendance.setLeaveType(rs.getString("Leave_Day"));
                        }
						if (attendance.getUpdatedTimeIn() == null)
							attendance.setCheckInApproved(true);
						else
							attendance.setCheckInApproved(false);

						attendance.setUpdatedTimeOut(rs.getTimestamp("Updated_TimeOut"));

						if (attendance.getUpdatedTimeOut() == null)
							attendance.setCheckOutApproved(true);
						else
							attendance.setCheckOutApproved(false);

						Users user = new Users();
						user.setUserId(Long.parseUnsignedLong(rs.getString("User_ID")));
						attendance.setUser(user);

						return attendance;
					}
				});

		if (list != null && list.size() != 0)
			return list;
		else
			return new ArrayList<Attendance>();
	}
}
