package com.aptoide.amethyst.openiab.webservices;

import com.aptoide.dataprovider.webservices.models.WebserviceOptions;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PayProductRequestPayPal extends PayProductRequestBase {
    private String correlationId;
    private String simCountryCode;

    @Override
    protected void optionsAddExtra(List<WebserviceOptions> options) {
        if(simCountryCode!=null)
            options.add(new WebserviceOptions("simcc", simCountryCode.toUpperCase(Locale.ENGLISH)));
        options.add(new WebserviceOptions("correlationID", correlationId));
    }
    @Override
    protected void parametersputExtra(Map<String, String> parameters) {
        parameters.put("correlationID",correlationId);
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    public void setSimCountryCode(String simCountryCode) {
        this.simCountryCode = simCountryCode;
    }
}
