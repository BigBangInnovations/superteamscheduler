package com.bigbang.teamworksScheduler.beans;

public class User {

	private long userID;
	private String gcmID;
	private Company company;

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getGcmID() {
		return gcmID;
	}

	public void setGcmID(String gcmID) {
		this.gcmID = gcmID;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
