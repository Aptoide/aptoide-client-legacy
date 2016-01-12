package com.aptoide.amethyst.webservices.listeners;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.models.BulkResponse;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Request a Store info and on success, saves it to database and call RepoAddedEvent BusProvider
 * <p>
 * Created by hsousa on 14/08/15.
 */
public class CheckSimpleStoreListener implements RequestListener<BulkResponse.GetStore> {

    public Callback callback;

    public interface Callback {
        void onSuccess();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
    }

    @Override
    public void onRequestSuccess(BulkResponse.GetStore response) {

        final Store store = new Store();
        try {
            BulkResponse.GetStore.StoreMetaData data = response.datasets.meta.data;
            store.setId(data.id.longValue());
            store.setName(response.datasets.meta.data.name);
            store.setDownloads(response.datasets.meta.data.downloads.intValue() + "");

            String sizeString = IconSizeUtils.generateSizeStringAvatar(Aptoide.getContext());

            String avatar = data.avatar;

            if (avatar != null) {
                String[] splitUrl = avatar.split("\\.(?=[^\\.]+$)");
                avatar = splitUrl[0] + "_" + sizeString + "." + splitUrl[1];
            }

            store.setAvatar(avatar);
            store.setDescription(data.description);
            store.setTheme(data.theme);
            store.setView(data.view);
            store.setBaseUrl(data.name);

            AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());


            long l = database.insertStore(store);
            database.updateStore(store, l);

            BusProvider.getInstance().post(new OttoEvents.RepoAddedEvent());
            if (callback != null) {
                callback.onSuccess();
            }
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String s = mapper.writeValueAsString(response);
                Crashlytics.logException(new Throwable(s, e));
            } catch (JsonProcessingException e1) {
                Logger.printException(e);
            }
        }
    }
}