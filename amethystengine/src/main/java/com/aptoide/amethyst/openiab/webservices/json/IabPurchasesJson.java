package com.aptoide.amethyst.openiab.webservices.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by j-pac on 19-02-2014.
 */
public class    IabPurchasesJson {


    public String status;


    public PublisherResponse publisher_response;

    public String getStatus() { return status; }

    public PublisherResponse getPublisher_response() {
        return publisher_response;
    }

    public static class PublisherResponse {
        @JsonProperty("INAPP_PURCHASE_ITEM_LIST") public List<String> itemList;

        @JsonProperty("INAPP_PURCHASE_DATA_LIST") public List<PurchaseDataObject> purchaseDataList;

        @JsonProperty("INAAP_DATA_SIGNATURE_LIST") public List<String> signatureList;

        @JsonProperty("INAPP_CONTINUATION_TOKEN") public String inapp_continuation_token;


        public List<String> getItemList() {
            return itemList;
        }

        public List<PurchaseDataObject> getPurchaseDataList() { return purchaseDataList; }

        public List<String> getSignatureList() {
            return signatureList;
        }

        public String getInapp_continuation_token() {
            return inapp_continuation_token;
        }

        public static class PurchaseDataObject {
             public int orderId;
             public String packageName;
             public String productId;
             public long purchaseTime;
             public String purchaseState;
             public String developerPayload;
             public String token;
             public String purchaseToken;

            public int getOrderId() { return orderId; }

            public String getPackageName() {
                return packageName;
            }

     

            public String getToken() {
                return token;
            }

            public String getJson() {

                Map<String, Object> myJson = new LinkedHashMap<String, Object>();

                myJson.put("orderId", orderId);
                myJson.put("packageName", packageName);
                myJson.put("productId", productId);
                myJson.put("purchaseTime", purchaseTime);
                myJson.put("purchaseToken", purchaseToken);
                if(developerPayload != null) myJson.put("developerPayload", developerPayload);

                ObjectMapper mapper = new ObjectMapper();

                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

                try {
                    return mapper.writeValueAsString(myJson);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }
}
