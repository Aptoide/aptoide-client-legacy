package com.aptoide.amethyst.websockets;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.aptoide.amethyst.utils.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 30-09-2013
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketSingleton {
    public static final String TAG = "WebSocketSingleton";

    private static WebSocketClient web_socket_client;
    String[] matrix_columns = new String[]{SearchManager.SUGGEST_COLUMN_ICON_1,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_QUERY,
            "_id"};
    private String query;
    private String buffer;

    private WebSocketClient.Listener listener = new WebSocketClient.Listener() {
        @Override
        public void onConnect() {
            Logger.d(TAG, "On Connect");

        }

        @Override
        public void onMessage(String message) {

            try {
                JSONArray array = new JSONArray(message);
                MatrixCursor mCursor = new MatrixCursor(matrix_columns);
                for (int i = 0; i < array.length(); i++) {
                    String suggestion = array.get(i).toString();
                    Logger.d(TAG, "Suggestion " + suggestion);
                    addRow(mCursor, suggestion, i);
                }

                if (array.length() == 0) {
                    buffer = query;
                }

                blockingQueue.add(mCursor);

            } catch (JSONException e) {
                Logger.printException(e);
            }

        }

        @Override
        public void onMessage(byte[] data) {
            Logger.d(TAG, Arrays.toString(data));
        }

        @Override
        public void onDisconnect(int code, String reason) {
            Logger.d(TAG, reason);
        }

        @Override
        public void onError(Exception error) {
            Logger.printException(error);
        }
    };
    private Uri mNotificationUri;
    private Context mContext;
    private BlockingQueue<Cursor> blockingQueue;

    private WebSocketSingleton() {
    }

    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> future;

    public void send(final String query) {
        this.query = query;
        // Fix nullPointer
        if (web_socket_client != null && web_socket_client.isConnected() && query.length() > 2 && (buffer == null || !query.startsWith(buffer))) {

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    JsonFactory f = new JsonFactory();

                    StringWriter writer = new StringWriter();
                    try {
                        JsonGenerator g = f.createJsonGenerator(writer);
                        g.writeStartObject();
                        g.writeStringField("query", query);
                        g.writeEndObject();
                        g.close();
                    } catch (IOException e) {
                        Logger.printException(e);
                    }
                    //"{\"query\":\"" + query + "\"}"

                    web_socket_client.send(writer.toString());

                    Logger.d(TAG, "Sending " + writer.toString());
                }
            };

            if (future != null) {
                future.cancel(false);
            }

            future = scheduledExecutorService.schedule(runnable, 500L, TimeUnit.MILLISECONDS);

//            JsonFactory f = new JsonFactory();
//
//            StringWriter writer = new StringWriter();
//            try {
//                JsonGenerator g = f.createJsonGenerator(writer);
//                g.writeStartObject();
//                g.writeStringField("query", query);
//                g.writeEndObject();
//                g.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            //"{\"query\":\"" + query + "\"}"
//
//            web_socket_client.send(writer.toString());
//
//            Logger.d(TAG, "Sending " + writer.toString());
        } else {
            MatrixCursor mCursor = null;
            blockingQueue.add(mCursor);
        }

    }

    public static WebSocketSingleton getInstance() {
        return WebSocketHolder.INSTANCE;
    }

    public void disconnect() {

        Logger.d(TAG, "onDisconnect");

        if (web_socket_client != null) {
            web_socket_client.disconnect();
            web_socket_client = null;
        }

    }

    public void connect() {

        if (web_socket_client == null) {
            web_socket_client = new WebSocketClient(java.net.URI.create("ws://buzz.webservices.aptoide.com:9000"), listener, null);
            web_socket_client.connect();
        }
        Logger.d(TAG, "OnConnecting");
    }

    public WebSocketSingleton setNotificationUri(Uri uri) {
        this.mNotificationUri = uri;
        return this;
    }

    public WebSocketSingleton setContext(Context context) {
        this.mContext = context;
        return this;
    }

    private void addRow(MatrixCursor matrix_cursor, String string, int i) {
        matrix_cursor.newRow().add(null).add(string).add(string).add(i);
    }

    public void setBlockingQueue(BlockingQueue a) {
        this.blockingQueue = a;
    }

    private static class WebSocketHolder {
        public static final WebSocketSingleton INSTANCE = new WebSocketSingleton();
    }


}
