package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.MissedCheckInSchedulerService;

public class MissedCheckInUsersSchedulerTask {

	private Logger LOG = LogManager.getLogger(MissedCheckInUsersSchedulerTask.class);

	@Autowired
	MissedCheckInSchedulerService missedCheckInService;

	protected void execute() {

				LOG.info("----------Start Missed CheckIn Scheduler----------");
			try {
				//missedCheckInService.fetchMissedCheckInUsers(); //0 30 4,5,6 ? * MON-SAT
				LOG.info("----------Stop Missed CheckIn Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in missed checkin.", e);
				LOG.error("----------Auto missed checkin ----------");
			}
		return;

	}
}
