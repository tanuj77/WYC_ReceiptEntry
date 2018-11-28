package com.ccs.ccs81.wyc_receiptentry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Banke Bihari on 17/11/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    HashMap<String,List<String>> mapForAllCustomer = new HashMap<String ,List<String>>();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WinYatra_Registraytion";

    //  table name
    private static final String TABLE_CUSTOMER = "Customer_Registration";

    //  Table Columns names
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_PASSWORD = "password";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_COMPANY_NAME + " TEXT," + KEY_DEVICE_ID + " TEXT,"
                + KEY_PASSWORD + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
// Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
// Adding new contact
    public Long insertCustomer(String companyName, String deviceId, String password) {
        Long status;
        String status2;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COMPANY_NAME, companyName);
        values.put(KEY_DEVICE_ID, deviceId);
        values.put(KEY_PASSWORD,password);
      status =  database.insert(TABLE_CUSTOMER, null, values);

        database.close();
        return  status;
    }

    public String CheckRecord(String getcompanyName){
        String companyName = null;
        String deviceId = null;
        String password = null;
        String check = "not exist";
        String selectQuery = "SELECT * FROM Customer_Registration WHERE company_name = '"+getcompanyName+"'";
        SQLiteDatabase database = this.getWritableDatabase();
         Cursor cursor = database.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                companyName = cursor.getString(cursor.getColumnIndex("company_name"));
                deviceId = cursor.getString(cursor.getColumnIndex("device_id"));
                password = cursor.getString(cursor.getColumnIndex("password"));
                check = "exist";
            }while (cursor.moveToNext());
        }
        return check;
    }

    public String ViewCustomer(String getDeviceId){
        String companyName = null;
        String deviceId = null;
        String password = null;
        String selectQuery = "SELECT * FROM Customer_Registration WHERE device_id = '"+getDeviceId+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                companyName = cursor.getString(cursor.getColumnIndex("company_name"));
                deviceId = cursor.getString(cursor.getColumnIndex("device_id"));
                password = cursor.getString(cursor.getColumnIndex("password"));
               // name = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        database.close();

        return companyName;
    }

//    public String DeleteCustomer(String getcompanyName){
//        String res = null;
//        String deleteQuery = "DELETE FROM Customer_Registration WHERE company_name = '"+getcompanyName+"'";
//        SQLiteDatabase database = this.getWritableDatabase();
//        database.execSQL(deleteQuery);
//        res = "Record Deleted";
//        return res;
//    }
//public String  updateCustomer(String getcompanyName,String getdeviceId,String phoneNo){
//    String res = "null";
//    String updateQuery = "UPDATE Customer_Registration SET company_name = '"+getcompanyName+"' WHERE device_id = '"+getdeviceId+"'";
//    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//    sqLiteDatabase.execSQL(updateQuery);
//    res = "Record Updated";
//
//    return res;
//}


//    public HashMap<String, List<String>> showAllCustomer(){
//        int m = 0;
//        String selectQuery = "SELECT * FROM customer_master";
//        SQLiteDatabase database = this.getWritableDatabase();
//        Cursor cursor = database.rawQuery(selectQuery,null);
//        if (cursor.moveToFirst()) {
//            do {
//                List<String> valueset = new ArrayList<String>();
//                valueset.add(cursor.getString(0));
//                valueset.add(cursor.getString(1));
//                valueset.add(cursor.getString(2));
//                mapForAllCustomer.put(m + "", valueset);
//                m++;
//            } while (cursor.moveToNext());
//        }
//        database.close();
//        return mapForAllCustomer;
//
//    }
}