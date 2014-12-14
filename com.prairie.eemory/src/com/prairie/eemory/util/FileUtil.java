package com.prairie.eemory.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.prairie.eemory.Constants;

import eu.medsea.mimeutil.MimeUtil;

public class FileUtil {

    public static String concatNameOfFiles(final List<File> files) {
        return concatNameOfFilesWithSeparator(files, ConstantsUtil.PLUS);
    }

    public static String concatNameOfFilesWithSeparator(final List<File> files, final String separator) {
        String name = StringUtils.EMPTY;
        for (File f : files) {
            if (StringUtils.isNotBlank(name)) {
                name += separator;
            }
            name += f.getName();
        }
        return name;
    }

    public static String mimeType(final File file) {
        if (MimeUtil.getMimeDetector(Constants.MimeDetector) == null) {
            MimeUtil.registerMimeDetector(Constants.MimeDetector);
        }
        return MimeUtil.getMimeTypes(file).toString();
    }

    public static String bytesToHex(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : bytes) {
            int intVal = 0xff & hashByte;
            if (intVal < 0x10) {
                sb.append(String.valueOf(0));
            }
            sb.append(Integer.toHexString(intVal));
        }
        return sb.toString();
    }

    public static File toFile(final URL url) {
        File file = FileUtils.toFile(url);
        if (file == null) {
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                file = new File(url.getPath());
            }
        }
        return file;
    }

}
