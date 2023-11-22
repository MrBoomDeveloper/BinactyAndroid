package com.mrboomdev.platformer.entity.character;

import androidx.annotation.NonNull;

import com.mrboomdev.platformer.entity.Entity;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.Nullable;

public class CharacterProgrammable extends CharacterEntity {
	@Json(ignore = true)
	private CharacterBrain brain;
	private boolean isDashEnabledForce;

	public CharacterProgrammable(@NonNull CharacterEntity parent) {
		super(parent);
	}

	public CharacterProgrammable(CharacterSkin skin, CharacterBody worldBody, @NonNull Entity.Stats stats) {
		super(skin, worldBody, stats);
	}

	public <T extends CharacterBrain> T setBrain(@Nullable T brain) {
		if(brain != null) brain.setEntity(this);

		this.brain = brain;
		return brain;
	}

	public void setDashEnabledForce(boolean isEnabled) {
		this.isDashEnabledForce = isEnabled;
	}

	@Override
	public void update() {
		super.update();

		if(isDestroyed) return;

		if(brain != null) {
			brain.update();
		}
	}

	@Override
	protected boolean canDash() {
		return isDashEnabledForce || super.canDash();
	}

	@Override
	protected boolean shouldStopSliding() {
		return super.shouldStopSliding() && brain == null;
	}
}