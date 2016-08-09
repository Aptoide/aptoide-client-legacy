/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

public class SecurityAlternativeParser implements AlternativeParser<SecurityOption> {

	@Override
	public SecurityOption parse(String string) {
		switch (string) {
			case "none":
				return SecurityOption.NONE;
			case "warning-pop-up":
				return SecurityOption.WARNING_POP_UP;
			case "security-overlay":
				return SecurityOption.SECURITY_OVERLAY;
			case "both":
				return SecurityOption.BOTH;
			default:
				return SecurityOption.NONE;
		}
	}
}
