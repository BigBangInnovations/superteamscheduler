package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

public class UserLiveTrackingStatus {

	private long ID;
	private long userID;
	private long companyID;
	private String code;
	private int isLiveTracking;
	private Date createdTime;
	private long createdBy;
	private Date lastModified;
	private long LastModifiedBy;
	
	
	public long getID() {
		return ID;
	}
	
	public void setID(long iD) {
		ID = iD;
	}
	
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
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public int getIsLiveTracking() {
		return isLiveTracking;
	}
	
	public void setIsLiveTracking(int isLiveTracking) {
		this.isLiveTracking = isLiveTracking;
	}
	
	public Date getCreatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
	public long getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public long getLastModifiedBy() {
		return LastModifiedBy;
	}
	
	public void setLastModifiedBy(long lastModifiedBy) {
		LastModifiedBy = lastModifiedBy;
	}
	
}
