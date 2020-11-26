package com.example.words2picture;

public interface DecoderCallback {
    void onStartTextEncoding();
    void onCompleteTextEncoding(Steganography result);
}