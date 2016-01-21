package cm.aptoide.pt.ui;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import cm.aptoide.pt.R;

/**
 * Created by hsousa on 11/11/15.
 */
public class ThisAppInOtherStores {

    @Bind(R.id.other_stores_content)      public RelativeLayout mContent;
    @Bind(R.id.other_stores_avatar_store) public ImageView mAvatarStore;
    @Bind(R.id.other_stores_avatar_app)   public ImageView mAvatarApp;
    @Bind(R.id.other_stores_name)         public TextView mStoreName;
//    @Bind(R.id.other_stores_app_name)     public TextView mAppName;
    @Bind(R.id.other_stores_app_version)  public TextView mAppVersion;
}
