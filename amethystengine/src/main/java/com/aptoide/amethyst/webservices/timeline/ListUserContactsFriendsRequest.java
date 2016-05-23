/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 13/05/2016.
 */

package com.aptoide.amethyst.webservices.timeline;

import com.aptoide.amethyst.social.SimpleContact;
import com.aptoide.amethyst.webservices.OauthErrorHandler;
import com.aptoide.amethyst.webservices.timeline.json.ListUserFriendsJson;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class ListUserContactsFriendsRequest extends RetrofitSpiceRequest<ListUserFriendsJson, ListUserContactsFriendsRequest.ListUserFriends> {

    private final List<SimpleContact> contacts;
    private final int limit;
    private final int offset;

    interface ListUserFriends {
        @POST("/webservices/3/listUserContactsFriends")
        @FormUrlEncoded
        ListUserFriendsJson run(@FieldMap HashMap<String, String> args, @Field("email")
                                List<String> emails, @Field("phone") List<String> phones);
    }

    public ListUserContactsFriendsRequest(List<SimpleContact> contacts, int limit, int offset) {
        super(ListUserFriendsJson.class, ListUserFriends.class);
        this.contacts = contacts;
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public ListUserFriendsJson loadDataFromNetwork() throws Exception {
        HashMap<String, String > parameters = new HashMap<String, String>();
        parameters.put("mode" , "json");
        parameters.put("limit", String.valueOf(limit));
        parameters.put("offset", String.valueOf(offset));

        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        for (SimpleContact contact: contacts) {
            emails.addAll(contact.getEmails());
            phones.addAll(contact.getPhoneNumbers());
        }

        try{
            return getService().run(parameters, emails, phones);
        }catch (RetrofitError e){
            OauthErrorHandler.handle(e);
        }

        return null;
    }
}

