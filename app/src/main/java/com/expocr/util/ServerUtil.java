package com.expocr.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class allows the app to communicate with the server by sending and receiving data from it.
 */
public class ServerUtil {

    private static final String ADDR_LOCALHOST = "127.0.0.1:8000/";
    private static final String ADDR_EMULATOR = "10.0.2.2:8000/";
    private static final String ADDR_AZURE = "cs428-expocr.cloudapp.net:8000/";

    /**
     * Getter for the server address being used (either the local server or the Azure server)
     * @return String address of server being used
     */
    public static String getServerAddress() { return getAzureAddress(); }

    private static String getLocalAddress(){
        return ADDR_LOCALHOST;
    }

    private static String getEmulatorAddress() { return ADDR_EMULATOR; }

    private static String getAzureAddress(){
        return ADDR_AZURE;
    }

    /**
     * Sends data to the server.
     * @param url url to open connection with
     * @param requestString
     * @param requestEncoding
     * @return String response from server
     */
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
                String temp = new String(buffer, 0, length, requestEncoding);
                response.append(temp);
            }
            is.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
