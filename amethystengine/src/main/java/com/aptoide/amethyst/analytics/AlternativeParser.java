/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

/**
 * Created by marcelobenites on 5/6/16.
 */
public interface AlternativeParser<T> {

	T parse(String string);

}
