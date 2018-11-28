package com.ccs.ccs81.wyc_receiptentry;

/**
 * Created by ccs81 on 04/05/2017.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.HashMap;

/**
 * Created by Banke Bihari on 9/14/2016.
 */

public class PartyDropdownWebservice {

    String namespace = "http://tempuri.org/";
    // private String url = "http://103.16.142.124/WYAactivityWiseSaleMIS_V1/WSMP_GetValidUser.asmx";
    private String url = null;
    String SOAP_ACTION;
    SoapObject request = null, objMessages = null;
    SoapSerializationEnvelope envelope;
    AndroidHttpTransport androidHttpTransport;
    HashMap<String, String> HashMapParty = new HashMap<String, String>();
    HashMap<String, String> HashMapError = new HashMap<String, String>();

    PartyDropdownWebservice() {
    }

    /**
     * Set Envelope
     */
    protected void SetEnvelope(String appUrl) {
        url = appUrl + "WSMP_GetParty.asmx";
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
    public HashMap<String, String> getConvertedWeight(String webUrl, String MethodName, String compID, String userName) {

        try {
            SOAP_ACTION = namespace + MethodName;

            //Adding values to request object
            request = new SoapObject(namespace, MethodName);

            //Adding Double value to request object
//            PropertyInfo weightProp =new PropertyInfo();
//            weightProp.setName("CompId");
//            weightProp.setValue(compID);
//            weightProp.setType(double.class);
//            request.addProperty(weightProp);

            //Adding String value to request object
            request.addProperty("Compid", compID.trim());
           // request.addProperty("Branch", branch.trim());
            request.addProperty("User", userName.trim());

            SetEnvelope(webUrl);

            try {

                //SOAP calling webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);

                //Got Webservice response
                String result = envelope.getResponse().toString();
                if (result.equals("Invalid User") || result.equalsIgnoreCase("Invalid User") || result == "Invalid User") {

                    // return result;
                } else {

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.optJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        String partyCode = jsonObject1.getString("CODE");
                        String partyName = jsonObject1.getString("NAME");

                        HashMapParty.put(partyCode, partyName);

                    }

                    return HashMapParty;
                }
            } catch (Exception e) {
                // TODO: handle exception
                HashMapError.put("Error", "Check Party DropDownWebservice");
                return HashMapError;
            }
        } catch (Exception e) {
            // TODO: handle exception
            HashMapError.put("Error", "Check Party DropDownWebservice");
            return HashMapError;
        }

        return null;
    }
    /************************************/
}