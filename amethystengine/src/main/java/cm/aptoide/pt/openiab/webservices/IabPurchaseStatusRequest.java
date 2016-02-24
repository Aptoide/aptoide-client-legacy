package cm.aptoide.pt.openiab.webservices;



import java.util.HashMap;

import cm.aptoide.pt.openiab.webservices.json.IabPurchaseStatusJson;

public class IabPurchaseStatusRequest extends BasePurchaseStatusRequest {

    @Override
    IabPurchaseStatusJson executeRequest(Webservice webervice, HashMap<String, String> parameters) {
        return webervice.processInAppBilling(parameters);
    }

    String getReqType(){
        return "iabpurchasestatus";
    }
}
