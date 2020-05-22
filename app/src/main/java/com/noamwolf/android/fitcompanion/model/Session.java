package com.noamwolf.android.fitcompanion.model;

import com.noamwolf.android.fitcompanion.model.Activity;

import java.util.List;

/**
 * Wrapper class for Google Fit "sessions" which are actually activities.
 */
public class Session {
	private List<Activity> session;
	
	public List<Activity> getSession() {
		return session;
	}

	public void setSession(List<Activity> session) {
		this.session = session;
	}
}
