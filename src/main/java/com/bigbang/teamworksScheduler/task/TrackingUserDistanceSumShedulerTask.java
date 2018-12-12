package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.TrackingDistanceSchedulerService;

public class TrackingUserDistanceSumShedulerTask {

	private Logger LOG = LogManager.getLogger(TrackingUserDistanceSumShedulerTask.class);

	@Autowired
	TrackingDistanceSchedulerService trackingDistanceService;

	protected void execute() {

			LOG.debug("----------Start Auto Distance Tracking Sum Scheduler----------");
			try {
				
               //w   trackingDistanceService.autoDistanceTrackingSum();
               //w   trackingDistanceService.autoDistanceTrackingSumForMonth();
				
		    LOG.debug("----------Stop Auto Distance Tracking Sum Scheduler-----------");
			} catch (Exception e) {
				LOG.debug("Error in auto distance sum tracking."+e);
				LOG.debug("----------Auto Distance Tracking Sum Aborted----------");
			}
		return;

	}
}
