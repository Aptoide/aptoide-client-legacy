package com.aptoide.amethyst.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.v7.GetApp;
import com.aptoide.models.ApkPermission;
import com.aptoide.models.ApkPermissionGroup;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsousa on 18/11/15.
 *
 * TODO: fix size of scrollview to shrink when not enough permissions on screen
 * <p/>
 */
public class DialogPermissions extends DialogFragment {

    private GetApp getApp;
    private String appName;
    private String versionName;
    private String icon;
    private String size;

    @Override
    public void onPause() {
        dismiss();
        super.onPause();
    }
    public static DialogPermissions newInstance(GetApp getApp, String appName, String versionName, String icon, String size) {

        DialogPermissions dialog = new DialogPermissions();
        dialog.getApp = getApp;
        dialog.appName = appName;
        dialog.versionName = versionName;
        dialog.icon = icon;
        dialog.size = size;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_permissions, null);
        AlertDialog builder = new AlertDialog.Builder(getActivity()).setView(v).create();

        v.findViewById(R.id.dialog_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tvAppInfo = (TextView) v.findViewById(R.id.dialog_app_info);
        tvAppInfo.setText(getString(R.string.dialog_version_size, versionName, size));

        TextView tvAppName = (TextView) v.findViewById(R.id.dialog_app_name);
        tvAppName.setText(appName);


        Glide.with(this).load(icon).into((ImageView) v.findViewById(R.id.dialog_appview_icon));

        final TableLayout tableLayout = (TableLayout) v.findViewById(R.id.dialog_table_permissions);

        final List<String> usedPermissions = getApp.nodes.meta.data.file.usedPermissions;

        List<ApkPermission> apkPermissions = AptoideUtils.AppUtils.parsePermissions(getContext(), usedPermissions);
        final ArrayList<ApkPermissionGroup> apkPermissionsGroup = AptoideUtils.AppUtils.fillPermissionsGroups(apkPermissions);

        if (apkPermissionsGroup.size() == 0) {
            TextView noPermissions = new TextView(getContext());
            noPermissions.setText(getString(R.string.no_permissions_required));
            noPermissions.setPadding(5, 5, 5, 5);
        } else {
            AptoideUtils.AppUtils.fillPermissionsForTableLayout(getContext(), tableLayout, apkPermissionsGroup);
        }

        return builder;
    }

}
