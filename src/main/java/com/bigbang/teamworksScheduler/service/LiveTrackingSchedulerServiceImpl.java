package com.bigbang.teamworksScheduler.service;

import handleException.LiveTrackingException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import notification.SendNotificationException;
import notification.SendNotifications;
import notification.SendNotificationsHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.beans.UserLiveTrackingStatus;
import com.bigbang.teamworksScheduler.beans.Users;
import com.bigbang.teamworksScheduler.dao.LiveTrackingSchedulerDAO;
import com.bigbang.teamworksScheduler.util.GsonUtil;
import com.bigbang.teamworksScheduler.util.Util;
import com.google.gson.Gson;

public class LiveTrackingSchedulerServiceImpl implements LiveTrackingSchedulerService {

	@Autowired
	LiveTrackingSchedulerDAO schedulerDAO;
	@Autowired
	DailyData dailyData;
	
	Logger LOG = LogManager.getLogger(LiveTrackingSchedulerServiceImpl.class);
	
	/**
	 * This service method will send check-out notification to users who have checked in but currently not checked out
	 * for the company which has current time as company end time
	 * @throws SendNotificationException 
	 * @throws NumberFormatException 
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void autoStopLiveTracking() throws LiveTrackingException,IOException, NumberFormatException, SendNotificationException {

		LOG.debug("Auto Live Tracking Stop service class");
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
//		List<Long> companyList = new ArrayList<Long>();
//		companyList.add(Long.parseLong("312"));
//        
//		for(int i=0;i<companyList.size();i++){
//			LOG.debug("company ID "+companyList.get(i));
//		}
		format = new SimpleDateFormat("yyyy-MM-dd");
		
		//Get List of live Tracking status class with list of users
		List<UserLiveTrackingStatus> userLiveTrackingList = schedulerDAO.getLiveTrackingOnUsers(companyList);


		for(int j = 0;j < userLiveTrackingList.size();j++){
			Users user   = schedulerDAO.getUserDetails(userLiveTrackingList.get(j).getUserID());
			Users sender = schedulerDAO.getUserDetails(userLiveTrackingList.get(j).getCreatedBy());
			
			Map<String, Object> map = new HashMap<String, Object>();
            
			map.put("FirstName", sender.getFirstName());
			map.put("LastName", sender.getLastName());
			map.put("Picture", sender.getPicture());
			map.put("ID", sender.getUserId());
			map.put("isLiveTracking", 0); //for stoping tracking
			map.put("liveTrackStatusID", userLiveTrackingList.get(j).getID());

			String message = "Your live Tracking is stopped by"+sender.getFirstName()+" "+sender.getLastName(); 
			List<Users> usersList =  new ArrayList<Users>();
			usersList.add(user);
			sendLiveTrackingNotification(map,message, sender, usersList,
					Integer.valueOf((String) Properties.get("Start_Stop_Live_Tracking")), 
					"AUTO_LIVE_TRACKING_STOP", userLiveTrackingList.get(j).getCompanyID(),
					1);
			
			int isSuccess = schedulerDAO.UpdateUserStatus(userLiveTrackingList.get(j).getID());
			LOG.debug("AUTO LIVE TRACKING STOPED OF USER: "+user.getUserId()+" success "+isSuccess);
		}
		
		
//		List<SendNotifications> not = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
//				(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
//				(String) Properties.get("senderID")).Send(autoLiveTrackingStopUser,
//
//		Integer.valueOf((String) Properties.get("Start_Stop_Live_Tracking")), "Your live Tracking is Stopped", null, 1,
//				"AUTO_LIVE_TRACKING_STOP", 0);
//
//		LOG.info("Auto Checkout Notification sent to " + not.size() + "users");
	}

	public void sendLiveTrackingNotification(Map<String, Object> map, String message, Users sender, List<Users> userList, int type,
			String transactionID, long companyID, int status) throws IOException, SendNotificationException {

		LOG.info("Sending update chat notification to members");
		Map<Long, String> gcmIDMap = new HashMap<Long, String>();

		for (Users user : userList) {
			 LOG.info("device id : " + user.getDeviceid());
			 LOG.info("userid : " + user.getUserId());
			if (user.getDeviceid() == null) {
				user.setDeviceid("");
			}
			gcmIDMap.put(user.getUserId(), user.getDeviceid());

		}

		Gson gson = GsonUtil.getInstance();

		if (gcmIDMap != null && gcmIDMap.size() != 0) {
			// Send notification to all users
			List<SendNotifications> notifications = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
					(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"), 
					(String) Properties.get("senderID")).Send(gcmIDMap,
					type, message, gson.toJsonTree(map), status, transactionID, companyID);
			LOG.info("Auto Checkout Notification sent to " + notifications.size() + "users");		
			}
	}

	
}
