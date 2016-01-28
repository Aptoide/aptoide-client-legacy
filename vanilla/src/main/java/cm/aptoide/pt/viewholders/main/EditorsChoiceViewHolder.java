package cm.aptoide.pt.viewholders.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.models.AppItem;
import com.aptoide.models.Displayable;
import com.aptoide.models.EditorsChoiceRow;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;
import cm.aptoide.pt.viewholders.BaseViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class EditorsChoiceViewHolder extends BaseViewHolder {

    @Bind({R.id.main_image, R.id.left_image, R.id.right_image})
    public ImageView[] images;

    @Bind(R.id.more)
    public TextView more;


    public EditorsChoiceViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populateView(Displayable displayable) {
//        EditorsChoiceViewHolder holder = (EditorsChoiceViewHolder) viewHolder;
        EditorsChoiceRow row = (EditorsChoiceRow) displayable;
        more.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, null));
        int i = 0;
        for (; i < images.length && i < row.appItemList.size(); i++) {
            final AppItem appItem = row.appItemList.get(i);
            Glide.with(itemView.getContext()).load(appItem.featuredGraphic).placeholder(R.drawable.placeholder_705x345).into(images[i]);
            images[i].setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
        }

        //when it's a tablet, we have to fill the extra image
        ImageView extraImage = (ImageView) itemView.findViewById(R.id.extra_image);
        if (extraImage != null && row.appItemList.size() > i + 1) {
            AppItem appItem = row.appItemList.get(i + 1);
            Glide.with(itemView.getContext()).load(appItem.featuredGraphic).placeholder(R.drawable.placeholder_705x345).into(extraImage);
        }

    }

}
