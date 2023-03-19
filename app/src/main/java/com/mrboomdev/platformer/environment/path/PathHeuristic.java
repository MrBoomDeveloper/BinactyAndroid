package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class PathHeuristic implements Heuristic<PathPoint> {

    @Override
    public float estimate(PathPoint from, PathPoint to) {
		return from.position.dst(to.position);
	}
}