package com.aptoide.amethyst.data_provider.getAds;

import android.content.Context;
import android.text.TextUtils;

import com.aptoide.amethyst.utils.AptoideExecutors;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.utils.ReferrerUtils;
import com.aptoide.amethyst.utils.SimpleFuture;
import com.aptoide.models.ApkSuggestionJson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by neuro on 22-02-2016.
 */
public class GetAdsRequestListener {

	/**
	 * TODO: Permitir broadcast ou não do referrer, embora não faça mal enviar sempre, não faz sentido.
	 */
	public static RequestListener<ApkSuggestionJson> withBroadcast(final Context context, final String packageName, final SpiceManager spiceManager, final
	SimpleFuture<String> simpleFuture, final int tries) {
		return new RequestListener<ApkSuggestionJson>() {

			@Override
			public void onRequestFailure(SpiceException spiceException) {
				Logger.e("InstalledBroadcastReceiver", "getAds onRequestFailure");
			}

			@Override
			public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {

				try {
					if (apkSuggestionJson.getAds().size() > 0) {
						ApkSuggestionJson.Ads ad = apkSuggestionJson.getAds().get(0);

						ReferrerUtils.extractReferrer(ad, spiceManager, simpleFuture, tries);

						AptoideUtils.AdNetworks.knock(ad.getInfo().getCpc_url());
						AptoideUtils.AdNetworks.knock(ad.getInfo().getCpi_url());
						AptoideUtils.AdNetworks.knock(ad.getInfo().getCpd_url());

						AptoideExecutors.getCachedThreadPool().execute(new Runnable() {
							@Override
							public void run() {
								final String referrer = simpleFuture.get();

								if (!TextUtils.isEmpty(referrer)) {
									ReferrerUtils.broadcastReferrer(context, packageName, referrer);
								}
							}
						});
					} else {
						ReferrerUtils.excludedCampaings.remove(packageName);
					}
				} catch (NullPointerException e) {
					// Propositadamente ignorado.
				}
			}
		};
	}
}
