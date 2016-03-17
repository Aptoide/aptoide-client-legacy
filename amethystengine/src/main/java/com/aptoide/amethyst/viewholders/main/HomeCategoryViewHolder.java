package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.ImageView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.GlideUtils;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.CategoryRow;

import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 30/09/15.
 */
public class HomeCategoryViewHolder extends BaseViewHolder {

    private final EnumStoreTheme theme;

    public ImageView imageView;

    public HomeCategoryViewHolder(View itemView, int viewType, EnumStoreTheme theme) {
        super(itemView, viewType);
        this.theme = theme;
    }

    @Override
    public void populateView(Displayable displayable) {
        // for v7, it was decided to use a variable number of categories. doing it programmatically via manual inflate
        // was the only fast way to do it correctly without UI bugs. Feel free to refactor it.. if you dare

//        final HomeCategoryViewHolder holder = (HomeCategoryViewHolder) viewHolder;
        final CategoryRow appItem = (CategoryRow) displayable;

//        categoryHome.removeAllViews();
//
//        ImageView[] views = new ImageView[appItem.getList().size()];
//        for (int i = 0; i < appItem.getList().size(); i++) {
//
//            View v = LayoutInflater.from(itemView.getContext()).inflate(R.layout.row_category_home_item, categoryHome, false);
//            views[i] = (ImageView) v.findViewById(R.id.image_category);
//            categoryHome.addView(v);
//            Glide.with(itemView.getContext()).load(appItem.getList().get(i).getGraphic()).into(views[i]);
//            views[i].setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(appItem.getList().get(i), theme));
//        }

//        Glide.with(itemView.getContext()).load(appItem.getGraphic()).into(imageView);
        GlideUtils.download(itemView.getContext(), appItem.getGraphic(), imageView);

        imageView.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(appItem, theme));
    }

    @Override
    protected void bindViews(View itemView) {
        imageView = (ImageView )itemView.findViewById(R.id.image_category);
    }
}
