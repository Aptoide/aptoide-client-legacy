package com.aptoide.amethyst.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.SearchApk;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SearchMoreHeader;
import com.aptoide.models.displayables.SuggestedAppDisplayable;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.MoreSearchActivity;

import com.aptoide.amethyst.StoresActivity;
import com.aptoide.amethyst.ui.MoreVersionsActivity;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.DummyBaseViewHolder;
import com.aptoide.amethyst.viewholders.ProgressBarRowViewHolder;
import com.aptoide.amethyst.viewholders.SearchAppViewHolder;
import com.aptoide.amethyst.viewholders.SuggestedAppViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private final ArrayList<Displayable> list;
    private String query;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public SearchAdapter(ArrayList<Displayable> list) {
        this.list = list;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (viewType == R.layout.search_app_row) {
            return new SearchAppViewHolder(view, viewType);
        } else if (viewType == R.layout.layout_header) {
            return new HeaderViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else if (viewType == R.layout.search_more_results) {
            return new DummyBaseViewHolder(view, viewType);
        } else if (viewType == R.layout.suggested_app_search) {
            return new SuggestedAppViewHolder(view, viewType);
        } else if (viewType == R.layout.row_progress_bar) {
            return new ProgressBarRowViewHolder(view, viewType);
        } else {
            throw new IllegalStateException(("This adapter doesn't know how to show viewtype " + viewType));
        }

    }

    @Override
    public void onBindViewHolder(final BaseViewHolder viewHolder, int position) {

        if (viewHolder.viewType == R.layout.layout_header) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            HeaderRow row = (HeaderRow) list.get(position);

            headerViewHolder.title.setText(row.getLabel());

            if (!row.isHasMore()) {
                headerViewHolder.more.setVisibility(View.GONE);
            }
        } else if (viewHolder.viewType == R.layout.search_app_row) {
            final SearchAppViewHolder item = (SearchAppViewHolder) viewHolder;

            final SearchApk appItem = (SearchApk) list.get(position);

            item.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popup = new PopupMenu(view.getContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_search_item, popup.getMenu());
                    MenuItem menuItem = popup.getMenu().findItem(R.id.versions);
                    menuItem.setVisible(appItem.hasOtherVersions);
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Intent intent = new Intent(item.itemView.getContext(), MoreVersionsActivity.class);
                            intent.putExtra(Constants.PACKAGENAME_KEY, appItem.packageName);
                            intent.putExtra(Constants.EVENT_LABEL, appItem.name);
                            item.itemView.getContext().startActivity(intent);
                            return true;
                        }
                    });
                    menuItem = popup.getMenu().findItem(R.id.go_to_store);
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Intent intent = new Intent(item.itemView.getContext(), StoresActivity.class);
                            intent.putExtra(Constants.STORENAME_KEY, appItem.repo);
                            intent.putExtra(Constants.STOREAVATAR_KEY, appItem.icon);
                            intent.putExtra(Constants.THEME_KEY, appItem.repo_theme);
                            intent.putExtra(Constants.DOWNLOAD_FROM_KEY, "store");
                            boolean subscribed = new AptoideDatabase(Aptoide.getDb()).existsStore(appItem.repo);
                            intent.putExtra(Constants.STORE_SUBSCRIBED_KEY, subscribed);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            item.itemView.getContext().startActivity(intent);
                            return true;
                        }
                    });

                    popup.show();
                }
            });

            item.name.setText(appItem.name);
            String downloadNumber = AptoideUtils.StringUtils.withSuffix(appItem.downloads) + " " + item.bottomView.getContext().getString(R.string.downloads);
            item.downloads.setText(downloadNumber);

            if (appItem.stars.floatValue() <= 0) {
                item.ratingBar.setVisibility(View.GONE);
            } else {
                item.ratingBar.setVisibility(View.VISIBLE);
                item.ratingBar.setRating(appItem.stars.floatValue());
            }

            Date modified = null;
            try {
                modified = dateFormatter.parse(appItem.timestamp);
            } catch (ParseException e) {
                Logger.printException(e);
            } finally {
                if (modified != null) {
                    String timeSinceUpdate = AptoideUtils.DateTimeUtils.getInstance(item.itemView.getContext()).getTimeDiffAll(item.itemView.getContext(),
                            modified.getTime());
                    if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
                        item.time.setText(timeSinceUpdate);
                    }
                }
            }

            final EnumStoreTheme theme = EnumStoreTheme.get(appItem.repo_theme);

            Drawable background = item.bottomView.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(item.itemView.getContext().getResources().getColor(theme.getColor700tint()));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(item.itemView.getContext().getResources().getColor(theme.getColor700tint()));
            }

            background = item.store.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(item.itemView.getContext().getResources().getColor(theme.getColor700tint()));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(item.itemView.getContext().getResources().getColor(theme.getColor700tint()));
            }

            item.store.setText(appItem.repo);
            Glide.with(viewHolder.itemView.getContext()).load(appItem.iconHd != null ? appItem.iconHd : appItem.icon).into(item.icon);

            if (appItem.malrank == 2) {
                item.icTrusted.setVisibility(View.VISIBLE);
            } else {
                item.icTrusted.setVisibility(View.GONE);
            }

            item.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AppViewActivity.class);
                    intent.putExtra(Constants.SEARCH_FROM_KEY, true);
                    intent.putExtra(Constants.MD5SUM_KEY, appItem.md5sum);
                    intent.putExtra(Constants.APPNAME_KEY, appItem.name);
                    intent.putExtra(Constants.PACKAGENAME_KEY, appItem.packageName);
                    intent.putExtra(Constants.STORENAME_KEY, appItem.repo);

                    Analytics.Search.searchPosition(appItem.position, appItem.fromSubscribedStore, appItem.repo);

                    v.getContext().startActivity(intent);
                }
            });
        } else if (viewHolder.viewType == R.layout.search_more_results) {
            viewHolder.itemView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(viewHolder.itemView.getContext(), MoreSearchActivity.class);
                    i.putExtra(MoreSearchActivity.QUERY_BUNDLE_KEY, query);
                    viewHolder.itemView.getContext().startActivity(i);
                }
            });
        } else if (viewHolder.viewType == R.layout.suggested_app_search) {
            viewHolder.populateView(list.get(position));
        } else if (viewHolder.viewType == R.layout.row_progress_bar) {
            viewHolder.populateView(list.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position) instanceof HeaderRow) {
            return R.layout.layout_header;
        } else if (list.get(position) instanceof SearchApk) {
            return R.layout.search_app_row;
        } else if (list.get(position) instanceof SearchMoreHeader) {
            return R.layout.search_more_results;
        } else if (list.get(position) instanceof SuggestedAppDisplayable) {
            return R.layout.suggested_app_search;
        } else if (list.get(position) instanceof ProgressBarRow) {
            return R.layout.row_progress_bar;
        } else {
            throw new IllegalStateException("This adapter doesn't know how to show " + list.get(position).getClass().getName());
        }
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
