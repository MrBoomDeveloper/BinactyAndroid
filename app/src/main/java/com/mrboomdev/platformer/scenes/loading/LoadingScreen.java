package com.mrboomdev.platformer.scenes.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.mrboomdev.platformer.environment.EnvironmentCreator;
import com.mrboomdev.platformer.environment.EnvironmentManager;
import com.mrboomdev.platformer.game.GameHolder;
import com.mrboomdev.platformer.scenes.core.CoreScreen;
import com.mrboomdev.platformer.scenes.gameplay.GameplayScreen;
import com.mrboomdev.platformer.util.FileUtil;
import static com.mrboomdev.platformer.scenes.loading.LoadingScreen.LoadStep.*;

public class LoadingScreen extends CoreScreen {
    private GameHolder game = GameHolder.getInstance();
    private LoadScene loadScene;
    private AssetManager asset;
    private Sprite banner;
    private SpriteBatch batch;
	private BitmapFont font;
	private EnvironmentCreator environmentCreator;
	private EnvironmentManager environment;
	private LoadStep loadStep = PREPAIRING;

    public LoadingScreen(LoadScene scene) {
        this.loadScene = scene;
        this.asset = game.assets;
        this.batch = new SpriteBatch();
        this.banner = new Sprite(asset.get("ui/banner/loading.jpg", Texture.class));
        this.banner.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.banner.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.font = asset.get("loading.ttf", BitmapFont.class);
    }

    @Override
    public void show() {
        switch(loadScene) {
            case LOBBY:
				loadOtherResources();
                game.launcher.exit();
                break;
			
            case GAMEPLAY:
				environmentCreator = new EnvironmentCreator()
					.setGamemode(new FileUtil("world/packs/fnaf/gamemode.json", FileUtil.Source.INTERNAL))
					.setMap(new FileUtil("world/packs/fnaf/maps/fnafMap1.json", FileUtil.Source.INTERNAL))
					.onCreate(manager -> {
						this.environment = manager;
						loadOtherResources();
					}).create();
					loadStep = MAP;
                break;
        }
    }
	
	private void loadOtherResources() {
		Gson gson = new Gson();
		LoadingFiles files = gson.fromJson(Gdx.files.internal("etc/loadFiles.json").readString(), LoadingFiles.class);
		files.loadToManager(asset, loadScene.name());
		this.loadStep = RESOURCES;
	}

    @Override
    public void render(float delta) {
        batch.begin();
		{
			banner.draw(batch);
			font.draw(batch, getStatus(), 50, 75);
		}
        batch.end();
		
		if(asset.update(17) && loadStep == RESOURCES) {
            switch(loadScene) {
                case GAMEPLAY:
                    game.setScreen(new GameplayScreen(environment));
                    break;
            }
        }
    }
	
	private String getStatus() {
		switch(loadStep) {
			case MAP:
				return environmentCreator.getStatus();
			case RESOURCES:
				return "Loading resources...";
			default:
				return "Prepairing...";
		}
	}
    
    @Override
    public void dispose() {
        batch.dispose();
    }
	
	public enum LoadScene {
        LOBBY,
        GAMEPLAY
    }
	
	public enum LoadStep {
		PREPAIRING,
		MAP,
		RESOURCES
	}
}