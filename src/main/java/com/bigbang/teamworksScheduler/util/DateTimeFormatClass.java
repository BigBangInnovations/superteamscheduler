package com.bigbang.teamworksScheduler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTimeFormatClass {

	/**
	 * . Private Constructor
	 */
	private DateTimeFormatClass() {

	}
	public static final String DATE_FORMAT = "yyyy/MM/dd";

	public static final String DATE_FORMAT_WITH_TIME = "yyyy/MM/dd HH:mm";

	public static final String DATE_FORMAT_WITH_TIME_AND_SECONDS = "yyyy/MM/dd HH:mm:ss";

	public static final String YEAR_MONTH_FORMAT = "yyyy/MM";

	public static final String MONTH_FORMAT = "MM";

	public static final String YEAR_FORMAT = "yyyy";

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public static final String DEFAULT_DATE_FORMAT_WITH_TIME = "yyyy-MM-dd HH:mm:ss.S";

	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

	public static final String DAY_FORMAT = "EEEE";

	public static final String TIME_FORMAT_SECONDS = "HH:mm:ss";

	public static final String TIME_FORMAT = "HH:mm";

	public static final String STR_DATE_FORMAT_WITH_TIME = "yyyy/MM/dd HH:mm:ss";

	public static final String STR_DATE_FORMAT = "dd/MM/yyyy";

	public static final String dd_MM_yyyy_format = "dd-MM-yyyy";

	public static final String DATE_WITH_TIMEZONE = "dd MMM yyyy HH:mm:ss z";
	
	public static final String MMM_D_YYYY_HH_mm_ss_a = "MMM d , yyyy HH:mm:ss a";

	public static final String DD_MM_YYYY_H_M_S_S = "dd/MM/yyyy HH:mm:ss.SS";
	public static Calendar getAbsoluteDate(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);

		return cal;
	}

	/**
	 * Calculate from HH:mm format to hrs format
	 * 
	 * @param time
	 * @return
	 */
	public static double calculateTime(String time) {
		String[] arr = time.split(":");
		double hrs = Double.parseDouble(arr[0]) + Double.parseDouble(arr[1]) / 60;

		return hrs;
	}

	/**
	 * Calculate from HH:mm format to hrs format
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public static double calculateTime(String startTime, String endTime) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT_SECONDS);
		Date start = formatter.parse(startTime);
		Date end = formatter.parse(endTime);

		double diff = end.getTime() - start.getTime();
		double hrs = diff / (1000 * 60 * 60);

		return hrs;
	}

	public static Date getOnlyDate(Date date) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		String str = formatter.format(date);

		return formatter.parse(str);
	}

	// calculate total no of days in month
	public static int getDaysInMonth(int monthNumber, int year) {
		int days = 0;
		if (monthNumber >= 0 && monthNumber < 12) {
			Calendar calendar = Calendar.getInstance();
			int date = 1;
			calendar.set(year, monthNumber - 1, date);
			days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		return days;
	}

	public static String convertTimeZone(Date date, String timeZone) {

		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));

		return formatter.format(date);

	}
	 public static String gmtToLocalTimeForKC(Date dtStart) {
	        String newDate = "";
	        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        String dates = format.format(dtStart);
	        format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
	        try {
	            Date localTime = format.parse(dates);
	            newDate = sdf.format(localTime);
	            return newDate;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        System.out.println(newDate);
	        return newDate;
	        
	    }
	 public static String utcToLocalTime(String dtStart) {
	        String newDate = "";
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
	        //String variables =  format.format(dtStart);
	        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
	        try {
	            Date localTime = format.parse(dtStart);
	            newDate = sdf.format(localTime);
	            return newDate;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return newDate;
	    }
}