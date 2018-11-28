package com.ccs.ccs81.wyc_receiptentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Banke Bihari on 21/11/2016.
 */
public class RecordList extends Activity {
    ListView listView;
    DBHandler dbHandler = new DBHandler(this);
    android.support.v7.widget.Toolbar toolbar;
    HashMap<String, List<String>> hashMapSendRecords = new HashMap<String, List<String>>();
    String sendcompanyName, sendbranchName, sendpartyCode, sendamount, sendchequeNumber, sendvoucherDate, sendbankName, senddepositBankId, senddepositBankname, sendnarration;

    List<String> listCompanyName = new ArrayList<String>();
    // List<String> listBranchName = new ArrayList<String>();
    List<String> listPartyCode = new ArrayList<String>();
    List<String> listAmount = new ArrayList<String>();
    List<String> listChequeNo = new ArrayList<String>();
    List<String> listVoucherDate = new ArrayList<String>();
    List<String> listBankName = new ArrayList<String>();
    List<String> listDepositBankId = new ArrayList<String>();
    List<String> listDepositBankName = new ArrayList<String>();
    List<String> listNarration = new ArrayList<String>();
    List<String> listVoucherNumber = new ArrayList<String>();
    String cpCompanyUrl, strCompanyName, strBranch, strPartyCode, strAmount, strChequeNo, strVoucherDate, strBankName, strDepositBankId, strDepositBankName, strNarration, strVcNumber;
    String mUserName, cpCompanyID, cpMobileNo, cpEmail, cpPartyCode;
    ProgressBar pbCircular;
    Button buttonSyncData, buttonDelete;
    HashMap<String, List<String>> mapListRes = new HashMap<String, List<String>>();
    JSONObject jsonobject;
    String strVoucherNumber = null;
    CustomListCustomer customListadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Customer List");

        final ListView listview = (ListView) findViewById(R.id.list_customer);
        pbCircular = (ProgressBar) findViewById(R.id.pbCirular);
        pbCircular.setVisibility(View.INVISIBLE);
        buttonSyncData = (Button) findViewById(R.id.button_Syncdataonserver);


        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("companyDetail", MODE_PRIVATE);
        cpCompanyID = sharedPreferences.getString("companyID", null);
        //cpStartDate = sharedPreferences.getString("startDate", null);
        cpCompanyUrl = sharedPreferences.getString("companyUrl", null);
        mUserName = sharedPreferences.getString("userName", null);
        cpPartyCode = sharedPreferences.getString("partyCode", null);
        cpMobileNo = sharedPreferences.getString("mobileNo", null);
        cpEmail = sharedPreferences.getString("cpEmail", null);


        mapListRes = dbHandler.showAllListRecords();
        Log.i("RRBBSqlite", "" + mapListRes);
        for (Map.Entry<String, List<String>> entry : mapListRes.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            listCompanyName.add(values.get(0));
            //listBranchName.add(values.get(1));
            listPartyCode.add(values.get(1));
            listAmount.add(values.get(2));
            listChequeNo.add(values.get(3));
            listVoucherDate.add(values.get(4));
            listBankName.add(values.get(5));
            listDepositBankId.add(values.get(6));
            listDepositBankName.add(values.get(7));
            listNarration.add(values.get(8));
            listVoucherNumber.add(values.get(9));

        }
        customListadapter = new CustomListCustomer(RecordList.this, listCompanyName, listPartyCode, listAmount, listChequeNo, listVoucherDate, listBankName, listDepositBankId, listDepositBankName, listNarration, listVoucherNumber);

        listview.setAdapter(customListadapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecordList.this);
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("This record will be send to server " + listCompanyName.get(position) + " and " + listAmount.get(position) + " ");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.setBackgroundResource(R.color.LightBlue);
                        strCompanyName = customListadapter.listCompanyName.get(position);
                        // strBranch = adapter.listBranchName.get(position);
                        strPartyCode = customListadapter.listPartyCode.get(position);
                        strAmount = customListadapter.listAmount.get(position);
                        strChequeNo = customListadapter.listChequeNo.get(position);
                        strVoucherDate = customListadapter.listVoucherDate.get(position);
                        strBankName = customListadapter.listBankName.get(position);
                        strDepositBankId = customListadapter.listDepositBankId.get(position);
                        strDepositBankName = customListadapter.listDepositBankName.get(position);
                        strNarration = customListadapter.listNarration.get(position);
                        strVoucherNumber = customListadapter.listVoucherNumber.get(position);

                        //   int noofrecords = dbHandler.UpdateStatusSelected(strCompanyName, strPartyCode, strAmount, strChequeNo, strVoucherDate, strBankName, strDepositBankName);

                        int alreadyExist = dbHandler.CheckExistRecord(strVoucherNumber, strCompanyName, strPartyCode, strAmount, strChequeNo, strVoucherDate, strBankName, strDepositBankId);
                        if (alreadyExist > 0) {
                            Toast.makeText(RecordList.this, "Record already send on server", Toast.LENGTH_LONG).show();
                        } else {
                            new receiptEntryInfoAsync().execute();
                           // customListadapter.updateAdapter(listVoucherNumber);
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();

            }

        });


        buttonSyncData.setVisibility(View.INVISIBLE);
        buttonSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashMapSendRecords = dbHandler.SelectedRecordsToSend();
                //  jsonobject=new JSONObject(hashMapSendRecords);

                for (Map.Entry<String, List<String>> entry : hashMapSendRecords.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();
                    sendcompanyName = values.get(0);
                    //sendbranchName = values.get(1);
                    sendpartyCode = values.get(1);
                    sendamount = values.get(2);
                    sendchequeNumber = values.get(3);
                    sendvoucherDate = values.get(4);
                    sendbankName = values.get(5);
                    senddepositBankId = values.get(6);
                    senddepositBankname = values.get(7);
                    sendnarration = values.get(8);
                }

                Intent mServiceIntent = new Intent(getApplicationContext(), CapitalService.class);
                mServiceIntent.putExtra(Constants.EXTRA_COUNTRY, sendamount);
                startService(mServiceIntent);

                Log.i("TEST", "JSON FORMAT:" + jsonobject);
                new receiptEntryInfoAsync().execute();
//                }

            }
        });

//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String deleteFlag = dbHandler.removeAlreadySendRecords(strCompanyName);
//                Toast.makeText(RecordList.this, "Already send data deleted from sqlite table", Toast.LENGTH_LONG).show();
//            }
//        });

    }


    class receiptEntryInfoAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pbCircular.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ReceiptEntryWebservice receiptEntryWebservice = new ReceiptEntryWebservice();
            strVoucherNumber = receiptEntryWebservice.getConvertedWeight(cpCompanyUrl, "Deposit_Detail", strCompanyName, mUserName,strPartyCode, strAmount, strChequeNo, strVoucherDate, strBankName, strDepositBankId, strDepositBankName, strNarration, cpMobileNo);
            //         aResponse = receiptEntryWebservice.getConvertedWeight(cpCompanyUrl, "Deposit_Detail",jsonobject);
            Log.i("RRBBledetail", strVoucherNumber);
            return null;
        }

        protected void onPostExecute(Void result) {
            pbCircular.setVisibility(View.INVISIBLE);
            // Toast.makeText(RecordList.this, "" + strVoucherNumber, Toast.LENGTH_LONG).show();
            int updateres = dbHandler.UpdateVoucherNo(strCompanyName, strPartyCode, strAmount, strChequeNo, strVoucherDate, strBankName, strDepositBankName, strVoucherNumber);
            //     Toast.makeText(RecordList.this, "voucher update res = "+updateres, Toast.LENGTH_LONG).show();
//            if (aResponse.trim().equalsIgnoreCase("Data Added Successfully") || aResponse.trim().equals("Data Added Successfully") || aResponse == "Data Added Successfully") {
//                String deleteFlag = dbHandler.removeRecord(strCompanyName, strBranch, strPartyCode, strAmount, strChequeNo, strVoucherDate, strBankName, strDepositBankName);
//                Toast.makeText(RecordList.this, "single record from sqlite table", Toast.LENGTH_LONG).show();
//            }
        }
    }
}

