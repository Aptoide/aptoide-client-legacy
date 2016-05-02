package com.aptoide.amethyst.webservices.timeline.json;

import com.aptoide.amethyst.social.AptoideFriends;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by fabio on 14-10-2015.
 */
public class ListUserFriendsJson extends GenericResponseV2 {

	private AptoideFriends aptoideFriends;

	@JsonCreator
	public ListUserFriendsJson(@JsonProperty("userfriends") AptoideFriends aptoideFriends) {
		this.aptoideFriends = aptoideFriends;
	}

	public ArrayList<Friend> getInactiveFriends() {
		if (aptoideFriends != null) {
			return aptoideFriends.getInactiveFriends();
		} else {
			return new ArrayList<>();
		}
	}

	public ArrayList<Friend> getActiveFriends() {
		if (aptoideFriends != null) {
			return aptoideFriends.getActiveFriends();
		} else {
			return new ArrayList<>();
		}
	}
}
