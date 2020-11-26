package com.example.words2picture;

public interface EncoderCallback {
    void onStartTextEncoding();
    void onCompleteTextEncoding(Steganography result);
}