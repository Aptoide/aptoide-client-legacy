/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 31/05/2016.
 */

package com.aptoide.amethyst.analytics;

/**
 * Options for security AB test.
 */
public enum SecurityOption {
	NONE,
	WARNING_POP_UP,
	SECURITY_OVERLAY,
	BOTH;

	public boolean showWarningPopUp() {
		switch (this) {
			case NONE:
			case SECURITY_OVERLAY:
				return false;
			case WARNING_POP_UP:
			case BOTH:
				return true;
			default:
				return false;
		}
	}

	public boolean showSecurityOverlay() {
		switch (this) {
			case BOTH:
			case SECURITY_OVERLAY:
				return true;
			case WARNING_POP_UP:
			case NONE:
				return false;
			default:
				return false;
		}
	}
}