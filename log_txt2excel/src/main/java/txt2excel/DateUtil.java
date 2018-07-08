package txt2excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/**
	 * 三种日期格式
	 * @param str
	 * @return
	 */
	public static boolean judgeDate(String str) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd");
		try {
			sdf1.parse(str);
			return true;
		} catch (ParseException e) {
			try {
				sdf2.parse(str);
				return true;
			} catch (ParseException e1) {
				try {
					sdf3.parse(str);
					return true;
				} catch (ParseException e2) {
					return false;
				}
			}
		}
	}

	public static Date parseDate(String daystr) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd");
		try {
			return sdf1.parse(daystr);
		} catch (ParseException e) {
			try {
				return sdf2.parse(daystr);
			} catch (ParseException e1) {
				try {
					return sdf3.parse(daystr);
				} catch (ParseException e2) {
					return null;
				}
			}
		}
	}

}
