package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.HolidaySchedulerService;

public class HolidaySchedulerTask {

	Logger LOG = LogManager.getLogger(HolidaySchedulerTask.class);

	@Autowired
	HolidaySchedulerService schedulerService;

	public void execute() {

		LOG.info("----------Start Holiday Scheduler----------");
		try {
			//w	 schedulerService.autoHolidayUpdate();
			//schedulerService.autoHolidayUpdate();
			LOG.info("----------Stop Holiday Scheduler----------");
		} catch (Exception e) {
			LOG.error("Error in updattig holiday in attendance", e);
			LOG.error("----------Holiday Scheduler Aborted----------");
		}
		return;
	}

}
