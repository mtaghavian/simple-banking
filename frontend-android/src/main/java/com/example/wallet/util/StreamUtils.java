package com.example.wallet.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class StreamUtils {

	public static void copy(InputStream is, OutputStream os,
			boolean closeInput, boolean closeOutput) throws IOException {
		byte b[] = new byte[10000];
		while (true) {
			int r = is.read(b);
			if (r < 0) {
				break;
			}
			os.write(b, 0, r);
		}
		if (closeInput) {
			is.close();
		}
		if (closeOutput) {
			os.flush();
			os.close();
		}
	}

}
