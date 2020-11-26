package com.example.words2picture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    private static final int SQUARE_BLOCK_SIZE = 512;
    private final static String TAG = Utility.class.getName();
    public static List<Bitmap> splitImage(Bitmap bitmap) {
        int chunkHeight, chunkWidth;
        ArrayList<Bitmap> chunkedImages = new ArrayList<>();
        int rows = bitmap.getHeight() / SQUARE_BLOCK_SIZE;
        int cols = bitmap.getWidth() / SQUARE_BLOCK_SIZE;
        int chunk_height_mod = bitmap.getHeight() % SQUARE_BLOCK_SIZE;
        int chunk_width_mod = bitmap.getWidth() % SQUARE_BLOCK_SIZE;
        if (chunk_height_mod > 0)
            rows++;
        if (chunk_width_mod > 0)
            cols++;
        int y_coordinate = 0;
        for (int x = 0; x < rows; x++) {

            int x_coordinate = 0;

            for (int y = 0; y < cols; y++) {

                chunkHeight = SQUARE_BLOCK_SIZE;
                chunkWidth = SQUARE_BLOCK_SIZE;

                if (y == cols - 1 && chunk_width_mod > 0)
                    chunkWidth = chunk_width_mod;

                if (x == rows - 1 && chunk_height_mod > 0)
                    chunkHeight = chunk_height_mod;

                chunkedImages.add(Bitmap.createBitmap(bitmap, x_coordinate, y_coordinate, chunkWidth, chunkHeight));
                x_coordinate += SQUARE_BLOCK_SIZE;
            }
            y_coordinate += SQUARE_BLOCK_SIZE;
        }
        return chunkedImages;
    }

    public static Bitmap mergeImage(List<Bitmap> images, int original_height, int original_width) {
        int rows = original_height / SQUARE_BLOCK_SIZE;
        int cols = original_width / SQUARE_BLOCK_SIZE;

        int chunk_height_mod = original_height % SQUARE_BLOCK_SIZE;
        int chunk_width_mod = original_width % SQUARE_BLOCK_SIZE;

        if (chunk_height_mod > 0)
            rows++;
        if (chunk_width_mod > 0)
            cols++;
        Log.d(TAG, "Width: " + original_width + " Height: " + original_height);
        Bitmap bitmap = Bitmap.createBitmap(original_width, original_height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        for (int irows = 0; irows < rows; irows++) {
            for (int icols = 0; icols < cols; icols++) {
                canvas.drawBitmap(images.get(count), (SQUARE_BLOCK_SIZE * icols), (SQUARE_BLOCK_SIZE * irows), new Paint());
                count++;
            }
        }
        return bitmap;
    }

    public static int[] byteArrayToIntArray(byte[] b) {
        Log.v("Size byte array", b.length + "");
        int size = b.length / 3;
        Log.v("Size Int array", size + "");
        System.runFinalization();
        System.gc();
        Log.v("FreeMemory", Runtime.getRuntime().freeMemory() + "");
        int[] result = new int[size];
        int offset = 0;
        int index = 0;
        while (offset < b.length) {
            result[index++] = byteArrayToInt(b, offset);
            offset = offset + 3;
        }
        return result;
    }

    private static int byteArrayToInt(byte[] b, int offset) {
        int value = 0x00000000;
        for (int i = 0; i < 3; i++) {
            int shift = (3 - 1 - i) * 8;
            value |= (b[i + offset] & 0x000000FF) << shift;
        }
        value = value & 0x00FFFFFF;
        return value;
    }

    public static byte[] convertArray(int[] array) {
        byte[] newarray = new byte[array.length * 3];
        for (int i = 0; i < array.length; i++) {
            newarray[i * 3] = (byte) ((array[i] >> 16) & 0xFF);
            newarray[i * 3 + 1] = (byte) ((array[i] >> 8) & 0xFF);
            newarray[i * 3 + 2] = (byte) ((array[i]) & 0xFF);

        }
        return newarray;
    }

    public static boolean isStringEmpty(String str) {
        boolean result = true;
        if (str == null) ;
        else {
            str = str.trim();
            if (str.length() > 0 && !str.equals("undefined"))
                result = false;
        }
        return result;
    }
}