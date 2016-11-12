package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
    //NUMBERS
    private TextureRegion tens;
    private TextureRegion ones;
    //BEST SCORE
    private TextureRegion bestTens;
    private TextureRegion bestOnes;

    DeathScreen(CykaGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        //VIEWPORT
        FitViewport viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
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
        //PREFERENCES / SCORE
        int points = GameScreen.points;
        setGameScore(points);

        int bestScore = CykaGame.prefs.getInteger("best-score", 0);

        if(points > bestScore) {
            CykaGame.prefs.putInteger("best-score", points);
            bestScore = points;
            CykaGame.prefs.flush();
        }

        setBestScore(bestScore);
        //BUTTONS INPUT
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(tens, 200, 714);
        game.batch.draw(ones, 320, 714);
        game.batch.draw(bestTens, 200, 600, 24, 42);
        game.batch.draw(bestOnes, 320, 600, 24, 42);
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
        int tensPlace = points / 10;
        int onesPlace = points - (tensPlace * 10);
        bestTens = GameBasic.numbersAnimation.getKeyFrame(tensPlace, true);
        bestOnes = GameBasic.numbersAnimation.getKeyFrame(onesPlace, true);
    }
    private void setGameScore(final int points) {
        Timer.schedule(new Timer.Task(){
                           int count = 0;
                           @Override
                           public void run() {
                               count++;
                               int a;
                               int b;
                               if(count < 25) {
                                   a = MathUtils.random(10);
                                   b = MathUtils.random(10);
                               }
                               else {
                                   a = points / 10;
                                   b = points - (a * 10);
                               }
                               tens = GameBasic.numbersAnimation.getKeyFrame(a, true);
                               ones = GameBasic.numbersAnimation.getKeyFrame(b, true);
                           }
                       }
                , 0f
                , 0.05f
                , 25
        );
    }
}
