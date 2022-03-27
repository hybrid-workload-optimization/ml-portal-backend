package kr.co.strato.global.validation;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

@Component
public class TokenValidator {

	@Value("${service.keycloak.public.key}")
	private String publicKey;
	
	public static String alg = "AES/CBC/PKCS5Padding";
	
	public boolean validateToken(String token) {
		boolean result = false;
		try {
			
			Optional<RSAPublicKey> k = getParsePublicKey();
			
			try {
				Claims claims = Jwts.parser()
						.setSigningKey(k.get())
						.parseClaimsJws(token)
						.getBody();
				System.out.println(claims);
				result = true;
				return true;
			}catch (Exception e) {
				e.printStackTrace();
				result = false;
				return result;
			}
			
		}catch (ExpiredJwtException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	
	public String extractUserId(String token) {
		String userId = null;
		try {
			Optional<RSAPublicKey> k = getParsePublicKey();
			Claims claims = Jwts.parser()
							.setSigningKey(k.get())
							.parseClaimsJws(token)
							.getBody();
			userId = claims.get("email").toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	
	@SuppressWarnings("deprecation")
	public Optional<RSAPublicKey> getParsePublicKey() {
		try {
            byte[] decode = com.google.api.client.util.Base64.decodeBase64(publicKey);
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
            return Optional.of(pubKey);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            System.out.println("Exception block | Public key parsing error ");
            return Optional.empty();
        }
	}
	
	/**
	 * 클라이언트 인증 시 사용되는 해싱키 값을 만들어주는 함수
	 * 클라이언트에서 Timestamp를 파라미터로 받고, 요청정보 중 url, method 추출
	 * 생성된 key로 JWT 복호화 
	 * @param timestamp
	 * @param url
	 * @param method
	 * @return
	 */
	public static String generateKey(Long timestamp, String url, String method) {
		String keyStr = "";
		String message = new StringBuilder().append(url).append(method).append(timestamp).toString();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(message.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			keyStr = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			keyStr = "";
		}
		return keyStr;
	}
	
	public String decrypt(String cipherText, Long timestamp, String url, String method) throws Exception {
		String key = generateKey(timestamp, url, method);
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        String iv = key.substring(0, 16);
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }
	
    public String encrypt(String text, Long timestamp, String url, String method) throws Exception {
    	String key = generateKey(timestamp, url, method);
    	System.out.println("=== key : " + key);
    	String iv = key.substring(0, 16);
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }
}
