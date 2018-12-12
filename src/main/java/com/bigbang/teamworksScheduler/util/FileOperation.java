package com.bigbang.teamworksScheduler.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.bigbang.teamworksScheduler.service.Properties;

public final class FileOperation {

	private static final Logger LOG = LogManager.getLogger(FileOperation.class);

	@Autowired
	private static ApplicationContext context;

	@Autowired
	private static Locale locale;

	/**
	 * @param object
	 * @param fileName
	 * @param companyID
	 * @return String
	 * @throws IOException
	 */
	public String doUpload(final Workbook object, String fileName, final long companyID) throws IOException {

		String path = (String) Properties.get("upload.scheduler.path") + companyID + "/"; //upload.path
//		String path = "C:/logs/TrackingReport/" + companyID + "/";

		File file = new File(path + fileName);
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);

		try {
			object.write(out);
			return path + fileName;
		} finally {
			out.close();
		}
	}

	
	/**
	 * @return uniqueKey Random Number
	 */
	public static String generateUniqueKey() {

		Random randomNum = new Random();
		String uniqueKey = String.valueOf(100000 + randomNum.nextInt(899999));
		return uniqueKey;
	}
}
