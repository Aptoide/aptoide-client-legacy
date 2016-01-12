package cm.aptoide.pt.viewholders.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aptoide.models.Displayable;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 13/07/15.
 */
public class DownloadViewHolder extends BaseViewHolder {

    @Bind(R.id.app_icon)                  public ImageView appIcon;
    @Bind(R.id.app_name)                  public TextView appName;
    @Bind(R.id.downloading_progress)      public ProgressBar downloadingProgress;
    @Bind(R.id.download_details_layout)   public RelativeLayout downloadDetails;
    @Bind(R.id.speed)                     public TextView speed;
    @Bind(R.id.eta)                       public TextView eta;
    @Bind(R.id.progress)                  public TextView progress;
    @Bind(R.id.app_error)                 public TextView appError;
    @Bind(R.id.view)                      public View view;
    @Bind(R.id.manage_icon)               public ImageView manageIcon;
    @Bind(R.id.row_app_download_indicator)public RelativeLayout layout;

    public DownloadViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {

    }
}