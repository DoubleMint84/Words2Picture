package com.example.words2picture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class Encoder extends AsyncTask<Steganography, Integer, Steganography> {
    private static final String TAG = Encoder.class.getName();
    private final Steganography result;
    private final EncoderCallback callbackInterface;
    private int maximumProgress;
    private final ProgressDialog progressDialog;

    public Encoder(Activity activity, EncoderCallback callbackInterface) {
        super();
        this.progressDialog = new ProgressDialog(activity);
        this.callbackInterface = callbackInterface;

        this.result = new Steganography();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog != null) {
            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.setTitle("Encoding Message");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Steganography stegnography) {
        super.onPostExecute(stegnography);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        callbackInterface.onCompleteTextEncoding(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.incrementProgressBy(values[0]);
        }
    }

    @Override
    protected Steganography doInBackground(Steganography... steganographies) {
        maximumProgress = 0;
        if (steganographies.length > 0) {
            Steganography steganography = steganographies[0];
            Bitmap bitmap = steganography.getImage();
            int originalHeight = bitmap.getHeight();
            int originalWidth = bitmap.getWidth();
            List<Bitmap> src_list = Utility.splitImage(bitmap);
            List<Bitmap> encoded_list = EncDec.encodeMessage(src_list, steganography.getEncryptedMessage(), new EncDec.ProgressHandler() {
                @Override
                public void setTotal(int total) {
                    maximumProgress = total;
                    progressDialog.setMax(maximumProgress);
                    Log.d(TAG, "Total Length : " + total);
                }
                @Override
                public void increment(int inc) {
                    publishProgress(inc);
                }
                @Override
                public void finished() {
                    Log.d(TAG, "Message Encoding has been ended");
                    progressDialog.setIndeterminate(true);
                }
            });
            for (Bitmap trash : src_list)
                trash.recycle();
            System.gc();
            Bitmap srcEncoded = Utility.mergeImage(encoded_list, originalHeight, originalWidth);
            result.setEncoded_image(srcEncoded);
            result.setEncoded(true);
        }

        return result;
    }
}