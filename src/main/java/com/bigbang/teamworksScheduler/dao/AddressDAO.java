package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;

import com.bigbang.teamworksScheduler.beans.CheckInCheckOutHistory;

public interface AddressDAO {
	public List<UserLocation> getUserLocations(final Date startdate);
	
	public void updateLocation(List<UserLocation> locationList);
	
	List<CheckInCheckOutHistory> getCheckInCheckOutHistory(final Date startDate);
	
	public void updateCheckInCheckOutAddress(List<CheckInCheckOutHistory> checkInCheckOutList);
}
