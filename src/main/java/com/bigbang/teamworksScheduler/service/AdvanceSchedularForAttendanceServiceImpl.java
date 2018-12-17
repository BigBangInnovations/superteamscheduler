package com.bigbang.teamworksScheduler.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.Constants;
import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.CompanyAttendance;
import com.bigbang.teamworksScheduler.beans.Holidays;
import com.bigbang.teamworksScheduler.beans.UserAttendance;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.CompanyDAO;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAO;
import com.bigbang.teamworksScheduler.dao.UserAttendanceDAO;
import com.bigbang.teamworksScheduler.dao.UserDAO;
import com.bigbang.teamworksScheduler.util.DateTimeFormatClass;
import com.bigbang.teamworksScheduler.util.Util;

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
	
	Logger LOG = LogManager.getLogger(AdvanceSchedularForAttendanceServiceImpl.class);

	public void runDailySchedular(long companyID , long userID , Date specifiDate) throws ParseException
	{
		LOG.info("Service function which executing Daily Schedular for Attendance");
		Calendar cal = Calendar.getInstance();
		Util util = new Util();
		
		SimpleDateFormat format1 = new SimpleDateFormat(DateTimeFormatClass.DATE_FORMAT);
		//String dateStart = "2018/11/23";
		String dateStart = format1.format(specifiDate);
		//Date d = new Date();
		Date d = format1.parse(dateStart);
		
		cal.setTime(util.getTimeZoneDate(d, (String) Properties.get("teamworks.scheduler.timezone")));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		Date date = cal.getTime();
		long dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		// Check if daily data is not stale
		if (dailyData.getDate() == null || date.after(dailyData.getDate())) {
			LOG.info("Daily Data is stale. Re-initiaizing data");
			dailyData.initailizeDailyData();
			LOG.info("Daily data intializes successfully");
		}

		List<String> status = new ArrayList<String>();
		status.add(Constants.LEAVE_APPROVED);
		
		List<Date> dateList = new ArrayList<Date>();
		dateList.add(date);

		//status.add(Constants.LEAVE_PENDING);
		
		//List<Long> activeCompany = schedulerDAO.getActiveCompany();
		
		//List<Long> companyList = companyDAO.getAllActiveCompaniesDetails(); // MasterSchema3
		List<Long> companyList = Arrays.asList(new Long(140)); // MasterSchema
		//List<Long> companyList = Arrays.asList(new Long(52));
		
		//List<Long> memberIDList = new ArrayList<Long>();
//		Map<Long,Long> userCompanyMap = new HashMap<Long,Long>();
//		fillingUserCompanyMap(companyList, userCompanyMap);
		
		Map<Long,Long> userCompanyMap = new HashMap<Long,Long>();
		userCompanyMap.put(new Long(426), new Long(280));
		//userCompanyMap.put(new Long(583), new Long(329));
		//userCompanyMapForMasterSchema.put(new Long(586), new Long(329));
		
		// Get list of company that are non working
		//List<Long> companyNotWorkingList = dailyData.getCompanyNotWorking();
		
		// Get List of active company users who have attendance entry for date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat formatForTime = new SimpleDateFormat("HH:mm:ss");
//		List<Long> checkedInUserList = schedulerDAO.getcheckedInUser(companyList, format.format(date));
//
//		if (checkedInUserList.size() == 0) {
//			checkedInUserList.add(Long.parseUnsignedLong("0"));
//		}
		// Get list of active company user who do not have attendance entry for the date
		//Map<Long, Long> absentUsers = schedulerDAO.getAbsentUserCompanyMap(companyList, checkedInUserList);
		
		List<Holidays> listOfHolidaysOfAllCompanies = holidaySchedulerDAO.getAllHolidaysOfAllCompany(companyList, date);
		
		Map<Long,Long> branchIDsOfAllUses = userDAO.getBranchIDOfAllActiveCompanies(companyList);
		
		SimpleDateFormat weekDayformatter = new SimpleDateFormat("EE");
		weekDayformatter.setTimeZone(TimeZone.getTimeZone((String) Properties.get("teamworks.scheduler.timezone")));
		String dayOfWeekInString = weekDayformatter.format(cal.getTime());

//		for(Map.Entry<Long, Long> map : userCompanyMap.entrySet())
//		{
//			
//			long userID = map.getKey();
//			long companyID = map.getValue();
			boolean byPassAttendanceProcess = true;
			
			LOG.info("START companyID is :-"+companyID+" And UserID:-"+userID);
			boolean coundTimeDifference = false;
			UserAttendance userAttendance = new UserAttendance();
			userAttendance.setUserID(userID);
			userAttendance.setCompanyID(companyID);

			//filter holidays for the specific Company
			List<Holidays> listOfHolidaysForSpecificCompany = listOfHolidaysOfAllCompanies.stream().filter(holiday -> (holiday.getCompanyID()==companyID)).collect(Collectors.toList());

			//filter holidays for the specific Company
			List<Holidays> listOfHolidaysForNational = listOfHolidaysForSpecificCompany.stream().filter(holiday -> (holiday.isNationalHoliday() == true)).collect(Collectors.toList());

			int isUserOnLeave = schedulerDAO.isLeaveExisting(userID, companyID, dateList, status, Constants.LEAVE_DAY_FULL);

			Attendance attendance = schedulerDAO.getUserAttendance(userID, companyID, date);
			Map<String,Date> firstCheckInAndLastCheckOut = getFirstCheckInAndLastCheckOut(attendance);

			Date firstCheckIn = firstCheckInAndLastCheckOut.get("firstCheckIn");
			Date lastCheckOut = firstCheckInAndLastCheckOut.get("lastCheckOut");
			
			if(isUserOnLeave > 0)
			{
				byPassAttendanceProcess = false;
				userAttendance.setOnLeave(true);
			}
			else if (listOfHolidaysForNational.size() > 0)
			{
				byPassAttendanceProcess = false;
				userAttendance.setHoliday(true);
				// Add holiday into attendance table
				 holidaySchedulerDAO.addAttendanceForUser(userID,companyID,Constants.LEAVE_TYPE_HOLIDAY, date);
				//SKIP process Attendance , Go for insert record 
			}
			else if(listOfHolidaysForNational.size() <= 0)
			{
				if(branchIDsOfAllUses != null && branchIDsOfAllUses.size() > 0)
				{
					if(branchIDsOfAllUses.containsKey(userID))
					{
						long branchID = branchIDsOfAllUses.get(userID);
						List<Holidays> onBranchHolidays = listOfHolidaysForSpecificCompany.stream().filter(holiday -> (holiday.getBranchID() == branchID)).collect(Collectors.toList());
						if(onBranchHolidays.size() > 0)
						{
							byPassAttendanceProcess = false;
							// Add holiday into attendance table
							userAttendance.setHoliday(true);
							holidaySchedulerDAO.addAttendanceForUser(userID,companyID,Constants.LEAVE_TYPE_HOLIDAY, date);
							//SKIP process Attendance , Go for insert record 
						}
					}
				}	
			}
			
			if(byPassAttendanceProcess)
			{

				if(firstCheckIn == null && lastCheckOut == null)
				{
					//Add ABSENT Entry into attendance table
					boolean checkAbsent = true;
					long shiftID = userDAO.getShiftIDOfUser(userID);
					CompanyAttendance companyAttendanced = userDAO.getShiftDetailOfUser(shiftID, companyID);
					if(companyAttendanced != null)
					{
						String workingDays = companyAttendanced.getWorkingDays();
						if(workingDays != null)
						{
							String arr[] = workingDays.split(",");
							List<String> listString = new ArrayList<String>(Arrays.asList(arr));
							boolean conainsOf = listString.contains(dayOfWeekInString);
							if(conainsOf)
							{
								if(companyAttendanced.isSaturdayPolicy())
								{
									if(dayOfWeek == Calendar.SATURDAY)
									{
										String workingSaturday = companyAttendanced.getWorkingSaturday();
										String saturdayArr[] = workingSaturday.split(",");
										boolean isSaturdayNotWorking = isWorkingOnThisSaturday(cal,saturdayArr);
										if(isSaturdayNotWorking)
										{
											checkAbsent = false;
											userAttendance.setHoliday(true);
											holidaySchedulerDAO.addAttendanceForUser(userID,companyID,Constants.LEAVE_TYPE_HOLIDAY, date);
										}
									}
								}
							}
							else 
							{
								checkAbsent = false;
								userAttendance.setHoliday(true);
								holidaySchedulerDAO.addAttendanceForUser(userID,companyID,Constants.LEAVE_TYPE_HOLIDAY, date);
							}
						}
					}
					
					if(checkAbsent)
					{
						userAttendance.setAbsent(true);
						holidaySchedulerDAO.addAttendanceForUser(userID,companyID,Constants.LEAVE_TYPE_ABSENT, date);
					}
				}
				else
				{
					boolean entry = true;
					 
					if(firstCheckIn==null || lastCheckOut == null)
					{
						entry = false;
					}
		
					if(entry)
					{
						firstCheckIn = convertUTCToLocal(firstCheckIn);
						lastCheckOut = convertUTCToLocal(lastCheckOut);

						userAttendance.setInTime(firstCheckIn);
						userAttendance.setOutTime(lastCheckOut);
						
						long shiftId = userDAO.getShiftIDOfUser(userID);
						CompanyAttendance companyAttendance = userDAO.getShiftDetailOfUser(shiftId, companyID);
						if(companyAttendance != null)
						{
							boolean flexibleHours = companyAttendance.isFlexibleHoursAllowed();
							
							LOG.info("flexibleHours is :-"+flexibleHours);
							if(flexibleHours)
							{
								long workingHour = 0;
								LOG.info("dayOfWeek is :-"+dayOfWeek);
								
								coundTimeDifference = checkCountTimeDifferenceOrNot(dayOfWeek, coundTimeDifference, userAttendance,companyAttendance);
								
								Date checkInFrom = setTimeToCurrentDate(companyAttendance.getCheckInFrom(),date);
								LOG.info("checkInFrom is :-"+checkInFrom);
								
		
								Date checkOutTo = setTimeToCurrentDate(companyAttendance.getCheckOutTo(),date);
								LOG.info("checkOutTo is :-"+checkOutTo);
		
								LOG.info("companyAttendance.isCountWorkingHoursBeforeCheckInFrom() :-"+companyAttendance.isCountWorkingHoursBeforeCheckInFrom());
							    if(!companyAttendance.isCountWorkingHoursBeforeCheckInFrom())
								{
									if(firstCheckIn.getTime() < checkInFrom.getTime())
									{
										LOG.info("Replacing First Check time to Shift Company CheckIn Time");
										firstCheckIn = checkInFrom;
									}
								}
							    
						    	LOG.info("companyAttendance.isCountWorkingHoursAfterCheckOutTo() :-"+companyAttendance.isCountWorkingHoursAfterCheckOutTo());
							    if(!companyAttendance.isCountWorkingHoursAfterCheckOutTo())
								{
									if(lastCheckOut.getTime() > checkOutTo.getTime())
									{
										LOG.info("Replacing Last Check Out time to Shift Company CheckOutTo Time");
										lastCheckOut = checkOutTo;
									}
								}
								
							    LOG.info("coundTimeDifference is :-"+coundTimeDifference);
								if(coundTimeDifference)
								{
									workingHour = getHoursMin(firstCheckIn,lastCheckOut);
									//timeDifference = calculateTime(firstCheckIn,lastCheckOut);
									//timeDifference = DateTimeFormatClass.calculateTime(String.valueOf(formatForTime.format(firstCheckIn)), String.valueOf(formatForTime.format(lastCheckOut)));
								}
								
								userAttendance.setHourWorked(String.valueOf(workingHour));
								LOG.info("workingHour is :-"+workingHour);
								
								if(companyAttendance.isMinShiftHour())
								{
									if(workingHour <= companyAttendance.getMinShiftHour())
									{
										userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
									}
									else if(workingHour > companyAttendance.getMinShiftHour() && workingHour < companyAttendance.getMinimumWorkingHourForPresence() && companyAttendance.isHalfDayWorkingHour())
									{
										userAttendance.setStatus(Constants.ATTENDANCE_HALFDAY);
									}
									else if(workingHour >= companyAttendance.getMinimumWorkingHourForPresence())
									{
										userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
									}
								}
								
								long shortHours = 0;
								LOG.info("companyAttendance.isCalculateDaily() is :-"+companyAttendance.isCalculateDaily());
								if(companyAttendance.isCalculateDaily())
								{
									if(workingHour < companyAttendance.getShiftHour())
									{
										shortHours = (companyAttendance.getShiftHour() - workingHour);
										userAttendance.setShorthours(shortHours);
									}
									else if (workingHour > companyAttendance.getShiftHour())
									{
//										if(!companyAttendance.isApprovalmaxworkinghours())
//										{
											workingHour = companyAttendance.getShiftHour();
//										}
//										else
//										{
											// Send approval to Manager for granting extra hour
											// Till then mark maxworking hours. If approved, replace with actual working hours
											// Work still Remain
//										}
									}
								}
							}
							else
							{
								calculationForFlexibleHourFalse(date, dayOfWeek,userAttendance, firstCheckIn, lastCheckOut,companyAttendance);
							}
							calculationForLateComingAndEarlyGoing(date,userAttendance, firstCheckIn, lastCheckOut,companyAttendance);
						}
					}
				}
			}	
			// Go for to add Record into USER_ATTENDANCE_TABLE
			userAttendanceDAO.insertUserAttendanceIntoTable(userAttendance.getUserID(), 
			userAttendance.getCompanyID(), date, userAttendance.getInTime(), 
			userAttendance.getOutTime(), userAttendance.isOnLeave(), 
			userAttendance.isHoliday(), userAttendance.isSaturday(), 
			userAttendance.isSunday(), userAttendance.getHourWorked() , 
			userAttendance.getStatus() , userAttendance.getShorthours(), 
			userAttendance.isLateComing(),userAttendance.isEarlyGoing(),userAttendance.isAbsent());

			LOG.info("END companyID is :-"+companyID+" And UserID:-"+userID);
		//}
		
	}

	/**
	 * @param dayOfWeek
	 * @param coundTimeDifference
	 * @param userAttendance
	 * @param companyAttendance
	 * @return
	 */
	private boolean checkCountTimeDifferenceOrNot(long dayOfWeek,
			boolean coundTimeDifference, UserAttendance userAttendance,
			CompanyAttendance companyAttendance) {
		if((dayOfWeek == Calendar.MONDAY) || (dayOfWeek == Calendar.TUESDAY) || (dayOfWeek == Calendar.WEDNESDAY) || (dayOfWeek == Calendar.THURSDAY) || (dayOfWeek == Calendar.FRIDAY))
		{
			coundTimeDifference = true;
		}
		else
		{
			if (dayOfWeek == Calendar.SATURDAY)
			{
				userAttendance.setSaturday(true);
				if(companyAttendance.isSaturdayCountedInWorkingHours())
				{
					coundTimeDifference = true;
				}
			}
			if(dayOfWeek == Calendar.SUNDAY)
			{
				userAttendance.setSunday(true);
				if(companyAttendance.isSundayCountedInWorkingHours())
				{
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
		Date checkInFrom = setTimeToCurrentDate(companyAttendance.getCheckInFrom(),date);
		LOG.info("checkInFrom is :-"+checkInFrom);

		Date checkInTo = setTimeToCurrentDate(companyAttendance.getCheckInTo(),date);
		LOG.info("checkInTo is :-"+checkInTo);
		
		Date checkOutFrom = setTimeToCurrentDate(companyAttendance.getCheckOutFrom(),date);
		LOG.info("checkOutFrom is :-"+checkOutFrom);

		Date checkOutTo = setTimeToCurrentDate(companyAttendance.getCheckOutTo(),date);
		LOG.info("checkOutTo is :-"+checkOutTo);

		long workingHour = 0;
		
		if(!companyAttendance.isLateComingHalfDay() && !companyAttendance.isEarlyGoingHalfDay())
		{
			if(firstCheckIn.getTime() <= checkInFrom.getTime())
			{
				firstCheckIn = checkInFrom;
			}
			workingHour = getHoursMin(firstCheckIn,lastCheckOut);
			
			userAttendance.setHourWorked(String.valueOf(workingHour));
			if(workingHour >= companyAttendance.getShiftHour())
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
		else if(firstCheckIn.getTime() >= checkInFrom.getTime() && firstCheckIn.getTime() <= checkInTo.getTime())
		{
			if(lastCheckOut.getTime() >= checkOutFrom.getTime() && lastCheckOut.getTime() <= checkOutTo.getTime())
			{
				//String timeAndMinute = getHoursMin(firstCheckIn,lastCheckOut);
				workingHour = getHoursMin(firstCheckIn,lastCheckOut);
				
				//int workingHour = (int)timeDifference; 
				userAttendance.setHourWorked(String.valueOf(workingHour));
				if(workingHour >= companyAttendance.getShiftHour())
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
	private void calculationForLateComingAndEarlyGoing(Date date,UserAttendance userAttendance, Date firstCheckIn,Date lastCheckOut, CompanyAttendance companyAttendance)throws ParseException 
	{
		Date maxLateComeTime = setTimeToCurrentDate(companyAttendance.getMaxLateComeTime(),date);
		LOG.info("maxLateComeTime is :-"+maxLateComeTime);

		if(companyAttendance.isLateleavingNextDayLateAllowed())
		{
			Date lastCheckOutOfPreviousDate = getLastCheckOutOfPreviousDate(date , userAttendance);
			if(lastCheckOutOfPreviousDate != null)
			{
				lastCheckOutOfPreviousDate = convertUTCToLocal(lastCheckOutOfPreviousDate);
			}
			
			if(lastCheckOutOfPreviousDate != null)
			{
				Date lastCheckOutTime = setTimeToCurrentDate(lastCheckOutOfPreviousDate,date);
				
				Date lateLeavingTimeOfPreviousDay = setTimeToCurrentDate(companyAttendance.getLateleavingNextDateLatetime(), date);
	
				if(lastCheckOutTime.getTime() >= lateLeavingTimeOfPreviousDay.getTime() && (firstCheckIn.getTime() >= maxLateComeTime.getTime()))
				{
					companyAttendance.setMaxLateComeTime(companyAttendance.getMaximumLateAllowedForLateLeaving()); 
				}
			}	
		}
		
		long workingHour = 0;
		workingHour = getHoursMin(firstCheckIn,lastCheckOut);

		if(companyAttendance.isFlexibleHoursAllowed())
		{
			userAttendance.setHourWorked(String.valueOf(workingHour));
		}
		else 
		{
			if(companyAttendance.isEarlyGoingHalfDay() || companyAttendance.isLateComingHalfDay())
			{
				userAttendance.setHourWorked(String.valueOf(workingHour));
			}
		}
			 
		
		boolean userCameLate = false;
		if(companyAttendance.isLateComingHalfDay())
		{
			if(firstCheckIn.getTime() >= maxLateComeTime.getTime())
			{ 
				userCameLate = true;
			}
		}
		
		Date maxEarlyGoTime = setTimeToCurrentDate(companyAttendance.getMaxEarlyGoTime(),date);
		LOG.info("maxEarlyGoTime is :-"+maxEarlyGoTime);
		
		boolean userGoneEarly = false;
		if(companyAttendance.isEarlyGoingHalfDay())
		{
			if(lastCheckOut.getTime() <= maxEarlyGoTime.getTime())
			{
				userGoneEarly = true;
			}
		}
		
		if(userCameLate && userGoneEarly)
		{
			userAttendance.setLateComing(true);
			userAttendance.setEarlyGoing(true);
			userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
		}
		else if(!userCameLate && !userGoneEarly && workingHour >= companyAttendance.getShiftHour())
		{
			userAttendance.setStatus(Constants.ATTENDANCE_FULLDAY);
		}
		else if(userCameLate|| userGoneEarly)
		{
			if(userCameLate)
			{
				userAttendance.setLateComing(true);
			}
			
			if(userGoneEarly)
			{ 
				userAttendance.setEarlyGoing(true);
			}

			if(companyAttendance.isHalfDayWorkingHour() && workingHour >= companyAttendance.getHalfDayWorkingHour())
			{
				userAttendance.setStatus(Constants.ATTENDANCE_HALFDAY);
			}
			else if(workingHour < companyAttendance.getHalfDayWorkingHour())
			{
				userAttendance.setStatus(Constants.ATTENDANCE_ABSENT);
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
	public Date getLastCheckOutOfPreviousDate(Date comingDate , UserAttendance userAttendance )
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(comingDate);
		calendar.add(Calendar.DATE, -1);
		
		Attendance attendance = schedulerDAO.getUserAttendance(userAttendance.getUserID(), userAttendance.getCompanyID(), calendar.getTime());		
		return attendance.getCheckOutTime();
	}
	/**
	 * set Time To CurrentDate
	 * 
	 * @param checkInFrom
	 * @param executionDate
	 * @return
	 */
	public Date setTimeToCurrentDate(Date checkInFrom,Date executionDate)
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
	public static double calculateTime(Date startTime, Date endTime) throws ParseException 
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
		//String totalWorkTime = diffHours + ":" + diffMinutes;
		long minutes = diffMinutes + (diffHours*60) ;
		return minutes;
	}

	
	public Date getDateAndTime(Date d)
	{
		Util util = new Util();
		Calendar cal = Calendar.getInstance();
		cal.setTime(util.getTimeZoneDate(d, (String) Properties.get("teamworks.scheduler.timezone")));
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
	private Map<String,Date> getFirstCheckInAndLastCheckOut(Attendance attendance) 
	{
		Map<String,Date> firstCheckInAndLastCheckOut = new HashMap<String,Date>();
		
		Date checkInTime = null;
		Date checkOutTime = null;
		
		if(attendance.getCheckInTime() == null)
		{
				if(attendance.getUpdatedTimeIn() != null)
				{
					checkInTime = attendance.getUpdatedTimeIn();
				}
		}
		else
		{
			checkInTime = attendance.getCheckInTime();
		}
		
		if(attendance.getCheckOutTime() == null)
		{
				if(attendance.getUpdatedTimeOut() != null)
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
	 * filling User Company Map
	 * 
	 * @param companyList
	 * @param userCompanyMap
	 */
	 private void fillingUserCompanyMap(List<Long> companyList,Map<Long, Long> userCompanyMap) 
	 {
		String memberID = "";
		for(long companyID : companyList)
		{
			//Get Super Admin of the company
			long superAdminID = userDAO.getCompanySuperAdmin(companyID);
			memberID = userDAO.getLowerHierarchy(superAdminID);
			
			if (memberID != null && memberID.trim().length() != 0) 
			{
				String[] memberArray = memberID.split(",");
				for (int i = 0; i < memberArray.length; i++) 
				{
					userCompanyMap.put(Long.parseLong(memberArray[i]), companyID);
					//memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
				}
			}
			//Add Super Admin to member list
			//memberIDList.add(superAdminID);
			userCompanyMap.put(superAdminID,companyID);
		}
	 }
	 
		public Date convertUTCToLocal(Date date)
		{
		  Calendar cal = Calendar.getInstance(); // creates calendar
		  cal.setTime(date); // sets calendar time/date=====> you can set your own date here
		  cal.add(Calendar.HOUR_OF_DAY, 5); // adds one hour
		  cal.add(Calendar.MINUTE, 30); // adds one Minute
		  cal.getTime();
		  return cal.getTime();
		}
		
		boolean isWorkingOnThisSaturday(Calendar currentCal,String saturdayArr[])
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
				
				
				System.out.println("Coming Date:-"+sdf.format(currentCal.getTime()));
				System.out.println("Coming Date:-"+sdf.format(disable.get(j)));
				System.out.println("Equals :-"+currentCal.getTime().equals(disable.get(j)));
				
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
		

}
