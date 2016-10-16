package me.samuki.cykacommander;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CykaGame extends Game {
	public static final int SCREEN_WIDTH = 640;
	public static final int SCREEN_HEIGHT = 1024;

	public SpriteBatch batch;
	public OrthographicCamera camera;
	public Preferences prefs;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		prefs = Gdx.app.getPreferences("game-prefs");

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
