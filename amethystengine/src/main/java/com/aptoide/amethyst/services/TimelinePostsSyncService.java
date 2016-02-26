package com.aptoide.amethyst.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.webservices.TimelineCheckRequestSync;
import com.aptoide.amethyst.webservices.json.TimelineActivityJson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import com.aptoide.amethyst.MainActivity;

/**
 * Created by fabio on 09-11-2015.
 */
public class TimelinePostsSyncService {
    private static final Object wiSyncAdapterLock = new Object();





    public void sync(Context context, String packageName) {
        try {
            Log.d("AptoideTimeline", "Starting timelinePostsService");
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());
            if(sPref.contains("timelineTimestamp") && sPref.getBoolean("socialtimelinenotifications", true)) {


                TimelineActivityJson timelineActivityJson = TimelineCheckRequestSync.getRequest("new_installs");

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("fromTimeline", true);

                intent.setClassName(packageName, MainActivity.class.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_aptoide_fb_notification)
                        .setContentIntent(resultPendingIntent)
                        .setOngoing(false)
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.notification_timeline_posts))
                        .setContentText(context.getString(R.string.notification_social_timeline)).build();
                notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
                ArrayList<String> avatarLinks = new ArrayList<String>();

                try {

                    setAvatares(avatarLinks, timelineActivityJson.getNew_installs().getFriends());

                    if (Build.VERSION.SDK_INT >= 16) {
                        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.push_notification_timeline_activity);
                        expandedView.setTextViewText(R.id.description, context.getString(R.string.notification_timeline_posts));
                        expandedView.removeAllViews(R.id.linearLayout2);
                        for (String avatar : avatarLinks) {
                            Bitmap loadedImage = ImageLoader.getInstance().loadImageSync(avatar);
                            RemoteViews imageView = new RemoteViews(context.getPackageName(), R.layout.timeline_friend_iv);
                            imageView.setImageViewBitmap(R.id.friend_avatar, loadedImage);
                            expandedView.addView(R.id.linearLayout2, imageView);
                        }

                        notification.bigContentView = expandedView;
                    }


                    if (!avatarLinks.isEmpty()) {
                        final NotificationManager managerNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        managerNotification.notify(86459, notification);
                    }


                } catch (NullPointerException ignored) {
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setAvatares(List<String> avatares, List<TimelineActivityJson.Friend> friends){
        for(TimelineActivityJson.Friend friend : friends){
            if(!avatares.contains(friend.getAvatar())){
                avatares.add(friend.getAvatar());

            }
        }
    }

}
