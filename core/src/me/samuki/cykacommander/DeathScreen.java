package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DeathScreen implements Screen {
    CykaGame game;
    Stage stage;
    FitViewport viewport;
    //PLAY AGAIN
    Skin playAgainSkin;
    ImageButton.ImageButtonStyle playAgainButtonStyle;
    //MENU
    Skin menuSkin;
    ImageButton.ImageButtonStyle menuButtonStyle;
    //NUMBERS
    int points;
    int tensPlace;
    int onesPlace;
    TextureRegion tens;
    TextureRegion ones;
    //PREFERENCES
    int bestScore;
    int cash;

    public DeathScreen(CykaGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        //VIEWPORT
        viewport = new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        //PLAY AGAIN
        playAgainSkin = new Skin();
        playAgainSkin.add("play_again", new Texture("play_button.png"));
        playAgainButtonStyle = new ImageButton.ImageButtonStyle();
        playAgainButtonStyle.up = playAgainSkin.newDrawable("play_again", Color.LIGHT_GRAY);

        final ImageButton playAgainButton = new ImageButton(playAgainButtonStyle);
        playAgainButton.setPosition(viewport.getWorldWidth()/2-playAgainButton.getWidth()/2,450);
        stage.addActor(playAgainButton);
        //MENU
        menuSkin = new Skin();
        menuSkin.add("menu", new Texture("menu_button.png"));
        menuButtonStyle = new ImageButton.ImageButtonStyle();
        menuButtonStyle.up = menuSkin.newDrawable("menu", Color.LIGHT_GRAY);

        final ImageButton menuButton = new ImageButton(menuButtonStyle);
        menuButton.setPosition(viewport.getWorldWidth()/2-menuButton.getWidth()/2,350);
        stage.addActor(menuButton);
        //PREFERENCES
        this.points = GameScreen.points;
        bestScore = CykaGame.prefs.getInteger("best-score", 0);

        if(points > bestScore) {
            CykaGame.prefs.putInteger("best-score", points);
            bestScore = points;
            CykaGame.prefs.flush();
        }
        //CASH
        cash = CykaGame.prefs.getInteger("cash", 0);
        for(int i = 0; i <= (points-5); i+=5) {
            cash++;
            CykaGame.prefs.putInteger("cash", cash);
            System.out.println(cash+"TAK");
            CykaGame.prefs.flush();
        }
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
        numbersDeathScreen(points);
        game.batch.draw(tens, 200, 714);
        game.batch.draw(ones, 320, 714);
        numbersDeathScreen(bestScore);
        game.batch.draw(tens, 200, 600, 24, 42);
        game.batch.draw(ones, 320, 600, 24, 42);
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

    public void numbersDeathScreen(int points) {
        tensPlace = points/10;
        onesPlace = points-(tensPlace*10);
        tens = GameBasic.numbersAnimation.getKeyFrame(tensPlace, true);
        ones = GameBasic.numbersAnimation.getKeyFrame(onesPlace, true);
    }
}
