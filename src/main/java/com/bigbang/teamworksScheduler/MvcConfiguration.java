package com.bigbang.teamworksScheduler;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bigbang.teamworksScheduler.dao.AddressDAO;
import com.bigbang.teamworksScheduler.dao.AddressDAOImpl;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAOImpl;
import com.bigbang.teamworksScheduler.dao.CompanyDAO;
import com.bigbang.teamworksScheduler.dao.CompanyDAOImpl;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAO;
import com.bigbang.teamworksScheduler.dao.HolidaySchedulerDAOImpl;
import com.bigbang.teamworksScheduler.dao.LeaveSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.LeaveSchedulerDAOImpl;
import com.bigbang.teamworksScheduler.dao.LiveTrackingSchedulerDAO;
import com.bigbang.teamworksScheduler.dao.LiveTrackingSchedulerDAOImpl;
import com.bigbang.teamworksScheduler.dao.MissedCheckInUserDAO;
import com.bigbang.teamworksScheduler.dao.MissedCheckInUserDAOImpl;
import com.bigbang.teamworksScheduler.dao.TrackingDAO;
import com.bigbang.teamworksScheduler.dao.TrackingDAOImpl;
import com.bigbang.teamworksScheduler.dao.UserAttendanceDAO;
import com.bigbang.teamworksScheduler.dao.UserAttendanceDAOImpl;
import com.bigbang.teamworksScheduler.dao.UserDAO;
import com.bigbang.teamworksScheduler.dao.UserDAOImpl;
import com.bigbang.teamworksScheduler.service.AddressSchedulerService;
import com.bigbang.teamworksScheduler.service.AddressSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.AdvanceSchedularForAttendanceService;
import com.bigbang.teamworksScheduler.service.AdvanceSchedularForAttendanceServiceImpl;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerService;
import com.bigbang.teamworksScheduler.service.AttendanceSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.CompanyService;
import com.bigbang.teamworksScheduler.service.CompanyServiceImpl;
import com.bigbang.teamworksScheduler.service.DailyData;
import com.bigbang.teamworksScheduler.service.HolidaySchedulerService;
import com.bigbang.teamworksScheduler.service.HolidaySchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.LeaveSchedulerService;
import com.bigbang.teamworksScheduler.service.LeaveSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.LiveTrackingSchedulerService;
import com.bigbang.teamworksScheduler.service.LiveTrackingSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.MissedCheckInSchedulerService;
import com.bigbang.teamworksScheduler.service.MissedCheckInUsersServiceImpl;
import com.bigbang.teamworksScheduler.service.Properties;
import com.bigbang.teamworksScheduler.service.TrackingDistanceSchedulerService;
import com.bigbang.teamworksScheduler.service.TrackingDistanceSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.TrackingReportSchedulerService;
import com.bigbang.teamworksScheduler.service.TrackingReportSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.service.TrackingSchedulerService;
import com.bigbang.teamworksScheduler.service.TrackingSchedulerServiceImpl;
import com.bigbang.teamworksScheduler.task.AbsentSchedulerTask;
import com.bigbang.teamworksScheduler.task.AddressShedulerTask;
import com.bigbang.teamworksScheduler.task.AttendanceSchedulerTask;
import com.bigbang.teamworksScheduler.task.AutoCheckInSchedulerTask;
import com.bigbang.teamworksScheduler.task.AutoCheckOutSchedulerTask;
import com.bigbang.teamworksScheduler.task.HolidaySchedulerTask;
import com.bigbang.teamworksScheduler.task.LeaveSchedulerTask;
import com.bigbang.teamworksScheduler.task.LiveTrackingStopSchedulerTask;
import com.bigbang.teamworksScheduler.task.MissedCheckInUsersSchedulerTask;
import com.bigbang.teamworksScheduler.task.RunDailySchedularForAttendanceSchedularTask;
import com.bigbang.teamworksScheduler.task.TrackingDistanceShedulerTask;
import com.bigbang.teamworksScheduler.task.TrackingShedulerTask;
import com.bigbang.teamworksScheduler.task.TrackingUserDistanceSumShedulerTask;
import com.bigbang.teamworksScheduler.task.UpdateTravelAddressTask;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@ComponentScan(basePackages = "net.codejava.spring")
@EnableWebMvc
public class MvcConfiguration {

	public static String masterSchema;

	@Bean
	public DatabaseDetails getDatabaseDetails() {
		return new DatabaseDetails();
	}

	@Bean(name = "dataSource")
	public DataSource getDataSource() {

		DataSource dataSource = new DataSource();
		dataSource.setDriverClassName(getDatabaseDetails().getMysqldbdriver());
		dataSource.setUrl(getDatabaseDetails().getMysqldburl() + ":" + getDatabaseDetails().getMysqldbport() + "/"
				+ getDatabaseDetails().getMysqldbschema());
		dataSource.setUsername(getDatabaseDetails().getMysqldbuser());
		dataSource.setPassword(getDatabaseDetails().getMysqldbpass());
		dataSource.setDefaultAutoCommit(getDatabaseDetails().getDefaultAutoCommit());
		dataSource.setMaxActive(getDatabaseDetails().getMaxActive());
		dataSource.setMaxIdle(getDatabaseDetails().getMaxIdle());
		dataSource.setMinIdle(getDatabaseDetails().getMinIdle());
		dataSource.setInitialSize(getDatabaseDetails().getInitialSize());
		dataSource.setMaxWait(getDatabaseDetails().getMaxWait());
		dataSource.setTestOnBorrow(getDatabaseDetails().isTestOnBorrow());
		dataSource.setTestOnReturn(getDatabaseDetails().isTestOnReturn());
		dataSource.setTestWhileIdle(getDatabaseDetails().isTestWhileIdle());
		dataSource.setValidationQuery(getDatabaseDetails().getValidationQuery());
		dataSource.setValidationInterval(getDatabaseDetails().getValidationInterval());
		dataSource.setTimeBetweenEvictionRunsMillis(getDatabaseDetails().getTimeBetweenEvictionRunsMillis());
		dataSource.setMinEvictableIdleTimeMillis(getDatabaseDetails().getMinEvictableIdleTimeMillis());
		masterSchema = getDatabaseDetails().getCommonServiceDB();
		return dataSource;
	}

	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		System.out.println("Database initialization");
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		return namedParameterJdbcTemplate;
	}

	@Bean
	public Properties getProperties() {
		return new Properties();
	}

	@Bean
	@DependsOn("dataSource")
	public AttendanceSchedulerDAO getSchedulerDAO() {
		return new AttendanceSchedulerDAOImpl();
	}

	@Bean
	public AttendanceSchedulerService getSchedulerService() {
		return new AttendanceSchedulerServiceImpl();
	}

	@Bean 
	public AdvanceSchedularForAttendanceService getAdvanceSchedularForAttendanceService()
	{
		return new AdvanceSchedularForAttendanceServiceImpl();
	}
	
	@Bean
	public LeaveSchedulerService getLeaveSchedulerService() {
		return new LeaveSchedulerServiceImpl();
	}

	@Bean
	public LeaveSchedulerDAO getLeaveSchedulerDAO() {
		return new LeaveSchedulerDAOImpl();
	}

	@Bean
	public HolidaySchedulerService getHolidaySchedulerService() {
		return new HolidaySchedulerServiceImpl();
	}

	@Bean
	public HolidaySchedulerDAO getHolidaySchedulerDAO() {
		return new HolidaySchedulerDAOImpl();
	}
	
	@Bean
	public TrackingSchedulerService getTrackingSchedulerService() {
		return new TrackingSchedulerServiceImpl();
	}

	@Bean
	public TrackingDAO getTrackingDAO() {
		return new TrackingDAOImpl();
	}
	
	@Bean
	public AddressSchedulerService getaAddressSchedulerService() {
		return new AddressSchedulerServiceImpl();
	}
	
	@Bean
	public AddressDAO getAddressDAO() {
		return new AddressDAOImpl();
	}
	
	@Bean
	public MissedCheckInSchedulerService getMissedCheckInSchedulerService() {
		return new MissedCheckInUsersServiceImpl();
	}

	@Bean
	public MissedCheckInUserDAO getMissedCheckInUserDAO() {
		return new MissedCheckInUserDAOImpl();
	}
	
	@Bean
	public LiveTrackingSchedulerService getLiveTrackingSchedulerService(){
		return new LiveTrackingSchedulerServiceImpl();
	}
	
	@Bean
	public LiveTrackingSchedulerDAO getLiveTrackingSchedulerDAO(){
		return new LiveTrackingSchedulerDAOImpl();
	}
	
	@Bean
	public TrackingDistanceSchedulerService getTrackingDistanceSchedulerService(){
		return new TrackingDistanceSchedulerServiceImpl();
	}
	
	@Bean
	public TrackingReportSchedulerService getTrackingReportSchedulerService(){
		return new TrackingReportSchedulerServiceImpl();
	}
	
	@Bean
	public CompanyService getCompanyService(){
		return new CompanyServiceImpl();
	}
	
	/**
	 * @return UserDAO
	 */
	@Bean
	public UserDAO getUserDAO() {
		return new UserDAOImpl();
	}
	
	/**
	 * 
	 * @return CompanyDAO
	 */
	@Bean
	public CompanyDAO getCompanyDAO(){
		return new  CompanyDAOImpl();
	}
	
	@Bean 
	public UserAttendanceDAO getUserAttendanceDao()
	{
		return new UserAttendanceDAOImpl();
	}
	
	@Bean(name = "loadInitialData")
	LoadInitialData getLoadInitialData() {
		return new LoadInitialData();
	}

	@Bean
	public DailyData getDailyData() {
		return new DailyData();
	}

	@Bean(name = "transactionManager")
	public DataSourceTransactionManager getDataSourceTransactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(getDataSource());
		return transactionManager;
	}

	@Bean(name = "AutoCheckInSchedularTask")
	public AutoCheckInSchedulerTask getAutoCheckInSchedularTask() {
		return new AutoCheckInSchedulerTask();
	}

	@Bean(name = "AutoCheckOutSchedularTask")
	public AutoCheckOutSchedulerTask getAutoCheckOutSchedularTask() {
		return new AutoCheckOutSchedulerTask();
	}

	@Bean(name = "AttendanceSchedularTask")
	public AttendanceSchedulerTask getAttendanceSchedularTask() {
		return new AttendanceSchedulerTask();
	}

	@Bean(name = "LeaveSchedulerTask")
	public LeaveSchedulerTask getLeaveSchedulerTask() {
		return new LeaveSchedulerTask();
	}

	@Bean(name = "HolidaySchedulerTask")
	public HolidaySchedulerTask getHolidaySchedulerTask() {
		return new HolidaySchedulerTask();
	}
	
	@Bean(name = "TrackingShedulerTask")
	public TrackingShedulerTask getTrackingShedulerTask() {
		return new TrackingShedulerTask();
	}
	
	@Bean(name = "AddressShedulerTask")
	public AddressShedulerTask getAddressShedulerTask() {
		return new AddressShedulerTask();
	}
	
	@Bean(name = "MissedCheckInUsersTask")
	public MissedCheckInUsersSchedulerTask getmCheckInUsersSchedulerTask() {
		return new MissedCheckInUsersSchedulerTask();
	}

	@Bean(name = "LiveTrackingStopTask")
	public LiveTrackingStopSchedulerTask getLiveTrackingStopSchedulerTask(){
		return new LiveTrackingStopSchedulerTask();
	}
	
	@Bean(name = "TrackingDistanceShedulerTask")
	public TrackingDistanceShedulerTask getTrackingDistanceShedulerTask(){
		return new TrackingDistanceShedulerTask();
	}
	
	@Bean(name = "AbsentSchedulerTask")
	public AbsentSchedulerTask getAbsentSchedulerTask(){
		return new AbsentSchedulerTask();
	}
	
	/*@Bean(name = "TrackingReportShedulerTask")
	public TrackingReportShedulerTask getTrackingReportShedulerTask(){
		return new TrackingReportShedulerTask();
	}*/
	
	@Bean(name = "TrackingUserDistanceSumShedulerTask")
	public TrackingUserDistanceSumShedulerTask getTrackingUserDistanceSumShedulerTask(){
		return new TrackingUserDistanceSumShedulerTask();
	}
	
	@Bean(name = "UpdateTravelAddressTask")
	public UpdateTravelAddressTask getUpdateTravelAddressTask(){
		return new UpdateTravelAddressTask();
	}
	
	@Bean(name = "RunDailySchedularForAttendanceSchedularTask")
	public RunDailySchedularForAttendanceSchedularTask getRunDailySchedularForAttendanceSchedularTask()
	{
		return new RunDailySchedularForAttendanceSchedularTask();
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean AutoCheckInJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("AutoCheckInSchedularTask");
		obj.setTargetMethod("execute");

		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean AutoCheckOutJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("AutoCheckOutSchedularTask");
		obj.setTargetMethod("execute");

		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean attendanceJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("AttendanceSchedularTask");
		obj.setTargetMethod("execute");

		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean leaveJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("LeaveSchedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean holidayJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("HolidaySchedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean trackingJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("TrackingShedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean addressJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("AddressShedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}

	@Bean
	public MethodInvokingJobDetailFactoryBean missedCheckInUsersJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("MissedCheckInUsersTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean liveTrackingStopTaskJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("LiveTrackingStopTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean TrackingDistanceShedulerTaskJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("TrackingDistanceShedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean AbsentSchedulerTaskJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("AbsentSchedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	/*@Bean
	public MethodInvokingJobDetailFactoryBean TrackingReportShedulerTaskJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("TrackingReportShedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}*/
	
	@Bean
	public MethodInvokingJobDetailFactoryBean TrackingUserDistanceSumShedulerTaskJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("TrackingUserDistanceSumShedulerTask");
		obj.setTargetMethod("execute");
		return obj;
	}
	
	@Bean
	public MethodInvokingJobDetailFactoryBean UpdateTravelAddressTaskJobDetailFactoryBean() {
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();

		obj.setTargetBeanName("UpdateTravelAddressTask");
		obj.setTargetMethod("execute");

		return obj;
	} 
	
	@Bean
	public MethodInvokingJobDetailFactoryBean dailySchedularJobDetailFactoryBean()
	{
		MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
		
		obj.setTargetBeanName("RunDailySchedularForAttendanceSchedularTask");
		obj.setTargetMethod("execute");
		
		return obj;
	}
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean autoCheckInCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(AutoCheckInJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("checkInTrigger");
		stFactory.setGroup("checkInGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.autocheckin"));
//		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.CheckInUsers"));
		return stFactory;
	}

	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean autoCheckOutCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(AutoCheckOutJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("checkOutTrigger");
		stFactory.setGroup("checkOutGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.autocheckout"));

		return stFactory;
	}

	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean attendanceCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(attendanceJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("attendanceTrigger");
		stFactory.setGroup("attendanceGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.attendance"));
		return stFactory;
	}

	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean leaveCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(leaveJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("leaveTrigger");
		stFactory.setGroup("leaveGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.leave"));
		return stFactory;
	}

	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean holidayCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(holidayJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("holidayTrigger");
		stFactory.setGroup("holidayGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.holiday"));
		return stFactory;
	}
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean trackingCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(trackingJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("trackingTrigger");
		stFactory.setGroup("trackingGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.tracking"));
		return stFactory;
	}
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean updateAddressCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(addressJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("addressTrigger");
		stFactory.setGroup("addressGroup");
		//stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.updateaddress"));
		stFactory.setCronExpression("0 30 14 * * ? *");
		return stFactory;
	}
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean missedCheckInUsersCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(missedCheckInUsersJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("missedCheckInUsersTrigger");
		stFactory.setGroup("missedCheckInUsersGroup");
		//stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.missedCheckInUsers"));
//		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.CheckInUsers"));
		stFactory.setCronExpression("0 30 4,5,6 ? * MON-SAT");
		return stFactory;
	}

	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean livetrackingstopCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(liveTrackingStopTaskJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("livetrackingstopTrigger");
		stFactory.setGroup("livetrackingstopGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.livetrackingstop"));
		return stFactory;
	}
	
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean trackingDistanceShedulerCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(TrackingDistanceShedulerTaskJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("trackingDistanceShedulerTrigger");
		stFactory.setGroup("trackingDistanceShedulerGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.trackingDistance"));
//		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.trackingDistanceMay"));
		return stFactory;
	}
	
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean AbsentSchedulerTaskCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(AbsentSchedulerTaskJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("AbsentSchedulerTaskTrigger");
		stFactory.setGroup("AbsentSchedulerTaskGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.absentUsers"));
		//stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.absentUsersDemo"));
		return stFactory;
	}
	
	/*@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean trackingReportShedulerTaskCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(TrackingReportShedulerTaskJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("trackingReportSchedulerTrigger");
		stFactory.setGroup("trackingReportSchedulerGroup");
		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.trackingReport"));

		return stFactory;
	}*/
	
//	@Bean
//	@DependsOn("loadInitialData")
//	public CronTriggerFactoryBean trackingUserDistanceSumShedulerTaskJobDetailFactoryBean() {
//		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
//
//		stFactory.setJobDetail(TrackingUserDistanceSumShedulerTaskJobDetailFactoryBean().getObject());
//		stFactory.setStartDelay(3000);
//		stFactory.setName("trackingUserDistanceSumSchedulerTrigger");
//		stFactory.setGroup("trackingUserDistanceSumSchedulerGroup");
//		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.userDistanceTrackingSum"));
//
//		return stFactory;
//	}
	
//	@Bean
//	@DependsOn("loadInitialData")
//	public CronTriggerFactoryBean UpdateTravelAddressCronTriggerFactoryBean() {
//		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
//
//		stFactory.setJobDetail(UpdateTravelAddressTaskJobDetailFactoryBean().getObject());
//		stFactory.setStartDelay(3000);
//		stFactory.setName("updateTravelAddressTrigger");
//		stFactory.setGroup("updateTravelAddressGroup");
//		stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.travelAddress.update"));
//
//		return stFactory;
//	}
	
	@Bean
	@DependsOn("loadInitialData")
	public CronTriggerFactoryBean dailySchedularCronTriggerFactoryBean() {
		CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();

		stFactory.setJobDetail(dailySchedularJobDetailFactoryBean().getObject());
		stFactory.setStartDelay(3000);
		stFactory.setName("dailySchedularTrigger");
		stFactory.setGroup("dailySchedularGroup");
		//stFactory.setCronExpression((String) Properties.get("teamworks.scheduler.attendance"));
		stFactory.setCronExpression("0 15 15 04 12 ? *");
		return stFactory;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean scheduler;
		scheduler = new SchedulerFactoryBean();
		scheduler.setTriggers(autoCheckInCronTriggerFactoryBean().getObject(), autoCheckOutCronTriggerFactoryBean()
				.getObject(), attendanceCronTriggerFactoryBean().getObject(),
				leaveCronTriggerFactoryBean().getObject(), 
				holidayCronTriggerFactoryBean().getObject(),
				trackingCronTriggerFactoryBean().getObject(),
				updateAddressCronTriggerFactoryBean().getObject(),
				missedCheckInUsersCronTriggerFactoryBean().getObject(),
				livetrackingstopCronTriggerFactoryBean().getObject(),
				trackingDistanceShedulerCronTriggerFactoryBean().getObject(),
				AbsentSchedulerTaskCronTriggerFactoryBean().getObject(),
			//	trackingReportShedulerTaskCronTriggerFactoryBean().getObject(),
			//	trackingUserDistanceSumShedulerTaskJobDetailFactoryBean().getObject(),
			//	UpdateTravelAddressCronTriggerFactoryBean().getObject(),
				dailySchedularCronTriggerFactoryBean().getObject());

		return scheduler;
	}
}
