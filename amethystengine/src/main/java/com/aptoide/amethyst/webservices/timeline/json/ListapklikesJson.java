package com.aptoide.amethyst.webservices.timeline.json;

import com.aptoide.dataprovider.webservices.json.GenericResponseV2;

import java.util.ArrayList;


/**
 * Created by fabio on 14-10-2015.
 */
public class ListapklikesJson extends GenericResponseV2 {
    public ArrayList<Friend> getUsersapks_likes() {
        return usersapks_likes;
    }
    ArrayList<Friend> usersapks_likes;
}
