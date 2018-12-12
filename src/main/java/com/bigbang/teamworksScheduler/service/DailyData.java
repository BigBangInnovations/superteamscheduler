package com.bigbang.teamworksScheduler.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;

public class DailyData {

	Logger LOG = LogManager.getLogger(DailyData.class);

	@Autowired
	AttendanceSchedulerDAO schedulerDAO;

	private static Date date;
	private static Map<String, List<Long>> startTimeCompanyMap;
	private static Map<String, List<Long>> endTimeCompanyMap;
	private static List<Long> companyNotWorkingList;
	private static List<Long> companyList;

	public Date getDate() {
		return date;
	}

	public List<Long> getStartTimeCompany(String startTime) {
		if (startTimeCompanyMap.containsKey(startTime))
			return startTimeCompanyMap.get(startTime);
		else
			return new ArrayList<Long>();
	}

	public List<Long> getEndTimeCompany(String endTime) {
		if (endTimeCompanyMap.containsKey(endTime))
			return endTimeCompanyMap.get(endTime);
		else
			return new ArrayList<Long>();
	}

	public List<Long> getCompanyNotWorking() {
		return companyNotWorkingList;
	}
	

	public void initailizeDailyData() {

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone((String) Properties
				.get("teamworks.scheduler.timezone")));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		// 1# Initialize Company Not working today
		LOG.debug("Initializing company not working for the date");
		SimpleDateFormat weekDayformatter = new SimpleDateFormat("EE");
		weekDayformatter.setTimeZone(TimeZone.getTimeZone((String) Properties.get("teamworks.scheduler.timezone")));

		String dayOfWeek = weekDayformatter.format(cal.getTime());

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(TimeZone.getTimeZone((String) Properties.get("teamworks.scheduler.timezone")));

		
		LOG.info("Day of week: " + dayOfWeek);
		companyNotWorkingList = schedulerDAO.getNotWorkingCompany(dayOfWeek, formatter.format(cal.getTime()));

		List<Company> companyList = schedulerDAO.getCompanyStartEndTime();

		// 2# Initialize start/end time company map
		LOG.info("Initializing Company start and end date");
		if (startTimeCompanyMap != null && endTimeCompanyMap != null) {
			startTimeCompanyMap.clear();
			endTimeCompanyMap.clear();
		} else {
			startTimeCompanyMap = new HashMap<String, List<Long>>();
			endTimeCompanyMap = new HashMap<String, List<Long>>();
		}

		for (Company company : companyList) {
			List<Long> tempStartList;
			if (startTimeCompanyMap.containsKey(company.getWorkingStartTime())) {
				tempStartList = startTimeCompanyMap.get(company.getWorkingStartTime());
				tempStartList.add(company.getCompanyID());
			} else {
				tempStartList = new ArrayList<Long>();
				tempStartList.add(company.getCompanyID());
			}
			startTimeCompanyMap.put(company.getWorkingStartTime(), tempStartList);
			List<Long> tempEndList;
			if (endTimeCompanyMap.containsKey(company.getWorkingEndTime())) {
				tempEndList = endTimeCompanyMap.get(company.getWorkingEndTime());
				tempEndList.add(company.getCompanyID());
			} else {
				tempEndList = new ArrayList<Long>();
				tempEndList.add(company.getCompanyID());
			}
			endTimeCompanyMap.put(company.getWorkingEndTime(), tempEndList);
		}

		// 3# Initialize date
		LOG.debug("Initializing daily data date");
		date = cal.getTime();
	}
}
