package com.mrboomdev.platformer.environment.path;

import androidx.annotation.NonNull;

import com.badlogic.gdx.ai.pfa.Connection;

public class PathConnection implements Connection<PathPoint> {
	private final PathPoint start, end;
	private final float cost;
	
	public PathConnection(@NonNull PathPoint start, @NonNull PathPoint end) {
		this.start = start;
		this.end = end;
		this.cost = start.position.dst(end.position);
	}

    @Override
    public float getCost() {
		return cost;
	}

    @Override
    public PathPoint getFromNode() {
		return start;
	}

    @Override
    public PathPoint getToNode() {
		return end;
	}
}