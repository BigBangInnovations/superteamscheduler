package com.bigbang.teamworksScheduler.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.AddressSchedulerService;

public class AddressShedulerTask {
	private Logger LOG = LogManager.getLogger(AutoCheckInSchedulerTask.class);

	@Autowired
	AddressSchedulerService updateAddressService;

	protected void execute() {

		LOG.debug("----------Start Auto Update Blank Address Scheduler----------");
			try {
				
				//updateAddressService.autoUpdatAddress();
				LOG.debug("----------Stop Auto Update Address Scheduler----------");
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error in  Auto Update Address Scheduler.", e);
				LOG.error("----------Auto Update Address Aborted----------");
			}
		return;

	}
}
