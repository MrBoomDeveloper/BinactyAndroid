package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;

public class Path extends DefaultGraphPath<PathPoint> {
	private float cachedCost = -1;

	@Override
	public void clear() {
		super.clear();
		cachedCost = -1;
	}

	@Override
	public void add(PathPoint node) {
		super.add(node);
		cachedCost = -1;
	}

	public float getTotalCost() {
		if(cachedCost != -1) return cachedCost;
		if(nodes.isEmpty()) return 0;

		cachedCost = 0;
		PathPoint previousPoint = null;

		for(var position : this) {
			if(previousPoint != null) {
				cachedCost += previousPoint.getPosition().dst(position.getPosition());
			}

			previousPoint = position;
		}

		return cachedCost;
	}
}