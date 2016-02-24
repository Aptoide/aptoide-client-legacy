package com.aptoide.amethyst.openiab.webservices.json;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by j-pac on 19-02-2014.
 */
public class IabPurchaseStatusJson {

    
    public String status;

    
    public PublisherResponse publisher_response;

    public String getStatus() {
        return status;
    }

    public PublisherResponse getPublisherResponse() {
        return publisher_response;
    }

    public static class PublisherResponse {

        @JsonProperty("RESPONSE_CODE")
        public int response_code;

        @JsonProperty("INAPP_PURCHASE_ITEM_LIST")
        public ArrayList<String> item;

        @JsonProperty("INAPP_PURCHASE_DATA_LIST")
        public ArrayList<PurchaseDataObject> data;

        @JsonProperty("INAAP_DATA_SIGNATURE_LIST")
        public ArrayList<String> signature;

        public ArrayList<String> getItem() {
            return item;
        }

        public ArrayList<PurchaseDataObject> getData() {
            return data;
        }

        public ArrayList<String> getSignature() {
            return signature;
        }

        public int getResponse_code() {
            return response_code;
        }

        public static class PurchaseDataObject {
             public int orderId;
             public String packageName;
             public String productId;
             public long purchaseTime;
             public String purchaseState;
             public String purchaseToken;
             public String developerPayload;

            public int getOrderId() { return orderId; }

            public String getPackageName() {
                return packageName;
            }

            public String getProductId() {
                return productId;
            }

            public long getPurchaseTime() {
                return purchaseTime;
            }

            public String getPurchaseState() { return purchaseState; }

            public String getPurchaseToken() {
                return purchaseToken;
            }



            public String getJson() {

                Map<String, Object> myJSon = new LinkedHashMap<String, Object>();

                myJSon.put("orderId", orderId);
                myJSon.put("packageName", packageName);
                myJSon.put("productId", productId);
                myJSon.put("purchaseTime", purchaseTime);
                myJSon.put("purchaseState", purchaseState);
                myJSon.put("purchaseToken", purchaseToken);
                if(developerPayload != null) myJSon.put("developerPayload", developerPayload);


                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                String json = null;
                try {
                    json = mapper.writeValueAsString(myJSon);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                Log.d("AptoideJson", json);

                return json;
            }
        }
    }
}
