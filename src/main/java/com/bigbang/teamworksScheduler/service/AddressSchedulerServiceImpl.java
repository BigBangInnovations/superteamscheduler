package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
 
import com.bigbang.teamworksScheduler.dao.AddressDAO;
import com.bigbang.teamworksScheduler.dao.UserLocation;
import com.bigbang.teamworksScheduler.util.GsonUtil;
import com.bigbang.teamworksScheduler.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import notification.SendNotificationException;

public class AddressSchedulerServiceImpl implements AddressSchedulerService{
	@Autowired
	DailyData dailyData;
	
	@Autowired
	AddressDAO addressDAO;
	
	@Autowired
	Properties properties;
	
	int hour;
	
	Logger LOG = LogManager.getLogger(AddressSchedulerServiceImpl.class);
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
		public void autoUpdatAddress() throws IOException,
				NumberFormatException, SendNotificationException, Exception {
			Util util = new Util();
			
			properties.refreshProperties();
			
			String splitKeyArray[] = Properties.get("geocode.location.key").toString().split(",");
			System.out.println("array size : " + splitKeyArray.length);
	
			Calendar cal = Calendar.getInstance();
			//cal.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
	
			cal.set(Calendar.SECOND, 0);
			
			Calendar cal1 = Calendar.getInstance();
			//cal1.setTime(util.getTimeZoneDate(new Date(), (String) Properties.get("teamworks.scheduler.timezone")));
	
			cal1.set(Calendar.SECOND, 0);
			
			Date date = cal.getTime();
	
			// Check if daily data is not stale
			int minute = cal.get(Calendar.MINUTE);
			if (minute <= 30) {
				cal.set(Calendar.MINUTE, 0);
			} else {
				cal.set(Calendar.MINUTE, 30);
			}
			date = cal.getTime();
			System.out.println("Date is : " + date);
			
			List<com.bigbang.teamworksScheduler.dao.UserLocation> locationList = addressDAO.getUserLocations(date);
			System.out.println("missed location size :" + locationList.size());
			int i = 0;
			int keyCounter = 0;
			System.out.println("key used : " + splitKeyArray[hour].toString());
			List<UserLocation> updateLocationList = new ArrayList<UserLocation>();
			Calendar cal11 = Calendar.getInstance();
			hour = cal11.get(Calendar.HOUR);
			 
			for(UserLocation location : locationList)
			{
				if(i  <= 150)
				{
					System.out.println(i + " Lattitude : " + location.getLatitude() + " Longitude : " + location.getLongitude()
						+ " Location : " + location.getLocation() + " |||| Date :" + location.getDate() + " User ID : "
						+ location.getUserId() + " CompanyID : " + location.getCompanyId());
				//System.out.println("key : " + key);
					System.out.println("key  : " + splitKeyArray[hour].toString());
				            		String address = getUserLocationInfo(location.getLatitude(), location.getLongitude(),splitKeyArray[hour].toString(),location,updateLocationList);
				            		System.out.println("address : "  + address);
				            		location.setLocation(address);
				        			updateLocationList.add(location);
				}
				else
				{
					break;
				}
				i++;
				 
			}
			System.out.println("location list : " + updateLocationList.size());
			addressDAO.updateLocation(updateLocationList);
		}
	
		public String getUserLocationInfo(final double latitude, final double longitude, final String key,UserLocation userlocation,List<UserLocation> updateLocationList) throws Exception
		  {
	
			//LOG.debug("Getting Location from Google Maps: Latitude: " + latitude + ", Longitude: " + longitude +
	//" Key : " + key);
	HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +latitude+ "," + longitude+"&key="+key);
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
	//	LOG.error("Error Getting Location from Google Maps: ", e);
		e.printStackTrace();
		throw new Exception(e.getMessage());
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
		//LOG.info("test", "formattted address:" + locationString);
	} else {
	
	}
	System.out.println("localstring : " + locationString);
	
	userlocation.setLocation(locationString);
	updateLocationList.add(userlocation);
	
	return locationString;
	}
}
