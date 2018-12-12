package com.bigbang.teamworksScheduler.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaEmail {
	private static final Logger LOG = LogManager.getLogger(JavaEmail.class);
		
		Properties emailProperties;
		Session mailSession;
		MimeMessage emailMessage;

		public void setMailServerProperties() {

			String emailPort = "25";//gmail's smtp port
			LOG.debug("In Java class");
			emailProperties = System.getProperties();
			emailProperties.put("mail.smtp.port", emailPort);
			emailProperties.put("mail.smtp.auth", "true");
			emailProperties.put("mail.smtp.starttls.enable", "true");
			emailProperties.put("mail.smtp.timeout", "300000");
		}

		public void createEmailMessage() throws AddressException,
				MessagingException {
			LOG.debug("In create mail");
			String[] toEmails = {"snhsnh789@gmail.com"};
			String emailSubject = "Test";
			String emailBody = "This is an test email";

			mailSession = Session.getDefaultInstance(emailProperties, null);
			emailMessage = new MimeMessage(mailSession);

			LOG.debug("Emails "+toEmails);
			for (int i = 0; i < toEmails.length; i++) {
				LOG.debug("sending to "+toEmails[i]);
				emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
			}

			emailMessage.setSubject(emailSubject);
			emailMessage.setContent(emailBody, "text/html");//for a html email
			LOG.debug("End create");
			//emailMessage.setText(emailBody);// for a text email

		}

		public void sendEmail() throws AddressException, MessagingException {

			LOG.debug("In send Email method");
			String emailHost = "mail.bigbanginnovations.in";
			String fromUser = "support@bigbanginnovations.in"; //just the id alone without @gmail.com
			String fromUserEmailPassword = "sprt@@098!!3";

			Transport transport = mailSession.getTransport("smtp");

			transport.connect(emailHost, fromUser, fromUserEmailPassword);
			transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
			transport.close();
			LOG.debug("Email sent successfully.");
		}

}
