package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;

import notification.SendNotificationException;
import notification.SendNotifications;
import notification.SendNotificationsHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.Users;
import com.bigbang.teamworksScheduler.dao.CompanyDAO;
import com.bigbang.teamworksScheduler.dao.MissedCheckInUserDAO;
import com.bigbang.teamworksScheduler.dao.TrackingDAO;
import com.bigbang.teamworksScheduler.dao.UserDAO;
import com.bigbang.teamworksScheduler.util.DateTimeFormatClass;
import com.bigbang.teamworksScheduler.util.ExportToExcel;
import com.bigbang.teamworksScheduler.util.MailUtil;
import com.bigbang.teamworksScheduler.util.Util;

public class MissedCheckInUsersServiceImpl implements MissedCheckInSchedulerService{
	
	@Autowired
	DailyData dailyData;
	
	@Autowired
	MissedCheckInUserDAO missedCheckInDAO;
	
	@Autowired
	TrackingDAO trackingDAO;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	CompanyDAO companyDAO;
	
	Logger LOG = LogManager.getLogger(MissedCheckInUsersServiceImpl.class);
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void fetchMissedCheckInUsers() throws IOException,
			NumberFormatException, SendNotificationException, NoSuchAlgorithmException, ParseException, MessagingException {
		
		System.out.println("fire sheduler for missed checkin users");
		
		Util util = new Util();
		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal.set(Calendar.SECOND, 0);
		
		Calendar cal1 = Calendar.getInstance();
		//cal1.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));

		cal1.set(Calendar.SECOND, 0);
		Date date = cal.getTime();
		Date timeDate = cal1.getTime();
		cal1.add(Calendar.HOUR, 5);
		cal1.add(Calendar.MINUTE, 30);
		timeDate = cal1.getTime();

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String timeStr = format.format(timeDate);
		System.out.println(timeStr);
		
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
		/*List<Long> companyList = new ArrayList<Long>();
		companyList.add((long) 52);
		companyList.add((long) 374);*/
	//	List<Long> companyList = missedCheckInDAO.getCompany(timeStr);
		
		List<Long> companyList = companyDAO.getAllActiveCompaniesDetails();
		//List<Long> companyList = new ArrayList<Long>();
		//companyList.add(128L);
		
//		Map<Long, List<Date>> allCompanyHoliday = missedCheckInDAO.getHolidayDates(companyList);
////		for(int i=0;i<allCompanyHoliday.size();i++){
//			for ( Map.Entry<Long, List<Date>> entry : allCompanyHoliday.entrySet()) {
//			   // long key = entry.getKey();
//			    List<Date> tab = entry.getValue();
//			    // do something with key and/or tab
//			    for(Date date_list : tab){
//			    	System.out.println("date_list :"+date_list);
//			    }
//			}
////		}
//
//		Map<Long, String> allCompanyWorkingDay = missedCheckInDAO.getWorkingDays(companyList);
//		for (Map.Entry<Long, String> entry : allCompanyWorkingDay.entrySet()) {
//			    long key = entry.getKey();
//			    String tab = entry.getValue();
//			    // do something with key and/or tab
////			    for(long date_list : tab){
//			    	System.out.println("date_list :"+tab);
////			    }
//			}
//		System.out.println();
//		
//		List<Date> dateList = new ArrayList<Date>();
//		Calendar calE = DateTimeFormatClass.getAbsoluteDate(date);
////		Calendar calEnd = DateTimeFormatClass.getAbsoluteDate(endDate);
//
//		calE.add(Calendar.DATE, 1);

		// Add list of working dates between start date and end date
//		for(int i=0;i<companyList.size();i++){
//			String companyWorking = allCompanyWorkingDay.get(companyList.get(i));
//			List<Date> holidayPerCompany = allCompanyHoliday.get(companyList);
//			if (companyWorking.contains(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()))
//					&& !holidayPerCompany.contains(cal.getTime()))
//				dateList.add(cal.getTime());
//			cal.add(Calendar.DATE, 1);
//	
//			// Check 1# Is leaves days office working days
//			if (dateList == null || dateList.size() == 0) {
//				LOG.error("Leave dates are not office working days");
//	//			throw new ApplyLeaveException(context.getMessage("leave.apply.nonWorkingDays", null, locale));
//			}
//		}
//		Date currentDate = new Date();
//		currentDate.setHours(00);
//		currentDate.setMinutes(00);
//		currentDate.setSeconds(00);
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		LOG.debug("CompanyList Size: "+companyList.size());
//		try{
//		//Get List of all the company having working day for current time
////			System.out.println("current date 00: "+currentDate);
////		List<Long> filteredCompanyIDList = companyService.workingCompaniesList(companyList, currentDate);
//		
//		System.out.println("List of working companies: "+filteredCompanyIDList);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		System.out.println("end");
//		LOG.info("companylist  : " + companyList.size());
//		companyList.removeAll(dailyData.getCompanyNotWorking());
//		
//		LOG.debug("List of company with after removing from list : " + companyList);
//		//companyList.forEach(id -> LOG.debug(id));
//		LOG.debug("List of company : " + companyList.size());
//		if (companyList.size() == 0) {
//			LOG.info("No company exit : " + timeStr);
//			return;
//		}
		
		//List<Long> usersList = missedCheckInDAO.getCompanySuperAdmin(companyList);
	//	LOG.debug("users list : " + usersList.size());
		
	//	Map<Long, String> loggedInUser = trackingDAO.getLoggedInUsersList(usersList);
//		LOG.debug("Logged in users : " + loggedInUser.size());
		
		//LOG.debug("Logged in users : " + loggedInUser);
//		try{
//			List<SendNotifications> notification = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
//				(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
//				(String) Properties.get("senderID")).Send(loggedInUser,
//				Integer.valueOf((String) Properties.get("MISSED_USERS_CHECKIN")), "Users not checked in today", null, 1,
//				"MISSED_USERS_CHECKIN", 0);
//			
//			LOG.debug("MISSED_USERS_CHECKIN Notification sent to " + notification.size() + " users");
//		}catch(Exception e){
//			LOG.error("Error sending missed checkin notification");
//			e.printStackTrace();
//		}
		
		String memberID = "";
		for(long companyID : companyList){
			Date currentDate = new Date();
			LOG.debug("Start Scheduler for companyID : " + companyID);
			if(companyID == 270)
			{
				//Get Super Admin of the company
				long superAdminID = userDAO.getCompanySuperAdmin(companyID);
				memberID = userDAO.getLowerHierarchy(superAdminID);
				
				List<Long> memberIDList = new ArrayList<Long>();
				   if (memberID != null && memberID.trim().length() != 0) {
				    String[] memberArray = memberID.split(",");

				    for (int i = 0; i < memberArray.length; i++) {
				     memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
				    }
			}
				//Add Super Admin to member list
				memberIDList.add(superAdminID);

				MailUtil mailUtil = new MailUtil();
				Session session =Session.getInstance(mailUtil.setMailingProperties1());
				Transport transport = session.getTransport("smtp");
				transport.connect("email-smtp.us-west-2.amazonaws.com", "AKIAJXW7T6XVNC77WM2Q", "AsNFJxvrnIjpMWABBXq0cCC3U2NIaBNRd66tfIuh9rW5");
				
			//Remove team members from the member List
				List<Long> emailMemberIDList = userDAO.removeTeamMemberList(memberIDList, companyID);   
				for(int i = 0;i<emailMemberIDList.size();i++){
					memberID = userDAO.getLowerHierarchy(emailMemberIDList.get(i));
					LOG.info("User_ID : " + emailMemberIDList.get(i));
					try{
						mailAllUserAttendance(emailMemberIDList.get(i), companyID, memberID, currentDate,"IST",transport, session,mailUtil);
					}catch(Exception e){
						LOG.info(" mail exception "+e);
					}
				}  
				transport.close();
			}
		}
	}
	
	public void mailAllUserAttendance(long adminID, long companyId,String memberID, Date currentDate,
			String timezone,Transport transport, Session session,MailUtil mailUtil) throws IOException, ParseException,
			NoSuchAlgorithmException, MessagingException {
		
		LOG.info("Generate Attendance report service method");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

	   List<Long> memberIDList = new ArrayList<Long>();
	   if (memberID != null && memberID.trim().length() != 0) {
	    String[] memberArray = memberID.split(",");

	    for (int i = 0; i < memberArray.length; i++) {
	     memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
	    }
	   }
	   
	    if (memberIDList.size() == 0) {
		   LOG.debug("No Reporting members");
			return;
		}
		List<Long> allReportingUsers = memberIDList;
		LOG.info("allReportingUsers "+allReportingUsers);
		
		//get admin/manager details
		Users adminDetails = userDAO.getUserDetails(adminID);
		memberIDList = userDAO.removeAdminUserList(memberIDList, companyId);
		LOG.info("No. of members:" + memberIDList.size());
		if (memberIDList.size() == 0) {
			return;
		}
		
		// Get reporting member details
		List<Users> members = userDAO.getUser(memberIDList);
		int managerCheckedIn = 0;
		int teamMemberCheckedIn = 0;
		for(int i=0;i<members.size();i++){
			if(members.get(i).getRoleID() == 3)
				managerCheckedIn++;
			else if(members.get(i).getRoleID() == 4)
				teamMemberCheckedIn++;
		}
		
		List<Users> ManagerInfo = userDAO.getManager(memberIDList);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String current_date = format.format(currentDate);
		
		// Get attendance for all reporting members for selected month and date
		List<Attendance> attendanceList = missedCheckInDAO.getExportNewAttendanceHistory(memberIDList, 
				companyId, current_date, current_date);

		List<Long> checkInID =  new ArrayList<Long>();
		for(Attendance att : attendanceList)
		{
			checkInID.add(att.getUser().getUserId());
		}
		
		List<Long> union = new ArrayList<Long>(memberIDList);
		union.addAll(checkInID);
		
		// Prepare an intersection
		List<Long> intersection = new ArrayList<Long>(memberIDList);
		intersection.retainAll(checkInID);
		
		// Subtract the intersection from the union
		union.removeAll(intersection);
		List<Long> removeAdmin = new ArrayList<Long>();
		int managerNotCheckedIn = 0;
		int teamMemberNotCheckedIn = 0;
		List<Users> memberNotCheckedIn = new ArrayList<Users>();
		if(union.size() > 0){
			removeAdmin = userDAO.removeAdminUserList(union, companyId);
			
			// Get reporting member details who have not checked in
			 memberNotCheckedIn = userDAO.getUser(removeAdmin);
			
			for(int i=0;i<memberNotCheckedIn.size();i++){
				if(memberNotCheckedIn.get(i).getRoleID() == 3)
					managerNotCheckedIn++;
				else if(memberNotCheckedIn.get(i).getRoleID() == 4)
					teamMemberNotCheckedIn++;
			}
		
		}
		Map<Long, Users> idToUserMap = new HashMap<Long, Users>();
		for (Users member : members) {
			idToUserMap.put(member.getUserId(), member);
		}

		Map<Long, Users> idToManagerMap = new HashMap<Long, Users>();
		for (Users manager : ManagerInfo) {
			idToManagerMap.put(manager.getUserId(), manager);
		}

		LinkedHashMap<Users, List<Attendance>> userToAttendanceMap = new LinkedHashMap<Users, List<Attendance>>();
		// Create user to attendance list mapping
		for (Attendance att : attendanceList) {
			if (userToAttendanceMap.containsKey(idToUserMap.get(att.getUser().getUserId()))) {
				List<Attendance> attList = userToAttendanceMap.get(idToUserMap.get(att.getUser().getUserId()));
				attList.add(att);
				userToAttendanceMap.put(idToUserMap.get(att.getUser().getUserId()), attList);
			} else {
				List<Attendance> attList = new ArrayList<Attendance>();
				attList.add(att);
				userToAttendanceMap.put(idToUserMap.get(att.getUser().getUserId()), attList);
			}
		}

		Company company = new Company();
		company.setCompanyID(companyId);

		ExportToExcel excel = new ExportToExcel();
		String filePath = excel.attendanceNewExcel(currentDate, timezone,
				userToAttendanceMap, company,idToManagerMap,memberNotCheckedIn,
				managerCheckedIn,managerNotCheckedIn,teamMemberCheckedIn,teamMemberNotCheckedIn);
		
		mailUtil.sendHTMLMail1(adminDetails.getEmailID(), filePath, "Attendance Report "+formatter.format(currentDate),
				"Greetings from Big Bang Innovations Pvt. Ltd.! Please find report attached.",transport);
	
		if(companyId == 270){
			mailUtil.sendHTMLMail1("hitesh.shah@bigbanginnovations.in", filePath, "Attendance Report "+formatter.format(currentDate),
					"Greetings from Big Bang Innovations Pvt. Ltd.! Please find report attached.",transport);
		}
		LOG.info("Mail Sent");
	}

	@Override
	public void sendBulkEmails() throws IOException, AddressException, MessagingException {
		
		JavaEmail javaEmail = new JavaEmail();
	
		javaEmail.setMailServerProperties();
		javaEmail.createEmailMessage();
		javaEmail.sendEmail();
		
	}

	
	
	}
