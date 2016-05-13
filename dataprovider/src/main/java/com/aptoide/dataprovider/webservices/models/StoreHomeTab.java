package com.aptoide.dataprovider.webservices.models;

import com.aptoide.dataprovider.webservices.models.v7.GetStore;
import com.aptoide.models.displayables.DisplayableList;

/**
 * This call is to be used on both in the StoresActivity and HomeStoreFragment,
 * where we call the whole getstore instead of only the tabs.
 *
 * The goal is to prevent two network calls in order to open a Store. With only call, we feed
 * both the StoresActivity and HomeStoreFragment.
 *
 * Created by hsousa on 25/08/15.
 */
public class StoreHomeTab {

    /**
     * The list is used on everywhere except StoreActivity (but it is used StoreFragment)
     */
    public DisplayableList list = new DisplayableList();

    /**
     * This object is only used to feed StoreActivity's pager
     */
    public GetStore store;

    /**
     * For endless adapters, we need the offset and total
     */
    public int offset, total;

    /**
     * To detect whether there are hidden adult items
     */
    public int hidden;
}
