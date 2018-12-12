package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bigbang.teamworksScheduler.beans.Holidays;

public interface HolidaySchedulerDAO {

	Map<Long, Long> getCompanyUsers(List<Long> companyID);

	void addAttendance(Map<Long, Long> userCompanyMap, int leaveType, Date date);

	List<Holidays> getAllHolidaysOfAllCompany(List<Long> companyIDs, Date date);

	void addAttendanceForUser(long userID, long companyID, int leaveType,Date date);

	List<Long> getNotWorkingCompanyfromCompanyAttendance(String day);
}
