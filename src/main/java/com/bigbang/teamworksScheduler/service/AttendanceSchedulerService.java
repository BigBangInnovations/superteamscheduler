package com.bigbang.teamworksScheduler.service;

import handleException.AutoCheckInException;
import handleException.AutoSchedulerException;

import java.io.IOException;

import notification.SendNotificationException;

public interface AttendanceSchedulerService {

	void autoCheckIn() throws AutoCheckInException, IOException, NumberFormatException, SendNotificationException;

	void autoCheckInTest() throws AutoCheckInException, IOException, NumberFormatException, SendNotificationException;
	
	void autoCheckOut() throws IOException, AutoCheckInException, NumberFormatException, SendNotificationException;

	void attendanceScheduler() throws IOException, AutoSchedulerException;
	
	void attendanceSchedulerTest() throws IOException, AutoSchedulerException;

	void absentUserScheduler() throws AutoSchedulerException;
	
	void autoUpdateTravelAddress() throws IOException, NumberFormatException, SendNotificationException, Exception;

}
