package com.bigbang.teamworksScheduler.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.beans.CompanyInfo;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.CompanyDAO;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAO;
import com.bigbang.teamworksScheduler.util.DateTimeFormatClass;

public class CompanyServiceImpl implements CompanyService {

	private static final Logger LOG = LogManager.getLogger(CompanyServiceImpl.class);
	@Autowired
	AttendanceSchedulerDAO schedulerDAO;
	
	@Autowired
	CompanyDAO companyDAO;
	
	/**
	 * Function to read company working days without saturday policy
	 * 
	 * @param companyID
	 * @param workingDays
	 * @param monthNumber
	 * @param year
	 * @return
	 * @throws ParseException
	 */
	@Override
	public List<Date> getCompanyWorkingDates(long companyID, String workingDays, int monthNumber, int year)
			throws ParseException {
		int days = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("EE", Locale.ENGLISH);
		List<Date> workingDates = new ArrayList<Date>();
		// fetch total no of days in month
		Calendar calendar = Calendar.getInstance();
		int date = 1;
		calendar.set(year, monthNumber, date);
		days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		// Calculate number of working days without Saturday policy

		while (date <= days) {
			calendar = Calendar.getInstance();
			calendar.set(year, monthNumber, date);
			if (workingDays.contains(formatter.format(calendar.getTime()))) {
				workingDates.add(DateTimeFormatClass.getOnlyDate(calendar.getTime()));
			}
			date++;
		}

		// Get list of company holidays
		List<Date> holidayList = companyDAO.getHolidayDates(companyID);

		// Remove all company holidays from the list
		workingDates.removeAll(holidayList);

		return workingDates;
	}

	/**
	 * Get Company Working days dates with Salturday Policy
	 * 
	 * @param companyID
	 * @param workingDays
	 * @param workingSaturday
	 * @param monthNumber
	 * @param year
	 * @return
	 * @throws ParseException
	 */
	@Override
	public List<Date> getCompanyWorkingDates(long companyID, String workingDays, String workingSaturday,
			int monthNumber, int year) throws ParseException {
		int days = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("EE", Locale.ENGLISH);
		List<Date> workingDates = new ArrayList<Date>();
		String[] workingSatArray = workingSaturday.split(",");
		int satCount = 0;

		// fetch total no of days in month
		Calendar calendar = Calendar.getInstance();
		int date = 1;
		calendar.set(year, monthNumber, date);
		days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		// For each date check if it is company working date. In case of sat separately check if it is working sat
		while (date <= days) {
			calendar = Calendar.getInstance();
			calendar.set(year, monthNumber, date);
			calendar = DateTimeFormatClass.getAbsoluteDate(calendar.getTime());
			if (workingDays.contains(formatter.format(calendar.getTime()))) {
				if ("Sat".equals(formatter.format(calendar.getTime()))) {
					if (satCount < 5 && "off".equals(workingSatArray[satCount])) {
						LOG.info("Non-working Saturday: " + calendar.getTime());
					} else {
						workingDates.add(DateTimeFormatClass.getOnlyDate(calendar.getTime()));
					}
					satCount++;
				} else {
					workingDates.add(DateTimeFormatClass.getOnlyDate(calendar.getTime()));
				}
			}
			date++;
		}

		// Get list of company holidays
		List<Date> holidayList = companyDAO.getHolidayDates(companyID);

		// Remove company holidays from working date list
		workingDates.removeAll(holidayList);

		return workingDates;
	}
	
	@Override
	public List<Long> workingCompaniesList(List<Long> companyIdList,Date CurrentDate) throws ParseException {
		
		System.out.println("currentDate: "+CurrentDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(CurrentDate);
		int startMonth = cal.get(Calendar.MONTH);
		int startYear  = cal.get(Calendar.YEAR);
		
		List<Long> workingCompanies =  new ArrayList<Long>();
		for(int i=0;i<companyIdList.size();i++){
			CompanyInfo companyDetails = companyDAO.getCompanybyID(companyIdList.get(i));
			boolean saturdayPolicy = companyDetails.isSaturdayPolicy();
			if(saturdayPolicy){
				List<Date> df = getCompanyWorkingDates(companyIdList.get(i), companyDetails.getWorkingDays(), 
						companyDetails.getWorkingSaturday(), startMonth, startYear);
				System.out.println("sat policy dates "+df);
				if(df.contains(CurrentDate)){
					System.out.println("working companyID: "+companyIdList.get(i));
					workingCompanies.add(companyIdList.get(i));
				}
			}else{
				List<Date> ds = getCompanyWorkingDates(companyIdList.get(i), companyDetails.getWorkingDays(), 
						 startMonth, startYear);
				System.out.println("no sat policy dates "+ds);
				if(ds.contains(CurrentDate)){
					System.out.println("working1 companyID: "+companyIdList.get(i));
					workingCompanies.add(companyIdList.get(i));
				}
			}
		}
		
		
		return workingCompanies;
	}

}
