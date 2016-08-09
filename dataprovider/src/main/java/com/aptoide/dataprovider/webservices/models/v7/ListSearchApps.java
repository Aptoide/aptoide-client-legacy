/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.dataprovider.webservices.models.v7;

import java.util.ArrayList;
import java.util.List;

public class ListSearchApps {

	public Info info;
	public SearchItemDataList datalist;

	public static class SearchItemDataList extends DataList {

		public List<SearchItem> list = new ArrayList<>();

	}
}
