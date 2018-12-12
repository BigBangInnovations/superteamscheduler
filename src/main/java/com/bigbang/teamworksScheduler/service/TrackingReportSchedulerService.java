package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import notification.SendNotificationException;

public interface TrackingReportSchedulerService {

	void autoTrackingReportSend() throws IOException, NumberFormatException, SendNotificationException, NoSuchProviderException, MessagingException, NoSuchAlgorithmException, ParseException;
	
}
