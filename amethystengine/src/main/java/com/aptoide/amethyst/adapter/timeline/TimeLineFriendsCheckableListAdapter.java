package com.aptoide.amethyst.adapter.timeline;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;

import com.aptoide.amethyst.R;
import com.bumptech.glide.Glide;

import java.util.List;


import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.webservices.timeline.json.Friend;

/**
 * Created by fabio on 14-10-2015.
 */
public class TimeLineFriendsCheckableListAdapter extends ArrayAdapter<Friend> {
    private Context ctx;

    public TimeLineFriendsCheckableListAdapter(Context context, List<Friend> items) {
        super(context, 0, items);
        this.ctx = context;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final View v;
        ViewHolder holder;

        if(convertView == null){
            v = LayoutInflater.from(ctx).inflate(R.layout.row_facebook_invite_friends, parent, false);
            holder = new ViewHolder();
            holder.name = (CheckedTextView) v.findViewById(R.id.username);
            holder.avatarImage = (ImageView) v.findViewById(R.id.user_avatar);

            v.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
            v = convertView;
        }


        Friend friend = getItem(position);

        holder.name.setChecked(((ListView)parent).isItemChecked(position));
        holder.name.setText(friend.getUsername());
        Log.d("AptoideDebug", friend.getUsername());
        Glide.with(ctx).load(friend.getAvatar()).transform(new CircleTransform(ctx)).into(holder.avatarImage);
        return v;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public CheckedTextView name;
        public ImageView avatarImage;
    }
}

