package com.bigbang.teamworksScheduler.service;

import java.text.ParseException;
import java.util.Date;

public interface AdvanceSchedularForAttendanceService 
{
	public void runDailySchedular(long companyID , long userID , Date specifiDate)throws ParseException;
}
