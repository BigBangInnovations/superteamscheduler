package com.bigbang.teamworksScheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.service.DailyData;
import com.bigbang.teamworksScheduler.service.Properties;

/**
 * This class is loaded during application start-up to initialize all the static data from database
 * 
 * @author Poorvi Nigotiya
 */
public class LoadInitialData implements InitializingBean, DisposableBean {

	private Logger logger = LogManager.getLogger(LoadInitialData.class);

	@Autowired
	Properties properties;
	@Autowired
	DailyData dailyData;

	// This method is called during the application stop
	@Override
	public void destroy() throws Exception {

		logger.info("-------------------Closing application-------------------");

	}

	// This method is called during application startup
	@Override
	public void afterPropertiesSet() throws Exception {

		logger.info("-------------------Initializing application Meta Data----");
		properties.refreshProperties();

		logger.info("-------------------Initialize daily data-----------------");
		dailyData.initailizeDailyData();

	}
}
