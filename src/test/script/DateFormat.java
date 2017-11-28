package script;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weijlu on 2017/3/20.
 */
public class DateFormat {
	public static void main(String args[]) throws ParseException {
		/*String time = "2017-03-20T00:46:23Z";
		String date_time = "2017-03-20T01:00:37.000Z";
		try {
			parse(date_time, "yyyy-MM-dd'T'HH:mm:ss'Z'");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}*/
		String startDate = "07-27-2017";
		String endDate = "07-30-2017";
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		Date start = format.parse(startDate);
		Date end = format.parse(endDate);
		while (end.getTime()>=start.getTime()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.ENGLISH);
			System.out.println("当前时间：" + sdf.format(end));

			end = DateUtils.addDays(end, -1);

		}


	}

	private static void parse(String time, String stringPatten) throws ParseException {
		if (time.indexOf(".") >= 0) {
			stringPatten = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(stringPatten);
		sdf.parse(time);
	}
}
