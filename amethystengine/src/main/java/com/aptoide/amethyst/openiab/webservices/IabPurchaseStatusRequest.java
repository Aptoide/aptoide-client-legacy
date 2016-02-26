package com.aptoide.amethyst.openiab.webservices;



import java.util.HashMap;

import com.aptoide.amethyst.openiab.webservices.json.IabPurchaseStatusJson;

public class IabPurchaseStatusRequest extends BasePurchaseStatusRequest {

    @Override
    IabPurchaseStatusJson executeRequest(Webservice webervice, HashMap<String, String> parameters) {
        return webervice.processInAppBilling(parameters);
    }

    String getReqType(){
        return "iabpurchasestatus";
    }
}
