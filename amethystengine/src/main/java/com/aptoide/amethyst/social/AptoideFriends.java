package com.aptoide.amethyst.social;

import com.aptoide.amethyst.webservices.timeline.json.Friend;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Friends of logged user also users of Aptoide.
 */
public class AptoideFriends {

	private ArrayList<Friend> inactiveFriends;

	private ArrayList<Friend> activeFriends;

	@JsonCreator
	public AptoideFriends(@JsonProperty("timeline_inactive") ArrayList<Friend> inactiveFriends, @JsonProperty("timeline_active") ArrayList<Friend> activeFriends) {
		this.inactiveFriends = inactiveFriends;
		this.activeFriends = activeFriends;
	}

	public ArrayList<Friend> getInactiveFriends() {
		return inactiveFriends;
	}

	public ArrayList<Friend> getActiveFriends() {
		return activeFriends;
	}
}
