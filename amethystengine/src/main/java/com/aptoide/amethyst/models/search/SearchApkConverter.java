/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.amethyst.models.search;

import com.aptoide.dataprovider.webservices.models.v7.SearchItem;
import com.aptoide.dataprovider.webservices.models.v7.ViewItem;
import com.aptoide.models.displayables.SearchApk;

import java.util.ArrayList;
import java.util.List;

public class SearchApkConverter {

	private final int bucketSize;

	public SearchApkConverter(int bucketSize) {
		this.bucketSize = bucketSize;
	}

	public List<SearchApk> convert(List<SearchItem> searchItems, boolean fromSubscribedStore) {
		final List<SearchApk> myStoresApps = new ArrayList<>();

		int position = 0;
		for (SearchItem searchItem: searchItems) {
			position++;
			myStoresApps.add(convert(searchItem, fromSubscribedStore, position));
		}
		return myStoresApps;
	}

	private SearchApk convert(SearchItem searchItem, boolean fromSubscribedStore, int position) {
		final SearchApk searchApk = new SearchApk(bucketSize);
		searchApk.name = searchItem.name;
		searchApk.downloads = searchItem.stats.downloads.longValue();
		searchApk.fromSubscribedStore = fromSubscribedStore;
		searchApk.hasOtherVersions = searchItem.hasVersions;
		searchApk.icon = searchItem.icon;
		searchApk.malrank = searchItem.file.malware.rank.equals(ViewItem.File.Malware.TRUSTED)? 2 : 0;
		searchApk.md5sum = searchItem.file.md5sum;
		searchApk.packageName = searchItem.packageName;
		searchApk.repo = searchItem.store.name;
		searchApk.repo_theme = searchItem.store.appearance.theme;
		searchApk.stars = searchItem.stats.rating.avg;
		searchApk.vername = searchItem.file.vername;
		searchApk.vercode = searchItem.file.vercode.intValue();
		searchApk.position = position;
		searchApk.timestamp = searchItem.updated;
		return searchApk;
	}
}
