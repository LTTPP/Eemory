package com.prairie.eemory.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.evernote.auth.EvernoteService;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.prairie.eemory.Constants;
import com.prairie.eemory.client.metadata.EvernoteBrand;

public class EvernoteUtil {

    public static Resource createResource(final File file, final String mimeType) throws NoSuchAlgorithmException, IOException {
        Resource resource = new Resource();
        resource.setData(readFileAsData(file));
        resource.setMime(mimeType);
        ResourceAttributes attributes = new ResourceAttributes();
        attributes.setFileName(file.getName());
        resource.setAttributes(attributes);
        return resource;
    }

    private static Data readFileAsData(final File file) throws IOException, NoSuchAlgorithmException {
        // Read the full binary contents of the file
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] block = new byte[10240];
        int len;
        while ((len = in.read(block)) >= 0) {
            byteOut.write(block, 0, len);
        }
        in.close();
        byte[] body = byteOut.toByteArray();

        // Create a new Data object to contain the file contents
        Data data = new Data();
        data.setSize(body.length);
        data.setBodyHash(MessageDigest.getInstance(ConstantsUtil.MD5).digest(body));
        data.setBody(body);

        return data;
    }

    public static EvernoteService evernoteService() {
        return brand().service();
    }

    public static EvernoteBrand brand() {
        String runOnSandbox = System.getProperty(Constants.PLUGIN_RUN_ON_SANDBOX);
        if (BooleanUtils.toBoolean(runOnSandbox)) {
            return EvernoteBrand.EVERNOTE_SANDBOX;
        } else {
            String brandValue = IDialogSettingsUtil.get(Constants.PLUGIN_SETTINGS_KEY_BRAND);
            if (StringUtils.isBlank(brandValue)) {
                return bootstrap();
            }
            EvernoteBrand brand = EvernoteBrand.valueOf(brandValue);
            if (brand == null) {
                return bootstrap();
            }
            return brand;
        }
    }

    private static EvernoteBrand bootstrap() {
        if (LocaleUtil.isChina()) {
            return EvernoteBrand.EVERNOTE_YINXIANG;
        } else {
            return EvernoteBrand.EVERNOTE_INTERNATIONAL;
        }
    }

}
