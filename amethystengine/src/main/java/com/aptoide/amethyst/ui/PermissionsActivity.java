package com.aptoide.amethyst.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.downloadmanager.DownloadUtils;
import com.aptoide.amethyst.downloadmanager.model.FinishedApk;
import com.aptoide.amethyst.utils.GlideUtils;
import com.aptoide.models.ApkPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 17-09-2013
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class PermissionsActivity extends Activity {


    static private ExecutorService executor = Executors.newFixedThreadPool(10);
    Context context;
    private FinishedApk apk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        this.context = this;
        apk = getIntent().getParcelableExtra("apk");

        ArrayList<String> permissionsList = getIntent().getStringArrayListExtra("permissions");

        try {

            List<String> list = Arrays.asList(getPackageManager().getPackageInfo(apk.getApkid(), PackageManager.GET_PERMISSIONS).requestedPermissions);
            ArrayList<String> installedPermissionsList = new ArrayList<>(list);

//            Log.d("TAG", "Installed permissions: " + installedPermissionsList.size() + " " + installedPermissionsList);
//            Log.d("TAG", "apk permissions: " + permissionsList.size() + " " + permissionsList);

            permissionsList.removeAll(installedPermissionsList);

//            Log.d("TAG", "apk permissions 2: " + permissionsList.size() + " " + permissionsList);

            ArrayList<ApkPermission> descriptionList = permissions(context, permissionsList);
            if (!permissionsList.isEmpty() && !descriptionList.isEmpty()) {
                permissionsDialog(apk, descriptionList);
            } else {
                DownloadUtils.installWithRoot(apk);
                finish();
            }

        } catch (PackageManager.NameNotFoundException e) {

            try {
                permissionsDialog(apk, permissions(context, permissionsList));
            } catch (NullPointerException ignore) {
                DownloadUtils.installWithRoot(apk);
                finish();
            }

        } catch (NullPointerException e) {
            DownloadUtils.installWithRoot(apk);
            finish();
        }
    }


    public static ArrayList<ApkPermission> permissions(Context context, ArrayList<String> permissionArray) {
        PackageManager pm = context.getPackageManager();

        CharSequence csPermissionGroupLabel;
        CharSequence csPermissionLabel;
        List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(0);
        ArrayList<ApkPermission> list = new ArrayList<>();

        if (permissionArray != null) {
            for (int i = 0; i != permissionArray.size(); i++) {
                String permission = permissionArray.get(i);

                for (PermissionGroupInfo pgi : lstGroups) {
                    try {
                        List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, 0);
                        for (PermissionInfo pi : lstPermissions) {
                            if (pi.name.equals(permission)) {
                                csPermissionLabel = pi.loadLabel(pm);
                                csPermissionGroupLabel = pgi.loadLabel(pm);
                                list.add(new ApkPermission(csPermissionGroupLabel.toString(), csPermissionLabel.toString()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            Collections.sort(list, new Comparator<ApkPermission>() {
                @Override
                public int compare(ApkPermission lhs, ApkPermission rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }

        return list;
    }

    private void permissionsDialog(final FinishedApk viewApk, ArrayList<ApkPermission> permissions) {

        View v = LayoutInflater.from(context).inflate(R.layout.app_permission, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.container);

        if (permissions != null) {

            if (!permissions.isEmpty()) {
                for (int i = 0; i < permissions.size(); i++) {
                    ApkPermission permission = permissions.get(i);

                    View permissionView = LayoutInflater.from(context).inflate(R.layout.row_permission, null);
                    if (i == 0 || !permissions.get(i - 1).getName().equals(permission.getName())) {
                        ((TextView) permissionView.findViewById(R.id.permission_name)).setText(permission.getName());
                    } else {
                        permissionView.findViewById(R.id.permission_name).setVisibility(View.GONE);
                    }
                    ((TextView) permissionView.findViewById(R.id.permission_description)).setText(permission.getDescription());
                    layout.addView(permissionView);
                }
            } else {
                TextView tv = new TextView(context);
                tv.setPadding(10, 10, 10, 10);
                tv.setText(context.getString(R.string.no_permissions_required));
                layout.addView(tv);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(context).setView(v).create();
        dialog.setTitle(context.getString(R.string.restore) + " " + viewApk.getName() + "?");
        //dialog.setIcon(BitmapDrawable.createFromPath(ImageLoader.getInstance().getDiscCache().get(path).getAbsolutePath()));

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        DownloadUtils.installWithRoot(viewApk);
                    }
                });
                finish();
            }
        });

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.setCancelable(false);
        //dialog.show();

        String path = viewApk.getIconPath();
        GlideUtils.showDialogWithGlideLoadingIconFromCache(dialog, path, this);
    }


}