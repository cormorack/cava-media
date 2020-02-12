package cavamedia;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 */
public class DateUtils {

	/**
	 * Adds a formatted date String
	 * @param filename
	 * @return
	 */
	public static String addDate(String filename) {

		Date today = new Date();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
		filename = filename + sdf.format(today);
		return filename;
	}

	/**
	 *
	 * @param timestamp
	 * @return
	 */
	public static java.util.Date convertFromTimeStamp(java.sql.Timestamp timestamp) {

		long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
		return new java.util.Date(milliseconds);
	}
	
	public static String stripHtml(String html) {

	    return html.replaceAll("\\<.*?>", "");
	}

	public static String cleanText(String text) {

    	String s = stripHtml(text);
    	s = s.replaceAll("\r\n|\n\r|\n|\r|\t|'|\"", "");
    	s = s.replaceAll("\\[(.*?)\\]","");

		return s;
	}

}
