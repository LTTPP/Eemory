package com.prairie.eevernote.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.prairie.eevernote.Constants;

import eu.medsea.mimeutil.MimeUtil;

public class FileUtil {

	public static String tempFileName() {
		// 2014-02-21T18-35-32
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.SimpleDateFormat2);
		return formatter.format(new Date());
	}

	public static String mimeType(File file) {
		if (MimeUtil.getMimeDetector(Constants.MimeDetector) == null) {
			MimeUtil.registerMimeDetector(Constants.MimeDetector);
		}
		return MimeUtil.getMimeTypes(file).toString();
	}

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

}
