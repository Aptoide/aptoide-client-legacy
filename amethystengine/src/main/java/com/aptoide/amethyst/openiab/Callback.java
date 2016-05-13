package com.aptoide.amethyst.openiab;

/**
 * Created by rmateus on 15-07-2014.
 */
public interface Callback {
    public void onClick(int payType, String imsi, String price, String currency);

    public void onCancel();
}
