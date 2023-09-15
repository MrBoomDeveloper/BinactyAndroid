package com.mrboomdev.platformer.environment.path.presets;

import androidx.annotation.NonNull;

import com.badlogic.gdx.utils.Array;
import com.mrboomdev.platformer.environment.map.MapTile;
import com.mrboomdev.platformer.environment.path.PathGraph;
import com.mrboomdev.platformer.environment.path.PathPoint;

import java.util.Arrays;

public class MapScanner {

	@NonNull
	public static PathGraph getGraphByWaypoints(@NonNull Iterable<MapTile> tiles, String[] waypoints) {
		return getGraph(tiles, tile -> {
			var stream = Arrays.stream(waypoints);
			return stream.anyMatch(item -> tile.name.equals(item));
		});
	}

	@NonNull
	public static PathGraph getGraph(@NonNull Iterable<MapTile> tiles, IsPointOkChecker checker) {
		var graph = new PathGraph();
		var points = new Array<PathPoint>();

		for(var tile : tiles) {
			if(!checker.isOk(tile)) continue;

			var point = new PathPoint(tile.getCachedPosition());
			graph.addPoint(point);
			points.add(point);
		}

		for(int i = 0; i < points.size; i++) {
			for(int a = 0; a < points.size; a++) {
				if(points.get(i).position.dst(points.get(a).position) > 2.5f) continue;
				graph.connectPoints(points.get(i), points.get(a));
			}
		}

		return graph;
	}

	public interface IsPointOkChecker {
		boolean isOk(MapTile tile);
	}
}