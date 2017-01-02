package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

class MenuScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    private FitViewport viewport;
    //SOUNDS
    private SoundsBase sounds;
    private float volume;
    //BACKGROUND
    private Texture background, vodka;
    private int vodkaX;
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
        //SOUNDS
        sounds = new SoundsBase(1);

        if(CykaGame.prefs.getBoolean("sound", true))
            volume = 1f;
        else
            volume = 0;
        //VIEWPORT
        viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //BACKGROUND
        background = new Texture("menu_background.png");
        vodka = new Texture("vodka.png");
        vodkaX = vodka.getWidth()/2;

        //LOGO
        logo = new Texture("cykalogo.png");
        //PLAY
        playSkin = new Skin();
        playSkin.add("play_up", new Texture("play_button_0.png"));
        playSkin.add("play_down", new Texture("play_button_1.png"));

        final Button playButton = new Button(playSkin.getDrawable("play_up"), playSkin.getDrawable("play_down"));
        playButton.setBounds(0, 0, 300, 140);
        playButton.setPosition(viewport.getWorldWidth()/2-playButton.getWidth()/2,550);
        stage.addActor(playButton);
        //SHOP
        shopSkin = new Skin();
        shopSkin.add("shop_up", new Texture("shop_button_0.png"));
        shopSkin.add("shop_down", new Texture("shop_button_1.png"));

        final Button shopButton = new Button(shopSkin.getDrawable("shop_up"), shopSkin.getDrawable("shop_down"));
        shopButton.setBounds(0, 0, 300, 140);
        shopButton.setPosition(viewport.getWorldWidth()/2-shopButton.getWidth()/2, 400);
        stage.addActor(shopButton);
        //SETTINGS
        settingsSkin = new Skin();
        settingsSkin.add("settings_up", new Texture("settings_button_0.png"));
        settingsSkin.add("settings_down", new Texture("settings_button_1.png"));

        final Button settingsButton = new Button(settingsSkin.getDrawable("settings_up"), settingsSkin.getDrawable("settings_down"));
        settingsButton.setBounds(0, 0, 480, 140);
        settingsButton.setPosition(viewport.getWorldWidth()/2-settingsButton.getWidth()/2, 250);
        stage.addActor(settingsButton);

        //BUTTONS INPUT
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.setScreen(new GameScreen(game));
            }
        });
        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.setScreen(new ShopScreen(game));
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.setScreen(new SettingsScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.845f, 0.845f, 0.845f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        game.batch.draw(vodka, viewport.getWorldWidth()/2-vodkaX, 32);
        game.batch.draw(logo, (viewport.getWorldWidth()/2)-(558/2), 750, 558, 198);
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
