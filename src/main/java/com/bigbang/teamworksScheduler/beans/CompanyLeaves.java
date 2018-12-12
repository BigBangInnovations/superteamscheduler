package com.bigbang.teamworksScheduler.beans;

public class CompanyLeaves {

	private long id;
	private long companyId;
	private int leaveTypeId;
	private double noOfLeaves;
	private String leaveUpdateCycle;
	private boolean active;
	private long modifiedBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public int getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(int companyTypeId) {
		this.leaveTypeId = companyTypeId;
	}

	public double getNoOfLeaves() {
		return noOfLeaves;
	}

	public void setNoOfLeaves(double noOfLeaves) {
		this.noOfLeaves = noOfLeaves;
	}

	public String getLeaveUpdateCycle() {
		return leaveUpdateCycle;
	}

	public void setLeaveUpdateCycle(String leaveUpdateCycle) {
		this.leaveUpdateCycle = leaveUpdateCycle;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
