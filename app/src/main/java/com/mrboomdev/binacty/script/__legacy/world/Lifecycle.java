package com.mrboomdev.binacty.script.__legacy.world;

import com.badlogic.gdx.graphics.Color;
import com.mrboomdev.platformer.game.GameHolder;

import box2dLight.ConeLight;

public class Lifecycle {

	public static void initNaturalLights() {
		var game = GameHolder.getInstance();

		var light = new ConeLight(
				game.environment.rayHandler,
				16,
				Color.WHITE,
				50,
				0, 0,
				0,
				50);

		light.setSoft(true);
		light.setSoftnessLength(10);

		light.setPosition(39, 54);
		light.setDistance(22);
		light.setConeDegree(17.5f);
		light.setDirection(-110);
		light.setColor(0.25f, 0.25f, 0.5f, .75f);
		light.setXray(true);
		light.setStaticLight(true);

		var stageLight = new ConeLight(
				game.environment.rayHandler,
				8,
				Color.WHITE,
				50,
				0, 0,
				0,
				50);

		stageLight.setColor(Color.WHITE);
		stageLight.setDistance(5);

		stageLight.setColor(0.25f, 0.25f, 0.5f, .5f);
		stageLight.setConeDegree(40);
		stageLight.setDistance(15);
		stageLight.setPosition(19, 27);
		stageLight.setDirection(-75);
	}
}