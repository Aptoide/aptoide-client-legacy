package com.aptoide.amethyst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.insights.ABTestClient;
import com.amazon.insights.AmazonInsights;
import com.amazon.insights.Event;
import com.amazon.insights.EventClient;
import com.amazon.insights.InsightsCallback;
import com.amazon.insights.InsightsCredentials;
import com.amazon.insights.Variation;
import com.amazon.insights.VariationSet;
import com.amazon.insights.error.InsightsError;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.utils.ReferrerUtils;
import com.aptoide.amethyst.utils.SimpleFuture;
import com.aptoide.amethyst.webservices.json.GetApkInfoJson;
import com.aptoide.amethyst.webservices.v2.GetAdsRequest;
import com.aptoide.models.ApkSuggestionJson;
import com.bumptech.glide.Glide;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.aptoide.amethyst.webservices.GetApkInfoRequestFromId;

import static com.aptoide.dataprovider.webservices.models.Constants.AD_ID_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.APPNAME_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.APP_ID_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.CPC_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.CPI_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.DOWNLOAD_FROM_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.FROM_SPONSORED_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.KEYWORD_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.LOCATION_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.PACKAGENAME_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.PARTNER_CLICK_URL_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.PARTNER_EXTRA;
import static com.aptoide.dataprovider.webservices.models.Constants.PARTNER_TYPE_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.STOREID_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.WHERE_FROM_KEY;

/**
 * Created by neuro on 01-10-2015.
 */
public class AppViewMiddleSuggested {

    List<View.OnClickListener> buttonListeners = new ArrayList<>(3);
    List<String> installButtonLabel = new ArrayList<>(3);

    String[] referrer = new String[1];
    final SpiceManager spiceManager;

    // Variável de controlo para coordenação entre webservice e amazon responses. Quando a true, é suposto desenhar a view.
    boolean control = false;
    AppViewActivity context;
    // Temp
    //
    public static EventClient eventClient;
    int middleAppViewVariant = 1;

    // Add helpers
    private String click_url;
    private long id;
    private long adId;


    private void onLoadMiddleAppViewEvent(AmazonInsights insightsInstance) {

        ABTestClient abClient = insightsInstance.getABTestClient();
        eventClient = insightsInstance.getEventClient();

        // Allocate/obtain variations for cm.aptoide.pt
        abClient.getVariations("cm.aptoide.pt")
                .setCallback(new InsightsCallback<VariationSet>() {
                    @Override
                    public void onComplete(VariationSet variations) {

                        final Variation variation = variations.getVariation("cm.aptoide.pt");
                        middleAppViewVariant = variation.getVariableAsInt("Middle App View Behavior", 3);

                        Logger.d("amazonABVariant", Integer.toString(middleAppViewVariant));

                        informInfoReady();
                    }

                    @Override
                    public void onError(final InsightsError error) {

                        // base class will record the error to log cat
                        super.onError(error);

                        // do any additional handling (if needed) if insights could not allocate any variations
                    }

                });

        // Create a visit event when the user starts Middle App View Event.
        Event middleAppViewVariantStart = eventClient.createEvent("Middle App View Event");

        // Record the visit event.
        eventClient.recordEvent(middleAppViewVariantStart);
        eventClient.submitEvents();
    }

    private void informInfoReady() {
        if (control) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO find memory leak
                    if (view == null) {
                        return;
                    }
                    View button = view.findViewById(R.id.btinstall);
                    // TODO find memory leak
                    if (button == null) {
                        return;
                    }
                    button.setOnClickListener(buttonListeners.get(middleAppViewVariant - 1));
                    ((Button) button).setText(installButtonLabel.get(middleAppViewVariant - 1));
                    button.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                }
            });
        } else {
            control = true;
        }
    }

    View view;

    public AppViewMiddleSuggested(final AppViewActivity context, final View view, final SpiceManager spiceManager, long appId, final String packageName, List<String> keywords) {

        this.context = context;
        this.view = view;
        this.spiceManager = spiceManager;

        // Apagar, amazon
        InsightsCredentials credentials = AmazonInsights.newCredentials(
                BuildConfig.AMAZON_PUBLIC_KEY, BuildConfig.AMAZON_PRIVATE_KEY);

        // Initialize a new instance of AmazonInsights specifically for your application.
        // The AmazonInsights library requires the Android context in order to access
        // Android services (i.e. SharedPrefs, etc)
        AmazonInsights insightsInstance = AmazonInsights.newInstance(credentials, context.getApplicationContext());

        onLoadMiddleAppViewEvent(insightsInstance);

        GetAdsRequest getAdsRequest = new GetAdsRequest(packageName, true);
        getAdsRequest.setLocation("middleappview");
        getAdsRequest.setKeyword(AptoideUtils.StringUtils.join(keywords, ",") + "," + "__null__");
        getAdsRequest.setLimit(1);

        spiceManager.execute(getAdsRequest, Long.toString(appId), 10 * 60 * 1000, new RequestListener<ApkSuggestionJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
            }

            @Override
            public void onRequestSuccess(final ApkSuggestionJson apkSuggestionJson) {
                try {

                    if (apkSuggestionJson.getAds().size() == 0) {
                        return;
                    }
                    if (apkSuggestionJson.getAds().get(0).getData().getPackageName().equals(packageName)) {
                        return;
                    }
                    if (apkSuggestionJson.getAds().get(0).getPartner() == null) {
                        return;
                    }
                    if (apkSuggestionJson.getAds().get(0).getPartner().getPartnerData() == null) {
                        return;
                    }

                    click_url = apkSuggestionJson.getAds().get(0).getPartner().getPartnerData().getClick_url();
                    String name = apkSuggestionJson.getAds().get(0).getData().getName();
                    String developer = apkSuggestionJson.getAds().get(0).getData().developer;
                    float size1 = apkSuggestionJson.getAds().get(0).getData().getSize().floatValue() / 1024 / 1024;
                    String description1 = apkSuggestionJson.getAds().get(0).getData().description;
                    final String repo = apkSuggestionJson.getAds().get(0).getData().getRepo();
                    final String adPackageName = apkSuggestionJson.getAds().get(0).getData().getPackageName();
                    id = apkSuggestionJson.getAds().get(0).getData().getId().longValue();
                    adId = apkSuggestionJson.getAds().get(0).getInfo().getAd_id();
                    float rating = apkSuggestionJson.getAds().get(0).getData().getStars().floatValue();
                    String cpc = apkSuggestionJson.getAds().get(0).getInfo().getCpc_url();
                    String cpi = apkSuggestionJson.getAds().get(0).getInfo().getCpi_url();

                    prepareABTestingVariables(context, name, apkSuggestionJson);

                    DecimalFormat df = new DecimalFormat("0.00");

                    ((TextView) view.findViewById(R.id.app_name)).setText(name);
                    ((TextView) view.findViewById(R.id.size_value)).setText(df.format(size1) + " MB");
                    ((TextView) view.findViewById(R.id.description)).setText(description1);
                    ((RatingBar) view.findViewById(R.id.rating_label)).setRating(rating);

                    ImageView imageView = (ImageView) view.findViewById(R.id.app_icon);
                    String imagePath = apkSuggestionJson.getAds().get(0).getData().getIcon();

                    imageView.setImageDrawable(null);
                    Glide.with(context).load(imagePath).into(imageView);

                    String md5 = apkSuggestionJson.getAds().get(0).getData().getMd5sum();

                    setListener(apkSuggestionJson);

                    informInfoReady();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            private void setListener(final ApkSuggestionJson apkSuggestionJson) {
                View relativeLayout = view.findViewById(R.id.apkinfoheader);
                relativeLayout.setClickable(true);
                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(context, AppViewActivity.class);

                        fillAppViewActivityIntent(i, apkSuggestionJson);

                        context.startActivity(i);

                    }
                });
            }
        });

    }

    private void fillAppViewActivityIntent(Intent i, ApkSuggestionJson apkSuggestionJson) {
        i.putExtra(APP_ID_KEY, apkSuggestionJson.getAds().get(0).getData().getId().longValue());
        i.putExtra(APPNAME_KEY, apkSuggestionJson.getAds().get(0).getData().getName());
        i.putExtra(AD_ID_KEY, apkSuggestionJson.getAds().get(0).getInfo().getAd_id());
        i.putExtra(PACKAGENAME_KEY, apkSuggestionJson.getAds().get(0).getData().getPackageName());
        i.putExtra(STOREID_KEY, apkSuggestionJson.getAds().get(0).getData().getRepo());
        i.putExtra(FROM_SPONSORED_KEY, true);
        i.putExtra(LOCATION_KEY, "middleappview");
        i.putExtra(KEYWORD_KEY, "__NULL__");
        i.putExtra(CPC_KEY, apkSuggestionJson.getAds().get(0).getInfo().getCpc_url());
        i.putExtra(CPI_KEY, apkSuggestionJson.getAds().get(0).getInfo().getCpi_url());
        i.putExtra(WHERE_FROM_KEY, "sponsored");
        i.putExtra(DOWNLOAD_FROM_KEY, "middle_app_view");

        if (apkSuggestionJson.getAds().get(0).getPartner() != null) {
            Bundle bundle = new Bundle();

            bundle.putString(PARTNER_TYPE_KEY, apkSuggestionJson.getAds().get(0).getPartner().getPartnerInfo().getName());
            bundle.putString(PARTNER_CLICK_URL_KEY, apkSuggestionJson.getAds().get(0).getPartner().getPartnerData().getClick_url());

            i.putExtra(PARTNER_EXTRA, bundle);
        }
    }

    private void prepareABTestingVariables(final AppViewActivity context, final String label, final ApkSuggestionJson apkSuggestionJson) {

        // Use Cases:
        //
        // 1 - Muda para AppViewActivity apenas.
        // 2 - Muda para AppViewActivity e inicia o download automáticamente.
        // 3 - Inicia o download em background sem sair da AppViewActivity.

        View.OnClickListener onClickListener1 = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AppViewActivity.class);

                AppViewMiddleSuggested.this.fillAppViewActivityIntent(i, apkSuggestionJson);
                new AptoideDatabase(Aptoide.getDb()).addToAmazonABTesting(apkSuggestionJson.getAds().get(0).getData().packageName);

                context.startActivity(i);
            }
        };

        View.OnClickListener onClickListener2 = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AppViewActivity.class);

                AppViewMiddleSuggested.this.fillAppViewActivityIntent(i, apkSuggestionJson);

                i.putExtra("forceAutoDownload", true);
                new AptoideDatabase(Aptoide.getDb()).addToAmazonABTesting(apkSuggestionJson.getAds().get(0).getData().packageName);

                context.startActivity(i);
            }
        };

        View.OnClickListener onClickListener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int md5sumHash = apkSuggestionJson.getAds().get(0).getData().getMd5sum().hashCode();
                        String packageName = apkSuggestionJson.getAds().get(0).getData().getPackageName();

                        AptoideUtils.AdNetworks.knock(apkSuggestionJson.getAds().get(0).getInfo().getCpc_url());
                        AptoideUtils.AdNetworks.knock(apkSuggestionJson.getAds().get(0).getInfo().getCpd_url());

                        ReferrerUtils.extractReferrer(apkSuggestionJson.getAds().get(0), spiceManager);

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.findViewById(R.id.btinstall).setEnabled(false);
                            }
                        });

                        GetApkInfoRequestFromId getApkInfoRequestFromId = new GetApkInfoRequestFromId(context);
                        getApkInfoRequestFromId.setAppId(Long.toString(id));

                        spiceManager.execute(getApkInfoRequestFromId, new RequestListener<GetApkInfoJson>() {
                            @Override
                            public void onRequestFailure(SpiceException spiceException) {
                                try {
                                    // on a try/catch block because the Activity may not be already visible
                                    Toast.makeText(context, "Error retrieving apk info.", Toast.LENGTH_SHORT).show();
                                } catch (Exception ignored) {}
                            }

                            @Override
                            public void onRequestSuccess(GetApkInfoJson getApkInfoJson) {

                                if (!getApkInfoJson.getStatus().equals("OK")) {
                                    return;
                                }

                                Download download = new Download();

                                GetApkInfoJson.Apk apk = getApkInfoJson.getApk();

                                download.setId(apk.getId().longValue());
                                download.setName(label);
                                download.setVersion(apk.getVername());
                                download.setIcon(apk.getIcon());
                                download.setPackageName(apk.packageName);
                                download.setMd5(apk.getMd5sum());

                                download.setCpiUrl(apkSuggestionJson.getAds().get(0).getInfo().getCpi_url());

                                download.setReferrer(referrer[0]);

                                 context.getService().startDownloadFromJson(getApkInfoJson, id, download);

                            }
                        });
                        new AptoideDatabase(Aptoide.getDb()).addToAmazonABTesting(apkSuggestionJson.getAds().get(0).getData().packageName);
                    }
                }).start();

            }
        };
        buttonListeners.add(onClickListener1);
        buttonListeners.add(onClickListener2);
        buttonListeners.add(onClickListener3);

        // Button Label
        installButtonLabel.add(context.getString(R.string.abTest1_label_1));
        installButtonLabel.add(context.getString(R.string.abTest1_label_2));
        installButtonLabel.add(context.getString(R.string.abTest1_label_3));
    }
}
