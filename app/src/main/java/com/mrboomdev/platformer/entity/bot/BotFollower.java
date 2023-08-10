package com.mrboomdev.platformer.entity.bot;

import com.mrboomdev.platformer.entity.character.CharacterBrain;
import com.mrboomdev.platformer.game.GameHolder;

public class BotFollower extends CharacterBrain {
	private final GameHolder game = GameHolder.getInstance();
	private Runnable completionCallback, failureCallback;
	private String[] waypoints;
	private float x, y;

	// START

	/*public GraphPath<PathPoint> path;
	public PathGraph graph;
	public BotTarget target;
	public AiStuckChecker stuckChecker;
	protected BotBrain.Responder responder;
	protected int refreshRate;
	private AiTargeter targeter;
	private long mapLastScanned;

	public BotBrain start() {
		this.stuckChecker = new AiStuckChecker();
		this.targeter = new AiTargeter(this);
		this.scanMap();
	}

	public void scanMap() {
		var startedScanningMapMs = System.currentTimeMillis();

		this.graph = new PathGraph();
		var points = new Array<PathPoint>();
		for(var tile : game.environment.map.tilesMap.values()) {
			if(!new Array<>(waypoints).contains(tile.name, false)) continue;

			var point = new PathPoint(tile.getCachedPosition());
			this.graph.addPoint(point);
			points.add(point);
		}

		for(int i = 0; i < points.size; i++) {
			for(int a = 0; a < points.size; a++) {
				if(points.get(i).position.dst(points.get(a).position) > 2.5f) continue;
				this.graph.connectPoints(points.get(i), points.get(a));
			}
		}

		LogUtil.debug(LogUtil.Tag.BOT, "Map scanned for: " + (System.currentTimeMillis() - startedScanningMapMs) + "ms");
	}

	@Override
	public void update() {
		long currentTime = System.currentTimeMillis();
		if((refreshRate != 0) && (currentTime > mapLastScanned + refreshRate * 1000f)) {
			mapLastScanned = currentTime;
			scanMap();
		}

		targeter.update();
	}

	public void goByPath(float speed) {
		if(entity == null || target == null) return;
		boolean shouldGoAway = false;
		var myPosition = entity.getPosition();
		var targetPosition = target.getPosition();

		if(path.getCount() > 1) {
			entity.usePower(path.get(1).position.cpy().sub(myPosition).scl(25).scl(1), speed, true);
		} else if(target instanceof CharacterEntity) {
			entity.usePower(myPosition.cpy().sub(myPosition).scl(1), speed, true);
		}

		if(stuckChecker.isStuck(myPosition) && myPosition.dst(targetPosition) > 1.25f) {
			stuckChecker.reset();
			targeter.setIgnored(target);
			targeter.exploreTimeoutProgress = 0;
		}
	}*/

	//END

	public void setTarget(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setWaypoints(String[] waypoints) {
		this.waypoints = waypoints;
	}

	public void onCompleted(Runnable callback) {
		this.completionCallback = callback;
	}

	public void onFailed(Runnable callback) {
		this.failureCallback = callback;
	}
}