package com.jump.standard.commons.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 3des工具类
 *
 * @author LiLin
 * @version 1.0.0
 * @date 2020/06/29
 */
public class TrippleDesUtil {

    private static final String ALGORITHM = "DESede/CBC/PKCS5Padding";

    /**
     * 加密
     */
    public static String encrypt(String unencrypted, String key, String iv) {
        byte[] encrypted = null;
        try {
            Cipher cipher = makeCipher(Cipher.ENCRYPT_MODE, key.getBytes(StandardCharsets.UTF_8),
                    iv.getBytes(StandardCharsets.UTF_8));
            encrypted = cipher.doFinal(unencrypted.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return Base64.encode(encrypted);
    }

    /**
     * 解密
     */
    public static String decrypt(String encrypted, String key, String iv) {
        byte[] decrypted = null;
        try {
            Cipher cipher = makeCipher(Cipher.DECRYPT_MODE, key.getBytes(StandardCharsets.UTF_8),
                    iv.getBytes(StandardCharsets.UTF_8));
            decrypted = cipher.doFinal(Base64.decode(encrypted));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static Cipher makeCipher(int enc, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            if (iv != null) {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                cipher.init(enc, initKey(key), ivParameterSpec);
            } else {
                cipher.init(enc, initKey(key));
            }
            return cipher;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static SecretKey initKey(byte[] key)
            throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException {
        byte[] keyValue = new byte[24]; // final 3DES key
        if (key.length == 16) {
            // Create the third key from the first 8 bytes
            System.arraycopy(key, 0, keyValue, 0, 16);
            System.arraycopy(key, 0, keyValue, 16, 8);
        } else if (key.length != 24) {
            throw new IllegalArgumentException("A TripleDES key should be 24 bytes long");
        } else {
            keyValue = key;
        }
        KeySpec ks = new DESedeKeySpec(key);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
        return skf.generateSecret(ks);
    }

    public static void main(String[] args) {
        String content = "test";
        String salt = StringRandomUtil.getStringRandom(8);
        String desKey = StringRandomUtil.getStringRandom(24);
        System.out.println(encrypt(content, desKey, salt));
        System.out.println(decrypt(encrypt(content, desKey, salt), desKey, salt));
    }

}
