package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import java.util.List;
import java.util.Map;

public class PayProductRequestUnitel extends PayProductRequestBase {
    private String payType;
    private String imsi;

    @Override
    protected void optionsAddExtra(List<WebserviceOptions> options) {
        options.add(new WebserviceOptions("imsi", imsi));
    }

    @Override
    protected void parametersputExtra(Map<String, String> parameters) {
        parameters.put("payType",payType);
        parameters.put("imsi",imsi);
    }

    public void setPayType(String id) {
        this.payType = id;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}
