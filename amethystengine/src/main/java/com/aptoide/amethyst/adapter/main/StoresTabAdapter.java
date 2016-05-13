package com.aptoide.amethyst.adapter.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.database.schema.Schema;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.displayables.StoreItem;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.AddStoreRow;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.stores.Store;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


import com.aptoide.amethyst.StoresActivity;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.main.AddStoreViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.amethyst.viewholders.main.StoreItemRowViewHolder;

/**
 * Created by hsousa on 22-06-2015.
 */
public class StoresTabAdapter extends RecyclerView.Adapter<BaseViewHolder> implements SpannableRecyclerAdapter {

    private List<Displayable> displayableList;

    public StoresTabAdapter(List<Displayable> displayableList) {
        this.displayableList = displayableList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder;

        if (viewType == R.layout.layout_header) {
            holder = new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType, EnumStoreTheme
                    .APTOIDE_STORE_THEME_DEFAULT);
        } else if (viewType == R.layout.row_store_item) {
            holder = new StoreItemRowViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType);
        } else if (viewType == R.layout.add_store_row) {
            holder = new AddStoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType);
        } else {
            throw new IllegalStateException("Invalid ViewType");
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {

        if (viewHolder.viewType == R.layout.layout_header) {
            HeaderRow row = (HeaderRow) displayableList.get(position);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.title.setText(row.getLabel());
        } else if (viewHolder.viewType == R.layout.row_store_item) {
            final StoreItem storeItem = (StoreItem) displayableList.get(position);
            StoreItemRowViewHolder holder = (StoreItemRowViewHolder) viewHolder;
            holder.storeName.setText(storeItem.storeName);

            @ColorInt int color = holder.itemView.getContext().getResources().getColor(storeItem.getStoreHeaderColor());
            holder.storeLayout.setBackgroundColor(color);
            holder.storeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), StoresActivity.class);
                    intent.putExtra(Constants.STOREID_KEY, storeItem.id);
                    intent.putExtra(Constants.STORENAME_KEY, storeItem.storeName);

                    intent.putExtra(Constants.STOREAVATAR_KEY, storeItem.storeAvatar);
                    intent.putExtra(Constants.THEME_KEY, storeItem.getThemeId());
                    intent.putExtra(Constants.DOWNLOAD_FROM_KEY, "store");
                    intent.putExtra(Constants.STORE_SUBSCRIBED_KEY, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    view.getContext().startActivity(intent);
                }
            });

            final Context context = viewHolder.itemView.getContext();
            if (storeItem.id == -1 || TextUtils.isEmpty(storeItem.storeAvatar)) {
                Glide.with(context).fromResource().load(R.drawable.ic_avatar_apps).transform(new CircleTransform(context)).into(holder.storeAvatar);
            } else {
                Glide.with(context).load(storeItem.storeAvatar).transform(new CircleTransform(context)).into(holder.storeAvatar);
            }

            holder.storeUnsubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AptoideDialog.msgBoxYesNo(view.getContext(), storeItem.storeName, Aptoide.getContext().getString(R.string.unsubscribe_yes_no), new
                            DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i == AptoideDialog.MSG_BUTTON_YES) {
                                removeStore(storeItem.id);
                                Toast.makeText(view.getContext(), Aptoide.getContext().getString(R.string.unsubscribed_) + storeItem.storeName, Toast
                                        .LENGTH_SHORT).show();
                            }
                        }
                    }, true);
                }
            });

            // in order to have subscribers and downloads, we'll need to change the db schema and persists that info
            holder.infoLayout.setVisibility(View.GONE);
        }
    }

    /**
     * When implementing MultiChoiceSelection, remove this method.
     *
     * @param id the Id of the store to be removed.
     */
    private void removeStore(long id) {
        List<Long> list = new ArrayList<>();
        list.add(id);
        removeStores(list);
    }

    /**
     * with the ids, retrieves the stores from the database and post {@link OttoEvents.RepoDeletedEvent}
     * @param checkedItems list of store Ids to be removed
     */
    public static void removeStores(final List<Long> checkedItems) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
                List<Store> stores = new ArrayList<>(checkedItems.size());
                for (Long id : checkedItems) {
                    Cursor c = database.getStore(id);
                    Store store = new Store();

                    if (c.moveToFirst()) {
                        store.setName(c.getString(c.getColumnIndex(Schema.Repo.COLUMN_NAME)));
                        store.setId(c.getLong(c.getColumnIndex(Schema.Repo.COLUMN_ID)));
                    }
                    c.close();
                    stores.add(store);
                }

                database.removeStores(stores);

                BusProvider.getInstance().post(new OttoEvents.RepoDeletedEvent(stores));
            }
        }).start();
    }

    @Override
    public int getItemViewType(int position) {
        if (displayableList.get(position) instanceof HeaderRow) {
            return R.layout.layout_header;
        }
        if (displayableList.get(position) instanceof AddStoreRow) {
            return R.layout.add_store_row;
        }
        return R.layout.row_store_item;
    }

    @Override
    public int getItemCount() {
        return displayableList.size();
    }

    @Override
    public int getSpanSize(int position) {
        return displayableList.get(position).getSpanSize();
    }
}
