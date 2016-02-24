package cm.aptoide.pt.viewholders.main;

import android.view.View;
import android.widget.Button;

import com.aptoide.models.Displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 23/06/15.
 */
public class AddStoreViewHolder extends BaseViewHolder {

    public Button more;

    public AddStoreViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        more = (Button) itemView.findViewById(R.id.more);
    }
}

