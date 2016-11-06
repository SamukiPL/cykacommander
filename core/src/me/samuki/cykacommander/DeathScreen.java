package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
        playAgainSkin.add("play_again", new Texture("play_button.png"));

        final ImageButton playAgainButton = new ImageButton(playAgainSkin.getDrawable("play_again"));
        playAgainButton.setPosition(viewport.getWorldWidth()/2-playAgainButton.getWidth()/2,450);
        stage.addActor(playAgainButton);
        //MENU
        Skin menuSkin = new Skin();
        menuSkin.add("menu", new Texture("menu_button.png"));

        final ImageButton menuButton = new ImageButton(menuSkin.getDrawable("menu"));
        menuButton.setPosition(viewport.getWorldWidth()/2-menuButton.getWidth()/2,350);
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

    private void setGameScore(int points) {
        int tensPlace = points / 10;
        int onesPlace = points - (tensPlace * 10);
        tens = GameBasic.numbersAnimation.getKeyFrame(tensPlace, true);
        ones = GameBasic.numbersAnimation.getKeyFrame(onesPlace, true);
    }
    private void setBestScore(int points) {
        int tensPlace = points / 10;
        int onesPlace = points - (tensPlace * 10);
        bestTens = GameBasic.numbersAnimation.getKeyFrame(tensPlace, true);
        bestOnes = GameBasic.numbersAnimation.getKeyFrame(onesPlace, true);
    }
}
