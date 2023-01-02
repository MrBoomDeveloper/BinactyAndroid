package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.ObjectMap;
import com.mrboomdev.platformer.environment.FreePosition;
import com.mrboomdev.platformer.environment.GoalHeuristic;
import com.mrboomdev.platformer.environment.PositionGraph;
import com.mrboomdev.platformer.environment.TravelPath;

public class PositionGraph implements IndexedGraph<FreePosition> {
    private GoalHeuristic heurtistic = new GoalHeuristic();
    public Array<FreePosition> positions = new Array<>();
    public Array<TravelPath> paths = new Array<>();
    public ObjectMap<FreePosition, Array<Connection<FreePosition>>> positionsMap = new ObjectMap<>();
    private int lastIndex = 0;
    
    public void addPosition(FreePosition position) {
        position.index = lastIndex;
        lastIndex++;
        positions.add(position);
    }
    
    public void connectPositions(FreePosition fromPosition, FreePosition toPosition) {
        TravelPath path = new TravelPath(fromPosition, toPosition);
        if(!positionsMap.containsKey(fromPosition)) {
            positionsMap.put(fromPosition, new Array<Connection<FreePosition>>());
        }
        positionsMap.get(fromPosition).add(path);
        paths.add(path);
    }
    
    public GraphPath<FreePosition> findPath(FreePosition start, FreePosition goal) {
        GraphPath<FreePosition> graph = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(start, goal, heurtistic, graph);
        return graph;
    }

    @Override
    public Array<Connection<FreePosition>> getConnections(FreePosition position) {
        if(positionsMap.containsKey(position)) {
            return positionsMap.get(position);
        }
        return new Array<>();
    }

    @Override
    public int getIndex(FreePosition position) {
        return position.index;
    }

    @Override
    public int getNodeCount() {
        return lastIndex;
    }
}
