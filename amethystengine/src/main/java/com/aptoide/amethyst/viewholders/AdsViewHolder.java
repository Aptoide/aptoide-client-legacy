package com.aptoide.amethyst.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;
import com.reach.AdServiceManager;
import com.reach.IAd;
import com.reach.IAdItem;
import com.reach.IAdService;
import com.reach.ICallback;
import com.reach.INativeAd;

import org.w3c.dom.Text;

/**
 * Created by pedroribeiro on 27/04/16.
 */
public class AdsViewHolder extends BaseViewHolder {

    public TextView name;
    public ImageView icon;

    public AdsViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        bindViews(itemView);
    }


    @Override
    public void populateView(Displayable displayable) {
        addAd(itemView);
    }

    @Override
    protected void bindViews(View itemView) {
        name = (TextView) itemView.findViewById(R.id.name);
        icon = (ImageView) itemView.findViewById(R.id.ad_icon);

    }

    private void addAd(final View view) {
        IAdService adService = AdServiceManager.get(Aptoide.getContext());

        INativeAd ad1 = adService.getNativeAd("native.ad1", 20, 20, 1, null);

        IAdItem item = ad1.getAdItem(0);
        item.bind(view,
                new String[]{IAdItem.ICON, IAdItem.TITLE, IAdItem.CALL_TO_ACTION},
                new int[]{R.id.ad_icon, R.id.name, R.id.button}
        );
        view.setVisibility(View.GONE);
        ad1.setOnLoadLisenter(new ICallback() {
            @Override
            public void call(int resultCode) {
                if(resultCode == IAd.OK) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
        ad1.load();
    }

}
