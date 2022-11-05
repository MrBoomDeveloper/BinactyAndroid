package com.mrboomdev.platformer.screens;

import com.badlogic.gdx.Game;
import com.mrboomdev.platformer.screens.GameplayScreen;

public class LoadingScreen extends Game {
    public LoadingScreen() {
        
    }
    
    @Override
    public void create() {
        setScreen(new GameplayScreen());
    }
}
