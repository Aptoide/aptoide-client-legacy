package com.aptoide.amethyst.viewholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.models.displayables.Displayable;



/**
 * Created by rmateus on 02/06/15.
 */
public class ExcludedUpdateViewHolder extends BaseViewHolder {

    public ImageView app_icon;
    public TextView tv_name;
    public TextView tv_vercode;
    public TextView tv_apkid;
    public CheckBox cb_exclude;

    public ExcludedUpdateViewHolder(View itemView, int viewType) {
        super(itemView, viewType);

    }

    @Override
    public void populateView(Displayable displayable) {

    }

    @Override
    protected void bindViews(View itemView) {
        app_icon = (ImageView)itemView.findViewById(R.id.app_icon);
        tv_name = (TextView)itemView.findViewById(R.id.tv_name);
        tv_vercode = (TextView)itemView.findViewById(R.id.tv_vercode);
        tv_apkid = (TextView)itemView.findViewById(R.id.tv_apkid);
        cb_exclude = (CheckBox)itemView.findViewById(R.id.cb_exclude);
    }
}