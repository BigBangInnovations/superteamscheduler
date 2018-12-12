package com.bigbang.teamworksScheduler.dao;

import java.util.List;
import java.util.Map;

import notification.SendNotifications;

import com.bigbang.teamworksScheduler.beans.CompanyLeaves;
import com.bigbang.teamworksScheduler.beans.User;

public interface LeaveSchedulerDAO {

	List<Long> getAutoLeaveUpdateCompany();

	List<Long> getUpdatedLeaveUser(int month, int year);

	List<CompanyLeaves> getCompanyLeaves(List<Long> companyIDs, List<String> cycles);

	void updateAutoLeaveBal(double leave, long companyID, int leaveTypeID);

	void addAutoLeaveLog(int month, int year);

	void updateAutoLeaveLog(int month, int year, CompanyLeaves companyLeaves);

	List<Long> getUserFromLeaveBal();

	Map<Long, Long> getMemberCompanyMap();

	String addLeaveBalance(Map<Long, Long> userCompanyMap);

	List<User> getUser(int month, int year);

	void addNotifications(List<SendNotifications> notifications);

}
