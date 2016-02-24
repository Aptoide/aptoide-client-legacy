package com.aptoide.amethyst.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.ui.IMediaObject;
import com.aptoide.amethyst.ui.Screenshot;
import com.aptoide.amethyst.ui.Video;
import com.aptoide.amethyst.ui.listeners.MediaObjectListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;


import com.aptoide.amethyst.viewholders.main.ScreenshotsViewHolder;

/**
 * Created by gmartinsribeiro on 01/12/15.
 */
public class ScreenshotsAdapter extends RecyclerView.Adapter<ScreenshotsViewHolder> {

    private final RequestManager glide;
    private ArrayList<IMediaObject> items;
    private int numberOfVideos;

    public ScreenshotsAdapter(final RequestManager glide, ArrayList<IMediaObject> items) {
        this.glide = glide;
        this.items = items;
        this.numberOfVideos = getNumberOfVideos(items);
    }
    @Override
    public ScreenshotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        View inflate = LayoutInflater.from(context).inflate(R.layout.row_item_screenshots_gallery, parent, false);

        return new ScreenshotsViewHolder(inflate, viewType);
    }

    @Override
    public void onBindViewHolder(ScreenshotsViewHolder holder, int position) {
        IMediaObject item = items.get(position);

        if (item instanceof Screenshot) {
            String thumbnail = AptoideUtils.UI.screenshotToThumb(holder.itemView.getContext(), item.getImageUrl(), ((Screenshot) item).getOrient());
            holder.media_layout.setForeground(null);
            holder.play_button.setVisibility(View.GONE);
            glide.load(thumbnail).placeholder(getPlaceholder(((Screenshot) item).getOrient())).into(holder.screenshot);
            holder.screenshot.setOnClickListener(new MediaObjectListener.ScreenShotsListener(holder.itemView.getContext(), getURLs(holder.itemView.getContext(), items), position - numberOfVideos));
            holder.media_layout.setOnClickListener(new MediaObjectListener.ScreenShotsListener(holder.itemView.getContext(), getURLs(holder.itemView.getContext(), items), position - numberOfVideos));
        }else if (item instanceof Video) {
            glide.load(item.getImageUrl()).placeholder(R.drawable.placeholder_300x300).into(holder.screenshot);
            holder.media_layout.setForeground(holder.itemView.getContext().getResources().getDrawable(R.color.overlay_black));
            holder.play_button.setVisibility(View.VISIBLE);
            holder.screenshot.setOnClickListener(new MediaObjectListener.VideoListener(holder.itemView.getContext(), ((Video) item).getVideoUrl()));
            holder.media_layout.setOnClickListener(new MediaObjectListener.VideoListener(holder.itemView.getContext(), ((Video) item).getVideoUrl()));
        }
    }

    private int getPlaceholder(String orient) {
        int id;
        if(orient != null && orient.equals("portrait")){
            id = R.drawable.placeholder_144x240;
        }else{
            id = R.drawable.placeholder_256x160;
        }
        return id;
    }

    private int getNumberOfVideos(ArrayList<IMediaObject> items) {
        int result = 0;
        for(IMediaObject item: items){
            if (item instanceof Video) {
                result++;
            }
        }
        return result;
    }

    private ArrayList<String> getURLs(Context ctx, ArrayList<IMediaObject> items) {
        ArrayList<String> urls = new ArrayList<>();
        for (IMediaObject mo : items) {
            if (mo instanceof Screenshot) {
                urls.add(AptoideUtils.UI.screenshotToThumb(ctx, mo.getImageUrl(), ((Screenshot) mo).getOrient()));
            }
        }
        return urls;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
