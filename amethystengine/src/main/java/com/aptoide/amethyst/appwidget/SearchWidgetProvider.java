package com.aptoide.amethyst.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.aptoide.amethyst.R;

/**
 * Created by brutus on 02-01-2014.
 */
public class SearchWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, SearchWidgetActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), getLayout());
            remoteViews.setOnClickPendingIntent(getLayoutTextId(), pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }

    public int getLayoutTextId() {
        return R.id.search_widget_text;
    }

    public int getLayout() {
        return R.layout.search_widget;
    }
}