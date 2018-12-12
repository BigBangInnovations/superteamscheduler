package com.bigbang.teamworksScheduler.dao;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UserAttendanceDAOImpl implements UserAttendanceDAO
{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public static String INSERT_QUERY_INTO_USER_ATTENDANCE = "INSERT INTO USER_ATTENDANCE_TABLE (userID,companyID,Date,inTime,outTime,"
			+ "onLeave,isHoliday,isSaturday,isSunday,hourWorked,status,shorthours,lateComing,earlyGoing,isAbsent) "
			+ "VALUES (:pUserID, :pCompanyID, :pDate, :pInTime, :pOutTime, :pOnLeave, :pIsHoliday, :pIsSaturday, :pIsSunday, :pHourWorked, :pStatus, :pShorthours, :pLateComing, :pEarlyGoing ,:isAbsent)";
	
	@Override
	public void insertUserAttendanceIntoTable(long userID, long companyID, Date date, 
			Date inTime, Date outTime, boolean onLeave, boolean isHoliday, boolean isSaturday,
			boolean isSunday, String hourWorked, String status, long shorthours, boolean lateComing, boolean earlyGoing , boolean isAbsent)
	{
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
		
		namedParameterJdbcTemplate.update(INSERT_QUERY_INTO_USER_ATTENDANCE, namedParameters);
	}

}

