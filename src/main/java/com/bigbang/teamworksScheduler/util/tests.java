package com.bigbang.teamworksScheduler.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class tests {
	
	public static final double IOS_LAT_LONG_ERROR = 9.223372036854776E+18;

	public static void main(String args[]) throws ParseException
	{
		
		Date visitCheckOutTime = new SimpleDateFormat("dd/MM/yyyy").parse("25/09/2018");
		Date visitCheckInTime = new SimpleDateFormat("dd/MM/yyyy").parse("23/09/2018");
		Date checkInTime = new SimpleDateFormat("dd/MM/yyyy").parse("24/09/2018");
		Date checkOutTime = new SimpleDateFormat("dd/MM/yyyy").parse("25/09/2018");
		if (visitCheckOutTime == null && (!checkInTime.after(visitCheckInTime) && !checkOutTime.before(visitCheckInTime))) {
			System.out.println("In If");
		}
		Date currentDate = new Date();
		Date time1 = new SimpleDateFormat("HH:mm:ss").parse("18:00:00");
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(time1);
		Date time2 = new SimpleDateFormat("HH:mm:ss").parse("23:59:59");
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(time2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(currentDate)));
		System.out.println("In if"+calendar.getTime());
		System.out.println("In if"+calendar1.getTime());
		System.out.println("In if"+calendar2.getTime());
		if (calendar.getTime().after(time1) && calendar.getTime().before(time2)) {
			System.out.println("In if");
		}
		
		calendar = Calendar.getInstance();
	      calendar.setTime(new SimpleDateFormat("HH:mm:ss").parse(new SimpleDateFormat("HH:mm:ss").format(currentDate)));
	      if (calendar.getTime().after(time1) && calendar.getTime().before(time2)) {
	        calendar.setTime(currentDate);
	        calendar.add(Calendar.DATE, 1);
	      } else {
	    	  calendar.setTime(currentDate);
	      }
	      Date attendanceDate = calendar.getTime();
	      System.out.println(attendanceDate);
	      
	      JsonObject json = new JsonObject();
	      json.addProperty("test", "hina");
	      JsonObject json1 = new JsonObject();
	      json1.add("data", json);
	      System.out.println(json1.get("data"));
	      
	      Gson gson = new GsonBuilder().create();
	      	JsonElement jElement = gson.toJsonTree(json1);
	      	System.out.println(jElement.toString());
			JsonObject jsonTest = jElement.getAsJsonObject();
			jsonTest.addProperty("employeeCode", "TUV");
			jElement = gson.toJsonTree(jsonTest);
			System.out.println(jElement.toString());
			
			getLocationInfo(23.0126823,72.6114456);
			String hh = "AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c,AIzaSyAm1XPmZcuBSFLSiwKuo5ALMBWDbYQTW4c";
			String[] hha = hh.split(",");
			System.out.println(hha.length);
			
			SimpleDateFormat df = new SimpleDateFormat();
			df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			System.out.println(df.format(new Date()));
			
			
	}
	
	public static String getLocationInfo(final double latitude, final double longitude) {

		System.out.println("Getting Location from Google Maps: Latitude: " + latitude + ", Longitude: " + longitude);

		HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key=AIzaSyC9pQw0PPPMsASADM3OoEDEfySOXfjNrvo");
		HttpClient client = HttpClients.createSystem();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			org.apache.http.HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int status;
			while ((status = stream.read()) != -1) {
				stringBuilder.append((char) status);
			}
		} catch (IOException e) {
			System.out.println("Error Getting Location from Google Maps: ");
		}

		JsonObject jsonObject = new JsonObject();
		jsonObject = GsonUtil.getInstance().fromJson(stringBuilder.toString(), JsonObject.class);

		JsonObject location;
		String locationString = "";

		// Read result
		JsonArray jArray = new JsonArray();
		if (jsonObject.has("results")) {
			jArray = jsonObject.getAsJsonArray("results");
		}

		if (jArray != null && jArray.size() != 0) {
			// Get JSON Array called "results" and then get the 0th complete object as JSON
			location = jArray.get(0).getAsJsonObject();
			if (location.has("formatted_address")) {
				// Get the value of the attribute whose name is "formatted_string"
				locationString = location.get("formatted_address").getAsString();
			}
			System.out.println("formattted address:" + locationString);
		} else {

		}
		return locationString;
	}
	
	public static Calendar getAbsoluteDate(Date date, String timezone, TimeZone defaultTimeZone) {
		TimeZone.setDefault(TimeZone.getTimeZone(timezone));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		return cal;
	}
	
	public static Date addLastHours(Date date)
	  {
		System.out.println(date);
	    Calendar cal = Calendar.getInstance(); // creates calendar
	    cal.setTime(date);
	    System.out.println(cal.getTime());
	    
	    // sets calendar time/date=====> you can set your own date here
	    cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    cal.set(Calendar.MILLISECOND, 0);
	    System.out.println(cal.getTime());
	    return cal.getTime(); 
	  }
}
