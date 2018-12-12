package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bigbang.teamworksScheduler.beans.Attendance;

public interface MissedCheckInUserDAO {
	
	List<Long> getCompany(String timeStr);
	
	public List<Long> getCompanySuperAdmin(List<Long> companyIDList);
	
	List<Attendance> getExportNewAttendanceHistory(List<Long> userIdList, long companyID, String startDate, String endDate);
	
	Map<Long,List<Date>> getHolidayDates(List<Long> companyID);
	
	Map<Long,String> getWorkingDays(List<Long> companyID);

}
