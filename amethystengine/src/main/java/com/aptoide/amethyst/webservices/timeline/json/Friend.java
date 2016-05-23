package com.aptoide.amethyst.webservices.timeline.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Friend {

	private String username;
	private String avatar;
	private String email;

	@JsonCreator
	public Friend(@JsonProperty("username") String username, @JsonProperty("avatar") String avatar, @JsonProperty("email") String email) {
		this.username = username;
		this.avatar = avatar;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Friend friend = (Friend) o;

		if (!username.equals(friend.username)) return false;
		if (!avatar.equals(friend.avatar)) return false;
		return email.equals(friend.email);
	}

	@Override
	public int hashCode() {
		int result = username.hashCode();
		result = 31 * result + avatar.hashCode();
		result = 31 * result + email.hashCode();
		return result;
	}
}
