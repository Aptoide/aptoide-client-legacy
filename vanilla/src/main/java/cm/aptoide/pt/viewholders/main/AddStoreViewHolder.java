package cm.aptoide.pt.viewholders.main;

import android.view.View;
import android.widget.Button;

import com.aptoide.models.Displayable;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 23/06/15.
 */
public class AddStoreViewHolder extends BaseViewHolder {

    @Bind(R.id.more)
    public Button more;

    public AddStoreViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);

    }

    @Override
    public void populateView(Displayable displayable) {

    }

}

