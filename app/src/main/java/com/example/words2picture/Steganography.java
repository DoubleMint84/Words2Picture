package com.example.words2picture;

import android.graphics.Bitmap;
import android.util.Log;

public class Steganography {
    private static final String TAG = Steganography.class.getName();
    private String message;
    private String password;
    private String encryptedMessage;
    private Bitmap image;
    private Bitmap encoded_image;
    private byte[] encrypted_zip;
    private Boolean encoded;
    private Boolean decoded;
    private Boolean secretKeyWrong;

    public Steganography() {
        this.encoded = false;
        this.decoded = false;
        this.secretKeyWrong = true;
        this.message = "";
        this.password = "";
        this.encryptedMessage = "";
        this.image = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
        this.encoded_image = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
        this.encrypted_zip = new byte[0];
    }

    public Steganography(String message, String password, Bitmap image) {
        this.message = message;
        this.password = convertKeyTo128bit(password);
        this.image = image;
        this.encrypted_zip = message.getBytes();
        this.encryptedMessage = encryptMessage(message, this.password);
        this.encoded = false;
        this.decoded = false;
        this.secretKeyWrong = true;
        this.encoded_image = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
    }

    public Steganography(String password, Bitmap image) {
        this.password = convertKeyTo128bit(password);
        this.image = image;
        this.encoded = false;
        this.decoded = false;
        this.secretKeyWrong = true;
        this.message = "";
        this.encryptedMessage = "";
        this.encoded_image = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
        this.encrypted_zip = new byte[0];
    }

    private static String encryptMessage(String message, String secret_key) {
        Log.d(TAG, "Message : " + message);
        String encrypted_message = "";
        if (message != null) {
            if (!Utility.isStringEmpty(secret_key)) {
                try {
                    encrypted_message = CryptDecrypt.encryptMessage(message, secret_key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                encrypted_message = message;
            }
        }
        Log.d(TAG, "Encrypted_message : " + encrypted_message);
        return encrypted_message;
    }

    public static String decryptMessage(String message, String secret_key) {
        String decrypted_message = "";
        if (message != null) {
            if (!Utility.isStringEmpty(secret_key)) {
                try {
                    decrypted_message = CryptDecrypt.decryptMessage(message, secret_key);
                } catch (Exception e) {
                    Log.d(TAG, "Error : " + e.getMessage() + " , may be due to wrong key.");
                }
            } else {
                decrypted_message = message;
            }
        }

        return decrypted_message;
    }

    private static String convertKeyTo128bit(String secret_key) {
        StringBuilder result = new StringBuilder(secret_key);
        if (secret_key.length() <= 16) {
            for (int i = 0; i < (16 - secret_key.length()); i++) {
                result.append("#");
            }
        } else {
            result = new StringBuilder(result.substring(0, 15));
        }
        return result.toString();
    }

    public Bitmap getEncoded_image() {
        return encoded_image;
    }

    public void setEncoded_image(Bitmap encoded_image) {
        this.encoded_image = encoded_image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public Bitmap getImage() {
        return image;
    }

    public Boolean isEncoded() {
        return encoded;
    }

    public void setEncoded(Boolean encoded) {
        this.encoded = encoded;
    }

    public Boolean isDecoded() {
        return decoded;
    }

    public void setDecoded(Boolean decoded) {
        this.decoded = decoded;
    }

    public Boolean isSecretKeyWrong() {
        return secretKeyWrong;
    }

    public void setSecretKeyWrong(Boolean secretKeyWrong) {
        this.secretKeyWrong = secretKeyWrong;
    }

}