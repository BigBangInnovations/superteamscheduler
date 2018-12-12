package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import notification.SendNotificationException;
import notification.SendNotifications;
import notification.SendNotificationsHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.beans.CompanyLeaves;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.dao.LeaveSchedulerDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LeaveSchedulerServiceImpl implements LeaveSchedulerService {

	@Autowired
	LeaveSchedulerDAO leaveSchedulerDAO;

	Logger LOG = LogManager.getLogger(LeaveSchedulerServiceImpl.class);

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//	public void autoLeaveUpdate() throws IOException, SendNotificationException {
//		LOG.debug("Auto Leave update service class");
//        LOG.debug("auto leave update called");
//		// Get list of users who have leave balance added into database
//		List<Long> userLeaveBalList = leaveSchedulerDAO.getUserFromLeaveBal();
//		LOG.debug("Leave banace set to 0 for usersId: ");
//		// Get Map of all users vs Company who have eligible for leave balance update
//		Map<Long, Long> userCompanyMap = leaveSchedulerDAO.getMemberCompanyMap();
//		LOG.debug("userCompanyMap: " + userCompanyMap);
//		for (Long userIDExist : userLeaveBalList) {
//			userCompanyMap.remove(userIDExist);
//		}
//		LOG.debug("after for loop: ");
//		// Add leave balance entry for users who do not have any leave balance currently
//		String userIdStr = leaveSchedulerDAO.addLeaveBalance(userCompanyMap);
//		LOG.debug("Leave banace set to 0 for usersId: " + userIdStr);
//
//		// ---Start update of leaves balance-----
//
//		List<String> leaveUpdateCycle = new ArrayList<String>();
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeZone(TimeZone.getTimeZone("IST"));
//		int month = cal.get(Calendar.MONTH) + 1;
//		int year = cal.get(Calendar.YEAR);
////		System.out.println(month);
////		System.out.println(year);
//		// Add leave log for new month
//		leaveSchedulerDAO.addAutoLeaveLog(month, year);
//
//		// Update leave cycle based on month.
//		leaveUpdateCycle.add("Monthly");
//		if ((month % 3) == 0) {
//			leaveUpdateCycle.add("Quarterly");
//		}
//		if (month == 12) {
//			leaveUpdateCycle.add("Annual");
//		}
//
//		// Get list of company for which auto leave scheduler is active
//		List<Long> companyList = leaveSchedulerDAO.getAutoLeaveUpdateCompany();
//		LOG.debug(companyList);
//		// Get list of company for for the cycle
//		List<CompanyLeaves> companyLeavesList = leaveSchedulerDAO.getCompanyLeaves(companyList, leaveUpdateCycle);
//
//		companyList.clear();
//
//		// update auto leave balance for all the company users
//		for (CompanyLeaves companyLeaves : companyLeavesList) {
//			leaveSchedulerDAO.updateAutoLeaveBal(companyLeaves.getNoOfLeaves(), companyLeaves.getCompanyId(),
//					companyLeaves.getLeaveTypeId());
//			leaveSchedulerDAO.updateAutoLeaveLog(month, year, companyLeaves);
//			companyList.add(companyLeaves.getCompanyId());
//		}
//
//		// Get all company user list for whom auto leave has been updated
//		List<User> userList = leaveSchedulerDAO.getUser(month, year);
//
//		List<SendNotifications> notificationList = new ArrayList<SendNotifications>();
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("FirstName", "System");
//		map.put("LastName", "Update");
//		map.put("ID", 0);
//		map.put("Picture", "");
//
//		Gson gson = new GsonBuilder().create();
//		for (User user : userList) {
//			SendNotifications notification = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
//					(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
//					(String) Properties.get("senderID")).Send(user.getGcmID(), Constants.UPDATE_LEAVE_TYPE,
//					Constants.UPDATE_LEAVE_MESSAGE, gson.toJsonTree(map), Constants.NOTIFICATION_STATUS_ALERT, "T"
//							+ month + "_" + year + "_Auto", user.getUserID(), user.getCompany().getCompanyID());
//			notificationList.add(notification);
//		}
//
//		LOG.info("Notification sent to " + notificationList.size() + "users");
//		// Add notification to database
//		leaveSchedulerDAO.addNotifications(notificationList);
//	}
	public void autoLeaveUpdate() throws IOException, SendNotificationException {
		LOG.debug("Auto Leave update service class");
		LOG.debug("auto leave update called");
		// Get list of users who have leave balance added into database
		List<Long> userLeaveBalList = leaveSchedulerDAO.getUserFromLeaveBal();
		LOG.debug("Leave banace set to 0 for usersId: ");
		// Get Map of all users vs Company who have eligible for leave balance update
		Map<Long, Long> userCompanyMap = leaveSchedulerDAO.getMemberCompanyMap();
		LOG.debug("userCompanyMap: " + userCompanyMap);
		for (Long userIDExist : userLeaveBalList) {
			userCompanyMap.remove(userIDExist);
		}
		LOG.debug("after for loop: ");
		// Add leave balance entry for users who do not have any leave balance currently
		String userIdStr = leaveSchedulerDAO.addLeaveBalance(userCompanyMap);
		LOG.debug("Leave banace set to 0 for usersId: " + userIdStr);

		// ---Start update of leaves balance-----

		List<String> leaveUpdateCycle = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("IST"));
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		LOG.debug(month);
		LOG.debug(year);
		// Add leave log for new month
		leaveSchedulerDAO.addAutoLeaveLog(month, year);

		// Update leave cycle based on month.
		leaveUpdateCycle.add("Monthly");
		if ((month % 3) == 0) {
			leaveUpdateCycle.add("Quarterly");
		}
		if (month == 12) {
			leaveUpdateCycle.add("Annual");
		}

		// Get list of company for which auto leave scheduler is active
		List<Long> companyList = leaveSchedulerDAO.getAutoLeaveUpdateCompany();
		LOG.debug(companyList);
		// Get list of company for for the cycle
		List<CompanyLeaves> companyLeavesList = leaveSchedulerDAO.getCompanyLeaves(companyList, leaveUpdateCycle);

		companyList.clear();

		// update auto leave balance for all the company users
		for (CompanyLeaves companyLeaves : companyLeavesList) {
			leaveSchedulerDAO.updateAutoLeaveBal(companyLeaves.getNoOfLeaves(), companyLeaves.getCompanyId(),
					companyLeaves.getLeaveTypeId());
			leaveSchedulerDAO.updateAutoLeaveLog(month, year, companyLeaves);
			companyList.add(companyLeaves.getCompanyId());
		}

		// Get all company user list for whom auto leave has been updated
		List<User> userList = leaveSchedulerDAO.getUser(month, year);

		List<SendNotifications> notificationList = new ArrayList<SendNotifications>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("FirstName", "System");
		map.put("LastName", "Update");
		map.put("ID", 0);
		map.put("Picture", "");

		Gson gson = new GsonBuilder().create();
		for (User user : userList) {
			SendNotifications notification = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
					(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
					(String) Properties.get("senderID")).Send(user.getGcmID(), Constants.UPDATE_LEAVE_TYPE,
					Constants.UPDATE_LEAVE_MESSAGE, gson.toJsonTree(map), Constants.NOTIFICATION_STATUS_ALERT, "T"
							+ month + "_" + year + "_Auto", user.getUserID(), user.getCompany().getCompanyID());
			notificationList.add(notification);
		}

		LOG.info("Notification sent to " + notificationList.size() + "users");
		// Add notification to database
		leaveSchedulerDAO.addNotifications(notificationList);
	}
}
