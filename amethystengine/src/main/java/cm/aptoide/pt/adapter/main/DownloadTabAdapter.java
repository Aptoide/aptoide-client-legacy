package cm.aptoide.pt.adapter.main;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.downloadmanager.adapter.NotOngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.adapter.OngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.amethyst.downloadmanager.state.EnumState;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.Displayable;
import com.aptoide.models.HeaderRow;
import com.bumptech.glide.Glide;

import java.util.List;


import cm.aptoide.pt.adapter.BaseAdapter;
import cm.aptoide.pt.viewholders.BaseViewHolder;
import cm.aptoide.pt.viewholders.DummyBaseViewHolder;
import cm.aptoide.pt.viewholders.main.HeaderViewHolder;
import cm.aptoide.pt.viewholders.main.NotOnGoingDownloadViewHolder;
import cm.aptoide.pt.viewholders.main.OnGoingDownloadViewHolder;

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
