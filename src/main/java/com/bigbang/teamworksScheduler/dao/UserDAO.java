package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bigbang.teamworksScheduler.beans.CompanyAttendance;
import com.bigbang.teamworksScheduler.beans.Users;

public interface UserDAO {

	/**
	 * @param userId
	 *            UserId
	 * @param companyId
	 *            CompanyID
	 * @return User
	 */

	List<Users> getUser(List<Long> id);
	
	Users getUserDetails(long userID);

	List<Users> getManager(List<Long> id);

	Long getCompanySuperAdmin(long companyID);
	
	String getLowerHierarchy(long users);

	List<Long> removeAdminUserList(List<Long> userList, long companyId);
	
	List<Long> removeTeamMemberList(List<Long> userList, long companyId);
	
	List<Long> getAdminSuperAdmin(long companyID);

	public long getShiftIDOfUser(long userID);
	
	public CompanyAttendance getShiftDetailOfUser(long shiftID,long companyID);

	Map<Long, Long> getBranchIDOfAllActiveCompanies(List<Long> companyIDs);
	
	Users getUserDetail(long userID);
	
	public String getUpperHierarchyDetails(final long userid);
	
	public String getDeviceID(final long userid);
}