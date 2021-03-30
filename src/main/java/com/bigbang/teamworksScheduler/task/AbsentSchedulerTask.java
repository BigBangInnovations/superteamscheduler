package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;

public class AbsentSchedulerTask {

	Logger LOG = LogManager.getLogger(AbsentSchedulerTask.class);

	@Autowired
	AttendanceSchedulerService schedulerService;

	public void execute() {

		LOG.info("----------Start Absent Scheduler----------");
		try {
		//w	  schedulerService.absentUserScheduler(); //0 25 18 * * ? *
			//System.out.println("absent scheduler called");
			//schedulerService.absentUserScheduler();
			LOG.info("----------Stop Absent Scheduler----------");
		} catch (Exception e) {
			LOG.error("Error in updating Absent in attendance", e);
			LOG.error("----------Absent Scheduler Aborted----------");
		}
		return;
	}

}
