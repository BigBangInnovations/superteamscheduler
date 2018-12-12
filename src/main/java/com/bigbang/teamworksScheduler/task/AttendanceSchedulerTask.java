package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.Properties;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;

public class AttendanceSchedulerTask {

	Logger LOG = LogManager.getLogger(AttendanceSchedulerTask.class);

	@Autowired
	AttendanceSchedulerService schedulerService;

	public void execute() {

		if (Boolean.parseBoolean((String) Properties.get("teamworks.attendance.schedular.on"))) {
			LOG.info("----------Start Attendance Scheduler----------");
			try {
				//w	schedulerService.attendanceScheduler();
					/*;*/
				//schedulerService.attendanceSchedulerTest();
			//	schedulerService.absentUserScheduler();
				System.out.println("absent attendance scheduler called");
				LOG.info("----------Stop Attendance Scheduler----------");
			} catch (Exception e) {
				LOG.error("Error in auto checkin.", e);
				LOG.error("----------Attendance Scheduler Aborted----------");
			}
		} else {
			LOG.info("---------Attendance scheduler disables--------");
		}

		return;
	}
}
