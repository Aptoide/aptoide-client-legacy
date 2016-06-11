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

	public List<SearchApk> convert(List<SearchItem> searchItems, int offset, boolean fromSubscribedStore) {
		final List<SearchApk> myStoresApps = new ArrayList<>();

		int position = 0;
		for (SearchItem searchItem: searchItems) {
			position++;
			myStoresApps.add(convert(searchItem, fromSubscribedStore, offset + position));
		}
		return myStoresApps;
	}

	private SearchApk convert(SearchItem searchItem, boolean fromSubscribedStore, int position) {
		return new SearchApk(bucketSize,
				fromSubscribedStore,
				position,
				searchItem.name,
				searchItem.store.name,
				searchItem.packageName,
				searchItem.file.vername,
				searchItem.file.vercode.intValue(),
				searchItem.file.md5sum,
				searchItem.updated,
				searchItem.file.malware.rank.equals(ViewItem.File.Malware.TRUSTED)? 2 : 0,
				searchItem.icon,
				searchItem.hasVersions,
				searchItem.stats.rating.avg,
				searchItem.store.appearance.theme,
				searchItem.stats.downloads.longValue());
	}
}
