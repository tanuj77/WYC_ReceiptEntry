package com.ccs.ccs81.wyc_receiptentry;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

/**
 * Created by Banke Bihari on 9/17/2016.
 */
public class SplashScreen extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    CursorLoader cursorLoader;
    String cpFinancialYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        onClickDisplayNames();


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("companyDetail", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("financialYear",cpFinancialYear);
                editor.commit();
                Toast.makeText(SplashScreen.this,cpFinancialYear,Toast.LENGTH_LONG).show();
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    //////////////Content Provider user///////START///////////

    public void onClickDisplayNames() {
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        cursorLoader = new CursorLoader(this, Uri.parse("content://com.wyc.ccs81.myprovider/cte/1"), null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();

        //StringBuilder res=new StringBuilder();
        while (!cursor.isAfterLast()) {
            //res.append("\n"+cursor.getString(cursor.getColumnIndex("id"))+ "-"+ cursor.getString(cursor.getColumnIndex("name")));

            cpFinancialYear = cursor.getString(cursor.getColumnIndex("financialYear"));

            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }








    @Override
    public void onDestroy() {
        super.onDestroy();
    }


//////////////Content Provider user///////END///////////

}
