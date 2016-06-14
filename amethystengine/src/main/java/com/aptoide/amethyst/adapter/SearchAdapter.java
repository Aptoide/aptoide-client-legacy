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

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.MoreSearchActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.StoresActivity;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.ui.MoreVersionsActivity;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.DummyBaseViewHolder;
import com.aptoide.amethyst.viewholders.ProgressBarRowViewHolder;
import com.aptoide.amethyst.viewholders.SearchAppViewHolder;
import com.aptoide.amethyst.viewholders.SuggestedAppViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.SearchApk;
import com.aptoide.models.displayables.SearchMoreHeader;
import com.aptoide.models.displayables.SuggestedAppDisplayable;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private final List<Displayable> list;
    private String query;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public SearchAdapter(List<Displayable> list) {
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
                    menuItem.setVisible(appItem.isOtherVersions());
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Intent intent = new Intent(item.itemView.getContext(), MoreVersionsActivity.class);
                            intent.putExtra(Constants.PACKAGENAME_KEY, appItem.getPackageName());
                            intent.putExtra(Constants.EVENT_LABEL, appItem.getName());
                            item.itemView.getContext().startActivity(intent);
                            return true;
                        }
                    });
                    menuItem = popup.getMenu().findItem(R.id.go_to_store);
                    menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Intent intent = new Intent(item.itemView.getContext(), StoresActivity.class);
                            intent.putExtra(Constants.STORENAME_KEY, appItem.getRepo());
                            intent.putExtra(Constants.STOREAVATAR_KEY, appItem.getIcon());
                            intent.putExtra(Constants.THEME_KEY, appItem.getRepoTheme());
                            intent.putExtra(Constants.DOWNLOAD_FROM_KEY, "store");
                            boolean subscribed = new AptoideDatabase(Aptoide.getDb()).existsStore(appItem.getRepo());
                            intent.putExtra(Constants.STORE_SUBSCRIBED_KEY, subscribed);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            item.itemView.getContext().startActivity(intent);
                            return true;
                        }
                    });

                    popup.show();
                }
            });

            item.name.setText(appItem.getName());
            String downloadNumber = AptoideUtils.StringUtils.withSuffix(appItem.getDownloads()) + " " + item.bottomView.getContext().getString(R.string.downloads);
            item.downloads.setText(downloadNumber);

            if (appItem.getStars().floatValue() <= 0) {
                item.ratingBar.setVisibility(View.GONE);
            } else {
                item.ratingBar.setVisibility(View.VISIBLE);
                item.ratingBar.setRating(appItem.getStars().floatValue());
            }

            Date modified = null;
            try {
                modified = dateFormatter.parse(appItem.getTimestamp());
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

            final EnumStoreTheme theme = EnumStoreTheme.get(appItem.getRepoTheme());

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

            item.store.setText(appItem.getRepo());
            Glide.with(viewHolder.itemView.getContext()).load(AptoideUtils.UI.parseIcon(appItem.getIcon())).into(item.icon);

            if (appItem.getMalwareRank() == 2) {
                item.icTrusted.setVisibility(View.VISIBLE);
            } else {
                item.icTrusted.setVisibility(View.GONE);
            }

            item.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AppViewActivity.class);
                    intent.putExtra(Constants.SEARCH_FROM_KEY, true);
                    intent.putExtra(Constants.MD5SUM_KEY, appItem.getMd5sum());
                    intent.putExtra(Constants.APPNAME_KEY, appItem.getName());
                    intent.putExtra(Constants.PACKAGENAME_KEY, appItem.getPackageName());
                    intent.putExtra(Constants.STORENAME_KEY, appItem.getRepo());

                    Analytics.Search.searchPosition(appItem.getPosition(), appItem.isFromSubscribedStore(), appItem.getRepo());
                    AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin("Search Result");

                    v.getContext().startActivity(intent);
                }
            });
        } else if (viewHolder.viewType == R.layout.search_more_results) {
            viewHolder.itemView.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(viewHolder.itemView.getContext(), MoreSearchActivity.class);
                    i.putExtra(SearchActivity.SEARCH_QUERY, query);
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
