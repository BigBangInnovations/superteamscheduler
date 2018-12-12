package com.bigbang.teamworksScheduler.service;

import java.io.IOException;

import notification.SendNotificationException;

public interface LeaveSchedulerService {

	void autoLeaveUpdate() throws IOException, SendNotificationException;

}
