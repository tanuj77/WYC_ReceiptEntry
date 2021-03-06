package com.ccs.ccs81.wyc_receiptentry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ccs81 on 18/09/2017.
 */

public class BankDropDownWebservice {
    String namespace = "http://tempuri.org/";
    // private String url = "http://103.16.142.124/WYAactivityWiseSaleMIS_V1/WSMP_GetValidUser.asmx";
    private String url = null;
    String SOAP_ACTION;
    SoapObject request = null, objMessages = null;
    SoapSerializationEnvelope envelope;
    AndroidHttpTransport androidHttpTransport;
HashMap<String,String> HashMapDepositBank = new HashMap<String, String>();
    HashMap<String,String> HashMapError = new HashMap<String, String>();
    private List<String> dropDownList = new ArrayList<>();

    BankDropDownWebservice() { }
    /**
     * Set Envelope
     */
    protected void SetEnvelope(String appUrl) {
        url = appUrl+"WSMP_BankDetails.asmx";
        try {
            // Creating SOAP envelope
            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            //You can comment that line if your web service is not .NET one.
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            androidHttpTransport = new AndroidHttpTransport(url);
            androidHttpTransport.debug = true;

        } catch (Exception e) {
            System.out.println("Soap Exception---->>>" + e.toString());
        }
    }

    // MethodName variable is define for which webservice function  will call
    public HashMap<String, String> getConvertedWeight(String webUrl, String MethodName, String compID, String method)
    {

        try {
            SOAP_ACTION = namespace + MethodName;

            //Adding values to request object
            request = new SoapObject(namespace, MethodName);
            request.addProperty("CompId", compID);

            SetEnvelope(webUrl);

            try {
                //SOAP calling webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);

                //Got Webservice response
                String result = envelope.getResponse().toString();
                if (result.equals("Invalid User") || result.equalsIgnoreCase("Invalid User") || result == "Invalid User"){

                    //return result;
                }else {

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        if (method.equalsIgnoreCase("Bank") || method.equals("Bank") || method =="Bank") {
                            String depositBankId = jsonObject1.getString("Bankid");
                            String depositBankName = jsonObject1.getString("BankName");
                            HashMapDepositBank.put(depositBankId,depositBankName);
                        }

                    }

                    return HashMapDepositBank;
                }
            } catch (Exception e) {
                // TODO: handle exception
                HashMapError.put("Error","Check BankDropDownWebservice");
                return HashMapError;
            }
        } catch (Exception e) {
            // TODO: handle exception
            HashMapError.put("Error","Check BankDropDownWebservice");
            return HashMapError;
        }

        return null;
    }

    /************************************/

}
