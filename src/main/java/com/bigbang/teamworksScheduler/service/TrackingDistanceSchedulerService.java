package com.bigbang.teamworksScheduler.service;

import java.io.IOException;

import notification.SendNotificationException;

public interface TrackingDistanceSchedulerService {

//	void autoTrackingDistanceUpdate() throws IOException, NumberFormatException, SendNotificationException;
	
	void autoTrackingGoogleDistanceUpdate() throws IOException, NumberFormatException, SendNotificationException;
	
	void autoTrackingGoogleDistanceUpdateMay() throws IOException, NumberFormatException, SendNotificationException;
	
	void autoDistanceTrackingSum() throws IOException, NumberFormatException, SendNotificationException;
	
	void autoDistanceTrackingSumForMonth() throws IOException, NumberFormatException, SendNotificationException;
	
}
