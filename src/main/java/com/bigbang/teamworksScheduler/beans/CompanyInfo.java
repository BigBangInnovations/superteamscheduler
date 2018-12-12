package com.bigbang.teamworksScheduler.beans;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyInfo {

	private long companyid;
	private String name;
	private String AddressLine1;
	private String AddressLine2;
	private long userid;
	private String workingDays;
	private String startTime;
	private String endTime;
	private int notificationLevel;
	@SerializedName("AutoLeaveUpdate")
	private boolean autoLeaveUpdate;
	private boolean payrollEnabled;
	private String minimumWorkingTime;
	private String avgWorkingTime;
	private boolean saturdayPolicy;
	private String workingSaturday;
	private String trackingStartTime;
	private String trakingEndTime;
	private long modifiedBy;
	private long createdBy;
	private String logo;
	private int trackingInteval;
	private boolean isFifteenMinTracking;
	private boolean otherDeviceLogin;
	private boolean isShelfieAllowed;
	private boolean isGeoFencing;
	private int radius;

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}


	public int getTrackingInteval() {
		return trackingInteval;
	}

	public void setTrackingInteval(int trackingInteval) {
		this.trackingInteval = trackingInteval;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public boolean isAutoLeaveUpdate() {
		return autoLeaveUpdate;
	}

	public void setAutoLeaveUpdate(boolean autoLeaveUpdate) {
		this.autoLeaveUpdate = autoLeaveUpdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddressLine1() {
		return AddressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		AddressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return AddressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		AddressLine2 = addressLine2;
	}

	public String getWorkingSaturday() {
		return workingSaturday;
	}

	public void setWorkingSaturday(String workingSaturday) {
		this.workingSaturday = workingSaturday;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public String getTrackingStartTime() {
		return trackingStartTime;
	}

	public void setTrackingStartTime(String trackingStartTime) {
		this.trackingStartTime = trackingStartTime;
	}

	/**
	 * @return CompanyID
	 */
	public long getCompanyid() {
		return companyid;
	}

	/**
	 * @param companyid
	 *            companyID
	 */
	public void setCompanyid(final long companyid) {
		this.companyid = companyid;
	}

	/**
	 * @return NotificationLevel
	 */
	public int getNotificationLevel() {
		return notificationLevel;
	}

	/**
	 * @param notificationLevel
	 *            NotificationLevel
	 */
	public void setNotificationLevel(final int notificationLevel) {
		this.notificationLevel = notificationLevel;
	}

	/**
	 * @return WorkingDays
	 */
	public String getWorkingDays() {
		return workingDays;
	}

	/**
	 * @param workingDays
	 *            WorkingDays
	 */
	public void setWorkingDays(final String workingDays) {
		this.workingDays = workingDays;
	}

	/**
	 * @return StartTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            StartTime
	 */
	public void setStartTime(final String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return EndTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            EndTime
	 */
	public void setEndTime(final String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return PayrollEnabled
	 */
	public boolean isPayrollEnabled() {
		return payrollEnabled;
	}

	/**
	 * @param isPayrollEnabled
	 *            true/false
	 */
	public void setPayrollEnabled(boolean isPayrollEnabled) {
		this.payrollEnabled = isPayrollEnabled;
	}

	/**
	 * @return UserID
	 */
	public long getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            UserID
	 */
	public void setUserid(final long userid) {
		this.userid = userid;
	}

	public String getMinimumWorkingTime() {
		return minimumWorkingTime;
	}

	public void setMinimumWorkingTime(String minimumWorkingTime) {
		this.minimumWorkingTime = minimumWorkingTime;
	}

	public String getAvgWorkingTime() {
		return avgWorkingTime;
	}

	public void setAvgWorkingTime(String avgWorkingTime) {
		this.avgWorkingTime = avgWorkingTime;
	}

	public boolean isSaturdayPolicy() {
		return saturdayPolicy;
	}

	public void setSaturdayPolicy(boolean saturdayPolicy) {
		this.saturdayPolicy = saturdayPolicy;
	}

	public String getTrakingEndTime() {
		return trakingEndTime;
	}

	public void setTrakingEndTime(String trakingEndTime) {
		this.trakingEndTime = trakingEndTime;
	}

	public boolean isFifteenMinTracking() {
		return isFifteenMinTracking;
	}

	public void setFifteenMinTracking(boolean isFifteenMinTracking) {
		this.isFifteenMinTracking = isFifteenMinTracking;
	}

	public boolean isOtherDeviceLogin() {
		return otherDeviceLogin;
	}

	public void setOtherDeviceLogin(boolean otherDeviceLogin) {
		this.otherDeviceLogin = otherDeviceLogin;
	}

	public boolean isShelfieAllowed() {
		return isShelfieAllowed;
	}

	public void setShelfieAllowed(boolean isShelfieAllowed) {
		this.isShelfieAllowed = isShelfieAllowed;
	}

	public boolean isGeoFencing() {
		return isGeoFencing;
	}

	public void setGeoFencing(boolean isGeoFencing) {
		this.isGeoFencing = isGeoFencing;
	}
}
