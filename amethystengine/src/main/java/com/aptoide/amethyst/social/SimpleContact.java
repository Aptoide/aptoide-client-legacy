/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 02/05/2016.
 */

package com.aptoide.amethyst.social;

public class SimpleContact {

	private String email;
	private String phoneNumber;

	public SimpleContact(String email, String phoneNumber) {
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
