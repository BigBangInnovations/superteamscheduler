package com.bigbang.teamworksScheduler.service;

import handleException.GetHierarchyException;


import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.bigbang.teamworksScheduler.beans.Users;

public interface AdvanceSchedularForAttendanceService 
{
	public void runDailySchedular(long companyID , long userID , Date startDate, Date endDate)throws ParseException;
	
	public void runDailySchedularForAllCompanyExceptSchbang() throws ParseException;
	
	public List<Users> getUpperHierarchyDetails(final long userid) throws GetHierarchyException;

	void weeklySchedularExecution(Date date); //,long UserID,long CompanyID

	void monthlySchedularExecution(Date date, long companyID);
}
