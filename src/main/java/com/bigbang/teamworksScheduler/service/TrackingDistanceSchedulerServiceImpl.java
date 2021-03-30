package com.bigbang.teamworksScheduler.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bigbang.teamworksScheduler.beans.UserDistanceSum;
import com.bigbang.teamworksScheduler.beans.UserLocation;
import com.bigbang.teamworksScheduler.dao.TrackingDAO;
import com.bigbang.teamworksScheduler.dao.UserDAO;
import com.bigbang.teamworksScheduler.util.DateTimeFormatClass;
import com.bigbang.teamworksScheduler.util.ExportToExcel;
import com.bigbang.teamworksScheduler.util.GsonUtil;
import com.bigbang.teamworksScheduler.util.MailUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import notification.SendNotificationException;

public class TrackingDistanceSchedulerServiceImpl implements TrackingDistanceSchedulerService {

	@Autowired
	TrackingDAO trackingDAO;
	
	@Autowired
	UserDAO userDAO;
	
	Logger LOG = LogManager.getLogger(TrackingDistanceSchedulerServiceImpl.class);
	
	
	public void generateTrackingReport(long userID, long companyID,String timezone, String emailID) 
			throws ParseException, IOException, NoSuchAlgorithmException, MessagingException {

		LOG.debug("Calling generateTrackingReport service method");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		MailUtil mailUtil = new MailUtil();
		Session session =Session.getInstance(mailUtil.setMailingProperties1());
		Transport transport = session.getTransport("smtp");
		transport.connect("email-smtp.us-west-2.amazonaws.com", "AKIAJXW7T6XVNC77WM2Q", "AsNFJxvrnIjpMWABBXq0cCC3U2NIaBNRd66tfIuh9rW5");
		
		String memberID = "";
		LOG.debug("Fetch list of all reporting members");
			memberID = userDAO.getLowerHierarchy(userID);

		List<Long> memberIDList = new ArrayList<Long>();
	    if (memberID != null && memberID.trim().length() != 0) {
	     String[] memberArray = memberID.split(",");

	     for (int i = 0; i < memberArray.length; i++) {
	      memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
	     }
	    }
	    
		if (memberIDList == null || memberIDList.size() == 0) {
			LOG.debug("No users reporting");
			return;
		}

		LOG.debug("No. of members:" + memberIDList.size());
 
		List<UserLocation> userLocations = trackingDAO.getAll(memberIDList, formatter.format(currentDate),companyID);

		ExportToExcel excel = new ExportToExcel();
		String filePath = excel.trackingExcel(userLocations, timezone, currentDate,companyID);

			mailUtil.sendHTMLMail1(emailID, filePath, "tracking_Report",
					"Company ID "+companyID, transport);
			mailUtil.sendHTMLMail1("rishimodi115@gmail.com", filePath, "tracking_Report",
					"Company ID "+companyID,transport);
			mailUtil.sendHTMLMail1("hitesh.shah@bigbanginnovations.in", filePath, "tracking_Report",
					"Company ID "+companyID,transport);
			
			transport.close();
//			mailUtil.sendHTMLMail("manukhera@gmail.com", filePath, "tracking_Report",
//					"report");

	}
	
	
	/**
	 * This method will calculate the distance between 2 locations
	 * 
	 * @param uLat
	 * @param uLon
	 * @param dLat
	 * @param dLon
	 * @return Double
	 */
	private double distance(double uLat, double uLon, double dLat, double dLon) {

		double earthRadius = 6371000; // meters
		double dLatitude = Math.toRadians(dLat - uLat);
		double dLongitude = Math.toRadians(dLon - uLon);
		double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) + Math.cos(Math.toRadians(uLat))
				* Math.cos(Math.toRadians(dLat)) * Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);
		return dist;
	}

	@Override
	public void autoTrackingGoogleDistanceUpdate() throws IOException,
			NumberFormatException, SendNotificationException {
		
		LOG.debug("fire sheduler for getting distance from tracking");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//		List<Long> companyList = new ArrayList<Long>();
		List<Long> superAdminList = new ArrayList<Long>();
		//production companies
//		companyList.add((long) 52); //big bang
//		companyList.add((long) 492);
//		companyList.add((long) 682); 
//		companyList.add((long) 524); //No reporting members yet
//		companyList.add((long) 612); //No reporting members yet
//		companyList.add((long) 374); //telenor
//		companyList.add((long) 638);
//		companyList.add((long) 611);
//		companyList.add((long) 628); //Fracton
//		companyList.add((long) 739); //Excitel
//		companyList.add((long) 690); //Shree Al Panel Pvt. Ltd.
//		companyList.add((long) 746); //finolex pipes ltd
//		companyList.add((long) 789);
//		companyList.add((long) 795);
		List<Long> companyList = trackingDAO.getAllTrackingCompanies();
		LOG.debug("companylist  : " + companyList.size());
		if (companyList.size() == 0) {
			LOG.debug("No company exist");
			return;
		}
		
		for(int k=0;k<companyList.size();k++){
			superAdminList.add(userDAO.getCompanySuperAdmin(companyList.get(k)));
		}
		
		try{
		
		int apiCall = 0;
		int apiCallLimit = 500;
		int key = 0;
		try{
			for(int k=0;k<companyList.size();k++){
			List<Long> usersList = trackingDAO.getUsers(companyList.get(k));
			//long superAdminID = userDAO.getCompanySuperAdmin(companyList.get(k));
			LOG.debug("userList "+usersList.size()+" Company ID: "+companyList.get(k));
				if(usersList.size() > 0)
				{
					List<UserLocation> locationDistanceUpdatedList = new ArrayList<UserLocation>(); 
					for(int i = 0;i<usersList.size();i++){
						LOG.debug("User tracking started: "+usersList.get(i));
						List<UserLocation> userLocationList = trackingDAO.getUserLocationList(usersList.get(i), formatter.format(currentDate));
						if(userLocationList.size() <= 0){
							LOG.debug("Tracking Data not found for UserID: "+usersList.get(i));
							continue;
						}
						double previousLat = 0;
						double previousLong = 0;
						for(int j=0;j<userLocationList.size();j++){
							long userID = userLocationList.get(j).getUserid();
							Date dateTime = userLocationList.get(j).getDate();
							
							UserLocation updateLocation = new UserLocation();
							if(j == 0){
								previousLat = userLocationList.get(j).getLatitude();
								previousLong = userLocationList.get(j).getLongitude();
								
								updateLocation.setUserid(userID);
								updateLocation.setDistance("0");
								updateLocation.setDate(dateTime);
								updateLocation.setDistanceValue(0);
								locationDistanceUpdatedList.add(updateLocation);
								continue;
							}
							
							
							// Calculate user current distance from address location
							String googleDistance = "";
							double distanceValue    = 0;
							if(apiCall == apiCallLimit){
								key++;
								if(key > 20){
									key = 1;
								}
								apiCall = 0;
							}
							
							UserLocation userLoc  = getGeoDistanceData(previousLat,previousLong,userLocationList.get(j).getLatitude(),userLocationList.get(j).getLongitude(),key);
							apiCall++;
							if(userLoc.getDistance() == null){
								googleDistance = "0";
							}else{
								googleDistance = userLoc.getDistance();
							}
							if(userLoc.getDistanceValue() > 0){
							   distanceValue  = userLoc.getDistanceValue();
							}else{
								distanceValue = 0;
							}
							previousLat    = userLocationList.get(j).getLatitude();
							previousLong   = userLocationList.get(j).getLongitude();
							
							updateLocation.setUserid(userID);
							updateLocation.setDistance(googleDistance);
							updateLocation.setDate(dateTime);
							updateLocation.setDistanceValue(distanceValue);
							locationDistanceUpdatedList.add(updateLocation);
						}
						
						//update distance to diff in distance between previous and current record
						trackingDAO.updateUserLocationDistance(locationDistanceUpdatedList);
						LOG.debug("User tracking completed: "+usersList.get(i));
					}
				}
				else
				{
					LOG.debug("No users exits : " + usersList.size());
				}
				LOG.debug("Completed for the company: "+companyList.get(k));
			}
			MailUtil mailUtil = new MailUtil();
			Session session =Session.getInstance(mailUtil.setMailingProperties1());

			Transport transport = session.getTransport("smtp");
			transport.connect("email-smtp.us-west-2.amazonaws.com", "AKIAJXW7T6XVNC77WM2Q", "AsNFJxvrnIjpMWABBXq0cCC3U2NIaBNRd66tfIuh9rW5");
			
			for(int k=0;k<companyList.size();k++){
				generateTrackingReport1(superAdminList.get(k), companyList.get(k),"IST", "bigbangtesting123@gmail.com",transport,session,mailUtil);	
			}
			transport.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public void generateTrackingReport1(long userID, long companyID,String timezone, String emailID,Transport transport, Session session,MailUtil mailUtil) 
			throws ParseException, IOException, NoSuchAlgorithmException, MessagingException {

		LOG.debug("Calling generateTrackingReport1 service method");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		String memberID = "";
		LOG.debug("Fetch list of all reporting members");
			memberID = userDAO.getLowerHierarchy(userID);

		List<Long> memberIDList = new ArrayList<Long>();
	    if (memberID != null && memberID.trim().length() != 0) {
	     String[] memberArray = memberID.split(",");

	     for (int i = 0; i < memberArray.length; i++) {
	      memberIDList.add(Long.parseUnsignedLong(memberArray[i]));
	     }
	    }
	    
		if (memberIDList == null || memberIDList.size() == 0) {
			LOG.debug("No users reporting");
//			mailUtil.sendHTMLMail1(emailID, null, "tracking_Report",
//					"No users Reporting for Company ID "+companyID, transport,session);
			return;
		}

		LOG.debug("No. of members:" + memberIDList.size());
 
		List<UserLocation> userLocations = trackingDAO.getAll(memberIDList, formatter.format(currentDate),companyID);

		ExportToExcel excel = new ExportToExcel();
		String filePath = excel.trackingExcel(userLocations, timezone, currentDate,companyID);

			mailUtil.sendHTMLMail1(emailID, filePath, "tracking_Report",
					"Company ID "+companyID, transport,session);
//			mailUtil.sendHTMLMail1("rishimodi115@gmail.com", filePath, "tracking_Report",
//					"Company ID "+companyID,transport,session);
//			mailUtil.sendHTMLMail1("hitesh.shah@bigbanginnovations.in", filePath, "tracking_Report",
//					"Company ID "+companyID,transport,session);
			LOG.debug("Email Sent "+companyID);
//			mailUtil.sendHTMLMail("manukhera@gmail.com", filePath, "tracking_Report",
//					"report");

	}
	
	public UserLocation getGeoDistanceData(final double startLatitude, final double startLongitude,
			final double endLatitude, final double endLongitude,int key) throws UnsupportedEncodingException {
		
		UserLocation userLoc = new UserLocation();
		try{
			
//		  Calendar cal = Calendar.getInstance();
//		  int hour = cal.get(Calendar.HOUR);
		  List<String> geoCodeKey = new ArrayList<String>();
		  geoCodeKey.addAll(Arrays.asList(((String) Properties.get("geocode.location.key")).split(",")));
		  
		  String Apikey = geoCodeKey.get(key);
//		  Object[] obj = {startLatitude,startLongitude,endLatitude,endLongitude,"AIzaSyD6xC-i4FnBPPJAs83NiBw2Bmchvx7CVYA"};
//		  HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?units=kms&origins=23.013,72.511&destinations=23.015,72.531&key=AIzaSyD6xC-i4FnBPPJAs83NiBw2Bmchvx7CVYA");
		  HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?units=kms&origins="+startLatitude+","
				  +startLongitude+"&destinations="+endLatitude+","+endLongitude+"&key="+Apikey);
		  //AIzaSyC6kt-BVsi4aKVJn0p-mrABFQOQNmLi5Oc
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
	      }

	        JsonObject jsonObject = new JsonObject();
			jsonObject = GsonUtil.getInstance().fromJson(stringBuilder.toString(), JsonObject.class);
	
			JsonObject location;
	
			// Read result
			JsonArray jArray = new JsonArray();
			if (jsonObject.has("rows")) {
				jArray = jsonObject.getAsJsonArray("rows");
			}
	
			String distance = "";
			long distanceValue = 0;
			JsonArray jElementArray   = new JsonArray();
			JsonObject jElementObject = new JsonObject();
			if (jArray != null && jArray.size() != 0) {
				// Get JSON Array called "results" and then get the 0th complete object as JSON
				location = jArray.get(0).getAsJsonObject();
				if(location != null){
					jElementArray = location.getAsJsonArray("elements");
					jElementObject = jElementArray.get(0).getAsJsonObject().getAsJsonObject("distance");
					distance = jElementObject.get("text").toString();
					distance = distance.replace("\"","");
					userLoc.setDistance(distance);
					distanceValue = jElementObject.get("value").getAsLong();
					userLoc.setDistanceValue(distanceValue);
				}else{
					userLoc.setDistance("0");
					userLoc.setDistanceValue(0);
				}
				
//				LOG.info("Distance:" + distance);
			}
			return userLoc;
		}catch(Exception e){
			LOG.debug("Error getting geo distance: "+e);
			return new UserLocation();
		}
		
	}

	@Override
	public void autoDistanceTrackingSum() throws IOException,
			NumberFormatException, SendNotificationException {
		LOG.debug("fire sheduler for getting distance sum from tracking");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		Date currentTime = (DateTimeFormatClass.getAbsoluteDate(new Date())).getTime(); 
		//List<Long> companyList = new ArrayList<Long>();
		List<Long> companyList = trackingDAO.getAllTrackingCompanies();
		LOG.debug("companylist  : " + companyList.size());
		if (companyList.size() == 0) {
			LOG.debug("No company exist");
			return;
		}
		
		try{
			for(int k=0;k<companyList.size();k++){
				List<Long> usersList = trackingDAO.getUsers(companyList.get(k));
				//LOG.debug("userList "+usersList.size()+" Company ID: "+companyList.get(k));
				
				if(usersList.size() > 0)
				{
					LOG.debug("User List fetched for users: "+usersList);
					List<UserDistanceSum> userLocationDistanceSumList = trackingDAO.getUserDistanceSum(usersList, formatter.format(currentDate), companyList.get(k));
					if(userLocationDistanceSumList.size() <= 0){
						LOG.debug("Distance sum not found given user list: ");
						continue;
					}
					LOG.debug("Total UserDistanceSum List found size: "+userLocationDistanceSumList.size());
					
					//add distance sum of all the distance travelled of current date
					int retVal = trackingDAO.addUserDailyDistanceSum(userLocationDistanceSumList,currentTime);
					if(retVal > 0){
						LOG.debug("Completed for the adding distance sum company: "+companyList.get(k));
					}else{
						LOG.debug("Error adding distance sum company: "+companyList.get(k));
					}
				}
				else
				{
					LOG.debug("No users exits for company ID : " + companyList.get(k));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			LOG.debug("Exception: "+e);
		}
		
	}

	@Override
	public void autoTrackingGoogleDistanceUpdateMay() throws IOException,
	NumberFormatException, SendNotificationException {


		LOG.debug("fire sheduler for getting distance from tracking");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		List<Long> companyList = new ArrayList<Long>();
//		companyList.add((long) 374);
		companyList = trackingDAO.getAllCompanyIDs();	
		LOG.debug("companylist  : " + companyList.size());
		if (companyList.size() == 0) {
			LOG.debug("No company exist");
			return;
		}
		
		try{
			for(int k=0;k<companyList.size();k++){
			List<Long> usersList = trackingDAO.getUsers(companyList.get(k));
			LOG.debug("userList "+usersList.size()+" Company ID: "+companyList.get(k));
			if(usersList.size() > 0)
			{
				List<UserLocation> locationDistanceUpdatedList = new ArrayList<UserLocation>(); 
				for(int i = 0;i<usersList.size();i++){
					
					List<UserLocation> userLocationList = trackingDAO.getUserLocationList1(usersList.get(i), formatter.format(currentDate));
					if(userLocationList.size() <= 0){
						LOG.debug("Tracking Data not found for UserID: "+usersList.get(i));
						continue;
					}
//					double previousLat = 0;
//					double previousLong = 0;
					for(int j=0;j<userLocationList.size();j++){
					long userID = userLocationList.get(j).getUserid();
					Date dateTime = userLocationList.get(j).getDate();
					
					   UserLocation updateLocation = new UserLocation();
						updateLocation.setUserid(userID);
						updateLocation.setDistance("0");
						updateLocation.setDate(dateTime);
						updateLocation.setDistanceValue(0);
						locationDistanceUpdatedList.add(updateLocation);
					}
				
				//update distance to diff in distance between previous and current record
				trackingDAO.updateUserLocationDistance(locationDistanceUpdatedList);
				LOG.debug("Updated for User: "+usersList.get(i));
			}
		}
		else
		{
			LOG.debug("No users exits for the company: " +companyList.get(k));
		}
		LOG.debug("Update for company "+companyList.get(k));
		}
	}catch(Exception e){
		e.printStackTrace();
	}
	}
//	public void autoTrackingGoogleDistanceUpdateMay() throws IOException,
//			NumberFormatException, SendNotificationException {
//
//		
//		LOG.debug("fire sheduler for getting distance from tracking");
//		
//		Date currentDate = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//		List<Long> companyList = new ArrayList<Long>();
////		companyList.add((long) 52);
//        companyList = trackingDAO.getAllCompanyIDs();	
//		LOG.debug("companylist  : " + companyList.size());
//		if (companyList.size() == 0) {
//			LOG.debug("No company exist");
//			return;
//		}
//		
//		try{
//			for(int k=0;k<companyList.size();k++){
//			List<Long> usersList = trackingDAO.getUsers(companyList.get(k));
//			LOG.debug("userList "+usersList.size()+" Company ID: "+companyList.get(k));
//				if(usersList.size() > 0)
//				{
//					List<UserLocation> locationDistanceUpdatedList = new ArrayList<UserLocation>(); 
//					for(int i = 0;i<usersList.size();i++){
//						
//						List<UserLocation> userLocationList = trackingDAO.getUserLocationList(usersList.get(i), formatter.format(currentDate));
//						if(userLocationList.size() <= 0){
//							LOG.debug("Tracking Data not found for UserID: "+usersList.get(i));
//							continue;
//						}
//						double previousLat = 0;
//						double previousLong = 0;
//						for(int j=0;j<userLocationList.size();j++){
//							long userID = userLocationList.get(j).getUserid();
//							Date dateTime = userLocationList.get(j).getDate();
//							
//							UserLocation updateLocation = new UserLocation();
//							if(j == 0){
//								previousLat = userLocationList.get(j).getLatitude();
//								previousLong = userLocationList.get(j).getLongitude();
//								
//								updateLocation.setUserid(userID);
//								updateLocation.setDistance("0");
//								updateLocation.setDate(dateTime);
//								updateLocation.setDistanceValue(0);
//								locationDistanceUpdatedList.add(updateLocation);
//								continue;
//							}
//							
//							
//							// Calculate user current distance from address location
//							double distanceInMeters = distance(previousLat,previousLong,userLocationList.get(j).getLatitude(),userLocationList.get(j).getLongitude());
//							distanceInMeters = Math.round(distanceInMeters);
//							//LOG.debug("distance in meters: "+distanceInMeters);
//							double inKm = 0;
//					    	String unit;
//					    	if(distanceInMeters > 1000){
//					    		inKm = distanceInMeters/1000.0;
//					    		unit = " km";
//					    	}else{
//					    		inKm = distanceInMeters;
//					    		unit = " m";
//					    	}
//					    	
//							previousLat    = userLocationList.get(j).getLatitude();
//							previousLong   = userLocationList.get(j).getLongitude();
//							
//							updateLocation.setUserid(userID);
//							updateLocation.setDistance(inKm+""+unit);
//							updateLocation.setDate(dateTime);
//							updateLocation.setDistanceValue(distanceInMeters);
//							locationDistanceUpdatedList.add(updateLocation);
//						}
//						
//						//update distance to diff in distance between previous and current record
//						trackingDAO.updateUserLocationDistance(locationDistanceUpdatedList);
//						LOG.debug("Updated for User: "+usersList.get(i));
//					}
//				}
//				else
//				{
//					LOG.debug("No users exits for the company: " +companyList.get(k));
//				}
//				LOG.debug("Update for company "+companyList.get(k));
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}


	@Override
	public void autoDistanceTrackingSumForMonth() throws IOException,
			NumberFormatException, SendNotificationException {
		LOG.debug("fire sheduler for getting distance sum from tracking for a month");
		
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "2017/9/1";
		String endTime   = "2017/9/12";
		Date currentTime = (DateTimeFormatClass.getAbsoluteDate(new Date())).getTime(); 
		//List<Long> companyList = new ArrayList<Long>();
		List<Long> companyList = trackingDAO.getAllTrackingCompanies();
		LOG.debug("companylist  : " + companyList.size());
		if (companyList.size() == 0) {
			LOG.debug("No company exist");
			return;
		}
		
		try{
			for(int k=0;k<companyList.size();k++){
				LOG.debug("Starting for company: "+companyList.get(k));
				List<Long> usersList = trackingDAO.getUsers(companyList.get(k));
				//LOG.debug("userList "+usersList.size()+" Company ID: "+companyList.get(k));
				
				if(usersList.size() > 0)
				{
					LOG.debug("User List fetched for users: "+usersList);
					List<UserDistanceSum> userLocationDistanceSumList = trackingDAO.getUserDistanceSumMonth(usersList, companyList.get(k),startTime,endTime);
					LOG.debug("Total UserDistanceSum List found size: "+userLocationDistanceSumList.size());
					if(userLocationDistanceSumList.size() <= 0){
						LOG.debug("Distance sum not found given user list: ");
						continue;
					}
					
					//add distance sum of all the distance travelled of current date
					int retVal = trackingDAO.addUserDailyDistanceSum(userLocationDistanceSumList,currentTime);
					if(retVal > 0){
						LOG.debug("Completed for the adding distance sum company: "+companyList.get(k));
					}else{
						LOG.debug("Error adding distance sum company: "+companyList.get(k));
					}
				}
				else
				{
					LOG.debug("No users exits for company ID : " + companyList.get(k));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			LOG.debug("Exception: "+e);
		}
		
	}


	
	
}
