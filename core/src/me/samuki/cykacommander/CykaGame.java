package me.samuki.cykacommander;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class CykaGame extends Game implements ApplicationListener {
	static final int SCREEN_WIDTH = 640;
	static final int SCREEN_HEIGHT = 1024;

	final ShareAction share;
	static PlayServices playServices;

	SpriteBatch batch;
	OrthographicCamera camera;
	static Preferences prefs;

	CykaGame(ShareAction share, PlayServices playServices) {
		this.share = share;
		CykaGame.playServices = playServices;
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		prefs = Gdx.app.getPreferences("game-prefs");
		GameBasic.adIsReady = false;

		this.setScreen(new CykaSplashScreen(this));
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
