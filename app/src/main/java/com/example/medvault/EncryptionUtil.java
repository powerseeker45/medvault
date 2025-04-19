package com.example.medvault;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionUtil {

    private static final String ALIAS = "MedVaultKey";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    public static void generateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if (!keyStore.containsAlias(ALIAS)) {
            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(keySpec);
            keyGenerator.generateKey();
        }
    }

    public static String encrypt(String input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        SecretKey key = ((SecretKey) keyStore.getKey(ALIAS, null));
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.encodeToString(combined, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedInput) throws Exception {
        byte[] combined = Base64.decode(encryptedInput, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        SecretKey key = ((SecretKey) keyStore.getKey(ALIAS, null));
        GCMParameterSpec spec = new GCMParameterSpec(128, combined, 0, 12);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decoded = cipher.doFinal(combined, 12, combined.length - 12);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
