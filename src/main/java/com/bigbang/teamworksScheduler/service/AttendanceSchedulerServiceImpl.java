package com.bigbang.teamworksScheduler.service;

import handleException.AutoSchedulerException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import notification.SendNotificationException;
import notification.SendNotifications;
import notification.SendNotificationsHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.beans.AddressBean;
import com.bigbang.teamworksScheduler.beans.CheckInCheckOutHistory;
import com.bigbang.teamworksScheduler.beans.TrackingBean;
import com.bigbang.teamworksScheduler.dao.AddressDAO;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAO;
import com.bigbang.teamworksScheduler.util.GsonUtil;
import com.bigbang.teamworksScheduler.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AttendanceSchedulerServiceImpl implements AttendanceSchedulerService {

	@Autowired
	AttendanceSchedulerDAO schedulerDAO;
	@Autowired
	DailyData dailyData;
	@Autowired
	HolidaySchedulerDAO holidaySchedulerDAO;
	@Autowired
	AddressDAO addressDAO;
	
	Logger LOG = LogManager.getLogger(AttendanceSchedulerServiceImpl.class);

	/**
	 * This service method will send check-in notification to the users who have not checked in for company which has
	 * current time as start time
	 * 
	 * @throws SendNotificationException
	 * @throws NumberFormatException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void autoCheckIn() throws IOException, NumberFormatException, SendNotificationException {
		
		LOG.debug("Auto Check-in service class");
		Util util = new Util();

		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal.set(Calendar.SECOND, 0);

		Date date = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String timeStr = format.format(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);

		date = cal.getTime();

		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		// Get list of company with start time as current time
		List<Long> companyList = dailyData.getStartTimeCompany(timeStr);

		companyList.removeAll(dailyData.getCompanyNotWorking());

		LOG.debug("List of company with start time as: " + timeStr);
		companyList.forEach(id -> LOG.debug(id));

		if (companyList.size() == 0) {
			LOG.info("No company with start time: " + timeStr);
			return;
		}

		format = new SimpleDateFormat("yyyy-MM-dd");

		// Get List of company users who have already checked in for the day
		List<Long> tempUserList = schedulerDAO.getcheckedInUser(companyList, format.format(date));
		LOG.debug("List of user who are already checked in");
		tempUserList.forEach(id -> LOG.debug(id));

		List<Long> userOnLeave = schedulerDAO.getUserOnLeave(date);
		LOG.debug("List of user who are on leave");
		userOnLeave.forEach(id -> LOG.debug(id));

		// Add list of users who are on leave
		tempUserList.addAll(userOnLeave);

		// Put 0 value in case no users checked in
		if (tempUserList.size() == 0) {
			tempUserList.add((Long.parseUnsignedLong("0")));
		}
		// Get list of company users who have not checked in yet and are not on leave
		Map<Long, String> autoCheckInUser = schedulerDAO.getNotCheckedInUserList(companyList, tempUserList);

		LOG.info("Auto Checkin notification sending to UserIDs: ");
		autoCheckInUser.keySet().forEach(id -> LOG.debug(id));

		try{
			List<SendNotifications> not = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
					(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
					(String) Properties.get("senderID")).Send(autoCheckInUser,
					Integer.valueOf((String) Properties.get("ATTENDANCE_AUTO_CHECKIN")), null, null, 1,
					"ATTENDANCE_AUTO_CHECKIN", 0);
			
			LOG.info("Auto Checkin Notification sent to " + not.size() + " users");
		}catch(Exception e){
			LOG.error("Error in sending auto checkin notification");
			e.printStackTrace();
		}
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void autoCheckInTest() throws IOException, NumberFormatException, SendNotificationException {

		LOG.debug("Auto Check-in service class Test");
		Util util = new Util();

		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal.set(Calendar.SECOND, 0);

		Date date = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String timeStr = format.format(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);

		date = cal.getTime();

		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		// Get list of company with start time as current time
		List<Long> companyList = dailyData.getStartTimeCompany(timeStr);
				
		// Get list of company with start time as current time
//		List<Long> companyList = new ArrayList<Long>();
//		companyList.add((long) 276);
//		List<Long> tempUserList = new ArrayList<Long>();
//		tempUserList.add((long) 6037);
				//dailyData.getStartTimeCompany(timeStr);

		companyList.removeAll(dailyData.getCompanyNotWorking());

		LOG.debug("List of company with start time as: " + timeStr);
		companyList.forEach(id -> LOG.debug(id));

		if (companyList.size() == 0) {
			LOG.info("No company with start time: " + timeStr);
			return;
		}

		format = new SimpleDateFormat("yyyy-MM-dd");

		// Get List of company users who have already checked in for the day
		List<Long> tempUserList = schedulerDAO.getcheckedInUser(companyList, format.format(date));
		LOG.debug("List of user who are already checked in");
		tempUserList.forEach(id -> LOG.debug(id));

		List<Long> userOnLeave = schedulerDAO.getUserOnLeave(date);
		LOG.debug("List of user who are on leave");
		userOnLeave.forEach(id -> LOG.debug(id));

		// Add list of users who are on leave
		tempUserList.addAll(userOnLeave);

		// Put 0 value in case no users checked in
		if (tempUserList.size() == 0) {
			tempUserList.add((Long.parseUnsignedLong("0")));
		}
		// Get list of company users who have not checked in yet and are not on leave
		Map<Long, String> autoCheckInUser = schedulerDAO.getNotCheckedInUserListTest(companyList, tempUserList);

		LOG.info("Auto Checkin notification sending to UserIDs: ");
		autoCheckInUser.keySet().forEach(id -> LOG.debug(id));

		try{
			List<SendNotifications> not = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
					(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
					(String) Properties.get("senderID")).Send(autoCheckInUser,
					Integer.valueOf((String) Properties.get("ATTENDANCE_AUTO_CHECKIN")), null, null, 1,
					"ATTENDANCE_AUTO_CHECKIN", 0);
			
			LOG.info("Auto Checkin Notification sent to " + not.size() + " users");
		}catch(Exception e){
			LOG.error("Error in sending auto checkin notification");
			e.printStackTrace();
		}
	}

	/**
	 * This service method will send check-out notification to users who have checked in but currently not checked out
	 * for the company which has current time as company end time
	 * @throws SendNotificationException 
	 * @throws NumberFormatException 
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void autoCheckOut() throws IOException, NumberFormatException, SendNotificationException {

		LOG.debug("Auto Check-out service class");
		Util util = new Util();

		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
		cal.set(Calendar.SECOND, 0);

		Date date = cal.getTime();

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String timeStr = format.format(date);

		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);

		date = cal.getTime();

		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		// Get list of company with end time as current time
		List<Long> companyList = dailyData.getEndTimeCompany(timeStr);
		companyList.removeAll(dailyData.getCompanyNotWorking());

		if (companyList.size() == 0) {
			LOG.info("No company with working end time" + timeStr);
			return;
		}

		format = new SimpleDateFormat("yyyy-MM-dd");
		// Get List of company users who are currently check-in but not checked out
		List<Long> tempUserList = schedulerDAO.getUserNotCheckedOut(companyList, date);

		if (tempUserList.size() == 0) {
			LOG.info("No users are currently checked-in");
			return;
		}
		// Get user ID vs GCM ID map for user list
		Map<Long, String> autoCheckOutUser = schedulerDAO.getGCMUserMap(tempUserList);

		StringBuffer str = new StringBuffer();
		for (Long id : autoCheckOutUser.keySet())
			str.append(id + ", ");
		LOG.info("Auto CheckOut notification sending to UserIDs: " + str.toString());

		List<SendNotifications> not = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
				(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
				(String) Properties.get("senderID")).Send(autoCheckOutUser,

		Integer.valueOf((String) Properties.get("ATTENDANCE_AUTO_CHECKOUT")), null, null, 1,
				"ATTENDANCE_AUTO_CHECKOUT", 0);

		LOG.info("Auto Checkout Notification sent to " + not.size() + "users");
	}

	/**
	 * Service method to read tracking data for all users and do autocheckin and autocheckout in case missing This will
	 * be done for the users who are not on leave and company which are working for the date
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void attendanceScheduler() throws AutoSchedulerException {

		LOG.info("Service function to enter automatic check in/check out from tracking");
		Calendar cal = Calendar.getInstance();
		Util util = new Util();

		List<TrackingBean> trackinglist = new ArrayList<TrackingBean>();

		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
		double distance;
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Date date = cal.getTime();

		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		List<Long> activeCompany = schedulerDAO.getActiveCompany();
		activeCompany.removeAll(dailyData.getCompanyNotWorking());

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		// Get users tracking data for date
		Map<Long, List<TrackingBean>> trackingMap = schedulerDAO.getTrackingData(format.format(date));
		// Get company permanent location data
		Map<Long, AddressBean> addMap = schedulerDAO.getCompanyPermanentLocation();
		// Get users on leave for date
		List<Long> userOnLeave = schedulerDAO.getUserOnLeave(date);

		LOG.info("Start attendance scheduler checkin users");
		// ------------------CheckIn Users------------------------------------------------------------------

		// Get List of active company users who have checked in for the day
		List<Long> checkedInUserList = schedulerDAO.getcheckedInUser(activeCompany, format.format(date));
		// Add list of user who is on leave
		checkedInUserList.addAll(userOnLeave);
		if (checkedInUserList.size() == 0) {
			checkedInUserList.add(Long.parseUnsignedLong("0"));
		}
		// Get list of active company user who have not checked in for day and are not on leave
		Map<Long, String> notCheckedInUsers = schedulerDAO.getNotCheckedInUserList(activeCompany, checkedInUserList);

		Map<TrackingBean, AddressBean> checkInMap = new HashMap<TrackingBean, AddressBean>();

		for (Long userID : notCheckedInUsers.keySet()) {
			trackinglist = trackingMap.get(userID);
			// Check if tracking entry of user is available else continue with next user
			if (trackinglist == null || trackinglist.size() == 0) {
				continue;
			}
			// If address map does not contain company address then continue
			if (!addMap.containsKey(trackinglist.get(0).getCompanyID())) {
				continue;
			}
			// Check each tracking entry for user id to find when user is within office area.
			for (TrackingBean tracking : trackinglist) {
				try{
					AddressBean addBean = addMap.get(tracking.getCompanyID());
	
					distance = util.distance(tracking.getLatitude(), tracking.getLongitude(), addBean.getLatitude(),
							addBean.getLongitude());
					if (distance <= Double.parseDouble((String) Properties.get("teamworks.attendance.distance"))) {
						LOG.debug("UserID: " + tracking.getUserID() + " In Time: " + tracking.getDateTime());
						checkInMap.put(tracking, addBean);
						break;
					}
				}catch(Exception e){
					LOG.debug("userID: "+tracking.getUserID());
					LOG.error("Error adding checkin schdeuler: "+e);
				}
			}
		}
		LOG.debug("Inserting " + checkInMap.size() + "entries");
		int count = schedulerDAO.addCheckInAttendance(checkInMap);
		// ------------------CheckIn Users completed--------------------------------------------------------
		LOG.info("attendance scheduler Checkin done for " + count + "users");

		LOG.info("Start attendance scheduler checkout users");
		// ------------------CheckOut Users-----------------------------------------------------------------
		// Get List of company users who are currently check-in
		List<Long> checkOutUserList = schedulerDAO.getUserNotCheckedOut(activeCompany, date);

		Map<TrackingBean, AddressBean> checkOutMap = new HashMap<TrackingBean, AddressBean>();

		if (checkOutUserList.size() == 0) {
			LOG.info("No users are currently checked-in");
			return;
		}

		for (Long userID : checkOutUserList) {
			trackinglist = trackingMap.get(userID);
			// Check if tracking entry of user is available else continue with next user
			if (trackinglist == null || trackinglist.size() == 0) {
				continue;
			}
			Collections.reverse(trackinglist);
			for (TrackingBean tracking : trackinglist) {
				try{
					AddressBean addBean = addMap.get(tracking.getCompanyID());
	
					distance = util.distance(tracking.getLatitude(), tracking.getLongitude(), addBean.getLatitude(),
							addBean.getLongitude());
					if (distance <= Double.parseDouble((String) Properties.get("teamworks.attendance.distance"))) {
						LOG.debug("UserID: " + tracking.getUserID() + " Out Time: " + tracking.getDateTime());
						checkOutMap.put(tracking, addBean);
						break;
					}
				}catch(Exception e){
					LOG.debug("userID: "+tracking.getUserID());
					LOG.error("Error adding checkout schdeuler: "+e);
				}
			}
		}
		LOG.debug("checking out" + checkOutMap.size() + "entries");
		count = schedulerDAO.updateCheckOutAttendance(checkOutMap, date);
		// ------------------CheckIn Users completed--------------------------------------------------------
		LOG.info("Attendance scheduler Checkout done for " + count + "users");
	}
	
	
	/**
	 * Service method to read tracking data for all users and do autocheckin and autocheckout in case missing This will
	 * be done for the users who are not on leave and company which are working for the date
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void attendanceSchedulerTest() throws AutoSchedulerException {

		LOG.info("Service function to enter automatic check in/check out from tracking");
		Calendar cal = Calendar.getInstance();
		Util util = new Util();

		List<TrackingBean> trackinglist = new ArrayList<TrackingBean>();

		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
		double distance;
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Date date = cal.getTime();

		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		List<Long> activeCompany = schedulerDAO.getActiveCompany();
		activeCompany.removeAll(dailyData.getCompanyNotWorking());

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		// Get users tracking data for date
		Map<Long, List<TrackingBean>> trackingMap = schedulerDAO.getTrackingData(format.format(date));
		// Get company permanent location data
		Map<Long, AddressBean> addMap = schedulerDAO.getCompanyPermanentLocation();
		// Get users on leave for date
		List<Long> userOnLeave = schedulerDAO.getUserOnLeave(date);

		LOG.info("Start attendance scheduler checkin users");
		// ------------------CheckIn Users------------------------------------------------------------------

		// Get List of active company users who have checked in for the day
		List<Long> checkedInUserList = schedulerDAO.getcheckedInUser(activeCompany, format.format(date));
		// Add list of user who is on leave
		checkedInUserList.addAll(userOnLeave);
		if (checkedInUserList.size() == 0) {
			checkedInUserList.add(Long.parseUnsignedLong("0"));
		}
		// Get list of active company user who have not checked in for day and are not on leave
		Map<Long, String> notCheckedInUsers = schedulerDAO.getNotCheckedInUserList(activeCompany, checkedInUserList);

		Map<TrackingBean, AddressBean> checkInMap = new HashMap<TrackingBean, AddressBean>();

		for (Long userID : notCheckedInUsers.keySet()) {
			trackinglist = trackingMap.get(userID);
			// Check if tracking entry of user is available else continue with next user
			if (trackinglist == null || trackinglist.size() == 0) {
				continue;
			}
			// If address map does not contain company address then continue
			if (!addMap.containsKey(trackinglist.get(0).getCompanyID())) {
				continue;
			}
			// Check each tracking entry for user id to find when user is within office area.
			for (TrackingBean tracking : trackinglist) {
				try{
					AddressBean addBean = addMap.get(tracking.getCompanyID());
	
					distance = util.distance(tracking.getLatitude(), tracking.getLongitude(), addBean.getLatitude(),
							addBean.getLongitude());
					if (distance <= Double.parseDouble((String) Properties.get("teamworks.attendance.distance"))) {
						LOG.debug("UserID: " + tracking.getUserID() + " In Time: " + tracking.getDateTime());
						checkInMap.put(tracking, addBean);
						break;
					}
				}catch(Exception e){
					LOG.debug("userID: "+tracking.getUserID());
					LOG.error("Error adding checkin schdeuler: "+e);
				}
			}
		}
		LOG.debug("Inserting " + checkInMap.size() + "entries");
		int count = schedulerDAO.addCheckInAttendance(checkInMap);
		// ------------------CheckIn Users completed--------------------------------------------------------
		LOG.info("attendance scheduler Checkin done for " + count + "users");

		LOG.info("Start attendance scheduler checkout users");
		// ------------------CheckOut Users-----------------------------------------------------------------
		// Get List of company users who are currently check-in
		List<Long> checkOutUserList = schedulerDAO.getUserNotCheckedOut(activeCompany, date);

		Map<TrackingBean, AddressBean> checkOutMap = new HashMap<TrackingBean, AddressBean>();

		if (checkOutUserList.size() == 0) {
			LOG.info("No users are currently checked-in");
			return;
		}

		for (Long userID : checkOutUserList) {
			trackinglist = trackingMap.get(userID);
			// Check if tracking entry of user is available else continue with next user
			if (trackinglist == null || trackinglist.size() == 0) {
				continue;
			}
			Collections.reverse(trackinglist);
			for (TrackingBean tracking : trackinglist) {
				try{
					AddressBean addBean = addMap.get(tracking.getCompanyID());
	
					distance = util.distance(tracking.getLatitude(), tracking.getLongitude(), addBean.getLatitude(),
							addBean.getLongitude());
					if (distance <= Double.parseDouble((String) Properties.get("teamworks.attendance.distance"))) {
						LOG.debug("UserID: " + tracking.getUserID() + " Out Time: " + tracking.getDateTime());
						checkOutMap.put(tracking, addBean);
						break;
					}
				}catch(Exception e){
					LOG.debug("userID: "+tracking.getUserID());
					LOG.error("Error adding checkout schdeuler: "+e);
				}
			}
		}
		LOG.debug("checking out" + checkOutMap.size() + "entries");
		count = schedulerDAO.updateCheckOutAttendance(checkOutMap, date);
		// ------------------CheckIn Users completed--------------------------------------------------------
		LOG.info("Attendance scheduler Checkout done for " + count + "users");
	}

	/**
	 * Service method will read Users for all company which are working, but have not checked in/out or not on leave.
	 * For such user will update attendance as absent
	 * 
	 * @throws AutoSchedulerException
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void absentUserScheduler() throws AutoSchedulerException {

		LOG.info("Service function to mark absent for users whose attendance not available");
		Calendar cal = Calendar.getInstance();
		Util util = new Util();

		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		/*cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 8); //running for sep
		cal.set(Calendar.YEAR, 2017);*/
		Date date = cal.getTime();
//Date date = new Date("2017/11/25");
		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		List<Long> activeCompany = schedulerDAO.getActiveCompany();
		activeCompany.removeAll(dailyData.getCompanyNotWorking());

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		System.out.println("Start absent user scheduler checkin users");

		// Get List of active company users who have attendance entry for date
		List<Long> checkedInUserList = schedulerDAO.getcheckedInUser(activeCompany, format.format(date));

		if (checkedInUserList.size() == 0) {
			checkedInUserList.add(Long.parseUnsignedLong("0"));
		}
		// Get list of active company user who do not have attendance entry for the date
		Map<Long, Long> absentUsers = schedulerDAO.getAbsentUserCompanyMap(activeCompany, checkedInUserList);

		System.out.println("Adding absent entries for users: " + absentUsers.keySet());
		// Add holiday into attendance table
	    holidaySchedulerDAO.addAttendance(absentUsers, Constants.LEAVE_TYPE_ABSENT, date);

		System.out.println("Absent entries added successfully");
	}

	@Override
	public void autoUpdateTravelAddress() throws IOException, NumberFormatException, SendNotificationException, Exception {

		String splitKeyArray[] = Properties.get("geocode.location.key").toString().split(",");
		LOG.debug("array size : " + splitKeyArray.length);
		
		Calendar cal = Calendar.getInstance();
		//cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
		
		cal.set(Calendar.SECOND, 0);
		
		Calendar cal1 = Calendar.getInstance();
		//cal1.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
		
		cal1.set(Calendar.SECOND, 0);
		
		Date date = cal.getTime();
		
		// Check if daily data is not stale
		int minute = cal.get(Calendar.MINUTE);
		if (minute <= 30) {
			cal.set(Calendar.MINUTE, 0);
		} else {
			cal.set(Calendar.MINUTE, 30);
		}
		date = cal.getTime();
		LOG.debug("Date is : " + date);
		
		List<CheckInCheckOutHistory> locationList = addressDAO.getCheckInCheckOutHistory(date);
		int hour;
		Calendar cal11 = Calendar.getInstance();
		hour = cal11.get(Calendar.HOUR);
		
		LOG.debug("missed travel location size :" + locationList.size());
		LOG.debug("key used : " + splitKeyArray[hour].toString());
		for(CheckInCheckOutHistory location : locationList)
		{
			if(location.getCheckInAddress() == null || location.getCheckInAddress().equals("")){
	    		String address = getUserLocationInfo(location.getCheckInLatitude(), location.getCheckInLongitude(),splitKeyArray[hour].toString());
	    		location.setCheckInAddress(address);
			}
			
			if(location.getCheckOutLatitude() > 0 && location.getCheckOutLongitude() > 0){
				if(location.getCheckOutAddress() == null || location.getCheckOutAddress().equals("")){
		    		String address = getUserLocationInfo(location.getCheckOutLatitude(), location.getCheckOutLongitude(), 
		    				splitKeyArray[hour].toString());
					location.setCheckOutAddress(address);
	
				}
			}
		}
		
		LOG.debug("location list : " + locationList.size());
		addressDAO.updateCheckInCheckOutAddress(locationList);
		}
	
		public String getUserLocationInfo(final double latitude, final double longitude, final String key) throws Exception{
		    //ZERO_RESULTS
			LOG.debug("Getting Location from Google Maps: Latitude: " + latitude + ", Longitude: " + longitude +" Key : " + key);
			HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +latitude+ "," + longitude+"&key="+key);
			HttpClient client = HttpClients.createSystem();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();
			
			try {
				response = client.execute(httpGet);
				org.apache.http.HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int status;
				while ((status = stream.read()) != -1) {
				stringBuilder.append((char) status);
				}
			} catch (IOException e) {
				//LOG.error("Error Getting Location from Google Maps: ", e);
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			
			JsonObject jsonObject = new JsonObject();
			jsonObject = GsonUtil.getInstance().fromJson(stringBuilder.toString(), JsonObject.class);
			
			JsonObject location;
			String locationString = "";
			
			//Read result
			JsonArray jArray = new JsonArray();
			if (jsonObject.has("results")) {
				jArray = jsonObject.getAsJsonArray("results");
			}
			
			if (jArray != null && jArray.size() != 0) {
				// Get JSON Array called "results" and then get the 0th complete object as JSON
				location = jArray.get(0).getAsJsonObject();
				if (location.has("formatted_address")) {
				// Get the value of the attribute whose name is "formatted_string"
				locationString = location.get("formatted_address").getAsString();
				}
				//LOG.info("test", "formattted address:" + locationString);
			} 
			LOG.debug("localstring : " + locationString);
			
			return locationString;
		}
	
}
