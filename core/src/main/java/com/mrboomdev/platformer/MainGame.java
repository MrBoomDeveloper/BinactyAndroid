package com.mrboomdev.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.mrboomdev.platformer.scenes.splash.SplashScreen;
import com.mrboomdev.platformer.util.Analytics;

public class MainGame extends Game {
    private Analytics analytics;

    public MainGame(Analytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public void create() {
        analytics.logDebug("Start game", "MainGame.create()");
        setScreen(new SplashScreen(this));
    }

    @Override
    public void setScreen(Screen screen) {
        analytics.logDebug("setScreen", screen.getClass().getName());
        super.setScreen(screen);
    }
}
