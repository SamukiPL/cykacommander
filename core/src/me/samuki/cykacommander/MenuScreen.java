package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private Skin leaderboardSkin;
    private Skin achievementSkin;

    MenuScreen(CykaGame game){
        this.game = game;
    }

    @Override
    public void show() {
        //SET ADS
        game.share.loadInterstitialAd();
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

        //GOOGLE GAME SERVICES
        leaderboardSkin = new Skin();
        leaderboardSkin.add("leaderboard_up", new Texture("leaderboard_button_0.png"));
        leaderboardSkin.add("leaderboard_down", new Texture("leaderboard_button_1.png"));

        final Button leaderboardButton = new Button(leaderboardSkin.getDrawable("leaderboard_up"), leaderboardSkin.getDrawable("leaderboard_down"));
        leaderboardButton.setBounds(0, 0, 70, 98);
        leaderboardButton.setPosition(150, 100);
        stage.addActor(leaderboardButton);

        achievementSkin = new Skin();
        achievementSkin.add("achievement_up", new Texture("achievements_button_0.png"));
        achievementSkin.add("achievement_down", new Texture("achievements_button_1.png"));

        final Button achievementButton = new Button(achievementSkin.getDrawable("achievement_up"), achievementSkin.getDrawable("achievement_down"));
        achievementButton.setBounds(0, 0, 70, 98);
        achievementButton.setPosition(viewport.getWorldWidth()-(150+70), 100);
        stage.addActor(achievementButton);

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
        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                CykaGame.playServices.showScore();
            }
        });
        achievementButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                CykaGame.playServices.showAchievement();
            }
        });

        if(!CykaGame.prefs.getBoolean("gender_is_set", false)) {
            //GENDER
            Skin uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

            Label label = new Label("", uiSkin);

            Skin genderSkin = new Skin();
            genderSkin.add("text", new Texture("please_set_your_gender.png"));
            genderSkin.add("male_up", new Texture("male_0.png"));
            genderSkin.add("male_down", new Texture("male_1.png"));
            genderSkin.add("female_up", new Texture("female_0.png"));
            genderSkin.add("female_down", new Texture("female_1.png"));
            genderSkin.add("apache_up", new Texture("apache_helicopter_0.png"));
            genderSkin.add("apache_down", new Texture("apache_helicopter_1.png"));

            Button button = new Button(genderSkin.getDrawable("text"));
            button.setBounds(0, 0, 600, 45);
            button.setPosition(25, 40);

            final Dialog dialog = new Dialog("", uiSkin, "dialog");

            dialog.padTop(50).setHeight(120f);
            dialog.getContentTable().add(label).width(viewport.getWorldWidth()).height(100f).row();
            dialog.getContentTable().addActor(button);
            dialog.getButtonTable().padTop(25).padBottom(300).setHeight(120f);
            dialog.getButtonTable().row();



            Button buttonM = new Button(genderSkin.getDrawable("male_up"), genderSkin.getDrawable("male_down"));
            buttonM.setBounds(0, 0, 162, 84);
            buttonM.setPosition(-285, 250);

            buttonM.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.share.genderResult();
                    dialog.hide();
                }
            });

            dialog.getButtonTable().addActor(buttonM);
            dialog.getButtonTable().row();

            Button buttonF = new Button(genderSkin.getDrawable("female_up"), genderSkin.getDrawable("female_down"));
            buttonF.setBounds(0, 0, 222, 84);
            buttonF.setPosition(-285, 150);

            buttonF.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.share.genderResult();
                    dialog.hide();
                }
            });

            dialog.getButtonTable().addActor(buttonF);
            dialog.getButtonTable().row();

            Button buttonA = new Button(genderSkin.getDrawable("apache_up"), genderSkin.getDrawable("apache_down"));
            buttonA.setBounds(0, 0, 570, 84);
            buttonA.setPosition(-285, 50);

            buttonA.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.share.genderResult();
                    dialog.hide();
                }
            });

            dialog.getButtonTable().addActor(buttonA);
            dialog.getButtonTable().row();

            dialog.invalidateHierarchy();
            dialog.invalidate();
            dialog.layout();
            dialog.show(stage);

            CykaGame.prefs.putBoolean("gender_is_set", true);
            CykaGame.prefs.flush();
        }
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
        leaderboardSkin.dispose();
        achievementSkin.dispose();
    }
}
