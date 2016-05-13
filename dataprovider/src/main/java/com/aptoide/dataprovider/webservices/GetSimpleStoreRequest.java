package com.aptoide.dataprovider.webservices;

import com.aptoide.dataprovider.webservices.models.Api;
import com.aptoide.dataprovider.webservices.models.BulkResponse;
import com.aptoide.models.stores.Login;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import com.aptoide.dataprovider.webservices.interfaces.IGetStoreWebService;

/**
 * Created by hsousa on 14/08/15.
 */
public class GetSimpleStoreRequest extends RetrofitSpiceRequest<BulkResponse.GetStore, IGetStoreWebService> {

    public Login login;
    public String store_name;

    public GetSimpleStoreRequest() {
        super(BulkResponse.GetStore.class, IGetStoreWebService.class);
    }

    @Override
    public BulkResponse.GetStore loadDataFromNetwork() throws Exception {

        Api.GetStore api = new Api.GetStore();
        api.addDataset("meta");
        api.datasets_params = null;
        api.store_name = store_name;
        if (login != null) {
            api.store_user = login.getUsername();
            api.store_pass_sha1 = login.getPassword();
        }
        return getService().checkServer(api);
    }
}
