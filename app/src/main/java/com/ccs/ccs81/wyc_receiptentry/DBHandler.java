package com.ccs.ccs81.wyc_receiptentry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;


/**
 * Created by CCS79 on 12-01-2018.
 */

public class DBHandler extends SQLiteOpenHelper {

    //Database version
    private static final int DATABASE_VERSION = 1;
    //Database name
    private static final String DATABASE_NAME = "Winyatra_ReceiptEntry";
    private static final String TABLE_NAME = "ReceiptEntry";
    private static final String Column_CompanyName = "CompanyName";
   // private static final String Column_BranchName = "BranchName";
    private static final String Column_PartyCode = "PartyCode";
    private static final String Column_Amount = "Amount";
    private static final String Column_ChequeNumber = "ChequeNumber";
    private static final String Column_VoucherDate = "VoucherDate";
    private static final String Column_BankName = "BankName";
    private static final String Column_DepositBankId = "DepositBankId";
    private static final String Column_DepositBankName = "DepositBankName";
    private static final String Column_Narration = "Narration";
    private static final String Column_VoucherNumber = "VoucherNumber";
    private static final String Column_VoucherNoStatus = "VoucherNoStatus";

    HashMap<String, List<String>> mapForAllRecords = new HashMap<String, List<String>>();
    HashMap<String, List<String>> mapForSelectedRecords = new HashMap<String, List<String>>();

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_RECEIPT_ENTRY_TABLE = "CREATE TABLE" + " " + TABLE_NAME + "(" + Column_CompanyName + " " + "Text," + Column_PartyCode + " " + "TEXT," +
                Column_Amount + " " + "Text," + Column_ChequeNumber + " " + "Text," + Column_VoucherDate + " " + "Text," + Column_BankName + " " + "Text," +
                Column_DepositBankId + " " + "Text," + Column_DepositBankName + " " + "Text," + Column_Narration + " " + "Text," + Column_VoucherNumber + " " + "Text," + Column_VoucherNoStatus + " " + "Text," + "Status" + " " + "Text" + ")";

        sqLiteDatabase.execSQL(CREATE_RECEIPT_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //DROP OLD TABLE IF ALREADY EXISTS
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //CREATE NEW TABLE
        onCreate(sqLiteDatabase);
    }


    public Long insertRecord(String companyName, String partyCode, String amount, String chequeNumber, String voucherDate, String bankName, String depositBankId, String depositBankName, String narration) {
        Long status;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Column_CompanyName, companyName);
       // contentValues.put(Column_BranchName, branchName);
        contentValues.put(Column_PartyCode, partyCode);
        contentValues.put(Column_Amount, amount);
        contentValues.put(Column_ChequeNumber, chequeNumber);
        contentValues.put(Column_VoucherDate, voucherDate);
        contentValues.put(Column_BankName, bankName);
        contentValues.put(Column_DepositBankId, depositBankId);
        contentValues.put(Column_DepositBankName, depositBankName);
        contentValues.put(Column_Narration, narration);
        contentValues.put(Column_VoucherNumber, "");
        contentValues.put(Column_VoucherNoStatus,"No");
        contentValues.put("Status", "no");

        status = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

        return status;
    }

    public int CheckExistRecord(String VoucherNumber,String CompanyID, String PartyCode, String Amount, String ChequeNo, String VoucherDate, String BankName, String DepositBankId) {
        int i = 0;
        //String query = "Select * from "+TABLE_NAME + " where "+Column_VoucherNumber + "='" + "0"+ "'";
       // String selectQuery = "Select * from "+TABLE_NAME + " Where "+Column_VoucherNumber + "='" + "0"+ "' AND "+Column_CompanyName+"= "+CompanyID+" AND "+Column_BranchName+" = "+Branch+" AND "+Column_PartyCode+" ="+PartyCode+" AND "+Column_Amount+" ="+PartyCode+" ";
       // String selectQuery = "Select * from ReceiptEntry WHERE VoucherNumber = '"+VoucherNumber+"' AND CompanyName = '"+CompanyID+"' AND BranchName = '"+Branch+"' AND  PartyCode = '"+PartyCode+"' AND  Amount = '"+PartyCode+"' AND ChequeNumber = '"+ChequeNo+"' AND voucherDate = '"+VoucherDate+"' AND BankName = '"+BankName+"' AND DepositBankName = '"+DepositBankName+"'";


        String selectQuery = "Select * from ReceiptEntry WHERE VoucherNoStatus = 'Got' AND CompanyName = '"+CompanyID+"' AND PartyCode = '"+PartyCode+"' AND Amount = '"+Amount+"' AND ChequeNumber = '"+ChequeNo+"' AND VoucherDate = '"+VoucherDate+"' AND BankName = '"+BankName+"' AND DepositBankId = '"+DepositBankId+"'";
//        String selectQuery = "Select * from ReceiptEntry WHERE Status = selected";
     //   String selectQuery = "Select * from ReceiptEntry WHERE VoucherNoStatus = 'Got' AND VoucherNumber = '"+VoucherNumber+"'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {
                Log.i("CheckExistRecord",""+cursor);
                Log.i("CheckExistRecord",""+cursor.getColumnIndex(Column_VoucherNumber));
                i++;
               // check = "Record Already Send 22";
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return i;
    }

    public String getTransaction() {
        ArrayList<HashMap<String, String>> transactionList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "Select * from" + " " + TABLE_NAME + " " + "Where Status = '" + "no" + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> singleTransaction = new HashMap<String, String>();
                singleTransaction.put("companyName", cursor.getString(cursor.getColumnIndex(Column_CompanyName)));
                //singleTransaction.put("branchName", cursor.getString(cursor.getColumnIndex(Column_BranchName)));
                singleTransaction.put("partyCode", cursor.getString(cursor.getColumnIndex(Column_PartyCode)));
                singleTransaction.put("amount", cursor.getString(cursor.getColumnIndex(Column_Amount)));
                singleTransaction.put("chequeNumber", cursor.getString(cursor.getColumnIndex(Column_ChequeNumber)));
                singleTransaction.put("voucherDate", cursor.getString(cursor.getColumnIndex(Column_VoucherDate)));
                singleTransaction.put("bankName", cursor.getString(cursor.getColumnIndex(Column_BankName)));
                singleTransaction.put("depositBank", cursor.getString(cursor.getColumnIndex(Column_DepositBankName)));
                singleTransaction.put("narration", cursor.getString(cursor.getColumnIndex(Column_Narration)));

                transactionList.add(singleTransaction);
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        Gson gson = new Gson();
        return gson.toJson(transactionList);
    }

    public HashMap<String, List<String>> showAllListRecords() {
        int m = 0;

        String selectQuery = "Select * from" + " " + TABLE_NAME + " " + "Where Status = '" + "no" + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                List<String> valueSet = new ArrayList<String>();
                valueSet.add(cursor.getString(0));
                valueSet.add(cursor.getString(1));
                valueSet.add(cursor.getString(2));
                valueSet.add(cursor.getString(3));
                valueSet.add(cursor.getString(4));
                valueSet.add(cursor.getString(5));
                valueSet.add(cursor.getString(6));
                valueSet.add(cursor.getString(7));
                valueSet.add(cursor.getString(8));
                valueSet.add(cursor.getString(9));

                mapForAllRecords.put(m + "", valueSet);
                m++;
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return mapForAllRecords;
    }


    public HashMap<String, List<String>> SelectedRecordsToSend() {
        int m = 0;

        String selectQuery = "Select * from" + " " + TABLE_NAME + " " + "Where Status = '" + "selected" + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                List<String> valueSet = new ArrayList<String>();
                valueSet.add(cursor.getString(0));
                valueSet.add(cursor.getString(1));
                valueSet.add(cursor.getString(2));
                valueSet.add(cursor.getString(3));
                valueSet.add(cursor.getString(4));
                valueSet.add(cursor.getString(5));
                valueSet.add(cursor.getString(6));
                valueSet.add(cursor.getString(7));
                valueSet.add(cursor.getString(8));
                valueSet.add(cursor.getString(9));

                mapForSelectedRecords.put(m + "", valueSet);
                m++;
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return mapForSelectedRecords;
    }


    public int UpdateStatusSelected(String CompanyID,  String PartyCode, String Amount, String ChequeNo, String VoucherDate, String BankName, String DepositBankName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Status", "selected");
        return db.update(TABLE_NAME, cv, Column_CompanyName + " = ? AND " + Column_PartyCode + " = ? AND " + Column_Amount + " = ? AND " + Column_ChequeNumber + " = ? AND " + Column_VoucherDate + " = ? AND " + Column_BankName + " = ? AND " + Column_DepositBankName + " = ?",
                new String[]{CompanyID, PartyCode, Amount, ChequeNo, VoucherDate, BankName, DepositBankName});
    }


    public int UpdateVoucherNo(String CompanyID,String PartyCode, String Amount, String ChequeNo, String VoucherDate, String BankName, String DepositBankName, String VoucherNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Column_VoucherNumber, VoucherNumber);
        cv.put(Column_VoucherNoStatus, "Got");
        return db.update(TABLE_NAME, cv, Column_CompanyName + " = ? AND "  + Column_PartyCode + " = ? AND " + Column_Amount + " = ? AND " + Column_ChequeNumber + " = ? AND " + Column_VoucherDate + " = ? AND " + Column_BankName + " = ? AND " + Column_DepositBankName + " = ?",
                new String[]{CompanyID, PartyCode, Amount, ChequeNo, VoucherDate, BankName, DepositBankName});
    }


    public String deleteSinglerecord(String CompanyID, String PartyCode, String Amount, String ChequeNo, String VoucherDate, String BankName, String DepositBankName) {
        String flag = "notRemoved";

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,
                Column_CompanyName + " = ? AND " +  Column_PartyCode + " = ? AND " + Column_Amount + " = ? AND " + Column_ChequeNumber + " = ? AND " + Column_VoucherDate + " = ? AND " + Column_BankName + " = ? AND " + Column_DepositBankName + " = ?",
                new String[]{CompanyID, PartyCode, Amount, ChequeNo, VoucherDate, BankName, DepositBankName});

        sqLiteDatabase.close();
        flag = "Record Deleted";

        return flag;
    }


    public String deleteRecords() {
        String flag = "notRemoved";

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, Column_VoucherNoStatus + " = ? ",
                new String[]{"Got"});
        sqLiteDatabase.close();
        flag = "Removed";

        return flag;
    }



    public String removeAlreadySendRecords(String CompanyID) {
        String flag = "notRemoved";

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, "Status" + " = ? AND " + Column_CompanyName + " = ? ",
                new String[]{"selected", CompanyID});
        sqLiteDatabase.close();
        flag = "Records Deleted";

        return flag;
    }


//DELTE QUERY EXAMPLE/////
//    public void delete(String dan, int vrijeme){
//        db.delete(TABLE_NAME,
//                TABLE_COLUMN_ONE + " = ? AND " + TABLE_COLUMN_TWO + " = ?",
//                new String[] {dan, vrijeme+""});
//    }
}


