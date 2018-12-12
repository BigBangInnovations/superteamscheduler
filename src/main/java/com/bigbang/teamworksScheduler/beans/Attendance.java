package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class Attendance {

	private long id;
	private Date attendanceDate;
	private Users user;
	private long companyID;
	private Date checkInTime;
	private long checkInAddressID;
	private Date checkOutTime;
	private long checkOutAddressID;
	private boolean present;
	private String manualAttendance;
	private boolean manualApproved;
	private Date timeIn;
	private Date timeOut;
	private String reason;
	private Date updatedTimeIn;
	private Date updatedTimeOut;
	private boolean late;
	private boolean early;
	private long minLate;
	private long minEarly;
	private long modifiedBy;
	private double latitude;
	private double longitude;
	private boolean checkInApproved;
	private boolean checkOutApproved;
	private String leaveType;
	private String leaveDay;
	private String totalTime;
	private String totalHours;
	private String address;
	private long clientId;
	private String clientName;
	private String checkInPhotoURL;
	private String checkOutPhotoURL;
	private int checkInAttendanceType;
	private int checkOutAttendanceType;
	private String checkOutAddress;
	private String checkInAddress;
	private long leaveTypeID;

	public String getCheckOutAddress() {
		return checkOutAddress;
	}

	public void setCheckOutAddress(String checkOutAddress) {
		this.checkOutAddress = checkOutAddress;
	}

	public String getCheckInAddress() {
		return checkInAddress;
	}

	public void setCheckInAddress(String checkInAddress) {
		this.checkInAddress = checkInAddress;
	}

	@Expose(serialize = false, deserialize = false)
	private Date startTime;
	@Expose(serialize = false, deserialize = false)
	private Date endTime;
	@Expose(serialize = false, deserialize = false)
	private String totalWorkTime;

	public Date getStartTime() {
		if (timeIn != null)
			startTime = timeIn;
		else if (checkInTime != null)
			startTime = checkInTime;
		return startTime;
	}

	public Date getEndTime() {

		if (timeOut != null)
			endTime = timeOut;
		else if (checkOutTime != null)
			endTime = checkOutTime;
		return endTime;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public String getLeaveDay() {
		return leaveDay;
	}

	public void setLeaveDay(String leaveDay) {
		this.leaveDay = leaveDay;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getTotalWorkTime() {
		if (getStartTime() == null || getEndTime() == null) {
			return "0:0";
		}
		long diff = getEndTime().getTime() - getStartTime().getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		totalWorkTime = diffHours + ":" + diffMinutes;
		return totalWorkTime;
	}

	public boolean isManualApproved() {
		return manualApproved;
	}

	public void setManualApproved(boolean manualApproved) {
		this.manualApproved = manualApproved;
	}

	public boolean isCheckInApproved() {
		return checkInApproved;
	}

	public void setCheckInApproved(boolean checkInApproved) {
		this.checkInApproved = checkInApproved;
	}

	public boolean isCheckOutApproved() {
		return checkOutApproved;
	}

	public void setCheckOutApproved(boolean checkOutApproved) {
		this.checkOutApproved = checkOutApproved;
	}

	public Date getAttendanceDate() {
		return attendanceDate;
	}

	public void setAttendanceDate(Date attendanceDate) {
		this.attendanceDate = attendanceDate;
	}

	public long getMinEarly() {
		return minEarly;
	}

	public void setMinEarly(long minEarly) {
		this.minEarly = minEarly;
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

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public long getCompanyID() {
		return companyID;
	}

	public void setCompanyID(long companyID) {
		this.companyID = companyID;
	}

	public Date getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Date getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public boolean isPresent() {
		return present;
	}

	public void setPresent(boolean present) {
		this.present = present;
	}

	public String getManualAttendance() {
		return manualAttendance;
	}

	public void setManualAttendance(String manualAttendance) {
		this.manualAttendance = manualAttendance;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}

	public Date getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Date timeOut) {
		this.timeOut = timeOut;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getUpdatedTimeIn() {
		return updatedTimeIn;
	}

	public void setUpdatedTimeIn(Date updatedTimeIn) {
		this.updatedTimeIn = updatedTimeIn;
	}

	public Date getUpdatedTimeOut() {
		return updatedTimeOut;
	}

	public void setUpdatedTimeOut(Date updatedTimeOut) {
		this.updatedTimeOut = updatedTimeOut;
	}

	public boolean isLate() {
		return late;
	}

	public void setLate(boolean late) {
		this.late = late;
	}

	public boolean isEarly() {
		return early;
	}

	public void setEarly(boolean early) {
		this.early = early;
	}

	public long getMinLate() {
		return minLate;
	}

	public void setMinLate(long minLate) {
		this.minLate = minLate;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public long getCheckInAddressID() {
		return checkInAddressID;
	}

	public void setCheckInAddressID(long checkInAddressID) {
		this.checkInAddressID = checkInAddressID;
	}

	public long getCheckOutAddressID() {
		return checkOutAddressID;
	}

	public void setCheckOutAddressID(long checkOutAddressID) {
		this.checkOutAddressID = checkOutAddressID;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}

	public String getCheckInPhotoURL() {
		return checkInPhotoURL;
	}

	public void setCheckInPhotoURL(String checkInPhotoURL) {
		this.checkInPhotoURL = checkInPhotoURL;
	}

	public String getCheckOutPhotoURL() {
		return checkOutPhotoURL;
	}

	public void setCheckOutPhotoURL(String checkOutPhotoURL) {
		this.checkOutPhotoURL = checkOutPhotoURL;
	}

	public int getCheckInAttendanceType() {
		return checkInAttendanceType;
	}

	public void setCheckInAttendanceType(int checkInAttendanceType) {
		this.checkInAttendanceType = checkInAttendanceType;
	}

	public int getCheckOutAttendanceType() {
		return checkOutAttendanceType;
	}

	public void setCheckOutAttendanceType(int checkOutAttendanceType) {
		this.checkOutAttendanceType = checkOutAttendanceType;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public long getLeaveTypeID() {
		return leaveTypeID;
	}

	public void setLeaveTypeID(long leaveTypeID) {
		this.leaveTypeID = leaveTypeID;
	}
	
}
