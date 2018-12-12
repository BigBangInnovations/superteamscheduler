package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import notification.SendNotificationException;

public interface MissedCheckInSchedulerService {
	
	void fetchMissedCheckInUsers() throws IOException, NumberFormatException, 
	SendNotificationException, NoSuchAlgorithmException, ParseException, MessagingException;
	
	void sendBulkEmails() throws IOException, AddressException, MessagingException;
}
