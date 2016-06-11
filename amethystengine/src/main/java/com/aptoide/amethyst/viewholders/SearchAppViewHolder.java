package com.aptoide.amethyst.viewholders;

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.StoresActivity;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.ui.MoreVersionsActivity;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.SearchApk;
import com.bumptech.glide.Glide;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rmateus on 02/06/15.
 */
public class SearchAppViewHolder extends BaseViewHolder {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public TextView name;
    public ImageView icon;
    public TextView downloads;
    public RatingBar ratingBar;
    public ImageView overflow;
    public TextView time;
    public TextView store;
    public ImageView icTrusted;
    public View bottomView;


    public SearchAppViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
        final SearchApk appItem = (SearchApk) displayable;

        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(view.getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_search_item, popup.getMenu());
                MenuItem menuItem = popup.getMenu().findItem(R.id.versions);
                menuItem.setVisible(appItem.isOtherVersions());
                menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent = new Intent(itemView.getContext(), MoreVersionsActivity.class);
                        intent.putExtra(Constants.PACKAGENAME_KEY, appItem.getPackageName());
                        intent.putExtra(Constants.EVENT_LABEL, appItem.getName());
                        itemView.getContext().startActivity(intent);
                        return true;
                    }
                });
                menuItem = popup.getMenu().findItem(R.id.go_to_store);
                menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent = new Intent(itemView.getContext(), StoresActivity.class);
                        intent.putExtra(Constants.STORENAME_KEY, appItem.getRepo());
                        intent.putExtra(Constants.STOREAVATAR_KEY, appItem.getIcon());
                        intent.putExtra(Constants.THEME_KEY, appItem.getRepoTheme());
                        intent.putExtra(Constants.DOWNLOAD_FROM_KEY, "store");
                        boolean subscribed = new AptoideDatabase(Aptoide.getDb()).existsStore(appItem.getRepo());
                        intent.putExtra(Constants.STORE_SUBSCRIBED_KEY, subscribed);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        itemView.getContext().startActivity(intent);
                        return true;
                    }
                });

                popup.show();
            }
        });

        name.setText(appItem.getName());
        String downloadNumber = AptoideUtils.StringUtils.withSuffix(appItem.getDownloads())+" "+bottomView.getContext().getString(R.string.downloads);
        downloads.setText(downloadNumber);

        if (appItem.getStars().floatValue() <= 0) {
            ratingBar.setVisibility(View.GONE);
        } else {
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(appItem.getStars().floatValue());
        }

        Date modified = null;
        try {
            modified = dateFormatter.parse(appItem.getTimestamp());
        } catch (ParseException e) {
            Logger.printException(e);
        } finally {
            if (modified != null) {
                String timeSinceUpdate= AptoideUtils.DateTimeUtils.getInstance(itemView.getContext()).getTimeDiffAll(itemView.getContext(), modified.getTime());
                if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
                    time.setText(timeSinceUpdate);
                }
            }
        }

        final EnumStoreTheme theme = EnumStoreTheme.get(appItem.getRepoTheme());

        Drawable background = bottomView.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
        }

        background = store.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
        }


        store.setText(appItem.getRepo());
        Glide.with(itemView.getContext()).load(AptoideUtils.UI.parseIcon(appItem.getIcon())).into(icon);

        if (appItem.getMalwareRank() == 2) {
            icTrusted.setVisibility(View.VISIBLE);
        } else {
            icTrusted.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AppViewActivity.class);
                intent.putExtra(Constants.APPNAME_KEY, appItem.getName());
                intent.putExtra(Constants.SEARCH_FROM_KEY, true);
                intent.putExtra(Constants.MD5SUM_KEY, appItem.getMd5sum());
                intent.putExtra(Constants.PACKAGENAME_KEY, appItem.getPackageName());
                intent.putExtra(Constants.STORENAME_KEY, appItem.getRepo());

                Analytics.Search.searchPosition(appItem.getPosition(), appItem.isFromSubscribedStore(), appItem.getRepo());
                AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin("Search Result");

                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    protected void bindViews(View itemView) {
        name = (TextView )itemView.findViewById(R.id.name);
        icon = (ImageView )itemView.findViewById(R.id.icon);
        downloads = (TextView )itemView.findViewById(R.id.downloads);
        ratingBar = (RatingBar )itemView.findViewById(R.id.ratingbar);
        overflow = (ImageView )itemView.findViewById(R.id.overflow);
        time = (TextView )itemView.findViewById(R.id.search_time);
        store = (TextView )itemView.findViewById(R.id.search_store);
        icTrusted = (ImageView )itemView.findViewById(R.id.ic_trusted_search);
        bottomView = (View )itemView.findViewById(R.id.bottom_view);
    }
}
