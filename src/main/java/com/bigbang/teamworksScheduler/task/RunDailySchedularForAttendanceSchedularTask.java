package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.AdvanceSchedularForAttendanceService;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;

public class RunDailySchedularForAttendanceSchedularTask 
{
	Logger LOG = LogManager.getLogger(RunDailySchedularForAttendanceSchedularTask.class);

	@Autowired
	AdvanceSchedularForAttendanceService attendanceService;
	
	public void execute()
	{
		try
		{
			LOG.info("----------Start RunDailySchedularForAttendance Scheduler----------");
			
			//attendanceService.runDailySchedular();

			LOG.info("----------Stop RunDailySchedularForAttendance Scheduler----------");
		}
		catch(Exception e)
		{
			LOG.error("Error in RunDailySchedularForAttendance in attendance", e);
		}
	}

}
