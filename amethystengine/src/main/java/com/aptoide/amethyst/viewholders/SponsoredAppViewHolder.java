package com.aptoide.amethyst.viewholders;

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.SponsoredSearchApp;
import com.bumptech.glide.Glide;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import static com.aptoide.dataprovider.webservices.models.Constants.STORENAME_KEY;
import static com.aptoide.dataprovider.webservices.models.Constants.WHERE_FROM_KEY;

/**
 * Created by neuro on 05-11-2015.
 */
public class SponsoredAppViewHolder extends BaseViewHolder {

    private TextView name;
    private ImageView icon;
    private TextView description;
    private TextView store;
    private TextView downloads;
    private TextView versionName;

    public SponsoredAppViewHolder(View view, int viewType) {
        super(view, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
        final SponsoredSearchApp appItem = (SponsoredSearchApp) displayable;

        name.setText(appItem.getName());
        versionName.setText(appItem.getVersionName());
        downloads.setText(AptoideUtils.StringUtils.withSuffix(appItem.getDownloads().longValue()));
        description.setText(Html.fromHtml(appItem.getDescription()));
        store.setText(appItem.getRepo());
        Glide.with(itemView.getContext()).load(AptoideUtils.UI.parseIcon(appItem.getIcon())).into(icon);

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(itemView.getContext(), AppViewActivity.class);
                long id = appItem.getId().longValue();
                long adId = appItem.getAdId();
                i.putExtra(APP_ID_KEY, id);
                i.putExtra(AD_ID_KEY, adId);
                i.putExtra(APPNAME_KEY, appItem.getName());
                i.putExtra(PACKAGENAME_KEY, appItem.getPackageName());
                i.putExtra(STORENAME_KEY, appItem.getRepo());
                i.putExtra(FROM_SPONSORED_KEY, true);
                i.putExtra(LOCATION_KEY, "homepage");
                i.putExtra(KEYWORD_KEY, "__NULL__");
                i.putExtra(CPC_KEY, appItem.getCpcUrl());
                i.putExtra(CPI_KEY, appItem.getCpiUrl());
                i.putExtra(WHERE_FROM_KEY, "sponsored");
                i.putExtra(DOWNLOAD_FROM_KEY, "sponsored");

                if (appItem.getPartnerClickUrl() != null && appItem.getPartnerName() != null) {
                    Bundle bundle = new Bundle();

                    bundle.putString(PARTNER_TYPE_KEY, appItem.getPartnerName());
                    bundle.putString(PARTNER_CLICK_URL_KEY, appItem.getPartnerClickUrl());

                    i.putExtra(PARTNER_EXTRA, bundle);
                }
                AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin("Suggested_Search Result");
                itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    protected void bindViews(View itemView) {
        name = (TextView) itemView.findViewById(R.id.name);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        downloads = (TextView) itemView.findViewById(R.id.downloads_number);
        versionName = (TextView) itemView.findViewById(R.id.versionName);
        description = (TextView) itemView.findViewById(R.id.description);
        store = (TextView) itemView.findViewById(R.id.search_store);
    }
}
