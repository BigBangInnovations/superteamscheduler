package com.bigbang.teamworksScheduler.beans;

import java.util.Date;

public class CompanyAttendance 
{
	private long id;
	private String companyShiftName;
	private long companyID;
	private long userID;
	private boolean flexibleHoursAllowed;
	private boolean countWorkingHoursBeforeCheckInFrom;
	private Date checkInFrom;
	private Date checkInTo;
	private Date checkOutFrom;
	private Date checkOutTo;
	private boolean countWorkingHoursAfterCheckOutTo;
	private boolean isMinimumWorkingHourForPresence;
	private int minimumWorkingHourForPresence;
	private boolean isHalfDayWorkingHour;
	private int halfDayWorkingHour;
	private int shiftHour;
	private int maxShiftHour;
	private boolean isMinShiftHour;
	private int minShiftHour;
	private boolean isEarlyGoingHalfDay;
	private boolean isLateComingHalfDay;
	private Date maxEarlyGoTime;
	private Date maxLateComeTime;
	private boolean saturdayCountedInWorkingHours;
	private boolean sundayCountedInWorkingHours;
	private boolean isCalculateDaily;
	private boolean isCalculateWeekly;
	private boolean isCalculateMonthly;
	private boolean lateleavingNextDayLateAllowed;
	private Date lateleavingNextDateLatetime;
	private Date maximumLateAllowedForLateLeaving;
	private int weeklyShortHoursHalfDay;
	private String aftermidnightMaxAllowedTime;
	private int minimumExtraMonthHours;
	private int hoursForEveryHalfDayReversal;
	private int halfCutPolicyPerHour;
	private int isCoreHours;
	private String workingDays;
	private String companyStartTime;
	private String companyEndTime;
	private boolean saturdayPolicy;
	private String workingSaturday;
	private long modifiedBy;
	private long isDefaultShift;
	private boolean approvalmaxworkinghours;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCompanyShiftName() {
		return companyShiftName;
	}
	public void setCompanyShiftName(String companyShiftName) {
		this.companyShiftName = companyShiftName;
	}
	public long getCompanyID() {
		return companyID;
	}
	public void setCompanyID(long companyID) {
		this.companyID = companyID;
	}
	public long getUserID() {
		return userID;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
	public boolean isFlexibleHoursAllowed() {
		return flexibleHoursAllowed;
	}
	public void setFlexibleHoursAllowed(boolean flexibleHoursAllowed) {
		this.flexibleHoursAllowed = flexibleHoursAllowed;
	}
	public boolean isCountWorkingHoursBeforeCheckInFrom() {
		return countWorkingHoursBeforeCheckInFrom;
	}
	public void setCountWorkingHoursBeforeCheckInFrom(
			boolean countWorkingHoursBeforeCheckInFrom) {
		this.countWorkingHoursBeforeCheckInFrom = countWorkingHoursBeforeCheckInFrom;
	}
	public Date getCheckInFrom() {
		return checkInFrom;
	}
	public void setCheckInFrom(Date checkInFrom) {
		this.checkInFrom = checkInFrom;
	}
	public Date getCheckInTo() {
		return checkInTo;
	}
	public void setCheckInTo(Date checkInTo) {
		this.checkInTo = checkInTo;
	}
	public Date getCheckOutFrom() {
		return checkOutFrom;
	}
	public void setCheckOutFrom(Date checkOutFrom) {
		this.checkOutFrom = checkOutFrom;
	}
	public Date getCheckOutTo() {
		return checkOutTo;
	}
	public void setCheckOutTo(Date checkOutTo) {
		this.checkOutTo = checkOutTo;
	}
	public boolean isCountWorkingHoursAfterCheckOutTo() {
		return countWorkingHoursAfterCheckOutTo;
	}
	public void setCountWorkingHoursAfterCheckOutTo(
			boolean countWorkingHoursAfterCheckOutTo) {
		this.countWorkingHoursAfterCheckOutTo = countWorkingHoursAfterCheckOutTo;
	}
	public boolean isMinimumWorkingHourForPresence() {
		return isMinimumWorkingHourForPresence;
	}
	public void setIsMinimumWorkingHourForPresence(
			boolean isMinimumWorkingHourForPresence) {
		this.isMinimumWorkingHourForPresence = isMinimumWorkingHourForPresence;
	}
	public int getMinimumWorkingHourForPresence() {
		return minimumWorkingHourForPresence;
	}
	public void setMinimumWorkingHourForPresence(int minimumWorkingHourForPresence) {
		this.minimumWorkingHourForPresence = minimumWorkingHourForPresence;
	}
	public boolean isHalfDayWorkingHour() {
		return isHalfDayWorkingHour;
	}
	public void setIsHalfDayWorkingHour(boolean isHalfDayWorkingHour) {
		this.isHalfDayWorkingHour = isHalfDayWorkingHour;
	}
	public int getHalfDayWorkingHour() {
		return halfDayWorkingHour;
	}
	public void setHalfDayWorkingHour(int halfDayWorkingHour) {
		this.halfDayWorkingHour = halfDayWorkingHour;
	}
	public int getShiftHour() {
		return shiftHour;
	}
	public void setShiftHour(int shiftHour) {
		this.shiftHour = shiftHour;
	}
	public int getMaxShiftHour() {
		return maxShiftHour;
	}
	public void setMaxShiftHour(int maxShiftHour) {
		this.maxShiftHour = maxShiftHour;
	}
	public boolean isMinShiftHour() {
		return isMinShiftHour;
	}
	public void setIsMinShiftHour(boolean isMinShiftHour) {
		this.isMinShiftHour = isMinShiftHour;
	}
	public int getMinShiftHour() {
		return minShiftHour;
	}
	public void setMinShiftHour(int minShiftHour) {
		this.minShiftHour = minShiftHour;
	}
	public boolean isEarlyGoingHalfDay() {
		return isEarlyGoingHalfDay;
	}
	public void setIsEarlyGoingHalfDay(boolean isEarlyGoingHalfDay) {
		this.isEarlyGoingHalfDay = isEarlyGoingHalfDay;
	}
	public boolean isLateComingHalfDay() {
		return isLateComingHalfDay;
	}
	public void setIsLateComingHalfDay(boolean isLateComingHalfDay) {
		this.isLateComingHalfDay = isLateComingHalfDay;
	}
	public Date getMaxEarlyGoTime() {
		return maxEarlyGoTime;
	}
	public void setMaxEarlyGoTime(Date maxEarlyGoTime) {
		this.maxEarlyGoTime = maxEarlyGoTime;
	}
	public Date getMaxLateComeTime() {
		return maxLateComeTime;
	}
	public void setMaxLateComeTime(Date maxLateComeTime) {
		this.maxLateComeTime = maxLateComeTime;
	}
	public boolean isSaturdayCountedInWorkingHours() {
		return saturdayCountedInWorkingHours;
	}
	public void setSaturdayCountedInWorkingHours(
			boolean saturdayCountedInWorkingHours) {
		this.saturdayCountedInWorkingHours = saturdayCountedInWorkingHours;
	}
	public boolean isSundayCountedInWorkingHours() {
		return sundayCountedInWorkingHours;
	}
	public void setSundayCountedInWorkingHours(boolean sundayCountedInWorkingHours) {
		this.sundayCountedInWorkingHours = sundayCountedInWorkingHours;
	}
	public boolean isCalculateDaily() {
		return isCalculateDaily;
	}
	public void setCalculateDaily(boolean isCalculateDaily) {
		this.isCalculateDaily = isCalculateDaily;
	}
	public boolean isCalculateWeekly() {
		return isCalculateWeekly;
	}
	public void setCalculateWeekly(boolean isCalculateWeekly) {
		this.isCalculateWeekly = isCalculateWeekly;
	}
	public boolean isCalculateMonthly() {
		return isCalculateMonthly;
	}
	public void setCalculateMonthly(boolean isCalculateMonthly) {
		this.isCalculateMonthly = isCalculateMonthly;
	}
	public boolean isLateleavingNextDayLateAllowed() {
		return lateleavingNextDayLateAllowed;
	}
	public void setLateleavingNextDayLateAllowed(
			boolean lateleavingNextDayLateAllowed) {
		this.lateleavingNextDayLateAllowed = lateleavingNextDayLateAllowed;
	}
	public Date getLateleavingNextDateLatetime() {
		return lateleavingNextDateLatetime;
	}
	public void setLateleavingNextDateLatetime(Date lateleavingNextDateLatetime) {
		this.lateleavingNextDateLatetime = lateleavingNextDateLatetime;
	}
	public Date getMaximumLateAllowedForLateLeaving() {
		return maximumLateAllowedForLateLeaving;
	}
	public void setMaximumLateAllowedForLateLeaving(Date maximumLateAllowedForLateLeaving) 
	{
		this.maximumLateAllowedForLateLeaving = maximumLateAllowedForLateLeaving;
	}
	public int getWeeklyShortHoursHalfDay() {
		return weeklyShortHoursHalfDay;
	}
	public void setWeeklyShortHoursHalfDay(int weeklyShortHoursHalfDay) {
		this.weeklyShortHoursHalfDay = weeklyShortHoursHalfDay;
	}
	public String getAftermidnightMaxAllowedTime() {
		return aftermidnightMaxAllowedTime;
	}
	public void setAftermidnightMaxAllowedTime(String aftermidnightMaxAllowedTime) {
		this.aftermidnightMaxAllowedTime = aftermidnightMaxAllowedTime;
	}
	public int getMinimumExtraMonthHours() {
		return minimumExtraMonthHours;
	}
	public void setMinimumExtraMonthHours(int minimumExtraMonthHours) {
		this.minimumExtraMonthHours = minimumExtraMonthHours;
	}
	public int getHoursForEveryHalfDayReversal() {
		return hoursForEveryHalfDayReversal;
	}
	public void setHoursForEveryHalfDayReversal(int hoursForEveryHalfDayReversal) {
		this.hoursForEveryHalfDayReversal = hoursForEveryHalfDayReversal;
	}
	public int getHalfCutPolicyPerHour() {
		return halfCutPolicyPerHour;
	}
	public void setHalfCutPolicyPerHour(int halfCutPolicyPerHour) {
		this.halfCutPolicyPerHour = halfCutPolicyPerHour;
	}
	public int getIsCoreHours() {
		return isCoreHours;
	}
	public void setIsCoreHours(int isCoreHours) {
		this.isCoreHours = isCoreHours;
	}
	public String getWorkingDays() {
		return workingDays;
	}
	public void setWorkingDays(String workingDays) {
		this.workingDays = workingDays;
	}
	public String getCompanyStartTime() {
		return companyStartTime;
	}
	public void setCompanyStartTime(String companyStartTime) {
		this.companyStartTime = companyStartTime;
	}
	public String getCompanyEndTime() {
		return companyEndTime;
	}
	public void setCompanyEndTime(String companyEndTime) {
		this.companyEndTime = companyEndTime;
	}
	public boolean isSaturdayPolicy() {
		return saturdayPolicy;
	}
	public void setSaturdayPolicy(boolean saturdayPolicy) {
		this.saturdayPolicy = saturdayPolicy;
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
	public long getIsDefaultShift() {
		return isDefaultShift;
	}
	public void setIsDefaultShift(long isDefaultShift) {
		this.isDefaultShift = isDefaultShift;
	}
	public boolean isApprovalmaxworkinghours() {
		return approvalmaxworkinghours;
	}
	public void setApprovalmaxworkinghours(boolean approvalmaxworkinghours) {
		this.approvalmaxworkinghours = approvalmaxworkinghours;
	}
	
	
}
