package com.mrboomdev.binacty.online;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class OnlineProfile {
	public String name, id;
	private boolean isEditable;

	public OnlineProfile(String id, String name) {
		this.name = name;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public boolean isEditable() {
		return isEditable;
	}

	@NonNull
	@Contract(pure = true)
	public static OnlineProfile getGuestProfile() {
		var profile = new OnlineProfile("guest_" + String.valueOf(Math.random()).substring(2), "Guest");
		profile.setEditable(false);

		return profile;
	}
}