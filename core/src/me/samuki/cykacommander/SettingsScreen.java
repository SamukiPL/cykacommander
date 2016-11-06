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


class SettingsScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    //SETTINGS VALUES
    private boolean soundOn;
    private boolean controlsOn;
    //OPTIONS TEXT
    private Texture soundText;

    SettingsScreen(CykaGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        //VIEWPORT
        FitViewport viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //SETTINGS VALUES
        soundOn = CykaGame.prefs.getBoolean("sound", true);
        controlsOn = CykaGame.prefs.getBoolean("controls_on", true);
        //ON/OFF
        Skin onOffSkin = new Skin();
        onOffSkin.add("on", new Texture("on_button.png"));
        onOffSkin.add("off", new Texture("off_button.png"));

        final ImageButton buttonSound = new ImageButton(onOffSkin.getDrawable("off"), onOffSkin.getDrawable("off"), onOffSkin.getDrawable("on"));
        buttonSound.setChecked(soundOn);
        buttonSound.setPosition(450, 800);
        stage.addActor(buttonSound);

        final ImageButton buttonControls = new ImageButton(onOffSkin.getDrawable("off"), onOffSkin.getDrawable("off"), onOffSkin.getDrawable("on"));
        buttonControls.setChecked(controlsOn);
        buttonControls.setPosition(450, 650);
        stage.addActor(buttonControls);
        //BACK
        Skin backSkin = new Skin();
        backSkin.add("back", new Texture("back_button.png"));

        final ImageButton backButton = new ImageButton(backSkin.getDrawable("back"), backSkin.getDrawable("back"));
        backButton.setPosition(10, viewport.getWorldHeight()-backButton.getHeight()-10);
        stage.addActor(backButton);
        //OPTIONS TEXT
        soundText = new Texture("sound_text.png");

        buttonSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CykaGame.prefs.putBoolean("sound", !soundOn);
                CykaGame.prefs.flush();
                soundOn = CykaGame.prefs.getBoolean("sound");
            }
        });
        buttonControls.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CykaGame.prefs.putBoolean("controls_on", !controlsOn);
                CykaGame.prefs.flush();
                controlsOn = CykaGame.prefs.getBoolean("controls_on");
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.1f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(soundText, 100, 800);
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
        soundText.dispose();
    }

}
