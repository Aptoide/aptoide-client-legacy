/*******************************************************************************
 * Copyright (c) 2015 hsousa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.models.displayables;

import android.os.Parcel;

public class ProgressBarRow extends Displayable {

    public ProgressBarRow(int bucketSize) {
        super(bucketSize);
    }

    protected ProgressBarRow(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProgressBarRow> CREATOR = new Creator<ProgressBarRow>() {
        @Override
        public ProgressBarRow createFromParcel(Parcel in) {
            return new ProgressBarRow(in);
        }

        @Override
        public ProgressBarRow[] newArray(int size) {
            return new ProgressBarRow[size];
        }
    };

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }
}
