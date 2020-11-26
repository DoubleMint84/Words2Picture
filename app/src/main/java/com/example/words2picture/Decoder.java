package com.example.words2picture;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.List;

public class Decoder extends AsyncTask<Steganography, Void, Steganography> {
    private final Steganography result;
    private final DecoderCallback textDecodingCallback;
    private ProgressDialog progressDialog;

    public Decoder(Activity activity, DecoderCallback textDecodingCallback) {
        super();
        this.progressDialog = new ProgressDialog(activity);
        this.textDecodingCallback = textDecodingCallback;
        this.result = new Steganography();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog != null) {
            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.setTitle("Decoding Message");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Steganography steganography) {
        super.onPostExecute(steganography);
        if (progressDialog != null)
            progressDialog.dismiss();
        textDecodingCallback.onCompleteTextEncoding(result);
    }

    @Override
    protected Steganography doInBackground(Steganography... steganographies) {
        if (steganographies.length > 0) {
            Steganography steganography = steganographies[0];
            Bitmap bitmap = steganography.getImage();
            List<Bitmap> srcEncodedList = Utility.splitImage(bitmap);
            String decoded_message = EncDec.decodeMessage(srcEncodedList);
            if (!Utility.isStringEmpty(decoded_message)) {
                result.setDecoded(true);
            }
            String decrypted_message = Steganography.decryptMessage(decoded_message, steganography.getPassword());
            if (!Utility.isStringEmpty(decrypted_message)) {
                result.setSecretKeyWrong(false);
                result.setMessage(decrypted_message);
                for (Bitmap trash : srcEncodedList)
                    trash.recycle();
                System.gc();
            }
        }
        return result;
    }
}
