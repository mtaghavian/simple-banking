package com.example.wallet.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class DateUtils {

	public static String formatTime(long t) {
		Date d = new Date(t);
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, Locale.getDefault());
		return formatter.format(d);
	}
}
