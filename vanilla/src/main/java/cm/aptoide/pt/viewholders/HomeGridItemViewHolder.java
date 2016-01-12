package cm.aptoide.pt.viewholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.models.AdItem;
import com.aptoide.models.AppItem;
import com.aptoide.models.Displayable;
import com.aptoide.models.MoreVersionsItem;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;

import static com.aptoide.amethyst.utils.AptoideUtils.StringUtils.withSuffix;

/**
 * Created by rmateus on 02/06/15.
 */
public class HomeGridItemViewHolder extends BaseViewHolder {

    @Bind(R.id.name)            public TextView name;
    @Bind(R.id.icon)            public ImageView icon;
    @Bind(R.id.downloads)       public TextView downloads;
    @Bind(R.id.ratingbar)       public RatingBar ratingBar;
    @Bind(R.id.store_name)      public TextView tvStoreName;
    @Bind(R.id.added_time)      public TextView tvAddedTime;

    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public HomeGridItemViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {

        if (displayable instanceof AdItem) {
            AdItem adItem = (AdItem) displayable;
            name.setText(adItem.appName);
            downloads.setText(Aptoide.getContext().getString(R.string.sponsored));
            ratingBar.setVisibility(View.GONE);
            itemView.setOnClickListener(new BaseAdapter.AdAppItemOnClickListener(adItem));
            Glide.with(itemView.getContext()).load(adItem.icon).into(icon);

        } else if (displayable instanceof MoreVersionsItem) {
            AppItem appItem = (AppItem) displayable;
            name.setText(appItem.appName);
            name.setMaxLines(1);
            name.setSingleLine();
            //reusing downloads textView to show version name
            downloads.setText(appItem.versionName);
            ratingBar.setVisibility(View.GONE);
            tvStoreName.setText(appItem.storeName);
            tvStoreName.setVisibility(View.VISIBLE);

            String timeSinceUpdate = null;
            Date modified = null;
            try {
                modified = dateFormatter.parse(appItem.modified);
            } catch (ParseException e) {
                Logger.printException(e);
            } finally {
                if (modified != null) {
                    timeSinceUpdate = AptoideUtils.DateTimeUtils.getInstance(itemView.getContext()).getTimeDiffAll(itemView.getContext(), modified.getTime());
                }
            }
            tvAddedTime.setText(TextUtils.isEmpty(timeSinceUpdate) ? appItem.modified : timeSinceUpdate);
            tvAddedTime.setVisibility(View.VISIBLE);
            downloads.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
            Glide.with(itemView.getContext()).load(appItem.icon).into(icon);
        } else {
            AppItem appItem = (AppItem) displayable;
            name.setText(appItem.appName);
            downloads.setText(withSuffix(appItem.downloads) + Aptoide.getContext().getString(R.string._downloads));
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(appItem.rating);
            itemView.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
            Glide.with(itemView.getContext()).load(appItem.icon).into(icon);
        }

    }

}