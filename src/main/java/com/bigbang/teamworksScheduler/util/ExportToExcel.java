package com.bigbang.teamworksScheduler.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bigbang.teamworksScheduler.beans.Attendance;
import com.bigbang.teamworksScheduler.beans.Company;
import com.bigbang.teamworksScheduler.beans.UserLocation;
import com.bigbang.teamworksScheduler.beans.Users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExportToExcel {
	Logger LOG = LogManager.getLogger(ExportToExcel.class);
	XSSFWorkbook workbook;
	CellStyle styleHeader;
	CellStyle styleRightPanel;
	CellStyle styleAlternateRow;
	CellStyle styleLowerBorder;
	CellStyle styleUpperBorder;
	CellStyle styleMiddleAlign;
	CellStyle styleAllRow;

	public ExportToExcel() {
		workbook = new XSSFWorkbook();

		// Define cell styles
		styleHeader = workbook.createCellStyle();
		styleAlternateRow = workbook.createCellStyle();
		styleRightPanel = workbook.createCellStyle();
		styleLowerBorder = workbook.createCellStyle();
		styleUpperBorder = workbook.createCellStyle();
		styleMiddleAlign = workbook.createCellStyle();
		styleAllRow      = workbook.createCellStyle();
		
		styleHeader.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styleHeader.setBorderBottom(CellStyle.BORDER_MEDIUM);
		styleHeader.setAlignment(CellStyle.ALIGN_CENTER);

		styleAlternateRow.setFillForegroundColor(IndexedColors.LEMON_CHIFFON .getIndex());
		styleAlternateRow.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		styleAlternateRow.setBorderBottom(CellStyle.BORDER_THIN);
		styleAlternateRow.setBorderTop(CellStyle.BORDER_THIN);
		styleAlternateRow.setBorderLeft(CellStyle.BORDER_THIN);
		styleAlternateRow.setBorderRight(CellStyle.BORDER_THIN);

		styleRightPanel.setFillForegroundColor(IndexedColors.GREEN.getIndex());

		styleLowerBorder.setBorderBottom(CellStyle.BORDER_MEDIUM);
		styleUpperBorder.setBorderTop(CellStyle.BORDER_MEDIUM);
		styleMiddleAlign.setAlignment(CellStyle.ALIGN_CENTER);

		styleAllRow.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		styleAllRow.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		styleAllRow.setBorderBottom(CellStyle.BORDER_THIN);
		styleAllRow.setBorderTop(CellStyle.BORDER_THIN);
		styleAllRow.setBorderLeft(CellStyle.BORDER_THIN);
		styleAllRow.setBorderRight(CellStyle.BORDER_THIN);
	}

	/**
	 * Create attendance excel
	 * 
	 * @param month
	 * @param year
	 * @param timezone
	 * @param userToAttendanceMap
	 * @param company
	 * @throws ParseException
	 * @throws IOException
	 */
	public String attendanceNewExcel(Date startDate, String timezone,
			LinkedHashMap<Users, List<Attendance>> userToAttendanceMap, Company company,Map<Long, Users> idToManagerMap,
			List<Users> memberNotCheckedIn,int mCheckedIn,int mNotCheckedIn,int tCheckedIn,
			int tNotCheckedIn) throws ParseException, IOException {
		
		try{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		// create sheet in xlsx workbook
		XSSFSheet sheet;
		sheet = workbook.createSheet("Attendance_" + sdf.format(startDate));
		
		SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
		timeformat.setTimeZone(TimeZone.getTimeZone(timezone));
		SimpleDateFormat seconds = new SimpleDateFormat("HH:mm:ss");

		int rowNum = 0;
		
		Row headerMerge = sheet.createRow(rowNum);
		Cell headerMergeCell1 = headerMerge.createCell(0);
		Cell headerMergeCell2 = headerMerge.createCell(1);
		Cell headerMergeCell3 = headerMerge.createCell(2);
		Cell headerMergeCell4 = headerMerge.createCell(3);
		
		headerMergeCell1.setCellValue("Total Manager Reporting");
		headerMergeCell3.setCellValue("Total Team Member Reporting");
		
		headerMergeCell1.setCellStyle(styleHeader);
		headerMergeCell2.setCellStyle(styleHeader);
		headerMergeCell3.setCellStyle(styleHeader);
		headerMergeCell4.setCellStyle(styleHeader);
		
		sheet.addMergedRegion(new CellRangeAddress(
				rowNum, //first row (0-based)
				rowNum, //last row  (0-based)
		        0, //first column (0-based)
		        1  //last column  (0-based)
		));
		
		sheet.addMergedRegion(new CellRangeAddress(
				rowNum, //first row (0-based)
				rowNum, //last row  (0-based)
		        2, //first column (0-based)
		        3  //last column  (0-based)
		));
		
		Row headerMain = sheet.createRow(rowNum + 1);
		Cell headerMainCell1 = headerMain.createCell(0);
		Cell headerMainCell2 = headerMain.createCell(1);
		Cell headerMainCell3 = headerMain.createCell(2);
		Cell headerMainCell4 = headerMain.createCell(3);
		
		headerMainCell1.setCellValue("Checked-In");
		headerMainCell2.setCellValue("Not Checked-In");
		headerMainCell3.setCellValue("Checked-In");
		headerMainCell4.setCellValue("Not Checked-In");
		
		headerMainCell1.setCellStyle(styleHeader);
		headerMainCell2.setCellStyle(styleHeader);
		headerMainCell3.setCellStyle(styleHeader);
		headerMainCell4.setCellStyle(styleHeader);
		
		
		Row rowStart;
		Cell managerCheckedIn;
		Cell managerNotCheckedIn;
		Cell teamMemberCheckedIn;
		Cell teamMemberNotCheckedIn;
		
		// Create rows for user
		rowStart = sheet.createRow(rowNum + 2);
		
		managerCheckedIn = rowStart.createCell(0);
		if(mCheckedIn > 0){
			if(mNotCheckedIn > 0){
				mCheckedIn = mCheckedIn - mNotCheckedIn; 
			}
			managerCheckedIn.setCellValue(Math.abs(mCheckedIn));
		}else{
			managerCheckedIn.setCellValue("N.A");
		}
		
		managerNotCheckedIn = rowStart.createCell(1);
		if(mNotCheckedIn > 0)
			managerNotCheckedIn.setCellValue(mNotCheckedIn);
		else
			managerNotCheckedIn.setCellValue("N.A");
		
		teamMemberCheckedIn = rowStart.createCell(2);
		if(tCheckedIn > 0){
			if(tNotCheckedIn > 0){
				tCheckedIn = tCheckedIn - tNotCheckedIn; 
			}
			teamMemberCheckedIn.setCellValue(tCheckedIn);
		}else{
			teamMemberCheckedIn.setCellValue("N.A");
		}
		
		teamMemberNotCheckedIn = rowStart.createCell(3);
		if(tNotCheckedIn > 0)
			teamMemberNotCheckedIn.setCellValue(tNotCheckedIn);
		else
			teamMemberNotCheckedIn.setCellValue("N.A");
		
		rowNum = rowNum+4;
		
		// Create header row
		Row headerRow = sheet.createRow(rowNum);

		Cell headerCell1 = headerRow.createCell(0);
		Cell headerCell2 = headerRow.createCell(1);
		Cell headerCell3 = headerRow.createCell(2);
		Cell headerCell4 = headerRow.createCell(3);
		Cell headerCell5 = headerRow.createCell(4);
		Cell headerCell6 = headerRow.createCell(5);

		headerCell1.setCellValue("User Name");
		headerCell2.setCellValue("Employee Code");
		headerCell3.setCellValue("Mobile No");
		headerCell4.setCellValue("Manager");
		headerCell5.setCellValue("Manager Contact");
		headerCell6.setCellValue("Date");
		
		// Set header style
		headerCell1.setCellStyle(styleHeader);
		headerCell2.setCellStyle(styleHeader);
		headerCell3.setCellStyle(styleHeader);
		headerCell4.setCellStyle(styleHeader);
		headerCell5.setCellStyle(styleHeader);
		headerCell6.setCellStyle(styleHeader);
		
		int cellCount = 6;
		// Get maximum days of month to prepare month chart in sheet
		int maxDay = 1; //one day data
		
		GregorianCalendar gcal = new GregorianCalendar();
		if(startDate != null)
		{
			gcal.setTime(startDate);
		}
		
		Map<Date, Integer> dateToCell = new HashMap<Date, Integer>();
		Cell headerCell;
		int cellCountNew = 0;
		headerCell = headerRow.createCell(cellCount);
		if(startDate != null)
		{
		    Date d = gcal.getTime();
		    String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
		    Date dates = new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
			headerCell.setCellValue(sdf.format(dates));
			headerCell.setCellStyle(styleHeader);
			dateToCell.put(dates, cellCount);
			cellCountNew = cellCount;
			cellCount++;
		    gcal.add(Calendar.DATE, 1);
		}

		Row rowStartTime;
		Cell cellMemberName;
		Cell cellMemberEmployeeCode;
		Cell cellMemberContact;
		Cell cellMemberManagerName;
		Cell cellMemberManagerContact;

		//all not checked in users
		for (Users user : memberNotCheckedIn) {
			long userID = user.getUserId();
			
			// Create rows for user
			rowStartTime = sheet.createRow(rowNum + 1);

			cellMemberName = rowStartTime.createCell(0);
			cellMemberName.setCellValue(user.getFirstName() + " " + user.getLastName());
			
			cellMemberEmployeeCode = rowStartTime.createCell(1);
			cellMemberEmployeeCode.setCellValue(user.getEmployeeCode());
			
			cellMemberContact = rowStartTime.createCell(2);
			cellMemberContact.setCellValue(user.getMobileNo1());
			
			String managerMobileNo = "";
			String managerName = "";
			if (idToManagerMap.containsKey(userID)) {
				managerMobileNo = idToManagerMap.get(userID).getManagerContact();
				managerName = idToManagerMap.get(userID).getManagerName();
			}
				
			cellMemberManagerName = rowStartTime.createCell(3);
			cellMemberManagerName.setCellValue(managerName);
			
			cellMemberManagerContact = rowStartTime.createCell(4);
			cellMemberManagerContact.setCellValue(managerMobileNo);

			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 0, 0));

			Cell startDateCell = rowStartTime.createCell(5);
			startDateCell.setCellValue("Check In");
			
			Cell cell1 = rowStartTime.createCell(cellCountNew);

			// Calculate start time
			String startTime = "";
			startTime = "-- : --";
			startDateCell.setCellValue("Check In");
				
			startTime = "-- : --";
			cell1.setCellValue(startTime);
			
			// Set cell style
			cell1.setCellStyle(styleAllRow);
			
			// Set row style
			rowStartTime.setRowStyle(styleAllRow);
			cellMemberName.setCellStyle(styleAllRow);
			cellMemberContact.setCellStyle(styleAllRow);
			cellMemberManagerName.setCellStyle(styleAllRow);
			cellMemberManagerContact.setCellStyle(styleAllRow);
			cellMemberEmployeeCode.setCellStyle(styleAllRow);
			startDateCell.setCellStyle(styleAllRow);
			rowNum += 1;
		}

		// Read attendance details for each user
		for (Users user : userToAttendanceMap.keySet()) {
			long userID = user.getUserId();
			
			// Create rows for user
			rowStartTime = sheet.createRow(rowNum + 1);

			cellMemberName = rowStartTime.createCell(0);
			cellMemberName.setCellValue(user.getFirstName() + " " + user.getLastName());
			
			cellMemberEmployeeCode = rowStartTime.createCell(1);
			cellMemberEmployeeCode.setCellValue(user.getEmployeeCode());
			
			cellMemberContact = rowStartTime.createCell(2);
			cellMemberContact.setCellValue(user.getMobileNo1());
			
			String managerMobileNo = "";
			String managerName = "";
			if (idToManagerMap.containsKey(userID)) {
				managerMobileNo = idToManagerMap.get(userID).getManagerContact();
				managerName = idToManagerMap.get(userID).getManagerName();
			}
				
			cellMemberManagerName = rowStartTime.createCell(3);
			cellMemberManagerName.setCellValue(managerName);
			
			cellMemberManagerContact = rowStartTime.createCell(4);
			cellMemberManagerContact.setCellValue(managerMobileNo);

			sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 0, 0));

			Cell startDateCell = rowStartTime.createCell(5);
			
			Map<Date, Long> hourMap = new HashMap<Date, Long>();
			Map<Date, String> startTimeMap = new HashMap<Date, String>();
			long totalHours = 0;

			for (Attendance attendance : userToAttendanceMap.get(user)) {
				// For each attendance of user insert values for start time and end time
				if (!dateToCell.containsKey(attendance.getAttendanceDate())) {
					continue;
				}
				Cell cell1 = rowStartTime.createCell(dateToCell.get(attendance.getAttendanceDate()));

				String startTime = "";

				// Calculate start time
				if(attendance.getStartTime() != null){
					startDateCell.setCellValue("Check In");
					
					if (attendance.getStartTime() != null) {
						startTime = timeformat.format(attendance.getStartTime());
					} 
					if (startTimeMap.containsKey(attendance.getAttendanceDate())) {
						startTime = startTimeMap.get(attendance.getAttendanceDate()) + " " + startTime;
					}
					startTimeMap.put(attendance.getAttendanceDate(), startTime);
					cell1.setCellValue(startTime);
					
				}else if(attendance.getStartTime() == null && attendance.getUpdatedTimeIn() != null){
					startDateCell.setCellValue("Check In");
					cell1.setCellValue("Pending");
					
				}else if(attendance.getLeaveTypeID() > 0){
					startDateCell.setCellValue("Leave");
					
					if(attendance.getLeaveTypeID() > 0){
						cell1.setCellValue(attendance.getLeaveDay());	
					}else{
						cell1.setCellValue("  ");
					}
					
				}else if(attendance.getManualAttendance() != null){
					startDateCell.setCellValue("Manual");
					
					if(attendance.isManualApproved() == true){
						cell1.setCellValue(attendance.getManualAttendance());
					}else{
						cell1.setCellValue("Pending");
					}
					
				}else{
					startDateCell.setCellValue("Check In");
					startTime = "-- : --";
					cell1.setCellValue(startTime);
				}
				
				// Set cell style
				if (rowNum % 2 != 0) {
					cell1.setCellStyle(styleAlternateRow);
				}
			}

			for (Date attDate : hourMap.keySet()) {
				totalHours = totalHours + hourMap.get(attDate);
			}

			// Set row style
			if (rowNum % 2 != 0) {
				rowStartTime.setRowStyle(styleAlternateRow);
				cellMemberName.setCellStyle(styleAlternateRow);
				cellMemberContact.setCellStyle(styleAlternateRow);
				cellMemberManagerName.setCellStyle(styleAlternateRow);
				cellMemberManagerContact.setCellStyle(styleAlternateRow);
				cellMemberEmployeeCode.setCellStyle(styleAlternateRow);
				startDateCell.setCellStyle(styleAlternateRow);
			}
			rowNum += 1;
		}

		// autosize all the columns
		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i);
		}

		FileOperation fileOperation = new FileOperation();
		String file = fileOperation.doUpload(workbook, "Attendance_" + FileOperation.generateUniqueKey() + ".xlsx",
				company.getCompanyID());
		//String file = fileOperation.doUpload(workbook, "Attendance1.xlsx",
				//company.getCompanyid());
		workbook.close();

		return file;
		}
		catch(Exception e){
//			LOG.error("excell exception e");
			return null;
		}
		
	}
	
	/**
	 * Create attendance excel
	 * 
	 * @param month
	 * @param year
	 * @param timezone
	 * @param userToAttendanceMap
	 * @param company
	 * @throws ParseException
	 * @throws IOException
	 */
	public String trackingExcel(List<UserLocation> userLocation, String timezone, Date startDate, long companyId)
			throws ParseException, IOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		// create sheet in xlsx workbook
		XSSFSheet sheet;
		sheet =	workbook.createSheet("Distance_Tracking_"+startDate);

		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		timeformat.setTimeZone(TimeZone.getTimeZone(timezone));

		int rowNum = 0;

		// Create header row
		Row headerRow = sheet.createRow(rowNum);
		headerRow.setRowStyle(styleHeader);

		Cell headerCell1 = headerRow.createCell(0);
		Cell headerCell2 = headerRow.createCell(1);
		Cell headerCell3 = headerRow.createCell(2);
		Cell headerCell4 = headerRow.createCell(3);
		Cell headerCell5 = headerRow.createCell(4);
		Cell headerCell6 = headerRow.createCell(5);
		Cell headerCell7 = headerRow.createCell(6);
		Cell headerCell8 = headerRow.createCell(7);
		
		headerCell1.setCellValue("Name");
		headerCell1.setCellStyle(styleHeader);

		headerCell2.setCellValue("Latitude");
		headerCell2.setCellStyle(styleHeader);

		headerCell3.setCellValue("Longitude");
		headerCell3.setCellStyle(styleHeader);

		headerCell4.setCellValue("Location From");
		headerCell4.setCellStyle(styleHeader);

		headerCell5.setCellValue("Location To");
		headerCell5.setCellStyle(styleHeader);

		headerCell6.setCellValue("Time");
		headerCell6.setCellStyle(styleHeader);
		
		headerCell7.setCellValue("Distance");
		headerCell7.setCellStyle(styleHeader);
		
		headerCell8.setCellValue("GPS");
		headerCell8.setCellStyle(styleHeader);
		
		Row row;
		Cell cellName;
		Cell cellLatitude;
		Cell cellLongitude;
		Cell cellLocationFrom;
		Cell cellLocationTo;
		Cell cellTime;
		Cell cellDistance;
		Cell cellGPS;
		
		String previousAddress = "";
		String defaultDistance;
		long userID = 0;
		// Read attendance details for each user
		for (UserLocation location : userLocation) {
			rowNum += 1;
			// Create rows for user
			row = sheet.createRow(rowNum);
//			double roundFig = round(Double.parseDouble(location.getLocation()), 2); 
			defaultDistance = location.getDistance();
			if(userID != location.getUserid()){
				defaultDistance = "0";
				previousAddress = "";
			}

			cellName 		 = row.createCell(0);
			cellLatitude	 = row.createCell(1);
			cellLongitude    = row.createCell(2);
			cellLocationFrom = row.createCell(3);
			cellLocationTo   = row.createCell(4);
			cellTime         = row.createCell(5);
			cellDistance     = row.createCell(6);
			cellGPS          = row.createCell(7);

			cellName.setCellValue(location.getFirstName() + " " + location.getLastName());
			cellLatitude.setCellValue(location.getLatitude());
			cellLongitude.setCellValue(location.getLongitude());
			cellLocationFrom.setCellValue(previousAddress);
			cellLocationTo.setCellValue(location.getLocation());
			cellTime.setCellValue(timeformat.format(location.getDate()));
			cellDistance.setCellValue(defaultDistance);
			if (location.isGpsOn()) {
				cellGPS.setCellValue("On");
			} else {
				cellGPS.setCellValue("Off");
			}
			userID = location.getUserid();
			previousAddress = location.getLocation();
		}
		// autosize all the columns
		for (int i = 0; i < 9; i++) {
			sheet.autoSizeColumn(i);
		}

		FileOperation fileOperation = new FileOperation();
		String file = fileOperation.doUpload(workbook, "Tracking_" + FileOperation.generateUniqueKey() + ".xlsx",
				companyId);
		//String file = fileOperation.doUpload(workbook, "Tracking.xlsx",
			//	companyId);
		workbook.close();

		return file;
	}
	
	
	/**
	 * Create attendance excel
	 * 
	 * @param month
	 * @param year
	 * @param timezone
	 * @param userToAttendanceMap
	 * @param company
	 * @throws ParseException
	 * @throws IOException
	 */
	public String trackingExcelReport(List<UserLocation> userLocation, String timezone, Date currentDate, 
			long companyId,Map<Long, LinkedHashMap<Date, Double>> allUserData,Map<Long, Users> idToUserMap, Date mapDate)
			throws ParseException, IOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format);
		// create sheet in xlsx workbook
		XSSFSheet sheet;
		sheet = workbook.createSheet("Tracking_" + sdf.format(currentDate));

		SimpleDateFormat timeformat = new SimpleDateFormat(DateTimeFormatClass.DEFAULT_DATE_TIME_FORMAT);
		timeformat.setTimeZone(TimeZone.getTimeZone(timezone));

		int rowNum = 0;

		// Create header row
		Row headerRow = sheet.createRow(rowNum);
		headerRow.setRowStyle(styleHeader);

		Cell headerCell1 = headerRow.createCell(0);
		Cell headerCell2 = headerRow.createCell(1);
		Cell headerCell3 = headerRow.createCell(2);
		Cell headerCell4 = headerRow.createCell(3);
		Cell headerCell5 = headerRow.createCell(4);
		Cell headerCell6 = headerRow.createCell(5);
		Cell headerCell7 = headerRow.createCell(6);

		headerCell1.setCellValue("Name");
		headerCell1.setCellStyle(styleHeader);

		headerCell2.setCellValue("Time");
		headerCell2.setCellStyle(styleHeader);

		headerCell3.setCellValue("Distance (meters)");
		headerCell3.setCellStyle(styleHeader);
		
		headerCell4.setCellValue("Location");
		headerCell4.setCellStyle(styleHeader);

		headerCell5.setCellValue("Latitude");
		headerCell5.setCellStyle(styleHeader);

		headerCell6.setCellValue("Longitude");
		headerCell6.setCellStyle(styleHeader);

		headerCell7.setCellValue("GPS");
		headerCell7.setCellStyle(styleHeader);

		Row row;
		Cell cellName;
		Cell cellTime;
		Cell cellDistance;
		Cell cellLocation;
		Cell cellLatitude;
		Cell cellLongitude;
		Cell cellGPS;

		// Read attendance details for each user
		for (UserLocation location : userLocation) {
			rowNum += 1;
			// Create rows for user
			row = sheet.createRow(rowNum);

			cellName = row.createCell(0);
			cellTime = row.createCell(1);
			cellDistance = row.createCell(2);
			cellLocation = row.createCell(3);
			cellLatitude = row.createCell(4);
			cellLongitude = row.createCell(5);
			cellGPS = row.createCell(6);

			cellName.setCellValue(location.getFirstName() + " " + location.getLastName());
			cellTime.setCellValue(timeformat.format(location.getDate()));
			cellDistance.setCellValue(location.getDistanceValue());
			cellLocation.setCellValue(location.getLocation());
			cellLatitude.setCellValue(location.getLatitude());
			cellLongitude.setCellValue(location.getLongitude());
			if (location.isGpsOn()) {
				cellGPS.setCellValue("On");
			} else {
				cellGPS.setCellValue("Off");
			}
		}
		
		// autosize all the columns
		for (int i = 0; i < 7; i++) {
			sheet.autoSizeColumn(i);
		}

		// create sheet in xlsx workbook
		sheet = workbook.createSheet("Distance_Details");
		LOG.debug("Sheet for Distance Details");
		
		int rowNumber = 0;
		// Create header row
		Row headerRow1 = sheet.createRow(rowNumber);

		Cell headerDetailCell1 = headerRow1.createCell(0);
//		Cell headerDetailCell2 = headerRow1.createCell(1);

		headerDetailCell1.setCellValue("Names");
//		headerDetailCell2.setCellValue("Dates");
		
		// Set header style
		headerDetailCell1.setCellStyle(styleHeader);
//		headerDetailCell2.setCellStyle(styleHeader);

		int cellCount = 1;
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(currentDate);
		
		
		
		Cell headerCel;
		Date d = gcal.getTime();
		
		d.setMinutes(00);
		d.setHours(00);
		d.setSeconds(00);
		
		LOG.debug("currentDate d: "+currentDate);
		Map<Date, Integer> dateToCell = new HashMap<Date, Integer>();
	    headerCel = headerRow1.createCell(cellCount);
	    String dateStr = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).format(d);
	    Date dates = new SimpleDateFormat(DateTimeFormatClass.dd_MM_yyyy_format).parse(dateStr);
	    headerCel.setCellValue(sdf.format(dates));
		headerCel.setCellStyle(styleHeader);
		dateToCell.put(dates, cellCount);
		
//	    String dateStr1 = new SimpleDateFormat(DateTimeFormatClass.DEFAULT_DATE_FORMAT_WITH_TIME).format(d);
//	    SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeFormatClass.DEFAULT_DATE_FORMAT_WITH_TIME);
//	    Date monthDate = dateFormat.parse(dateStr1);
//		LOG.debug("month date size: "+monthDate);
		cellCount++;


		// Create column for total hours
//		headerCel = headerRow1.createCell(cellCount);
//		headerCel.setCellValue("Total Distance");
//		headerCel.setCellStyle(styleHeader);
		
		Row rows;
		Cell cellUserName;
		Cell cellUserDistance = null;
//		Cell cellTotalDistance;
		
		
//		try{
		Iterator it = allUserData.entrySet().iterator();
		
		int temp = 0;
		while (it.hasNext()) {
			double meters = 0;
			temp++;
			rowNumber += 1;
			// Create rows for user
			rows = sheet.createRow(rowNumber);

			cellUserName      = rows.createCell(0);
			long user_id = 0;
			String userName = "";
		    Map.Entry pairs = (Map.Entry)it.next();
		    user_id = (long) pairs.getKey();
		    if (idToUserMap.containsKey(user_id)) {
		    	userName = idToUserMap.get(user_id).getFirstName()+" "+idToUserMap.get(user_id).getLastName();
			}
		    cellUserName.setCellValue(userName);
			
		    int startRow = 1;
//		    try{
		   // for(Date month_date : monthDate){
		    	LOG.debug("month_Date: "+mapDate);
		    	LinkedHashMap<Date, Double> newMap = (LinkedHashMap<Date, Double>) pairs.getValue();
			    Iterator it1 = newMap.entrySet().iterator();
		    	
			    cellUserDistance  = rows.createCell(startRow);
			    LOG.debug("iscontain dates: "+newMap.containsKey(mapDate));
//			    LOG.debug("new Map.size "+newMap);
			    
			    if(newMap.containsKey(mapDate)){
			    	double distanceTravelled = newMap.get(mapDate);
			    	
			    	double inKm = 0.0;
			    	String unit;
			    	if(distanceTravelled > 1000){
//			    		System.out.println("Distnace Travelled "+distanceTravelled);
			    		inKm = distanceTravelled/1000.0;
//			    		System.out.println("Distnace inKm "+inKm);
			    		unit = " km";
			    	}else{
			    		inKm = distanceTravelled;
			    		unit = " m";
			    	}
			    	cellUserDistance.setCellValue(inKm+""+unit);	
			    	meters = meters +newMap.get(mapDate);
			    }else{
			    	cellUserDistance.setCellValue("N.A");
			    }
				startRow++;
				cellUserDistance.setCellStyle(styleAlternateRow);
		//    }
		   
//		    cellTotalDistance = rows.createCell(cellCount);
//		    double inKm = 0;
//	    	String unit;
//		    if(meters > 1000){
//		    	inKm = meters/1000.0;
//		    	unit = " km";
//		    }else{
//		    	inKm = meters;
//		    	unit = " m";
//		    }
//		    LOG.debug("total "+inKm+""+unit);
//		    cellTotalDistance.setCellValue(inKm+""+unit);
				cellUserName.setCellStyle(styleAlternateRow);
				cellUserDistance.setCellStyle(styleAlternateRow);
//				cellTotalDistance.setCellStyle(styleAlternateRow);
		    
//		}catch(Exception e ){
//			e.printStackTrace();
//		}
		}
//		}catch(Exception e ){
//			e.printStackTrace();
//		}
		
		// autosize all the columns
		for (int m = 0; m < 35; m++) {
			sheet.autoSizeColumn(m);
		}
		
		FileOperation fileOperation = new FileOperation();
		String file = fileOperation.doUpload(workbook, "Tracking_" + FileOperation.generateUniqueKey() + ".xlsx",
				companyId);
		//String file = fileOperation.doUpload(workbook, "Tracking.xlsx",
			//	companyId);
		workbook.close();

		return file;
	}
	
//	public String trackingGoogleExcel(List<UserLocation> userLocation, String timezone, Date startDate, long companyId)
//			throws ParseException, IOException {
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//		// create sheet in xlsx workbook
//		XSSFSheet sheet;
//		sheet =	workbook.createSheet("Distance_Tracking_"+startDate);
//
//		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		timeformat.setTimeZone(TimeZone.getTimeZone(timezone));
//
//		int rowNum = 0;
//
//		// Create header row
//		Row headerRow = sheet.createRow(rowNum);
//		headerRow.setRowStyle(styleHeader);
//
//		Cell headerCell1 = headerRow.createCell(0);
//		Cell headerCell2 = headerRow.createCell(1);
//		Cell headerCell3 = headerRow.createCell(2);
//		Cell headerCell4 = headerRow.createCell(3);
////		Cell headerCell5 = headerRow.createCell(4);
////		Cell headerCell6 = headerRow.createCell(5);
////		Cell headerCell7 = headerRow.createCell(6);
//
//		headerCell1.setCellValue("Name");
//		headerCell1.setCellStyle(styleHeader);
//
////		headerCell2.setCellValue("Time");
////		headerCell2.setCellStyle(styleHeader);
//
//		headerCell2.setCellValue("Location From");
//		headerCell2.setCellStyle(styleHeader);
//
////		headerCell4.setCellValue("Latitude");
////		headerCell4.setCellStyle(styleHeader);
////
////		headerCell5.setCellValue("Longitude");
////		headerCell5.setCellStyle(styleHeader);
//
//		headerCell3.setCellValue("Location To");
//		headerCell3.setCellStyle(styleHeader);
//
//		headerCell4.setCellValue("Distance");
//		headerCell4.setCellStyle(styleHeader);
//		
//		Row row;
//		Cell cellName;
//		Cell cellLocationFrom;
//		Cell cellLocationTo;
//		Cell cellDistance;
//
//		String previousAddress = "";
//		double defaultDistance;
//		long userID = 0;
//		// Read attendance details for each user
//		for (UserLocation location : userLocation) {
//			rowNum += 1;
//			// Create rows for user
//			row = sheet.createRow(rowNum);
//			defaultDistance = round(Long.parseLong(location.getDistance()),2);
//			if(userID != location.getUserid()){
//				defaultDistance = 0;
//			}
//
//			cellName 		 = row.createCell(0);
//			cellLocationFrom = row.createCell(1);
//			cellLocationTo   = row.createCell(2);
//			cellDistance     = row.createCell(3);
//
//			cellName.setCellValue(location.getFirstName() + " " + location.getLastName());
//			cellLocationFrom.setCellValue(previousAddress);
//			cellLocationTo.setCellValue(location.getLocation());
//			cellDistance.setCellValue(defaultDistance);
//			
//			userID = location.getUserid();
//			previousAddress = location.getLocation();
//		}
//		// autosize all the columns
//		for (int i = 0; i < 8; i++) {
//			sheet.autoSizeColumn(i);
//		}
//
//		FileOperation fileOperation = new FileOperation();
//		String file = fileOperation.doUpload(workbook, "Tracking_" + FileOperation.generateUniqueKey() + ".xlsx",
//				companyId);
//		//String file = fileOperation.doUpload(workbook, "Tracking.xlsx",
//			//	companyId);
//		workbook.close();
//
//		return file;
//	}
	
	/**
	 * Calculate hours and minute from the mili second
	 * 
	 * @param diff
	 * @return
	 */
	public String getHoursMin(long diff) {
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000);
		String totalWorkTime = diffHours + ":" + diffMinutes;
		return totalWorkTime;
	}

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
