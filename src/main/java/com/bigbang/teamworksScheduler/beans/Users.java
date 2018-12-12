package com.bigbang.teamworksScheduler.beans;

public class Users {
	
	private long userId;
	private long companyId;
	private String firstName;
	private String lastName;
	private String deviceid;
	private String picture;
	private String desc;
	private String emailID;
	private String password;
	private String DeviceName;
	private String MobileNo1;
	private String managerName;
	private String managerContact;
	private String employeeCode;
	private int roleID;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDeviceName() {
		return DeviceName;
	}
	public void setDeviceName(String deviceName) {
		DeviceName = deviceName;
	}
	public String getMobileNo1() {
		return MobileNo1;
	}
	public void setMobileNo1(String mobileNo1) {
		MobileNo1 = mobileNo1;
	}
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerContact() {
		return managerContact;
	}
	public void setManagerContact(String managerContact) {
		this.managerContact = managerContact;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public int getRoleID() {
		return roleID;
	}
	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}
}
