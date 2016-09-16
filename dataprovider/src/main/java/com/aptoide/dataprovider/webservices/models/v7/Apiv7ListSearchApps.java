/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.dataprovider.webservices.models.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Apiv7ListSearchApps extends Apiv7 {

	public boolean mature;
	public String query;
	@JsonProperty("store_names") public List<String> storeNames;
	@JsonProperty("stores_auth_map") public Map<String, List<String>> storeAuthMap;
	public boolean trusted;

}