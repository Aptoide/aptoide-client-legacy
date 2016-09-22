package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.dataprovider.webservices.models.BulkResponse;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Classe para pedidos de retorno de uma store indicando um id.
 * É necessário refactorizar o modelo de dados para a v7.
 *
 * Created by hsousa on 08/08/15.
 */
public class GetStoreMetaRequest extends RetrofitSpiceRequest<BulkResponse.GetStore.StoreMeta, GetStoreMetaRequest.StoreMetaWebservice>{

    private final long storeId;

    public GetStoreMetaRequest(long storeId) {
        super(BulkResponse.GetStore.StoreMeta.class, StoreMetaWebservice.class);

        this.storeId = storeId;
    }

    @Override
    public BulkResponse.GetStore.StoreMeta loadDataFromNetwork() throws Exception {
        BulkResponse.GetStore.StoreMeta store = getService().getStore(storeId);
        return store;
    }

    public interface StoreMetaWebservice{
        @GET(Defaults.BASE_V7_URL+"/getStoreMeta/store_id/{storeId}")
        BulkResponse.GetStore.StoreMeta getStore(@Path("storeId") long storeId);
    }
}
