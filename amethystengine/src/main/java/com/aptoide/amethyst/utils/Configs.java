/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.aptoide.amethyst.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author rafael
 * @since summerinternship2011
 */
public class Configs {

    public final static SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat TIME_STAMP_FORMAT_INFO_XML = new SimpleDateFormat("yyyy-MM-dd");
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String LOGIN_USER_ID = "useridLogin";
    public static final String LOGIN_PASSWORD = "passwordLogin";
    public static final String LOGIN_USER_LOGIN = "usernameLogin";
    public static final String LOGIN_USER_TOKEN = "usernameToken";
    public static final String LOGIN_USER_USERNAME = "userName";
    public static final String LOGIN_DEFAULT_REPO = "defaultRepo";


    private Configs() {}

    public DateFormat getDateFormat(Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

    public DateFormat getTimeFormat(Context context) {
        return android.text.format.DateFormat.getTimeFormat(context);
    }

}
