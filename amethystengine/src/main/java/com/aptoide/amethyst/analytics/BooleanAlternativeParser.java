/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

public class BooleanAlternativeParser implements AlternativeParser<Boolean> {

	@Override
	public Boolean parse(String string) {
		if (string.equals("true")) {
			return true;
		}
		return false;
	}
}
