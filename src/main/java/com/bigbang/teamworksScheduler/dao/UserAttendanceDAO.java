package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;

import notification.SendNotifications;

import org.apache.commons.collections.map.MultiValueMap;

import com.bigbang.teamworksScheduler.beans.UserAttendance;

public interface UserAttendanceDAO 
{
	
	public int insertUserAttendanceIntoTable(long userID, long companyID, Date date,Date inTime, Date outTime, boolean onLeave, boolean isHoliday, boolean isSaturday,
	boolean isSunday, String hourWorked, String status, long shorthours, boolean lateComing, boolean earlyGoing, boolean isAbsent);

	List<UserAttendance> getUserAttendanceData(Date startDate, Date endDate,long companyID, long userID);

	public int[] userAttendanceHalfDayReverse(long companyid, long modifiedBy, List<UserAttendance> userAttendanceList);
	
	public int addNotifications(SendNotifications notifications);
	
	public Integer checkRecordExistInUserAttendanceTable(long userID, long companyID, Date date);

	public void updateUserAttendanceRecordIntoTable(UserAttendance userAttendance);

	public int[] userAttendanceFullDayReverse(long companyid, long modifiedBy,List<UserAttendance> userAttendanceList);
}
