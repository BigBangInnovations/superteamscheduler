package com.bigbang.teamworksScheduler.service;

import java.io.IOException;

import notification.SendNotificationException;

public interface TrackingSchedulerService {

	void autoTrackingUpdate() throws IOException, NumberFormatException, SendNotificationException;
	
}
