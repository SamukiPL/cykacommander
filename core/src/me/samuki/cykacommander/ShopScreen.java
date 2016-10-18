package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ShopScreen implements Screen {
    private CykaGame game;
    GameBasic basic;
    private Stage stage;
    private FitViewport viewport;
    //BACKGROUND
    private Texture background;
    //CASH
    private int cash;
    private int tensPlace;
    private int onesPlace;
    private TextureRegion tens;
    private TextureRegion ones;
    //BUTTON STYLES
    ImageButton.ImageButtonStyle buyButton;
    ImageButton.ImageButtonStyle useButton;
    //SHOP PRICES
    private static final int PRICE_CHANGE = 0;
    //SHOPITEMS
    private static final int AMOUNT = 2; //Ships for sale

    private boolean[] isBought;
    private Texture[] shopItems;
    private ImageButton[] shopButtons;
    private int[] price;

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
        Skin backSkin = new Skin();
        backSkin.add("back", new Texture("back_button.png"));

        final ImageButton backButton = new ImageButton(backSkin.getDrawable("back"), backSkin.getDrawable("back"));
        backButton.setPosition(10, viewport.getWorldHeight()-backButton.getHeight()-10);
        stage.addActor(backButton);
        //BACKGROUND
        background = new Texture("shop_background.png");
        //CASH
        cash = CykaGame.prefs.getInteger("cash", 0);
        //SHOP ITEMS
        CykaGame.prefs.putBoolean("isBought_0", true);
        CykaGame.prefs.flush();
        isBought = new boolean[AMOUNT];
        shopItems = new Texture[AMOUNT];
        shopButtons = new ImageButton[AMOUNT];
        price = new int[AMOUNT];

        Skin buyUseSkin = new Skin();
        //BUY BUTTON STYLE
        buyUseSkin.add("buy_up", new Texture("buy_button.png"));
        buyUseSkin.add("buy_down", new Texture("buy_button.png"));
        buyButton = new ImageButton.ImageButtonStyle();
        buyButton.up = buyUseSkin.getDrawable("buy_up");
        buyButton.down = buyUseSkin.getDrawable("buy_down");
        //USE BUTTON STYLE
        buyUseSkin.add("use_0", new Texture("use_button_0.png"));
        buyUseSkin.add("use_1", new Texture("use_button_1.png"));
        useButton = new ImageButton.ImageButtonStyle();
        useButton.up = buyUseSkin.getDrawable("use_0");
        useButton.down = buyUseSkin.getDrawable("use_1");
        useButton.checked = buyUseSkin.getDrawable("use_1");

        for(int i = 0; i < AMOUNT; i++) {
            final int index = i;

            isBought[index] = CykaGame.prefs.getBoolean("isBought_"+index, false);
            if(!isBought[index]) {
                shopItems[index] = new Texture("shop_items/shop_ship_" + index + ".png");
                shopButtons[index] = new ImageButton(buyButton);
            }
            else {
                shopItems[index] = new Texture("shop_items/shop_ship_" + index + ".png");
                shopButtons[index] = new ImageButton(useButton);
            }

            if(i%2 == 0) {
                shopButtons[index].setPosition(160 - (shopButtons[index].getWidth()/2) + (160 * index), 465);
            }
            else {
                shopButtons[index].setPosition(160 - (shopButtons[index].getWidth()/2) + (160 * (index-1)), 50);
            }
            stage.addActor(shopButtons[index]);

            price[index] = PRICE_CHANGE+(PRICE_CHANGE*index);

            shopButtons[index].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isBought[index]) {
                        //IF NOT BOUGHT
                        if(cash >= price[index]) {
                            cash-=price[index];
                            CykaGame.prefs.putInteger("cash", cash);
                            CykaGame.prefs.putBoolean("isBought_"+index, true);
                            isBought[index] = true;
                            shopButtons[index].setStyle(useButton);
                            shopButtons[index].setChecked(false);
                            CykaGame.prefs.flush();
                        }
                    }
                    else {
                        if (shopButtons[index].isChecked()) {
                            shopButtons[CykaGame.prefs.getInteger("whichShip", 0)].setChecked(false);
                            CykaGame.prefs.putInteger("whichShip", index);
                            CykaGame.prefs.flush();
                        }
                        else
                            shopButtons[index].setChecked(true);
                    }

                }
            });
        }

        shopButtons[CykaGame.prefs.getInteger("whichShip", 0)].setChecked(true);

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

    private void showWallet(int points) {
        tensPlace = points/10;
        onesPlace = points-(tensPlace*10);
        tens = GameBasic.numbersAnimation.getKeyFrame(tensPlace, true);
        ones = GameBasic.numbersAnimation.getKeyFrame(onesPlace, true);
        game.batch.draw(tens, 524, 898, 48, 84);
        game.batch.draw(ones, 572, 898, 48, 84);
    }

    private void shopItemsDraw() {
        for(int i = 0; i < AMOUNT; i++) {
            if(i%2 == 0) {
                game.batch.draw(shopItems[i], 160-(192/2)+(160*i), 590, 192, 192);
            }
            else {
                game.batch.draw(shopItems[i], 160-(192/2)+(160*(i-1)), 175, 192, 192);
            }
            }
        }
}
