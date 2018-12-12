package com.bigbang.teamworksScheduler.dao;

import java.util.Comparator;
import java.util.Date;
 
import com.google.gson.annotations.SerializedName;

public class UserLocation {

	private long userid;
	private long companyid;
	private String time;
	private String location;
	private Date date;
	private double latitude;
	private double longitude;
	//private User user;

	private String FirstName;
	private String LastName;
	private String imageURL;
	@SerializedName("isGPSOn")
	private boolean gpsOn;
	@SerializedName("isMockLocation")
	private boolean mockLocation;
	private String trackingType;

	/**
	 * . Default Constructor
	 */
	public UserLocation() {

	}

	public String getTrackingType() {
		return trackingType;
	}

	public void setTrackingType(String trackingType) {
		this.trackingType = trackingType;
	}

	public boolean isMockLocation() {
		return mockLocation;
	}

	public void setMockLocation(boolean mockLocation) {
		this.mockLocation = mockLocation;
	}

	/**
	 * @param userid
	 *            User ID
	 * @param date
	 *            Date
	 * @param location
	 *            Location
	 * @param latitude
	 *            Latitude
	 * @param longitude
	 *            Longitude
	 * @param companyid
	 *            Company ID
	 */
	public UserLocation(final long userid, final Date date, final String location, final double latitude,
			final double longitude, final long companyid, final boolean gpsOn, boolean mockLocation) {
		this.userid = userid;
		this.date = date;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
		this.companyid = companyid;
		this.gpsOn = gpsOn;
		this.mockLocation = mockLocation;
	}

	public boolean isGpsOn() {
		return gpsOn;
	}

	public void setGpsOn(boolean isGPSOn) {
		this.gpsOn = isGPSOn;
	}

	/**
	 * @param userId
	 *            UserID
	 */
	public void setUserId(final long userId) {
		this.userid = userId;
	}

	/**
	 * @return UserID
	 */
	public long getUserId() {
		return userid;
	}

	/**
	 * @param date
	 *            Date
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param location
	 *            Location
	 */
	public void setLocation(final String location) {
		this.location = location;
	}

	/**
	 * @return Location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param latitude
	 *            Latitude
	 */
	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return Latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param longitude
	 *            Longitude
	 */
	public void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return Longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return Time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            Time
	 */
	public void setTime(final String time) {
		this.time = time;
	}

	/**
	 * @return CompanyId
	 */
	public long getCompanyId() {
		return companyid;
	}

	/**
	 * @param companyId
	 *            Company ID
	 */
	public void setCompanyId(final long companyId) {
		this.companyid = companyId;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	// Comparator for sorting the UserLocation list based on User's First name - Last name
	public static Comparator<UserLocation> firstNameComparator = new Comparator<UserLocation>() {

		public int compare(final UserLocation userLocation1, final UserLocation userLocation2) {
			String firstName1 = userLocation1.getFirstName().toUpperCase();
			String firstName2 = userLocation2.getFirstName().toUpperCase();

			int compResult = firstName1.compareTo(firstName2);

			if (compResult != 0) {
				return compResult;
			} else {
				String lastName1 = userLocation1.getLastName().toUpperCase();
				String lastName2 = userLocation2.getLastName().toUpperCase();
				return lastName1.compareTo(lastName2);
			}
		}
	};

}
