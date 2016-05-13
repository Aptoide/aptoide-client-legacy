package com.aptoide.amethyst.subscription;

import com.aptoide.dataprovider.webservices.models.v2.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruicardoso on 09-03-2016.
 */
public class SubscriptionStatus {
    public String uid;
    public int validity;
    public String status;
    public String sid;
    public String subscriptionTypeId;

    /**
     * Returns if user is subscribed
     * @return true if user is subscribed, false if it isn't or expired
     */
    public boolean isSubscribed(){
        return validity > 0 && status.equals("SUBSCRIBED");
    }
}
