package com.aptoide.models.displayables;

import com.aptoide.models.displayables.AppItem;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class to hold data used to display an AdItem
 *
 * Created by hsousa on 29/09/15.
 */
public class AdItem extends AppItem {

    public String cpcUrl;
    public String cpiUrl;
    public String cpdUrl;
    public String partnerName;
    public String partnerClickUrl;
    public long adId;

    public AdItem(@JsonProperty("BUCKETSIZE") int bucketSize) {
        super(bucketSize);
    }
}