package com.aptoide.amethyst.viewholders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.SuggestedAppDisplayable;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

import com.aptoide.amethyst.AppViewActivity;


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
public class SuggestedAppViewHolder extends BaseViewHolder {

    TextView labelTextView;
    TextView sizeTextView;
    TextView descriptionTextView;
    ImageView iconImageView;
    TextView store;
    View bottomView;

    public SuggestedAppViewHolder(View view, int viewType) {
        super(view, viewType);
    }

    public void populateView(Displayable displayable) {

        if (displayable instanceof SuggestedAppDisplayable) {
            itemView.setVisibility(View.VISIBLE);
            SuggestedAppDisplayable suggestedAppDisplayable = (SuggestedAppDisplayable) displayable;

            DecimalFormat df = new DecimalFormat("0.00");

            labelTextView.setText(suggestedAppDisplayable.getLabel());
            sizeTextView.setText(df.format(suggestedAppDisplayable.getSize()) + " MB");
            descriptionTextView.setText(suggestedAppDisplayable.getDescription());
            store.setText(suggestedAppDisplayable.getStore());
            Glide.with(itemView.getContext()).load(suggestedAppDisplayable.getIconPath()).into(iconImageView);

            itemView.setOnClickListener(generateOnClickListener(suggestedAppDisplayable));

        }
    }

    @Override
    protected void bindViews(View itemView) {
        labelTextView = (TextView )itemView.findViewById(R.id.app_name);
        sizeTextView = (TextView )itemView.findViewById(R.id.size_value);
        descriptionTextView = (TextView )itemView.findViewById(R.id.description);
        iconImageView = (ImageView )itemView.findViewById(R.id.app_icon);
        store = (TextView )itemView.findViewById(R.id.search_store);
        bottomView = (View )itemView.findViewById(R.id.bottom_view);
    }

    private View.OnClickListener generateOnClickListener(final SuggestedAppDisplayable suggestedAppDisplayable) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(itemView.getContext(), AppViewActivity.class);
                long id = suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getData().id.longValue();
                long adId = suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getInfo().getAd_id();
                i.putExtra(APP_ID_KEY, id);
                i.putExtra(AD_ID_KEY, adId);
                i.putExtra(APPNAME_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getData().getName());
                i.putExtra(PACKAGENAME_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getData().packageName);
                i.putExtra(STORENAME_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getData().repo);
                i.putExtra(FROM_SPONSORED_KEY, true);
                i.putExtra(LOCATION_KEY, "homepage");
                i.putExtra(KEYWORD_KEY, "__NULL__");
                i.putExtra(CPC_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getInfo().getCpc_url());
                i.putExtra(CPI_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getInfo().getCpi_url());
                i.putExtra(WHERE_FROM_KEY, "sponsored");
                i.putExtra(DOWNLOAD_FROM_KEY, "sponsored");

                if (suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getPartner() != null) {
                    Bundle bundle = new Bundle();

                    bundle.putString(PARTNER_TYPE_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getPartner().getPartnerInfo().name);
                    bundle.putString(PARTNER_CLICK_URL_KEY, suggestedAppDisplayable.getApkSuggestionJson().getAds().get(0).getPartner().getPartnerData().click_url);

                    i.putExtra(PARTNER_EXTRA, bundle);
                }

                itemView.getContext().startActivity(i);
            }
        };
    }
}
