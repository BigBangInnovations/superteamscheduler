package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.TrackingReportSchedulerService;

public class TrackingReportShedulerTask {

	private Logger LOG = LogManager.getLogger(TrackingReportShedulerTask.class);

	@Autowired
	TrackingReportSchedulerService trackingReportService;

	protected void execute() {

			LOG.debug("----------Start Auto Tracking Report Sending Scheduler----------");
			try {
				
//			w	trackingReportService.autoTrackingReportSend();
				
				LOG.debug("----------Stop Auto Tracking Report Sending Scheduler----------");
			} catch (Exception e) {
				e.printStackTrace();
				LOG.debug("Error in auto Tracking Report Sending."+e);
				LOG.debug("----------Auto Distance Tracking Aborted----------");
			}
		return;

	}
}
