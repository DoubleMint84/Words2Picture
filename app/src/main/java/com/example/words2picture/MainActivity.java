package com.example.words2picture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    static final int GALLERY_REQUEST = 1;
    private EditText editText;
    private ImageView imgKey, imgInput;
    private Bitmap bitKey = null, bitInput = null, bitEncrypt = null;
    private int intentCall = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.textToEncrypt);
        imgKey = findViewById(R.id.imgKey);
        imgInput = findViewById(R.id.imgEncrypt);
    }


    public void openKeyImage(View view) {
        intentCall = 0;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    public void openInputImage(View view) {
        intentCall = 1;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = imageReturnedIntent.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    switch (intentCall) {
                        case 0:
                            bitKey = BitmapFactory.decodeStream(imageStream);
                            imgKey.setImageBitmap(bitKey);
                            break;
                        case 1:
                            bitInput = BitmapFactory.decodeStream(imageStream);
                            imgInput.setImageBitmap(bitInput);
                            break;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
