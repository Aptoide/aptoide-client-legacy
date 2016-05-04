/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 02/05/2016.
 */

package com.aptoide.amethyst.social;

import java.util.List;

public class SimpleContact {

	private List<String> emails;
	private List<String> phoneNumbers;

	public SimpleContact(List<String> emails, List<String> phoneNumbers) {
		this.emails = emails;
		this.phoneNumbers = phoneNumbers;
	}

	public List<String> getEmails() {
		return emails;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
}
