package com.example.mihika.expocr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoCaptureActivity extends AppCompatActivity {

    // code adapted from following tutorials:
    // https://androidkennel.org/android-camera-access-tutorial/#comment-3110958747
    // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

    private Button takePictureButton;
    private ImageView imageView;
    private Uri file;
    private String trainedDataPath="/mnt/sdcard/tesseract/tessdata/eng.traineddata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);

        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = FileProvider.getUriForFile(PhotoCaptureActivity.this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("PhotoCapture", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
                onPhotoTaken();
            }
        }
    }

    protected void onPhotoTaken() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        String path = Environment.getExternalStorageDirectory()+"/tesseract/tessdata";

        Bitmap bitmap = BitmapFactory.decodeFile( file.getPath(), options );
        Bitmap bitmap= BitmapFactory.decodeFile( Environment.getExternalStorageDirectory() +"/img/testimg.png", options);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        useTess(bitmap);*/
    }

    protected void useTess(Bitmap bitmap)
    {
        checkFile(new File("/mnt/sdcard/tesseract/tessdata"));
        TessBaseAPI tessApi=new TessBaseAPI();
        String setLang="eng";
        tessApi.init(trainedDataPath, setLang);
        tessApi.setImage(bitmap);
        String text=tessApi.getUTF8Text();
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        tessApi.end();
    }

    protected void copyTrainingFiles() {
        try {
            AssetManager assetMan= getAssets();
            InputStream instream = assetMan.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(trainedDataPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyTrainingFiles();
        }
        if(dir.exists()) {
            File datafile = new File(trainedDataPath);

            if (!datafile.exists()) {
                copyTrainingFiles();
            }
        }
    }
}
