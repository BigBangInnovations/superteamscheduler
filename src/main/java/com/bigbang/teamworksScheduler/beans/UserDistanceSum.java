package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

public class UserDistanceSum {

	private long userid;
	private long companyid;
	private Date time;
	private String location;
	private Date date;

	private String FirstName;
	private String LastName;
	private double distanceValue;
	
	public long getUserid() {
		return userid;
	}
	
	public void setUserid(long userid) {
		this.userid = userid;
	}
	
	public long getCompanyid() {
		return companyid;
	}
	
	public void setCompanyid(long companyid) {
		this.companyid = companyid;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getFirstName() {
		return FirstName;
	}
	
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	
	public String getLastName() {
		return LastName;
	}
	
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	
	public double getDistanceValue() {
		return distanceValue;
	}
	
	public void setDistanceValue(double distanceValue) {
		this.distanceValue = distanceValue;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
