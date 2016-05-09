/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

public class BooleanAlternativeConverter implements AlternativeConverter<Boolean> {

	@Override
	public Boolean convert(String string) {
		if (string.equals("true")) {
			return true;
		}
		return false;
	}
}
