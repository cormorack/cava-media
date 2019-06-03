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
