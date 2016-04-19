package com.aptoide.amethyst.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.dialogs.AdultDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.preferences.ManagerPreferences;
import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.LifeCycleMonitor;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.ChangeUserSettingsRequest;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.text.DecimalFormat;


import com.aptoide.amethyst.webservices.json.GetUserSettingsJson;

/**
 * Created by fabio on 26-10-2015.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppCompatDelegate mDelegate;

    String aptoide_path = Aptoide.getConfiguration().getPathCache();
    String icon_path = aptoide_path + "icons/";
    ManagerPreferences preferences;
    Context mctx;
    private boolean unlocked = false;
    private static boolean isSetingPIN = false;

    @Override
    protected void onDestroy() {
//        Analytics.Lifecycle.Activity.onDestroy(this);
        super.onDestroy();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.DESTROY);
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.RESUME);
//        Analytics.Lifecycle.Activity.onResume(this, null);

//        initializeAnalyticsListeners();
    }

//    private void initializeAnalyticsListeners() {
//        findPreference("iconDownloadRules").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("generalDownloadRules").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("hwspecsChkBox").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("checkautoupdate").setOnPreferenceClickListener(onPreferenceClickListener);
////        findPreference("showUpdatesNotification").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("auto_update").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("schDwnBox").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("changetheme").setOnPreferenceClickListener(onPreferenceClickListener);
//        findPreference("allowRoot").setOnPreferenceClickListener(onPreferenceClickListener);
//
//        findPreference("showUpdatesNotification").setOnPreferenceChangeListener(onPreferenceChangeListener);
//
//    }

//    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object o) {
//            final CheckBoxPreference cb = (CheckBoxPreference) preference;
//
//            Analytics.Settings.onSettingChange("Debug  Settings: ", cb.isChecked());
//
//            if (cb.isChecked()) {
//                Analytics.Settings.onSettingChange("Debug  Settings: ", cb.isChecked());
//            } else {
//
//            }
//
//            return false;
//        }
//    };

//    Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
//        @Override
//        public boolean onPreferenceClick(Preference preference) {
//            String key = preference.getKey();
//
//            System.out.println("Debug: event: blabla: " + key);
//
//            switch (key) {
//                case "iconDownloadRules":
//                    Analytics.Settings.onSettingChange("Click on Icon Download Rules");
//                    break;
//                case "generalDownloadRules":
//                    Analytics.Settings.onSettingChange("Click on General Download Rules");
//                    break;
//                case "hwspecsChkBox":
//                    Analytics.Settings.onSettingChange("Check on Filter Applications");
//                    break;
//                case "checkautoupdate":
//                    Analytics.Settings.onSettingChange("Check on Enable Aptoide Auto Update");
//                    break;
////                case "showUpdatesNotification":
////                    Analytics.Settings.onSettingChange("Check on Updates Notification");
////                    break;
//                case "auto_update":
//                    Analytics.Settings.onSettingChange("Check on Enable Auto Update");
//                    break;
//                case "schDwnBox":
//                    Analytics.Settings.onSettingChange("Check on Automatic Install");
//                    break;
//                case "changetheme":
//                    Analytics.Settings.onSettingChange("Check on Themes (Dark/Light)");
//                    break;
//                case "allowRoot":
//                    Analytics.Settings.onSettingChange("Check on Allow Root Installation");
//                    break;
//
////                // apagar
////                case "hwspecs":
////                    Analytics.Settings.onSettingChange("CLick on Hardware Specs");
////                    break;
//
//
//            }
//
//            return false;
//
//        }
//    };

    @Override
    protected void onPause() {
//        Analytics.Lifecycle.Activity.onPause(this);
        super.onPause();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.PAUSE);
    }

    private Dialog DialogSetAdultpin(final Preference mp) {
        isSetingPIN = true;
        final View v = LayoutInflater.from(this).inflate(R.layout.dialog_requestpin, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.asksetadultpinmessage)
                .setView(v)

                .setPositiveButton(R.string.setpin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = ((EditText) v.findViewById(R.id.pininput)).getText().toString();
                        if (!TextUtils.isEmpty(input)) {
                            SecurePreferences.getInstance()
                                    .edit()
                                    .putInt(AdultDialog.MATUREPIN, Integer.valueOf(input))
                                    .commit();
                            mp.setTitle(R.string.remove_mature_pin_title);
                            mp.setSummary(R.string.remove_mature_pin_summary);
//                            FlurryAgent.logEvent("Settings_Added_Pin_To_Lock_Adult_Content");
                            //mp.setOnPreferenceClickListener(removeclick);
                        }
                        isSetingPIN = false;
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSetingPIN = false;
                    }
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isSetingPIN = false;
            }
        });

        return alertDialog;
    }

    private void maturePinSetRemoveClick() {

//        Analytics.Settings.onSettingChange("Click on Set Adult Content Pin");

        int pin = SecurePreferences.getInstance().getInt(AdultDialog.MATUREPIN, -1);
        final Preference mp = findPreference("Maturepin");
        if (pin != -1) {
            // With Pin
            AdultDialog.dialogRequestMaturepin(SettingsActivity.this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == -1) {
                        SecurePreferences.getInstance().edit().putInt(AdultDialog.MATUREPIN, -1).commit();
                        final Preference mp = findPreference("Maturepin");
                        mp.setTitle(R.string.set_mature_pin_title);
                        mp.setSummary(R.string.set_mature_pin_summary);
//                    FlurryAgent.logEvent("Settings_Removed_Pin_Adult_Content");
                    }
                }
            }).show();
        } else {
            DialogSetAdultpin(mp).show();// Without Pin
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.CREATE);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        addPreferencesFromResource(R.xml.preferences);

        mctx = this;
        new GetDirSize().execute(new File(aptoide_path), new File(icon_path));
//        preferences = new ManagerPreferences(mctx);
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
//
//			@Override
//			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//					String key) {
//				preferences.setIconDownloadPermissions(new ViewIconDownloadPermissions(((CheckBoxPreference)findPreference("wifi")).isChecked(),
//						((CheckBoxPreference)findPreference("ethernet")).isChecked(),
//						((CheckBoxPreference)findPreference("4g")).isChecked(),
//						((CheckBoxPreference)findPreference("3g")).isChecked()));
//			}
//		});

        int pin = SecurePreferences.getInstance().getInt(AdultDialog.MATUREPIN, -1);
        final Preference mp = findPreference("Maturepin");
        if (pin != -1) {
            Log.d("PINTEST", "PinBuild");
            mp.setTitle(R.string.remove_mature_pin_title);
            mp.setSummary(R.string.remove_mature_pin_summary);
        }
        mp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                maturePinSetRemoveClick();
                return true;
            }
        });
        findPreference(Constants.MATURE_CHECK_BOX).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final CheckBoxPreference cb = (CheckBoxPreference) preference;

                if (cb.isChecked()) {
                    cb.setChecked(false);
//                    Analytics.Settings.onSettingChange("Check on Filter Adult Content", true);
                    AdultDialog.buildAreYouAdultDialog(SettingsActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                cb.setChecked(true);
//                                AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, true);
                                BusProvider.getInstance().post(new OttoEvents.MatureEvent(true));
//                                Analytics.Settings.onSettingChange("Check on Filter Adult Content", false);
                            }
                        }
                    }).show();
                } else {
//                    AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false);
                    BusProvider.getInstance().post(new OttoEvents.MatureEvent(false));
                }


                return true;
            }
        });

        findPreference("showAllUpdates").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Analytics.Settings.onSettingChange("Check On Filter Updates");

                SettingsResult();
                if (!((CheckBoxPreference) preference).isChecked()) {
//                    FlurryAgent.logEvent("Setting_Do_Not_Filter_Incompatible_Updates");
                }
                return true;
            }
        });

        findPreference("clearcache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {


            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Analytics.Settings.onSettingChange("Click on Clear Cache");

                if (unlocked) {
                    new DeleteDir().execute(new File(icon_path));
//                    FlurryAgent.logEvent("Setting_Cleared_Cache");
                }

                return false;
            }
        });
        findPreference("clearapk").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Analytics.Settings.onSettingChange("CClick n Remove Data and Configurations");

                if (unlocked) {
                    new DeleteDir().execute(new File(aptoide_path));
//                    FlurryAgent.logEvent("Setting_Removed_Data_And_Configurations");
                }

                return false;
            }
        });


        disableSocialTimeline();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);


//		Preference hwspecs = (Preference) findPreference("hwspecs");
//		hwspecs.setIntent(new Intent(getBaseContext(), HWSpecActivity.class));
        Preference hwSpecs = findPreference("hwspecs");

        findPreference("theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                FlurryAgent.logEvent("Setting_Changed_Application_Theme");
                Toast.makeText(SettingsActivity.this, getString(R.string.restart_aptoide), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        hwSpecs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Analytics.Settings.onSettingChange("CLick on Hardware Specs");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mctx);
                alertDialogBuilder.setTitle(getString(R.string.setting_hwspecstitle));
                alertDialogBuilder
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setMessage(getString(R.string.setting_sdk_version) + ": " + AptoideUtils.HWSpecifications.getSdkVer() + "\n" +
                                        getString(R.string.setting_screen_size) + ": " + AptoideUtils.HWSpecifications.getScreenSize(mctx) + "\n" +
                                        getString(R.string.setting_esgl_version) + ": " + AptoideUtils.HWSpecifications.getGlEsVer(mctx) + "\n" +
                                        getString(R.string.screenCode) + ": " + AptoideUtils.HWSpecifications.getNumericScreenSize(mctx) + "/" + AptoideUtils.HWSpecifications.getDensityDpi(mctx) + "\n" +
                                        getString(R.string.cpuAbi) + ": " + AptoideUtils.HWSpecifications.getAbis()
//                            + (ApplicationAptoide.PARTNERID!=null ? "\nPartner ID:" + ApplicationAptoide.PARTNERID : "")
                        )
                        .setCancelable(false)
                        .setNeutralButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                FlurryAgent.logEvent("Setting_Opened_Dialog_Hardware_Filters");
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;
            }
        });

//		if(!ApplicationAptoide.MATURECONTENTSWITCH){
//			CheckBoxPreference mCheckBoxPref = (CheckBoxPreference) findPreference("matureChkBox");
//			PreferenceCategory mCategory = (PreferenceCategory) findPreference("filters");
//			mCategory.removePreference(mCheckBoxPref);
//		}

//		Preference showExcluded = findPreference("showexcludedupdates");
//		showExcluded.setIntent(new Intent(mctx, ExcludedUpdatesActivity.class));

        EditTextPreference maxFileCache = (EditTextPreference) findPreference("maxFileCache");

        maxFileCache.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        maxFileCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Analytics.Settings.onSettingChange("Click on Download File Cache");

                ((EditTextPreference) preference).getEditText().setText(PreferenceManager.getDefaultSharedPreferences(mctx).getString
                        ("maxFileCache", "200"));
//                FlurryAgent.logEvent("Setting_Added_Max_File_Cache");
                return false;
            }
        });

        Preference about = findPreference("aboutDialog");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

//                Analytics.Settings.onSettingChange("Click on About Us");

                View view = LayoutInflater.from(mctx).inflate(R.layout.dialog_about, null);
                String versionName = "";

                try {
                    versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.printException(e);
                }

                ((TextView) view.findViewById(R.id.aptoide_version)).setText(getString(R.string.version) + " " + versionName);
                ((TextView) view.findViewById(R.id.credits)).setMovementMethod(LinkMovementMethod.getInstance());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mctx).setView(view);
                final AlertDialog aboutDialog = alertDialogBuilder.create();
                aboutDialog.setTitle(getString(R.string.about_us));
                aboutDialog.setIcon(android.R.drawable.ic_menu_info_details);
                aboutDialog.setCancelable(false);
                aboutDialog.setButton(Dialog.BUTTON_NEUTRAL, getString(android.R.string.ok), new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        FlurryAgent.logEvent("Setting_Opened_About_Us_Dialog");
                        dialog.cancel();
                    }
                });
                aboutDialog.show();

                return true;
            }
        });

//		if(ApplicationAptoide.PARTNERID!=null){
//			PreferenceScreen preferenceScreen = getPreferenceScreen();
//			Preference etp = preferenceScreen.findPreference("aboutDialog");
//
//			PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("about");
//			preferenceGroup.removePreference(etp);
//			preferenceScreen.removePreference(preferenceGroup);
//
//		}
//
//
//        if(!ApplicationAptoide.DEBUG_MODE){
//            PreferenceScreen preferenceScreen = getPreferenceScreen();
//            Preference etp = preferenceScreen.findPreference("devmode");
//            PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("devmode");
//            preferenceGroup.removePreference(etp);
//            preferenceScreen.removePreference(preferenceGroup);
//        }


//        getActionBar().setTitle("");
//        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (isSetingPIN) {
//            Log.d("PINTEST","is Setting adult pin");
            DialogSetAdultpin(mp).show();
        }

//        Analytics.Lifecycle.Activity.onCreate(this);

    }

    public void disableSocialTimeline() {
        if (Preferences.getBoolean(Preferences.TIMELINE_ACEPTED_BOOL, false)) {
            findPreference("disablesocialtimeline").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
//                    FlurryAgent.logEvent("Settings_Disabled_Social_Timeline");
                    final ProgressDialog pd;

                    pd = new ProgressDialog(mctx);
                    pd.setMessage(getString(R.string.please_wait));
                    pd.show();

                    ChangeUserSettingsRequest request = new ChangeUserSettingsRequest();
                    request.addTimeLineSetting(ChangeUserSettingsRequest.TIMELINEINACTIVE);

                    manager.execute(request, new RequestListener<GenericResponseV2>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            pd.dismiss();
                        }

                        @Override
                        public void onRequestSuccess(GenericResponseV2 responseV2) {
                            if (responseV2.getStatus().equals("OK")) {
                                pd.dismiss();
                                manager.removeDataFromCache(GetUserSettingsJson.class, "timeline-status");
                                PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).edit().remove(Preferences.TIMELINE_ACEPTED_BOOL).remove(Preferences.SHARE_TIMELINE_DOWNLOAD_BOOL).commit();
                                ((PreferenceScreen) findPreference("root")).removePreference(findPreference("socialtimeline"));
                                Account account = AccountManager.get(SettingsActivity.this).getAccountsByType(Aptoide.getConfiguration().getAccountType())[0];

                                String timelineActivitySyncAdapterAuthority = Aptoide.getConfiguration().getTimelineActivitySyncAdapterAuthority();

                                String timeLinePostsSyncAdapterAuthority = Aptoide.getConfiguration().getTimeLinePostsSyncAdapterAuthority();

                                ContentResolver.setSyncAutomatically(account, timelineActivitySyncAdapterAuthority, false);
                                if (Build.VERSION.SDK_INT >= 8)
                                    ContentResolver.removePeriodicSync(account, timelineActivitySyncAdapterAuthority, new Bundle());

                                ContentResolver.setSyncAutomatically(account, timeLinePostsSyncAdapterAuthority, false);
                                if (Build.VERSION.SDK_INT >= 8)
                                    ContentResolver.removePeriodicSync(account, timeLinePostsSyncAdapterAuthority, new Bundle());

                            }
                        }
                    });
                    return false;
                }
            });

        } else {
            ((PreferenceScreen) findPreference("root")).removePreference(findPreference("socialtimeline"));
        }
    }

    private void SettingsResult() {
        setResult(RESULT_OK);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


    public class DeleteDir extends AsyncTask<File, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mctx);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();
        }

        @Override
        protected Void doInBackground(File... params) {
            deleteDirectory(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();
            Toast toast = Toast.makeText(mctx, mctx.getString(R.string.clear_cache_sucess), Toast.LENGTH_SHORT);
            toast.show();
            new GetDirSize().execute(new File(aptoide_path), new File(icon_path));
        }

    }


    public class GetDirSize extends AsyncTask<File, Void, Double[]> {
        double getDirSize(File dir) {
            double size = 0;
            try {
                if (dir.isFile()) {
                    size = dir.length();
                } else {
                    File[] subFiles = dir.listFiles();
                    for (File file : subFiles) {
                        if (file.isFile()) {
                            size += file.length();
                        } else {
                            size += this.getDirSize(file);
                        }

                    }
                }
            } catch (Exception e) {
                Logger.printException(e);
            }
            return size;
        }

        @Override
        protected Double[] doInBackground(File... dir) {
            Double[] sizes = new Double[2];

            for (int i = 0; i != sizes.length; i++) {
                sizes[i] = this.getDirSize(dir[i]) / 1024 / 1024;
            }
            return sizes;
        }

        @Override
        protected void onPostExecute(Double[] result) {
            super.onPostExecute(result);
            redrawSizes(result);
            unlocked = true;
        }

    }

    private void redrawSizes(Double[] size) {
        if (!Build.DEVICE.equals("alien_jolla_bionic")) {
            findPreference("clearapk").setSummary(getString(R.string.clearcontent_sum) + " (" + AptoideUtils.StringUtils.getFormattedString(this, R.string.cache_using_X_mb, new DecimalFormat("#.##").format(size[0])) + ")");
            findPreference("clearcache").setSummary(getString(R.string.clearcache_sum) + " (" + AptoideUtils.StringUtils.getFormattedString(this, R.string.cache_using_X_mb, new DecimalFormat("#.##").format(size[1])) + ")");
        } else {
            findPreference("clearapk").setSummary(getString(R.string.clearcontent_sum_jolla) + " (" + AptoideUtils.StringUtils.getFormattedString(this, R.string.cache_using_X_mb, new DecimalFormat("#.##").format(size[0])) + ")");
            findPreference("clearcache").setSummary(getString(R.string.clearcache_sum_jolla) + " (" + AptoideUtils.StringUtils.getFormattedString(this, R.string.cache_using_X_mb, new DecimalFormat("#.##").format(size[1])) + ")");
        }

    }


    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();

        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);


    @Override
    protected void onStart() {
        super.onStart();
        manager.start(this);
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.START);
//        FlurryAgent.onStartSession(this, getResources().getString(R.string.FLURRY_KEY));
//        Analytics.Lifecycle.Activity.onStart(this);
    }

    @Override
    protected void onStop() {
        manager.shouldStop();
        LifeCycleMonitor.sendLiveCycleEvent(this, OttoEvents.ActivityLifeCycleEvent.LifeCycle.STOP);
//        FlurryAgent.onEndSession(this);
//        Analytics.Lifecycle.Activity.onStop(this);
        super.onStop();
        getDelegate().onStop();
    }

}
