/*******************************************************************************
 * Copyright (c) 2015 Aptoide.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.utils;

import com.aptoide.dataprovider.webservices.models.v7.Apiv7ListSearchApps;
import com.aptoide.dataprovider.webservices.v7.GetListSearchAppsv7;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.BuildConfig;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.preferences.AptoidePreferences;
import com.aptoide.amethyst.preferences.EnumPreferences;
import com.aptoide.amethyst.preferences.ManagerPreferences;
import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.social.WebViewFacebook;
import com.aptoide.amethyst.social.WebViewTwitter;
import com.aptoide.amethyst.webservices.AddApkFlagRequest;
import com.aptoide.amethyst.webservices.ChangeUserRepoSubscription;
import com.aptoide.amethyst.webservices.ChangeUserSettingsRequest;
import com.aptoide.amethyst.webservices.Errors;
import com.aptoide.amethyst.webservices.GetAppRequest;
import com.aptoide.amethyst.webservices.GetUserRepoSubscriptions;
import com.aptoide.amethyst.webservices.SearchRequest;
import com.aptoide.amethyst.webservices.json.GetUserRepoSubscriptionJson;
import com.aptoide.amethyst.webservices.listeners.CheckSimpleStoreListener;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.amethyst.webservices.v3.RateAppRequest;
import com.aptoide.dataprovider.webservices.GetSimpleStoreRequest;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.aptoide.dataprovider.webservices.models.v7.GetAppMeta;
import com.aptoide.dataprovider.webservices.v7.CommunityGetstoreRequest;
import com.aptoide.dataprovider.webservices.v7.GetListViewItemsRequestv7;
import com.aptoide.dataprovider.webservices.v7.GetMoreVersionsAppRequest;
import com.aptoide.dataprovider.webservices.v7.GetStoreRequestv7;
import com.aptoide.dataprovider.webservices.v7.GetStoreWidgetRequestv7;
import com.aptoide.models.ApkPermission;
import com.aptoide.models.ApkPermissionGroup;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.crashlytics.android.Crashlytics;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.text.WordUtils;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static android.net.ConnectivityManager.TYPE_ETHERNET;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 15-10-2013
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class AptoideUtils {

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());
    }

    public static int getPixels(Context context, int dipValue) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        Logger.d("getPixels", "" + px);
        return px;
    }

    public static class UI {


        private static final String TAG = UI.class.getSimpleName();

        public static int getVerCode(Context context) {
            PackageManager manager = context.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                return -1;
            }
        }

        public static void toast(String text) {
            Toast.makeText(Aptoide.getContext(), text, Toast.LENGTH_LONG).show();
        }

        public static int getToolbarHeight(Context context) {
            final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                    new int[]{R.attr.actionBarSize});
            int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();

            return toolbarHeight;
        }

        public static int getTabsHeight(Context context) {
            return (int) context.getResources().getDimension(R.dimen.tabsHeight);
        }

        private static char[] c = new char[]{'k', 'm', 'b', 't'};

        /**
         * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
         *
         * @param n         the number to format
         * @param iteration in fact this is the class from the array c
         * @return a String representing the number n formatted in a cool looking way.
         */
        public static String coolFormat(double n, int iteration) {
            double d = ((long) n / 100) / 10.0;
            boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
            return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                    ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                            (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                    ) + "" + c[iteration])
                    : coolFormat(d, iteration + 1));
        }

        /**
         * Check if thread calling this method is the UI Thread.
         *
         * @return true if UI Thread
         */
        public static boolean isUiThread() {
            return Looper.getMainLooper().getThread() == Thread.currentThread();
        }

        public static String screenshotToThumb(String imageUrl, String orientation) {

            String screen = null;

            try {

                if (imageUrl.contains("_screen")) {

                    String sizeString = IconSizeUtils.generateSizeStringScreenshots(orientation);

                    String[] splitUrl = imageUrl.split("\\.(?=[^\\.]+$)");
                    screen = splitUrl[0] + "_" + sizeString + "." + splitUrl[1];

                } else {

                    String[] splitString = imageUrl.split("/");
                    StringBuilder db = new StringBuilder();
                    for (int i = 0; i != splitString.length - 1; i++) {
                        db.append(splitString[i]);
                        db.append("/");
                    }

                    db.append("thumbs/mobile/");
                    db.append(splitString[splitString.length - 1]);
                    screen = db.toString();
                }

            } catch (Exception e) {
                Logger.printException(e);
                Crashlytics.setString("imageUrl", imageUrl);
                Crashlytics.logException(e);
            }

            return screen;
        }

        public static String getScreenshotThumbnail(String imageUrl, String orientation) {

            String screen;
            String sizeString;

            if (imageUrl.contains("_screen")) {

                if(orientation != null && orientation.equals("portrait")){
                    sizeString = "192x320";
                }else{
                    sizeString = "256x160";
                }

                String[] splittedUrl = imageUrl.split("\\.(?=[^\\.]+$)");
                screen = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];

            } else {


                String[] splitedString = imageUrl.split("/");
                StringBuilder db = new StringBuilder();
                for (int i = 0; i != splitedString.length - 1; i++) {
                    db.append(splitedString[i]);
                    db.append("/");
                }
                db.append("thumbs/mobile/");
                db.append(splitedString[splitedString.length - 1]);
                screen = db.toString();
            }

            return screen;
        }

        /**
         * Sets a color to a Drawable
         *
         * @param drawable
         * @param color
         */
        public static void setDrawableColor(Drawable drawable, int color) {

            // Assuming "color" is your target color
            float r = Color.red(color) / 255f;
            float g = Color.green(color) / 255f;
            float b = Color.blue(color) / 255f;
            float a = Color.alpha(color) / 255f;


            ColorMatrix cm = new ColorMatrix(new float[]{
                    r, r, r, r, r, //red
                    g, g, g, g, g, //green
                    b, b, b, b, b, //blue
                    1, 1, 1, 1, 1 //alpha
            });

            ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);

            drawable.setColorFilter(cf);
        }

        public static void toastError(List<ErrorResponse> errors) {
            if (errors == null) {
                return;
            }
            for (ErrorResponse error : errors) {
                String localizedError = null;
                try {
                    localizedError = Aptoide.getContext().getString(Errors.getErrorsMap().get(error.code));
                } catch (NullPointerException ignored) {
                }
                if (localizedError == null) {
                    localizedError = error.msg;
                }
                if (localizedError != null)
                    Toast.makeText(Aptoide.getContext(), localizedError, Toast.LENGTH_LONG).show();
            }
        }

        public static int getBucketSize() {
            int bucket = 1;
            float screenWidth = getScreenWidthInDip();

            int magicNumber = Aptoide.getContext().getResources().getInteger(R.integer.bucket_size_magic_number);
            if (screenWidth > magicNumber) {
                bucket = (int) (screenWidth / magicNumber);
            }

            Logger.d("APTOIDEUTILS", "bucketsize = " + bucket);

            return bucket;
        }
        public static int getEditorChoiceBucketSize() {
            int bucket = 1;
            float screenWidth = getScreenWidthInDip();

            if (screenWidth > 300) {
                bucket = (int) (screenWidth / 300);
            }

            Logger.d("APTOIDEUTILS", "bucketsize = " + bucket);

            return bucket;
        }

        public static int getStoreBucketSize() {
            int bucket = 1;
            float screenWidth = getScreenWidthInDip();

            if (screenWidth > 150) {
                bucket = (int) (screenWidth / 150);
            }

            Logger.d("APTOIDEUTILS", "storeBucketsize = " + bucket);

            return bucket;
        }

        protected static float getScreenWidthInDip() {
            WindowManager wm = ((WindowManager) Aptoide.getContext().getSystemService(Context.WINDOW_SERVICE));
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            int screenWidth_in_pixel = dm.widthPixels;
            return screenWidth_in_pixel / dm.density;
        }

        /**
         * On v7 webservices there is no attribute of HD icon. <br />Instead,
         * the logic is that if the filename ends with <b>_icon</b> it is an HD icon.
         *
         * @param iconUrl The String with the URL of the icon
         * @return A String with
         */
        public static String parseIcon(String iconUrl) {
            try {
                if (iconUrl.contains("_icon")) {
                    String sizeString = IconSizeUtils.generateSizeString();
                    if (sizeString != null && !sizeString.isEmpty()) {
                        String[] splittedUrl = iconUrl.split("\\.(?=[^\\.]+$)");
                        iconUrl = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
                    }
                }
            } catch (Exception e) {
                Logger.printException(e);
            }
            return iconUrl;
        }
        /**
         * On v7 webservices there is no attribute of HD icon. <br />Instead,
         * the logic is that if the filename ends with <b>_icon</b> it is an HD icon.
         *
         * @param iconUrl The String with the URL of the icon
         * @return A String with
         */
        public static String parseStoreIcon(String iconUrl) {
            try {
                if (iconUrl.contains("_ravatar")) {
                    String sizeString = IconSizeUtils.generateSizeStoreString();
                    if (sizeString != null && !sizeString.isEmpty()) {
                        String[] splittedUrl = iconUrl.split("\\.(?=[^\\.]+$)");
                        iconUrl = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
                    }
                }
            } catch (Exception e) {
                Logger.printException(e);
            }
            return iconUrl;
        }

        /**
         * Parse dp to pixels
         *
         * @param context Context of the app
         * @param dps The int mesuare in dps
         * @return An integer with the pixels value
         */
        public static int parseDpsToPixels(Context context, int dps) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dps * scale + 0.5f);
        }

        /**
        * Unbind all views
        *
         */
        public static void unbindDrawables(View view) {
            final Drawable background = view.getBackground();
            if (background != null) {
                background.setCallback(null);
                view.setBackgroundDrawable(null);
            }
            if (view instanceof ImageView) {
                ((ImageView)view).setImageDrawable(null);
            } else if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }


        /**
         * this method ensure that the returned span size is a valid one
         * The recyclerView must have a GridLayoutManager and the adapter must be an instance of SpannableRecyclerAdapter.
         * @param recyclerView where the item will be placed
         * @param position item position
         * @return a valid span size for the given item
         */
        public static int getSpanSize(RecyclerView recyclerView, int position) {
            if (!(recyclerView.getAdapter() instanceof SpannableRecyclerAdapter) || !(recyclerView.getLayoutManager() instanceof GridLayoutManager)) {
                String errorMsg = "The recyclerView must have a GridLayoutManager and the adapter must be an instance of SpannableRecyclerAdapter.";
                Crashlytics.logException(new ClassCastException(errorMsg));
                Logger.e(TAG, errorMsg);
                return 1;
            }
            int spanSize = ((SpannableRecyclerAdapter) recyclerView.getAdapter()).getSpanSize(position);
            if (spanSize >= ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount()) {
                return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
            } else if (spanSize < 1) {
                return 1; //min span size
            } else {
                return spanSize;
            }
        }
    }


    /**
     * Algorithms Utils
     */
    public static class Algorithms {

        /**
         * method to copy a string to clipboard
         * @param text texto to copy to clipBoard
         * @return true if it was copied successfully false otherwise
         */
        public static boolean copyToClipBoard(Context context, String text) {
            if (text==null || text.isEmpty()) {
                return false;
            } else {
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText(text, text);
                    clipboard.setPrimaryClip(clip);
                }
            }
            return true;
        }

        public static String computeSHA1sumFromBytes(byte[] bytes)
                throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(bytes, 0, bytes.length);
            byte[] sha1hash = md.digest();
            return convToHexWithColon(sha1hash);
        }

        private static String convToHexWithColon(byte[] data) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                int halfbyte = (data[i] >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    if ((0 <= halfbyte) && (halfbyte <= 9))
                        buf.append((char) ('0' + halfbyte));
                    else
                        buf.append((char) ('a' + (halfbyte - 10)));
                    halfbyte = data[i] & 0x0F;

                } while (two_halfs++ < 1);

                if (i < data.length - 1) {
                    buf.append(":");
                }

            }
            return buf.toString();
        }

        public static String computeSHA1sum(String text)
                throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convToHex(sha1hash);
        }

        private static String convToHex(byte[] data) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                int halfbyte = (data[i] >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    if ((0 <= halfbyte) && (halfbyte <= 9))
                        buf.append((char) ('0' + halfbyte));
                    else
                        buf.append((char) ('a' + (halfbyte - 10)));
                    halfbyte = data[i] & 0x0F;
                } while (two_halfs++ < 1);
            }
            return buf.toString();
        }

        public static String md5Calc(File f) {
            byte[] buffer = new byte[1024];
            int read, i;
            String md5hash;
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                InputStream is = new FileInputStream(f);
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                md5hash = bigInt.toString(16);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            if (md5hash.length() != 33) {
                String tmp = "";
                for (i = 1; i < (33 - md5hash.length()); i++) {
                    tmp = tmp.concat("0");
                }
                md5hash = tmp.concat(md5hash);
            }

            return md5hash;
        }

        public static String computeHmacSha1(String value, String keyString)
                throws InvalidKeyException, IllegalStateException,
                UnsupportedEncodingException, NoSuchAlgorithmException {
            System.out.println(value);
            System.out.println(keyString);
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"),
                    "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);

            byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));

            return convToHex(bytes);

        }
    }


    /**
     * Network Utils
     */
    public static class NetworkUtils {

        private static int TIME_OUT = 15000; // 15s

        private static String getUserId() {
            String user_id;

            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext());

            user_id = sPref.getString("advertisingIdClient", null);

            // Fallback para user
            if (isNullOrEmpty(user_id)) {
                user_id = android.provider.Settings.Secure.getString(Aptoide.getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            // Fallback para UUID
            if (isNullOrEmpty(user_id)) {
                user_id = sPref.getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), "NoInfo");
            }

            return user_id;
        }

        public static String getUserAgentString(Context mctx, boolean update) {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(mctx);
            String myid = getUserId();
            String myscr = sPref.getInt(EnumPreferences.SCREEN_WIDTH.name(), 0) + "x" + sPref.getInt(EnumPreferences.SCREEN_HEIGHT.name(), 0);
            String verString = null;
            try {
                verString = mctx.getPackageManager().getPackageInfo(mctx.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            String extraId = Aptoide.getConfiguration().getExtraId();

            return "aptoide-" + verString + ";" + HWSpecifications.TERMINAL_INFO + ";" + myscr + ";id:" + myid + ";" + sPref.getString(AptoideConfiguration.LOGIN_USER_LOGIN, "") + ";" + extraId;
        }

        public static boolean isIconDownloadPermitted(Context context) {

            return true;
//            return isAvailable(context,
//                    new DownloadPermissions(
//                            getSharedPreferences().getBoolean("wifi", true),
//                            getSharedPreferences().getBoolean("ethernet", true),
//                            getSharedPreferences().getBoolean("4g", true),
//                            getSharedPreferences().getBoolean("3g", true)));
        }

        public static boolean isGeneralDownloadPermitted(Context context) {
            final SharedPreferences preferences = getSharedPreferences();
            final boolean wifiAllowed = preferences.getBoolean("generalnetworkwifi", true);
            final boolean wifiAvailable = isAvailable(context, TYPE_WIFI);
            final boolean mobileAllowed = preferences.getBoolean("generalnetworkmobile", true);
            final boolean mobileAvailable = isAvailable(context, TYPE_MOBILE);
            return !(wifiAvailable && !wifiAllowed) && !(mobileAvailable && !mobileAllowed);
        }

        public static boolean isAvailable(Context context, int networkType) {
            final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return Build.VERSION.SDK_INT < 21
                    ? isAvailableSdk1(manager, networkType)
                    : isAvailableSdk21(manager, networkType);
        }

        private static boolean isAvailableSdk1(final ConnectivityManager manager,
                                               final int networkType) {
            final NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && info.getType() == networkType;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private static boolean isAvailableSdk21(final ConnectivityManager manager,
                                                final int networkType) {
            for (final Network network : manager.getAllNetworks()) {
                final NetworkInfo info = manager.getNetworkInfo(network);
                if (info != null && info.isConnected() && info.getType() == networkType) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        public static int checkServerConnection(final String string, final String username, final String password) throws Exception {


            HttpURLConnection client = (HttpURLConnection) new URL(string
                    + "info.xml").openConnection();
            if (username != null && password != null) {
                String basicAuth = "Basic "
                        + new String(Base64.encode(
                        (username + ":" + password).getBytes(),
                        Base64.NO_WRAP));
                client.setRequestProperty("Authorization", basicAuth);
            }

            client.setRequestMethod("HEAD");
            client.setConnectTimeout(TIME_OUT);
            client.setReadTimeout(TIME_OUT);

            String contentType = client.getContentType();
            int responseCode = client.getResponseCode();
            client.disconnect();
            if (Aptoide.DEBUG_MODE)
                Logger.i("CheckServerConnection", "Checking on: " + client.getURL().toString());
            if (contentType.equals("application/xml")) {
                return 0;
            } else {
                return responseCode;
            }
        }

        public static String getConnectionType() {
            final Context context = Aptoide.getContext();
            final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo info = manager.getActiveNetworkInfo();
            // Used only for backward compatibility, should be replaced with info.getTypeName()
            if (info != null) {
                switch (info.getType()) {
                    case TYPE_ETHERNET:
                        return "ethernet";
                    case TYPE_WIFI:
                        return "mobile";
                    case TYPE_MOBILE:
                        return "mobile";
                }
            }
            return "unknown";
        }
    }


    /**
     * Repo/Store Utils
     */
    public static class RepoUtils {


        public static String checkStoreUrl(String uri_str) {
            uri_str = uri_str.trim();
            if (!uri_str.contains(".")) {
                uri_str = uri_str.concat(".store.aptoide.com");
            }

            uri_str = RepoUtils.formatRepoUri(uri_str);

            if (uri_str.contains("bazaarandroid.com")) {
                uri_str = uri_str.replaceAll("bazaarandroid\\.com", "store.aptoide.com");
            }

            return uri_str;
        }

        public static String split(String repo) {
            Logger.d("Aptoide-RepoUtils", "Splitting " + repo);
            repo = formatRepoUri(repo);
            return repo.split("http://")[1].split("\\.store")[0].split("\\.bazaarandroid.com")[0];
        }

        public static String formatRepoUri(String uri_str) {

            uri_str = uri_str.toLowerCase(Locale.ENGLISH);

            if (uri_str.contains("http//")) {
                uri_str = uri_str.replaceFirst("http//", "http://");
            }

            if (uri_str.length() != 0 && uri_str.charAt(uri_str.length() - 1) != '/') {
                uri_str = uri_str + '/';
                Logger.d("Aptoide-ManageRepo", "repo uri: " + uri_str);
            }
            if (!uri_str.startsWith("http://")) {
                uri_str = "http://" + uri_str;
                Logger.d("Aptoide-ManageRepo", "repo uri: " + uri_str);
            }

            return uri_str;
        }


        public static void startParse(final String storeName, final Context context, final SpiceManager spiceManager) {
            Toast.makeText(context, AptoideUtils.StringUtils.getFormattedString(context, R.string.subscribing, storeName), Toast.LENGTH_SHORT).show();

            GetSimpleStoreRequest request = new GetSimpleStoreRequest();
            request.store_name = storeName;

            CheckSimpleStoreListener checkStoreListener = new CheckSimpleStoreListener();
            checkStoreListener.callback = new CheckSimpleStoreListener.Callback() {
                @Override
                public void onSuccess() {
                    addStoreOnCloud(storeName, context, spiceManager);
                }
            };

            spiceManager.execute(request, checkStoreListener);
        }

        /**
         * Adds the default store
         */
        public static void addDefaultAppsStore(Context context) {
            AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());
            if (database.existsStore(Defaults.DEFAULT_STORE_ID)) {
                return;
            }

            final Store store = new Store();
            store.setId(Defaults.DEFAULT_STORE_ID);
            store.setName(Defaults.DEFAULT_STORE_NAME);
            store.setDownloads("");


            String sizeString = IconSizeUtils.generateSizeStringAvatar();

            String avatar = "http://pool.img.aptoide.com/apps/b62b1c9459964d3a876b04c70036b10a_ravatar.png";

            // integrate the icon into a drawable ?
            if (avatar != null) {
                String[] splittedUrl = avatar.split("\\.(?=[^\\.]+$)");
                avatar = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
            }

            store.setAvatar(avatar);
            store.setDescription(context.getResources().getString(R.string.aptoide_description));
            store.setTheme("default");
            store.setView("list");
            store.setBaseUrl("apps");


            try {
                long l = database.insertStore(store);
                database.updateStore(store, l);
            } catch (Exception e) {
                Logger.printException(e);
            }
        }


        /**
         * Try to sync all your repos
         *
         * @param context
         * @param spiceManager
         */
        public static void syncRepos(Context context, SpiceManager spiceManager) {
            final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (!defaultSharedPreferences.getBoolean(AptoidePreferences.REPOS_SYNCED, false)) {

                GetUserRepoSubscriptions subscriptions = new GetUserRepoSubscriptions();
                spiceManager.execute(subscriptions, new RequestListener<GetUserRepoSubscriptionJson>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                    }

                    @Override
                    public void onRequestSuccess(final GetUserRepoSubscriptionJson getUserRepoSubscriptionJson) {
                        // Call database operations off the UI Thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                boolean storeInserted = false;

                                for (GetUserRepoSubscriptionJson.RepoInfo subscription : getUserRepoSubscriptionJson.getSubscription()) {

                                    try {
                                        final Store store = new Store();

                                        store.setId(subscription.getId().longValue());
                                        store.setName(subscription.getName());
                                        store.setDownloads(subscription.getDownloads());

                                        String sizeString = IconSizeUtils.generateSizeStringAvatar();
                                        String avatar = subscription.getAvatar();

                                        if (avatar != null) {
                                            String[] splitUrl = avatar.split("\\.(?=[^\\.]+$)");
                                            avatar = splitUrl[0] + "_" + sizeString + "." + splitUrl[1];
                                        }

                                        store.setAvatar(avatar);
                                        store.setDescription(subscription.getDescription());
                                        store.setTheme(subscription.getTheme());
                                        store.setView(subscription.getView());
                                        store.setBaseUrl(subscription.getName());

                                        AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());

                                        try {
                                            long l = database.insertStore(store);
                                            database.updateStore(store, l);
                                            storeInserted = true;

                                        } catch (Exception e) {
                                            Logger.printException(e);
                                        }

                                    } catch (Exception e) {
                                        Logger.printException(e);
                                    }
                                }

                                defaultSharedPreferences.edit().putBoolean(AptoidePreferences.REPOS_SYNCED, true).apply();

                                if (storeInserted) {
                                    BusProvider.getInstance().post(new OttoEvents.RepoAddedEvent());
                                }
                            }
                        }).start();
                    }
                });
            }
        }

        /**
         * If there's a local account created, add the store to its aptoide account
         *
         * @param storeName
         * @param context
         * @param spiceManager
         */
        private static void addStoreOnCloud(String storeName, Context context, SpiceManager spiceManager) {
            if (AccountManager.get(context).getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
                ChangeUserRepoSubscription changeUserRepoSubscription = new ChangeUserRepoSubscription();
                ChangeUserRepoSubscription.RepoSubscription repoSubscription = new ChangeUserRepoSubscription.RepoSubscription(storeName, true);
                changeUserRepoSubscription.setRepoSubscription(repoSubscription);
                spiceManager.execute(changeUserRepoSubscription, null);
            }
        }

        public static void removeStoreOnCloud(Store store, Context context, SpiceManager spiceManager) {
            if (AccountManager.get(context).getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
                ChangeUserRepoSubscription changeUserRepoSubscription = new ChangeUserRepoSubscription();
                ChangeUserRepoSubscription.RepoSubscription repoSubscription = new ChangeUserRepoSubscription.RepoSubscription(store.getName(), false);
                changeUserRepoSubscription.setRepoSubscription(repoSubscription);
                spiceManager.execute(changeUserRepoSubscription, null);
            }
        }

        public static GetMoreVersionsAppRequest GetMoreAppVersionsRequest(String packageName, int limit, int offset) {
            GetMoreVersionsAppRequest request = new GetMoreVersionsAppRequest(AptoideUtils.UI.getBucketSize());
            request.filters = Aptoide.filters;
            request.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            request.aptoideVercode = AptoideUtils.UI.getVerCode(Aptoide.getContext());
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext());
            request.packageName = packageName;
            request.limit = limit;
            request.offset = offset;
            return request;
        }

        public static GetStoreRequestv7 buildStoreRequest(long storeId, String context) {
            GetStoreRequestv7 request = buildGenericStoreRequest(AptoideUtils.UI.getBucketSize());
            request.storeId = storeId;
            request.context = context;
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeId);
            if (login != null) {
                request.user = login.getUsername();
                request.password = login.getPasswordSha1();
            }
            return request;
        }

        public static final int NUMBER_OF_LINES = 4;

        public static GetStoreRequestv7 buildStoreRequest(long storeId, String context, int bucketSize) {

            CommunityGetstoreRequest request = new CommunityGetstoreRequest(bucketSize, UI.getEditorChoiceBucketSize());
            request.loggedIn = AccountUtils.isLoggedIn(Aptoide.getContext());
            request.nview = "response";
            request.filters = Aptoide.filters;
            request.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            request.aptoideVercode = AptoideUtils.UI.getVerCode(Aptoide.getContext());
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext());
            request.storeId = storeId;
            request.context = context;
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeId);
            if (login != null) {
                request.user = login.getUsername();
                request.password = login.getPasswordSha1();
            }
            return request;
        }

        public static GetStoreRequestv7 buildStoreRequest(String storeName, String context) {
            GetStoreRequestv7 request = buildGenericStoreRequest(AptoideUtils.UI.getBucketSize());
            request.storeName = storeName;
            request.context = context;
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeName);
            if (login != null) {
                request.user = login.getUsername();
                request.password = login.getPasswordSha1();
            }
            return request;
        }

        public static GetSimpleStoreRequest buildSimpleStoreRequest(String storeName) {
            GetSimpleStoreRequest request = new GetSimpleStoreRequest();
            request.store_name = storeName;
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeName);
            request.login = login;
            return request;
        }

        private static GetStoreRequestv7 buildGenericStoreRequest(int bucketSize) {
            GetStoreRequestv7 request = new GetStoreRequestv7(bucketSize);
            request.loggedIn = AccountUtils.isLoggedIn(Aptoide.getContext());
            request.nview = "response";
            request.filters = Aptoide.filters;
            request.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            request.aptoideVercode = AptoideUtils.UI.getVerCode(Aptoide.getContext());
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext());
            if(Aptoide.DEBUG_MODE){
                request.country = AptoideUtils.getSharedPreferences()
                        .getString("forcecountry", null);
            }
            return request;
        }

        public static GetStoreWidgetRequestv7 buildStoreWidgetRequest(long storeId, String actionUrl, String bundleTitle) {
            GetStoreWidgetRequestv7 getStoreWidgetRequestv7 = buildStoreWidgetRequest(storeId, actionUrl);
            getStoreWidgetRequestv7.bundleTitle = bundleTitle;
            return getStoreWidgetRequestv7;
        }

        public static GetStoreWidgetRequestv7 buildStoreWidgetRequest(long storeId, String actionUrl) {
            GetStoreWidgetRequestv7 request = new GetStoreWidgetRequestv7(actionUrl, AptoideUtils.UI.getBucketSize());
            request.loggedIn = AccountUtils.isLoggedIn(Aptoide.getContext());
            request.nview = "response";
            request.filters = Aptoide.filters;
            request.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            request.aptoideVercode = AptoideUtils.UI.getVerCode(Aptoide.getContext());
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext());
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeId);
            if (login != null) {
                request.user = login.getUsername();
                request.password = login.getPasswordSha1();
            }
            return request;
        }

        public static GetListViewItemsRequestv7 buildViewItemsRequest(String storeName, String actionUrl, String layout, int offset) {
            GetListViewItemsRequestv7 request = new GetListViewItemsRequestv7(actionUrl, layout, UI.getBucketSize(), offset);
            request.loggedIn = AccountUtils.isLoggedIn(Aptoide.getContext());
            request.nview = "response";
            if(Aptoide.DEBUG_MODE){
                request.country = AptoideUtils.getSharedPreferences()
                        .getString("forcecountry", null);
            }
            request.filters = Aptoide.filters;
            request.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            request.aptoideVercode = AptoideUtils.UI.getVerCode(Aptoide.getContext());
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext());
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeName);
            if (login != null) {
                request.user = login.getUsername();
                request.password = login.getPasswordSha1();
            }
            return request;
        }

        public static GetAppRequest buildGetAppRequest(String storeName) {
            GetAppRequest request = new GetAppRequest(UI.getBucketSize());
            request.token = SecurePreferences.getInstance().getString("access_token", null);
            request.filters = Aptoide.filters;
            request.mature = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false);
            request.aptoideVercode = AptoideUtils.UI.getVerCode(Aptoide.getContext());
            request.lang = AptoideUtils.StringUtils.getMyCountryCode(Aptoide.getContext());
            request.storeName = storeName;
            final Login login = new AptoideDatabase(Aptoide.getDb()).getStoreLogin(storeName);
            if (login != null) {
                request.user = login.getUsername();
                request.password = login.getPasswordSha1();
            }
            return request;
        }

        public static GetAppRequest buildGetAppRequestFromAppId(long appId, String storeName, String packageName) {
            GetAppRequest request = buildGetAppRequest(storeName);
            request.appId = appId;
            request.packageName = packageName;
            return request;
        }

        public static GetAppRequest buildGetAppRequestFromMd5(String md5, String storeName) {
            GetAppRequest request = buildGetAppRequest(storeName);
            request.md5 = md5;
            return request;
        }

        public static GetAppRequest buildGetAppRequestFromPackageName(String packageName) {
            GetAppRequest request = buildGetAppRequest(null);
            request.packageName = packageName;
            return request;
        }

        public static RateAppRequest buildRateRequest(long appId, float rate) {
            return new RateAppRequest(appId, rate);
        }

        public static AddApkFlagRequest buildFlagAppRequest(String storeName, String flag, String md5sum) {
            AddApkFlagRequest request = new AddApkFlagRequest();
            request.setFlag(flag);
            request.setMd5sum(md5sum);
            request.setRepo(storeName);
            return request;
        }

        public static GetListSearchAppsv7 buildSearchAppsRequest(String query, boolean trusted, int limit, int offset) {
            final Apiv7ListSearchApps arguments = new Apiv7ListSearchApps();
            arguments.access_token = SecurePreferences.getInstance().getString("access_token", null);
            arguments.aptoide_vercode = UI.getVerCode(Aptoide.getContext());
            arguments.q = HWSpecifications.filters(Aptoide.getContext());
            arguments.lang = StringUtils.getMyCountry(Aptoide.getContext());
            arguments.offset = offset;
            arguments.limit = limit;
            arguments.trusted = trusted;
            arguments.query = query;
            return new GetListSearchAppsv7(arguments);
        }

        public static SearchRequest buildSearchRequest(String query, int limit, int otherReposLimit, int offset, int otherReposOffset) {
            SearchRequest request = new SearchRequest();
            request.setSearchQuery(query);
            AptoideDatabase db = new AptoideDatabase(Aptoide.getDb());
            final List<String> strings = db.getSubscribedStoreNames();
            String[] arraysStrings = new String[strings.size()];
            strings.toArray(arraysStrings);
            request.setRepos(arraysStrings);
            request.setLimit(limit);
            request.setOtherReposLimit(otherReposLimit);
            request.setU_offset(otherReposOffset);
            request.setOffset(offset);
            request.setAccess_token(SecurePreferences.getInstance()
                    .getString("access_token", null));
            request.setAptoide_uid(ManagerPreferences.getInstance(Aptoide.getContext())
                    .getAptoideId());
            return request;
        }

        public static SearchRequest buildSearchRequest(String query, int searchLimit, int otherReposSearchLimit, int offset, String storeName) {
            SearchRequest request = buildSearchRequest(query,searchLimit,otherReposSearchLimit,offset,0);
            if (storeName!=null && !TextUtils.isEmpty(storeName)) {
                String[] arraysStrings = new String[1];
                arraysStrings[0] = storeName;
                request.setRepos(arraysStrings);
            }
            return request;
        }
    }


    public static class HWSpecifications {

        public static final String TERMINAL_INFO = getModel() + "(" + getProduct() + ")"
                + ";v" + getRelease() + ";" + System.getProperty("os.arch");

        public static String getProduct() {
            return android.os.Build.PRODUCT.replace(";", " ");
        }

        public static String getModel() {
            return android.os.Build.MODEL.replaceAll(";", " ");
        }

        public static String getRelease() {
            return android.os.Build.VERSION.RELEASE.replaceAll(";", " ");
        }

//        private static String cpuAbi2;
//
//        public static String getDeviceId(Context context) {
//            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//        }
//

        /**
         * @return the sdkVer
         */

        public static int getSdkVer() {
            return Build.VERSION.SDK_INT;
        }

        /**
         * @return the screenSize
         */
        public static int getScreenSize(Context context) {
            return context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        }

        public static int getNumericScreenSize(Context context) {
            int size = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            return (size + 1) * 100;
        }


        /**
         * @return the esglVer
         */
        public static String getGlEsVer(Context context) {
            return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
        }

        public static int getDensityDpi() {
            Context context = Aptoide.getContext();
            if (context == null) {
                return 0;
            }

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(metrics);

            int dpi = metrics.densityDpi;


            if (dpi <= 120) {
                dpi = 120;
            } else if (dpi <= 160) {
                dpi = 160;
            } else if (dpi <= 213) {
                dpi = 213;
            } else if (dpi <= 240) {
                dpi = 240;
            } else if (dpi <= 320) {
                dpi = 320;
            } else if (dpi <= 480) {
                dpi = 480;
            } else {
                dpi = 640;
            }

            return dpi;
        }

        public static String getScreenDensity() {

            Context context = Aptoide.getContext();
            int density = context.getResources().getDisplayMetrics().densityDpi;

            switch (density) {
                case DisplayMetrics.DENSITY_LOW:
                    return "ldpi";
                case DisplayMetrics.DENSITY_MEDIUM:
                    return "mdpi";
                case DisplayMetrics.DENSITY_HIGH:
                    return "hdpi";
                case DisplayMetrics.DENSITY_XHIGH:
                    return "xhdpi";
                case DisplayMetrics.DENSITY_XXHIGH:
                    return "xxhdpi";
                case DisplayMetrics.DENSITY_XXXHIGH:
                    return "xxxhdpi";

                default:
                    return "hdpi";
            }

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @SuppressWarnings("deprecation")
        public static String getAbis() {
            final String[] abis = getSdkVer() >= 21
                    ? Build.SUPPORTED_ABIS
                    : new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < abis.length; i++) {
                builder.append(abis[i]);
                if (i < abis.length - 1) {
                    builder.append(",");
                }
            }
            return builder.toString();
        }

//        public static String getCpuAbi() {
//            return Build.CPU_ABI;
//        }
//
//        public static String getCpuAbi2() {
//
//            if (getSdkVer() >= 8 && !Build.CPU_ABI2.equals(Build.UNKNOWN)) {
//                return Build.CPU_ABI2;
//            } else {
//                return "";
//            }
//        }

        @Nullable
        public static String filters(Context context) {
            final ManagerPreferences managerPreferences = ManagerPreferences.getInstance(context);
            if (!managerPreferences.preferences.getBoolean("hwspecsChkBox", true)) {
                return null;
            }

            int minSdk = AptoideUtils.HWSpecifications.getSdkVer();
            String minScreen = Filters.Screen.values()
                    [AptoideUtils.HWSpecifications.getScreenSize(context)]
                    .name()
                    .toLowerCase(Locale.ENGLISH);
            String minGlEs = AptoideUtils.HWSpecifications.getGlEsVer(context);


            final int density = AptoideUtils.HWSpecifications.getDensityDpi();

            String cpuAbi = AptoideUtils.HWSpecifications.getAbis();

            int myversionCode = 0;
            PackageManager manager = context.getPackageManager();
            try {
                myversionCode = manager.getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignore) {
            }


            String filters = (Build.DEVICE.equals("alien_jolla_bionic") ? "apkdwn=myapp&" : "") + "maxSdk=" + minSdk + "&maxScreen=" + minScreen + "&maxGles=" + minGlEs + "&myCPU=" + cpuAbi + "&myDensity=" + density + "&myApt=" + myversionCode;

            return Base64.encodeToString(filters.getBytes(), 0).replace("=", "").replace("/", "*").replace("+", "_").replace("\n", "");
        }

//        public static final String TERMINAL_INFO = getModel() + "("+ getProduct() + ")"
//                +";v"+getRelease()+";"+System.getProperty("os.arch");
//
//        public static String getProduct(){
//            return android.os.Build.PRODUCT.replace(";", " ");
//        }
//
//        public static String getModel(){
//            return android.os.Build.MODEL.replaceAll(";", " ");
//        }
//
//
//        public static String getRelease(){
//            return android.os.Build.VERSION.RELEASE.replaceAll(";", " ");
//        }

    }


    public static class StringUtils {

        public static String getMyCountryCode(Context context) {
            return context.getResources().getConfiguration().locale.getLanguage() + "_" + context.getResources().getConfiguration().locale.getCountry();
        }

        public static String getMyCountry(Context context) {
            return context.getResources().getConfiguration().locale.getLanguage();
        }

        public static String formatBits(long bytes) {
            int unit = 1024;
            if (bytes < unit) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = ("KMGTPE").charAt(exp - 1) + "";
            return String.format(Locale.ENGLISH, "%.1f %sb", bytes / Math.pow(unit, exp), pre);
        }

        public static String formatEta(long eta, String left) {

            if (eta > 0) {
                long days = eta / (1000 * 60 * 60 * 24);
                eta -= days * 1000 * 60 * 60 * 24;
                long hours = eta / (1000 * 60 * 60);
                eta -= hours * 1000 * 60 * 60;
                long minutes = eta / (1000 * 60);
                eta -= minutes * 1000 * 60;
                long seconds = eta / 1000;

                String etaString = "";
                if (days > 0) {
                    etaString += days + "d ";
                }
                if (hours > 0) {
                    etaString += hours + "h ";
                }
                if (minutes > 0) {
                    etaString += minutes + "m ";
                }
                if (seconds > 0) {
                    etaString += seconds + "s";
                }

                return etaString + " " + left;
            }
            return "";
        }

        public static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            String query = uri.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return query_pairs;
        }

        public static String withSuffix(String count) {
            long countl = 0;
            try {
                countl = Long.valueOf(count);
            } catch (Exception e) {
                Logger.printException(e);
            }

            return withSuffix(countl);
        }

        public static String withSuffix(long count) {
            if (count < 1000) return String.valueOf(count);
            int exp = (int) (Math.log(count) / Math.log(1000));
            return String.format("%d %c",
                    (int) (count / Math.pow(1000, exp)),
                    "kMGTPE".charAt(exp - 1));
        }

        public static String withDecimalSuffix(long count) {
            double result = (double) count;
            if (result < 1000) return "" + (int)result;
            int exp = (int) (Math.log(result) / Math.log(1000));
            String aux = new DecimalFormat("#.#").format(result / Math.pow(1000, exp));
            return String.format("%s %c", aux, "kMGTPE".charAt(exp - 1));
        }

        /**
         * <p>Joins the elements of the provided {@code Iterator} into
         * a single String containing the provided elements.</p>
         *
         * <p>No delimiter is added before or after the list.
         * A {@code null} separator is the same as an empty String ("").</p>
         *
         * @param iterator  the {@code Iterator} of values to join together, may be null
         * @param separator  the separator character to use, null treated as ""
         * @return the joined String, {@code null} if null iterator input
         */
        public static String join(final Iterator<?> iterator, final String separator) {

            // handle null, zero and one elements before building a buffer
            if (iterator == null) {
                return null;
            }
            if (!iterator.hasNext()) {
                return "";
            }
            final Object first = iterator.next();
            if (!iterator.hasNext()) {
                @SuppressWarnings( "deprecation" ) // ObjectUtils.toString(Object) has been deprecated in 3.2
                final String result = ObjectUtils.toString(first);
                return result;
            }

            // two or more elements
            final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
            if (first != null) {
                buf.append(first);
            }

            while (iterator.hasNext()) {
                if (separator != null) {
                    buf.append(separator);
                }
                final Object obj = iterator.next();
                if (obj != null) {
                    buf.append(obj);
                }
            }
            return buf.toString();
        }

        /**
         * <p>Joins the elements of the provided {@code Iterable} into
         * a single String containing the provided elements.</p>
         *
         * <p>No delimiter is added before or after the list.
         * A {@code null} separator is the same as an empty String ("").</p>
         *
         * @param iterable  the {@code Iterable} providing the values to join together, may be null
         * @param separator  the separator character to use, null treated as ""
         * @return the joined String, {@code null} if null iterator input
         * @since 2.3
         */
        public static String join(final Iterable<?> iterable, final String separator) {
            if (iterable == null) {
                return null;
            }
            return join(iterable.iterator(), separator);
        }

        public static String parseLocalyticsTag(String str) {
            return WordUtils.capitalize(str.replace('-', ' '));
        }

        public static String getFormattedString(Context context, @StringRes int resId, Object... formatArgs) {
            String result;
            final Resources resources = context.getResources();
            try {
                result = resources.getString(resId, formatArgs);
            }catch (UnknownFormatConversionException ex){
                final String resourceEntryName = resources.getResourceEntryName(resId);
                final String displayLanguage = Locale.getDefault().getDisplayLanguage();
                Logger.e("UnknownFormatConversion", "String: " + resourceEntryName + " Locale: " + displayLanguage);
                Crashlytics.log(3, "UnknownFormatConversion", "String: " + resourceEntryName + " Locale: " + displayLanguage);
                result = resources.getString(resId);
            }
            return result;
        }

        /**
         * get rounded value from the given double and remove the .0 if exact number
         *
         * @param number number to be rounded
         * @return rounded number in string format
         */
        public static String getRoundedValueFromDouble(double number) {
            if (number == (long) number) {
                return String.valueOf((long) number);
            } else {
                return String.format("%.1f", number);
            }
        }

        public static String commaSeparatedValues(List<?> list) {
            String s = new String();

            if (list.size() > 0) {
                s = list.get(0).toString();

                for (int i = 1; i < list.size(); i++) {
                    s += "," + list.get(i).toString();
                }
            }

            return s;
        }
    }


    public static class AccountUtils {

        public static boolean isLoggedIn(Context context) {
            AccountManager manager = AccountManager.get(context);

            return manager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length != 0;
        }

        public static boolean isLoggedInOrAsk(Activity activity) {
            final AccountManager manager = AccountManager.get(activity);

            if (manager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length == 0) {
                manager.addAccount(Aptoide.getConfiguration().getAccountType(),
                        AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, activity, null, null);

                return false;
            }
            return true;
        }

    }


    public static class SocialMedia {

        public static void showTwitter(Context context) {
            if (AppUtils.isAppInstalled(context, "com.twitter.android")) {
                String url = "http://www.twitter.com/aptoide";
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(twitterIntent);
//            FlurryAgent.logEvent("Opened_Twitter_App");
            } else {
                Intent intent = new Intent(context, WebViewTwitter.class);
                context.startActivity(intent);
//            FlurryAgent.logEvent("Opened_Twitter_Webview");
            }
        }


        public static @Nullable String getFacebookPageURL(@NonNull Context context, String facebookUrl) {
            PackageManager packageManager = context.getPackageManager();
            String toReturn = facebookUrl;
            try {
                int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) { //newer versions of fb app
                    toReturn = "fb://facewebmodal/f?href=" + facebookUrl;
                }
            } catch (PackageManager.NameNotFoundException e) {
                toReturn = facebookUrl; //normal web url
            }
            return toReturn;
        }

        public static void showFacebook(Context context) {
            if (AppUtils.isAppInstalled(context, "com.facebook.katana")) {
                Intent sharingIntent;
                try {
                    context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/225295240870860"));
                    context.startActivity(sharingIntent);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ActivityNotFoundException notFound) {
                    Toast.makeText(context, context.getString(R.string.not_found), Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(context, WebViewFacebook.class);
                context.startActivity(intent);
//            FlurryAgent.logEvent("Opened_Facebook_Webview");
            }
        }

        public static void acceptTimeline() {
            Preferences.putBooleanAndCommit(Preferences.TIMELINE_ACEPTED_BOOL, true);

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ChangeUserSettingsRequest request = new ChangeUserSettingsRequest();
                    request.addTimeLineSetting(ChangeUserSettingsRequest.TIMELINEACTIVE);
                    try {
                        request.loadDataFromNetwork();
                    } catch (Exception e) {
                        Logger.printException(e);
                    }
                }
            });
        }

    }


    public static class AppUtils {

        public static boolean isAppInstalled(Context context, String packageName) {
            PackageManager pm = context.getPackageManager();
            boolean installed;
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                installed = false;
            }
            return installed;
        }

        public static PackageInfo getPackageInfo(Context context, String packageName) {
            PackageManager pm = context.getPackageManager();
            try {
                return pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            } catch (PackageManager.NameNotFoundException e) {
                // Ignore
            }
            return null;
        }

        public static boolean isRooted() {
            return findBinary("su");
        }

        private static boolean findBinary(String binaryName) {
            boolean found = false;

            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                    "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;
                    break;
                }
            }

            return found;
        }

        @NonNull
        private static void iterateExtras(Bundle extras, String tag) {

            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                if (value != null) {
                    Log.d(tag, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
                }
            }
        }


        public static void fillPermissionsForTableLayout(Context context, TableLayout mPermissionsTable, List<ApkPermissionGroup> apkPermissions) {
            final int ZERO_DIP = 0;
            final float WEIGHT_ONE = 1f;


            TableRow tr = new TableRow(context);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            LinearLayout linearLayout;
            int items = 0;


            for (int i = 0; i <= apkPermissions.size(); i++) {

                if (i >= apkPermissions.size()) {
                    if (tr.getChildCount() > 0) {
                        // there's still a TableRow left that needs to be added
                        tr.setPadding(0, 0, 0, 20);
                        mPermissionsTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                } else {
                    items ++;

                    ApkPermissionGroup apkPermission = apkPermissions.get(i);

                    if (apkPermission != null) {
                        linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.row_permission, tr, false);
                        TextView name = (TextView) linearLayout.findViewById(R.id.permission_name);
                        name.setText(apkPermission.getName());

                        for (String s : apkPermission.getDescriptions()) {
                            TextView description  = (TextView) LayoutInflater.from(context).inflate(R.layout.row_description, linearLayout, false);
                            description.setText(s);
                            linearLayout.addView(description);
                        }

                        tr.addView(linearLayout, new TableRow.LayoutParams(ZERO_DIP, TableRow.LayoutParams.WRAP_CONTENT, WEIGHT_ONE));


                        if (items % 2 == 0) {
                            mPermissionsTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            tr = new TableRow(context);
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        }
                    }
                }
            }
        }

        public static List<ApkPermission> parsePermissions(Context context, List<String> permissionArray) {
            List<ApkPermission> list = new ArrayList<>();
            CharSequence csPermissionGroupLabel;
            CharSequence csPermissionLabel;
            PackageManager pm = context.getPackageManager();

            List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(PackageManager.PERMISSION_GRANTED);
            for (String permission : permissionArray) {

                for (PermissionGroupInfo pgi : lstGroups) {
                    try {
                        List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, PackageManager.PERMISSION_GRANTED);
                        for (PermissionInfo pi : lstPermissions) {
                            if (pi.name.equals(permission)) {
                                csPermissionLabel = pi.loadLabel(pm);
                                csPermissionGroupLabel = pgi.loadLabel(pm);
                                list.add(new ApkPermission(csPermissionGroupLabel.toString(), csPermissionLabel.toString()));
                            }
                        }
                    } catch (Exception e) {
                        Logger.printException(e);
                    }
                }
            }

            Collections.sort(list, new Comparator<ApkPermission>() {
                @Override
                public int compare(ApkPermission lhs, ApkPermission rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });

            return list;
        }


        public static ArrayList<ApkPermissionGroup> fillPermissionsGroups(List<ApkPermission> permissions) {
            ArrayList<ApkPermissionGroup> list = new ArrayList<>();
            String prevName = null;
            ApkPermissionGroup apkPermission = null;

            for (int i = 0; i <= permissions.size(); i++) {

                if (i >= permissions.size()) {
                    if (!list.contains(apkPermission)) {
                        list.add(apkPermission);
                    }
                } else {

                    ApkPermission permission = permissions.get(i);

                    if (!permission.getName().equals(prevName)) {
                        prevName = permission.getName();
                        apkPermission = new ApkPermissionGroup(permission.getName(), permission.getDescription());
                        list.add(apkPermission);
                    } else {
                        apkPermission.setDescription(permission.getDescription());
                    }
                }
            }

            return list;
        }


        public static void checkPermissions(Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    activity.requestPermissions(new String[]{Constants.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }


        public static long sumFileSizes(long fileSize, GetAppMeta.Obb obb) {

            if (obb == null || obb.main == null) {
                return fileSize;
            } else if (obb.patch == null) {
                return fileSize + obb.main.filesize.longValue();
            } else {
                return fileSize + obb.main.filesize.longValue() + obb.patch.filesize.longValue();
            }

        }


    }


    public static class DateTimeUtils extends DateUtils {

        private static String mTimestampLabelYesterday;
        private static String mTimestampLabelToday;
        private static String mTimestampLabelJustNow;
        private static String mTimestampLabelMinutesAgo;
        private static String mTimestampLabelHoursAgo;
        private static String mTimestampLabelHourAgo;
        private static String mTimestampLabelDaysAgo;
        private static String mTimestampLabelWeekAgo;
        private static String mTimestampLabelWeeksAgo;
        private static String mTimestampLabelMonthAgo;
        private static String mTimestampLabelMonthsAgo;
        private static String mTimestampLabelYearAgo;
        private static String mTimestampLabelYearsAgo;
        private static DateTimeUtils instance;

        /**
         * Singleton constructor, needed to get access to the application context & strings for i18n
         *
         * @param context Context
         * @return DateTimeUtils singleton instance
         * @throws Exception
         */
        public static DateTimeUtils getInstance(Context context) {
            if (instance == null) {
                instance = new DateTimeUtils();
                mTimestampLabelYesterday = context.getResources().getString(R.string.WidgetProvider_timestamp_yesterday);
                mTimestampLabelToday = context.getResources().getString(R.string.WidgetProvider_timestamp_today);
                mTimestampLabelJustNow = context.getResources().getString(R.string.WidgetProvider_timestamp_just_now);
                mTimestampLabelMinutesAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_minutes_ago);
                mTimestampLabelHoursAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_hours_ago);
                mTimestampLabelHourAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_hour_ago);
                mTimestampLabelDaysAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_days_ago);
                mTimestampLabelWeekAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_week_ago2);
                mTimestampLabelWeeksAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_weeks_ago);
                mTimestampLabelMonthAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_month_ago);
                mTimestampLabelMonthsAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_months_ago);
                mTimestampLabelYearAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_year_ago);
                mTimestampLabelYearsAgo = context.getResources().getString(R.string.WidgetProvider_timestamp_years_ago);
            }
            return instance;
        }

        /**
         * Checks if the given date is yesterday.
         *
         * @param date - Date to check.
         * @return TRUE if the date is yesterday, FALSE otherwise.
         */
        private static boolean isYesterday(long date) {

            final Calendar currentDate = Calendar.getInstance();
            currentDate.setTimeInMillis(date);

            final Calendar yesterdayDate = Calendar.getInstance();
            yesterdayDate.add(Calendar.DATE, -1);

            return yesterdayDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) && yesterdayDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);
        }

        private static String[] weekdays = new DateFormatSymbols().getWeekdays(); // get day names
        private static final long millisInADay = 1000 * 60 * 60 * 24;


        /**
         * Displays a user-friendly date difference string
         *
         * @param timedate Timestamp to format as date difference from now
         * @return Friendly-formatted date diff string
         */
        public String getTimeDiffString(Context context, long timedate) {
            Calendar startDateTime = Calendar.getInstance();
            Calendar endDateTime = Calendar.getInstance();
            endDateTime.setTimeInMillis(timedate);
            long milliseconds1 = startDateTime.getTimeInMillis();
            long milliseconds2 = endDateTime.getTimeInMillis();
            long diff = milliseconds1 - milliseconds2;

            long hours = diff / (60 * 60 * 1000);
            long minutes = diff / (60 * 1000);
            minutes = minutes - 60 * hours;
            long seconds = diff / (1000);

            boolean isToday = DateTimeUtils.isToday(timedate);
            boolean isYesterday = DateTimeUtils.isYesterday(timedate);

            if (hours > 0 && hours < 12) {
                return hours == 1 ? AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_hour_ago, hours) : AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_hours_ago, hours);
            } else if (hours <= 0) {
                if (minutes > 0)
                    return AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_minutes_ago, minutes);
                else {
                    return mTimestampLabelJustNow;
                }
            } else if (isToday) {
                return mTimestampLabelToday;
            } else if (isYesterday) {
                return mTimestampLabelYesterday;
            } else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
                return weekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
            } else {
                return formatDateTime(context, timedate, DateUtils.FORMAT_NUMERIC_DATE);
            }
        }

        public String getTimeDiffAll(Context context, long time) {

            long diffTime = new Date().getTime() - time;

            if (isYesterday(time) || isToday(time)) {
                getTimeDiffString(context, time);
            } else {
                if (diffTime < DateUtils.WEEK_IN_MILLIS) {
                    int diffDays = Double.valueOf(Math.ceil(diffTime / millisInADay)).intValue();
                    return diffDays == 1 ? mTimestampLabelYesterday : AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_days_ago, diffDays);
                } else if (diffTime < DateUtils.WEEK_IN_MILLIS * 4) {
                    int diffDays = Double.valueOf(Math.ceil(diffTime / WEEK_IN_MILLIS)).intValue();
                    return diffDays == 1 ? mTimestampLabelMonthAgo : AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_months_ago, diffDays);
                } else if (diffTime < DateUtils.WEEK_IN_MILLIS * 4 * 12) {
                    int diffDays = Double.valueOf(Math.ceil(diffTime / (WEEK_IN_MILLIS * 4))).intValue();
                    return diffDays == 1 ? mTimestampLabelMonthAgo : AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_months_ago, diffDays);
                } else {
                    int diffDays = Double.valueOf(Math.ceil(diffTime / (WEEK_IN_MILLIS * 4 * 12))).intValue();
                    return diffDays == 1 ? mTimestampLabelYearAgo : AptoideUtils.StringUtils.getFormattedString(context, R.string.WidgetProvider_timestamp_years_ago, diffDays);
                }
            }

            return getTimeDiffString(context, time);
        }
    }


    public static class VoteUtils {

        public static void voteComment(SpiceManager spiceManager, int commentId,
                                       String repoName,
                                       String token,
                                       RequestListener<GenericResponseV2> commentRequestListener,
                                       AddApkCommentVoteRequest.CommentVote vote) {


            AddApkCommentVoteRequest commentVoteRequest = new AddApkCommentVoteRequest();

            commentVoteRequest.setRepo(repoName);
            commentVoteRequest.setToken(token);
            commentVoteRequest.setCmtid(commentId);
            commentVoteRequest.setVote(vote);

            spiceManager.execute(commentVoteRequest, commentRequestListener);
            Toast.makeText(Aptoide.getContext(), Aptoide.getContext().getString(R.string.casting_vote), Toast.LENGTH_SHORT).show();
        }
    }


    public static class AdNetworks {

        public static String parseString(Context context, String clickUrl) throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {

            String deviceId = android.provider.Settings.Secure.getString(Aptoide.getContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            String myid = PreferenceManager.getDefaultSharedPreferences(context).getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), "NoInfo");

            if (deviceId != null) {
                clickUrl = clickUrl.replace("[USER_ANDROID_ID]", deviceId);
            }

            if (myid != null) {
                clickUrl = clickUrl.replace("[USER_UDID]", myid);
            }

            clickUrl = replaceAdvertisementId(clickUrl, context);
            clickUrl = clickUrl.replace("[TIME_STAMP]", String.valueOf(new Date().getTime()));

            return clickUrl;
        }

        private static String replaceAdvertisementId(String clickUrl, Context context) throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {

            String aaId = "";
            if (GoogleServices.checkGooglePlayServices(context)) {
                if (AptoideUtils.getSharedPreferences().contains("advertisingIdClient")) {
                    aaId = AptoideUtils.getSharedPreferences().getString("advertisingIdClient", "");
                } else {
                    try {
                        aaId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
                    } catch (Exception e) {
                        // In case we try to do this from a Broadcast Receiver, exception will be thrown.
                        Logger.w("AptoideUtils", e.getMessage());
                    }
                }
            } else {
                byte[] data = new byte[16];
                String deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.setSeed(deviceId.hashCode());
                secureRandom.nextBytes(data);
                aaId = UUID.nameUUIDFromBytes(data).toString();
            }

            clickUrl = clickUrl.replace("[USER_AAID]", aaId);

            return clickUrl;
        }

        /**
         * Execute a simple request (knock at the door) to the given URL.
         * @param url
         */
        public static void knock(String url) {
            OkHttpClient client = new OkHttpClient();

            Request click = new Request.Builder().url(url).build();

            client.newCall(click).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                }
            });
        }

    }


    public static class GoogleServices {

        public static boolean checkGooglePlayServices(Context context) {
            return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
        }

    }

    public static class ServerConnectionUtils {

        public static String getErrorCodeFromErrorList(Context context, List<ErrorResponse> errors) {
            StringBuilder errorMessage = new StringBuilder();
            Integer errorCode;
            if (errors == null) {
                return "";
            }
            for (ErrorResponse error : errors) {
                if (errors.size()>1) {
                    errorMessage.append("\n");
                }
                errorCode = Errors.getErrorsMap().get(error.code);
                if (errorCode != null) {
                    errorMessage.append(context.getString(errorCode));
                } else {
                    errorMessage.append(context.getString(R.string.error_occured));
                }
            }

            if (errors.size()>0&& TextUtils.isEmpty(errorMessage.toString())) {
                errorMessage.append(context.getString(R.string.error_occured));
            }
            return errorMessage.toString();
        }
    }

    public static class Concurrency{
        private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        public static void post(final Activity activity, final Runnable runnable, long delayInMillis) {
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    activity.runOnUiThread(runnable);
                }
            }, delayInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private static boolean isNullOrEmpty(Object o) {
        return o == null || "".equals(o);
    }


    /**
     * this class should have all the utils methods related to crashlytics
     */
    public static class CrashlyticsUtils{
        public static final String SCREEN_HISTORY = "SCREEN_HISTORY";
        public static final String NUMBER_OF_SCREENS = "NUMBER_OF_SCREENS";
        public static final String NUMBER_OF_SCREENS_ON_BACK_STACK = "NUMBER_OF_SCREENS_ON_BACK_STACK";
        /**
         * arrayList with all screen names history
         */
        private static ArrayList<String> history = new ArrayList<>();

        /**
         * this var sets the max screens should be added to history
         */
        private static int MAX_HISTORY = 10;

        /**
         * This saves the number of screens showed
         */
        private static int totalNumberScreens = 0;

        /**
         * This saves the number of screens on back stack
         */
        private static int numberScreensOnBackStack = 0;

        /**
         * this method adds a screen name to the history to be reported to crashlytics
         * @param screeName screen name that should be reported to crashlytics
         */
        public static void addScreenToHistory (String screeName) {
            if (BuildConfig.FABRIC_CONFIGURED) {
                addScreen(screeName);
                Crashlytics.setString(SCREEN_HISTORY, history.toString());
            }
        }

        /**
         * Adds the screen to history.
         * @param screeName Name of the screen to add to history.f
         */
        private static void addScreen(String screeName) {
            if (history.size() >= MAX_HISTORY) {
                history.remove(0);
            }
            history.add(screeName);
        }

        public static void subsctibeActivityLiveCycleEvent() {
            BusProvider.getInstance().register(new LifeCycleMonitor());
        }

        /**
         * Updates the total screens showed and the screens on back stack
         *
         * @param isAdding Indicates if it's to update the number due to a new screen (true) or not (false)
         */
        public static void updateNumberOfScreens(boolean isAdding) {
            if (isAdding) {
                totalNumberScreens++;
                numberScreensOnBackStack++;
                Crashlytics.setInt(NUMBER_OF_SCREENS, totalNumberScreens);
                Crashlytics.setInt(NUMBER_OF_SCREENS_ON_BACK_STACK, numberScreensOnBackStack);
            } else {
                numberScreensOnBackStack--;
                Crashlytics.setInt(NUMBER_OF_SCREENS_ON_BACK_STACK, numberScreensOnBackStack);
            }
        }

        public static void resetScreenHistory() {
            if (history != null) {
                history.clear();
            }
        }
    }

    public static class FlurryAppviewOrigin {

        public static final int MAX_ARRAY_SIZE = 5;

        private static ArrayList<String> mEventActions = new ArrayList<>();

        public static void addAppviewOrigin(String category) {
            if (category != null && !TextUtils.isEmpty(category)) {
                int indexOf = mEventActions.indexOf(category);
                if (indexOf >= 0) {
                    mEventActions.remove(category);
                } else if (mEventActions.size() > MAX_ARRAY_SIZE) {
                    mEventActions.remove(0);
                }
                mEventActions.add(category);
            }
        }

        public static String getAppviewOrigin() {
            List<String> aux = new ArrayList<>();
            for (int i = mEventActions.size() - 1; i >= 0; i--) {
                aux.add(mEventActions.get(i));
            }
            return TextUtils.join("_", aux);
        }

        public static void resetAppviewOrigins() {
            mEventActions.clear();
            mEventActions.add("home");
        }
    }
}