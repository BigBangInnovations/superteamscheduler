package com.bigbang.teamworksScheduler.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.CompanyInfo;
import com.bigbang.teamworksScheduler.beans.Holidays;


public interface CompanyDAO {

	/**
	 * @param company
	 *            CompanyObject
	 * @return Status
	 */
	//void updateCompanyInfo(CompanyInfo company, boolean isUpdate);

	/**
	 * @param companyid
	 *            CompanyID
	 * @return CompanyObject
	 */
	CompanyInfo getCompanybyID(long companyid);

	/**
	 * @return List of Default Privileges
	 */
	//List<Privilege> getPrivileges();

	/**
	 * @param companyid
	 *            CompanyID
	 * @return List of Privileges
	 */
	//List<Privilege> getRolePrivilege(long companyid);

	/**
	 * @param companyid
	 *            CompanyID
	 * @return Status
	 */
	void clearRolePrivilege(long companyid);

	/**
	 * @param companyid
	 *            CompanyID
	 * @param roleid
	 *            RoleID
	 * @return List of Privileges
	 */
	//List<Privilege> getUserPrivileges(long companyid, long roleid);

	/**
	 * @param companyid
	 *            CompanyID
	 * @return List of Holidays
	 */
	//List<Holidays> getHolidays(long companyid);

	/**
	 * @param companyid
	 *            CompanyID
	 * @return CompanyTime - StartTime, EndTime, WorkingHours
	 */
	Map<String, Object> getCompanyTime(long companyid);

	/**
	 * @param companyID
	 *            CompanyID
	 * @return Holidays
	 */
	List<Date> getHolidayDates(long companyID);

	/**
	 * @param companyID
	 *            CompanyID
	 * @return Working Days
	 */
	String getWorkingDays(long companyID);

	/**
	 * @param companyID
	 *            CompanyID
	 * @return CreationDate
	 */
	Date getCompanyCreateDate(long companyID);

	void setAutoLeaveUpdate(long companyId, boolean active);

	boolean getAutoLeaveUpdate(long companyId);

//	void addRolePrivilege(long companyid, long modifiedBy, List<Privilege> privilegeIdList);

	public CompanyInfo getCompanyDetails(long companyID);

//	public void updatePayrollDetails(CompanyPayroll companyPayroll) throws AddCompanyPayrollException;
	
//	public List<Privilege> getManualAttendanceRolePrivilege(final long companyid, int previlageID);
	
	public List<CompanyInfo> getAllCompanyDetails();
	
//	public void addManualRolePrivilege(final long roleid , final long previlegeID ,  final long modifiedBy, List<Company> companyIDList);
	
	int getCompanyRadius(long companyID);
	
	boolean getCompanyGeoFencing(long companyID);
	
	public List<Holidays> getCompanyHolidays(final long companyid);
	
	public boolean checkIfCompanyHoliday(long companyID, Date Date);
	
	public List<Long> getAllActiveCompaniesDetails();
	
}
