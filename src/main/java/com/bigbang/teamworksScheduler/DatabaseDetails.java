package com.bigbang.teamworksScheduler;

import org.springframework.beans.factory.annotation.Value;

public class DatabaseDetails {

	@Value("${db.driver.className}")
	private String mysqldbdriver;

	@Value("${db.driver.connector}" + "${db.host}")
	private String mysqldburl;

	@Value("${db.port}")
	private String mysqldbport;

	@Value("${db.name}")
	private String mysqldbschema;

	@Value("${db.username}")
	private String mysqldbuser;

	@Value("${db.password}")
	private String mysqldbpass;

	@Value("${db.connection.maxActive}")
	private Integer maxActive;

	@Value("${db.connection.defaultAutoCommit}")
	private Boolean defaultAutoCommit;

	@Value("${db.connection.maxIdle}")
	private Integer maxIdle;

	@Value("${db.connection.minIdle}")
	private Integer minIdle;

	@Value("${db.connection.initialSize}")
	private Integer initialSize;

	@Value("${db.connection.maxWait}")
	private Integer maxWait;

	@Value("${db.connection.testOnBorrow}")
	private boolean testOnBorrow;

	@Value("${db.connection.testOnReturn}")
	private boolean testOnReturn;

	@Value("${db.connection.testWhileIdle}")
	private boolean testWhileIdle;

	@Value("${db.connection.validationQuery}")
	private String validationQuery;

	@Value("${db.connection.validationQueryTimeout}")
	private int validationQueryTimeout;

	@Value("${db.connection.timeBetweenEvictionRunsMillis}")
	private int timeBetweenEvictionRunsMillis;

	@Value("${db.connection.minEvictableIdleTimeMillis}")
	private int minEvictableIdleTimeMillis;

	@Value("${db.connection.validationInterval}")
	private long validationInterval;

	@Value("${db.connection.serverTimezone}")
	private String serverTimezone;

	@Value("${db.connection.useLegacyDatetimeCode}")
	private boolean useLegacyDatetimeCode;

	@Value("${db.commonService.name}")
	private String commonServiceDB;

	public String getCommonServiceDB() {
		return commonServiceDB;
	}

	public void setCommonServiceDB(String commonServiceDB) {
		this.commonServiceDB = commonServiceDB;
	}

	public String getMysqldbport() {
		return mysqldbport;
	}

	public void setMysqldbport(String mysqldbport) {
		this.mysqldbport = mysqldbport;
	}

	public String getMysqldbschema() {
		return mysqldbschema;
	}

	public void setMysqldbschema(String mysqldbschema) {
		this.mysqldbschema = mysqldbschema;
	}

	public Boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public int getValidationQueryTimeout() {
		return validationQueryTimeout;
	}

	public void setValidationQueryTimeout(int validationQueryTimeout) {
		this.validationQueryTimeout = validationQueryTimeout;
	}

	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public long getValidationInterval() {
		return validationInterval;
	}

	public void setValidationInterval(long validationInterval) {
		this.validationInterval = validationInterval;
	}

	public String getServerTimezone() {
		return serverTimezone;
	}

	public void setServerTimezone(String serverTimezone) {
		this.serverTimezone = serverTimezone;
	}

	public boolean isUseLegacyDatetimeCode() {
		return useLegacyDatetimeCode;
	}

	public void setUseLegacyDatetimeCode(boolean useLegacyDatetimeCode) {
		this.useLegacyDatetimeCode = useLegacyDatetimeCode;
	}

	public String getMysqldbdriver() {
		return mysqldbdriver;
	}

	public void setMysqldbdriver(String mysqldbdriver) {
		this.mysqldbdriver = mysqldbdriver;
	}

	public String getMysqldburl() {
		return mysqldburl;
	}

	public void setMysqldburl(String mysqldburl) {
		this.mysqldburl = mysqldburl;
	}

	public String getMysqldbuser() {
		return mysqldbuser;
	}

	public void setMysqldbuser(String mysqldbuser) {
		this.mysqldbuser = mysqldbuser;
	}

	public String getMysqldbpass() {
		return mysqldbpass;
	}

	public void setMysqldbpass(String mysqldbpass) {
		this.mysqldbpass = mysqldbpass;
	}

}
