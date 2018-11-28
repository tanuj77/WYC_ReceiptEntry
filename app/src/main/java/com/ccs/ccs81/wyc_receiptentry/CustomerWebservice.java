package com.ccs.ccs81.wyc_receiptentry;

/**
 * Created by Banke Bihari on 9/14/2016.
 */

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;


public class CustomerWebservice {

    String namespace = "http://tempuri.org/";
    // private String url = "http://103.16.142.124/webserviceGilpin/MobileAppSvc.asmx";
    private String url = null;
    String SOAP_ACTION;
    SoapObject request = null, objMessages = null;
    SoapSerializationEnvelope envelope;
    AndroidHttpTransport androidHttpTransport;

    CustomerWebservice() {
    }


    /**
     * Set Envelope
     */
    protected void SetEnvelope(String wsUrl) {
        url = wsUrl + "MobileAppSvc.asmx";
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
    public String checkValidUsernamePassword(String appUrl, String MethodName, String compID, String custCode, String userName, String password) {

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
            request.addProperty("CompId", "" + compID);
            request.addProperty("BranchName", "");
            request.addProperty("CustCode", "" + custCode);
            request.addProperty("UserName", "" + userName);
            request.addProperty("UserPwd", "" + password);

            SetEnvelope(appUrl);

            try {

                //SOAP calling webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);

                //Got Webservice response
                String result = envelope.getResponse().toString();

                return result;

            } catch (Exception e) {
                // TODO: handle exception
                return e.toString();
            }
        } catch (Exception e) {
            // TODO: handle exception
            return e.toString();
        }

    }

    /************************************/
}