package com.aptoide.amethyst.adapter.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.HeaderRow;
import com.aptoide.models.displayables.UpdateHeaderRow;
import com.aptoide.models.displayables.InstallRow;
import com.aptoide.models.displayables.UpdateRow;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.aptoide.amethyst.AppViewActivity;

import com.aptoide.amethyst.UninstallRetainFragment;
import com.aptoide.amethyst.UploadApkActivity;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.amethyst.viewholders.DummyBaseViewHolder;
import com.aptoide.amethyst.viewholders.main.HeaderViewHolder;
import com.aptoide.amethyst.viewholders.main.InstalledViewHolder;
import com.aptoide.amethyst.viewholders.main.UpdateViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class UpdatesTabAdapter extends RecyclerView.Adapter<BaseViewHolder> implements SpannableRecyclerAdapter {

    private final FragmentActivity activity;
    private List<Displayable> displayableList;

    public UpdatesTabAdapter(List<Displayable> displayableList, FragmentActivity activity) {
        this.displayableList = displayableList;
        this.setHasStableIds(false);
        this.activity = activity;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.installed_row) {
            return new InstalledViewHolder(view, viewType);
        } else if (viewType == R.layout.update_row) {
            return new UpdateViewHolder(view, viewType);
        } else if (viewType == R.layout.layout_header) {
            return new HeaderViewHolder(view, viewType, EnumStoreTheme.APTOIDE_STORE_THEME_DEFAULT);
        } else {
            return new DummyBaseViewHolder(view, viewType);
        }
    }

    Executor ex = Executors.newSingleThreadExecutor();

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {

        if (holder.viewType == R.layout.layout_header) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            HeaderRow headerRow = (HeaderRow) displayableList.get(position);
            headerViewHolder.title.setText(headerRow.getLabel());
            headerViewHolder.more.setText(headerViewHolder.more.getContext().getString(R.string.update_all));
            ViewGroup.LayoutParams layoutParams = headerViewHolder.more.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            headerViewHolder.more.setLayoutParams(layoutParams);
            headerViewHolder.more.setVisibility(!headerRow.isHasMore() ? View.GONE : View.VISIBLE);
            headerViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<UpdateRow> updateRows = new ArrayList<>();
                    for (Displayable displayable : displayableList) {
                        if (displayable instanceof UpdateRow) {
                            UpdateRow row = (UpdateRow) displayable;
                            updateRows.add(row);
                        }
                        Analytics.Updates.updateAll();
                    }
                    BusProvider.getInstance().post(new OttoEvents.StartDownload(updateRows));
                }
            });
        } else if (holder.viewType == R.layout.update_row) {
            final UpdateViewHolder updateViewHolder = (UpdateViewHolder) holder;

            final UpdateRow appItem = (UpdateRow) displayableList.get(position);

            updateViewHolder.name.setText(appItem.appName);
            updateViewHolder.appInstalledVersion.setText(appItem.versionNameInstalled);
            updateViewHolder.appUpdateVersion.setText(String.valueOf(appItem.versionName));
            updateViewHolder.updateButtonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Displayable displayable = displayableList.get(holder.getAdapterPosition());
                    if (displayable instanceof UpdateRow) {
                        UpdateRow row = (UpdateRow) displayable;
                        List<UpdateRow> updateRows = new ArrayList<>();
                        updateRows.add(row);
                        BusProvider.getInstance().post(new OttoEvents.StartDownload(updateRows));
                    }
                    Analytics.Updates.update();
                }
            });

            updateViewHolder.itemView.setLongClickable(true);
            updateViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.exclude_update_uninstall_menu, null);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    dialogView.findViewById(R.id.confirmIgnoreUpdateUninstall).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
                                    database.addToExcludeUpdate(appItem);
                                    BusProvider.getInstance().post(new OttoEvents.ExcludedUpdateAddedEvent(position));
                                }
                            }).start();
                            int position = holder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                displayableList.remove(position);
                                notifyItemRemoved(position);
                            }

                            // When we remove the last update, we also need to remove the header.
                            checkAndRemoveHeaderRow(holder.getAdapterPosition());
                            Toast.makeText(v.getContext(), "Ignored", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    return true;
                }

                /**
                 * Check if there's any Update on the displayableList and if so, removes the Update header
                 */
                void checkAndRemoveHeaderRow(int position) {
                    if (isUpdateListEmpty()) {
                        for (Iterator<Displayable> it = displayableList.iterator(); it.hasNext(); ) {
                            Displayable displayable = it.next();
                            if (displayable instanceof UpdateHeaderRow) {
                                it.remove();
                                notifyItemRemoved(position);
                            }
                        }
                    }
                }

                /**
                 * Iterates through the displayableList and check if there's an Update row type.
                 * @return
                 */
                boolean isUpdateListEmpty() {
                    for (Displayable displayable : displayableList) {
                        if (displayable instanceof UpdateRow) {
                            return false;
                        }
                    }
                    return true;
                }
            });
            updateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AppViewActivity.class);
                    i.putExtra(Constants.UPDATE_FROM_KEY, true);
                    i.putExtra(Constants.MD5SUM_KEY, appItem.md5sum);
                    i.putExtra(Constants.PACKAGENAME_KEY, appItem.packageName);
                    i.putExtra(Constants.VERSIONNAME_KEY, appItem.versionName);
                    i.putExtra(Constants.APPNAME_KEY, appItem.appName);
                    i.putExtra(Constants.STORENAME_KEY, appItem.storeName);
                    i.putExtra(Constants.ICON_KEY, appItem.icon);
                    i.putExtra(Constants.DOWNLOAD_FROM_KEY, "updates"); // renamed from: recommended_apps
                    v.getContext().startActivity(i);
                }
            });
            Glide.with(updateViewHolder.itemView.getContext()).load(appItem.icon).asBitmap().into(updateViewHolder.icon);
        } else if (holder.viewType == R.layout.installed_row) {
            final InstalledViewHolder installedViewHolder = (InstalledViewHolder) holder;

            final InstallRow installRow = (InstallRow) displayableList.get(position);

            installedViewHolder.name.setText(installRow.appName);
            installedViewHolder.tvAppVersion.setText(installRow.versionName);
            installedViewHolder.createReviewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UploadApkActivity.class);

                    String packageName = installRow.packageName;

                    intent.putExtra("package_name", packageName);
                    v.getContext().startActivity(intent);

                    Analytics.Updates.createReview();
                }
            });
            installedViewHolder.installedItemFrame.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.exclude_update_uninstall_menu, null);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    ((TextView) dialogView.findViewById(R.id.tvUpdateUninstall)).setText(R.string.updatesTabAdapterUninstall);
                    dialogView.findViewById(R.id.confirmIgnoreUpdateUninstall).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Fragment fragment = new UninstallRetainFragment();
                            Bundle args = new Bundle();
                            args.putString("name", installRow.appName);
                            args.putString("package", installRow.packageName);
                            args.putString("version", installRow.versionName);
                            args.putString("icon", installRow.icon);
                            fragment.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().add(fragment, "uninstall").commit();

//                                displayableList.remove(holder.getAdapterPosition());
//                                notifyItemRemoved(holder.getAdapterPosition());

                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();

                    return true;
                }
            });
            installedViewHolder.installedItemFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = activity.getPackageManager().getLaunchIntentForPackage(installRow.packageName);
                    if (installedViewHolder.itemView != null && installedViewHolder.itemView.getContext() != null && intent != null) {
                        installedViewHolder.itemView.getContext().startActivity(intent);
                    }
                }
            });
//                installedViewHolder.downloads.setText(String.valueOf(installRow.versionName));
//                installedViewHolder.ratingBar.setRating((float) installRow.rating);

            //installedViewHolder.icon.setImageURI(Uri.parse(installRow.icon));

            Glide.with(installedViewHolder.itemView.getContext()).load(installRow.icon).into(installedViewHolder.icon);
        }

    }




    @Override
    public int getItemCount() {
        return displayableList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Displayable displayable = displayableList.get(position);

        if (displayable instanceof InstallRow) {
            return R.layout.installed_row;
        } else if (displayable instanceof UpdateRow) {
            return R.layout.update_row;
        } else if (displayable instanceof HeaderRow) {
            return R.layout.layout_header;
        } else {
            throw new IllegalStateException("InvalidType");
        }

    }

    @Override
    public int getSpanSize(int position) {
        return displayableList.get(position).getSpanSize();
    }
}
