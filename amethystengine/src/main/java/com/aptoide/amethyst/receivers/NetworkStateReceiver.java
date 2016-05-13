package com.aptoide.amethyst.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.utils.AptoideUtils;

import com.aptoide.amethyst.ui.ScheduledDownloadsActivity;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class NetworkStateReceiver extends BroadcastReceiver {
    public NetworkStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        handleScheduledDownloads(context, intent);
    }

    private void handleScheduledDownloads(@NonNull final Context context, @NonNull final Intent intent) {
        if (intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1) != TYPE_WIFI
                || !AptoideUtils.NetworkUtils.isAvailable(context, TYPE_WIFI)) {
            return;
        }
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("schDwnBox", false)
                || !AptoideUtils.NetworkUtils.isGeneralDownloadPermitted(context)) {
            return;
        }

        if (new AptoideDatabase(Aptoide.getDb()).hasScheduledDownloads()) {
            final Intent i = ScheduledDownloadsActivity.newIntent(context, true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
