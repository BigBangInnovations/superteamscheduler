package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

public class UserAttendance 
{
	private long ID;
	private long userID;
	private long companyID;
	private Date date;
	private Date inTime;
	private Date outTime;
	private boolean onLeave;
	private boolean isHoliday;
	private boolean isSaturday;
	private boolean isSunday;
	private String hourWorked;
	private String status;
	private long shorthours;
	private boolean lateComing;
	private boolean earlyGoing;
	private boolean isAbsent;
	
	private long leaveTypeID;
	
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}
	public String getHourWorked() {
		return hourWorked;
	}
	public void setHourWorked(String hourWorked) {
		this.hourWorked = hourWorked;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getShorthours() {
		return shorthours;
	}
	public void setShorthours(long shorthours) {
		this.shorthours = shorthours;
	}
	public boolean isOnLeave() {
		return onLeave;
	}
	public void setOnLeave(boolean onLeave) {
		this.onLeave = onLeave;
	}
	public boolean isHoliday() {
		return isHoliday;
	}
	public void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	public boolean isSaturday() {
		return isSaturday;
	}
	public void setSaturday(boolean isSaturday) {
		this.isSaturday = isSaturday;
	}
	public boolean isSunday() {
		return isSunday;
	}
	public void setSunday(boolean isSunday) {
		this.isSunday = isSunday;
	}
	public boolean isLateComing() {
		return lateComing;
	}
	public void setLateComing(boolean lateComing) {
		this.lateComing = lateComing;
	}
	public boolean isEarlyGoing() {
		return earlyGoing;
	}
	public void setEarlyGoing(boolean earlyGoing) {
		this.earlyGoing = earlyGoing;
	}
	public boolean isAbsent() {
		return isAbsent;
	}
	public void setAbsent(boolean isAbsent) {
		this.isAbsent = isAbsent;
	}
	public long getLeaveTypeID() {
		return leaveTypeID;
	}
	public void setLeaveTypeID(long leaveTypeID) {
		this.leaveTypeID = leaveTypeID;
	}
	
	
	
}
