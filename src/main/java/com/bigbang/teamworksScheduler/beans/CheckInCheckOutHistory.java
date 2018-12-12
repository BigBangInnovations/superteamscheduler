package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

public class CheckInCheckOutHistory {

	private long id;
	private long attendanceID;
	private long userID;
	private long companyID;
	private double checkInLatitude;
	private double checkInLongitude;
	private double checkInDistance;
	private String checkInPhoto;
	private String checkInAddress;
	private double checkOutLatitude;
	private double checkOutLongitude;
	private double checkOutDistance;
	private String checkOutPhoto;
	private String checkOutAddress;
	private Date createdTime;
	private Date modifiedTime;
	private int active;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAttendanceID() {
		return attendanceID;
	}
	public void setAttendanceID(long attendanceID) {
		this.attendanceID = attendanceID;
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
	public double getCheckInLatitude() {
		return checkInLatitude;
	}
	public void setCheckInLatitude(double checkInLatitude) {
		this.checkInLatitude = checkInLatitude;
	}
	public double getCheckInLongitude() {
		return checkInLongitude;
	}
	public void setCheckInLongitude(double checkInLongitude) {
		this.checkInLongitude = checkInLongitude;
	}
	public double getCheckInDistance() {
		return checkInDistance;
	}
	public void setCheckInDistance(double checkInDistance) {
		this.checkInDistance = checkInDistance;
	}
	public String getCheckInPhoto() {
		return checkInPhoto;
	}
	public void setCheckInPhoto(String checkInPhoto) {
		this.checkInPhoto = checkInPhoto;
	}
	public String getCheckInAddress() {
		return checkInAddress;
	}
	public void setCheckInAddress(String checkInAddress) {
		this.checkInAddress = checkInAddress;
	}
	public double getCheckOutLatitude() {
		return checkOutLatitude;
	}
	public void setCheckOutLatitude(double checkOutLatitude) {
		this.checkOutLatitude = checkOutLatitude;
	}
	public double getCheckOutLongitude() {
		return checkOutLongitude;
	}
	public void setCheckOutLongitude(double checkOutLongitude) {
		this.checkOutLongitude = checkOutLongitude;
	}
	public double getCheckOutDistance() {
		return checkOutDistance;
	}
	public void setCheckOutDistance(double checkOutDistance) {
		this.checkOutDistance = checkOutDistance;
	}
	public String getCheckOutPhoto() {
		return checkOutPhoto;
	}
	public void setCheckOutPhoto(String checkOutPhoto) {
		this.checkOutPhoto = checkOutPhoto;
	}
	public String getCheckOutAddress() {
		return checkOutAddress;
	}
	public void setCheckOutAddress(String checkOutAddress) {
		this.checkOutAddress = checkOutAddress;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	
}
