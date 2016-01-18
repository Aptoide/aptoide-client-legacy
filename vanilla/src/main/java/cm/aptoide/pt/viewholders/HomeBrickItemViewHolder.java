package cm.aptoide.pt.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aptoide.models.BrickAppItem;
import com.aptoide.models.Displayable;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;

/**
 * Created by hsousa on 20/10/15.
 */
public class HomeBrickItemViewHolder extends BaseViewHolder {

    @Bind(R.id.app_name)          public TextView name;
    @Bind(R.id.featured_graphic)  public ImageView graphic;
    @Bind(R.id.ratingbar)         public RatingBar ratingBar;


    public HomeBrickItemViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {
//        HomeBrickItemViewHolder holder = (HomeBrickItemViewHolder) viewHolder;
        BrickAppItem appItem = (BrickAppItem) displayable;

        name.setText(appItem.appName);
//                downloads.setText(withSuffix(appItem.downloads) + " downloads");
//                ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(appItem.rating);
        itemView.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
        Glide.with(itemView.getContext()).load(appItem.featuredGraphic).placeholder(R.drawable.placeholder_705x345).into(graphic);

    }

}