package com.bigbang.teamworksScheduler.service;


import handleException.LiveTrackingException;

import java.io.IOException;

import notification.SendNotificationException;

public interface LiveTrackingSchedulerService {

	void autoStopLiveTracking() throws LiveTrackingException, IOException, NumberFormatException, SendNotificationException;
	
}
