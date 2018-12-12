package com.bigbang.teamworksScheduler.beans;

public class Company {

	private long CompanyID;
	private String workingStartTime;
	private String workingEndTime;

	public long getCompanyID() {
		return CompanyID;
	}

	public void setCompanyID(long companyID) {
		CompanyID = companyID;
	}

	public String getWorkingStartTime() {
		return workingStartTime;
	}

	public void setWorkingStartTime(String workingStartTime) {
		this.workingStartTime = workingStartTime;
	}

	public String getWorkingEndTime() {
		return workingEndTime;
	}

	public void setWorkingEndTime(String workingEndTime) {
		this.workingEndTime = workingEndTime;
	}

}
