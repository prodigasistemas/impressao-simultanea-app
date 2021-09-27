package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	public static String getData(String format) {
		DateFormat f = new SimpleDateFormat(format, new Locale("pt", "BR"));
		return f.format(new Date());
	}

	public static String getData() {
		return getData("dd/MM/yyyy - HH:mm:ss");
	}
}
