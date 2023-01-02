package com.mrboomdev.platformer.environment;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import com.mrboomdev.platformer.environment.FreePosition;

public class GoalHeuristic implements Heuristic<FreePosition> {

    @Override
    public float estimate(FreePosition fromPosition, FreePosition toPosition) {
        return Vector2.dst(fromPosition.x, fromPosition.y, toPosition.x, toPosition.y);
    }
}