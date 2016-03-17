package com.aptoide.amethyst.adapter.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.Displayable;

import java.util.List;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.DummyBaseViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.amethyst.viewholders.main.NotOnGoingDownloadViewHolder;
import com.aptoide.amethyst.viewholders.main.OnGoingDownloadViewHolder;

/**
 * Created by hsousa on 13-07-2015.
 */
public class DownloadTabAdapter extends BaseAdapter {

    public DownloadTabAdapter(List<Displayable> displayableList) {
        super(displayableList);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (viewType == R.layout.row_app_downloading_ongoing) {
            return new OnGoingDownloadViewHolder(view, viewType);
        } else if (viewType == R.layout.row_app_downloading_notongoing) {
            return new NotOnGoingDownloadViewHolder(view, viewType);
        } else if (viewType == R.layout.layout_header) {
            return new HeaderViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else {
            return new DummyBaseViewHolder(view, viewType);
        }
    }

}
