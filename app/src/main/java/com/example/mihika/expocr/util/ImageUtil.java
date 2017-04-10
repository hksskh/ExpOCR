package com.example.mihika.expocr.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    public static byte[] getBytesFromBitmap(Bitmap bitmap){
        byte[] data;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();

        return data;
    }
}
