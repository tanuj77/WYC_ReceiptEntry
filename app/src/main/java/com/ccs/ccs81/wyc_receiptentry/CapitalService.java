package com.ccs.ccs81.wyc_receiptentry;

/**
 * Created by CCS79 on 29-01-2018.
 */

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CapitalService extends IntentService {

    public CapitalService(){
        super("Empty Constructor");
    }

    public CapitalService(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    // This method is executed in background when this service is started
    @Override
    protected void onHandleIntent(Intent intent) {
        String country = "";
        String capital = "";
        String url = "";

        try {

            country = URLEncoder.encode(intent.getStringExtra(Constants.EXTRA_COUNTRY),"utf-8");

        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        url = Constants.SERVER_URL + "/get_capital.php?country=" + country;

        try {
            // Retreiving capital from the url
            capital = downloadUrl(url);

            // Creating an intent for broadcastreceiver
            Intent broadcastIntent = new Intent(Constants.BROADCAST_ACTION);

            // Attaching data to the intent
            broadcastIntent.putExtra(Constants.EXTRA_CAPITAL, capital);

            // Sending the broadcast
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** A method to download data from url */
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();


            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while fetching data", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}

