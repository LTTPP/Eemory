package com.prairie.eemory.util;

import java.nio.charset.Charset;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptionUtil {

    private static BasicTextEncryptor textEncryptor;

    static {
        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(StringUtils.toEncodedString(Base64.decodeBase64("ZWVtb3J5"), Charset.forName(CharEncoding.UTF_8)));
    }

    public static String encrypt(final String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    public static String decrypt(final String encryptedText) {
        return textEncryptor.decrypt(encryptedText);
    }

}
