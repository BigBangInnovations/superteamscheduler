package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;
import com.bigbang.teamworksScheduler.service.DailyData;

public class UpdateTravelAddressTask {

	private Logger LOG = LogManager.getLogger(UpdateTravelAddressTask.class);
	
	@Autowired
	DailyData dailyData;
	
	@Autowired
	AttendanceSchedulerService schedulerService;
	
	protected void execute() {

		LOG.debug("----------Start Travel Location update Scheduler----------");
		try {
			
			//w schedulerService.autoUpdateTravelAddress();
             
			
	    LOG.debug("----------Stop Travel Location update Scheduler-----------");
		} catch (Exception e) {
			LOG.debug("Error in auto distance sum tracking."+e);
			LOG.debug("----------Auto Distance Tracking Sum Aborted----------");
		}
	return;

	}
	
}
