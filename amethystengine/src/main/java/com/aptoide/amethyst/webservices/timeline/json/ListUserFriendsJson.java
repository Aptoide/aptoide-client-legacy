package com.aptoide.amethyst.webservices.timeline.json;

import com.aptoide.dataprovider.webservices.json.GenericResponseV2;

import java.util.ArrayList;

/**
 * Created by fabio on 14-10-2015.
 */
public class ListUserFriendsJson extends GenericResponseV2 {
    public ArrayList<Friend> getInactiveFriends() {
        if(userfriends!=null){
            return userfriends.timeline_inactive;
        }else{
            return new ArrayList<Friend>();
        }
    }
    public ArrayList<Friend> getActiveFriends() {
        if(userfriends!=null){
            return userfriends.timeline_active;
        }else{
            return new ArrayList<Friend>();
        }
    }

    Friends userfriends;

    public static class Friends{
        ArrayList<Friend> timeline_inactive;

        ArrayList<Friend> timeline_active;
    }
}
