package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import notification.SendNotificationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.beans.CompanyInfo;
import com.bigbang.teamworksScheduler.beans.UserLocation;
import com.bigbang.teamworksScheduler.beans.Users;
import com.bigbang.teamworksScheduler.dao.CompanyDAO;
import com.bigbang.teamworksScheduler.dao.TrackingDAO;
import com.bigbang.teamworksScheduler.dao.UserDAO;
import com.bigbang.teamworksScheduler.util.DateTimeFormatClass;
import com.bigbang.teamworksScheduler.util.ExportToExcel;
import com.bigbang.teamworksScheduler.util.MailUtil;

public class TrackingReportSchedulerServiceImpl implements TrackingReportSchedulerService {

	@Autowired
	TrackingDAO trackingDAO;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	CompanyDAO companyDAO;
	
	Logger LOG = LogManager.getLogger(TrackingReportSchedulerServiceImpl.class);
	
	@Override
	public void autoTrackingReportSend() throws IOException,
			NumberFormatException, SendNotificationException, MessagingException, NoSuchAlgorithmException, ParseException {
		
		LOG.debug("In service method for sending daily tracking report to superAdmin/Admin");
		
		//Get List of all the companies, where we need to send the reports
//		List<Long> superAdminList = new ArrayList<Long>();
		List<Long> companyList = trackingDAO.getAllTrackingCompanies();
//		List<Long> companyList = new ArrayList<Long>();
//		companyList.add((long) 52); //big bang
//		companyList.add((long) 492);
//		companyList.add((long) 682); 
//		companyList.add((long) 524); //No reporting members yet
//		companyList.add((long) 612); //No reporting members yet
//		companyList.add((long) 374); //telenor
//		companyList.add((long) 638);
//		companyList.add((long) 611);
//		companyList.add((long) 628); //Fracton
//		companyList.add((long) 739); //Excitel
//		companyList.add((long) 690); //Shree Al Panel Pvt. Ltd.
//		companyList.add((long) 746); //finolex pipes ltd
//		companyList.add((long) 789);
//		companyList.add((long) 795);
//		for(int k=0;k<companyList.size();k++){
//			superAdminList.add(userDAO.getCompanySuperAdmin(companyList.get(k)));
//		}
		MailUtil mailUtil = new MailUtil();
		Session session = Session.getInstance(mailUtil.setMailingProperties1());
		Transport transport = session.getTransport("smtp");
		transport.connect("email-smtp.us-west-2.amazonaws.com", "AKIAJXW7T6XVNC77WM2Q", "AsNFJxvrnIjpMWABBXq0cCC3U2NIaBNRd66tfIuh9rW5");
		
		for(int k=0;k<companyList.size();k++){
			generateTrackingReport(companyList.get(k),"IST", transport,session,mailUtil);	
		}
		transport.close();

	}
	
	public void generateTrackingReport(long companyID,String timezone, Transport transport, Session session,MailUtil mailUtil) 
			throws ParseException, IOException, NoSuchAlgorithmException, MessagingException {

		LOG.debug("Calling generateTrackingReport1 service method");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		//get List of all the admin and super admin's
		List<Long> adminSuperAdminList = new ArrayList<Long>();
		adminSuperAdminList = userDAO.getAdminSuperAdmin(companyID);
		
		for(int k=0;k<adminSuperAdminList.size();k++){
			Users adminDetails = userDAO.getUserDetails(adminSuperAdminList.get(k));
			String memberID = "";
			LOG.debug("Fetch list of all reporting members");
				memberID = userDAO.getLowerHierarchy(adminSuperAdminList.get(k));
	
			List<Long> memberIDList = new ArrayList<Long>();
		    if (memberID != null && memberID.trim().length() != 0) {
		     String[] memberArray = memberID.split(",");
	
		     for (int i = 0; i < memberArray.length; i++) {
		      memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
		     }
		    }
		    
			if (memberIDList == null || memberIDList.size() == 0) {
				LOG.debug("No users reporting");
				if(adminDetails.getEmailID() != null && !adminDetails.getEmailID().equals("")){
					mailUtil.sendHTMLMail1(adminDetails.getEmailID(), null, "tracking_Report",
						"No users Reporting for Company ID "+companyID, transport,session);
				}
				return;
			}
	
			LOG.debug("No. of members:" + memberIDList.size());
	 
			CompanyInfo company = companyDAO.getCompanybyID(companyID);
			
			//Get all company working days Mon,Tue,Wed,Thu,Fri,Sat,Sun
			List<Integer> companyWorkingDaysIndex = new ArrayList<Integer>();
			if(company.getWorkingDays().contains("Sun")){
				companyWorkingDaysIndex.add(1);
			}
			if(company.getWorkingDays().contains("Mon")){
				companyWorkingDaysIndex.add(2);
			}
			if(company.getWorkingDays().contains("Tue")){
				companyWorkingDaysIndex.add(3);
			}
			if(company.getWorkingDays().contains("Wed")){
				companyWorkingDaysIndex.add(4);
			}
			if(company.getWorkingDays().contains("Thu")){
				companyWorkingDaysIndex.add(5);
			}
			if(company.getWorkingDays().contains("Fri")){
				companyWorkingDaysIndex.add(6);
			}
			if(company.getWorkingDays().contains("Sat")){
				companyWorkingDaysIndex.add(7);
			}
			
			//set working saturdays
			Map<Integer,Integer> saturdayStatus = new HashMap<Integer,Integer>();
			if(company.isSaturdayPolicy() == true){
				List<String> items = Arrays.asList(company.getWorkingSaturday().split("\\s*,\\s*"));
				for(int i= 0;i<items.size();i++){
					if(items.get(i).equalsIgnoreCase("full") || items.get(i).equalsIgnoreCase("half")){
						saturdayStatus.put(i+1,1);
					}else{
						saturdayStatus.put(i+1,0);
					}
				}
			}
					
			//get company holiday list
			boolean isNonWorkingDay = companyDAO.checkIfCompanyHoliday(companyID,currentDate);
			if(isNonWorkingDay){
				LOG.debug("Company is not working today: Holiday");
				return;
			}
			
			List<Date> companyNonWorkingDates = NotWorkingDays(currentDate, saturdayStatus, companyWorkingDaysIndex);
	
			Date date = DateTimeFormatClass.getAbsoluteDate(currentDate).getTime();
			String dateStr = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).format(date);
			date = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).parse(dateStr);
			
			if(companyNonWorkingDates.contains(date)){
				LOG.debug("Company is not working today");
				return;
			}
			
			List<UserLocation> userLocations = trackingDAO.getAll(memberIDList, formatter.format(currentDate), companyID);
			if(userLocations.size() <= 0){
				LOG.debug("No user tracking found");
				return;
			}
			
			//get user total distance Traveled per day
			List<UserLocation> allUserDistance = trackingDAO.getUserDistancePerDay(currentDate,companyID,memberIDList);
			LOG.debug("allUserDistance: "+allUserDistance.size());
			
			// Get reporting member details
			List<Users> members = userDAO.getUser(memberIDList);
					
			Map<Long, Users> idToUserMap = new HashMap<Long, Users>();
			for (Users member : members) {
				idToUserMap.put(member.getUserId(), member);
			}
			
			Date mapDate = new Date();
			Map<Long, LinkedHashMap<Date, Double>> allUserData = new HashMap<Long, LinkedHashMap<Date,Double>>();
			for(UserLocation loc : allUserDistance){
				mapDate = loc.getDate();
//			    String dateStr1 = new SimpleDateFormat(DateTimeFormatClass.DEFAULT_DATE_FORMAT_WITH_TIME).format(loc.getDate());
//			    SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormatClass.DEFAULT_DATE_FORMAT_WITH_TIME);
				LOG.debug("date: "+loc.getDate());
//				Date date1 = dateFormat.parse(dateStr1);
				if (allUserData.containsKey(loc.getUserid())) {
					LinkedHashMap<Date, Double> userData = allUserData.get(loc.getUserid());
					userData.put(loc.getDate(),loc.getDistanceValue());
					allUserData.put(loc.getUserid(), userData);
				} else {
					LinkedHashMap<Date, Double> userData =  new LinkedHashMap<Date, Double>();
					userData.put(loc.getDate(), loc.getDistanceValue());
					allUserData.put(loc.getUserid(), userData);
				}
	
			}
			LOG.debug("allUserData Size: "+allUserData.size());
			
			ExportToExcel excel = new ExportToExcel();
			String filePath = excel.trackingExcelReport(userLocations, timezone, currentDate, companyID, allUserData, idToUserMap, mapDate);
			if(adminDetails.getEmailID() != null && !adminDetails.getEmailID().equals("")){
				if(companyID == 52){
					mailUtil.sendHTMLMail1(adminDetails.getEmailID(), filePath, "tracking_Report",
							"Company ID "+companyID, transport,session); 
				}
			/*	mailUtil.sendHTMLMail1("snhsnh789@gmail.com", filePath, "Daily_Tracking_Report",
						"Your Daily Tracking Report "+companyID, transport,session); //adminDetails.getEmailID();
				
				mailUtil.sendHTMLMail1("rishimodi115@gmail.com", filePath, "tracking_Report",
						"Company ID "+companyID, transport,session);*/
			}
			
//			mailUtil.sendHTMLMail1("snhsnh789@gmail.com", filePath, "tracking_Report",
//					"Company ID "+companyID,transport,session);
			LOG.debug("Email Sent to "+adminDetails.getEmailID());
//			mailUtil.sendHTMLMail("manukhera@gmail.com", filePath, "tracking_Report",
//					"report");
		}
	}
	
 public List<Date> NotWorkingDays(Date currentDate,Map<Integer,Integer> saturdayStatus,List<Integer> companyWorkingDaysIndex) throws ParseException{
		
		int startMonth = 0;
		int startYear  = 0;
		List<Date> companyNotWorkingDays = new ArrayList<Date>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		startMonth = cal.get(Calendar.MONTH);
		startYear  = cal.get(Calendar.YEAR);
		
		// Get maximum days of month to prepare month chart in sheet
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.MONTH, startMonth);
		cal1.set(Calendar.YEAR, startYear);
		int maxDay = cal1.getActualMaximum(Calendar.DAY_OF_MONTH);
				
		int day = 1;
		int satCount = 1;
		while (day <= maxDay) {
			cal1.set(Calendar.DAY_OF_MONTH, day);
			Date date = DateTimeFormatClass.getAbsoluteDate(cal1.getTime()).getTime();
			String dateStr = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).format(date);
			date = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).parse(dateStr);

			Calendar c = Calendar.getInstance();
			c.setTime(cal1.getTime());
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if(!companyWorkingDaysIndex.contains(dayOfWeek)){
				companyNotWorkingDays.add(date);
			}
			if(saturdayStatus.size() > 0){
				if(dayOfWeek == 7){
					System.out.println("saturday status: "+saturdayStatus.get(satCount));
					if(saturdayStatus.get(satCount) == 0){
						companyNotWorkingDays.add(date);
					}	
					satCount++;
				}
			}
			
			day++;
		}
		return companyNotWorkingDays;
	}
 
 
 	public List<Date> NotWorkingDay(Date currentDate,Map<Integer,Integer> saturdayStatus,List<Integer> companyWorkingDaysIndex) throws ParseException{
		
		int startMonth = 0;
		int startYear  = 0;
		List<Date> companyNotWorkingDays = new ArrayList<Date>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		startMonth = cal.get(Calendar.MONTH);
		startYear  = cal.get(Calendar.YEAR);
		
		// Get maximum days of month to prepare month chart in sheet
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.MONTH, startMonth);
		cal1.set(Calendar.YEAR, startYear);
		int maxDay = cal1.getActualMaximum(Calendar.DAY_OF_MONTH);
				
		//		int day = 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int satCount = 1;
		while (day <= maxDay) {
			cal1.set(Calendar.DAY_OF_MONTH, day);
			Date date = DateTimeFormatClass.getAbsoluteDate(cal1.getTime()).getTime();
			String dateStr = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).format(date);
			date = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).parse(dateStr);

			Calendar c = Calendar.getInstance();
			c.setTime(cal1.getTime());
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if(!companyWorkingDaysIndex.contains(dayOfWeek)){
				companyNotWorkingDays.add(date);
			}
			if(saturdayStatus.size() > 0){
				if(dayOfWeek == 7){
					System.out.println("saturday status: "+saturdayStatus.get(satCount));
					if(saturdayStatus.get(satCount) == 0){
						companyNotWorkingDays.add(date);
					}	
					satCount++;
				}
			}
			
			day++;
		}
		return companyNotWorkingDays;
	}

}
