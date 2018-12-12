package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.Properties;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;

public class AutoCheckOutSchedulerTask {

	Logger LOG = LogManager.getLogger(AutoCheckOutSchedulerTask.class);

	@Autowired
	AttendanceSchedulerService schedulerService;

	public void execute() {

		if (Boolean.parseBoolean((String) Properties.get("teamworks.autocheckout.on"))) {
			LOG.info("----------Start Auto CheckOut Scheduler----------");
			try {
			      //w schedulerService.autoCheckOut();
				LOG.info("----------Stop Auto CheckOut Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in auto checkout." + e);
				LOG.error("----------Auto CheckOut Aborted----------");
			}
		} else {
			LOG.info("---------Auto CheckOut scheduler disables--------");
		}
		return;
	}

}
