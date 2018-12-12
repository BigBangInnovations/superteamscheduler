package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.dao.AddressDAO;
import com.bigbang.teamworksScheduler.dao.TrackingDAO;
import com.bigbang.teamworksScheduler.util.Util;

import notification.SendNotificationException;
import notification.SendNotifications;
import notification.SendNotificationsHelper;

public class TrackingSchedulerServiceImpl implements TrackingSchedulerService {

	@Autowired
	DailyData dailyData;
	
	@Autowired
	TrackingDAO trackingDAO;
	
	@Autowired
	AddressDAO addressDAO;
	
	@Autowired
	AddressSchedulerService updateAddressService;
	
	Logger LOG = LogManager.getLogger(TrackingSchedulerServiceImpl.class);
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void autoTrackingUpdate() throws IOException, NumberFormatException, SendNotificationException {
		LOG.debug("fire sheduler after 10 min");
		
		Util util = new Util();

		Calendar cal = Calendar.getInstance();
//		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal.set(Calendar.SECOND, 0);
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal1.set(Calendar.SECOND, 0);
		
		Date date = cal.getTime();
		
		Date timeDate = cal1.getTime();
		 
//		cal1.add(Calendar.HOUR, 5);
//		cal1.add(Calendar.MINUTE, 30);
		
//		timeDate = cal1.getTime();

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String timeStr = format.format(timeDate);
		LOG.debug(timeStr);
		
		// Check if daily data is not stale
		int minute = cal.get(Calendar.MINUTE);
		if(minute < 29)
		{
			cal.set(Calendar.MINUTE, 0);
		}
		else
		{
			cal.set(Calendar.MINUTE, 30);
		}
		date = cal.getTime();
		LOG.debug("Date is : " + date);
		
		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.debug("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.debug("Daily data intializes successfully");
		}
		 
		// Get list of company which start time is less than current time and end time is greater than current time
		LOG.debug("current time to get list of company from company time: "+timeStr);
		List<Long> companyList = trackingDAO.getCompany(timeStr);
		
		LOG.debug("companylist  : " + companyList.size());

		companyList.removeAll(dailyData.getCompanyNotWorking());
		
		LOG.debug("List of company with after removing from list : " + companyList);
		//companyList.forEach(id -> LOG.debug(id));
		
		if (companyList.size() == 0) {
			LOG.info("No company exit : " + timeStr);
			return;
		}
		
		List<Long> usersList = trackingDAO.getCompanyUsers(companyList);
		
		LOG.debug("DATE to get tracking users list : "+date);
		if(usersList.size() > 0)
		{
			List<Long> fetchTrackedUsersList = trackingDAO.getCompanyTrackingUsers(usersList,date);
		
			LOG.debug("List fetch tracking Users : " + fetchTrackedUsersList.size());
			usersList.removeAll(fetchTrackedUsersList);
		}
		else
		{
			LOG.info("No users exits : " + usersList.size());
		}
		LOG.debug("total missed users size : " + usersList);
		// Get list of company users who have logged in yet 
		
		/*for(Long userID : usersList)
		{
			List<Long> usersList1 = new ArrayList<Long>();
			for(int i = 0; i<10; i++)
			{
				usersList1.add(userID);
			}
		}*/
		int startLimit = 1;
		int endLimit = 100;
		LOG.debug("for loop count: "+(usersList.size()/100));
		for(int i = 1; i<(usersList.size()/100);i++)
		{
			List<Long> userList1 = usersList.subList(startLimit, endLimit);
			startLimit = endLimit + 1;
			endLimit = startLimit + 99;
			LOG.debug("startLimit: "+startLimit+" endLimit: "+endLimit);
			
			LOG.debug("UserList in Limit: "+userList1);
			Map<Long, String> loggedInUser = trackingDAO.getLoggedInUsersList(userList1);
			
			LOG.debug("Logged in users : " + loggedInUser.size());
			
			LOG.debug("Logged in users : " + loggedInUser);
			

			List<SendNotifications> notification = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
					(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
					(String) Properties.get("senderID")).Send(loggedInUser,
					Integer.valueOf((String) Properties.get("AUTO_TRACKING")), "", null, 1,
					"AUTO_TRACKING", 0);
			LOG.debug("Auto Tracking Notification sent to " + notification.size() + " users");
			
		}
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	try {
							updateAddressService.autoUpdatAddress();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		        }, 
		        100000 
		);
		
	}
}
