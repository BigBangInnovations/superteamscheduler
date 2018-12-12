package com.bigbang.teamworksScheduler.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bigbang.teamworksScheduler.dao.AttendanceSchedulerDAO;

public class Properties {

	@Autowired
	AttendanceSchedulerDAO schedulerDAO;

	private static Map<String, Object> PROPERTIES = new HashMap<String, Object>();

	public boolean refreshProperties() {

		PROPERTIES = schedulerDAO.getProperties();

		return true;
	}

	public static Object get(String key) {

		return PROPERTIES.get(key);
	}

//	public void put(String string, String string2) {
//		// TODO Auto-generated method stub
//		
//	}

}
