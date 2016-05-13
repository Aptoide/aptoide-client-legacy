package com.aptoide.models;

/**
 * Created by hsousa on 14-10-2015.
 */
public interface IHasMore {

    String getLabel();
    String getTag();
    boolean isHasMore();
    String getEventActionUrl();
    String getEventType();
    String getEventName();
    String getLayout();
    boolean getHomepage();
    long getStoreId();

}
