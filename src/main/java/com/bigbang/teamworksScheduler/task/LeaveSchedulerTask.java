package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.LeaveSchedulerService;
import com.bigbang.teamworksScheduler.service.Properties;

public class LeaveSchedulerTask {

	Logger LOG = LogManager.getLogger(LeaveSchedulerTask.class);

	@Autowired
	LeaveSchedulerService schedulerService;

	public void execute() {

		if (Boolean.parseBoolean((String) Properties.get("teamworks.autoleave.schedular.on"))) {
			LOG.info("----------Start Auto Leave Scheduler----------");
			try {
				//schedulerService.autoLeaveUpdate();
				LOG.info("----------Stop Auto Leave Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in auto leaveScheduler.", e);
				LOG.error("----------Auto Leave Scheduler Aborted----------");
			}
		} else {
			LOG.info("---------Auto Leave scheduler disables--------");
		}

		return;
	}
}
