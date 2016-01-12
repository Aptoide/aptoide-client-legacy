package cm.aptoide.pt.viewholders.appview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;

/**
 * Created by hsousa on 03/12/15.
 */
public class MoreAppViewVersionsHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.other_stores_content)      public RelativeLayout mContent;
    @Bind(R.id.other_stores_avatar_store) public ImageView mAvatarStore;
    @Bind(R.id.other_stores_avatar_app)   public ImageView mAvatarApp;
    @Bind(R.id.other_stores_name)         public TextView mStoreName;
    @Bind(R.id.other_stores_app_name)     public TextView mAppName;
    @Bind(R.id.other_stores_app_version)  public TextView mAppVersion;

    public MoreAppViewVersionsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
