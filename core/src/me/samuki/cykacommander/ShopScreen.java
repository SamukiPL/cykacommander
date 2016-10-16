package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ShopScreen implements Screen {
    CykaGame game;
    GameBasic basic;
    Stage stage;
    FitViewport viewport;
    //BACKGROUND
    Texture background;
    //BUTTONS SKINS
    Skin backSkin;
    Skin buyUseSkin;
    //CASH
    int cash;
    int tensPlace;
    int onesPlace;
    TextureRegion tens;
    TextureRegion ones;
    //SHOPITEMS
    private static final int AMOUNT = 4; //Ships for sale

    Texture[] shopItems;
    ImageButton[] shopButtons;

    public ShopScreen(CykaGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        basic = new GameBasic();
        //VIEWPORT
        viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //BACK
        backSkin = new Skin();
        backSkin.add("back", new Texture("back_button.png"));

        final ImageButton backButton = new ImageButton(backSkin.getDrawable("back"), backSkin.getDrawable("back"));
        backButton.setPosition(10, viewport.getWorldHeight()-backButton.getHeight()-10);
        stage.addActor(backButton);
        //BACKGROUND
        background = new Texture("shop_background.png");
        //CASH
        cash = game.prefs.getInteger("cash", 0);
        //SHOP ITEMS
        shopItems = new Texture[AMOUNT];
        shopButtons = new ImageButton[AMOUNT];

        buyUseSkin = new Skin();
        buyUseSkin.add("buy", new Texture("buy_button.png"));
        buyUseSkin.add("use", new Texture("use_button.png"));

        for(int i = 0; i < AMOUNT; i++) {
            final int index = i;

            shopItems[i] = new Texture("shop_items/shop_ship_1.png");
            shopButtons[i] = new ImageButton(buyUseSkin.getDrawable("buy"), buyUseSkin.getDrawable("buy"), buyUseSkin.getDrawable("use"));
            if(i%2 == 0) {
                shopButtons[i].setPosition(160 - (shopButtons[i].getWidth()/2) + (160 * i), 465);
            }
            else {
                shopButtons[i].setPosition(160 - (shopButtons[i].getWidth()/2) + (160 * (i-1)), 50);
            }
            stage.addActor(shopButtons[i]);

            shopButtons[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println(index);
                }
            });
        }

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
        showWallet(cash);
        game.batch.draw(tens, 524, 898, 48, 84);
        game.batch.draw(ones, 572, 898, 48, 84);
        shopItemsDraw();
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
        for(int i = 0; i < AMOUNT; i++) {
            shopItems[i].dispose();
        }
    }

    public void showWallet(int points) {
        tensPlace = points/10;
        onesPlace = points-(tensPlace*10);
        tens = GameBasic.numbersAnimation.getKeyFrame(tensPlace, true);
        ones = GameBasic.numbersAnimation.getKeyFrame(onesPlace, true);
    }

    public void shopItemsDraw() {
        for(int i = 0; i < AMOUNT; i++) {
            if(i%2 == 0) {
                game.batch.draw(shopItems[i], 112-shopItems[i].getWidth()/2+(160*i), 615, 128, 128);
            }
            else {
                game.batch.draw(shopItems[i], 112-shopItems[i].getWidth()/2+(160*(i-1)), 200, 128, 128);
            }
            }
        }
}
