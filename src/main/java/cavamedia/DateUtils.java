package cavamedia;

import java.util.Date;
import java.util.Calendar;
import java.util.regex.*;
import java.text.SimpleDateFormat;

/**
 *
 */
public class DateUtils {
    
	private static SimpleDateFormat dateFormat = new SimpleDateFormat();

	/**
	 *
	 * @param month
	 * @param day
	 * @param year
	 * @return
	 */
    public static Date newDate(int month, int day, int year){
        Calendar inst = Calendar.getInstance();
        inst.clear();
        inst.set(year, month, day);
        return inst.getTime();
    }

    public static Calendar createCalendarMonth(int month, int day, int year) {
        Calendar inst = Calendar.getInstance();
        inst.clear();
        inst.set(year, month, day);
        return inst;
    }

	/**
	* Formats a Date
    * @param format: A String
    * @return: formatted date as a String
    */
	public static String getDate(String format) {

		Date date = Calendar.getInstance().getTime();
   		String formattedDate = (new SimpleDateFormat(format)).format(date);
   		return formattedDate;
    }

	public static String getDateAsString(Date date, String format) {

   		String formattedDate = (new SimpleDateFormat(format)).format(date);
   		return formattedDate;
    }

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
	 * @param date
	 * @param formatString
	 * @return
	 */
    public static Date formatDate(Date date, String formatString) {
          
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        formatter.format(date);
        return date;
    }

	public static java.util.Date convertFromTimeStamp(java.sql.Timestamp timestamp) {

	    long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
	    return new java.util.Date(milliseconds);
	}
	
	public static String stripHtml(String html) {

	    return html.replaceAll("\\<.*?>", "");
	}
	
	public boolean isInteger(String input) {

	    try{
	        Integer.parseInt( input );
	        return true;
	    } catch( Exception e) {
	        return false;
	    }
	}
}
