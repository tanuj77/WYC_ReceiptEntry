package com.ccs.ccs81.wyc_receiptentry;

/**
 * Created by Banke Bihari on 21/11/2016.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomListCustomer extends ArrayAdapter<String> {
    private final Activity context;
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

    public CustomListCustomer(Activity context, List<String> listCompanyName, List<String> listPartyCode, List<String> listAmount, List<String> listChequeNo, List<String> listVoucherDate, List<String> listBankName,List<String> listDepositBankId ,List<String> listDepositBankName, List<String> listNarration,List<String> listVoucherNumber) {
        super(context, R.layout.activity_customerlist, listCompanyName);
        this.context = context;
        this.listCompanyName = listCompanyName;
       // this.listBranchName = listBranchName;
        this.listPartyCode = listPartyCode;
        this.listAmount = listAmount;
        this.listChequeNo=listChequeNo;
        this.listVoucherDate = listVoucherDate;
        this.listBankName = listBankName;
        this.listDepositBankId = listDepositBankId;
        this.listDepositBankName = listDepositBankName;
        this.listNarration = listNarration;
        this.listVoucherNumber = listVoucherNumber;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(R.layout.activity_customerlist, null, true);


        TextView tvCompanyName = (TextView) convertView.findViewById(R.id.tv_companyname);
       // TextView tvBranchName = (TextView) convertView.findViewById(R.id.tv_branchname);
        TextView tvPartyCode = (TextView) convertView.findViewById(R.id.tv_partycode);
        TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_amount);
        TextView tvChequeNo = (TextView) convertView.findViewById(R.id.tv_chequenumber);
        TextView tvVoucherDate = (TextView) convertView.findViewById(R.id.tv_voucherdate);
        TextView tvBankName = (TextView) convertView.findViewById(R.id.tv_bankname);
        TextView tvDepositBankName = (TextView) convertView.findViewById(R.id.tv_depositbankname);
        TextView tvNarration = (TextView) convertView.findViewById(R.id.tv_narration);
        TextView tvVoucherNumber  = (TextView) convertView.findViewById(R.id.tv_vouchernumber);

        tvCompanyName.setText(listCompanyName.get(position));
        //tvBranchName.setText(listBranchName.get(position));
        tvPartyCode.setText(listPartyCode.get(position));
        tvAmount.setText(listAmount.get(position));
        tvChequeNo.setText(listChequeNo.get(position));
        tvVoucherDate.setText(listVoucherDate.get(position));
        tvBankName.setText(listBankName.get(position));
        tvDepositBankName.setText(listDepositBankName.get(position));
        tvNarration.setText(listNarration.get(position));
        tvVoucherNumber.setText(listVoucherNumber.get(position));

        return convertView;

    }
//    public void updateAdapter(List<String> listVoucherNumber) {
//        this.listVoucherNumber= listVoucherNumber;
//
//        //and call notifyDataSetChanged
//        notifyDataSetChanged();
//    }
}