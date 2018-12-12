package com.bigbang.teamworksScheduler.dao;

import java.util.List;
import java.util.Map;

import com.bigbang.teamworksScheduler.beans.UserLiveTrackingStatus;
import com.bigbang.teamworksScheduler.beans.Users;

public interface LiveTrackingSchedulerDAO {

	List<UserLiveTrackingStatus> getLiveTrackingOnUsers(List<Long> company);
	
	Users getUserDetails(final long userID);
	
	int UpdateUserStatus(long id);
	
	Map<Long, String> getGCMUserMap(List<Long> userID);

}
