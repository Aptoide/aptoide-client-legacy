package com.aptoide.amethyst.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by rmateus on 12-12-2014.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {

            RecyclerView mTarget = null;

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof RecyclerView) {
                    mTarget = (RecyclerView) child;
                    break;
                }
            }

            if (mTarget != null) {
                return ((LinearLayoutManager) mTarget.getLayoutManager()).findFirstCompletelyVisibleItemPosition() != 0;
            } else {
                return super.canChildScrollUp();
            }


        } else {
            return ViewCompat.canScrollVertically(getChildAt(0), -1);
        }
    }

}
