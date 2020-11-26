package com.example.words2picture;

import android.util.Log;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptDecrypt {

    public static String encryptMessage(String message, String secret_key) throws Exception {


        SecretKeySpec aesKey = new SecretKeySpec(secret_key.getBytes(), "AES");
        Cipher cipher;


        cipher = Cipher.getInstance("AES");


        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted;

        encrypted = cipher.doFinal(message.getBytes());

        Log.d("crypto", "Encrypted  in crypto (mine): " + Arrays.toString(encrypted) + "string: " + android.util.Base64.encodeToString(cipher.doFinal(message.getBytes()), 0));

        Log.d("crypto", "Encrypted  in crypto (theirs): " + Arrays.toString(cipher.doFinal(message.getBytes())) + "string : " + new String(encrypted));

        return android.util.Base64.encodeToString(cipher.doFinal(message.getBytes()), 0);
    }



    public static String decryptMessage(String encrypted_message, String secret_key) throws Exception {

        Log.d("Decrypt", "message: + " + encrypted_message);

        SecretKeySpec aesKey = new SecretKeySpec(secret_key.getBytes(), "AES");
        Cipher cipher;


        cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted;
        byte[] decoded;
        decoded = android.util.Base64.decode(encrypted_message.getBytes(), 0);
        decrypted = new String(cipher.doFinal(decoded));


        return decrypted;
    }

}