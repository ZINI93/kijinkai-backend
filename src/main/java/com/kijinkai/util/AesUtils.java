package com.kijinkai.util;


import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtils {


    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;  // AES 블록 사이즈

    /**
     * 암호화 (Encrypt)
     * @param plainText 평문 (예: 계좌번호)
     * @param secretKey 32바이트(256비트) 고정 비밀키 문자열
     * @return IV + 암호문이 합쳐진 Base64 문자열
     */
    public static String encrypt(String plainText, String secretKey){
        if (!StringUtils.hasText(plainText)) return null;
        validateKey(secretKey);

        try{

            // 1. 랜덤 IV 생성 -> 매번 다른 암호화문을 만듬
            byte[] iv = new byte[IV_SIZE];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);


            // 2. 키 생성
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 3. 암호화 수행
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 4. 결과 합치기
            // 나중에 복호화할 때 IV가 필요하므로 같이 저장해야 함
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            // 5. Base64로 인코딩하여 반환
            return Base64.getEncoder().encodeToString(combined);

        }catch (Exception e){
            throw new RuntimeException("AES Encryption failed", e);
        }
    }


    public static String decrypt(String cipherText, String secretKey){
        if (!StringUtils.hasText(cipherText)) return null;
        validateKey(secretKey);


        try {

            // base64 디코딩
            byte[] decode = Base64.getDecoder().decode(cipherText);


            // iv 분리
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(decode, 0, iv, 0, IV_SIZE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 실제 암호문 분리
            byte[] encrypted = new byte[decode.length - IV_SIZE];
            System.arraycopy(decode, IV_SIZE, encrypted, 0, encrypted.length);


            // 키 생성
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 복호화 수행
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);

        }catch (Exception e){
            throw new RuntimeException("AES Decryption failed", e);
        }
    }


    // 키 길이 검증
    private static void validateKey(String secretKey){
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length != 32){
            throw new IllegalArgumentException("Secret key must be exactly 32 byte for AES-256");
        }
    }

}
