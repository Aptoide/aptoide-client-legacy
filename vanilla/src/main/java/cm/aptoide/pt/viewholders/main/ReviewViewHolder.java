package cm.aptoide.pt.viewholders.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.models.Displayable;
import com.aptoide.models.ReviewRowItem;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ui.ReviewActivity;
import cm.aptoide.pt.ui.widget.CircleTransform;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 24/09/15.
 */
public class ReviewViewHolder extends BaseViewHolder {

    private final EnumStoreTheme theme;
    @Bind(R.id.app_icon)          public ImageView appIcon;
    @Bind(R.id.rating)            public TextView rating;
    @Bind(R.id.app_name)          public TextView appName;
    @Bind(R.id.avatar)            public ImageView avatar;
    @Bind(R.id.reviewer)          public TextView reviewer;
    @Bind(R.id.description)       public TextView description;
    @Bind(R.id.score)             public FrameLayout score;

    public ReviewViewHolder(View itemView, int viewType, EnumStoreTheme theme) {
        super(itemView, viewType);
        this.theme = theme;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {
//        ReviewViewHolder holder = (ReviewViewHolder) viewHolder;
        final ReviewRowItem appItem = (ReviewRowItem) displayable;
        final Context context = itemView.getContext();

        appName.setText(appItem.appName);
        description.setText(appItem.description);
        reviewer.setText(AptoideUtils.StringUtils.getFormattedString(context, R.string.reviewed_by, appItem.reviewer));
        rating.setText(AptoideUtils.StringUtils.getRoundedValueFromDouble(appItem.rating));
        Glide.with(context).load(appItem.appIcon).into(appIcon);
        Glide.with(context).load(appItem.avatar).transform(new CircleTransform(context)).into(avatar);

        if(theme != null) {
            @ColorInt int color = context.getResources().getColor(theme.getStoreHeader());
            score.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("review_id", appItem.reviewId);
                context.startActivity(intent);
            }
        });

    }
}
