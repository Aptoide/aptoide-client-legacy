package com.aptoide.amethyst.adapter.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


import com.aptoide.amethyst.webservices.timeline.json.ApkInstallComments;

/**
 * Created by fabio on 14-10-2015.
 */
public class TimelineCommentsAdapter extends BaseAdapter {

    private final List<ApkInstallComments.Comment> list;
    protected Context ctx;

    public TimelineCommentsAdapter(Context ctx, List<ApkInstallComments.Comment> list) {
        this.list = list;
        this.ctx = ctx;
    }


    @Override
    public int getCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public ApkInstallComments.Comment getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        class ViewHolder {
            public ImageView avatar;
            public TextView personName;
            public TextView comment;
            public TextView timestamp;
        }

        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.row_timeline_comments, null);

            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.timeline_user_avatar);
            holder.personName = (TextView) convertView.findViewById(R.id.timeline_user_comment);
            holder.comment = (TextView) convertView.findViewById(R.id.timeline_comment);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timeline_comment_time);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }


        ApkInstallComments.Comment comment = getItem(position);

        Glide.with(parent.getContext()).load(comment.getAvatar()).into(holder.avatar);
        holder.personName.setText(comment.getUsername());
        holder.comment.setText(comment.getText());
        holder.timestamp.setText(getTime(comment.getTimestamp()));

//	    holder.appIcon.setVisibility(View.GONE);        // Hide an element

        return convertView;
    }

    private static String getTime(String time) {
        String result = "";
        final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            result = AptoideUtils.DateTimeUtils.getInstance(Aptoide.getContext()).getTimeDiffString(Aptoide.getContext(), dateFormater.parse(time).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}

