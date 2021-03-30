package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bigbang.teamworksScheduler.beans.AddressBean;
import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.TrackingBean;

public interface AttendanceSchedulerDAO {

	List<Long> getStartTimeCompany(String timeStr);

	List<Long> getEndTimeCompany(String timeStr);

	List<Long> getcheckedInUser(List<Long> companyID, String date);

	Map<Long, String> getNotCheckedInUserList(List<Long> companyID, List<Long> userID);
	
	Map<Long, String> getNotCheckedInUserListTest(List<Long> companyID, List<Long> userID);

	List<Company> getCompanyStartEndTime();

	Map<String, Object> getProperties();

	Map<Long, String> getGCMUserMap(List<Long> userID);

	List<Long> getUserNotCheckedOut(List<Long> companyID, Date date);

	List<Long> getActiveCompany();

	Map<Long, List<TrackingBean>> getTrackingData(String date);

	Map<Long, AddressBean> getCompanyPermanentLocation();

	int addCheckInAttendance(Map<TrackingBean, AddressBean> map);

	int updateCheckOutAttendance(Map<TrackingBean, AddressBean> map, Date date);

	List<Long> getUserOnLeave(Date date);

	Map<Long, Long> getAbsentUserCompanyMap(List<Long> companyID, List<Long> userID);

	List<Long> getNotWorkingCompany(String day, String date);

	public Attendance getUserAttendance(long userID, long companyID, Date date);
	
	int isLeaveExisting(long userID, long companyID, Date date, List<String> status, String leaveDay);

	int isManualApproved(long userID, long companyID, List<Date> dateList,List<String> status, String manualDay);

	void addFailedAttendanceSchedular(long userID, long companyID, Date date);

	Map<String, Long> getLeaveExistingWithType(long userID, long companyID, Date date, List<String> status, String leaveDay);

}
