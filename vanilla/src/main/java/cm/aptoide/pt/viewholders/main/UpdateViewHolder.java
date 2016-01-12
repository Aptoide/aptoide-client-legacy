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
public class UpdateViewHolder extends BaseViewHolder {

    @Bind(R.id.name)                  public TextView name;
    @Bind(R.id.icon)                  public ImageView icon;
    @Bind(R.id.app_installed_version)  public TextView appInstalledVersion;
    @Bind(R.id.app_update_version)    public TextView appUpdateVersion;
    @Bind(R.id.updateButtonLayout)    public LinearLayout updateButtonLayout;

    public UpdateViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {

    }
}
