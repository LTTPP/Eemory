package com.prairie.eevernote.client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.prairie.eevernote.Constants;

import eu.medsea.mimeutil.MimeUtil;

/**
 * Helper class
 */
public class Helper {

	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte hashByte : bytes) {
			int intVal = 0xff & hashByte;
			if (intVal < 0x10) {
				sb.append(Constants.ZERO_CHAR);
			}
			sb.append(Integer.toHexString(intVal));
		}
		return sb.toString();
	}

	public static String timestamp() {
		// 2014-02-21T18:35:32
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.SimpleDateFormat);
		return formatter.format(new Date());
	}

	public static String mimeType(File file) {
		if (MimeUtil.getMimeDetector(Constants.MimeDetector) == null) {
			MimeUtil.registerMimeDetector(Constants.MimeDetector);
		}
		return MimeUtil.getMimeTypes(file).toString();
	}

}
