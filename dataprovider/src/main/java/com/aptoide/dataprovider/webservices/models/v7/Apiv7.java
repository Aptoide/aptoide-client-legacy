/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.dataprovider.webservices.models.v7;

/**
 * Base request body for all API V7 requests.
 */
public class Apiv7 {

	public String access_token;
	public int aptoide_vercode;
	public String lang;
	public int limit;
	public int offset;
	public String q;
}