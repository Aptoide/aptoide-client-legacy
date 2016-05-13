package com.aptoide.amethyst.downloadmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.util.Log;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.configuration.AptoideConfiguration;
import com.aptoide.amethyst.downloadmanager.exception.CompletedDownloadException;
import com.aptoide.amethyst.downloadmanager.exception.IPBlackListedException;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.aptoide.amethyst.downloadmanager.exception.ContentTypeNotApkException;
import com.aptoide.amethyst.downloadmanager.exception.DownloadNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 02-07-2013
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class DownloadConnection implements Serializable {
    private static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    HttpURLConnection connection;
    private BufferedInputStream mStream;
    private final static int TIME_OUT = 30000;
    private boolean paidApp;
    protected URL mURL;

//
//    protected DownloadConnection(URL url)
//    {
//        this.mURL = url;
//    }

    public String getFileName()
    {
        String fileName = this.mURL.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    public URL getURL()
    {
        return this.mURL;
    }

    public DownloadConnection(URL url) throws IOException {
        this.mURL = url;
    }


    /**
     * Refathored to remove org.apache.http.NameValuePair
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        boolean first = true;
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void connect(long downloaded, boolean update) throws IOException, CompletedDownloadException, DownloadNotFoundException, IPBlackListedException, ContentTypeNotApkException {
        connection = (HttpURLConnection) this.mURL.openConnection();

        connection.setConnectTimeout(TIME_OUT);
        connection.setReadTimeout(TIME_OUT);

        connection.setRequestProperty("User-Agent", AptoideUtils.NetworkUtils.getUserAgentString(Aptoide.getContext(), update));

        if(paidApp) {
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Map<String, String> params = new HashMap<>();

            refreshToken();

            String token = SecurePreferences.getInstance().getString("access_token", null);
            params.put("access_token", token);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
        }

        Log.d("DownloadManager", "Downloading from: " + mURL.toString() + " with " + AptoideUtils.NetworkUtils.getUserAgentString(Aptoide.getContext(), update));
        if (downloaded > 0L) {
            // server must support partial content for resume
            connection.addRequestProperty("Range", "bytes=" + downloaded + "-");
            int responseCode = connection.getResponseCode();
            Log.d("DownloadManager", "Response Code is: " + responseCode);
            if (responseCode == SC_REQUESTED_RANGE_NOT_SATISFIABLE) {
                throw new CompletedDownloadException();
            } else if (responseCode != HttpURLConnection.HTTP_PARTIAL) {
                throw new IOException("Server doesn't support partial content.");
            }
        } else if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new DownloadNotFoundException();
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {

                throw new IPBlackListedException();
            }
            // response not ok
            throw new IOException("Cannot retrieve file from server.");
        }

        if("application/json".equals(connection.getHeaderField("Content-Type"))){
            throw new ContentTypeNotApkException();
        }

        mStream = new BufferedInputStream(connection.getInputStream(), 8 * 1024);
    }

    private void refreshToken() {
        Account account = AccountManager.get(Aptoide.getContext()).getAccountsByType(Aptoide.getConfiguration().getAccountType())[0];
        String refreshToken = "";
        try {
            refreshToken = AccountManager.get(Aptoide.getContext()).blockingGetAuthToken(account, AptoideConfiguration.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            Logger.printException(e);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "refresh_token");
        parameters.put("client_id", "Aptoide");
        parameters.put("refresh_token", refreshToken);
//        HttpContent content = new UrlEncodedContent(parameters);
//        GenericUrl url = new GenericUrl(WebserviceOptions.WebServicesLink+"/3/oauth2Authentication");
//        HttpRequest oauth2RefresRequest = AndroidHttp.newCompatibleTransport().createRequestFactory().buildPostRequest(url, content);
//        oauth2RefresRequest.setParser(new JacksonFactory().createJsonObjectParser());
//        OAuth responseJson = oauth2RefresRequest.execute().parseAs(OAuth.class);
//
//        SharedPreferences preferences = SecurePreferences.getInstance();
//
//        preferences.edit().putString("access_token", responseJson.getAccess_token()).commit();
    }

    public void close() {
        connection.disconnect();
    }

    public BufferedInputStream getStream() {
        return mStream;
    }

    public long getShallowSize() throws IOException {
        return mURL.openConnection().getContentLength();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setPaidApp(boolean paidApp) {
        this.paidApp = paidApp;
    }
}
