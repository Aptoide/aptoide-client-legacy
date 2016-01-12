package cm.aptoide.pt.viewholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.models.Displayable;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;

/**
 * Created by rmateus on 02/06/15.
 */
public class ExcludedUpdateViewHolder extends BaseViewHolder {

    @Bind(R.id.app_icon)        public ImageView app_icon;
    @Bind(R.id.tv_name)         public TextView tv_name;
    @Bind(R.id.tv_vercode)      public TextView tv_vercode;
    @Bind(R.id.tv_apkid)        public TextView tv_apkid;
    @Bind(R.id.cb_exclude)      public CheckBox cb_exclude;

    public ExcludedUpdateViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);

    }

    @Override
    public void populateView(Displayable displayable) {

    }

}