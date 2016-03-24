package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;

/**
 * Created by hsousa on 12-10-2015.
 */
public class AdultItem extends Displayable {

    public AdultItem(int bucketSize) {
        super(bucketSize);
    }

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }

}
