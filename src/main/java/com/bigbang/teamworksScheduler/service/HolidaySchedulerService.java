package com.bigbang.teamworksScheduler.service;

import notification.SendNotificationException;

public interface HolidaySchedulerService {

	void autoHolidayUpdate() throws SendNotificationException;

}
