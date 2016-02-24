package cm.aptoide.pt.viewholders;

import android.view.View;
import android.widget.ProgressBar;

import com.aptoide.models.Displayable;

import cm.aptoide.pt.R;

/**
 * Created by hsousa on 20/10/15.
 */
public class ProgressBarRowViewHolder extends BaseViewHolder {

    public ProgressBar progressBar;

    public ProgressBarRowViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_endless);
    }
}