package com.aptoide.amethyst.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;

import com.aptoide.amethyst.Aptoide;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by hsousa on 04/08/15.
 */
public class GlideUtils {

    /**
     * Shows a dialog loading an image icon from cache only
     * Motivations behind this class: replacing ImageLoader from nostra13
     *
     * @param dialog
     * @param path
     * @param context
     */
    public static void showDialogWithGlideLoadingIconFromCache(final AlertDialog dialog, String path, Context context) {

        final long start = System.currentTimeMillis();
        Glide.with(context)
                .using(GlideUtils.DownloadOnlyFromCacheLoader)
                .load(path)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        final long end = System.currentTimeMillis();
                        System.out.println("APTOIDE-DEBUG: (ready) time loading image: " + (end-start));
                        dialog.setIcon(resource);
                        dialog.show();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        final long end = System.currentTimeMillis();

                        System.out.println("APTOIDE-DEBUG: (error) time loading image: " + (end-start));
                        dialog.show();
                        super.onLoadFailed(e, errorDrawable);
                    }
                });
    }

    public static void downloadOnlyFromCache(Context context, String url, ImageView imageView) {

        Glide.with(context)
                .using(DownloadOnlyFromCacheLoader)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


    public static void download(Context context, String url, ImageView imageView) {
        if (Aptoide.DEBUG_MODE) {
            downloadWithDebug(context, url, imageView);
        } else {
            Glide.with(context).load(url).asBitmap().into(imageView);
        }
    }

    public static void downloadDontTransform(Context context, String url, ImageView imageView) {
        if (Aptoide.DEBUG_MODE) {
            downloadDontTransformWithDebug(context, url, imageView);
        } else {
            Glide.with(context).load(url).dontTransform().into(imageView);
        }
    }

    private static void downloadWithDebug(Context context, String url, ImageView imageView) {

        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Logger.e("APTOIDE-GLIDE", "onException " + (e == null ? "null" : e.getMessage()) + " model=" + model);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Logger.e("APTOIDE-GLIDE", "onResourceReady" + " model=" + model);
                        return false;
                    }
                })
                .into(imageView);
    }

    private static void downloadDontTransformWithDebug(Context context, String url, ImageView imageView) {

        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Logger.e("APTOIDE-GLIDE", "onException" + (e == null ? "null" : e.getMessage()) + " model=" + model);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Logger.e("APTOIDE-GLIDE", "onResourceReady" + " model=" + model);
                        return false;
                    }
                })
                .dontTransform()
                .into(imageView);
    }

    @WorkerThread
    public static Bitmap downloadOnlyFromCache(Context context, String url, int width, int height) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        Bitmap myBitmap = Glide.with(context)
                .using(DownloadOnlyFromCacheLoader)
                .load(url)
                .asBitmap()
                .centerCrop()
                .into(width, height)
                .get();
        long end = System.currentTimeMillis();
        System.out.println("downloadOnlyFromCache took " + (end-start) + "ms");

        return myBitmap;
    }


        public static StreamModelLoader<String> DownloadOnlyFromCacheLoader = new StreamModelLoader<String>() {
        @Override
        public DataFetcher<InputStream> getResourceFetcher(final String model, int i, int i1) {
            return new DataFetcher<InputStream>() {
                @Override
                public InputStream loadData(Priority priority) {
                    return null;
                }

                @Override
                public void cleanup() {

                }

                @Override
                public String getId() {
                    return model;
                }

                @Override
                public void cancel() {

                }
            };
        }
    };
}
