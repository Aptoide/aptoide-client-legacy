package com.aptoide.amethyst.appwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

/**
 * Created by fabio on 15-10-2015.
 */
public class RecyclerView extends android.support.v7.widget.RecyclerView {
    public RecyclerView(Context context) {
        super(context);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void scrollTo(int x, int y) {
        //fix
    }

    private ContextMenu.ContextMenuInfo mContextMenuInfo = null;

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }


    @Override
    public boolean showContextMenuForChild(View originalView) {
        final int longPressPosition = getChildPosition(originalView);
        if (longPressPosition >= 0) {
            final long longPressId = getAdapter().getItemId(longPressPosition);
            mContextMenuInfo = createContextMenuInfo(longPressPosition, longPressId);
            return super.showContextMenuForChild(originalView);
        }
        return false;
    }

    ContextMenu.ContextMenuInfo createContextMenuInfo(int position, long id) {
        return new RecyclerContextMenuInfo(position, id);
    }

    /**
     * Extra menu information provided to the
     * {@link android.view.View.OnCreateContextMenuListener#onCreateContextMenu(android.view.ContextMenu, View, ContextMenuInfo) }
     * callback when a context menu is brought up for this AdapterView.
     */
    public static class RecyclerContextMenuInfo implements ContextMenu.ContextMenuInfo {

        public RecyclerContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }

        /**
         * The position in the adapter for which the context menu is being
         * displayed.
         */
        public int position;

        /**
         * The row id of the item for which the context menu is being displayed.
         */
        public long id;
    }


}

