package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

public class TrackingBean {

	long userID;
	long companyID;
	String latitude;
	String longitude;
	Date dateTime;

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public long getCompanyID() {
		return companyID;
	}

	public void setCompanyID(long companyID) {
		this.companyID = companyID;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}
