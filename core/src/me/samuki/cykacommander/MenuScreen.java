package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

class MenuScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    private FitViewport viewport;
    //LOGO
    private Texture logo;
    //BUTTONS SKINS
    private Skin playSkin;
    private Skin shopSkin;
    private Skin settingsSkin;

    MenuScreen(CykaGame game){
        this.game = game;
    }

    @Override
    public void show() {
        //VIEWPORT
        viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //LOGO
        logo = new Texture("cykalogo.png");
        //PLAY
        playSkin = new Skin();
        playSkin.add("play", new Texture("play_button.png"));

        final ImageButton playButton = new ImageButton(playSkin.getDrawable("play"), playSkin.getDrawable("play"));
        playButton.setPosition(viewport.getWorldWidth()/2-playButton.getWidth()/2,500);
        stage.addActor(playButton);
        //SHOP
        shopSkin = new Skin();
        shopSkin.add("shop", new Texture("shop_button.png"));

        final ImageButton shopButton = new ImageButton(shopSkin.getDrawable("shop"), shopSkin.getDrawable("shop"));
        shopButton.setPosition(viewport.getWorldWidth()/2-shopButton.getWidth()/2, 400);
        stage.addActor(shopButton);
        //SETTINGS
        settingsSkin = new Skin();
        settingsSkin.add("settings", new Texture("settings_button.png"));

        final ImageButton settingsButton = new ImageButton(settingsSkin.getDrawable("settings"), settingsSkin.getDrawable("settings"));
        settingsButton.setPosition(viewport.getWorldWidth()/2-settingsButton.getWidth()/2, 300);
        stage.addActor(settingsButton);

        //BUTTONS INPUT
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ShopScreen(game));
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.1f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(logo, (viewport.getWorldWidth()/2)-(logo.getWidth()/2), 800);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        playSkin.dispose();
        shopSkin.dispose();
        settingsSkin.dispose();
        settingsSkin.dispose();
    }
}
