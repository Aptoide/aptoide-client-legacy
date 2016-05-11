/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 02/05/2016.
 */

package com.aptoide.amethyst.social;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class ContactsProvider {

	private final ContentResolver contentResolver;

	public ContactsProvider(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	public Observable<List<SimpleContact>> getDeviceContacts() throws SecurityException {
		return Observable.create(new OnSubscribeRegisterContentObserver(contentResolver,
				ContactsContract.Contacts.CONTENT_URI, true, null))
				.flatMap(new Func1<Void, Observable<List<SimpleContact>>>() {

					@Override
					public Observable<List<SimpleContact>> call(Void aVoid) {
						List<SimpleContact> contacts = new ArrayList<>();

						final Cursor cursor = contentResolver.query(ContactsContract.Contacts
								.CONTENT_URI, null, null, null, null);

						try {
							SimpleContact simpleContact;
							if (cursor != null && cursor.moveToFirst()) {
								do {
									simpleContact = getSimpleContact(cursor.getString(cursor
											.getColumnIndex(ContactsContract.Contacts._ID)));
									if (simpleContact != null) {
										contacts.add(simpleContact);
									}
								} while (cursor.moveToNext());
							}
						} finally {
							if (cursor != null) {
								cursor.close();
							}
						}
						return Observable.just(contacts);
					}
				});
	}

	@Nullable
	private SimpleContact getSimpleContact(String contactId) {
		final List<String> phones = getContactPhones(contactId);
		final List<String> emails = getContactEmails(contactId);
		if (emails.isEmpty() && phones.isEmpty()) {
			return null;
		}
		return new SimpleContact(emails, phones);
	}

	@NonNull
	private List<String> getContactEmails(String contactId) {
		final List<String> emails = new ArrayList<>();
		Cursor emailCursor = null;
		try {
			emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email
					.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " +
					"?", new String[]{contactId}, null);

			if (emailCursor != null && emailCursor.moveToFirst()) {
				do {
					emails.add(emailCursor.getString(emailCursor.getColumnIndex(ContactsContract
							.CommonDataKinds.Email.DATA)));
				} while (emailCursor.moveToNext());
			}
		} finally {
			if (emailCursor != null) {
				emailCursor.close();
			}
		}
		return emails;
	}

	@NonNull
	private List<String> getContactPhones(String contactId) {
		final List<String> phones = new ArrayList<>();
		Cursor phoneCursor = null;
		try {
			phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone
					.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
					"?", new String[]{contactId}, null);

			if (phoneCursor != null && phoneCursor.moveToFirst()) {
				do {
					phones.add(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract
							.CommonDataKinds.Phone.NUMBER)));
				} while (phoneCursor.moveToNext());
			}
		} finally {
			if (phoneCursor != null) {
				phoneCursor.close();
			}
		}
		return phones;
	}
}
