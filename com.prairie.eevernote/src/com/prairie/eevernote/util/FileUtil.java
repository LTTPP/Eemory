package com.prairie.eevernote.util;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.medsea.mimeutil.MimeUtil;

public class FileUtil implements ConstantsUtil {

    public static String concatNameOfFiles(final List<File> files) {
        return concatNameOfFilesWithSeparator(files, PLUS);
    }

    public static String concatNameOfFilesWithSeparator(final List<File> files, final String separator) {
        String name = StringUtils.EMPTY;
        for (File f : files) {
            name += separator;
            name += f.getName();
        }
        return name;
    }

    public static String mimeType(final File file) {
        if (MimeUtil.getMimeDetector(MimeDetector) == null) {
            MimeUtil.registerMimeDetector(MimeDetector);
        }
        return MimeUtil.getMimeTypes(file).toString();
    }

    public static String bytesToHex(final byte[] bytes) {
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
