package cm.aptoide.pt.viewholders.main;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aptoide.models.Displayable;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class CommentHeaderViewHolder extends BaseViewHolder {

    @Bind(R.id.title)         public TextView title;
    @Bind(R.id.write_comment) public LinearLayout writeComment;

    public CommentHeaderViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {

    }

}