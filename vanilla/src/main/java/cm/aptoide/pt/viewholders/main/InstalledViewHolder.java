package cm.aptoide.pt.viewholders.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aptoide.models.Displayable;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 17/06/15.
 */
public class InstalledViewHolder extends BaseViewHolder {

    @Bind(R.id.name)                public TextView name;
    @Bind(R.id.icon)                public ImageView icon;
    @Bind(R.id.app_update_version)  public TextView tvAppVersion;
    @Bind(R.id.installedItemFrame)  public View installedItemFrame;
    @Bind(R.id.reviewButtonLayout)  public LinearLayout createReviewLayout;

    public InstalledViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);

    }

    @Override
    public void populateView(Displayable displayable) {

    }
}
