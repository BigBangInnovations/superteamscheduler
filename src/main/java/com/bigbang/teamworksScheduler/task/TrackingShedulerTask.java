package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.TrackingSchedulerService;

public class TrackingShedulerTask {

	private Logger LOG = LogManager.getLogger(TrackingShedulerTask.class);

	@Autowired
	TrackingSchedulerService trackingService;

	protected void execute() {

			LOG.info("----------Start Auto Tracking Scheduler----------");
			try {
					//trackingService.autoTrackingUpdate();
				
				LOG.info("----------Stop Auto Tracking Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in auto checkin.", e);
				LOG.error("----------Auto Tracking Aborted----------");
			}
		return;

	}
}
