package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.ai.pfa.Connection;

public class PathConnection implements Connection<PathPoint> {
	private PathPoint start, end;
	private float cost;
	
	public PathConnection(PathPoint start, PathPoint end) {
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
