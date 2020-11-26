package com.example.words2picture;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.words2picture.Utility;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class EncDec {

    private static final String TAG = EncDec.class.getName();
    private static final String END_MESSAGE_COSTANT = "#!@";
    private static final String START_MESSAGE_COSTANT = "@!#";
    private static final int[] binary = {16, 8, 0};
    private static final byte[] andByte = {(byte) 0xC0, 0x30, 0x0C, 0x03};
    private static final int[] toShift = {6, 4, 2, 0};



    private static byte[] encodeMessage(int[] integer_pixel_array, int image_columns, int image_rows,
                                        MessageEncodingStatus messageEncodingStatus, ProgressHandler progressHandler) {
        int channels = 3;

        int shiftIndex = 4;

        byte[] result = new byte[image_rows * image_columns * channels];

        int resultIndex = 0;

        for (int row = 0; row < image_rows; row++) {

            for (int col = 0; col < image_columns; col++) {


                int element = row * image_columns + col;

                byte tmp;

                for (int channelIndex = 0; channelIndex < channels; channelIndex++) {

                    if (!messageEncodingStatus.isMessageEncoded()) {

                        tmp = (byte) ((((integer_pixel_array[element] >> binary[channelIndex]) & 0xFF) & 0xFC) | ((messageEncodingStatus.getByteArrayMessage()[messageEncodingStatus.getCurrentMessageIndex()] >> toShift[(shiftIndex++)
                                % toShift.length]) & 0x3));// 6

                        if (shiftIndex % toShift.length == 0) {

                            messageEncodingStatus.incrementMessageIndex();

                            if (progressHandler != null)
                                progressHandler.increment(1);

                        }

                        if (messageEncodingStatus.getCurrentMessageIndex() == messageEncodingStatus.getByteArrayMessage().length) {

                            messageEncodingStatus.setMessageEncoded();

                            if (progressHandler != null)
                                progressHandler.finished();

                        }
                    } else {

                        tmp = (byte) ((((integer_pixel_array[element] >> binary[channelIndex]) & 0xFF)));
                    }

                    result[resultIndex++] = tmp;

                }

            }

        }


        return result;

    }


    public static List<Bitmap> encodeMessage(List<Bitmap> splitted_images,
                                             String encrypted_message, ProgressHandler progressHandler) {


        List<Bitmap> result = new ArrayList<>(splitted_images.size());



        encrypted_message = encrypted_message + END_MESSAGE_COSTANT;
        encrypted_message = START_MESSAGE_COSTANT + encrypted_message;



        byte[] byte_encrypted_message = encrypted_message.getBytes(Charset.forName("ISO-8859-1"));


        MessageEncodingStatus message = new MessageEncodingStatus(byte_encrypted_message, encrypted_message);

        if (progressHandler != null) {
            progressHandler.setTotal(encrypted_message.getBytes(Charset.forName("ISO-8859-1")).length);
        }


        Log.i(TAG, "Message length " + byte_encrypted_message.length);

        for (Bitmap bitmap : splitted_images) {

            if (!message.isMessageEncoded()) {


                int width = bitmap.getWidth();
                int height = bitmap.getHeight();


                int[] oneD = new int[width * height];
                bitmap.getPixels(oneD, 0, width, 0, 0, width, height);


                int density = bitmap.getDensity();


                byte[] encodedImage = encodeMessage(oneD, width, height, message, progressHandler);


                int[] oneDMod = Utility.byteArrayToIntArray(encodedImage);


                Bitmap encoded_Bitmap = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888);
                encoded_Bitmap.setDensity(density);

                int masterIndex = 0;


                for (int j = 0; j < height; j++)
                    for (int i = 0; i < width; i++) {

                        encoded_Bitmap.setPixel(i, j, Color.argb(0xFF,
                                oneDMod[masterIndex] >> 16 & 0xFF,
                                oneDMod[masterIndex] >> 8 & 0xFF,
                                oneDMod[masterIndex++] & 0xFF));

                    }

                result.add(encoded_Bitmap);

            } else {

                result.add(bitmap.copy(bitmap.getConfig(), false));
            }
        }

        return result;
    }


    private static void decodeMessage(byte[] byte_pixel_array, int image_columns,
                                      int image_rows, MessageDecodingStatus messageDecodingStatus) {


        Vector<Byte> byte_encrypted_message = new Vector<>();

        int shiftIndex = 4;

        byte tmp = 0x00;


        for (byte aByte_pixel_array : byte_pixel_array) {


            tmp = (byte) (tmp | ((aByte_pixel_array << toShift[shiftIndex
                    % toShift.length]) & andByte[shiftIndex++ % toShift.length]));

            if (shiftIndex % toShift.length == 0) {
                byte_encrypted_message.addElement(tmp);

                byte[] nonso = {byte_encrypted_message.elementAt(byte_encrypted_message.size() - 1)};
                String str = new String(nonso, Charset.forName("ISO-8859-1"));

                if (messageDecodingStatus.getMessage().endsWith(END_MESSAGE_COSTANT)) {

                    Log.i("TEST", "Decoding ended");


                    byte[] temp = new byte[byte_encrypted_message.size()];

                    for (int index = 0; index < temp.length; index++)
                        temp[index] = byte_encrypted_message.get(index);


                    String stra = new String(temp, Charset.forName("ISO-8859-1"));


                    messageDecodingStatus.setMessage(stra.substring(0, stra.length() - 1));


                    messageDecodingStatus.setEnded();

                    break;
                } else {

                    messageDecodingStatus.setMessage(messageDecodingStatus.getMessage() + str);

                    if (messageDecodingStatus.getMessage().length() == START_MESSAGE_COSTANT.length()
                            && !START_MESSAGE_COSTANT.equals(messageDecodingStatus.getMessage())) {

                        messageDecodingStatus.setMessage("");
                        messageDecodingStatus.setEnded();

                        break;
                    }
                }

                tmp = 0x00;
            }

        }

        if (!Utility.isStringEmpty(messageDecodingStatus.getMessage()))


            try {
                messageDecodingStatus.setMessage(messageDecodingStatus.getMessage().substring(START_MESSAGE_COSTANT.length(), messageDecodingStatus.getMessage()
                        .length()
                        - END_MESSAGE_COSTANT.length()));
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    public static String decodeMessage(List<Bitmap> encodedImages) {


        MessageDecodingStatus messageDecodingStatus = new MessageDecodingStatus();

        for (Bitmap bit : encodedImages) {
            int[] pixels = new int[bit.getWidth() * bit.getHeight()];

            bit.getPixels(pixels, 0, bit.getWidth(), 0, 0, bit.getWidth(),
                    bit.getHeight());

            byte[] b;

            b = Utility.convertArray(pixels);

            decodeMessage(b, bit.getWidth(), bit.getHeight(), messageDecodingStatus);

            if (messageDecodingStatus.isEnded())
                break;
        }

        return messageDecodingStatus.getMessage();
    }

    public interface ProgressHandler {

        void setTotal(int tot);

        void increment(int inc);

        void finished();
    }

    private static class MessageDecodingStatus {

        private String message;
        private boolean ended;

        MessageDecodingStatus() {
            message = "";
            ended = false;
        }

        boolean isEnded() {
            return ended;
        }

        void setEnded() {
            this.ended = true;
        }

        String getMessage() {
            return message;
        }

        void setMessage(String message) {
            this.message = message;
        }


    }

    private static class MessageEncodingStatus {
        private boolean messageEncoded;
        private int currentMessageIndex;
        private byte[] byteArrayMessage;
        private String message;

        MessageEncodingStatus(byte[] byteArrayMessage, String message) {
            this.messageEncoded = false;
            this.currentMessageIndex = 0;
            this.byteArrayMessage = byteArrayMessage;
            this.message = message;
        }

        void incrementMessageIndex() {
            currentMessageIndex++;
        }

        boolean isMessageEncoded() {
            return messageEncoded;
        }

        void setMessageEncoded() {
            this.messageEncoded = true;
        }

        int getCurrentMessageIndex() {
            return currentMessageIndex;
        }

        byte[] getByteArrayMessage() {
            return byteArrayMessage;
        }


    }

}