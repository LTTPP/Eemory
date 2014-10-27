package com.prairie.eevernote.util;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptionUtil {

    private static BasicTextEncryptor textEncryptor;

    static {
        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("c21f9dd4d2c38ecc08480fa561b5e97c");
    }

    public static String encrypt(final String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    public static String decrypt(final String encryptedText) {
        return textEncryptor.decrypt(encryptedText);
    }

}
