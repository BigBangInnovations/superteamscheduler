package com.bigbang.teamworksScheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bigbang.teamworksScheduler.beans.CheckInCheckOutHistory;

public class AddressDAOImpl implements AddressDAO{
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	

	Logger LOG = LogManager.getLogger(AddressDAOImpl.class);
	private static final String GET_USERS_LOCATIONS = "SELECT l.User_ID, l.DateTime, "
			+ "l.Location, l.Latitude, l.Longitude, l.GPS_On, l.Tracking_Type,l.Company_ID FROM User_Location l "
			+ " WHERE Cast(l.DateTime as date) <= Cast(:dateTime as date) and Cast(l.DateTime as date) >= Cast('2018/1/16 00:00:00' as date) AND l.location = '' "
			+ " AND l.Latitude != 0 AND l.Longitude != 0 AND l.Active = 1 ORDER BY l.DateTime DESC";
	
	private static final String UPDATE_USERLOCATION = "Update User_Location set Location = :location "
			+ "where User_ID = :userID and Company_ID = :companyID and DateTime = :dateTime;";
	
	private static final String GET_PENDING_ATTENDANCE_ADDRESS_UPDATE = "SELECT ch.* FROM "
			+ " CheckIn_CheckOut_History ch JOIN Attendance_Master a ON a.ID = ch.Attendance_ID "
			+ " WHERE  ((ch.CheckIn_Latitude != 0 AND ch.CheckIn_Longitude != 0 AND ch.CheckIn_Address = '' "
			+ " AND a.Attendance_CheckIn_Type = :type) "
			+ " OR (CheckOut_Latitude != 0 AND CheckOut_Longitude != 0 AND CheckOut_Address = '' "
			+ " AND Attendance_CheckOut_Type = :type)) "
			+ "AND CAST(a.Attendance_Date as DATE) = CAST(:createdTime AS DATE) "
			+ " AND ch.Is_Active = :active ";
	
	private static final String UPDATE_TRAVEL_CHECK_IN_CHECK_OUT_ADDRESS = "UPDATE CheckIn_CheckOut_History SET "
			+ " CheckIn_Address = :checkInAddress , CheckOut_Address = :checkOutAddress "
			+ " WHERE ID = :ID AND Attendance_ID = :attendanceID ";
	
	@Override
	public List<UserLocation> getUserLocations(final Date startdate) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("dateTime", startdate);
		//namedParameters.addValue("companyid", companyID);

		// fetch each User's latest User Location
		// To display on the Tracking Page of Admin/Manager
		List<UserLocation> listUserLocation = namedParameterJdbcTemplate.query(GET_USERS_LOCATIONS, namedParameters,
				new RowMapper<UserLocation>() {

					@Override
					public UserLocation mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						Date datetime = new java.util.Date(rs.getTimestamp("DateTime").getTime());

						UserLocation aUserLocation = new UserLocation();

						aUserLocation.setUserId(Long.parseUnsignedLong(rs.getString("User_ID")));
						aUserLocation.setTime(new SimpleDateFormat("HH:mm:ss").format(datetime));
						aUserLocation.setDate(datetime);
						aUserLocation.setLocation(rs.getString("Location"));
						aUserLocation.setLatitude(rs.getDouble("Latitude"));
						aUserLocation.setLongitude(rs.getDouble("Longitude"));
						aUserLocation.setGpsOn(rs.getBoolean("GPS_On"));
						aUserLocation.setTrackingType(rs.getString("Tracking_Type"));
						aUserLocation.setCompanyId(rs.getLong("Company_ID"));
						return aUserLocation;
					}
				});

		// Collections.sort(listUserLocation, UserLocation.firstNameComparator);
		return listUserLocation;
	}

	@Override
	public void updateLocation(List<UserLocation> locationList) {
		int i = 0;
		try
		{
		for (UserLocation ulocation : locationList) {
			System.out.println(ulocation.getCompanyId());
			System.out.println("__________________");
			System.out.println("userID  " + ulocation.getUserId());
			System.out.println("dateTime " + ulocation.getDate());
			System.out.println("location " + ulocation.getLocation());
			System.out.println("companyID " + ulocation.getCompanyId());
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("userID", ulocation.getUserId());
			namedParameters.addValue("dateTime", ulocation.getDate());
			namedParameters.addValue("location", ulocation.getLocation());
			namedParameters.addValue("companyID", ulocation.getCompanyId());

			namedParameterJdbcTemplate.update(UPDATE_USERLOCATION, namedParameters);
			i++;
			System.out.println("i " + i);
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public List<CheckInCheckOutHistory> getCheckInCheckOutHistory(Date startDate) {
		
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("createdTime", startDate);
		namedParameters.addValue("type", 4);
		namedParameters.addValue("active", 1);

		System.out.println("date: "+startDate);
		System.out.println("SQL: "+GET_PENDING_ATTENDANCE_ADDRESS_UPDATE);
		List<CheckInCheckOutHistory> listUserLocation = namedParameterJdbcTemplate.query(GET_PENDING_ATTENDANCE_ADDRESS_UPDATE, 
				namedParameters, new RowMapper<CheckInCheckOutHistory>() {

					@Override
					public CheckInCheckOutHistory mapRow(final ResultSet rs, final int rowNum) throws SQLException {

						CheckInCheckOutHistory checkInCheckOut = new CheckInCheckOutHistory();
						checkInCheckOut.setId(rs.getLong("ID"));
						checkInCheckOut.setAttendanceID(rs.getInt("Attendance_ID"));
						checkInCheckOut.setCheckInAddress(rs.getString("CheckIn_Address"));
						checkInCheckOut.setCheckInLatitude(rs.getDouble("CheckIn_Latitude"));
						checkInCheckOut.setCheckInLongitude(rs.getDouble("CheckIn_Longitude"));
						checkInCheckOut.setCheckOutAddress(rs.getString("CheckOut_Address"));
						checkInCheckOut.setCheckOutLatitude(rs.getDouble("CheckOut_Latitude"));
						checkInCheckOut.setCheckOutLongitude(rs.getDouble("CheckOut_Longitude"));
						
						return checkInCheckOut;
					}
				});

		return listUserLocation;
		
	}

	@Override
	public void updateCheckInCheckOutAddress(List<CheckInCheckOutHistory> checkInCheckOutList) {
		
		int i = 0;
		for (CheckInCheckOutHistory history : checkInCheckOutList) {
			LOG.debug("ID: "+history.getId());
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			namedParameters.addValue("checkInAddress", history.getCheckInAddress());
			namedParameters.addValue("checkOutAddress", history.getCheckOutAddress());
			namedParameters.addValue("ID", history.getId());
			namedParameters.addValue("attendanceID", history.getAttendanceID());

			System.out.println("i: "+i);
			namedParameterJdbcTemplate.update(UPDATE_TRAVEL_CHECK_IN_CHECK_OUT_ADDRESS, namedParameters);
			i++;
		}
		
	}

}
