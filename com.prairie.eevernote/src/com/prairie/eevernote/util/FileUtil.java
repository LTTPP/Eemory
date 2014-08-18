package com.prairie.eevernote.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.prairie.eevernote.Constants;

import eu.medsea.mimeutil.MimeUtil;

public class FileUtil implements Constants {

	public static String tempFileName() {
		// 2014-02-21T18-35-32
		SimpleDateFormat formatter = new SimpleDateFormat(SimpleDateFormat2);
		return formatter.format(new Date());
	}

	public static String concatNameOfFiles(List<File> files) {
		return concatNameOfFilesWithSeparator(files, PLUS);
	}

	public static String concatNameOfFilesWithSeparator(List<File> files, String separator) {
		String name = StringUtil.EMPTY;
		for (File f : files) {
			name += separator;
			name += f.getName();
		}
		return name;
	}

	public static String mimeType(File file) {
		if (MimeUtil.getMimeDetector(MimeDetector) == null) {
			MimeUtil.registerMimeDetector(MimeDetector);
		}
		return MimeUtil.getMimeTypes(file).toString();
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte hashByte : bytes) {
			int intVal = 0xff & hashByte;
			if (intVal < 0x10) {
				sb.append(String.valueOf(ZERO));
			}
			sb.append(Integer.toHexString(intVal));
		}
		return sb.toString();
	}

}
