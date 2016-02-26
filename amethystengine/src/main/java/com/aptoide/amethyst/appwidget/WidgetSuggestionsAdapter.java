package com.aptoide.amethyst.appwidget;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.Logger;



/**
 * Created by hsousa on 06-01-2014.
 */
public class WidgetSuggestionsAdapter extends CursorAdapter {

    public WidgetSuggestionsAdapter(Context context) {
        super(context, null, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_search_widget_suggestion, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        WidgetSuggestionHolder holder = (WidgetSuggestionHolder) view.getTag();
        if (holder == null) {
            holder = new WidgetSuggestionHolder();
            holder.searchSuggestion = (TextView) view.findViewById(R.id.search_suggestion);
            view.setTag(holder);
        }

        final String suggestion = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));

        holder.searchSuggestion.setText(Html.fromHtml(suggestion));
        Logger.d("Widget", "Added to adapter: " + holder.searchSuggestion.getText().toString());
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
    }

    public static class WidgetSuggestionHolder {
        public TextView searchSuggestion;
    }
}
