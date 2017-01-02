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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

class DeathScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    private FitViewport viewport;
    //SOUNDS
    private SoundsBase sounds;
    private float volume;
    //BACKGROUND
    private Texture background;
    private Animation numbersFrames;

    private final int points = GameScreen.points;
    private int bestScore = CykaGame.prefs.getInteger("best-score", 0);
    //NUMBERS
    private static TextureRegion hundredths;
    private static TextureRegion tens;
    private static TextureRegion ones;
    //BEST SCORE
    private TextureRegion bestHundredths;
    private TextureRegion bestTens;
    private TextureRegion bestOnes;
    private Texture bestScoreText;
    //COINS
    private Texture coin;

    DeathScreen(CykaGame game) {
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
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        //BACKGROUND
        background = new Texture("death_screen/background.png");
        //PLAY AGAIN
        Skin playAgainSkin = new Skin();
        playAgainSkin.add("play_again_up", new Texture("play_button_0.png"));
        playAgainSkin.add("play_again_down", new Texture("play_button_1.png"));

        final Button playAgainButton = new Button(playAgainSkin.getDrawable("play_again_up"), playAgainSkin.getDrawable("play_again_down"));
        playAgainButton.setBounds(0, 0, 300, 140);
        playAgainButton.setPosition(viewport.getWorldWidth()/2-playAgainButton.getWidth()/2,450);
        stage.addActor(playAgainButton);
        //MENU
        Skin menuSkin = new Skin();
        menuSkin.add("menu_up", new Texture("menu_button_0.png"));
        menuSkin.add("menu_down", new Texture("menu_button_1.png"));

        final Button menuButton = new Button(menuSkin.getDrawable("menu_up"), menuSkin.getDrawable("menu_down"));
        menuButton.setBounds(0, 0, 300, 140);
        menuButton.setPosition(viewport.getWorldWidth()/2-menuButton.getWidth()/2,300);
        stage.addActor(menuButton);
        //SHARE
        Skin shareSkin = new Skin();
        shareSkin.add("share_up", new Texture("share_button_0.png"));
        shareSkin.add("share_down", new Texture("share_button_1.png"));

        final Button shareButton = new Button(shareSkin.getDrawable("share_up"), shareSkin.getDrawable("share_down"));
        shareButton.setBounds(0, 0, 340, 140);
        shareButton.setPosition(viewport.getWorldWidth()/2-shareButton.getWidth()/2,150);
        stage.addActor(shareButton);
        //PREFERENCES / SCORE
        numbersFrames = GameBasic.spriteCutting("death_screen/numbers_death.png", 5, 2);
        //CONTROLS OFF
        System.out.println(CykaGame.prefs.getInteger("plays_counter", 0));
        if(CykaGame.prefs.getInteger("plays_counter", 0) == 0)
            CykaGame.prefs.putBoolean("controls_on", false);
        CykaGame.prefs.putInteger("plays_counter", CykaGame.prefs.getInteger("plays_counter", 0)+1);
        CykaGame.prefs.flush();

        setGameScore(points);

        int isBest = 0;

        if(points > bestScore) {
            CykaGame.prefs.putInteger("best-score", points);
            bestScore = points;
            CykaGame.prefs.flush();
            isBest = 1;
        }
        bestScoreText = new Texture("death_screen/best_score_"+isBest+".png");
        setBestScore(bestScore);
        //COINS
        int coinNumber = 0;
        if(points < 10)
            coinNumber = 0;
        else if (points < 20)
            coinNumber = 1;
        else if (points < 30)
            coinNumber = 2;
        else if (points < 40)
            coinNumber = 3;
        else if (points < 50)
            coinNumber = 4;
        coin = new Texture("death_screen/coin_"+coinNumber+".png");

        //BUTTONS INPUT
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.setScreen(new GameScreen(game));
            }
        });
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.setScreen(new MenuScreen(game));
            }
        });
        shareButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sounds.buttonClickSound.play(volume);
                game.share.shareScore(points);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.845f, 0.845f, 0.845f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        drawScore(points);
        drawBestScore(bestScore);
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
    }

    private void setBestScore(int points) {
        int hundredthsPlace = points / 100;
        int tensPlace = (points - (hundredthsPlace * 100)) / 10;
        int onesPlace = points - ((hundredthsPlace * 100) + (tensPlace * 10));
        bestHundredths = numbersFrames.getKeyFrame(tensPlace, true);
        bestTens = numbersFrames.getKeyFrame(tensPlace, true);
        bestOnes = numbersFrames.getKeyFrame(onesPlace, true);
    }
    private void setGameScore(final int points) {
        Timer.schedule(new Timer.Task(){
            int count = 0;
                           @Override
                           public void run() {
                               int a = count / 100;
                               int b = (count - (a * 100)) / 10;
                               int c = count - ((a * 100) +(b * 10));
                               count++;
                               hundredths = numbersFrames.getKeyFrame(a, true);
                               tens = numbersFrames.getKeyFrame(b, true);
                               ones = numbersFrames.getKeyFrame(c, true);
                           }
                       }
                , 0f
                , 0.5f/(points*2)
                , points
        );
    }

    private void drawScore(int points) {
        if(points <= 99) {
            game.batch.draw(tens, 150, 714, 120, 240);
            game.batch.draw(ones, 270, 714, 120, 240);
            game.batch.draw(coin, 400, 770, 128, 128);
        }
        else {
            game.batch.draw(hundredths, 76, 714, 120, 240);
            game.batch.draw(tens, 196, 714, 120, 240);
            game.batch.draw(ones, 316, 714, 120, 240);
            game.batch.draw(coin, 466, 770, 128, 128);
        }
    }

    private void drawBestScore(int bestScore) {
        if(bestScore <= 99) {
            game.batch.draw(bestScoreText, 180, 615, 180, 85);
            game.batch.draw(bestTens, 380, 615, 48, 84);
            game.batch.draw(bestOnes, 428, 615, 48, 84);
        }
        else {
            game.batch.draw(bestScoreText, 158, 615, 180, 85);
            game.batch.draw(bestHundredths, 358, 615, 48, 84);
            game.batch.draw(bestTens, 406, 615, 48, 84);
            game.batch.draw(bestOnes, 454, 615, 48, 84);

        }
    }
}
