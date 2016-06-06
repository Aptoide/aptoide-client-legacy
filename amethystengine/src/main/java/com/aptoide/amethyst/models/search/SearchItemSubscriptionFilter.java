/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 30/05/2016.
 */

package com.aptoide.amethyst.models.search;

import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.dataprovider.webservices.models.v7.SearchItem;
import com.aptoide.models.stores.Store;

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

	private List<SearchItem> filterSubscribed(List<SearchItem> items) {
		final List<Store> subscribedStores = database.getSubscribedStores();
		final List<SearchItem> subscribedSearchItems = new ArrayList<>();

		for (Store store: subscribedStores) {
			for (SearchItem app: items) {
				if (app.store.name.equals(store.getName())) {
					subscribedSearchItems.add(app);
				}
			}
		}
		return subscribedSearchItems;
	}
}