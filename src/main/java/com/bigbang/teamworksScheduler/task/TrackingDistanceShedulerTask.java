package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.TrackingDistanceSchedulerService;

public class TrackingDistanceShedulerTask {

	private Logger LOG = LogManager.getLogger(TrackingDistanceShedulerTask.class);

	@Autowired
	TrackingDistanceSchedulerService trackingDistanceService;

	protected void execute() {

			LOG.debug("----------Start Auto Distance Tracking Scheduler----------");
			try {
				
                 //trackingDistanceService.autoTrackingGoogleDistanceUpdate();
				
				//trackingDistanceService.autoTrackingGoogleDistanceUpdateMay();//0 30 16,17 ? * MON-SUN
				
				LOG.debug("----------Stop Auto Distance Tracking Scheduler----------");
			} catch (Exception e) {
				LOG.debug("Error in auto distance tracking."+e);
				LOG.debug("----------Auto Distance Tracking Aborted----------");
			}
		return;

	}
}
