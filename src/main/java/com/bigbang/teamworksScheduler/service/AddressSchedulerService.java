package com.bigbang.teamworksScheduler.service;

import java.io.IOException;

import notification.SendNotificationException;

public interface AddressSchedulerService {
	void autoUpdatAddress() throws IOException, NumberFormatException, SendNotificationException,Exception;
}
