package com.ccs.ccs81.wyc_receiptentry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

/**
 * Created by Banke Bihari on 10/1/2016.
 */
public class ReceiptEntryWebservice {

    String namespace = "http://tempuri.org/";
    // private String url = "http://103.16.142.124/WinYatra_GetArlnBsFareYqTaxBrnId_PUBLISH_FINAL_Json_V2/ArlnBsFareYqTaxBrnId.asmx";
    // private String url = "http://103.16.142.124/WYAactivityBySales_V1/WS_GetSales.asmx";
    private String url = null;
    String SOAP_ACTION;
    SoapObject request = null, objMessages = null;
    SoapSerializationEnvelope envelope;
    AndroidHttpTransport androidHttpTransport;

    ReceiptEntryWebservice() {
    }


    /**
     * Set Envelope
     */
    protected void SetEnvelope(String appUrl) {
        url = appUrl + "WY_Deposit_Details.asmx";
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
    public String getConvertedWeight(String webUrl, String MethodName, String companyID,String userId, String partyCode, String amount, String chequeNumber, String chequeDate,String bankName,String depositBankId,String depositBankName,String narration,String mobileNo) {

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

           request.addProperty("CompId", "" + companyID.trim());
            request.addProperty("user",""+userId.trim());
            request.addProperty("PCode", "" + partyCode.trim());
            request.addProperty("Amount", "" + amount.trim());
            request.addProperty("ChequeNo", "" + chequeNumber.trim());
            request.addProperty("ChequeDate", "" + chequeDate.trim());
            request.addProperty("Narration", "" + narration.trim());
          //  request.addProperty("dtEndDt", "" + voucherDate.trim());
           request.addProperty("BankFrom", "" + bankName.trim());
           request.addProperty("DepositedIn", "" + depositBankName.trim());
            request.addProperty("BankCode", "" + depositBankId.trim());


//            request.addProperty("Mob", "" + mobileNo.trim());

            SetEnvelope(webUrl);

            try {

                //SOAP calling webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);

                //Got Webservice response
                String result = envelope.getResponse().toString();

//http://stackoverflow.com/questions/28133531/how-to-store-and-parse-soap-object-response
//http://stackoverflow.com/questions/32969039/getting-values-from-soap-xml-response
                //    String result = "{'table': [{'AIRLINE':'098','BASFARE':'90'},{'AIRLINE':'98','BASFARE':'90'}]}";
                if (result.equals("No Data Found") || result.equalsIgnoreCase("No Data Found") || result == "No Data Found") {

                    return result;
                } else if (result.equalsIgnoreCase("Party doesn't belongs to this branch") || result.equals("Party doesn't belongs to this branch") || result == "Party doesn't belongs to this branch") {
                    return result;
                } else {

                    return result;
                }

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
