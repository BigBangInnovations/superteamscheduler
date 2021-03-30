package com.bigbang.teamworksScheduler;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bigbang.teamworksScheduler.service.AdvanceSchedularForAttendanceService;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;
import com.bigbang.teamworksScheduler.service.DailyData;
import com.bigbang.teamworksScheduler.service.HolidaySchedulerService;

/**
 * This class handles the request related to company, customer, vendor and their contact. It includes create, update,
 * delete and get operations.
 * 
 * @author Poorvi Nigotiya
 * 
 */
@RestController
public class DataController {

	@Autowired
	private LoadInitialData loadInitialData;
	@Autowired
	DailyData dailyData;
	@Autowired
	HolidaySchedulerService holidaySchedulerService;
	@Autowired
	AttendanceSchedulerService attendanceSchedulerService;
	
	@Autowired
	AdvanceSchedularForAttendanceService advanceSchedularForAttendanceService;
	
	private static final Logger LOG = LogManager.getLogger(DataController.class);

	/**
	 * API to create new company
	 * 
	 * @param companyDetails
	 * @param addressdetails
	 * @param userID
	 * @param file
	 * @return JsonObject
	 */
	@RequestMapping(value = "/refreshData", method = RequestMethod.GET)
	public String refreshData() {

		LOG.info("API: refreshData");
		try {
			dailyData.initailizeDailyData();
			return "Success";
		} catch (Exception e) {
			LOG.error("Error in creating company: ", e);
			return "Error";
		}
	}

	@RequestMapping(value = "/holidayScheduler", method = RequestMethod.GET)
	public String holidayScheduler() {

		LOG.info("API: refreshData");
		try {
			holidaySchedulerService.autoHolidayUpdate();
			return "Success";
		} catch (Exception e) {
			LOG.error("Error in creating company: ", e);
			return "Error";
		}
	}

	@RequestMapping(value = "/absentScheduler", method = RequestMethod.GET)
	public String absentScheduler() {

		LOG.info("API: refreshData");
		try {
			attendanceSchedulerService.absentUserScheduler();
			return "Success";
		} catch (Exception e) {
			LOG.error("Error in creating company: ", e);
			return "Error";
		}
	}
	
	@RequestMapping(value = "/attendanceSchedular", method = RequestMethod.GET)
	public String attendanceSchedular(@RequestParam(value = "UserID") final long userID,@RequestParam(value = "CompanyID") final long companyID, @RequestParam(value = "StartDate") final Date startDate, @RequestParam(value = "EndDate") final Date endDate) 
	{
		LOG.info("API: attendanceSchedular");
		try {
			advanceSchedularForAttendanceService.runDailySchedular(companyID , userID , startDate ,endDate);
			return "Success";
		} catch (Exception e) {
			LOG.error("Error in creating company: ", e);
			return "Error";
		}
	}

	@RequestMapping(value = "/weeklySchedular", method = RequestMethod.GET)
	public String weeklySchedular(@RequestParam(value = "Date") final Date date)//,@RequestParam(value="UserID") final long userID,@RequestParam(value="CompanyID") long companyID
	{
		LOG.info("API: weeklySchedular");
		try {
			advanceSchedularForAttendanceService.weeklySchedularExecution(date);//,userID,companyID
			return "Success";
		} catch (Exception e) {
			LOG.error("Error in weekly Schedular : ", e);
			return "Error";
		}
	}

	@RequestMapping(value = "/monthlySchedular", method = RequestMethod.GET)
	public String monthlySchedular(@RequestParam(value = "Date") final Date date,@RequestParam(value="CompanyID") final long companyID) 
	{
		LOG.info("API: monthlySchedular");
		try {
			advanceSchedularForAttendanceService.monthlySchedularExecution(date,companyID);
			return "Success";
		} catch (Exception e) {	
			LOG.error("Error in monthly Schedular : ", e);
			return "Error";
		}
	}

}
