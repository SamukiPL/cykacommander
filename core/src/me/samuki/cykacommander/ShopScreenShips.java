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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

class ShopScreenShips extends ShopScreen implements Screen {

    private ImageButton.ImageButtonStyle useButton;
    //SHOPITEMS
    private static final int AMOUNT = 18; //Ships for sale
    private int shopPage;
    private boolean setButtons;

    private boolean[] isBought;
    private TextureRegion[] priceText;
    private Texture[] shopItems;
    private ImageButton[] shopButtons;
    private int[] prices = {0, 25, 25, 50, 50, 50, 75, 75, 100, 125, 150, 175, 200, 225, 250, 500, 750, 1000, 1250, 1500};
    private int[] shipPrice = {0, 1, 1, 2, 2, 2, 3, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    ShopScreenShips(CykaGame game) {
        super(game);
    }

    @Override
    public void show() {
        //VIEWPORT
        viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //PAGE CHANGE
        setButtons = true;
        Actor actor = new Actor();
        actor.setPosition(0, 0);
        actor.setSize(640, 1024);
        stage.addActor(actor);
        actor.addListener(new ActorGestureListener() {
            public void fling (InputEvent event, float velocityX, float velocityY, int button) {
                if(velocityX > 1000) {
                    shopPage--;
                    for (ImageButton shopButton : shopButtons) {
                        shopButton.setVisible(false);
                        shopButton.setTouchable(Touchable.disabled);
                        setButtons = true;
                    }
                }
                else if(velocityX < -1000) {
                    shopPage++;
                    for (ImageButton shopButton : shopButtons) {
                        shopButton.setVisible(false);
                        shopButton.setTouchable(Touchable.disabled);
                        setButtons = true;
                    }
                }
            }
        });

        //SHOP ITEMS
        CykaGame.prefs.putBoolean("isBought_0", true); //FIRST SHIP
        CykaGame.prefs.flush();
        isBought = new boolean[AMOUNT];
        priceText = new TextureRegion[AMOUNT];
        shopItems = new Texture[AMOUNT];
        shopButtons = new ImageButton[AMOUNT];
        //PRICES ANIMATION
        Animation pricesFrames = GameBasic.spriteCutting("prices/prices.png", 4, 4);
        //TIMER FOR NUMBERS

        shopPage = 0;

        Skin buyUseSkin = new Skin();
        //BUY BUTTON STYLE
        buyUseSkin.add("buy_up", new Texture("buy_button_0.png"));
        buyUseSkin.add("buy_down", new Texture("buy_button_1.png"));
        ImageButton.ImageButtonStyle buyButton = new ImageButton.ImageButtonStyle();
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
                priceText[index] = pricesFrames.getKeyFrame(shipPrice[index]);
                shopItems[index] = new Texture("shop_items/shop_item_not_bought.png");
                shopButtons[index] = new ImageButton(buyButton);
            }
            else {
                priceText[index] = pricesFrames.getKeyFrame(shipPrice[index]);
                shopItems[index] = new Texture("shop_items/rus/shop_ship_" + index + ".png");
                shopButtons[index] = new ImageButton(useButton);
            }
            shopButtons[index].setVisible(false);
            shopButtons[index].setTouchable(Touchable.disabled);
            stage.addActor(shopButtons[index]);

            shopButtons[index].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isBought[index]) {
                        //IF NOT BOUGHT
                        if(cash >= prices[index]) {
                            //CASH CHANGE
                            int tmpCash = cash;
                            cash-= prices[index];
                            cashDecrease(tmpCash, cash);
                            CykaGame.prefs.putInteger("cash", cash);
                            //HOLDING STATUS
                            CykaGame.prefs.putBoolean("isBought_"+index, true);
                            shopItems[index] = new Texture("shop_items/rus/shop_ship_" + index + ".png");
                            isBought[index] = true;
                            shopButtons[index].setStyle(useButton);
                            //CHECK CHANGE
                            shopButtons[CykaGame.prefs.getInteger("whichShip", 0)].setChecked(false);
                            CykaGame.prefs.putInteger("whichShip", index);
                            shopButtons[index].setChecked(true);
                            //REMEMBER THAT!
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
        background.dispose();
        for(int i = 0; i < AMOUNT; i++) {
            shopItems[i].dispose();
        }
    }

    private void shopItemsDraw() {
        int shipSize;

        if(shopPage > AMOUNT/2-1){
            shopPage = 0;
        }
        if(shopPage < 0) {
            shopPage = AMOUNT/2-1;
        }
        int row = shopPage*2;
        for(int i = 0; i < 4; i++) {
            int item = i+row;
            if(item > AMOUNT-1) {
                row = -2;
                item = i+row;
            }
            if(isBought[item])
                shipSize = 192;
            else {
                shipSize = 128;
                game.batch.draw(priceText[item], 160 - (135 / 2) + (160 * (i - (i % 2))), 728 - (415 * (i % 2)), 135, 72);
            }
            game.batch.draw(shopItems[item], 160-(shipSize/2)+(160*(i-(i%2))), 590-(415*(i%2)), shipSize, shipSize);
            shopButtons[item].setPosition(160 - (shopButtons[item].getWidth()/2) + (160*(i-(i%2))), 465-(415*(i%2)));
            if(setButtons) {
                shopButtons[item].setVisible(true);
                shopButtons[item].setTouchable(Touchable.enabled);
                if(i == 3)
                    setButtons = false;
            }
            }
        }

    @Override
    void showWallet(int... places) {
        super.showWallet(places);
    }

    @Override
    void cashDecrease(int cashBefore, int cashAfter) {
        super.cashDecrease(cashBefore, cashAfter);
    }
}