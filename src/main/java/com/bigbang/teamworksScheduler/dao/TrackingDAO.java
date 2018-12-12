package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bigbang.teamworksScheduler.beans.User;
import com.bigbang.teamworksScheduler.beans.UserDistanceSum;
import com.bigbang.teamworksScheduler.beans.UserLocation;

public interface TrackingDAO {

	public List<Long> getCompanyUsers(List<Long> companyIDList);
	
	public List<Long> getUsers(long companyID);
	
	public List<Long> getCompanyTrackingUsers(List<Long> usersList,Date date);
	
	public Map<Long, String> getLoggedInUsersList(List<Long> userList);
	
	List<Long> getCompany(String timeStr);
	
	public List<UserLocation> getUserLocationList(long userID,String currentDate);
	
	public List<UserLocation> getUserLocationList1(long userID,String currentDate);
	
//	public int updateUserLocationDistance(long userID,double distance,Date dateTime);
	
	public int updateUserLocationDistance(List<UserLocation> updatedUserLocation);
	
	public List<UserLocation> getAll(List<Long> userIDList, String currentDate,long companyid);
	
	public List<Long> getAllCompanyIDs();
	
	public List<Long> getAllTrackingCompanies();
	
	List<UserLocation> getUserDistancePerDay(Date currentDate, long companyID,List<Long> userIDList);
	
	public List<UserDistanceSum> getUserDistanceSum(List<Long> userList, String currentDate,long companyID);
	
	public int addUserDailyDistanceSum(List<UserDistanceSum> userDailyDistanceSum, Date dateTime);
	
	public List<UserDistanceSum> getUserDistanceSumMonth(List<Long> userList, long companyID, String startDate, String endDate);
}
