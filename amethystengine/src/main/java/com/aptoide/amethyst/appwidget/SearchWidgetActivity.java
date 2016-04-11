package com.aptoide.amethyst.appwidget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.websockets.WebSocketSingleton;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


import com.aptoide.amethyst.SearchActivity;


/**
 * Created by brutus on 02-01-2014.
 */
public class SearchWidgetActivity extends AppCompatActivity {

    private AutoCompleteTextView searchAutoComplete;
    private WidgetSuggestionsAdapter suggestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.search_widget_activity);

        searchAutoComplete = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchAutoComplete.setThreshold(3);

        suggestionAdapter = new WidgetSuggestionsAdapter(this);

        searchAutoComplete.setAdapter(suggestionAdapter);

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 2) {
                    try {
                        WebSocketSingleton.getInstance().send(searchAutoComplete.getText().toString());
                        handler.post(runnable);
                    } catch (Exception e) {
                        Logger.printException(e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchAutoComplete.getText().toString();

                if (searchQuery.length() != 0) {
                    searchApp(searchQuery);
                    finish();

                } else {
                    Toast toast = Toast.makeText(SearchWidgetActivity.this, R.string.empty_search, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getString(R.string.search));
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        WebSocketSingleton webSocketSingleton = WebSocketSingleton.getInstance();
        webSocketSingleton.connect();
        webSocketSingleton.setBlockingQueue(blockingQueue);
    }


    final static Handler handler = new Handler();
    BlockingQueue<Cursor> blockingQueue = new ArrayBlockingQueue<>(1);
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Cursor matrix_cursor = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
                for (matrix_cursor.moveToFirst(); !matrix_cursor.isAfterLast(); matrix_cursor.moveToNext()) {
                    Logger.d("Cursor", matrix_cursor.getString(matrix_cursor.getColumnIndex(android.app.SearchManager.SUGGEST_COLUMN_TEXT_1)));
                }
                suggestionAdapter = new WidgetSuggestionsAdapter(SearchWidgetActivity.this);

                searchAutoComplete.setAdapter(suggestionAdapter);
                suggestionAdapter.swapCursor(matrix_cursor);
                suggestionAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Logger.printException(e);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        WebSocketSingleton.getInstance().disconnect();
    }

    private void searchApp(String query) {

        /** In v6, searches were done in the browser.  */
//        String url = Aptoide.getConfiguration().getUriSearch() + query + "&q=" + AptoideUtils.HWSpecifications.filters(this);
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        url = url.replaceAll(" ", "%20");
//        i.setData(Uri.parse(url));
//        startActivity(i);

        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra(android.app.SearchManager.QUERY, query);
        startActivity(i);
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


}