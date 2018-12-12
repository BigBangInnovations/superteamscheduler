package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.Properties;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;

public class AutoCheckInSchedulerTask {

	private Logger LOG = LogManager.getLogger(AutoCheckInSchedulerTask.class);

	@Autowired
	AttendanceSchedulerService schedulerService;

	protected void execute() {

		if (Boolean.parseBoolean((String) Properties.get("teamworks.autocheckin.on"))) {
			LOG.info("----------Start Auto CheckIn Scheduler----------");
			try {
//	w		  schedulerService.autoCheckIn();
//				schedulerService.autoCheckInTest();
			//	schedulerService.autoCheckIn();
				LOG.info("----------Stop Auto CheckIn Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in auto checkin.", e);
				LOG.error("----------Auto CheckIn Aborted----------");
			}
		} else {
			LOG.info("---------Auto CheckIn scheduler disables--------");
		}

		return;

	}

}
