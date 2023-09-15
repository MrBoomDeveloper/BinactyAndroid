package com.mrboomdev.platformer.environment.path;

import androidx.annotation.NonNull;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PathGraph implements IndexedGraph<PathPoint> {
	public Array<PathPoint> points = new Array<>();
	public Array<PathConnection> connections = new Array<>();
	private final PathHeuristic heuristic = new PathHeuristic();
	private final ObjectMap<PathPoint, Array<Connection<PathPoint>>> connectionsMap = new ObjectMap<>();
	
	public void addPoint(PathPoint point) {
		points.add(point);
		point.index = points.size - 1;
	}
	
	public void connectPoints(PathPoint from, PathPoint to) {
		var connection = new PathConnection(from, to);
		if(!connectionsMap.containsKey(from))
			connectionsMap.put(from, new Array<>());

		connectionsMap.get(from).add(connection);
		connections.add(connection);
	}
	
	public Path findPath(PathPoint from, PathPoint to) {
		var path = new Path();
		new IndexedAStarPathFinder<>(this).searchNodePath(from, to, heuristic, path);
		return path;
	}
	
	public PathPoint findNearest(Vector2 position) {
		var positions = new Array<>(points);
		return positions.selectRanked((a, b) -> {
			if(a.equals(b)) return 0;
			return (a.position.dst(position) > b.position.dst(position)) ? 1 : -1;
		}, 1);
	}

    @Override
    public Array<Connection<PathPoint>> getConnections(PathPoint from) {
		if(!connectionsMap.containsKey(from)) return new Array<>(0);
		return connectionsMap.get(from);
	}

    @Override
    public int getIndex(@NonNull PathPoint point) {
		return point.index;
	}

    @Override
    public int getNodeCount() {
		return points.size;
	}
}