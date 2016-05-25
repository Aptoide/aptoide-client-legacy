/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchItem extends ViewItem {

	@JsonProperty("has_versions") public boolean hasVersions;

}
