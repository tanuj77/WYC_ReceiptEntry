package com.ccs.ccs81.wyc_receiptentry;

/**
 * Created by CCS79 on 29-05-2018.
 */

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class FcmMySingleton {
    private static FcmMySingleton mInstance;
    private static Context mCtx;
    private RequestQueue requestQueue;


    private FcmMySingleton(Context context){
        mCtx = context;
        requestQueue=getRequestQueue();
    }



    private RequestQueue getRequestQueue(){
        if (requestQueue==null)
        {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }



    public static synchronized FcmMySingleton getmInstance(Context context){
        if (mInstance==null){
            mInstance = new FcmMySingleton(context);
        }
        return mInstance;
    }


    public<T> void addToRequestque(Request<T> request)
    {
        getRequestQueue().add(request);
    }

}
