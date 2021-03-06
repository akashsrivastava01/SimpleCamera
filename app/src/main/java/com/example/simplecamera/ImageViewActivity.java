package com.example.simplecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity implements View.OnClickListener {

    // Layout View Elements
    ImageView clickedImageView;
    ImageView backImageView;
    Button imageShareButton;

    Uri imageUri;
    String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Bundle extras = getIntent().getExtras();
        imageUri = (Uri) extras.get("IMAGE_URI");
        imageName = (String) extras.get("IMAGE_NAME");
        initializeViewElements();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (imageUri != null) {
            Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show();
            loadImage();
        }
    }

    private void initializeViewElements() {
        clickedImageView = findViewById(R.id.clickedImageView);
        backImageView = findViewById(R.id.backImageView);
        imageShareButton = findViewById(R.id.imageShareButton);
        backImageView.setOnClickListener(this);
        imageShareButton.setOnClickListener(this);
    }

    private void loadImage() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        clickedImageView.getDisplay().getMetrics(displayMetrics);

        Picasso.get().load(imageUri).resize(displayMetrics.widthPixels, displayMetrics.heightPixels).centerCrop().into(clickedImageView);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backImageView) {
            super.onBackPressed();
        } else if (view.getId() == R.id.imageShareButton) {
            shareImage();
        }
    }

    private void shareImage() {
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, getImageToShare(imageBitmap));
            intent.putExtra(Intent.EXTRA_TEXT, "Hey, Check This Image Out!");
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setType("image/jpeg");
            startActivity(Intent.createChooser(intent, "Share Via"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri getImageToShare(Bitmap bitmap) {
        File imagefolder = new File(getFilesDir(), "SimpleImages");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, imageName + ".jpeg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, this.getPackageName(), file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }

}