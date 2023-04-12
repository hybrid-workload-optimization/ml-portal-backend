package kr.co.strato.global.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

public class EncryptUtil {
    private static final String SECRET_KEY = "STRTCLOUDPORTAL@";
    private static final String SALT = "STRATOSALT";
    private static final String algorithm = "PBKDF2WithHmacSHA256";
    private static final String transformation = "AES/CBC/PKCS5PADDING";

    public static String encrypt(String data) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(SECRET_KEY.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey secretKey = factory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] b = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.encodeBase64String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String decrypt(String data) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(SECRET_KEY.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            KeySpec keySpec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey secretKey = factory.generateSecret(keySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher ciper = Cipher.getInstance(transformation);
            ciper.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] b = ciper.doFinal(Base64.decodeBase64(data));
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static String decryptRSA(String cipherText) {
        //개인키 문자열
        String privateKeyString = readFile();
        //string -> bytes
        byte[] privateKeyBytes = java.util.Base64.getMimeDecoder().decode(privateKeyString.getBytes());

        //개인키 객체 생성
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            //암호화 객체 생성
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            //복호화
            byte[] decryptedBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(cipherText));

            //bytes -> string
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile() {
        String filePath = "key/private.pem"; // 파일 경로
        ClassPathResource resource = new ClassPathResource(filePath);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputStream = resource.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.startsWith("-----BEGIN") && !line.startsWith("-----END")) {
                    stringBuilder.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static String decryptAES(String ivString, String keyString, String ciphertext) {
        try {
            //string -> bytes
            byte[] ciphertextBytes = java.util.Base64.getDecoder().decode(ciphertext);
            byte[] key = java.util.Base64.getDecoder().decode(keyString);
            byte[] iv = Hex.decodeHex(ivString);

            //키 객체, IV 객체 생성
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            //암호화 객체 생성
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            //복호화
            byte[] plaintextBytes = cipher.doFinal(ciphertextBytes);
            //bytes -> string
            return new String(plaintextBytes);
        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
