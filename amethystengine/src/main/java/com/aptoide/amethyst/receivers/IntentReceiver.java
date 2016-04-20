package com.aptoide.amethyst.receivers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Base64;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.xml.XmlAppHandler;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.ApkSuggestionJson;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.aptoide.amethyst.AppViewActivity;
import com.aptoide.amethyst.MainActivity;

import com.aptoide.amethyst.SearchActivity;
import com.aptoide.amethyst.ui.SearchManager;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 10-10-2013
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class IntentReceiver extends AptoideBaseActivity implements DialogInterface.OnDismissListener {

    private ArrayList<String> server;
    private String TMP_MYAPP_FILE;
    private HashMap<String, String> app;
    private final AptoideDatabase db = new AptoideDatabase(Aptoide.getDb());
    private AsyncTask<String, Void, Void> asyncTask;
//    private DownloadService service;
    
//    private Class startClass = Aptoide.getConfiguration().getStartActivityClass();
//    private Class appViewClass = Aptoide.getConfiguration().getAppViewActivityClass();
    private Class startClass = MainActivity.class;
    private Class appViewClass = AppViewActivity.class;
    private Class searchManagerClass = SearchManager.class;

//    private ServiceConnection downloadConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder downloadService) {
////            service = ((DownloadService.LocalBinder) downloadService).getService();
//
//            try {
//                continueLoading();
//            } catch (Exception e) {
//                Logger.printException(e);
//                finish();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        bindService(new Intent(this, DownloadService.class), downloadConnection, BIND_AUTO_CREATE);
        continueLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (asyncTask != null) {
            asyncTask.cancel(true);
        }

//        unbindService(downloadConnection);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        proceed();
    }

    private void proceed() {
        if (server != null) {
            startActivityWithRepo(server);
        } else {
            Toast.makeText(this, getString(R.string.error_occured), Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private void continueLoading() {

        TMP_MYAPP_FILE = getCacheDir() + "/myapp.myapp";
        String uri = getIntent().getDataString();
        Analytics.ApplicationLaunch.website(uri);

        if (uri.startsWith("aptoiderepo")) {

            ArrayList<String> repo = new ArrayList<>();
            repo.add(uri.substring(14));
            startActivityWithRepo(repo);

        } else if (uri.startsWith("aptoidexml")) {

            String repo = uri.substring(13);
            parseXmlString(repo);
            Intent i = new Intent(IntentReceiver.this, startClass);
            i.putExtra("newrepo", repo);
            i.addFlags(Constants.NEW_REPO_FLAG);
            startActivity(i);
            finish();

        } else if (uri.startsWith("aptoidesearch://")) {
            startIntentFromPackageName(uri.split("aptoidesearch://")[1]);
        } else if (uri.startsWith("aptoidevoicesearch://")) {
            aptoidevoiceSearch(uri.split("aptoidevoicesearch://")[1]);
        } else if (uri.startsWith("market")) {
            String params = uri.split("&")[0];
            String param = params.split("=")[1];
            if (param.contains("pname:")) {
                param = param.substring(6);
            } else if (param.contains("pub:")) {
                param = param.substring(4);
            }
            startIntentFromPackageName(param);

        } else if (uri.startsWith("http://market.android.com/details?id=")) {
            String param = uri.split("=")[1];
            startIntentFromPackageName(param);

        } else if (uri.startsWith("https://market.android.com/details?id=")) {
            String param = uri.split("=")[1];
            startIntentFromPackageName(param);

        } else if (uri.startsWith("https://play.google.com/store/apps/details?id=")) {
            String params = uri.split("&")[0];
            String param = params.split("=")[1];
            if (param.contains("pname:")) {
                param = param.substring(6);
            } else if (param.contains("pub:")) {
                param = param.substring(4);
            }
            startIntentFromPackageName(param);

        } else if (uri.contains("aptword://")) {

            String param = uri.substring("aptword://".length());

            if (!TextUtils.isEmpty(param)) {

                param = param.replaceAll("\\*", "_").replaceAll("\\+", "/");

                String json = new String(Base64.decode(param.getBytes(), 0));

                Log.d("AptoideAptWord", json);

                ApkSuggestionJson.Ads ad = null;
                try {
                    ad = new ObjectMapper().readValue(json, ApkSuggestionJson.Ads.class);
                } catch (IOException e) {
                    Logger.printException(e);
                }

                if (ad != null) {
                    Intent i = new Intent(this, appViewClass);
                    long id = ad.data.id.longValue();
                    i.putExtra(Constants.APP_ID_KEY, id);
                    i.putExtra("packageName", ad.data.packageName);
                    i.putExtra("repoName", ad.data.repo);
                    i.putExtra("fromSponsored", true);
                    i.putExtra("location", "homepage");
                    i.putExtra("keyword", "__NULL__");
                    i.putExtra("cpc", ad.info.cpc_url);
                    i.putExtra("cpi", ad.info.cpi_url);
                    i.putExtra("whereFrom", "sponsored");
                    i.putExtra("download_from", "sponsored");

                    if (ad.partner != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("partnerType", ad.partner.partnerInfo.name);
                        bundle.putString("partnerClickUrl", ad.partner.partnerData.click_url);
                        i.putExtra("partnerExtra", bundle);
                    }

                    startActivity(i);
                }

                finish();
            }


        } else if (uri.contains("imgs.aptoide.com")) {

            String[] strings = uri.split("-");
            long id = Long.parseLong(strings[strings.length - 1].split("\\.myapp")[0]);

            startFromMyApp(id);
            finish();

        } else if (uri.startsWith("http://webservices.aptoide.com")) {
            /** refactored to remove org.apache libs */
            Map<String, String> params = null;

            try {
                params = AptoideUtils.StringUtils.splitQuery(URI.create(uri));
            } catch (UnsupportedEncodingException e) {
                Logger.printException(e);
            }

            if (params != null) {
                String uid = null;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getKey().equals("uid")) {
                        uid = entry.getValue();
                    }
                }

                if (uid != null) {
                    try {
                        long id = Long.parseLong(uid);
                        startFromMyApp(id);

                    } catch (NumberFormatException e) {
                        Logger.printException(e);
                        Toast.makeText(getApplicationContext(), R.string.simple_error_occured + uid, Toast.LENGTH_LONG).show();
                    }
                }
            }

            finish();


        } else if (uri.startsWith("file://")) {

            downloadMyApp();

        } else if (uri.startsWith("aptoideinstall://")) {

            try {
                long id = Long.parseLong(uri.substring("aptoideinstall://".length()));
                startFromMyApp(id);
            } catch (NumberFormatException e) {
                Logger.printException(e);
            }

            finish();

        } else if (uri.startsWith("aptwords://")) {

            String parsedString = uri.substring("aptwords://".length());

            String[] splitString = parsedString.split("/");

            long id = Long.parseLong(splitString[0]);
            String cpi = splitString[1];


            try {
                cpi = URLDecoder.decode(cpi, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Logger.printException(e);
            }

            Intent i = new Intent(this, appViewClass);

            i.putExtra(Constants.FROM_MY_APP_KEY, true);
            i.putExtra(Constants.APP_ID_KEY, id);
            i.putExtra("cpi", cpi);
            i.putExtra("download_from", "my_app_with_cpi");

            startActivity(i);
            finish();

        } else {
            finish();
        }
    }

    public void startFromMyApp(long id) {
        Intent i = new Intent(this, appViewClass);
        i.putExtra(Constants.FROM_MY_APP_KEY, true);
        i.putExtra(Constants.APP_ID_KEY, id);
        i.putExtra("download_from", "my_app");

        startActivity(i);
    }

    public void startActivityWithRepo(ArrayList<String> repo) {
        Intent i = new Intent(IntentReceiver.this, startClass);
        i.putExtra("newrepo", repo);
        i.addFlags(Constants.NEW_REPO_FLAG);
        startActivity(i);
        Analytics.ApplicationLaunch.newRepo();

        finish();
    }

    private void downloadMyApp() {
        asyncTask = new MyAppDownloader().execute(getIntent().getDataString());
    }


    private void downloadMyappFile(String myappUri) throws Exception {
        try {
            URL url = new URL(myappUri);
            URLConnection connection;
            if (!myappUri.startsWith("file://")) {
                connection = url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
            } else {
                connection = url.openConnection();
            }

            BufferedInputStream getit = new BufferedInputStream(connection.getInputStream(), 1024);

            File file_teste = new File(TMP_MYAPP_FILE);
            if (file_teste.exists())
                file_teste.delete();

            FileOutputStream saveit = new FileOutputStream(TMP_MYAPP_FILE);
            BufferedOutputStream bout = new BufferedOutputStream(saveit, 1024);
            byte data[] = new byte[1024];

            int readed = getit.read(data, 0, 1024);
            while (readed != -1) {
                bout.write(data, 0, readed);
                readed = getit.read(data, 0, 1024);
            }


            bout.close();
            getit.close();
            saveit.close();
        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    private void parseXmlMyapp(String file) throws Exception {

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XmlAppHandler handler = new XmlAppHandler();
            sp.parse(new File(file), handler);
            server = handler.getServers();
            app = handler.getApp();

        } catch (IOException | SAXException | ParserConfigurationException e) {
            Logger.printException(e);
        }
    }

    private void parseXmlString(String file) {

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            XmlAppHandler handler = new XmlAppHandler();
            xr.setContentHandler(handler);

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(file));
            xr.parse(is);
            server = handler.getServers();
            app = handler.getApp();

        } catch (IOException | SAXException | ParserConfigurationException e) {
            Logger.printException(e);
        }
    }

    public void aptoidevoiceSearch(String param) {
// TODO: voiceSearch was used by a foreign app, dunno if still used.
//        Cursor c = new AptoideDatabase(Aptoide.getDb()).getSearchResults(param, StoreActivity.Sort.DOWNLOADS);
//
//        ArrayList<String> namelist = new ArrayList<String>();
//        ArrayList<Long> idlist = new ArrayList<Long>();
//
//        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//            namelist.add(c.getString(c.getColumnIndex("name")));
//            idlist.add(c.getLong(c.getColumnIndex("_id")));
//        }
//
//        Intent i = new Intent();
//        i.putStringArrayListExtra("namelist", namelist);
//        i.putExtra("idlist", AptoideUtils.longListToLongArray(idlist));
//
//        setResult(UNKONWN_FLAG, i);
        finish();
    }

//    public void startIntentFromPackageName(String param) {
//        long id = db.getApkFromPackage(param);
//        Intent i;
//        if (id > 0) {
//            i = new Intent(this, appViewClass);
//            i.putExtra(Constants.APP_ID_KEY, id);
//            i.putExtra("download_from", "market_intent");
//        } else {
//            i = new Intent(this, searchManagerClass);
//            i.putExtra("search", param);
//        }
//
//        startActivity(i);
//        finish();
//    }

    public void startIntentFromPackageName(String packageName) {
        Intent i;

        if (AptoideUtils.AppUtils.isAppInstalled(this, packageName)) {
            i = new Intent(this, appViewClass);
            i.putExtra(Constants.MARKET_INTENT, true);
            i.putExtra(Constants.PACKAGENAME_KEY, packageName);

        } else {
            i = new Intent(this, SearchActivity.class);
            i.putExtra(android.app.SearchManager.QUERY, packageName);
//            i.putExtra("search", packageName);
        }

        startActivity(i);
        finish();
    }

    class MyAppDownloader extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(IntentReceiver.this);
            pd.show();
            pd.setCancelable(false);
            pd.setMessage(getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                downloadMyappFile(params[0]);
                parseXmlMyapp(TMP_MYAPP_FILE);
            } catch (Exception e) {
                Logger.printException(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd.isShowing() && !isFinishing()) pd.dismiss();

            if (app != null && !app.isEmpty()) {

                /** never worked... */
//                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IntentReceiver.this);
//                final AlertDialog installAppDialog = dialogBuilder.create();
////                installAppDialog.setTitle(ApplicationAptoide.MARKETNAME);
//                installAppDialog.setIcon(android.R.drawable.ic_menu_more);
//                installAppDialog.setCancelable(false);
//
//
//                installAppDialog.setMessage(getString(R.string.installapp_alrt) + app.get("name") + "?");
//
//                installAppDialog.setButton(Dialog.BUTTON_POSITIVE, getString(android.R.string.yes), new Dialog.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
////                        Download download = new Download();
////                        Log.d("Aptoide-IntentReceiver", "getapk id: " + id);
////                        download.setId(id);
////                        ((Start)getApplicationContext()).installApp(0);
//
//                        Toast toast = Toast.makeText(IntentReceiver.this, getString(R.string.starting_download), Toast.LENGTH_SHORT);
//                        toast.show();
//                    }
//                });
//
//                installAppDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(android.R.string.no), neutralListener);
//                installAppDialog.setOnDismissListener(IntentReceiver.this);
//                installAppDialog.show();

            } else {
                proceed();
            }
        }
    }

    @Override
    protected String getScreenName() {
        return null;
    }

}
