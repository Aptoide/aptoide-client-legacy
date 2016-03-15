package com.aptoide.amethyst.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.data_provider.getAds.GetAdsRequestListener;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.models.ApkSuggestionJson;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.octo.android.robospice.SpiceManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.webservices.RegisterAdRefererRequest;

/**
 * Created by neuro on 08-10-2015.
 */
public class ReferrerUtils {

    public static final ReferrersMap excludedCampaings = new ReferrersMap();
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void extractReferrer(final WebView[] webview, final AppViewActivity context, final String packageName, final SpiceManager spiceManager, final String click_url, final long downloadId, final long appId, final long adId, final String[] referrerToSet) {
        Logger.d("ExtractReferrer", "Called for: " + click_url);

        final String[] internalClickUrl = {click_url};

        AptoideExecutors.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    internalClickUrl[0] = AptoideUtils.AdNetworks.parseString(context, click_url);
                    Logger.d("ExtractReferrer", "Parsed click_url: " + internalClickUrl[0]);
                } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview[0] = new WebView(context);
                        webview[0].getSettings().setJavaScriptEnabled(true);
                        webview[0].setWebViewClient(new WebViewClient() {

                            Future<Void> future;

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String clickUrl) {

                                if (future == null) {
                                    future = postponeReferrerExtraction(10, false);
                                }

//                        System.out.println("Debug: Refferer: " + clickUrl);

                                if (clickUrl.startsWith("market://") || clickUrl.startsWith("https://play.google.com") || clickUrl.startsWith("http://play" +
                                        ".google.com")) {
//                            System.out.println("Debug: Refferer: Inner: " + clickUrl);
                                    Logger.d("ExtractReferrer", "Clickurl landed on market");
                                    referrerToSet[0] = getReferrer(clickUrl);
                                    Logger.d("ExtractReferrer", "Referrer successfully extracted");
                                    context.getService().setReferrer(downloadId, referrerToSet[0]);

                                    new AptoideDatabase(Aptoide.getDb()).setReferrerToRollbackAction(packageName, referrerToSet[0]);

                                    future.cancel(false);
                                    postponeReferrerExtraction(0, true);

                                    return true;
                                }

                                return false;
                            }

                            private ScheduledFuture<Void> postponeReferrerExtraction(int delta, final boolean success) {
//                        System.out.println("Debug: Referrer: Postponing adSuccess: " + delta + ", " + success);
                                Logger.d("ExtractReferrer", "Referrer postponed " + delta + " seconds.");
                                Callable<Void> callable = new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        Logger.d("ExtractReferrer", "Sending RegisterAdRefererRequest with value " + success);
                                        if (spiceManager.isStarted()) {
                                            spiceManager.execute(new RegisterAdRefererRequest(adId, appId, internalClickUrl[0], success),
                                                    RegisterAdRefererRequest.newDefaultResponse());
                                        }

                                        return null;
                                    }
                                };

                                return executorService.schedule(callable, delta, TimeUnit.SECONDS);
                            }
                        });
                        webview[0].loadUrl(internalClickUrl[0]);
                    }
                });
            }
        });
    }

    public static void extractReferrer(final Context context, final ApkSuggestionJson.Ads ads, final SpiceManager spiceManager, final String click_url, final
    SimpleFuture<String> simpleFuture, final int retries) {

        try {
            final String packageName = ads.getData().getPackageName();
            final long appId = ads.getData().getId().longValue();
            final long adId = ads.getInfo().getAd_id();
            final long networkId = ads.getPartner().getPartnerInfo().getId().longValue();

            Logger.d("ExtractReferrer", "Called for: " + click_url + " with packageName " + packageName);

//			final TTTT tttt = new TTTT(context, packageName, spiceManager, simpleFuture, retries);

            final String[] internalClickUrl = {click_url};
            final SimpleFuture<String> clickUrlFuture = new SimpleFuture<>();

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params;
            params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager
                    .LayoutParams
                    .TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 0;
            params.y = 0;
            params.width = 0;
            params.height = 0;

            LinearLayout view = new LinearLayout(context);
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            AptoideExecutors.getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        internalClickUrl[0] = AptoideUtils.AdNetworks.parseString(context, click_url);
                        clickUrlFuture.set(internalClickUrl[0]);
                        Logger.d("ExtractReferrer", "Parsed click_url: " + internalClickUrl[0]);
                    } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    }
                }
            });
            clickUrlFuture.get();
            WebView wv = new WebView(context);
            wv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            view.addView(wv);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.setWebViewClient(new WebViewClient() {

                Future<Void> future;

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                    if (future == null) {
                        future = postponeReferrerExtraction(3, retries);
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String clickUrl) {

//					if (future == null) {
//						future = postponeReferrerExtraction(3, retries);
//					}

                    if (clickUrl.startsWith("market://") || clickUrl.startsWith("https://play.google.com") || clickUrl.startsWith("http://play" + ".google" +
                            ".com")) {
//						tttt.cancel();
                        Logger.d("ExtractReferrer", "Clickurl landed on market");
                        simpleFuture.set(getReferrer(clickUrl));
                        Logger.d("ExtractReferrer", "Referrer successfully extracted");

                        future.cancel(false);
                        postponeReferrerExtraction(0, true);

                        return true;
                    }

                    return false;
                }

                private ScheduledFuture<Void> postponeReferrerExtraction(int delta, int retries){
                    return postponeReferrerExtraction(delta, false, retries);
                }

                private ScheduledFuture<Void> postponeReferrerExtraction(int delta, boolean success){
                    return postponeReferrerExtraction(delta, success, 0);
                }

                private ScheduledFuture<Void> postponeReferrerExtraction(int delta, final boolean success, final int retries) {
                    Logger.d("ExtractReferrer", "Referrer postponed " + delta + " seconds.");
                    Callable<Void> callable = new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Logger.d("ExtractReferrer", "Sending RegisterAdRefererRequest with value " + success);

                            if (!spiceManager.isStarted()) {
                                spiceManager.start(Aptoide.getContext());
                            }

                            // Por ora fica desactivado pois pode induzir em falsos negativos.

                            spiceManager.execute(new RegisterAdRefererRequest(adId, appId, internalClickUrl[0], success), RegisterAdRefererRequest
                                    .newDefaultResponse());

                            if (!success) {
                                excludedCampaings.add(packageName, networkId);

                                if (retries > 0) {
//								ReferrerUtils.extractReferrer(context, packageName, appId, adId, spiceManager, click_url, simpleFuture, retries - 1);
                                    spiceManager.execute(GetAdsRequest.newDefaultRequest("secondtry", packageName), GetAdsRequestListener.withBroadcast(context, packageName, spiceManager, new SimpleFuture<String>(), retries - 1));
                                }
                            }

                            return null;
                        }
                    };

                    return executorService.schedule(callable, delta, TimeUnit.SECONDS);
                }
            });

            wv.loadUrl(click_url);
//			tttt.post(5000);


            windowManager.addView(view, params);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public static String getReferrer(String uri) {
        List<NameValuePair> params = URLEncodedUtils.parse(URI.create(uri), "UTF-8");

        String referrer = null;
        for (NameValuePair param : params) {

            if (param.getName().equals("referrer")) {
                referrer = param.getValue();
            }
        }
        return referrer;
    }

    public static void broadcastReferrer(Context context, String packageName, String referrer) {
        Intent i = new Intent("com.android.vending.INSTALL_REFERRER");
        i.setPackage(packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        i.putExtra("referrer", referrer);
        context.sendBroadcast(i);
        Logger.d("InstalledBroadcastReceiver", "Sent broadcast to " + packageName + " with referrer " + referrer);
    }
}
