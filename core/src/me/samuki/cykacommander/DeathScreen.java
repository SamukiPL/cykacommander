package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private Texture playAgain;
    private boolean startGame;

    private final int points = GameScreen.points;
    private int bestScore = CykaGame.prefs.getInteger("best-score", 0);
    //NUMBERS
    private Timer numbersRunning;
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
        //PREFERENCES / SCORE
        numbersFrames = GameBasic.spriteCutting("death_screen/numbers_death.png", 5, 2);
        //CONTROLS OFF
        if(CykaGame.prefs.getInteger("plays_counter", 1) == 1)
            CykaGame.prefs.putBoolean("controls_on", false);
        CykaGame.prefs.putInteger("plays_counter", CykaGame.prefs.getInteger("plays_counter", 1)+1);
        CykaGame.prefs.flush();
        //POINTS
        numbersRunning = new Timer();
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

        if(CykaGame.playServices.isSignedIn()) {
            CykaGame.playServices.unlockAchievement(bestScore, CykaGame.prefs.getInteger("plays_counter"));
            CykaGame.playServices.submitScore(bestScore);
        }
        //PLAY AGAIN
        startGame = false;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                startGame = true;
            }
        },(0.5f/points)*points);
        playAgain = new Texture("death_screen/tap_again.png");

        final Actor playAgainButton = new Actor();
        playAgainButton.setBounds(0, 0, 640, 1024);
        playAgainButton.setPosition(0,0);
        stage.addActor(playAgainButton);
        //MENU
        Skin menuSkin = new Skin();
        menuSkin.add("menu_up", new Texture("menu_button_0.png"));
        menuSkin.add("menu_down", new Texture("menu_button_1.png"));

        final Button menuButton = new Button(menuSkin.getDrawable("menu_up"), menuSkin.getDrawable("menu_down"));
        menuButton.setBounds(0, 0, 300, 140);
        menuButton.setPosition(viewport.getWorldWidth()/2-menuButton.getWidth()/2,296);
        stage.addActor(menuButton);
        //SHARE
        Skin shareSkin = new Skin();
        shareSkin.add("share_up", new Texture("share_button_0.png"));
        shareSkin.add("share_down", new Texture("share_button_1.png"));

        final Button shareButton = new Button(shareSkin.getDrawable("share_up"), shareSkin.getDrawable("share_down"));
        shareButton.setBounds(0, 0, 340, 140);
        shareButton.setPosition(viewport.getWorldWidth()/2-shareButton.getWidth()/2,146);
        stage.addActor(shareButton);

        //BUTTONS INPUT
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sounds.buttonClickSound.play(volume);
                if(startGame)
                    game.setScreen(new GameScreen(game));
                else {
                    int a = points / 100;
                    int b = (points - (a * 100)) / 10;
                    int c = points - ((a * 100) +(b * 10));
                    hundredths = numbersFrames.getKeyFrame(a, true);
                    tens = numbersFrames.getKeyFrame(b, true);
                    ones = numbersFrames.getKeyFrame(c, true);
                    numbersRunning.clear();
                    startGame = true;
                }
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
        if(startGame)
            game.batch.draw(playAgain, viewport.getWorldWidth()/2 - 445/2, 453, 445, 35);
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
        background.dispose();
        playAgain.dispose();
        sounds.dispose();
        bestScoreText.dispose();
        coin.dispose();
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
        numbersRunning.schedule(new Timer.Task(){
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
            game.batch.draw(tens, 150, 600, 120, 240);
            game.batch.draw(ones, 270, 600, 120, 240);
            game.batch.draw(coin, 400, 656, 128, 128);
        }
        else {
            game.batch.draw(hundredths, 76, 714, 120, 240);
            game.batch.draw(tens, 196, 600, 120, 240);
            game.batch.draw(ones, 316, 600, 120, 240);
            game.batch.draw(coin, 466, 656, 128, 128);
        }
    }

    private void drawBestScore(int bestScore) {
        if(bestScore <= 99) {
            game.batch.draw(bestScoreText, 180, 501, 180, 85);
            game.batch.draw(bestTens, 380, 501, 48, 84);
            game.batch.draw(bestOnes, 428, 501, 48, 84);
        }
        else {
            game.batch.draw(bestScoreText, 158, 501, 180, 85);
            game.batch.draw(bestHundredths, 358, 501, 48, 84);
            game.batch.draw(bestTens, 406, 501, 48, 84);
            game.batch.draw(bestOnes, 454, 501, 48, 84);

        }
    }
}
