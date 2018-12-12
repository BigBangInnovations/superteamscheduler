package com.bigbang.teamworksScheduler.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Holidays {
	private long holidayId;
	private Date holidayDate;
	private String holidayName;
	private boolean isNationalHoliday;
	private long branchID;
	private boolean isMandatory;
	private long companyID;

	/**
	 * @param holidayDate
	 *            Date
	 * @param holidayName
	 *            Name
	 */
	public Holidays(final Date holidayDate, final String holidayName) {
		this.holidayDate = holidayDate;
		this.holidayName = holidayName;
	}

	/**
	 * @return String
	 */
	public String toString() {
		DateFormat currentDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return "{\"holiday_date\":" + currentDateFormat.format(this.holidayDate) + ",\"holiday_name\":"
				+ this.holidayName + "}";
	}

	public long getHolidayId() {
		return holidayId;
	}

	public void setHolidayId(long holidayId) {
		this.holidayId = holidayId;
	}

	public Date getHolidayDate() {
		return holidayDate;
	}

	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public boolean isNationalHoliday() {
		return isNationalHoliday;
	}

	public void setNationalHoliday(boolean isNationalHoliday) {
		this.isNationalHoliday = isNationalHoliday;
	}

	public long getBranchID() {
		return branchID;
	}

	public void setBranchID(long branchID) {
		this.branchID = branchID;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public long getCompanyID() {
		return companyID;
	}

	public void setCompanyID(long companyID) {
		this.companyID = companyID;
	}
	
	
}
