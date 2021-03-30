package com.bigbang.teamworksScheduler.service;

import handleException.GetHierarchyException;
import handleException.NotifyHierarchyException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import notification.SendNotificationException;
import notification.SendNotifications;
import notification.SendNotificationsHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.CompanyAttendance;
import com.bigbang.teamworksScheduler.beans.Holidays;
import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.beans.UserAttendance;
import com.bigbang.teamworksScheduler.beans.Users;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.CompanyDAO;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAO;
import com.bigbang.teamworksScheduler.dao.UserAttendanceDAO;
import com.bigbang.teamworksScheduler.dao.UserDAO;
import com.bigbang.teamworksScheduler.util.DateTimeFormatClass;
import com.bigbang.teamworksScheduler.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.log.Log;

public class AdvanceSchedularForAttendanceServiceImpl implements AdvanceSchedularForAttendanceService 
{
	@Autowired
	AttendanceSchedulerDAO schedulerDAO;

	@Autowired
	DailyData dailyData;

	@Autowired
	UserDAO userDAO;

	@Autowired
	CompanyDAO companyDAO;

	@Autowired
	UserAttendanceDAO userAttendanceDAO;

	@Autowired
	HolidaySchedulerDAO holidaySchedulerDAO;

	Logger LOG = LogManager
			.getLogger(AdvanceSchedularForAttendanceServiceImpl.class);

	public void runDailySchedular(long companyIDs, long userIDs,Date startDate, Date endDate) throws ParseException 
	{
		LOG.info("Service function which executing Daily Schedular for Attendance");
		Calendar cal = Calendar.getInstance();
		Util util = new Util();

		SimpleDateFormat format1 = new SimpleDateFormat(DateTimeFormatClass.DATE_FORMAT);
		String start = format1.format(startDate);
		String end = format1.format(endDate);
		Date startD = format1.parse(start);
		Date endD = format1.parse(end);

		List<Date> dateListOfRequest = Util.getDaysBetweenStartDateAndEndDate(startD, endD);

		for (Date d : dateListOfRequest) 
		{
			cal.setTime(util.getTimeZoneDate(d,(String) Properties.get("teamworks.scheduler.timezone")));
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.AM_PM, Calendar.AM);
			Date date = cal.getTime();
			long dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

			// Check if daily data is not stale
			if (dailyData.getDate() == null || date.after(dailyData.getDate())) 
			{
				LOG.info("Daily Data is stale. Re-initiaizing data");
				dailyData.initailizeDailyData();
				LOG.info("Daily data intializes successfully");
			}

			List<String> status = new ArrayList<String>();
			status.add(Constants.LEAVE_APPROVED);

			List<Date> dateList = new ArrayList<Date>();
			dateList.add(date);

			// status.add(Constants.LEAVE_PENDING);

			// List<Long> activeCompany = schedulerDAO.getActiveCompany();

			// List<Long> companyList =
			// companyDAO.getAllActiveCompaniesDetails(); // MasterSchema3
			List<Long> companyList = Arrays.asList(new Long(companyIDs)); // MasterSchema
			LOG.info("companyList:-"+companyList);
			LOG.info("userIDs:-"+userIDs);
			// List<Long> companyList = Arrays.asList(new Long(52));

			// List<Long> memberIDList = new ArrayList<Long>();
			Map<Long, Long> userCompanyMap = new HashMap<Long, Long>();
			if (userIDs == 0)
			{
				fillingUserCompanyMap(companyList, userCompanyMap);
			} 
			else
			{
				userCompanyMap.put(new Long(userIDs), new Long(companyIDs));
			}
			// userCompanyMap.put(new Long(583), new Long(329));
			// userCompanyMapForMasterSchema.put(new Long(586), new Long(329));

			// Get list of company that are non working
			// List<Long> companyNotWorkingList =
			// dailyData.getCompanyNotWorking();

			// Get List of active company users who have attendance entry for
			// date 

			// SimpleDateFormat formatForTime = new
			// SimpleDateFormat("HH:mm:ss");
			// List<Long> checkedInUserList =
			// schedulerDAO.getcheckedInUser(companyList, format.format(date));
			//
			// if (checkedInUserList.size() == 0) {
			// checkedInUserList.add(Long.parseUnsignedLong("0"));
			// }
			// Get list of active company user who do not have attendance entry
			// for the date
			// Map<Long, Long> absentUsers =
			// schedulerDAO.getAbsentUserCompanyMap(companyList,
			// checkedInUserList);

			List<Holidays> listOfHolidaysOfAllCompanies = holidaySchedulerDAO.getAllHolidaysOfAllCompany(companyList, date);

			Map<Long, Long> branchIDsOfAllUses = userDAO.getBranchIDOfAllActiveCompanies(companyList);

			SimpleDateFormat weekDayformatter = new SimpleDateFormat("EE");
			weekDayformatter.setTimeZone(TimeZone.getTimeZone((String) Properties.get("teamworks.scheduler.timezone")));
			String dayOfWeekInString = weekDayformatter.format(cal.getTime());

			for (Map.Entry<Long, Long> map : userCompanyMap.entrySet()) 
			{
				long userID = map.getKey();
				long companyID = map.getValue();
				boolean byPassAttendanceProcess = true;

				LOG.info("START companyID is :-" + companyID + " And UserID:-"+ userID);
				boolean coundTimeDifference = false;
				UserAttendance userAttendance = new UserAttendance();
				userAttendance.setUserID(userID);
				userAttendance.setCompanyID(companyID);

				try 
				{
					mainLogicOfAttendance(cal, date, dayOfWeek, status, dateList,listOfHolidaysOfAllCompanies, branchIDsOfAllUses,
					dayOfWeekInString, userID, companyID,byPassAttendanceProcess, coundTimeDifference,userAttendance);
				}
				catch (IOException e)
				{
					LOG.info("IOException in mainLogicOfAttendance");
					e.printStackTrace();
				} 
				catch (SendNotificationException e) 
				{
					LOG.info("SendNotificationException in mainLogicOfAttendance");
					e.printStackTrace();
				}
			}
		}
		// START work for Weekly Schedular if(dayOfWeek == Calendar.SATURDAY)
		// if(dayOfWeek == Calendar.SUNDAY)
		// {
		// weeklySchedularExecution(date);
		// }
	}

	public void runDailySchedularForAllCompanyExceptSchbang() throws ParseException 
	{
		LOG.info("Service function which executing Daily Schedular for Attendance");
		Calendar cal = Calendar.getInstance();
		Util util = new Util();

//		SimpleDateFormat format1 = new SimpleDateFormat(DateTimeFormatClass.DATE_FORMAT);
//		String start = "2018/10/03";
//		String end = "2018/10/06";
//		Date startD = format1.parse(start);
//		Date endD = format1.parse(end);

//		List<Date> dateListOfRequest = Util.getDaysBetweenStartDateAndEndDate(startD, endD);
//
//		for (Date d : dateListOfRequest) 
//		{
			Date d = new Date(); // for Current Date
			//cal.setTime(util.getTimeZoneDate(d,(String) Properties.get("teamworks.scheduler.timezone")));
			cal.setTime(d);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.AM_PM, Calendar.AM);
			Date date = cal.getTime();
			long dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

			// Check if daily data is not stale
//			if (dailyData.getDate() == null || date.after(dailyData.getDate())) 
//			{
//				LOG.info("Daily Data is stale. Re-initiaizing data");
//				dailyData.initailizeDailyData();
//				LOG.info("Daily data intializes successfully");
//			}

			List<String> status = new ArrayList<String>();
			status.add(Constants.LEAVE_APPROVED);

			List<Date> dateList = new ArrayList<Date>();
			dateList.add(date);

			List<Long> companyList = companyDAO.getAllActiveCompaniesDetails(); // MasterSchema3
			companyList.remove(new Long(1459));

			//List<Long> companyList = Arrays.asList(new Long(52)); // MasterSchema
			// List<Long> companyList = Arrays.asList(new Long(52));

			// List<Long> memberIDList = new ArrayList<Long>();
			Map<Long, Long> userCompanyMap = new HashMap<Long, Long>();
			fillingUserCompanyMap(companyList, userCompanyMap);

			List<Holidays> listOfHolidaysOfAllCompanies = holidaySchedulerDAO.getAllHolidaysOfAllCompany(companyList, date);

			Map<Long, Long> branchIDsOfAllUses = userDAO.getBranchIDOfAllActiveCompanies(companyList);

			SimpleDateFormat weekDayformatter = new SimpleDateFormat("EE");
			weekDayformatter.setTimeZone(cal.getTimeZone());
			String dayOfWeekInString = weekDayformatter.format(cal.getTime());

			for (Map.Entry<Long, Long> map : userCompanyMap.entrySet()) 
			{
				long userID = map.getKey();
				long companyID = map.getValue();
				boolean byPassAttendanceProcess = true;

				LOG.info("START companyID is :-" + companyID + " And UserID:-"+ userID);
				boolean coundTimeDifference = false;
				UserAttendance userAttendance = new UserAttendance();
				userAttendance.setUserID(userID);
				userAttendance.setCompanyID(companyID);

				try 
				{
					mainLogicOfAttendance(cal, date, dayOfWeek, status, dateList,listOfHolidaysOfAllCompanies, branchIDsOfAllUses,
					dayOfWeekInString, userID, companyID,byPassAttendanceProcess, coundTimeDifference,userAttendance);
				} 
				catch (IOException e)
				{
					schedulerDAO.addFailedAttendanceSchedular(userID,companyID,date);
					LOG.info("IOException occurred :- "+e);
				} 
				catch (SendNotificationException e) 
				{
					schedulerDAO.addFailedAttendanceSchedular(userID,companyID,date);
					LOG.info("SendNotificationException occurred :- "+e);
				}
				catch(Exception e)
				{
					schedulerDAO.addFailedAttendanceSchedular(userID,companyID,date);
					LOG.info("Exception occurred :- "+e);
				}
			}
		//}
		// START work for Weekly Schedular if(dayOfWeek == Calendar.SATURDAY) or if month end
//		 if((dayOfWeek == Calendar.SUNDAY) || (cal.get(Calendar.DATE) == cal.getActualMaximum(Calendar.DATE)))
//		 {
//			 weeklySchedularExecution(date);
//		 }
	}

	/**
	 * Main Logic Of Attendance
	 * 
	 * @param cal
	 * @param date
	 * @param dayOfWeek
	 * @param status
	 * @param dateList
	 * @param listOfHolidaysOfAllCompanies
	 * @param branchIDsOfAllUses
	 * @param dayOfWeekInString
	 * @param userID
	 * @param companyID
	 * @param byPassAttendanceProcess
	 * @param coundTimeDifference
	 * @param userAttendance
	 * @throws ParseException
	 * @throws SendNotificationException 
	 * @throws IOException 
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void mainLogicOfAttendance(Calendar cal, Date date, long dayOfWeek,List<String> status, List<Date> dateList,
			List<Holidays> listOfHolidaysOfAllCompanies,Map<Long, Long> branchIDsOfAllUses, String dayOfWeekInString,
			long userID, long companyID, boolean byPassAttendanceProcess,boolean coundTimeDifference, UserAttendance userAttendance)
			throws ParseException, IOException, SendNotificationException 
	{

		// filter holidays for the specific Company
		List<Holidays> listOfHolidaysForSpecificCompany = listOfHolidaysOfAllCompanies.stream().filter(holiday -> (holiday.getCompanyID() == companyID)).collect(Collectors.toList());

		// filter holidays for the specific Company
		List<Holidays> listOfHolidaysForNational = listOfHolidaysForSpecificCompany.stream().filter(holiday -> (holiday.isNationalHoliday() == true)).collect(Collectors.toList());

//		Map<String, Long> leaveData = schedulerDAO.getLeaveExistingWithType(userID, companyID,date, status, Constants.LEAVE_DAY_FULL);
//		long isUserOnLeave = leaveData.get("count");
		int isUserOnLeave = schedulerDAO.isLeaveExisting(userID, companyID,date, status, Constants.LEAVE_DAY_FULL);
		LOG.info(Constants.LEAVE_DAY_FULL+":"+"userID:-"+userID+":"+"Leave:-"+isUserOnLeave);
		
		Attendance attendance = schedulerDAO.getUserAttendance(userID,companyID, date);
		//Changes for pending case need to consider in Attendance
		//Map<String, Date> firstCheckInAndLastCheckOut = getFirstCheckInAndLastCheckOutNEW(attendance);
		Map<String, Date> firstCheckInAndLastCheckOut = getFirstCheckInAndLastCheckOutThird(attendance);

		Date firstCheckIn = firstCheckInAndLastCheckOut.get("firstCheckIn");
		Date lastCheckOut = firstCheckInAndLastCheckOut.get("lastCheckOut");

		if (isUserOnLeave > 0) 
		{
			if (firstCheckIn == null && lastCheckOut == null)
			{
				byPassAttendanceProcess = false;
			}
			userAttendance.setOnLeave(true);
			//userAttendance.setLeaveTypeID(leaveData.get("leaveType"));
		} 
		else if (listOfHolidaysForNational.size() > 0) 
		{
			if (firstCheckIn == null && lastCheckOut == null)
			{
				byPassAttendanceProcess = false;
			}
			userAttendance.setHoliday(true);
			// Add holiday into attendance table
			holidaySchedulerDAO.addAttendanceForUser(userID, companyID,Constants.LEAVE_TYPE_HOLIDAY, date);
			// SKIP process Attendance , Go for insert record
		}
		else if (listOfHolidaysForNational.size() <= 0) 
		{
			if (branchIDsOfAllUses != null && branchIDsOfAllUses.size() > 0) 
			{
				if (branchIDsOfAllUses.containsKey(userID)) 
				{
					long branchID = branchIDsOfAllUses.get(userID);
					List<Holidays> onBranchHolidays = listOfHolidaysForSpecificCompany.stream().filter(holiday -> (holiday.getBranchID() == branchID)).collect(Collectors.toList());
					if (onBranchHolidays.size() > 0) 
					{
						if (firstCheckIn == null && lastCheckOut == null)
						{
							byPassAttendanceProcess = false;
						}
						// Add holiday into attendance table
						userAttendance.setHoliday(true);
						holidaySchedulerDAO.addAttendanceForUser(userID,companyID, Constants.LEAVE_TYPE_HOLIDAY, date);
						// SKIP process Attendance , Go for insert record
					}
				}
			}
		}

		if (byPassAttendanceProcess) {

			if (firstCheckIn == null && lastCheckOut == null)
			{
				// Add ABSENT Entry into attendance table
				boolean checkAbsent = true;
				long shiftID = userDAO.getShiftIDOfUser(userID);
				CompanyAttendance companyAttendanced = userDAO.getShiftDetailOfUser(shiftID, companyID);
				if (companyAttendanced != null)
				{
					String workingDays = companyAttendanced.getWorkingDays();
					if (workingDays != null) 
					{
						String arr[] = workingDays.split(",");
						List<String> listString = new ArrayList<String>(Arrays.asList(arr));
						boolean conainsOf = listString.contains(dayOfWeekInString);
						if (conainsOf) 
						{
							if (companyAttendanced.isSaturdayPolicy())
							{
								if (dayOfWeek == Calendar.SATURDAY) 
								{
									String workingSaturday = companyAttendanced.getWorkingSaturday();
									String saturdayArr[] = workingSaturday.split(",");
									boolean isSaturdayNotWorking = isWorkingOnThisSaturday(cal, saturdayArr);
									if (isSaturdayNotWorking)
									{
										checkAbsent = false;
										userAttendance.setHoliday(true);
										holidaySchedulerDAO.addAttendanceForUser(userID,companyID,Constants.LEAVE_TYPE_HOLIDAY,date);
									}
								}
							}
						}
						else
						{
							checkAbsent = false;
							userAttendance.setHoliday(true);
							holidaySchedulerDAO.addAttendanceForUser(userID,companyID, Constants.LEAVE_TYPE_HOLIDAY,date);
						}
					}
				}

				if (checkAbsent)
				{
					List<String> statusManual = new ArrayList<String>();
					statusManual.add("1");
					
					int isUserOnHalfLeave = schedulerDAO.isLeaveExisting(userID, companyID,date, status, Constants.LEAVE_DAY_HALF);
					LOG.info(Constants.LEAVE_DAY_HALF+":"+"userID:-"+userID+":"+"Leave:-"+isUserOnHalfLeave);
					
					int isUserOnHalfManual = schedulerDAO.isManualApproved(userID,companyID,dateList,statusManual,Constants.MANUAL_DAY_HALF);
					int isUserOnFullManual = schedulerDAO.isManualApproved(userID,companyID,dateList,statusManual,Constants.MANUAL_DAY_FULL);
					
					if(isUserOnHalfLeave > 0 && isUserOnHalfManual > 0)
					{
						userAttendance.setStatus(Constants.DISPLAY_HALF_LEAVE_AND_HALF_MANUAL);
					}
					else if(isUserOnHalfLeave > 0)
					{
						userAttendance.setStatus(Constants.DISPLAY_HALF_LEAVE);
					}
					else if(isUserOnHalfManual > 0)
					{
						userAttendance.setStatus(Constants.DISPLAY_MANUAL_DAY_HALF);
					}
					else if(isUserOnFullManual > 0)
					{
						userAttendance.setStatus(Constants.DISPLAY_MANUAL_DAY_FULL);
					}
					else
					{
						userAttendance.setAbsent(true);
						holidaySchedulerDAO.addAttendanceForUser(userID, companyID,Constants.LEAVE_TYPE_ABSENT, date);
					}
				}
			} 
			else
			{
				boolean entry = true;

				if (firstCheckIn == null || lastCheckOut == null)
				{
					entry = false;
				}

				if (entry)
				{
					boolean goesForApprovalWorkingHour = false;
					
					firstCheckIn = convertUTCToLocal(firstCheckIn);
					lastCheckOut = convertUTCToLocal(lastCheckOut);

					userAttendance.setInTime(firstCheckIn);
					userAttendance.setOutTime(lastCheckOut);

					long shiftId = userDAO.getShiftIDOfUser(userID);
					CompanyAttendance companyAttendance = userDAO.getShiftDetailOfUser(shiftId, companyID);
					if (companyAttendance != null) 
					{
						boolean flexibleHours = companyAttendance.isFlexibleHoursAllowed();

						LOG.info("flexibleHours is :-" + flexibleHours);
						if (flexibleHours)
						{
							long workingHour = 0;
							LOG.info("dayOfWeek is :-" + dayOfWeek);

							coundTimeDifference = checkCountTimeDifferenceOrNot(dayOfWeek, coundTimeDifference,userAttendance, companyAttendance);

							Date checkInFrom = setTimeToCurrentDate(companyAttendance.getCheckInFrom(), date);
							LOG.info("checkInFrom is :-" + checkInFrom);

							Date checkOutTo = setTimeToCurrentDate(companyAttendance.getCheckOutTo(), date);
							LOG.info("checkOutTo is :-" + checkOutTo);

							LOG.info("companyAttendance.isCountWorkingHoursBeforeCheckInFrom() :-"+ companyAttendance.isCountWorkingHoursBeforeCheckInFrom());
							if (!companyAttendance.isCountWorkingHoursBeforeCheckInFrom())
							{
								if (firstCheckIn.getTime() < checkInFrom.getTime())
								{
									LOG.info("Replacing First Check time to Shift Company CheckIn Time");
									firstCheckIn = checkInFrom;
								}
							}

							LOG.info("companyAttendance.isCountWorkingHoursAfterCheckOutTo() :-"+ companyAttendance.isCountWorkingHoursAfterCheckOutTo());
							if (!companyAttendance.isCountWorkingHoursAfterCheckOutTo())
							{
								if (lastCheckOut.getTime() > checkOutTo.getTime())
								{
									LOG.info("Replacing Last Check Out time to Shift Company CheckOutTo Time");
									lastCheckOut = checkOutTo;
								}
							}

							LOG.info("coundTimeDifference is :-"+ coundTimeDifference);
							if (coundTimeDifference)
							{
								workingHour = getHoursMin(firstCheckIn,lastCheckOut);
								// timeDifference =
								// calculateTime(firstCheckIn,lastCheckOut);
								// timeDifference =
								// DateTimeFormatClass.calculateTime(String.valueOf(formatForTime.format(firstCheckIn)),
								// String.valueOf(formatForTime.format(lastCheckOut)));
							}

							userAttendance.setHourWorked(String.valueOf(workingHour));
							LOG.info("workingHour is :-" + workingHour);

							if (companyAttendance.isMinShiftHour())
							{
								if (workingHour <= companyAttendance.getMinShiftHour())
								{
									userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
								} 
								else if (workingHour > companyAttendance.getMinShiftHour()&& workingHour < companyAttendance.getMinimumWorkingHourForPresence() && companyAttendance.isHalfDayWorkingHour())
								{
									userAttendance.setStatus(Constants.ATTENDANCE_HALFDAY);
								} 
								else if (workingHour >= companyAttendance.getMinimumWorkingHourForPresence())
								{
									userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
								}
							}

							long shortHours = 0;
							LOG.info("companyAttendance.isCalculateDaily() is :-"+ companyAttendance.isCalculateDaily());
							if (companyAttendance.isCalculateDaily())
							{
								if (workingHour < companyAttendance.getShiftHour()) 
								{
									shortHours = (companyAttendance.getShiftHour() - workingHour);
									userAttendance.setShorthours(shortHours);
								} 
							}
							if (workingHour > companyAttendance.getMaxShiftHour())
							{
								 if(companyAttendance.isApprovalmaxworkinghours())
								 {
									 	goesForApprovalWorkingHour = true;
									 	long exactWorkingHour = workingHour;
									 	workingHour = companyAttendance.getMaxShiftHour();
									 	userAttendance.setHourWorked(String.valueOf(workingHour));
									 	try 
									 	{
											sendApprovalNotificationForWorkingHours(userID , companyID ,exactWorkingHour ,date,userAttendance);
										}
									 	catch (GetHierarchyException e) 
									 	{
											e.printStackTrace();
										} 
									 	catch (NotifyHierarchyException e) 
									 	{
											e.printStackTrace();
										}
								 }
							}

						}
						else
						{
							calculationForFlexibleHourFalse(date, dayOfWeek,userAttendance, firstCheckIn, lastCheckOut,companyAttendance);
						}
						calculationForLateComingAndEarlyGoing(date,userAttendance, firstCheckIn, lastCheckOut,companyAttendance,goesForApprovalWorkingHour,status);
					}
				}
			}
		}
		
		userAttendance.setDate(date);
		// Go for to add Record into USER_ATTENDANCE_TABLE
		insertUpdateRecordIntoUserAttendanceTable(userAttendance);
	}

	public void insertUpdateRecordIntoUserAttendanceTable(UserAttendance userAttendance)
	{
		int generatedID = 0;
		long ID = userAttendanceDAO.checkRecordExistInUserAttendanceTable(userAttendance.getUserID(),userAttendance.getCompanyID(),userAttendance.getDate());
		if(ID > 0)
		{
			userAttendance.setID(ID);
			userAttendanceDAO.updateUserAttendanceRecordIntoTable(userAttendance);
			LOG.info("Record UPDATE for companyID :-" + userAttendance.getCompanyID() + " And UserID:-" + userAttendance.getUserID()+" And ID:- "+ID);
		}
		else
		{
			generatedID = userAttendanceDAO.insertUserAttendanceIntoTable(userAttendance.getUserID(), userAttendance.getCompanyID(),
			userAttendance.getDate(), userAttendance.getInTime(), userAttendance.getOutTime(),userAttendance.isOnLeave(), userAttendance.isHoliday(),
			userAttendance.isSaturday(), userAttendance.isSunday(),userAttendance.getHourWorked(), userAttendance.getStatus(),
			userAttendance.getShorthours(), userAttendance.isLateComing(),userAttendance.isEarlyGoing(), userAttendance.isAbsent());
			LOG.info("Record ADDED for companyID :-" + userAttendance.getCompanyID() + " And UserID:-" + userAttendance.getUserID()+" And ID:- "+generatedID);
		}
	}
	
	/**
	 * Send Approval Notification For Working Hours
	 * 
	 * @param userID
	 * @param companyID
	 * @param exactWorkingHour
	 * @throws IOException
	 * 
	 * @throws SendNotificationException
	 * @throws GetHierarchyException 
	 * @throws NotifyHierarchyException 
	 * @throws ParseException 
	 */
	private void sendApprovalNotificationForWorkingHours(long userID,long companyID, long exactWorkingHour,Date date,UserAttendance userAttendance) throws IOException,SendNotificationException, GetHierarchyException, NotifyHierarchyException, ParseException 
	{
		Users userDetail = userDAO.getUserDetail(userID);
 
		String transactionID = "A"+ Constants.ATTENDANCE_WORKING_HOUR_REQUEST_NOTIFICATION + (Calendar.getInstance().getTimeInMillis());

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateString = format.format(date);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("FirstName", userDetail.getFirstName());
		map.put("LastName", userDetail.getLastName());
		map.put("WorkingHour", exactWorkingHour);
		map.put("Date", dateString);
		map.put("MemberID", userID);
		map.put("CompanyID", companyID);
		map.put("CheckInTime", format.format(convertLocalToUTC(userAttendance.getInTime())));
		map.put("CheckOutTime", format.format(convertLocalToUTC(userAttendance.getOutTime())));
		
		Gson gson = new GsonBuilder().create();
		
		boolean isAdminPresent = false;
		
		List<Users> userList = new ArrayList<Users>();
		try {

			// Get upper hierarchy user detail list
			userList = getUpperHierarchyDetails(userID);

			Iterator<Users> userIterator = userList.iterator();
			SendNotifications notification = null;

			// Iterate throw upper hierarchy list to send notification
			while (userIterator.hasNext())
			{
				Users user = userIterator.next();

				if (user.getRoleID()==Constants.ADMIN_ROLE)
				{
					isAdminPresent = true;
					LOG.debug("Sending notification to Admin UserID: " + user.getUserId());

					notification = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
							(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
							(String) Properties.get("senderID")).Send(userDAO.getDeviceID(user.getUserId()), Constants.ATTENDANCE_WORKING_HOUR_REQUEST,
							userDetail.getFirstName()+Constants.ATTENDANCE_WORKING_HOUR_REQUEST_MSG, gson.toJsonTree(map), Constants.NOTIFICATION_STATUS_PENDING, transactionID , user.getUserId(), companyID);

					int notified = userAttendanceDAO.addNotifications(notification);
					if (notified == 1) {
						LOG.info("Notification saved successfully.");
					} else {
						LOG.info("Notification not saved.");
					}
				} 
				else
				{
					if (!isAdminPresent)
					{
						LOG.debug("Sending notification to Super Admin UserID: " + user.getUserId());
						notification = new SendNotificationsHelper((String) Properties.get("iOSCertPath"),
								(String) Properties.get("iOSPassword"), (Boolean) Properties.get("iOSProductionEnv"),
								(String) Properties.get("senderID")).Send(userDAO.getDeviceID(user.getUserId()), Constants.ATTENDANCE_WORKING_HOUR_REQUEST,
								userDetail.getFirstName()+Constants.ATTENDANCE_WORKING_HOUR_REQUEST_MSG, gson.toJsonTree(map), Constants.NOTIFICATION_STATUS_PENDING, transactionID , user.getUserId(), companyID);

						int notified =userAttendanceDAO.addNotifications(notification);
						if (notified == 1) {
							LOG.info("Notification saved successfully.");
						} else {
							LOG.info("Notification not saved.");
						}
					}
				}
			}
		} catch (IOException e) {
			LOG.error("Error sending notification", e);
			throw new NotifyHierarchyException(e.getMessage());
		}
	}

	/**
	 * Check Count Time Difference Or Not
	 * 
	 * @param dayOfWeek
	 * @param coundTimeDifference
	 * @param userAttendance
	 * @param companyAttendance
	 * @return boolean
	 */
	private boolean checkCountTimeDifferenceOrNot(long dayOfWeek,
			boolean coundTimeDifference, UserAttendance userAttendance,
			CompanyAttendance companyAttendance) {
		if ((dayOfWeek == Calendar.MONDAY) || (dayOfWeek == Calendar.TUESDAY)
				|| (dayOfWeek == Calendar.WEDNESDAY)
				|| (dayOfWeek == Calendar.THURSDAY)
				|| (dayOfWeek == Calendar.FRIDAY)) {
			coundTimeDifference = true;
		} else {
			if (dayOfWeek == Calendar.SATURDAY) {
				userAttendance.setSaturday(true);
				if (companyAttendance.isSaturdayCountedInWorkingHours()) {
					coundTimeDifference = true;
				}
			}
			if (dayOfWeek == Calendar.SUNDAY) {
				userAttendance.setSunday(true);
				if (companyAttendance.isSundayCountedInWorkingHours()) {
					coundTimeDifference = true;
				}
			}
		}
		return coundTimeDifference;
	}

	/**
	 * calculation For Flexible Hour False
	 * 
	 * @param date
	 * @param dayOfWeek
	 * @param userAttendance
	 * @param firstCheckIn
	 * @param lastCheckOut
	 * @param companyAttendance
	 * @throws ParseException
	 */
	private void calculationForFlexibleHourFalse(Date date, long dayOfWeek,UserAttendance userAttendance, Date firstCheckIn,
			Date lastCheckOut, CompanyAttendance companyAttendance)throws ParseException 
	{
		Date checkInFrom = setTimeToCurrentDate(companyAttendance.getCheckInFrom(), date);
		LOG.info("checkInFrom is :-" + checkInFrom);

		Date checkInTo = setTimeToCurrentDate(companyAttendance.getCheckInTo(),date);
		LOG.info("checkInTo is :-" + checkInTo);

		Date checkOutFrom = setTimeToCurrentDate(companyAttendance.getCheckOutFrom(), date);
		LOG.info("checkOutFrom is :-" + checkOutFrom);

		Date checkOutTo = setTimeToCurrentDate(companyAttendance.getCheckOutTo(), date);
		LOG.info("checkOutTo is :-" + checkOutTo);

		long workingHour = 0;

		if (!companyAttendance.isLateComingHalfDay()&& !companyAttendance.isEarlyGoingHalfDay()) 
		{
			if (firstCheckIn.getTime() <= checkInFrom.getTime()) 
			{
				firstCheckIn = checkInFrom;
			}
			workingHour = getHoursMin(firstCheckIn, lastCheckOut);

			userAttendance.setHourWorked(String.valueOf(workingHour));
			if (workingHour >= companyAttendance.getShiftHour()) 
			{
				userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
			}
			else if (workingHour >= companyAttendance.getHalfDayWorkingHour()) 
			{
				userAttendance.setStatus(Constants.ATTENDANCE_HALFDAY);
			}
			else
			{
				userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
			}
		} 
		else if (firstCheckIn.getTime() >= checkInFrom.getTime() && firstCheckIn.getTime() <= checkInTo.getTime()) 
		{
			if (lastCheckOut.getTime() >= checkOutFrom.getTime() && lastCheckOut.getTime() <= checkOutTo.getTime()) 
			{
				// String timeAndMinute =
				// getHoursMin(firstCheckIn,lastCheckOut);
				workingHour = getHoursMin(firstCheckIn, lastCheckOut);

				// int workingHour = (int)timeDifference;
				userAttendance.setHourWorked(String.valueOf(workingHour));
				if (workingHour >= companyAttendance.getShiftHour())
				{
					userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
				} 
				else if (workingHour >= companyAttendance.getHalfDayWorkingHour())
				{
					userAttendance.setStatus(Constants.ATTENDANCE_HALFDAY);
				}
			}
		}

		if (dayOfWeek == Calendar.SATURDAY) 
		{
			userAttendance.setSaturday(true);
		} 
		else if (dayOfWeek == Calendar.SUNDAY) 
		{
			userAttendance.setSunday(true);
		}
	}

	/**
	 * calculation For Late Coming And Early Going
	 * 
	 * @param date
	 * @param userAttendance
	 * @param firstCheckIn
	 * @param lastCheckOut
	 * @param companyAttendance
	 * @throws ParseException
	 */
	private void calculationForLateComingAndEarlyGoing(Date date,UserAttendance userAttendance, Date firstCheckIn,Date lastCheckOut, CompanyAttendance companyAttendance,boolean goesForApprovalWorkingHour,List<String> status) throws ParseException 
	{
		Date maxLateComeTime = setTimeToCurrentDate(companyAttendance.getMaxLateComeTime(), date);
		LOG.info("maxLateComeTime is :-" + maxLateComeTime);

		if (companyAttendance.isLateleavingNextDayLateAllowed()) 
		{
			Date lastCheckOutOfPreviousDate = getLastCheckOutOfPreviousDate(date, userAttendance);
			if (lastCheckOutOfPreviousDate != null) 
			{
				lastCheckOutOfPreviousDate = convertUTCToLocal(lastCheckOutOfPreviousDate);
			}

			if (lastCheckOutOfPreviousDate != null) 
			{
				Date lastCheckOutTime = setTimeToCurrentDate(lastCheckOutOfPreviousDate, date);

				Date lateLeavingTimeOfPreviousDay = setTimeToCurrentDate(companyAttendance.getLateleavingNextDateLatetime(),date);

				if (lastCheckOutTime.getTime() >= lateLeavingTimeOfPreviousDay.getTime() && (firstCheckIn.getTime() >= maxLateComeTime.getTime())) 
				{
					companyAttendance.setMaxLateComeTime(companyAttendance.getMaximumLateAllowedForLateLeaving());
				}
			}
		}

		long workingHour = 0;
		workingHour = getHoursMin(firstCheckIn, lastCheckOut);
		if(goesForApprovalWorkingHour)
		{
			workingHour = companyAttendance.getMaxShiftHour();
		}

		if (companyAttendance.isFlexibleHoursAllowed()) 
		{
			userAttendance.setHourWorked(String.valueOf(workingHour));
		} 
		else 
		{
			if (companyAttendance.isEarlyGoingHalfDay() || companyAttendance.isLateComingHalfDay()) 
			{
				userAttendance.setHourWorked(String.valueOf(workingHour));
			}
		}

		boolean userCameLate = false;
		if (companyAttendance.isLateComingHalfDay()) 
		{
			if (firstCheckIn.getTime() >= maxLateComeTime.getTime()) 
			{
				userCameLate = true;
			}
		}

		Date maxEarlyGoTime = setTimeToCurrentDate(companyAttendance.getMaxEarlyGoTime(), date);
		LOG.info("maxEarlyGoTime is :-" + maxEarlyGoTime);

		boolean userGoneEarly = false;
		if (companyAttendance.isEarlyGoingHalfDay())
		{
			if (lastCheckOut.getTime() <= maxEarlyGoTime.getTime()) 
			{
				userGoneEarly = true;
			}
		}

		if (userCameLate && userGoneEarly)
		{
			userAttendance.setLateComing(true);
			userAttendance.setEarlyGoing(true);
			userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
		} 
		else if (!userCameLate && !userGoneEarly && workingHour >= companyAttendance.getShiftHour()) 
		{
			userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
		} 
		else if (userCameLate || userGoneEarly)
		{
			if (userCameLate) 
			{
				userAttendance.setLateComing(true);
			}

			if (userGoneEarly) 
			{
				userAttendance.setEarlyGoing(true);
			}

			if (companyAttendance.isHalfDayWorkingHour() && workingHour >= companyAttendance.getHalfDayWorkingHour()) 
			{
				userAttendance.setStatus(Constants.ATTENDANCE_HALFDAY);
			} 
			else if (workingHour < companyAttendance.getHalfDayWorkingHour()) 
			{
				userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
			}
		}
		
		//Adding for the Meeting on 25-01-2020
		if(userAttendance.getStatus().equals(Constants.ATTENDANCE_HALFDAY))
		{
			int isUserOnHalfLeave = schedulerDAO.isLeaveExisting(userAttendance.getUserID(), userAttendance.getCompanyID(),date, status, Constants.LEAVE_DAY_HALF);
			LOG.info(Constants.LEAVE_DAY_HALF+":"+"userID:-"+userAttendance.getUserID()+":"+"Leave:-"+isUserOnHalfLeave);
			if(isUserOnHalfLeave > 0)
			{
				userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
			}
		}
	}

	/**
	 * get Last CheckOutOfPreviousDate
	 * 
	 * @param comingDate
	 * @param userAttendance
	 * @return
	 */
	public Date getLastCheckOutOfPreviousDate(Date comingDate,UserAttendance userAttendance) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(comingDate);
		calendar.add(Calendar.DATE, -1);

		Attendance attendance = schedulerDAO.getUserAttendance(userAttendance.getUserID(), userAttendance.getCompanyID(),calendar.getTime());
		return attendance.getCheckOutTime();
	}

	/**
	 * set Time To CurrentDate
	 * 
	 * @param checkInFrom
	 * @param executionDate
	 * @return
	 */
	public Date setTimeToCurrentDate(Date checkInFrom, Date executionDate) 
	{
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(checkInFrom);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(executionDate);
		currentDate.set(Calendar.AM_PM, calendar1.get(Calendar.AM_PM));
		currentDate.set(Calendar.HOUR, calendar1.get(Calendar.HOUR));
		currentDate.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
		currentDate.set(Calendar.SECOND, calendar1.get(Calendar.SECOND));
		return currentDate.getTime();
	}

	/**
	 * calculate Time
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static double calculateTime(Date startTime, Date endTime)throws ParseException
	{
		double diff = endTime.getTime() - startTime.getTime();
		double hrs = diff / (1000 * 60 * 60);

		return hrs;
	}

	/**
	 * Calculate hours and minute from the mili second
	 * 
	 * @param diff
	 * @return
	 */
	private long getHoursMin(Date startTime, Date endTime)
	{
		long diff = endTime.getTime() - startTime.getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		// String totalWorkTime = diffHours + ":" + diffMinutes;
		long minutes = diffMinutes + (diffHours * 60);
		return minutes;
	}

	public Date getDateAndTime(Date d) 
	{
		Util util = new Util();
		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(d,(String) Properties.get("teamworks.scheduler.timezone")));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Date date = cal.getTime();
		return date;
	}

	/**
	 * get First CheckIn And Last CheckOut
	 * 
	 * @param attendance
	 * @return Map<String,Date>
	 */
	private Map<String, Date> getFirstCheckInAndLastCheckOut(Attendance attendance) 
	{
		Map<String, Date> firstCheckInAndLastCheckOut = new HashMap<String, Date>();

		Date checkInTime = null;
		Date checkOutTime = null;

		if (attendance.getCheckInTime() == null)
		{
			if (attendance.getUpdatedTimeIn() != null) 
			{
				checkInTime = attendance.getUpdatedTimeIn();
			}
		} 
		else
		{
			checkInTime = attendance.getCheckInTime();
		}

		if (attendance.getCheckOutTime() == null) 
		{
			if (attendance.getUpdatedTimeOut() != null) 
			{
				checkOutTime = attendance.getUpdatedTimeOut();
			}
		}
		else
		{
			checkOutTime = attendance.getCheckOutTime();
		}

		firstCheckInAndLastCheckOut.put("firstCheckIn", checkInTime);
		firstCheckInAndLastCheckOut.put("lastCheckOut", checkOutTime);

		return firstCheckInAndLastCheckOut;
	}
	
	/**
	 * get First CheckIn And Last CheckOut
	 * 
	 * @param attendance
	 * @return Map<String,Date>
	 */
	private Map<String, Date> getFirstCheckInAndLastCheckOutNEW(Attendance attendance) 
	{
		Map<String, Date> firstCheckInAndLastCheckOut = new HashMap<String, Date>();

		Date checkInTime = null;
		Date checkOutTime = null;

		if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) 
		{
			if (attendance.getTimeIn() != null) 
			{
				checkInTime = attendance.getTimeIn();
			}
			else
			{
				checkInTime = attendance.getCheckInTime();
			}

			if (attendance.getTimeOut() != null) 
			{
				checkOutTime = attendance.getTimeOut();
			} 
			else
			{
				checkOutTime = attendance.getCheckOutTime();
			}
		}
		else if(attendance.getUpdatedTimeIn() != null  && attendance.getUpdatedTimeOut() != null)
		{
			if (attendance.getCheckInTime() != null) 
			{
				checkInTime = attendance.getCheckInTime();
			} 
			else 
			{
				checkInTime = attendance.getUpdatedTimeIn();
			}

			if (attendance.getCheckOutTime() != null)
			{
				checkOutTime = attendance.getCheckOutTime();
			} 
			else 
			{
				checkOutTime = attendance.getUpdatedTimeOut();
			}
		}
		else if(attendance.getUpdatedTimeIn() != null)
		{
			checkInTime = attendance.getUpdatedTimeIn();
			if(attendance.getCheckOutTime() != null)
			{
				checkOutTime = attendance.getCheckOutTime();
			}
		}
		else if(attendance.getCheckInTime() != null)
		{
			checkInTime = attendance.getCheckInTime();
			if(attendance.getUpdatedTimeOut() != null)
			{
				checkOutTime = attendance.getUpdatedTimeOut();
			}
		}
		
		firstCheckInAndLastCheckOut.put("firstCheckIn", checkInTime);
		firstCheckInAndLastCheckOut.put("lastCheckOut", checkOutTime);

		return firstCheckInAndLastCheckOut;
	}

	/**
	 * get First CheckIn And Last CheckOut
	 * 
	 * @param attendance
	 * @return Map<String,Date>
	 */
	private Map<String, Date> getFirstCheckInAndLastCheckOutThird(Attendance attendance) 
	{
		Map<String, Date> firstCheckInAndLastCheckOut = new HashMap<String, Date>();

		Date checkInTime = null;
		Date checkOutTime = null;

		if(attendance.getUpdatedTimeIn() != null  && attendance.getUpdatedTimeOut() != null)
		{
			if (attendance.getCheckInTime() != null && (attendance.getCheckInTime().getTime() < attendance.getUpdatedTimeIn().getTime())) 
			{
				checkInTime = attendance.getCheckInTime();
			} 
			else 
			{
				checkInTime = attendance.getUpdatedTimeIn();
			}

			if (attendance.getCheckOutTime() != null && (attendance.getCheckOutTime().getTime() > attendance.getUpdatedTimeOut().getTime()))
			{
				checkOutTime = attendance.getCheckOutTime();
			} 
			else 
			{
				checkOutTime = attendance.getUpdatedTimeOut();
			}
		}
		else if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) 
		{
			if (attendance.getTimeIn() != null) 
			{
				checkInTime = attendance.getTimeIn();
			}
			else
			{
				checkInTime = attendance.getCheckInTime();
			}

			if (attendance.getTimeOut() != null) 
			{
				checkOutTime = attendance.getTimeOut();
			} 
			else
			{
				checkOutTime = attendance.getCheckOutTime();
			}
			
			if(attendance.getUpdatedTimeIn() != null && (checkInTime.getTime() > attendance.getUpdatedTimeIn().getTime()))
			{
				checkInTime = attendance.getUpdatedTimeIn();
			}
			
			if(attendance.getUpdatedTimeOut() != null && (checkOutTime.getTime() < attendance.getUpdatedTimeOut().getTime()))
			{
				checkOutTime = attendance.getUpdatedTimeOut();
			}
			
		}
		else if(attendance.getUpdatedTimeIn() != null)
		{
			checkInTime = attendance.getUpdatedTimeIn();
			if(attendance.getCheckOutTime() != null)
			{
				checkOutTime = attendance.getCheckOutTime();
			}
		}
		else if(attendance.getCheckInTime() != null)
		{
			checkInTime = attendance.getCheckInTime();
			if(attendance.getUpdatedTimeOut() != null)
			{
				checkOutTime = attendance.getUpdatedTimeOut();
			}
		}
		
		firstCheckInAndLastCheckOut.put("firstCheckIn", checkInTime);
		firstCheckInAndLastCheckOut.put("lastCheckOut", checkOutTime);

		return firstCheckInAndLastCheckOut;
	}


	/**
	 * filling User Company Map
	 * 
	 * @param companyList
	 * @param userCompanyMap
	 */
	private void fillingUserCompanyMap(List<Long> companyList,Map<Long, Long> userCompanyMap)
	{
		String memberID = "";
		for (long companyID : companyList) 
		{
			// Get Super Admin of the company
			long superAdminID = userDAO.getCompanySuperAdmin(companyID);
			memberID = userDAO.getLowerHierarchy(superAdminID);

			if (memberID != null && memberID.trim().length() != 0)
			{
				String[] memberArray = memberID.split(",");
				for (int i = 0; i < memberArray.length; i++)
				{
					userCompanyMap.put(Long.parseLong(memberArray[i]),companyID);
					// memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
				}
			}
			// Add Super Admin to member list
			// memberIDList.add(superAdminID);
			userCompanyMap.put(superAdminID, companyID);
		}
	}

	public Date convertUTCToLocal(Date date) 
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(date); // sets calendar time/date=====> you can set your own
		// date here
		cal.add(Calendar.HOUR_OF_DAY, 5); // adds one hour
		cal.add(Calendar.MINUTE, 30); // adds one Minute
		cal.getTime();
		return cal.getTime();
	}
	
	public static Date  convertLocalToUTC(Date date)
	{
	    Calendar cal = Calendar.getInstance(); // creates calendar
	    cal.setTime(date); // sets calendar time/date=====> you can set your own date here
	    cal.add(Calendar.HOUR_OF_DAY, -5); // adds one hour
	    cal.add(Calendar.MINUTE, -30); // adds one Minute
	    cal.getTime();
	    return cal.getTime();
	}

	boolean isWorkingOnThisSaturday(Calendar currentCal, String saturdayArr[]) 
	{
		List<Date> disable = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		int comingMonth = currentCal.get(Calendar.MONTH);
		cal.set(Calendar.MONTH, comingMonth);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		int month = cal.get(Calendar.MONTH);
		do 
		{
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == Calendar.SATURDAY)
				disable.add(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		} while (cal.get(Calendar.MONTH) == month);

		for (int j = 0; j < disable.size(); j++) 
		{
			SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormatClass.DEFAULT_DATE_FORMAT_WITH_TIME);

			LOG.info("Coming Date:-"+ sdf.format(currentCal.getTime()));
			LOG.info("Coming Date:-" + sdf.format(disable.get(j)));
			LOG.info("Equals :-"+ currentCal.getTime().equals(disable.get(j)));

			if (currentCal.getTime().equals(disable.get(j))) 
			{
				if (saturdayArr[j].equalsIgnoreCase("off")) 
				{
					// This saturday is off
					return true;
				} 
				else 
				{
					// This is working saturday
					return false;
				}
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void weeklySchedularExecution(Date date) //long UserID,long CompanyID 
	{
		LOG.info("START weeklySchedularExecution ");
		Date weekStartDate = Util.getWeekStartDate(date);
		Date weekEndDate = date;
		LOG.info("weekStartDate :-"+weekStartDate);
		LOG.info("weekEndDate :-"+weekEndDate);

		List<Date> listOfDate = iterateBetweenDates(weekStartDate,weekEndDate);
		LOG.info("List of date:- "+listOfDate);
		
		//List<Long> companyList = companyDAO.getAllActiveCompaniesDetails();
		//companyList.remove(new Long(1459));
		List<Long> companyList = Arrays.asList(new Long(1459));
		//LOG.info("List of Company after removal of schbang :- "+companyList);
		
		Map<Long, Long> userCompanyMap = new HashMap<Long, Long>();
		fillingUserCompanyMap(companyList, userCompanyMap);
		//userCompanyMap.put(new Long(UserID), new Long(CompanyID));
		//userCompanyMap.put(12340L, 1459L);
		
		for (Map.Entry<Long, Long> map : userCompanyMap.entrySet()) 
		{
			long workingHoursTotal = 0;
			long userID = map.getKey();
			long companyID = map.getValue();
			LOG.info("Start for UserID :-"+userID+", companyID :-"+companyID);
			
			long shiftID = userDAO.getShiftIDOfUser(userID);
			CompanyAttendance companyAtteSetting = userDAO.getShiftDetailOfUser(shiftID, companyID);
			if (companyAtteSetting != null) 
			{
				if (companyAtteSetting.isFlexibleHoursAllowed() && companyAtteSetting.isCalculateWeekly()) 
				{
					String workingDays = companyAtteSetting.getWorkingDays();
					List<String> listString = new ArrayList<String>();
					List<Integer> listLong = new ArrayList<Integer>();
					if (workingDays != null)
					{
						String arr[] = workingDays.split(",");
						listString = new ArrayList<String>(Arrays.asList(arr));
						for (String s : listString)
						{
							if ("Mon".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.MONDAY);
							}
							else if ("Tue".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.TUESDAY);
							}
							else if ("Wed".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.WEDNESDAY);
							}
							else if ("Thu".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.THURSDAY);
							}
							else if ("Fri".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.FRIDAY);
							} 
							else if ("Sat".equalsIgnoreCase(s)) 
							{
								listLong.add(Calendar.SATURDAY);
							} 
							else if ("Sun".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.SUNDAY);
							}
						}
					}

					//START : Check For IF on Half Leave for all Dates
					List<String> status = new ArrayList<String>();
					status.add(Constants.LEAVE_APPROVED);

					List<String> statusManual = new ArrayList<String>();
					statusManual.add("1");

					for(Date dates : listOfDate)
					{
						List<Date> dateList = new ArrayList<Date>();
						dateList.add(dates);

						int isUserOnHalfLeave = schedulerDAO.isLeaveExisting(userID, companyID,dates, status, Constants.LEAVE_DAY_HALF);
						LOG.info(Constants.LEAVE_DAY_HALF+":"+"userID:-"+userID+":"+"Leave:-"+isUserOnHalfLeave);
						
						int isUserOnHalfManual = schedulerDAO.isManualApproved( userID,companyID, dateList, statusManual ,Constants.MANUAL_DAY_HALF);
						
						int isUserOnFullManual = schedulerDAO.isManualApproved( userID,companyID, dateList, statusManual ,Constants.MANUAL_DAY_FULL);
						
						if(isUserOnFullManual > 0)
						{
							workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour());
						}
						else 
						{
							if((isUserOnHalfLeave > 0) && (isUserOnHalfManual > 0))
							{
								workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour());
							}
							else if (isUserOnHalfLeave > 0)
							{
								workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour()/2);
							}
							else if (isUserOnHalfManual > 0)
							{
								workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour()/2);
							}
						}
					}
					//END : Check For IF on Half Leave for all Dates
					
					List<UserAttendance> listOfAttendanceData = userAttendanceDAO.getUserAttendanceData(weekStartDate, weekEndDate,companyID, userID);
					List<UserAttendance> remainingList = listOfAttendanceData.stream().filter(userAttendance -> (!userAttendance.isOnLeave() && !userAttendance.isHoliday() && !userAttendance.isAbsent())).collect(Collectors.toList());
					
					int countWorkingDay = 0;
					long shortHourForAllDay = 0;
					for (UserAttendance attandanceData : remainingList) 
					{
						int dayOfDate = getDayFromTheDate(attandanceData.getDate());
						if (listLong.contains(dayOfDate)) 
						{
							if(attandanceData.getHourWorked() != null && !attandanceData.getHourWorked().isEmpty())
							{
								workingHoursTotal = workingHoursTotal + Integer.parseInt(attandanceData.getHourWorked());
							}
							if(StringUtils.isNotBlank(attandanceData.getStatus()) && !attandanceData.getStatus().equals("ABSENT"))
							{
								countWorkingDay++;								
							}
							shortHourForAllDay = shortHourForAllDay+attandanceData.getShorthours();
						}
					}
					
					List<UserAttendance> holidayDays = listOfAttendanceData.stream().filter(userAttendance -> (userAttendance.isHoliday()|| userAttendance.isSaturday() || userAttendance.isSunday())).collect(Collectors.toList());
					for (UserAttendance attandanceData : holidayDays)
					{	
						int dayOfDate = getDayFromTheDate(attandanceData.getDate());
						if(!listLong.contains(dayOfDate))
						{
							if(dayOfDate == Calendar.SATURDAY && companyAtteSetting.isSaturdayCountedInWorkingHours())
							{
								if(attandanceData.getHourWorked() != null && !attandanceData.getHourWorked().isEmpty())
								{
									workingHoursTotal = workingHoursTotal + Integer.parseInt(attandanceData.getHourWorked());
								}
							}
							else if(dayOfDate == Calendar.SUNDAY && companyAtteSetting.isSundayCountedInWorkingHours())
							{
								if(attandanceData.getHourWorked() != null && !attandanceData.getHourWorked().isEmpty())
								{
									workingHoursTotal = workingHoursTotal + Integer.parseInt(attandanceData.getHourWorked());
								}
							}
						}
					}
					
					LOG.info("countWorkingDay :-"+countWorkingDay);
					double shiftHourforOneDayInHour = 0;
					if(companyAtteSetting.getShiftHour() != 0)
					{	
						shiftHourforOneDayInHour = companyAtteSetting.getShiftHour();
					}
					
					double totalShiftHourForAllDay = shiftHourforOneDayInHour*countWorkingDay;
					LOG.info("totalShiftHourForAllDay :-"+totalShiftHourForAllDay);
					
					if(totalShiftHourForAllDay > workingHoursTotal)
					{
						double totalShortHour = totalShiftHourForAllDay - workingHoursTotal;
						
						double shortHour = 0;
						if(totalShortHour != 0)
						{
							shortHour = totalShortHour / 60;
						}
						
						LOG.info("shortHour :-"+shortHour);
						double weeklyShortHours = 0;
						if(shortHour != 0)
						{
							weeklyShortHours = companyAtteSetting.getWeeklyShortHoursHalfDay() / 60;
						}
						LOG.info("weeklyShortHours :-"+weeklyShortHours);
						double total = 0;
						if(shortHour != 0 && weeklyShortHours != 0)
						{
							total = shortHour/weeklyShortHours;
						}
						
						long ceilTotal = (long) Math.ceil(total);
						
						if(ceilTotal > 1)
						{
							LOG.info("After New logic implemented First Half Day Skip");
							LOG.info("Total Half day will reverse :-"+ (ceilTotal-1) );
						}
						else
						{
							LOG.info("Total Half day will reverse :-"+ 0);	
						}
						
						List<UserAttendance> updatelist = new ArrayList<UserAttendance>();
						for(UserAttendance attandanceData : remainingList)
						{
							if(ceilTotal > 1)
							{
								if(attandanceData.getStatus() != null && attandanceData.getStatus().equalsIgnoreCase(Constants.ATTENDANCE_FULLDAY))
								{
									attandanceData.setStatus(Constants.ATTENDANCE_HALFDAY);
									updatelist.add(attandanceData);
									ceilTotal--;
								}
							}
						}
						int [] updateArr = userAttendanceDAO.userAttendanceHalfDayReverse(companyID,userID, updatelist);
						LOG.info("updateArr is :-" + updateArr);
					}
				}
			}
			LOG.info("Done for UserID :-"+userID+", companyID :-"+companyID);
		}
		LOG.info("DONE weeklySchedularExecution");
	}

	public int getDayFromTheDate(Date d) 
	{
		Calendar cal = Calendar.getInstance();
		//Util util = new Util();
		//cal.setTime(util.getTimeZoneDate(d,(String) Properties.get("teamworks.scheduler.timezone")));
		cal.setTime(d);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Date date = cal.getTime();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}
	
	@Override
	public List<Users> getUpperHierarchyDetails(final long userid) throws GetHierarchyException 
	{
		List<Long> hierarchyDetails = new ArrayList<Long>();
		// Get upper level hierarchy for userID
		String managerID = userDAO.getUpperHierarchyDetails(userid);

		if (managerID == null)
			throw new GetHierarchyException("Unable to fetch hierarchy");

		String[] splitArray = managerID.split(",");
		for (int k = 0; k < splitArray.length; k++) {
			if (!splitArray[k].toString().equals("")) {
				hierarchyDetails.add(Long.parseLong(splitArray[k]));
			}
		}

		List<Users> userList = new ArrayList<Users>();
		if (hierarchyDetails != null && hierarchyDetails.size() != 0) {
			userList = userDAO.getUser(hierarchyDetails);
		}

		if (userList == null) {
			throw new GetHierarchyException("Unable to fetch hierarchy");
		}

		List<Users> sortedManagerHierarchy = new ArrayList<Users>();

		for (Long id : hierarchyDetails) {
			Iterator<Users> it = userList.iterator();
			while (it.hasNext()) {
				Users user = it.next();
				if (id == user.getUserId()) {
					sortedManagerHierarchy.add(user);
					userList.remove(user);
					break;
				}
			}
		}
		return sortedManagerHierarchy;
	}


	private static List<Date> iterateBetweenDates(Date startDate, Date endDate) 
	{
		List<Date> dateList = new ArrayList<Date>();
	    Calendar startCalender = Calendar.getInstance();
	    startCalender.setTime(startDate);
	    Calendar endCalendar = Calendar.getInstance();
	    endCalendar.setTime(endDate);
	    for(; startCalender.compareTo(endCalendar)<=0;startCalender.add(Calendar.DATE, 1)) 
	    {
	    	dateList.add(startCalender.getTime());
	    }
		return dateList;
	}
	
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void monthlySchedularExecution(Date date,long CompanyID) 
	{
		LOG.info(" START monthlySchedularExecution ");
		
		Date monthStartDate = Util.getMonthStartDate(date);
		Date monthEndDate = date;
		
		LOG.info("monthStartDate :-"+monthStartDate);
		LOG.info("monthEndDate :-"+monthEndDate);
		
		List<Date> listOfDate = iterateBetweenDates(monthStartDate,monthEndDate);
		LOG.info("List of dates:- "+listOfDate);
		
		//List<Long> companyList = companyDAO.getAllActiveCompaniesDetails();
		//companyList.remove(new Long(1459));
		//List<Long> companyList = Arrays.asList(new Long(333));
		//LOG.info("List of Company after removal of schbang :- "+companyList);
		
		List<Long> companyList = new ArrayList<Long>();
		companyList.add(CompanyID);
		
		Map<Long, Long> userCompanyMap = new HashMap<Long, Long>();
		fillingUserCompanyMap(companyList, userCompanyMap);
		//userCompanyMap.put(12340L, 1459L);
		//userCompanyMap.put(new Long(UserID), new Long(CompanyID));

		for (Map.Entry<Long, Long> map : userCompanyMap.entrySet()) 
		{
			long workingHoursTotal = 0;
			long userID = map.getKey();
			long companyID = map.getValue();
			LOG.info("Start for UserID :-"+userID+", companyID :-"+companyID);
			
			long shiftID = userDAO.getShiftIDOfUser(userID);
			CompanyAttendance companyAtteSetting = userDAO.getShiftDetailOfUser(shiftID, companyID);
			if (companyAtteSetting != null) 
			{
				if (companyAtteSetting.isFlexibleHoursAllowed() && (companyAtteSetting.isCalculateDaily() || companyAtteSetting.isCalculateWeekly()))
				{
					String workingDays = companyAtteSetting.getWorkingDays();
					List<String> listString = new ArrayList<String>();
					List<Integer> listLong = new ArrayList<Integer>();
					if (workingDays != null)
					{
						String arr[] = workingDays.split(",");
						listString = new ArrayList<String>(Arrays.asList(arr));
						for (String s : listString)
						{
							if ("Mon".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.MONDAY);
							}
							else if ("Tue".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.TUESDAY);
							}
							else if ("Wed".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.WEDNESDAY);
							}
							else if ("Thu".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.THURSDAY);
							}
							else if ("Fri".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.FRIDAY);
							} 
							else if ("Sat".equalsIgnoreCase(s)) 
							{
								listLong.add(Calendar.SATURDAY);
							} 
							else if ("Sun".equalsIgnoreCase(s))
							{
								listLong.add(Calendar.SUNDAY);
							}
						}
					}

					//START : Check For IF on Half Leave for all Dates
					List<String> status = new ArrayList<String>();
					status.add(Constants.LEAVE_APPROVED);

					List<String> statusManual = new ArrayList<String>();
					statusManual.add("1");

					for(Date dates : listOfDate)
					{
						List<Date> dateList = new ArrayList<Date>();
						dateList.add(dates);

						int isUserOnHalfLeave = schedulerDAO.isLeaveExisting(userID, companyID,dates, status, Constants.LEAVE_DAY_HALF);
						LOG.info(Constants.LEAVE_DAY_HALF+":"+"userID:-"+userID+":"+"Leave:-"+isUserOnHalfLeave);
						
						int isUserOnHalfManual = schedulerDAO.isManualApproved( userID,companyID, dateList, statusManual ,Constants.MANUAL_DAY_HALF);
						
						int isUserOnFullManual = schedulerDAO.isManualApproved( userID,companyID, dateList, statusManual ,Constants.MANUAL_DAY_FULL);
						
						if(isUserOnFullManual > 0)
						{
							workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour());
						}
						else 
						{
							if((isUserOnHalfLeave > 0) && (isUserOnHalfManual > 0))
							{
								workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour());
							}
							else if (isUserOnHalfLeave > 0)
							{
								workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour()/2);
							}
							else if (isUserOnHalfManual > 0)
							{
								workingHoursTotal = workingHoursTotal + (companyAtteSetting.getShiftHour()/2);
							}
						}
					}
					//END : Check For IF on Half Leave for all Dates
					
					List<UserAttendance> listOfAttendanceData = userAttendanceDAO.getUserAttendanceData(monthStartDate, monthEndDate,companyID, userID);
					List<UserAttendance> remainingList = listOfAttendanceData.stream().filter(userAttendance -> (!userAttendance.isOnLeave() && !userAttendance.isHoliday() && !userAttendance.isAbsent())).collect(Collectors.toList());
					
					int countWorkingDay = 0;
					long shortHourForAllDay = 0;
					for (UserAttendance attandanceData : remainingList) 
					{
						int dayOfDate = getDayFromTheDate(attandanceData.getDate());
						if (listLong.contains(dayOfDate)) 
						{
							if(attandanceData.getHourWorked() != null && !attandanceData.getHourWorked().isEmpty())
							{
								workingHoursTotal = workingHoursTotal + Integer.parseInt(attandanceData.getHourWorked());
							}
							if(StringUtils.isNotBlank(attandanceData.getStatus()) && !attandanceData.getStatus().equals("ABSENT"))
							{
								countWorkingDay++;
							}
							shortHourForAllDay = shortHourForAllDay+attandanceData.getShorthours();
						}
					}
					
					List<UserAttendance> holidayDays = listOfAttendanceData.stream().filter(userAttendance -> (userAttendance.isHoliday() || userAttendance.isSaturday() || userAttendance.isSunday())).collect(Collectors.toList());
					for (UserAttendance attandanceData : holidayDays)
					{	
						int dayOfDate = getDayFromTheDate(attandanceData.getDate());
						if(!listLong.contains(dayOfDate))
						{
							if(dayOfDate == Calendar.SATURDAY && companyAtteSetting.isSaturdayCountedInWorkingHours())
							{
								if(attandanceData.getHourWorked() != null && !attandanceData.getHourWorked().isEmpty())
								{
									workingHoursTotal = workingHoursTotal + Integer.parseInt(attandanceData.getHourWorked());
								}
							}
							else if(dayOfDate == Calendar.SUNDAY && companyAtteSetting.isSundayCountedInWorkingHours())
							{
								if(attandanceData.getHourWorked() != null && !attandanceData.getHourWorked().isEmpty())
								{
									workingHoursTotal = workingHoursTotal + Integer.parseInt(attandanceData.getHourWorked());
								}
							}
						}
					}
					
					LOG.info("countWorkingDay :-"+countWorkingDay);
					double shiftHourforOneDayInHour = 0;
					if(companyAtteSetting.getShiftHour() != 0)
					{	
						shiftHourforOneDayInHour = companyAtteSetting.getShiftHour();
					}
					
					double totalShiftHourForAllDay = shiftHourforOneDayInHour*countWorkingDay;
					LOG.info("totalShiftHourForAllDay :-"+totalShiftHourForAllDay);
					
					LOG.info("workingHoursTotal :-"+workingHoursTotal);

					if(totalShiftHourForAllDay < workingHoursTotal)
					{
						double totalExtraHourInMinute = workingHoursTotal - totalShiftHourForAllDay;
						
						double minimumExtraHourPerMonth = companyAtteSetting.getMinimumExtraMonthHours();
						
						LOG.info("totalExtraHourInMinute :-"+totalExtraHourInMinute);
						LOG.info("minimumExtraHourPerMonth :-"+minimumExtraHourPerMonth);
						if(totalExtraHourInMinute >= minimumExtraHourPerMonth)
						{
							double extraHour = 0;
							if(totalExtraHourInMinute != 0)
							{
								extraHour = totalExtraHourInMinute / 60;
							}
							
							LOG.info("extraHour :-"+extraHour);
							double monthlyExtraHourForReverse = 0;
							if(extraHour != 0)
							{
								monthlyExtraHourForReverse = companyAtteSetting.getHoursForEveryHalfDayReversal() / 60;
							}
							
							LOG.info("monthalyExtraHour :-"+monthlyExtraHourForReverse);
							double total = 0;
							if(extraHour != 0 && monthlyExtraHourForReverse != 0)
							{
								total = extraHour/monthlyExtraHourForReverse;
							}
							
							long floorTotal = (long) Math.floor(total);
							LOG.info("Total Half day will reverse In Monthly To FULL DAY:-"+floorTotal);
							
							List<UserAttendance> updatelist = new ArrayList<UserAttendance>();
							for(UserAttendance attandanceData : remainingList)
							{
								if(floorTotal > 0)
								{
									if(attandanceData.getStatus() != null && attandanceData.getStatus().equalsIgnoreCase(Constants.ATTENDANCE_HALFDAY))
									{
										attandanceData.setStatus(Constants.ATTENDANCE_FULLDAY);
										updatelist.add(attandanceData);
										floorTotal--;
									}
								}
							}
							int [] updateArr = userAttendanceDAO.userAttendanceFullDayReverse(companyID,userID, updatelist);
							LOG.info("updateArr is :-" + Arrays.toString(updateArr));
						}
					}
				}
			}
			LOG.info("Done for UserID :-"+userID+", companyID :-"+companyID);
		}

		LOG.info(" END monthlySchedularExecution ");
	}

}
