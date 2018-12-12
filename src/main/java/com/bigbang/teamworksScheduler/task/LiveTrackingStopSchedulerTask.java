package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.LiveTrackingSchedulerService;
import com.bigbang.teamworksScheduler.service.Properties;

public class LiveTrackingStopSchedulerTask {

	private Logger LOG = LogManager.getLogger(LiveTrackingStopSchedulerTask.class);

	@Autowired
	LiveTrackingSchedulerService schedulerService;

	protected void execute() {

		if (Boolean.parseBoolean((String) Properties.get("teamworks.scheduler.livetrackingstop.on"))) {
			LOG.info("----------Start Live Tracking Stop Scheduler----------");
			try {
			    //w schedulerService.autoStopLiveTracking();
				LOG.info("----------Stop Live Tracking Stop Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in 1ive tracking stop scheduler", e);
				LOG.error("----------Auto Live Tracking Stop Aborted----------");
			}
		} else {
			LOG.info("---------Auto Live Tracking Stop scheduler disables--------");
		}

		return;

	}

}
