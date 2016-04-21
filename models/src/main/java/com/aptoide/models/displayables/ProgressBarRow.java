/*******************************************************************************
 * Copyright (c) 2015 hsousa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.models.displayables;

import com.aptoide.models.displayables.Displayable;

public class ProgressBarRow extends Displayable {

    public ProgressBarRow(int bucketSize) {
        super(bucketSize);
    }

    @Override
    public int getSpanSize() {
        return FULL_ROW;
    }
}
