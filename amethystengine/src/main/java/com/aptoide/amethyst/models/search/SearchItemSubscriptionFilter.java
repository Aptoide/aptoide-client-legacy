/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 30/05/2016.
 */

package com.aptoide.amethyst.models.search;

import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.dataprovider.webservices.models.v7.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class SearchItemSubscriptionFilter {

	private final AptoideDatabase database;

	public SearchItemSubscriptionFilter(AptoideDatabase database) {
		this.database = database;
	}

	public List<SearchItem> filterUnsubscribed(List<SearchItem> items) {
		final List<SearchItem> unsubscribedSearchItems = new ArrayList<>(items);
		unsubscribedSearchItems.removeAll(filterSubscribed(items));
		return unsubscribedSearchItems;
	}

	public List<SearchItem> filterSubscribed(List<SearchItem> items) {
		final List<String> subscribedStoresNames = database.getSubscribedStoreNames();
		final List<SearchItem> subscribedSearchItems = new ArrayList<>();

		for (String storeName: subscribedStoresNames) {
			for (SearchItem app: items) {
				if (app.store.name.equals(storeName)) {
					subscribedSearchItems.add(app);
				}
			}
		}
		return subscribedSearchItems;
	}
}