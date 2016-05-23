package com.aptoide.amethyst.social;

import com.aptoide.amethyst.webservices.timeline.json.Friend;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Friends of logged user also users of Aptoide.
 */
public class AptoideFriends {

	private List<Friend> inactiveFriends;

	private List<Friend> activeFriends;

	@JsonCreator
	public AptoideFriends(@JsonProperty("timeline_inactive") List<Friend> inactiveFriends,
						  @JsonProperty("timeline_active") List<Friend> activeFriends) {
		this.inactiveFriends = inactiveFriends;
		this.activeFriends = activeFriends;
	}

	public List<Friend> getInactiveFriends() {
		return inactiveFriends;
	}

	public List<Friend> getActiveFriends() {
		return activeFriends;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AptoideFriends that = (AptoideFriends) o;

		if (!inactiveFriends.equals(that.inactiveFriends)) return false;
		return activeFriends.equals(that.activeFriends);
	}

	@Override
	public int hashCode() {
		int result = inactiveFriends.hashCode();
		result = 31 * result + activeFriends.hashCode();
		return result;
	}
}
