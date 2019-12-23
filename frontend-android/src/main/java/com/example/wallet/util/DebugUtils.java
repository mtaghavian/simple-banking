package com.example.wallet.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class DebugUtils {

	public static void debug(Context context, Exception ex, LinearLayout ll) {
		try {
			String msg = serializeException(ex);
			UiUtils.makeToast(context, msg, ll);
		} catch (Exception e) {
		}
	}

	public static String serializeException(Exception ex) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter writer = null;
			writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			ex.printStackTrace(writer);
			writer.close();
			return new String(os.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "Unable to serialize";
		}
	}
}
