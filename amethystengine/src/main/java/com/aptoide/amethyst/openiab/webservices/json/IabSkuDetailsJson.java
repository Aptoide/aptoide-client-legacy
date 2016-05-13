package com.aptoide.amethyst.openiab.webservices.json;

import com.aptoide.models.PaymentServices;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by j-pac on 19-02-2014.
 */
public class IabSkuDetailsJson {


    public String status;

    public Metadata getMetadata() {
        return metadata;
    }


    public Metadata metadata;


    public PublisherResponse publisher_response;


    public ArrayList<PaymentServices> payment_services;

    public String getStatus() {
        return status;
    }

    public PublisherResponse getPublisher_response() {
        return publisher_response;
    }

    public ArrayList<PaymentServices> getPayment_services() {
        return payment_services;
    }





    public static class PublisherResponse {

        @JsonProperty("DETAILS_LIST")
        public ArrayList<PurchaseDataObject> details_slist;

        public ArrayList<PurchaseDataObject> getDetailss_list() { return details_slist; }


    }

    public static class PurchaseDataObject {

        public String productId;
        public String sku;
        public String price;
        public String title;
        public String description;
        public String developerPayload;





        public String getPrice() {
            return price;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getSku() {
            return sku;
        }

        public String getProductId() {
            return productId;
        }




    }



}
