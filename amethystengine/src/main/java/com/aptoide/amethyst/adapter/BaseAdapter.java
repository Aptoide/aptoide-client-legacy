/*******************************************************************************
 * Copyright (c) 2015 Aptoide.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.adapter;

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.downloadmanager.adapter.NotOngoingDownloadRow;
import com.aptoide.amethyst.downloadmanager.adapter.OngoingDownloadRow;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.ui.MoreCommentsActivity;
import com.aptoide.amethyst.ui.MoreFriendsInstallsActivity;
import com.aptoide.amethyst.ui.MoreHighlightedActivity;
import com.aptoide.amethyst.ui.MoreListViewItemsActivity;
import com.aptoide.amethyst.ui.MoreListViewItemsBrickActivity;
import com.aptoide.amethyst.ui.MoreReviewsActivity;
import com.aptoide.amethyst.ui.MoreStoreWidgetActivity;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.IHasMore;
import com.aptoide.models.displayables.AdItem;
import com.aptoide.models.displayables.AdultItem;
import com.aptoide.models.displayables.AppItem;
import com.aptoide.models.displayables.BrickAppItem;
import com.aptoide.models.displayables.CategoryRow;
import com.aptoide.models.displayables.CommentItem;
import com.aptoide.models.displayables.CommentPlaceHolderRow;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.EditorsChoiceRow;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.HomeStoreItem;
import com.aptoide.models.displayables.MoreVersionsItem;
import com.aptoide.models.displayables.ProgressBarRow;
import com.aptoide.models.displayables.ReviewPlaceHolderRow;
import com.aptoide.models.displayables.ReviewRowItem;
import com.aptoide.models.displayables.StoreHeaderRow;
import com.aptoide.models.displayables.TimeLinePlaceHolderRow;
import com.aptoide.models.displayables.TimelineRow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.ADS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_FACEBOOK_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_GETSTOREWIDGETS;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_LIST_APPS;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_LIST_STORES;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_TWITCH_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.Action.Event.EVENT_YOUTUBE_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.COMMENTS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.REVIEWS_TYPE;
import static com.aptoide.dataprovider.webservices.models.v7.GetStoreWidgets.WidgetDatalist.WidgetList.TIMELINE_TYPE;

/**
 * Created by hsousa on 08/08/15.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    protected List<Displayable> displayableList;

    public BaseAdapter(List<Displayable> displayableList) {
        this.displayableList = displayableList;
    }

    @Override
    public int getItemCount() {
        return displayableList.size();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.populateView(displayableList.get(position));
    }

    @Override
    public int getItemViewType(int position) {

        Displayable displayable = displayableList.get(position);

        if (displayable instanceof HeaderRow) {
            return R.layout.layout_header;
        } else if (displayable instanceof EditorsChoiceRow) {
            return R.layout.editors_choice_row;
        } else if (displayable instanceof StoreHeaderRow) {
            return R.layout.row_store_header;
        } else if (displayable instanceof ReviewRowItem) {
            return R.layout.row_review;
        } else if (displayable instanceof ReviewPlaceHolderRow) {
            return R.layout.row_empty;
        } else if (displayable instanceof CategoryRow) {
            return R.layout.row_category_home_item;
        } else if (displayable instanceof TimeLinePlaceHolderRow) {
            return R.layout.row_empty;
        } else if (displayable instanceof TimelineRow) {
            return R.layout.timeline_item;
        } else if (displayable instanceof HomeStoreItem) {
            return R.layout.row_store_item;
        } else if (displayable instanceof AdultItem) {
            return R.layout.row_adult_switch;
        } else if (displayable instanceof ProgressBarRow) {
            return R.layout.row_progress_bar;
        } else if (displayable instanceof MoreVersionsItem) {
            return R.layout.grid_item;
        } else if (displayable instanceof CommentItem) {
            return R.layout.comment_row;
        } else if (displayable instanceof OngoingDownloadRow) {
            return R.layout.row_app_downloading_ongoing;
        } else if (displayable instanceof NotOngoingDownloadRow) {
            return R.layout.row_app_downloading_notongoing;
        } else if (displayable instanceof BrickAppItem) { // this has to be above the instanceof AppItem instruction
            return R.layout.brick_app_item;
        } else if (displayable instanceof AppItem) {
            return R.layout.grid_item;
        } else if (displayable instanceof CommentPlaceHolderRow) {
            return R.layout.row_empty;
        }
//        else {
//            throw new IllegalStateException("InvalidType");
//        }

        return R.layout.row_empty;

    }

    /**
     * In order to refactor this listener to be inherited, it will be necessary to finish
     * all AppItem listeners and extract the common code
     */
    public static class AppItemOnClickListener implements View.OnClickListener {

        private final AppItem appItem;
        private final int position;
        private boolean isHome;


        public AppItemOnClickListener(AppItem appItem) {
            this.appItem = appItem;
            position = -1;
        }

        public AppItemOnClickListener(AppItem appItem, int position, boolean isHome) {
            this.appItem = appItem;
            this.position = position;
            this.isHome = isHome;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), AppViewActivity.class);
            i.putExtra(Constants.FROM_RELATED_KEY, true);

            i.putExtra(Constants.APP_ID_KEY, appItem.id);
            i.putExtra(Constants.APPNAME_KEY, appItem.appName);
            i.putExtra(Constants.MD5SUM_KEY, appItem.md5sum);
            i.putExtra(Constants.DOWNLOAD_FROM_KEY, Constants.LOCAL_TOP_APPS_VALUE);
            i.putExtra(Constants.ICON_KEY, appItem.icon);
            i.putExtra(Constants.DOWNLOADS_KEY, appItem.downloads);
            i.putExtra(Constants.RATING_KEY, appItem.rating);
            i.putExtra(Constants.GRAPHIC_KEY, appItem.featuredGraphic);
            i.putExtra(Constants.FILESIZE_KEY, appItem.fileSize);
            i.putExtra(Constants.STOREID_KEY, appItem.storeId);
            i.putExtra(Constants.STORENAME_KEY, appItem.storeName);
            i.putExtra(Constants.PACKAGENAME_KEY, appItem.packageName);
            i.putExtra(Constants.VERSIONNAME_KEY, appItem.versionName);
            AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin(appItem.category);
            if ((position >= 0)) {
                Analytics.HomePageEditorsChoice.sendHomePageEdiorsChoiceEvent(position, appItem.appName, isHome);
            }
            view.getContext().startActivity(i);
        }
    }

    public static class AdAppItemOnClickListener implements View.OnClickListener {

        private final AdItem adItem;

        public AdAppItemOnClickListener(AdItem adItem) {
            this.adItem = adItem;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), AppViewActivity.class);
            i.putExtra(Constants.FROM_SPONSORED_KEY, true);

            i.putExtra(Constants.APP_ID_KEY, adItem.id);
            i.putExtra(Constants.AD_ID_KEY, adItem.adId);
            i.putExtra(Constants.APPNAME_KEY, adItem.appName);
            i.putExtra(Constants.PACKAGENAME_KEY, adItem.packageName);
            i.putExtra(Constants.STORENAME_KEY, adItem.storeName);
            i.putExtra(Constants.CPC_KEY, adItem.cpcUrl);
            i.putExtra(Constants.CPI_KEY, adItem.cpiUrl);
            i.putExtra(Constants.CPD_KEY, adItem.cpdUrl);

            i.putExtra(Constants.LOCATION_KEY, "homepage");
            i.putExtra(Constants.KEYWORD_KEY, "__NULL__");
            i.putExtra(Constants.WHERE_FROM_KEY, "sponsored");
            i.putExtra(Constants.DOWNLOAD_FROM_KEY, "sponsored");

            if (adItem.partnerName != null && adItem.partnerClickUrl != null) {
                Bundle bundle = new Bundle();
                bundle.putString("partnerType", adItem.partnerName);
                bundle.putString("partnerClickUrl", adItem.partnerClickUrl);
                i.putExtra("partnerExtra", bundle);
            }
            AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin(adItem.category);
            view.getContext().startActivity(i);
        }
    }

    public static class TimelineItemOnClickListener implements View.OnClickListener {

        private final TimelineRow item;

        public TimelineItemOnClickListener(TimelineRow item) {
            this.item = item;
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), AppViewActivity.class);

            i.putExtra(Constants.FROM_TIMELINE_KEY, true);
            i.putExtra(Constants.STORENAME_KEY, item.repoName);
            i.putExtra(Constants.APPNAME_KEY, item.appName);
            i.putExtra(Constants.MD5SUM_KEY, item.md5sum);
            i.putExtra(Constants.DOWNLOAD_FROM_KEY, "timeline");

            view.getContext().startActivity(i);
        }
    }

    public static class CommentItemOnClickListener implements View.OnClickListener {

        private final CommentItem item;

        public CommentItemOnClickListener(CommentItem item) {
            this.item = item;
        }

        @Override
        public void onClick(View view) {
            if (item != null && item.appid != null) {
                Intent i = new Intent(view.getContext(), AppViewActivity.class);
                i.putExtra(Constants.APP_ID_KEY, item.appid.longValue());
                i.putExtra(Constants.APPNAME_KEY, item.appname);
                i.putExtra(Constants.FROM_COMMENT_KEY, true);
                view.getContext().startActivity(i);
            }
        }
    }

    public static class IHasMoreOnClickListener implements View.OnClickListener {

        private final IHasMore row;
        private final EnumStoreTheme theme;
        private final String storeName;
        private final long storeId;
        public String bundleCategory;

        public IHasMoreOnClickListener(IHasMore row, EnumStoreTheme theme) {
            this.row = row;
            this.theme = theme;
            storeName = null;
            this.storeId = 0;
        }

        public IHasMoreOnClickListener(IHasMore row, EnumStoreTheme theme, String storeName) {
            this.row = row;
            this.theme = theme;
            this.storeName = storeName;
            this.storeId = 0;
        }

        public IHasMoreOnClickListener(IHasMore row, EnumStoreTheme theme, long storeId) {
            this.row = row;
            this.theme = theme;
            this.storeName = null;
            this.storeId = storeId;
        }

        public IHasMoreOnClickListener(IHasMore row, EnumStoreTheme theme, String storeName, long storeId) {
            this.row = row;
            this.theme = theme;
            this.storeName = storeName;
            this.storeId = storeId;
        }

        @Override
        public void onClick(View view) {
            if (row.isHasMore() && row.getEventName() != null) {

                Intent i = null;

                switch (row.getEventName()) {

                    case EVENT_LIST_APPS:
                        if (row.getLayout().equals(Constants.LAYOUT_BRICK)) {
                            i = new Intent(view.getContext(), MoreListViewItemsBrickActivity.class);
                        } else {
                            i = new Intent(view.getContext(), MoreListViewItemsActivity.class);
                            if (storeName != null && !TextUtils.isEmpty(storeName)) {
                                i.putExtra(SearchActivity.SEARCH_THEME, theme);
                                i.putExtra(SearchActivity.SEARCH_SOURCE, storeName);
                            }
                            if (bundleCategory != null && !TextUtils.isEmpty(bundleCategory)) {
                                Analytics.HomePageBundles.sendHomePageBundleEvent(bundleCategory, row.getTag());
                            }
                        }
                        break;
                    case EVENT_LIST_STORES:
                        i = new Intent(view.getContext(), MoreListViewItemsActivity.class);
                        break;
                    case EVENT_GETSTOREWIDGETS:
                        i = new Intent(view.getContext(), MoreStoreWidgetActivity.class);
                        Analytics.HomePageBundles.sendHomePageBundleEvent(row.getTag());
                        break;
                    case ADS_TYPE:
                        i = new Intent(view.getContext(), MoreHighlightedActivity.class);
                        break;
                    case TIMELINE_TYPE:
                        i = new Intent(view.getContext(), MoreFriendsInstallsActivity.class);
                        break;
                    case REVIEWS_TYPE:
                        i = new Intent(view.getContext(), MoreReviewsActivity.class);
                        if (storeId > 0) {
                            i.putExtra(Constants.STOREID_KEY, storeId);
                        }
                        break;
                    case COMMENTS_TYPE:
                        i = new Intent(view.getContext(), MoreCommentsActivity.class);
                        break;
                    case EVENT_FACEBOOK_TYPE:
                        sendActionEvent(view, AptoideUtils.SocialMedia.getFacebookPageURL(view.getContext(), row
                                .getEventActionUrl()));
                        break;
                    case EVENT_TWITCH_TYPE:
                    case EVENT_YOUTUBE_TYPE:
                    default:
                        sendActionEvent(view, row.getEventActionUrl());
                        break;
                }

                if (row.getHomepage()) {
                    AptoideUtils.FlurryAppviewOrigin.addAppviewOrigin(row.getLabel() + " More");
                }
                if (i != null) {
                    i.putExtra(Constants.EVENT_ACTION_URL, row.getEventActionUrl());
                    i.putExtra(Constants.EVENT_NAME, row.getEventName());
                    i.putExtra(Constants.EVENT_TYPE, row.getEventType());
                    i.putExtra(Constants.EVENT_LABEL, row.getLabel());
                    i.putExtra(Constants.LOCALYTICS_TAG, row.getTag());
                    i.putExtra(Constants.THEME_KEY, theme == null ? 0 : theme.ordinal());
                    i.putExtra(Constants.HOMEPAGE_KEY, row.getHomepage());
                    if (i.getLongExtra(Constants.STOREID_KEY, 0) == 0) {
                        i.putExtra(Constants.STOREID_KEY, row.getStoreId());
                    }
                    view.getContext().startActivity(i);
                }
            }
        }

        private void sendActionEvent(View view, String eventActionUrl) {
            Intent i;
            if (eventActionUrl != null) {
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(eventActionUrl));
                view.getContext().startActivity(i);
            }
        }
    }
}
