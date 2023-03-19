package com.mrboomdev.platformer.environment.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.mrboomdev.platformer.environment.path.PathHeuristic;
import java.util.Comparator;

public class PathGraph implements IndexedGraph<PathPoint> {
	public Array<PathPoint> points = new Array<>();
	public Array<PathConnection> connections = new Array<>();
	private PathHeuristic heuristic = new PathHeuristic();
	private ObjectMap<PathPoint, Array<Connection<PathPoint>>> connectionsMap = new ObjectMap<>();
	
	public void addPoint(PathPoint point) {
		points.add(point);
		point.index = points.size - 1;
	}
	
	public void connectPoints(PathPoint from, PathPoint to) {
		var connection = new PathConnection(from, to);
		if(!connectionsMap.containsKey(from))
			connectionsMap.put(from, new Array<Connection<PathPoint>>());
		connectionsMap.get(from).add(connection);
		connections.add(connection);
	}
	
	public GraphPath<PathPoint> findPath(PathPoint from, PathPoint to) {
		GraphPath<PathPoint> graph = new DefaultGraphPath<>();
		new IndexedAStarPathFinder<>(this).searchNodePath(from, to, heuristic, graph);
		return graph;
	}
	
	public PathPoint findNearest(Vector2 position) {
		var positions = new Array<PathPoint>(points);
		var nearest = positions.selectRanked(new Comparator<PathPoint>() {
			@Override
			public int compare(PathPoint a, PathPoint b) {
				return (a.position.dst(position) > b.position.dst(position)) ? 1 : -1;
			}
		}, 1);
		return nearest;
	}

    @Override
    public Array<Connection<PathPoint>> getConnections(PathPoint from) {
		if(!connectionsMap.containsKey(from)) return new Array<>(0);
		return connectionsMap.get(from);
	}

    @Override
    public int getIndex(PathPoint point) {
		return point.index;
	}

    @Override
    public int getNodeCount() {
		return points.size;
	}
}