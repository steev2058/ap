package com.apps2you.albaraka.utils;

import android.util.Base64;

import com.apps2you.albaraka.utils.cryptography.PBKDF;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class SCEE { // SecureCompatibleEncryptionExamples

    private final static String ALGORITHM_NAME = "AES/GCM/NoPadding";
    private final static int ALGORITHM_NONCE_SIZE = 12;
    private final static int ALGORITHM_TAG_SIZE = 128;
    private final static int ALGORITHM_KEY_SIZE = 128;
    private final static int AES_KEY_SIZE = 16;
    private final static String PBKDF2_NAME = "PBKDF2WithHmacSHA256";
    private final static String PBKDF2_TYPE = "HmacSHA256";
    private final static int PBKDF2_SALT_SIZE = 16;
    private final static int PBKDF2_ITERATIONS = 32767;

    public static String encryptString(String plaintext, String password) throws GeneralSecurityException {
        // Generate a 128-bit salt using a CSPRNG.
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[PBKDF2_SALT_SIZE];
        rand.nextBytes(salt);

        byte[] secretKey = generateSecretKey(password, salt);

        // Encrypt and prepend salt.
        byte[] ciphertextAndNonce = encrypt(plaintext.getBytes(StandardCharsets.UTF_8), secretKey);
        byte[] ciphertextAndNonceAndSalt = new byte[salt.length + ciphertextAndNonce.length];
        System.arraycopy(salt, 0, ciphertextAndNonceAndSalt, 0, salt.length);
        System.arraycopy(ciphertextAndNonce, 0, ciphertextAndNonceAndSalt, salt.length, ciphertextAndNonce.length);

        // Return as base64 string.
        return Base64.encodeToString(ciphertextAndNonceAndSalt, android.util.Base64.DEFAULT);
    }

    public static String decryptString(String base64CiphertextAndNonceAndSalt, String password) throws GeneralSecurityException {
        // Decode the base64.
        byte[] ciphertextAndNonceAndSalt = Base64.decode(base64CiphertextAndNonceAndSalt, android.util.Base64.DEFAULT);

        // Retrieve the salt and ciphertextAndNonce.
        byte[] salt = new byte[PBKDF2_SALT_SIZE];
        byte[] ciphertextAndNonce = new byte[ciphertextAndNonceAndSalt.length - PBKDF2_SALT_SIZE];
        System.arraycopy(ciphertextAndNonceAndSalt, 0, salt, 0, salt.length);
        System.arraycopy(ciphertextAndNonceAndSalt, salt.length, ciphertextAndNonce, 0, ciphertextAndNonce.length);

        byte[] secretKey = generateSecretKey(password, salt);

        // Decrypt and return result.
        return new String(decrypt(ciphertextAndNonce, secretKey), StandardCharsets.UTF_8);
    }

    private static byte[] generateSecretKey(String password, byte[] salt) throws GeneralSecurityException {
        try {
            // Create an instance of PBKDF2 and derive the key.
            PBEKeySpec pwSpec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, ALGORITHM_KEY_SIZE);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_NAME);
            return keyFactory.generateSecret(pwSpec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            return PBKDF.pbkdf2(PBKDF2_TYPE, password.getBytes(StandardCharsets.UTF_8), salt, PBKDF2_ITERATIONS, AES_KEY_SIZE);
        }
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // Generate a 96-bit nonce using a CSPRNG.
        SecureRandom rand = new SecureRandom();
        byte[] nonce = new byte[ALGORITHM_NONCE_SIZE];
        rand.nextBytes(nonce);

        // Create the cipher instance and initialize.
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce));

        // Encrypt and prepend nonce.
        byte[] ciphertext = cipher.doFinal(plaintext);
        byte[] ciphertextAndNonce = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, ciphertextAndNonce, 0, nonce.length);
        System.arraycopy(ciphertext, 0, ciphertextAndNonce, nonce.length, ciphertext.length);

        return ciphertextAndNonce;
    }

    public static byte[] decrypt(byte[] ciphertextAndNonce, byte[] key) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        // Retrieve the nonce and ciphertext.
        byte[] nonce = new byte[ALGORITHM_NONCE_SIZE];
        byte[] ciphertext = new byte[ciphertextAndNonce.length - ALGORITHM_NONCE_SIZE];
        System.arraycopy(ciphertextAndNonce, 0, nonce, 0, nonce.length);
        System.arraycopy(ciphertextAndNonce, nonce.length, ciphertext, 0, ciphertext.length);

        // Create the cipher instance and initialize.
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(ALGORITHM_TAG_SIZE, nonce));

        // Decrypt and return result.
        return cipher.doFinal(ciphertext);
    }
}