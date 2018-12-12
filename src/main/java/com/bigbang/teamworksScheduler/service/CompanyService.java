package com.bigbang.teamworksScheduler.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface CompanyService {
	
	public List<Date> getCompanyWorkingDates(long companyID, String workingDays, String workingSaturday,
			int monthNumber, int year) throws ParseException;
	
	public List<Date> getCompanyWorkingDates(long companyID, String workingDays, int monthNumber, int year)
			throws ParseException;
			
	public List<Long> workingCompaniesList(List<Long> companyIdList,Date CurrentDate) throws ParseException;

}
