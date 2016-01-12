package cm.aptoide.pt.viewholders;

import android.view.View;
import android.widget.ProgressBar;

import com.aptoide.models.Displayable;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;

/**
 * Created by hsousa on 20/10/15.
 */
public class ProgressBarRowViewHolder extends BaseViewHolder {

    @Bind(R.id.progress_bar_endless)   public ProgressBar progressBar;

    public ProgressBarRowViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

}