package com.mrboomdev.platformer.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ActorUtil {
	public static void test() {
		Actor actor = new Actor();
		ActorUtil.addListener((InputEvent event, float x, float y) -> {
			System.out.println(x + " " + y);
		}, actor);
	}
	
	public static void addListener(EventListener listener, Actor actor) {
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.run(event, x, y);
				System.out.println(event);
			}
		});
	}
	
	public enum EventType {
		UP,
        DOWN,
        MOVE,
        HOLD,
        AMOGUS
	}
	
	public interface EventListener {
		public void run(InputEvent event, float x, float y);
	}
}