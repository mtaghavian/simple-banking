package wallet.util;

import org.springframework.stereotype.Component;
import java.io.*;
import java.security.MessageDigest;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */
@Component
public class ByteUtils {

    public static String hash(String s) {
        try {
            return toHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")), "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String toHex(byte b[], String delimeter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String h = String.format("%h", b[i] & 0xff);
            if (h.length() == 1) {
                h = "0" + h;
            }
            sb.append((i == 0) ? h : (delimeter + h));
        }
        return sb.toString();
    }

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

    public static boolean hasIllegalCharacters(String s) {
        boolean ok = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!(Character.isLetterOrDigit(ch)
                    || (ch >= 32 && ch <= 64))) {
                ok = true;
                break;
            }
        }
        return ok;
    }

    public static String checkLengthAndIllegalCharacters(String name, String str, int lb, int ub) {
        if (str.length() < lb || str.length() > ub) {
            return name + " must be between " + lb + " and " + ub + " characters";
        } else {
            if (hasIllegalCharacters(str)) {
                return name + " has illegal characters";
            } else {
                return null;
            }
        }
    }
}
