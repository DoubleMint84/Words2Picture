package com.example.words2picture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EncoderCallback, DecoderCallback {
    private static final int SELECT_PICTURE = 100;
    private static String TAG = "WORDS";
    private int callType = 1;
    private Uri filePath;
    private EditText editText, passText;
    private ProgressDialog saveDialog;
    private TextView debug;
    private ImageView imgKey;
    private Bitmap bitImage = null, bitRes = null;
    private int intentCall = 0;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.textToEncrypt);
        imgKey = findViewById(R.id.imgKey);
        passText = findViewById(R.id.edtPass);
        debug = findViewById(R.id.debugText);
        btnSave = findViewById(R.id.btnSave);
        clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }

    public void openKeyImage(View view) {
        intentCall = 0;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {

            filePath = imageReturnedIntent.getData();
            try {
                bitImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                imgKey.setImageBitmap(bitImage);
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e);
            }
        }
    }
    public void encryptCall(View view) {
        callType = 1;
        Steganography steganography = new Steganography(editText.getText().toString(),
                passText.getText().toString(),
                bitImage);
        Encoder encoder = new Encoder(this, this);
        encoder.execute(steganography);
    }

    @Override
    public void onStartTextEncoding() { }

    @Override
    public void onCompleteTextEncoding(Steganography result) {
        if (callType == 1) {
            if (result != null && result.isEncoded()) {
                Bitmap encoded_image = result.getEncoded_image();
                Toast toast = Toast.makeText(getApplicationContext(), "Text encoded", Toast.LENGTH_SHORT);
                toast.show();
                imgKey.setImageBitmap(encoded_image);
                bitRes = encoded_image;
                btnSave.setEnabled(true);
            }
        } else {
            if (result != null){
                if (!result.isDecoded()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "No message", Toast.LENGTH_SHORT);
                    toast.show();

                }else{

                    if (!result.isSecretKeyWrong()){
                        Toast toast = Toast.makeText(getApplicationContext(), "Image decoded", Toast.LENGTH_SHORT);
                        toast.show();
                        editText.setText(result.getMessage());
                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
            else {

                Toast toast = Toast.makeText(getApplicationContext(), "Select Image First", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void decipherCall(View view) {
        callType = 2;
        Steganography steganography = new Steganography(passText.getText().toString(),
                bitImage);
        Decoder decoder = new Decoder(this, this);
        decoder.execute(steganography);
    }

    public void onButtonSaveClick(View view) {
        final Bitmap imgToSave = bitRes;
        Thread PerformEncoding = new Thread(new Runnable() {
            @Override
            public void run() {
                saveToInternalStorage(imgToSave);
            }
        });
        saveDialog = new ProgressDialog(this);
        saveDialog.setMessage("Saving, Please Wait...");
        saveDialog.setTitle("Saving Image");
        saveDialog.setIndeterminate(false);
        saveDialog.setCancelable(false);
        saveDialog.show();
        PerformEncoding.start();
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        OutputStream fOut;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Encoded" + ".PNG");
        try {
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            debug.post(new Runnable() {
                @Override
                public void run() {
                    saveDialog.dismiss();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyCall(View view) {
        String text = editText.getText().toString();
        clipData = ClipData.newPlainText("text",text);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(getApplicationContext(),"Text Copied ",Toast.LENGTH_SHORT).show();
    }
}
