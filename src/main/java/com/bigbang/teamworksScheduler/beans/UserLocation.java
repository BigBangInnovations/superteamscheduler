package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class UserLocation {
	
	private long userid;
	private long companyid;
	private String time;
	private String location;
	private Date date;
	private double latitude;
	private double longitude;
	private Users user;

	private String FirstName;
	private String LastName;
	private String imageURL;
	@SerializedName("isGPSOn")
	private boolean gpsOn;
	@SerializedName("isMockLocation")
	private boolean mockLocation;
	private String trackingType;
	private int isLiveTracking;
	private String distance;
	private double distanceValue;
//	private double distance;
	
	
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
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
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public boolean isGpsOn() {
		return gpsOn;
	}
	public void setGpsOn(boolean gpsOn) {
		this.gpsOn = gpsOn;
	}
	public boolean isMockLocation() {
		return mockLocation;
	}
	public void setMockLocation(boolean mockLocation) {
		this.mockLocation = mockLocation;
	}
	public String getTrackingType() {
		return trackingType;
	}
	public void setTrackingType(String trackingType) {
		this.trackingType = trackingType;
	}
	public int getIsLiveTracking() {
		return isLiveTracking;
	}
	public void setIsLiveTracking(int isLiveTracking) {
		this.isLiveTracking = isLiveTracking;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
//	public double getDistance() {
//		return distance;
//	}
//	public void setDistance(double distance) {
//		this.distance = distance;
//	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public double getDistanceValue() {
		return distanceValue;
	}
	public void setDistanceValue(double distanceValue) {
		this.distanceValue = distanceValue;
	}
}
