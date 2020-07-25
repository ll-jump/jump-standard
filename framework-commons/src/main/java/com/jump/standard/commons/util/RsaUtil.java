package com.jump.standard.commons.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * rsa加密工具类
 *
 * @author LiLin
 * @version 1.0.0
 * @date 2020/07/06
 */
public class RsaUtil {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    private static final int KEYSIZE = 1024;

    /**
     * 签名
     */
    public static String sign(String content, String key) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            if (null == key || "".equals(key)) {
                return null;
            }
            PrivateKey pk = getPrivateKey(key);
            signature.initSign(pk);
            signature.update(content.getBytes());
            return new String(Base64.encodeBase64(signature.sign()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验签
     */
    public static boolean checkSign(String content, String sign, String key) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            if (null == key || "".equals(key)) {
                return false;
            }
            PublicKey pk = getPublicKey(key);
            signature.initVerify(pk);
            signature.update(content.getBytes());
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通过私钥解密
     *
     * @return 返回 解密后的数据
     */
    public static byte[] decryptByPrivateKey(String content, PrivateKey pk) {

        try {
            Cipher ch = Cipher.getInstance(ALGORITHM);
            ch.init(Cipher.DECRYPT_MODE, pk);
            return ch.doFinal(Base64.decodeBase64(content));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 通过公钥加密
     *
     * @return,加密数据，未进行base64进行加密
     */
    public static byte[] encryptByPublicKey(String content, PublicKey pk) {

        try {
            Cipher ch = Cipher.getInstance(ALGORITHM);
            ch.init(Cipher.ENCRYPT_MODE, pk);
            return ch.doFinal(content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 通过公钥解密
     *
     * @return 返回 解密后的数据
     */
    public static byte[] decryptByPublicKey(String content, PublicKey pk) {

        try {
            Cipher ch = Cipher.getInstance(ALGORITHM);
            ch.init(Cipher.DECRYPT_MODE, pk);
            return ch.doFinal(Base64.decodeBase64(content));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 通过私钥加密
     *
     * @return,加密数据，未进行base64进行加密
     */
    public static byte[] encryptByPrivateKey(String content, PrivateKey pk) {

        try {
            Cipher ch = Cipher.getInstance(ALGORITHM);
            ch.init(Cipher.ENCRYPT_MODE, pk);
            return ch.doFinal(content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 解密数据，接收端接收到数据直接解密
     */
    public static String decryptByPrivateKey(String content, String key) {
        if (null == key || "".equals(key)) {
            return null;
        }
        PrivateKey pk = getPrivateKey(key);
        byte[] data = decryptByPrivateKey(content, pk);
        String res = null;
        try {
            res = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 对内容进行加密
     */
    public static String encryptByPublicKey(String content, String key) {
        PublicKey pk = getPublicKey(key);
        byte[] data = encryptByPublicKey(content, pk);
        String res = null;
        try {
            res = Base64.encodeBase64String(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;

    }

    /**
     * 解密数据，接收端接收到数据直接解密
     */
    public static String decrypt(String content, String key) {
        if (null == key || "".equals(key)) {
            return null;
        }
        PublicKey pk = getPublicKey(key);
        byte[] data = decryptByPublicKey(content, pk);
        String res = null;
        try {
            res = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 对内容进行加密
     */
    public static String encrypt(String content, String key) {
        PrivateKey pk = getPrivateKey(key);
        byte[] data = encryptByPrivateKey(content, pk);
        String res = null;
        try {
            res = Base64.encodeBase64String(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;

    }

    /**
     * 得到私钥对象
     *
     * @param privateKey 密钥字符串（经过base64编码的秘钥字节）
     */
    public static PrivateKey getPrivateKey(String privateKey) {
        try {
            byte[] keyBytes;

            keyBytes = Base64.decodeBase64(privateKey);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privatekey = keyFactory.generatePrivate(keySpec);

            return privatekey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取公钥对象
     *
     * @param publicKey 密钥字符串（经过base64编码秘钥字节）
     */
    public static PublicKey getPublicKey(String publicKey) {
        try {
            byte[] keyBytes;
            keyBytes = Base64.decodeBase64(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publickey = keyFactory.generatePublic(keySpec);
            return publickey;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;

    }

    private static void genKeyPair() throws NoSuchAlgorithmException {

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom secureRandom = new SecureRandom();

        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        keyPairGenerator.initialize(KEYSIZE, secureRandom);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();

        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        String publicKeyBase64 = Base64.encodeBase64String(publicKeyBytes);
        String privateKeyBase64 = Base64.encodeBase64String(privateKeyBytes);

        System.out.println("publicKeyBase64:" + publicKeyBase64);
        System.out.println("privateKeyBase64:" + privateKeyBase64);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        genKeyPair();
        String privateKey
                = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANyhwAD+Tne5CMWrFh+851GCO7yo9hPOJ9/mSTCgZ/xMVOaqNHv8ZWW6MziqA16VdUkaJ0WeHt/l93JE7NU82fx/1EuHefGcJH8DNfu3BruuG4BoymV5lUqveniCjxE2V/QXeDKLVD3iysIA17uLR0X8IFYEypKRGmOykokzEjV5AgMBAAECgYBzigEShBpzd+KwITgkxrgcZycBVToIhR08k0wgut+5r/+GC/wQMcynySqpsd6x2XLiJWALhRKGDdfXb2DXClbceniOY2w8ZcByMd7utYyMiYQPEEYO8CNohfhE/C3Wn1rK1VMyXKPFOdTSo9Ib02UwMiNccax/QcIfAJhNcnzHQQJBAPxK0fjIj4cBA69PsNWot8cnf0RJI0Vg/wKGxORPlmrndVytVAwioveGlkU4bf1gWUao5EkAXuYIiHbOUrV8jz8CQQDf39DzGqYYgP2jQdPbjfowlgOd18qSWhBozHlX7mwEPPr4JtQdZw8P1LoMvjdvGlYoQI4ZxNMCioLG3vYU9cVHAkEAptSvHm05g9Om5SG0VMT5qeczYCtg+HrYhoop3rPGSrD9yagQQOoSi83ixqk3CmrQ/kmmk6N8HW8dFjr/NGXGrwJBAIeKPDlTAwiG4Aj9i8S7eqP7zwi7YzukJ5crZLPl96PBP7kJ2RZMFQRiqJmaeHldmpgYLXBQh+hb771zs80Q13UCQCRnqDiDqgWSCkmZhFoxYeIbEDHKwiR2oSKPJd5gyBB48PP6IMe6kI5zbyd0odhlFCuhuAIz6QGmejN6U/yDho8=";
        String publicKey
                = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcocAA/k53uQjFqxYfvOdRgju8qPYTziff5kkwoGf8TFTmqjR7/GVlujM4qgNelXVJGidFnh7f5fdyROzVPNn8f9RLh3nxnCR/AzX7twa7rhuAaMpleZVKr3p4go8RNlf0F3gyi1Q94srCANe7i0dF/CBWBMqSkRpjspKJMxI1eQIDAQAB";

        String encryptStr = encrypt("7c401931a8e6ae8425ab93e0ea554f49a53109e04a74cf345207f11758a773d3", privateKey);
        System.out.println("encryptStr:" + encryptStr);
        String decryptStr = decrypt(encryptStr, publicKey);
        System.out.println("decryptStr:" + decryptStr);

//        String encryptStr2 = encryptByPublicKey("1234", publicKey);
//        System.out.println("encryptStr2:" + encryptStr2);
//        String decryptStr2 = decryptByPrivateKey(encryptStr2, privateKey);
//        System.out.println("decryptStr2:" + decryptStr2);

//        String sign = sign("wx/B6c3eR/HwRCM66gFO5QgiboPP+STTngEqz3jcWR0HPQZbYtBXUBCpTwfrJ39qd4nxIjwsEDxEeFj8rSH+rNsdnmsMiQCA/0GL1wckq/Rkk/wCvNMXADmYBsXNYhG5q4BpQtQqQoGXFMikFCrWD2aI8ASUZWjpsJ6iSgU3jQ+sQCULYiic8+fmWnWGp5Wc", privateKey);
//        System.out.println("sign:" + sign);
//
//        boolean checkSign = checkSign("wx/B6c3eR/HwRCM66gFO5QgiboPP+STTngEqz3jcWR0HPQZbYtBXUBCpTwfrJ39qd4nxIjwsEDxEeFj8rSH+rNsdnmsMiQCA/0GL1wckq/Rkk/wCvNMXADmYBsXNYhG5q4BpQtQqQoGXFMikFCrWD2aI8ASUZWjpsJ6iSgU3jQ+sQCULYiic8+fmWnWGp5Wc", "U1JpTBbcSkPz4VmMl0r0XIaGTnfPpu936Jfg4rTUKeNeyvLMOZ/CUGJMw+45/hrfQJxaaw/huayHuBmnNuSV/llrioqSYT2E8EuMzL5E6DtHOSVPlsaZHzmALp00Dlvzw3XZ407/pbpWJHfW5WYzpUSJcXS/w5Xy5d6nvyiADqM=", publicKey);
//        System.out.println("checkSign:" + checkSign);
    }

}
