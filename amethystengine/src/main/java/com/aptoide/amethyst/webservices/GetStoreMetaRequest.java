package com.aptoide.amethyst.webservices;

import com.aptoide.dataprovider.webservices.models.BulkResponse;
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
        @GET("/ws2.aptoide.com/api/7/getStoreMeta/store_id/{storeId}")
        BulkResponse.GetStore.StoreMeta getStore(@Path("storeId") long storeId);
    }
}
