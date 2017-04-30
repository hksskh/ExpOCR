package com.example.mihika.expocr;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mihika.expocr.util.LoadingDialog;
import com.example.mihika.expocr.util.ServerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This activity handles taking the picture for when a user wants to scan a bill.
 */
public class PhotoCaptureActivity extends AppCompatActivity {

    // code adapted from following tutorials:
    // https://androidkennel.org/android-camera-access-tutorial/#comment-3110958747
    // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

    private final int FINISH_LOADING = 1;
    private final int MAX_IMAGE_SIZE = 4 * 1024 * 1024;

    private Button takePictureButton;
    private ImageView imageView;
    private Dialog loading_dialog;
    private Handler handler;

    private Uri file;
    private final String TAG = "PhotoCaptureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);

        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageview);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch(msg.what){
                    case FINISH_LOADING:
                        LoadingDialog.closeDialog(loading_dialog);
                        break;
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    /**
     * Get permission from user to use their camera.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    /**
     * Takes the picture and stores it.
     * @param view
     */
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
                takePictureButton.setText("Recognize this receipt!");
                takePictureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loading_dialog = LoadingDialog.showDialog(PhotoCaptureActivity.this, "Receipt Recognizing...");
                        byte[] image_byte = loadImage();
                        Log.d(TAG, "image_byte length: " + String.valueOf(image_byte.length));
                        String image_string = byteToString(image_byte);
                        Log.d(TAG, "image_string length: " + String.valueOf(image_string.length()));
                        sendImageToOCR(image_string);
                    }
                });
            }
        }
    }

    private void sendImageToOCR(final String image_string){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://" + ServerUtil.getServerAddress() + "transaction/ocr_test";
                String requestString = null;
                try {
                    requestString = new String("image=".getBytes(), "ISO-8859-1") + image_string;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String response = ServerUtil.sendData(url, requestString, "ISO-8859-1");

                Message msg = new Message();
                msg.what = FINISH_LOADING;
                handler.sendMessage(msg);
                //Log.d(TAG, "From server:" + response);
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    //Log.d(TAG, "response: " + jsonObject);
                    if(jsonObject.has("warning")){
                        System.out.println("warning: " + jsonObject.get("warning"));
                    }else{
                        JSONArray jsonArray = jsonObject.getJSONArray("receipt_sketch");
                        Intent intent = new Intent(PhotoCaptureActivity.this, RecognizeReceiptActivity.class);
                        intent.putExtra("receipt_sketch", jsonArray.toString());
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String byteToString(byte[] data){
        String image_string = null;

        try {
            image_string = new String(data, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return image_string;
    }

    private byte[] loadImage(){
        byte[] data = null;
        File image_file = null;
        FileInputStream fis = null;

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        float scale = (float)3200 / Math.max(bitmap.getWidth(), bitmap.getHeight());
        System.out.println(scale);
        if(scale < 1){
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        System.out.println(bitmap.getWidth());
        System.out.println(bitmap.getHeight());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();
        System.out.println(data.length);
        if(data.length > MAX_IMAGE_SIZE){
            scale = (float) Math.sqrt(MAX_IMAGE_SIZE / (double)data.length);
            System.out.println(scale);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        }
        System.out.println(bitmap.getWidth());
        System.out.println(bitmap.getHeight());
        System.out.println("resize: " + data.length);

        return data;
    }
}
