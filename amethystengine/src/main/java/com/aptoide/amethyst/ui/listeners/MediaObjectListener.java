package com.aptoide.amethyst.ui.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.aptoide.amethyst.ui.ScreenshotsViewer;

import java.util.ArrayList;

/**
 * Created by hsousa on 10/08/15.
 */
public class MediaObjectListener {


    public static class ScreenShotsListener implements View.OnClickListener {

        private Context context;
        private final int position;
        private ArrayList<String> urls;

        public ScreenShotsListener(Context context, ArrayList<String> urls, int position) {
            this.context = context;
            this.position = position;
            this.urls = urls;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ScreenshotsViewer.class);
            intent.putStringArrayListExtra("url", urls);
            intent.putExtra("position", position);
            context.startActivity(intent);
            //FlurryAgent.logEvent("App_View_Clicked_On_Screenshot");
        }
    }

    public static class VideoListener implements View.OnClickListener {

        private Context context;
        private String videoUrl;

        public VideoListener(Context context, String videoUrl) {
            this.context = context;
            this.videoUrl = videoUrl;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            context.startActivity(intent);
            //FlurryAgent.logEvent("App_View_Clicked_On_Video");
        }
    }
}
