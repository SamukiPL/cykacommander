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

class ShopScreen implements Screen {
    CykaGame game;
    Stage stage;
    FitViewport viewport;
    //BACKGROUND
    static Texture background;
    //CASH
    static int cash;
    static int thousandthsPlace, hundredthsPlace, tensPlace, onesPlace;
    static Timer changeNumbers;
    static Animation numbersFrames;
    static Texture currency;

    ShopScreen(CykaGame game) {
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
        //BACKGROUND
        background = new Texture("shop_background.png");

        //CASH
        cash = CykaGame.prefs.getInteger("cash", 0);
        thousandthsPlace = cash / 1000;
        hundredthsPlace = (cash - (thousandthsPlace * 1000)) / 100;
        tensPlace = (cash - ((thousandthsPlace * 1000) + (hundredthsPlace * 100))) / 10;
        onesPlace = cash - ((thousandthsPlace * 1000) + (hundredthsPlace * 100) + (tensPlace * 10));
        changeNumbers = new Timer();
        numbersFrames = GameBasic.spriteCutting("prices/shop_numbers.png", 5, 2);
        currency = new Texture("prices/currency.png");

        //BACK
        Skin backSkin = new Skin();
        backSkin.add("back_up", new Texture("back_button_0.png"));
        backSkin.add("back_down", new Texture("back_button_1.png"));

        final Button backButton = new Button(backSkin.getDrawable("back_up"), backSkin.getDrawable("back_down"));
        backButton.setBounds(0, 0, 270, 126);
        backButton.setPosition(10, viewport.getWorldHeight()-backButton.getHeight()-10);
        stage.addActor(backButton);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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
        showWallet(thousandthsPlace, hundredthsPlace, tensPlace, onesPlace);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();

        game.setScreen(new ShopScreenShips(game)) ;
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


    void showWallet(int... places) {
        TextureRegion thousands =   numbersFrames.getKeyFrame(places[0], true);
        TextureRegion hundreds  =   numbersFrames.getKeyFrame(places[1], true);
        TextureRegion tens      =   numbersFrames.getKeyFrame(places[2], true);
        TextureRegion ones      =   numbersFrames.getKeyFrame(places[3], true);
        game.batch.draw(thousands, 380, 898, 48, 84);
        game.batch.draw(hundreds, 428, 898, 48, 84);
        game.batch.draw(tens, 476, 898, 48, 84);
        game.batch.draw(ones, 524, 898, 48, 84);
        game.batch.draw(currency, 572, 898, 48, 84);
    }

    void cashDecrease(final int cashBefore, final int cashAfter) {
        changeNumbers.clear();
        changeNumbers.scheduleTask(new Timer.Task(){
                                       int a = cashBefore-1;
                                       @Override
                                       public void run() {
                                           thousandthsPlace = a / 1000;
                                           hundredthsPlace = (a - (thousandthsPlace * 1000)) / 100;
                                           tensPlace = (a - ((thousandthsPlace * 1000) + (hundredthsPlace * 100))) / 10;
                                           onesPlace = a - ((thousandthsPlace * 1000) + (hundredthsPlace * 100) + (tensPlace * 10));
                                           a--;
                                       }
                                   }
                , 0f
                , 0.003f
                , cashBefore-cashAfter-1
        );
    }

}
