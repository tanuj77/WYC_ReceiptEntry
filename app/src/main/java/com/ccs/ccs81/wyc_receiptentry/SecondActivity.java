package com.ccs.ccs81.wyc_receiptentry;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
//import com.goodiebag.pinview.Pinview;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Banke Bihari on 04/11/2016.
 */
public class SecondActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    EditText editTextCompanyID, editTextBranch, editTextPartyCode, editTextAmount, editTextChequeNo, editTextBankName, editTextNarration;
    EditText et1_cn, et2_cn, et3_cn, et4_cn, et5_cn, et6_cn, et7_cn, et8_cn, et9_cn, et10_cn;
    Spinner spinnerBranch, spinnerParty, spinnerDepositBankName;
    TextView textViewChequeDate, textViewVoucherDate;

    Button buttonSubmit, buttonSync, buttonDelete;
    ProgressBar pbCircular;
    String branchName = "", customerBranch, agentBranch, amount;
    String chequeNumber = "", chequeDate, voucherDate;
    String cpCompanyID, cpCompanyUrl, cpPartyCode, cpMobileNo, partyCode;
    String mUserName, mPassword, check;
    String aResponse, res, bankName, narration, saveData, strFinancialYear,cpFinancialYear;
    String makeExcelSheet = "false";
    String flagPartyError = "", flagAmountError = "", flagChequeError = "", flagVoucherdateError = "", flagBankError = "", flagDepositBankError = "", flagNarration = "";
    String branchDropDownResponse = "", bankDropDownResponse = "", partyDropDownResponse = "", cpMultipleBranch = "", cpBranch = "", cpEmail = "";
    String strDepositBankName, strDepositBankId, strPartyCode, strPartyName, strBranchName;
    HashMap<String, String> hashMapBankDropDown = new HashMap<String, String>();
    HashMap<String, String> HashMapPartyDropDownResponse = new HashMap<String, String>();
    ConnectionDetector cd;
    DBHandler dbHandler = new DBHandler(this);
    CheckBox checkBoxOffline;

    List<String> listPartyCode = new ArrayList<String>();
    List<String> listPartyName = new ArrayList<String>();
    List<String> depositBanksId = new ArrayList<String>();
    List<String> depositBanksName = new ArrayList<String>();
    // private Pinview pinview;
    //  PinEntryEditText pinEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        cd = new ConnectionDetector(getApplicationContext());
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Internet Connection Error");
            alertDialog.setMessage("Please connect to working Internet connection");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SecondActivity.this.finish();
                }
            });
            alertDialog.show();
            return;
        }


        editTextCompanyID = (EditText) findViewById(R.id.et_companyid);

        editTextAmount = (EditText) findViewById(R.id.et_Amount);

        et1_cn = (EditText) findViewById(R.id.et1_cn);
        et2_cn = (EditText) findViewById(R.id.et2_cn);
        et3_cn = (EditText) findViewById(R.id.et3_cn);
        et4_cn = (EditText) findViewById(R.id.et4_cn);
        et5_cn = (EditText) findViewById(R.id.et5_cn);
        et6_cn = (EditText) findViewById(R.id.et6_cn);
        et7_cn = (EditText) findViewById(R.id.et7_cn);
        et8_cn = (EditText) findViewById(R.id.et8_cn);
        et9_cn = (EditText) findViewById(R.id.et9_cn);
        et10_cn = (EditText) findViewById(R.id.et10_cn);

        textViewChequeDate = (TextView) findViewById(R.id.et_chequedate);
        //textViewVoucherDate = (TextView) findViewById(R.id.et_voucherdate);
        editTextPartyCode = (EditText) findViewById(R.id.et_partycode);
        spinnerParty = (Spinner) findViewById(R.id.spinner_party);
        editTextBankName = (EditText) findViewById(R.id.et_bankname);
        // spinnerFinancialYear= (Spinner) findViewById(R.id.spinnerfinancialyear);
        spinnerDepositBankName = (Spinner) findViewById(R.id.spinner_depositbankname);
        editTextNarration = (EditText) findViewById(R.id.et_narration);
        checkBoxOffline = (CheckBox) findViewById(R.id.cb_offline);
        buttonSubmit = (Button) findViewById(R.id.btn_submit);
        buttonSync = (Button) findViewById(R.id.btn_sync);
        buttonDelete = (Button) findViewById(R.id.btn_deleterecords);
        pbCircular = (ProgressBar) findViewById(R.id.pbCirular);
        pbCircular.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("companyDetail", MODE_PRIVATE);
        cpCompanyID = sharedPreferences.getString("companyID", null);
        cpCompanyUrl = sharedPreferences.getString("companyUrl", null);
        cpPartyCode = sharedPreferences.getString("partyCode", null);
        mUserName = sharedPreferences.getString("userName", null);
        mPassword = sharedPreferences.getString("password", null);
        cpMobileNo = sharedPreferences.getString("mobileNo", null);
        // branchDropDownResponse = sharedPreferences.getString("branchDropDownResponse",null);
        //  partyDropDownResponse = sharedPreferences.getString("partyDropDownResponse", null);
        bankDropDownResponse = sharedPreferences.getString("bankDropDownResponse", null);
        cpMultipleBranch = sharedPreferences.getString("multipleBranch", null);
        cpFinancialYear = sharedPreferences.getString("financialYear", null);
        cpEmail = sharedPreferences.getString("cpEmail", null);

        new infoAsyncPartyDropDown().execute();

//Convert string into  hash map using json
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        HashMap<String, String> hashMapBank = gson.fromJson(bankDropDownResponse, type);

        //use values
        String toastString = hashMapBank.get("key1") + " | " + hashMapBank.get("key2");
        //Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();

        spinnerDepositBankName.setOnItemSelectedListener(this);

        depositBanksName.add("Select Deposit Bank Name");
        depositBanksId.add("0");
        for (Map.Entry<String, String> entry : hashMapBank.entrySet()) {
            String keyBankId = entry.getKey();
            String valuesBankName = entry.getValue();
            depositBanksId.add(keyBankId);
            depositBanksName.add(valuesBankName);
        }


        ArrayAdapter<String> depositBankDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, depositBanksName);
        depositBankDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositBankName.setAdapter(depositBankDataAdapter);


//        Calendar calendar = Calendar.getInstance();
//        int fyear = calendar.get(Calendar.YEAR);
//        spinnerFinancialYear.setOnItemSelectedListener(this);
//        List<String> financialYearList = new ArrayList<String>();
//        financialYearList.add("Select Financial Year");
//        //   financialYearList.add(cpFinancialYear);
//        for (int i = Integer.parseInt(cpFinancialYear); i <=fyear; i++) {
//
//            financialYearList.add(""+i);
//        }
//
//        ArrayAdapter<String> financialYearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, financialYearList);
//        financialYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerFinancialYear.setAdapter(financialYearAdapter);
        //Convert string into  hash map using json
//        java.lang.reflect.Type typeParty = new TypeToken<HashMap<String, String>>() {
//        }.getType();
//        Gson gsonParty = new Gson();
//        HashMap<String, String> hashMapParty = gsonParty.fromJson(partyDropDownResponse, typeParty);
//
//        //use values
//        String toastStringParty = hashMapParty.get("key1") + " | " + hashMapParty.get("key2");
//        Toast.makeText(this, toastStringParty, Toast.LENGTH_LONG).show();
//
//        spinnerParty.setOnItemSelectedListener(this);
//
//        listPartyName.add("Select Party Name");
//        listPartyCode.add("0");
//        for (Map.Entry<String, String> entry : hashMapParty.entrySet()) {
//            String keyPartyCode = entry.getKey();
//            String valuesPartyName = entry.getValue();
//            listPartyCode.add(keyPartyCode);
//            listPartyName.add(valuesPartyName);
//        }
//
//        ArrayAdapter<String> partyDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listPartyName);
//        partyDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerParty.setAdapter(partyDataAdapter);

        spinnerParty.setOnItemSelectedListener(this);

        editTextCompanyID.setText(cpCompanyID);


        if (cpPartyCode == "Agent" || cpPartyCode.equalsIgnoreCase("Agent") || cpPartyCode.equals("Agent")) {
//            editTextBranch.setText("");
//            editTextBranch.setClickable(false);
//            editTextBranch.setFocusableInTouchMode(false);
//            editTextBranch.setFocusable(false);
//            editTextBranch.setVisibility(View.INVISIBLE);
//
//            spinnerBranch.setClickable(true);
//            spinnerBranch.setFocusableInTouchMode(true);
//            spinnerBranch.setFocusable(true);
//            spinnerBranch.setVisibility(View.VISIBLE);
//            spinnerBranch.setEnabled(true);
//            branchDropDownResponse = branchDropDownResponse.replace("[", "");
//            branchDropDownResponse = branchDropDownResponse.replace("]", "");
//            String[] branch = branchDropDownResponse.split(",");
//
//            spinnerBranch.setOnItemSelectedListener(this);
//            List<String> listBranch = new ArrayList<String>();
//            listBranch.add("Select Agent Branch");
//            for (int i = 0; i < branch.length; i++) {
//
//                listBranch.add(branch[i]);
//            }
//            listBranch.add("All");
//
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listBranch);
//            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerBranch.setAdapter(dataAdapter);

            editTextPartyCode.setClickable(true);
            editTextPartyCode.setFocusable(true);
            editTextPartyCode.setFocusableInTouchMode(true);
            editTextPartyCode.setVisibility(View.INVISIBLE);

            spinnerParty.setVisibility(View.VISIBLE);

        } else {
//            if (cpMultipleBranch == "yes" || cpMultipleBranch.equalsIgnoreCase("yes") || cpMultipleBranch.equals("yes")) {
//                customerBranch = cpBranch;
//                editTextBranch.setText(cpBranch);
//                editTextBranch.setClickable(false);
//                editTextBranch.setFocusableInTouchMode(false);
//                editTextBranch.setFocusable(false);
//                editTextBranch.setVisibility(View.VISIBLE);
//
//                spinnerBranch.setClickable(false);
//                spinnerBranch.setFocusableInTouchMode(false);
//                spinnerBranch.setFocusable(false);
//                spinnerBranch.setEnabled(false);
//                spinnerBranch.setVisibility(View.INVISIBLE);
//            } else {
//                customerBranch = "";
//                editTextBranch.setText("");
//                editTextBranch.setClickable(false);
//                editTextBranch.setFocusableInTouchMode(false);
//                editTextBranch.setFocusable(false);
//                editTextBranch.setVisibility(View.VISIBLE);
//
//                spinnerBranch.setClickable(false);
//                spinnerBranch.setFocusableInTouchMode(false);
//                spinnerBranch.setFocusable(false);
//                spinnerBranch.setEnabled(false);
//                spinnerBranch.setVisibility(View.INVISIBLE);
//            }
            partyCode = cpPartyCode;
            editTextPartyCode.setClickable(false);
            editTextPartyCode.setFocusable(false);
            editTextPartyCode.setFocusableInTouchMode(false);
            editTextPartyCode.setText(cpPartyCode);
            editTextPartyCode.setVisibility(View.VISIBLE);

            spinnerParty.setVisibility(View.INVISIBLE);
        }

        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String strAmount = editTextAmount.getText().toString();

                Pattern p = Pattern.compile("^[-+]?([0-9]{0,9})?\\.?([0-9]{0,2})?$");
                Matcher m = p.matcher(strAmount);
                if (m.matches()) {
                    flagAmountError = "No";
                    // Toast.makeText(SecondActivity.this, "Valid Amount", Toast.LENGTH_SHORT).show();
                } else {
                    flagAmountError = "Yes";
                    AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this).create();
                    alertDialog.setTitle("Invalid Amount");
                    alertDialog.setMessage("Please enter valid amount.......Example = 123456789.77");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            editTextAmount.setText("");

                            //editTextAmount.requestFocus();
                        }
                    });
                    alertDialog.show();
                    return;
                }
            }
        });


        et1_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et1 = et1_cn.getText().length();
                if (et1 == 1) {
                    chequeNumber = et1_cn.getText().toString();
                    flagChequeError = "No";
                    et1_cn.setFocusable(false);
                    et1_cn.setFocusableInTouchMode(false);
                    et1_cn.setClickable(false);
                    // et1_cn.setKeyListener(null);
                    et1_cn.setCursorVisible(false);
                    et1_cn.setPressed(false);

                    et2_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et2_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et2 = et2_cn.getText().length();
                if (et2 == 1) {
                    chequeNumber = chequeNumber.concat(et2_cn.getText().toString());
                    et2_cn.setFocusable(false);
                    et2_cn.setFocusableInTouchMode(false);
                    et2_cn.setClickable(false);
                    // et2_cn.setKeyListener(null);
                    et2_cn.setCursorVisible(false);
                    et2_cn.setPressed(false);
                    et3_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et3_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et3 = et3_cn.getText().length();
                if (et3 == 1) {
                    chequeNumber = chequeNumber.concat(et3_cn.getText().toString());
                    et3_cn.setFocusable(false);
                    et3_cn.setFocusableInTouchMode(false);
                    et3_cn.setClickable(false);
                    // et3_cn.setKeyListener(null);
                    et3_cn.setCursorVisible(false);
                    et3_cn.setPressed(false);
                    et4_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et4_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et4 = et4_cn.getText().length();
                if (et4 == 1) {
                    chequeNumber = chequeNumber.concat(et4_cn.getText().toString());
                    et4_cn.setFocusable(false);
                    et4_cn.setFocusableInTouchMode(false);
                    et4_cn.setClickable(false);
                    // et4_cn.setKeyListener(null);
                    et4_cn.setCursorVisible(false);
                    et4_cn.setPressed(false);
                    et5_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et5_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et5 = et5_cn.getText().length();
                if (et5 == 1) {
                    chequeNumber = chequeNumber.concat(et5_cn.getText().toString());
                    et5_cn.setFocusable(false);
                    et5_cn.setFocusableInTouchMode(false);
                    et5_cn.setClickable(false);
                    // et5_cn.setKeyListener(null);
                    et5_cn.setCursorVisible(false);
                    et5_cn.setPressed(false);
                    et6_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et6_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et6 = et6_cn.getText().length();
                if (et6 == 1) {
                    chequeNumber = chequeNumber.concat(et6_cn.getText().toString());
                    et6_cn.setFocusable(false);
                    et6_cn.setFocusableInTouchMode(false);
                    et6_cn.setClickable(false);
                    //et6_cn.setKeyListener(null);
                    et6_cn.setCursorVisible(false);
                    et6_cn.setPressed(false);
                    et7_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et7_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et7 = et7_cn.getText().length();
                if (et7 == 1) {
                    chequeNumber = chequeNumber.concat(et7_cn.getText().toString());
                    et7_cn.setFocusable(false);
                    et7_cn.setFocusableInTouchMode(false);
                    et7_cn.setClickable(false);
                    //et7_cn.setKeyListener(null);
                    et7_cn.setCursorVisible(false);
                    et7_cn.setPressed(false);
                    et8_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et8_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et8 = et8_cn.getText().length();
                if (et8 == 1) {
                    chequeNumber = chequeNumber.concat(et8_cn.getText().toString());
                    et8_cn.setFocusable(false);
                    et8_cn.setFocusableInTouchMode(false);
                    et8_cn.setClickable(false);
                    // et8_cn.setKeyListener(null);
                    et8_cn.setCursorVisible(false);
                    et8_cn.setPressed(false);
                    et9_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et9_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et9 = et9_cn.getText().length();
                if (et9 == 1) {
                    chequeNumber = chequeNumber.concat(et9_cn.getText().toString());
                    et9_cn.setFocusable(false);
                    et9_cn.setFocusableInTouchMode(false);
                    et9_cn.setClickable(false);
                    //et9_cn.setKeyListener(null);
                    et9_cn.setCursorVisible(false);
                    et9_cn.setPressed(false);
                    et10_cn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et10_cn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer et10 = et10_cn.getText().length();
                if (et10 == 1) {
                    chequeNumber = chequeNumber.concat(et10_cn.getText().toString());
                    et10_cn.setFocusable(false);
                    et10_cn.setFocusableInTouchMode(false);
                    et10_cn.setClickable(false);
                    // et10_cn.setKeyListener(null);
                    et10_cn.setCursorVisible(false);
                    et10_cn.setPressed(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextBankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextBankName.getText().length() > 45) {
                    flagBankError = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Bank Name greater than limit");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // editTextBankName.setText("");

                            dialog.cancel();

                        }
                    });
                    builder.show();
                } else {
                    flagBankError = "No";
                }
            }
        });

        editTextNarration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextNarration.getText().length() > 60) {
                    flagNarration = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage(" Narration greater than limit ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // editTextNarration.setText("");

                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    flagNarration = "No";
                }
            }
        });


        //////////START////////Show calendar and select date ////////
        final Calendar myStartDateCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListenerStartDAte = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myStartDateCalendar.set(Calendar.YEAR, year);
                myStartDateCalendar.set(Calendar.MONTH, month);
                myStartDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                textViewChequeDate.setText(sdf.format(myStartDateCalendar.getTime()));
                chequeDate = String.valueOf(new StringBuilder().append(year).append("-").append(month + 1).append("-").append(dayOfMonth)).trim();
            }
        };

        textViewChequeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SecondActivity.this, datePickerListenerStartDAte, myStartDateCalendar
                        .get(Calendar.YEAR), myStartDateCalendar.get(Calendar.MONTH),
                        myStartDateCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                textViewVoucherDate.setText(sdf.format(myCalendar.getTime()));
                voucherDate = String.valueOf(new StringBuilder().append(year).append("-").append(month + 1).append("-").append(dayOfMonth)).trim();
            }
        };


// ////////END////////Show calendar and select date ////////

//        branchDropDownResponse = branchDropDownResponse.replace("[", "");
//        branchDropDownResponse = branchDropDownResponse.replace("]", "");
//        String[] branch = branchDropDownResponse.split(",");
//
//        spinnerBranch.setOnItemSelectedListener(this);
//        List<String> listBranch = new ArrayList<String>();
//        listBranch.add("Select Agent Branch");
//        for (int i = 0; i < branch.length; i++) {
//
//            listBranch.add(branch[i]);
//        }
//        listBranch.add("All");
//
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listBranch);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerBranch.setAdapter(dataAdapter);


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SecondActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app needs storage permission.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app needs storage permission.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }

                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                    editor.commit();
                } else {
///END/ Run time Permission to write file////in above else block further code vl write ///////

                    makeExcelSheet = "false";

                if (!cd.isConnectingToInternet()) {
                    Toast.makeText(SecondActivity.this, "Please connect to working Internet connection", Toast.LENGTH_LONG).show();
                    return;
                }

//                if (agentBranch.equalsIgnoreCase("Select Agent Branch") || agentBranch.equals("Select Agent Branch") || agentBranch == "Select Agent Branch") {
//                    branchName = customerBranch;
//                } else {
//                    branchName = agentBranch;
//                }
                if (cpPartyCode == "Agent" || cpPartyCode.equalsIgnoreCase("Agent") || cpPartyCode.equals("Agent")) {
                    if (strPartyName.trim().equalsIgnoreCase("Select Party Name") || strPartyName.trim().equals("Select Party Name") || strPartyName == "Select Party Name") {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                        builder.setTitle("Alert");
                        builder.setMessage("Select Any Party Name");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spinnerParty.requestFocus();
                                flagPartyError = "Yes";
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        flagPartyError = "No";
                    }
                }

                if (editTextAmount.getText().length() < 1) {
                    flagAmountError = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Enter some amount");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (flagAmountError.trim().equalsIgnoreCase("Yes") || flagAmountError.trim().equals("Yes") || flagAmountError == "Yes") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Please enter valid amount");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                }


                //  Toast.makeText(SecondActivity.this, "chequeNumber = " + chequeNumber, Toast.LENGTH_LONG).show();
                if (chequeNumber.trim().equalsIgnoreCase("") || chequeNumber.trim().equals("") || chequeNumber == "") {
                    flagChequeError = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Cheque number is not correct");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

                if (textViewChequeDate.getText().length() < 1) {
                    flagVoucherdateError = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Select Voucher Date first");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    flagVoucherdateError = "No";
                }


                if (editTextBankName.getText().length() < 1) {
                    flagBankError = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Enter Bank Name");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (flagBankError.trim().equalsIgnoreCase("Yes") || flagBankError.trim().equals("Yes") || flagBankError == "Yes") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Bank Name greater than limit");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

                if (strDepositBankName.equals("Select Deposit Bank Name") || strDepositBankName == "Select Deposit Bank Name") {
                    flagDepositBankError = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Select Deposit Bank");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    flagDepositBankError = "No";
                }

                if (editTextNarration.getText().length() < 1) {
                    flagNarration = "Yes";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage(" Narration field can not be empty ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (flagNarration.trim().equalsIgnoreCase("Yes") || flagNarration.trim().equals("Yes") || flagNarration == "Yes") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Narration greater than limit ");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    builder.show();
                }


                if (cpPartyCode == "Agent" || cpPartyCode.equalsIgnoreCase("Agent") || cpPartyCode.equals("Agent")) {

                    amount = editTextAmount.getText().toString();
                    // chequeNumber = editTextChequeNo.getText().toString();

                    bankName = editTextBankName.getText().toString();
                    narration = editTextNarration.getText().toString();
                    partyCode = strPartyCode;
                    if (flagPartyError.trim().equalsIgnoreCase("No") && flagAmountError.trim().equalsIgnoreCase("No") && flagChequeError.trim().equalsIgnoreCase("No") && flagVoucherdateError.trim().equalsIgnoreCase("No") && flagBankError.trim().equalsIgnoreCase("No") && flagDepositBankError.trim().equalsIgnoreCase("No") && flagNarration.trim().equalsIgnoreCase("No")) {

                        pbCircular.setVisibility(View.VISIBLE);
                        new agentAsync().execute();
                    }
                } else {
                    amount = editTextAmount.getText().toString();
                    // chequeNumber = editTextChequeNo.getText().toString();
                    bankName = editTextBankName.getText().toString();
                    narration = editTextNarration.getText().toString();
                    partyCode = cpPartyCode;
                    if (flagAmountError.trim().equalsIgnoreCase("No") && flagChequeError.trim().equalsIgnoreCase("No") && flagVoucherdateError.trim().equalsIgnoreCase("No") && flagBankError.trim().equalsIgnoreCase("No") && flagDepositBankError.trim().equalsIgnoreCase("No") && flagNarration.trim().equalsIgnoreCase("No")) {

                        pbCircular.setVisibility(View.VISIBLE);
                        new customerAsync().execute();
                    }
                }
            }

            }
        });


        checkBoxOffline.setChecked(false);
        saveData = "Online";
        checkBoxOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveData = "Offline";
                } else {
                    saveData = "Online";
                }
            }
        });

        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SecondActivity.this, RecordList.class);
                startActivity(intent);
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rec = dbHandler.deleteRecords();
                Toast.makeText(SecondActivity.this, rec, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void abcTransaction() {

        String khuchnai = dbHandler.getTransaction();
        Log.d("RRBBjson", khuchnai);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
//        if (spinner.getId() == R.id.spinner_branch) {
//            agentBranch = parent.getItemAtPosition(position).toString();
//            agentBranch = String.valueOf(agentBranch);

//                new infoAsyncPartyDropDown().execute();

//        }
        if (spinner.getId() == R.id.spinner_party) {
            strPartyName = parent.getItemAtPosition(position).toString();
            strPartyName = String.valueOf(strPartyName);
            strPartyCode = listPartyCode.get(position);
            Log.e("RRBBstrPartyCode", strPartyName);
            Log.e("RRBBstrPartyCode", strPartyCode);

        }
        if (spinner.getId() == R.id.spinner_depositbankname) {
            strDepositBankName = parent.getItemAtPosition(position).toString();
            strDepositBankName = String.valueOf(strDepositBankName);
            strDepositBankId = depositBanksId.get(position);
            // Toast.makeText(SecondActivity.this, strDepositBankId, Toast.LENGTH_LONG).show();
        }

//        if (spinner.getId() == R.id.spinnerfinancialyear){
//            strFinancialYear = parent.getItemAtPosition(position).toString();
//            strFinancialYear = String.valueOf(strFinancialYear);
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    class agentAsync extends AsyncTask<Void, Void, Void> {
        String aResponse;

        @Override
        protected Void doInBackground(Void... voids) {
            //Create Webservice class object
            AgentWebservice agentWebservice = new AgentWebservice();

            //Call Webservice class method and pass values and get response
            //  Log.i("BBRRaResponse", cpCompanyUrl + "" + cpCompanyID + "" + mUserName + "" + mPassword + "" + cpPartyCode);
            aResponse = agentWebservice.getConvertedWeight(cpCompanyUrl, "GetValidUser", cpCompanyID, mUserName, mPassword);
            res = aResponse;

            if (res == "Invalid User" || res.equalsIgnoreCase("Invalid User") || res.equals("Invalid User")) {
                check = "";
                //  startDate = "";
            } else {

                String[] strArray = res.split("%");
                check = strArray[0];
                // startDate = strArray[1];
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            pbCircular.setVisibility(View.INVISIBLE);
            if (res == "Invalid User" || res.equalsIgnoreCase("Invalid User") || res.equals("Invalid User")) {
                Toast.makeText(SecondActivity.this, "Do Registration Again", Toast.LENGTH_LONG).show();
            } else if (check.equals("Valid User")) {

                if (saveData.trim().equalsIgnoreCase("Offline") || saveData.equals("Offline") || saveData == "Offline") {
                    Long check = dbHandler.insertRecord(cpCompanyID, partyCode, amount, chequeNumber, chequeDate, bankName, strDepositBankId, strDepositBankName, narration);
                    // Toast.makeText(SecondActivity.this, "" + check, Toast.LENGTH_LONG).show();
                    editTextAmount.setText("");
                    et1_cn.setText("");
                    et2_cn.setText("");
                    et3_cn.setText("");
                    et4_cn.setText("");
                    et5_cn.setText("");
                    et6_cn.setText("");
                    et7_cn.setText("");
                    et8_cn.setText("");
                    et9_cn.setText("");
                    et10_cn.setText("");
                    chequeNumber = "";
                    et1_cn.setFocusable(true);
                    et1_cn.setFocusableInTouchMode(true);
                    et1_cn.setClickable(true);
                    et1_cn.setCursorVisible(true);
                    et1_cn.setPressed(true);
                    et1_cn.requestFocus();
                    et2_cn.setFocusable(true);
                    et2_cn.setFocusableInTouchMode(true);
                    et2_cn.setClickable(true);
                    et2_cn.setCursorVisible(true);
                    et2_cn.setPressed(true);
                    et3_cn.setFocusable(true);
                    et3_cn.setFocusableInTouchMode(true);
                    et3_cn.setClickable(true);
                    et3_cn.setCursorVisible(true);
                    et3_cn.setPressed(true);
                    et4_cn.setFocusable(true);
                    et4_cn.setFocusableInTouchMode(true);
                    et4_cn.setClickable(true);
                    et4_cn.setCursorVisible(true);
                    et4_cn.setPressed(true);
                    et5_cn.setFocusable(true);
                    et5_cn.setFocusableInTouchMode(true);
                    et5_cn.setClickable(true);
                    et5_cn.setCursorVisible(true);
                    et5_cn.setPressed(true);
                    et6_cn.setFocusable(true);
                    et6_cn.setFocusableInTouchMode(true);
                    et6_cn.setClickable(true);
                    et6_cn.setCursorVisible(true);
                    et6_cn.setPressed(true);
                    et7_cn.setFocusable(true);
                    et7_cn.setFocusableInTouchMode(true);
                    et7_cn.setClickable(true);
                    et7_cn.setCursorVisible(true);
                    et7_cn.setPressed(true);
                    et8_cn.setFocusable(true);
                    et8_cn.setFocusableInTouchMode(true);
                    et8_cn.setClickable(true);
                    et8_cn.setCursorVisible(true);
                    et8_cn.setPressed(true);
                    et9_cn.setFocusable(true);
                    et9_cn.setFocusableInTouchMode(true);
                    et9_cn.setClickable(true);
                    et9_cn.setCursorVisible(true);
                    et9_cn.setPressed(true);
                    et10_cn.setFocusable(true);
                    et10_cn.setFocusableInTouchMode(true);
                    et10_cn.setClickable(true);
                    et10_cn.setCursorVisible(true);
                    et10_cn.setPressed(true);

                    editTextNarration.setText("");
                    textViewChequeDate.setText("");
                    editTextBankName.setText("");

                } else if (saveData.trim().equalsIgnoreCase("Online") || saveData.trim().equals("Online") || saveData == "Online") {
                    new receiptEntryInfoAsync().execute();
                    editTextAmount.setText("");
                    et1_cn.setText("");
                    et2_cn.setText("");
                    et3_cn.setText("");
                    et4_cn.setText("");
                    et5_cn.setText("");
                    et6_cn.setText("");
                    et7_cn.setText("");
                    et8_cn.setText("");
                    et9_cn.setText("");
                    et10_cn.setText("");
                    chequeNumber = "";
                    et1_cn.setFocusable(true);
                    et1_cn.setFocusableInTouchMode(true);
                    et1_cn.setClickable(true);
                    et1_cn.setCursorVisible(true);
                    et1_cn.setPressed(true);
                    et1_cn.requestFocus();
                    et2_cn.setFocusable(true);
                    et2_cn.setFocusableInTouchMode(true);
                    et2_cn.setClickable(true);
                    et2_cn.setCursorVisible(true);
                    et2_cn.setPressed(true);
                    et3_cn.setFocusable(true);
                    et3_cn.setFocusableInTouchMode(true);
                    et3_cn.setClickable(true);
                    et3_cn.setCursorVisible(true);
                    et3_cn.setPressed(true);
                    et4_cn.setFocusable(true);
                    et4_cn.setFocusableInTouchMode(true);
                    et4_cn.setClickable(true);
                    et4_cn.setCursorVisible(true);
                    et4_cn.setPressed(true);
                    et5_cn.setFocusable(true);
                    et5_cn.setFocusableInTouchMode(true);
                    et5_cn.setClickable(true);
                    et5_cn.setCursorVisible(true);
                    et5_cn.setPressed(true);
                    et6_cn.setFocusable(true);
                    et6_cn.setFocusableInTouchMode(true);
                    et6_cn.setClickable(true);
                    et6_cn.setCursorVisible(true);
                    et6_cn.setPressed(true);
                    et7_cn.setFocusable(true);
                    et7_cn.setFocusableInTouchMode(true);
                    et7_cn.setClickable(true);
                    et7_cn.setCursorVisible(true);
                    et7_cn.setPressed(true);
                    et8_cn.setFocusable(true);
                    et8_cn.setFocusableInTouchMode(true);
                    et8_cn.setClickable(true);
                    et8_cn.setCursorVisible(true);
                    et8_cn.setPressed(true);
                    et9_cn.setFocusable(true);
                    et9_cn.setFocusableInTouchMode(true);
                    et9_cn.setClickable(true);
                    et9_cn.setCursorVisible(true);
                    et9_cn.setPressed(true);
                    et10_cn.setFocusable(true);
                    et10_cn.setFocusableInTouchMode(true);
                    et10_cn.setClickable(true);
                    et10_cn.setCursorVisible(true);
                    et10_cn.setPressed(true);
                    editTextNarration.setText("");
                    textViewChequeDate.setText("");
                    editTextBankName.setText("");

                }


            }
        }
    }

    class customerAsync extends AsyncTask<Void, Void, Void> {
        String bResponse;

        @Override
        protected Void doInBackground(Void... voids) {
            //Create Webservice class object
            CustomerWebservice com = new CustomerWebservice();

            //Call Webservice class method and pass values and get response
            bResponse = com.checkValidUsernamePassword(cpCompanyUrl, "wyCheckPartyLogin", cpCompanyID, partyCode, mUserName, mPassword);

            Log.i("AndroidExampleOutput", "----" + bResponse);

            return null;
        }

        protected void onPostExecute(Void result) {
            //Toast.makeText(getApplicationContext(), bResponse, Toast.LENGTH_LONG).show();

            if (bResponse.equals("Successfully Login")) {
                if (saveData.trim().equalsIgnoreCase("Offline") || saveData.equals("Offline") || saveData == "Offline") {
                    Long check = dbHandler.insertRecord(cpCompanyID, partyCode, amount, chequeNumber, chequeDate, bankName, strDepositBankId, strDepositBankName, narration);
                    Toast.makeText(SecondActivity.this, "" + check, Toast.LENGTH_LONG).show();
                    editTextAmount.setText("");

                    et1_cn.setText("");
                    et2_cn.setText("");
                    et3_cn.setText("");
                    et4_cn.setText("");
                    et5_cn.setText("");
                    et6_cn.setText("");
                    et7_cn.setText("");
                    et8_cn.setText("");
                    et9_cn.setText("");
                    et10_cn.setText("");
                    chequeNumber = "";
                    et1_cn.setFocusable(true);
                    et1_cn.setFocusableInTouchMode(true);
                    et1_cn.setClickable(true);
                    et1_cn.setCursorVisible(true);
                    et1_cn.setPressed(true);
                    et1_cn.requestFocus();
                    et2_cn.setFocusable(true);
                    et2_cn.setFocusableInTouchMode(true);
                    et2_cn.setClickable(true);
                    et2_cn.setCursorVisible(true);
                    et2_cn.setPressed(true);
                    et3_cn.setFocusable(true);
                    et3_cn.setFocusableInTouchMode(true);
                    et3_cn.setClickable(true);
                    et3_cn.setCursorVisible(true);
                    et3_cn.setPressed(true);
                    et4_cn.setFocusable(true);
                    et4_cn.setFocusableInTouchMode(true);
                    et4_cn.setClickable(true);
                    et4_cn.setCursorVisible(true);
                    et4_cn.setPressed(true);
                    et5_cn.setFocusable(true);
                    et5_cn.setFocusableInTouchMode(true);
                    et5_cn.setClickable(true);
                    et5_cn.setCursorVisible(true);
                    et5_cn.setPressed(true);
                    et6_cn.setFocusable(true);
                    et6_cn.setFocusableInTouchMode(true);
                    et6_cn.setClickable(true);
                    et6_cn.setCursorVisible(true);
                    et6_cn.setPressed(true);
                    et7_cn.setFocusable(true);
                    et7_cn.setFocusableInTouchMode(true);
                    et7_cn.setClickable(true);
                    et7_cn.setCursorVisible(true);
                    et7_cn.setPressed(true);
                    et8_cn.setFocusable(true);
                    et8_cn.setFocusableInTouchMode(true);
                    et8_cn.setClickable(true);
                    et8_cn.setCursorVisible(true);
                    et8_cn.setPressed(true);
                    et9_cn.setFocusable(true);
                    et9_cn.setFocusableInTouchMode(true);
                    et9_cn.setClickable(true);
                    et9_cn.setCursorVisible(true);
                    et9_cn.setPressed(true);
                    et10_cn.setFocusable(true);
                    et10_cn.setFocusableInTouchMode(true);
                    et10_cn.setClickable(true);
                    et10_cn.setCursorVisible(true);
                    et10_cn.setPressed(true);
                    editTextNarration.setText("");
                    textViewChequeDate.setText("");
                    editTextBankName.setText("");

                } else if (saveData.trim().equalsIgnoreCase("Online") || saveData.trim().equals("Online") || saveData == "Online") {
                    new receiptEntryInfoAsync().execute();
                    editTextAmount.setText("");
                    et1_cn.setText("");
                    et2_cn.setText("");
                    et3_cn.setText("");
                    et4_cn.setText("");
                    et5_cn.setText("");
                    et6_cn.setText("");
                    et7_cn.setText("");
                    et8_cn.setText("");
                    et9_cn.setText("");
                    et10_cn.setText("");
                    chequeNumber = "";
                    et1_cn.setFocusable(true);
                    et1_cn.setFocusableInTouchMode(true);
                    et1_cn.setClickable(true);
                    et1_cn.setCursorVisible(true);
                    et1_cn.setPressed(true);
                    et1_cn.requestFocus();
                    et2_cn.setFocusable(true);
                    et2_cn.setFocusableInTouchMode(true);
                    et2_cn.setClickable(true);
                    et2_cn.setCursorVisible(true);
                    et2_cn.setPressed(true);
                    et3_cn.setFocusable(true);
                    et3_cn.setFocusableInTouchMode(true);
                    et3_cn.setClickable(true);
                    et3_cn.setCursorVisible(true);
                    et3_cn.setPressed(true);
                    et4_cn.setFocusable(true);
                    et4_cn.setFocusableInTouchMode(true);
                    et4_cn.setClickable(true);
                    et4_cn.setCursorVisible(true);
                    et4_cn.setPressed(true);
                    et5_cn.setFocusable(true);
                    et5_cn.setFocusableInTouchMode(true);
                    et5_cn.setClickable(true);
                    et5_cn.setCursorVisible(true);
                    et5_cn.setPressed(true);
                    et6_cn.setFocusable(true);
                    et6_cn.setFocusableInTouchMode(true);
                    et6_cn.setClickable(true);
                    et6_cn.setCursorVisible(true);
                    et6_cn.setPressed(true);
                    et7_cn.setFocusable(true);
                    et7_cn.setFocusableInTouchMode(true);
                    et7_cn.setClickable(true);
                    et7_cn.setCursorVisible(true);
                    et7_cn.setPressed(true);
                    et8_cn.setFocusable(true);
                    et8_cn.setFocusableInTouchMode(true);
                    et8_cn.setClickable(true);
                    et8_cn.setCursorVisible(true);
                    et8_cn.setPressed(true);
                    et9_cn.setFocusable(true);
                    et9_cn.setFocusableInTouchMode(true);
                    et9_cn.setClickable(true);
                    et9_cn.setCursorVisible(true);
                    et9_cn.setPressed(true);
                    et10_cn.setFocusable(true);
                    et10_cn.setFocusableInTouchMode(true);
                    et10_cn.setClickable(true);
                    et10_cn.setCursorVisible(true);
                    et10_cn.setPressed(true);
                    editTextNarration.setText("");
                    textViewChequeDate.setText("");
                    editTextBankName.setText("");
                }

            } else {
                Toast.makeText(SecondActivity.this, "Do Registration Again", Toast.LENGTH_LONG).show();
            }
        }
    }


    class receiptEntryInfoAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ReceiptEntryWebservice receiptEntryWebservice = new ReceiptEntryWebservice();
            aResponse = receiptEntryWebservice.getConvertedWeight(cpCompanyUrl, "Deposit_Detail", cpCompanyID, mUserName,partyCode, amount, chequeNumber, chequeDate, bankName, strDepositBankId, strDepositBankName, narration, cpMobileNo);
            Log.i("RRBBreceiptentry", aResponse);
            return null;
        }

        protected void onPostExecute(Void result) {
            pbCircular.setVisibility(View.INVISIBLE);
            Toast.makeText(SecondActivity.this, "Bill number  =  " + aResponse, Toast.LENGTH_LONG).show();
        }
    }

    class infoAsyncPartyDropDown extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
//            pbCircular.setVisibility(View.VISIBLE);
            PartyDropdownWebservice partyDropdownWebservice = new PartyDropdownWebservice();
            HashMapPartyDropDownResponse = partyDropdownWebservice.getConvertedWeight(cpCompanyUrl, "GETParty", cpCompanyID, mUserName);
//            Gson gson = new Gson();
//            hashMapPartyString = gson.toJson(HashMapPartyDropDownResponse);

            return null;
        }

        protected void onPostExecute(Void result) {
            listPartyCode.clear();
            listPartyName.clear();
            listPartyName.add("Select Party Name");
            listPartyCode.add("0");
            for (Map.Entry<String, String> entry : HashMapPartyDropDownResponse.entrySet()) {
                String keyPartyCode = entry.getKey();
                String valuesPartyName = entry.getValue();
                listPartyCode.add(keyPartyCode);
                listPartyName.add(valuesPartyName);
            }
            ArrayAdapter<String> partyDataAdapter = new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_spinner_item, listPartyName);
            partyDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerParty.setAdapter(partyDataAdapter);


        }
    }


}

