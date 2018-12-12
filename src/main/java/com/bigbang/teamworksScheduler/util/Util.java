package com.bigbang.teamworksScheduler.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

	public static final String DECIMAL_FORMAT = "#.####";

	/**
	 * This method will calculate the distance between 2 locations
	 * 
	 * @param uLat
	 * @param uLon
	 * @param dLat
	 * @param dLon
	 * @return Double
	 */
	public double distance(String latitude1, String longitude1, String latitude2, String longitude2) {

		// DecimalFormat dformat = new DecimalFormat(DECIMAL_FORMAT);
		double uLat = Double.parseDouble(latitude1);
		double uLon = Double.parseDouble(longitude1);
		double dLat = Double.parseDouble(latitude2);
		double dLon = Double.parseDouble(longitude2);

		double earthRadius = 6371000; // meters
		double dLatitude = Math.toRadians(dLat - uLat);
		double dLongitude = Math.toRadians(dLon - uLon);
		double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) + Math.cos(Math.toRadians(uLat))
				* Math.cos(Math.toRadians(dLat)) * Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);
		return dist;
	}

	public Date getTimeZoneDate(Date date, String timezone) {

		Calendar cal = Calendar.getInstance();
		DateFormat defaultFormat = new SimpleDateFormat();
		DateFormat zoneFormat = new SimpleDateFormat();
		try {
			zoneFormat.setTimeZone(TimeZone.getTimeZone(timezone));
			String zoneDate = zoneFormat.format(date);
			cal.setTime(defaultFormat.parse(zoneDate));
			return cal.getTime();
		} catch (ParseException e) {
			return null;
		}
	}
}
