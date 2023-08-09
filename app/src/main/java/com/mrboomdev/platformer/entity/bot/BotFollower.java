package com.mrboomdev.platformer.entity.bot;

import com.mrboomdev.platformer.entity.character.CharacterBrain;
public class BotFollower extends CharacterBrain {
	private Runnable completionCallback, failureCallback;
	private String[] waypoints;
	private float x, y;

	@Override
	public void update() {

	}

	public void goTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	public void onCompleted(Runnable callback) {
		this.completionCallback = callback;
	}

	public void onFailed(Runnable callback) {
		this.failureCallback = callback;
	}

	public void start() {

	}
}