package com.example.mihika.expocr.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.mihika.expocr.LoginActivity;
import com.example.mihika.expocr.SignupActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerUtil {

    public static String sendData(String url, String requestString, String requestEncoding){
        StringBuilder response = new StringBuilder();

        try {
            URL wsurl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) wsurl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(requestString.getBytes(requestEncoding));
            os.close();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1)
            {
                String temp = new String(buffer, 0, length, "UTF-8");
                response.append(temp);
                System.out.println(temp);
            }
            is.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
