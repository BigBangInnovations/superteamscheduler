package com.bigbang.teamworksScheduler.dao;

import java.util.Date;

public interface UserAttendanceDAO 
{
	
	public void insertUserAttendanceIntoTable(long userID, long companyID, Date date, 
			Date inTime, Date outTime, boolean onLeave, boolean isHoliday, boolean isSaturday,
			boolean isSunday, String hourWorked, String status, long shorthours, boolean lateComing, boolean earlyGoing, boolean isAbsent);

}
