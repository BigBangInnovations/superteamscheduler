package com.bigbang.teamworksScheduler.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.mail.smtp.SMTPMessage;


public class MailUtil {

	/**
	 * Configuring setup for sending mails or messages.
	 * 
	 * @author Poorvi Nigotiya
	 */
	private static final Logger LOG = LogManager.getLogger(MailUtil.class);

	private Session session;

	/**
	 * Send mail to the emailID with body as the content and cid as unique photo id
	 * 
	 * @param emailID
	 * @param body
	 * @param cid
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
//	public void sendHTMLMail(String emailID, String filePath, String subject, String body)
//			throws NoSuchAlgorithmException {
//		setMailingProperties();
//		String cid = generateCid();
//
//		SMTPMessage message = new SMTPMessage(session);
//		MimeMultipart content = new MimeMultipart();
//		MimeBodyPart textPart = new MimeBodyPart();
//		MimeBodyPart imagePart = new MimeBodyPart();
//		MimeBodyPart filePart = new MimeBodyPart();
//
//		String contentID = "<" + cid + ">";
//		try {
//			// adding HTML Text to message
//			textPart.setText(body, "US-ASCII", "html");
//			content.addBodyPart(textPart);
//
//			// Adding Image to message
//			imagePart.attachFile("/home/teamworks/attachments/" + "logo.jpg");
//			// imagePart.attachFile("C:/logs/logo.jpg");
//			imagePart.setContentID(contentID);
//			imagePart.setDisposition(MimeBodyPart.INLINE);
//			content.addBodyPart(imagePart);
//
//			if (filePath != null) {
//				filePart.attachFile(filePath);
//				filePart.setContentID(filePath);
//				content.addBodyPart(filePart);
//			}else{
//				content.getBodyPart("");
//			}
//			message.setFrom(new InternetAddress("support@bigbanginnovations.in"));
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailID));
//			message.setSubject(subject);
//			message.setContent(content);
//
//			LOG.info("Sending mail to: " + emailID);
//
//			Transport.send(message);
//			LOG.info("Mail Sent successfully");
//
//		} catch (AddressException e) {
//			e.printStackTrace();
//			LOG.error(" Invalid email address", e);
//		} catch (MessagingException e) {
//			e.printStackTrace();
//			LOG.error(" Unable to send message", e);
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOG.error("Unable to send message", e);
//		}
//	}
	public void sendHTMLMail1(String emailID, String filePath, String subject, String body,Transport transport)
			throws NoSuchAlgorithmException {
//		setMailingProperties();
		String cid = generateCid();

		SMTPMessage message = new SMTPMessage(session);
		MimeMultipart content = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();
		MimeBodyPart imagePart = new MimeBodyPart();
		MimeBodyPart filePart = new MimeBodyPart();

		String contentID = "<" + cid + ">";
		try {
			// adding HTML Text to message
			textPart.setText(body, "US-ASCII", "html");
			content.addBodyPart(textPart);

			// Adding Image to message
			imagePart.attachFile("/mnt/data/attachments/" + "logo.jpg");
//			imagePart.attachFile("/home/teamworks/attachments/" + "logo.jpg");
			// imagePart.attachFile("C:/logs/logo.jpg");
			imagePart.setContentID(contentID);
			imagePart.setDisposition(MimeBodyPart.INLINE);
			content.addBodyPart(imagePart);

			if (filePath != null) {
				filePart.attachFile(filePath);
				filePart.setContentID(filePath);
				content.addBodyPart(filePart);
			}else{
				content.getBodyPart("");
			}
			message.setFrom(new InternetAddress("support@bigbanginnovations.in"));
			InternetAddress[] address = {new InternetAddress(emailID)};
			   message.setRecipients(Message.RecipientType.TO, address);
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailID));
			message.setSubject(subject);
			message.setContent(content);

			LOG.info("Sending mail to: " + emailID);

			transport.sendMessage(message,address);
			LOG.info("Mail Sent successfully");

		} catch (AddressException e) {
			e.printStackTrace();
			LOG.error(" Invalid email address", e);
		} catch (MessagingException e) {
			e.printStackTrace();
			LOG.error(" Unable to send message", e);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Unable to send message", e);
		}
	}
	public void sendHTMLMail1(String emailID, String filePath, String subject, String body,Transport transport,Session session)
			throws NoSuchAlgorithmException {
//		setMailingProperties();
		String cid = generateCid();

		SMTPMessage message = new SMTPMessage(session);
		MimeMultipart content = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();
		MimeBodyPart imagePart = new MimeBodyPart();
		MimeBodyPart filePart = new MimeBodyPart();

		String contentID = "<" + cid + ">";
		try {
			// adding HTML Text to message
			textPart.setText(body, "US-ASCII", "html");
			content.addBodyPart(textPart);

			// Adding Image to message
			imagePart.attachFile("/mnt/data/attachments/" + "logo.jpg");
//			imagePart.attachFile("/home/teamworks/attachments/" + "logo.jpg");
			// imagePart.attachFile("C:/logs/logo.jpg");
			imagePart.setContentID(contentID);
			imagePart.setDisposition(MimeBodyPart.INLINE);
			content.addBodyPart(imagePart);

			if (filePath != null) {
				filePart.attachFile(filePath);
				filePart.setContentID(filePath);
				content.addBodyPart(filePart);
			}else{
				content.getBodyPart("");
			}
			message.setFrom(new InternetAddress("support@bigbanginnovations.in"));
			InternetAddress[] address = {new InternetAddress(emailID)};
			   message.setRecipients(Message.RecipientType.TO, address);
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailID));
			message.setSubject(subject);
			message.setContent(content);

			LOG.info("Sending mail to: " + emailID);

			transport.sendMessage(message,address);
			LOG.info("Mail Sent successfully");

		} catch (AddressException e) {
			e.printStackTrace();
			LOG.error(" Invalid email address", e);
		} catch (MessagingException e) {
			e.printStackTrace();
			LOG.error(" Unable to send message", e);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Unable to send message", e);
		}
	}
	// This method will read all the mailing properties from database
//	public void setMailingProperties() {
//
//		LOG.info("Setting mailing properties");
//		Properties properties = new Properties();
//		properties.put("mail.transport.protocol", "smtp");
//		properties.put("mail.smtp.starttls.enable", "true");
//		properties.put("mail.smtp.host", "mail.bigbanginnovations.in");
//		properties.put("mail.smtp.auth", "true");
//		properties.put("mail.smtp.port", "25");
//		properties.put("mail.host", "mail.bigbanginnovations.in");
//		properties.put("mail.smtp.timeout", "30000");
//
//		session = Session.getInstance(properties, new javax.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("support@bigbanginnovations.in",
//						"sprt@@098!!3");
//			}
//		});
//	}

	public Properties setMailingProperties1() {

		  LOG.info("Setting mailing properties");
		  Properties properties = new Properties();
		  properties.put("mail.transport.protocol", "smtp");
		  properties.put("mail.smtp.starttls.enable", "true");
		  properties.put("mail.smtp.host", "email-smtp.us-west-2.amazonaws.com");
		  properties.put("mail.smtp.auth", "true");
		  properties.put("mail.smtp.port", "25");
		  properties.put("mail.smtp.timeout", "300000");

		  return properties;
	}
	
	/**
	 * This method generated unique Content ID for Email
	 * 
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
	public static String generateCid() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(32);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return DatatypeConverter.printHexBinary(raw);
	}
}
