package com.bigbang.teamworksScheduler.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import notification.SendNotificationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAO;
import com.bigbang.teamworksScheduler.dao.LeaveSchedulerDAO;
import com.bigbang.teamworksScheduler.util.Util;

public class HolidaySchedulerServiceImpl implements HolidaySchedulerService {

	@Autowired
	DailyData dailyData;
	@Autowired
	LeaveSchedulerDAO leaveSchedulerDAO;
	@Autowired
	HolidaySchedulerDAO holidaySchedulerDAO;

	/**
	 * This service method will send check-in notification to the users who have not checked in for company which has
	 * current time as start time
	 * 
	 */

	Logger LOG = LogManager.getLogger(HolidaySchedulerServiceImpl.class);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void autoHolidayUpdate() throws SendNotificationException {

		LOG.debug("Auto Holiday Update service class");
		Util util = new Util();

		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal.set(Calendar.SECOND, 0);

		Date date = cal.getTime();

		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);

		date = cal.getTime();

		//date = new Date("2017/11/26");
		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		// Get list of company that are non working
		List<Long> companyList = dailyData.getCompanyNotWorking();

		LOG.info("Company not working today are: " + companyList);

		// Get List of company users
		Map<Long, Long> userCompanyMap = holidaySchedulerDAO.getCompanyUsers(companyList);

		LOG.info("Adding holiday for users: " + userCompanyMap.keySet());
		// Add holiday into attendance table
		holidaySchedulerDAO.addAttendance(userCompanyMap, Constants.LEAVE_TYPE_HOLIDAY, date);

		LOG.info("Holiday added successfully");
	}

}
