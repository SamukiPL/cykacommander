package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

class CykaSplashScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    private FitViewport viewport;
    private long startTime;

    private Texture samukiLogo;
    private Texture updateText;

    private Animation appLogoAnimation;
    private TextureRegion appLogo;

    CykaSplashScreen (CykaGame game) {
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
        samukiLogo = new Texture("samuki_logo.png");
        updateText = new Texture("update_text.png");

        startTime = TimeUtils.millis();

        appLogoAnimation = GameBasic.spriteCutting("splash_screen_logo.png", 3, 3);
        appLogo = appLogoAnimation.getKeyFrame(0);

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
                            int a = 0;
                                @Override
                                public void run() {
                                    appLogo = appLogoAnimation.getKeyFrame(a);
                                    a++;
                                }
                            },
                0f,
                0.111f,
                8);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.845f, 0.845f, 0.845f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(appLogo, viewport.getWorldWidth()/2 - 60, viewport.getWorldHeight()/2 - 60, 120, 120);
        Color color = game.batch.getColor();
        color.a = 0.4f;
        game.batch.setColor(color);
        game.batch.draw(samukiLogo, viewport.getWorldWidth()/2-32, 32, 64, 64);
        game.batch.draw(updateText, viewport.getWorldWidth()/2-198, viewport.getWorldHeight()-100, 396, 42);
        color.a = 1f;
        game.batch.setColor(color);
        game.batch.end();

        if (TimeUtils.millis()>(startTime+1250))
            game.setScreen(new MenuScreen(game));
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
        samukiLogo.dispose();
        updateText.dispose();
    }
}
