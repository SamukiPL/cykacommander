package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;


class SettingsScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    private FitViewport viewport;
    //SOUNDS
    private SoundsBase sounds;
    private float volume;
    //BACKGROUND
    private Texture background;
    //SETTINGS VALUES
    private boolean soundOn;
    private boolean controlsOn;
    private int setControl;
    //OPTIONS TEXT
    private Texture soundText;
    private Texture controlsText;
    private Animation whichControlFrame;
    private TextureRegion whichControl;

    SettingsScreen(CykaGame game) {
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
        background = new Texture("settings_screen/background.png");
        //SETTINGS VALUES
        soundOn = CykaGame.prefs.getBoolean("sound", true);
        controlsOn = CykaGame.prefs.getBoolean("controls_on", true);
        setControl = CykaGame.prefs.getInteger("which_control", 0);
        //ON/OFF
        Skin onOffSkin = new Skin();
        onOffSkin.add("on", new Texture("on_button.png"));
        onOffSkin.add("off", new Texture("off_button.png"));

        final Button buttonSound = new Button(onOffSkin.getDrawable("off"), onOffSkin.getDrawable("off"), onOffSkin.getDrawable("on"));
        buttonSound.setBounds(0, 0, 96, 96);
        buttonSound.setChecked(soundOn);
        buttonSound.setPosition(450, 700);
        stage.addActor(buttonSound);

        final Button buttonControls = new Button(onOffSkin.getDrawable("off"), onOffSkin.getDrawable("off"), onOffSkin.getDrawable("on"));
        buttonControls.setBounds(0, 0, 96, 96);
        buttonControls.setChecked(controlsOn);
        buttonControls.setPosition(450, 550);
        stage.addActor(buttonControls);
        //ARROWS
        Animation arrowsFrames = GameBasic.spriteCutting("settings_screen/next_back.png", 2, 2);
        Skin arrowsSkin = new Skin();
        arrowsSkin.add("left_down", arrowsFrames.getKeyFrame(0));
        arrowsSkin.add("left_up", arrowsFrames.getKeyFrame(1));
        arrowsSkin.add("right_down", arrowsFrames.getKeyFrame(2));
        arrowsSkin.add("right_up", arrowsFrames.getKeyFrame(3));

        final Button leftArrow = new Button(arrowsSkin.getDrawable("left_up"), arrowsSkin.getDrawable("left_down"));
        leftArrow.setBounds(0, 0, 54, 96);
        leftArrow.setPosition(50, 400);
        stage.addActor(leftArrow);

        final Button rightArrow = new Button(arrowsSkin.getDrawable("right_up"), arrowsSkin.getDrawable("right_down"));
        rightArrow.setBounds(0, 0, 54, 96);
        rightArrow.setPosition(536, 400);
        stage.addActor(rightArrow);
        //BACK
        Skin backSkin = new Skin();
        backSkin.add("back_up", new Texture("back_button_0.png"));
        backSkin.add("back_down", new Texture("back_button_1.png"));

        final Button backButton = new Button(backSkin.getDrawable("back_up"), backSkin.getDrawable("back_down"));
        backButton.setBounds(0, 0, 270, 126);
        backButton.setPosition(10, viewport.getWorldHeight()-backButton.getHeight()-10);
        stage.addActor(backButton);
        //OPTIONS TEXT
        soundText = new Texture("settings_screen/sound_text.png");
        controlsText = new Texture("settings_screen/controls_text.png");
        whichControlFrame = GameBasic.spriteCutting("settings_screen/which_control.png", 1, 3);
        whichControl = whichControlFrame.getKeyFrame(setControl);

        buttonSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                CykaGame.prefs.putBoolean("sound", !soundOn);
                CykaGame.prefs.flush();
                soundOn = CykaGame.prefs.getBoolean("sound");
            }
        });
        buttonControls.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                CykaGame.prefs.putBoolean("controls_on", !controlsOn);
                CykaGame.prefs.flush();
                controlsOn = CykaGame.prefs.getBoolean("controls_on");
            }
        });
        leftArrow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                switch (setControl) {
                    case 0:
                        setControl = 2;
                        break;
                    case 1:
                        setControl = 0;
                        break;
                    case 2:
                        setControl = 1;
                        break;
                }
                whichControl = whichControlFrame.getKeyFrame(setControl);
                CykaGame.prefs.putInteger("which_control", setControl);
                CykaGame.prefs.flush();
            }
        });
        rightArrow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                switch (setControl) {
                    case 0:
                        setControl = 1;
                        break;
                    case 1:
                        setControl = 2;
                        break;
                    case 2:
                        setControl = 0;
                        break;
                }
                whichControl = whichControlFrame.getKeyFrame(setControl);
                CykaGame.prefs.putInteger("which_control", setControl);
                CykaGame.prefs.flush();
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.845f, 0.845f, 0.845f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        game.batch.draw(soundText, 100, 700, 310, 90);
        game.batch.draw(controlsText, 50, 559, 384, 72);
        game.batch.draw(whichControl, 117.5f, 404, 405, 81);
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
