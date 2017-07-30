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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

class ShopScreen implements Screen {
    private CykaGame game;
    private Stage stage;
    private FitViewport viewport;
    //BACKGROUND
    private static Texture background;
    //SOUNDS
    private SoundsBase sounds;
    private float volume;
    //CASH
    private static int cash;
    private static int thousandthsPlace, hundredthsPlace, tensPlace, onesPlace;
    private static Timer changeNumbers;
    private static Animation numbersFrames;
    private static Texture currency;
    private Animation pricesFrames;
    private ImageButton.ImageButtonStyle buyButton;


    private float shopX = 0;
    private int shopVelocity = 0;

    private ImageButton.ImageButtonStyle useButton;
    //SHOPITEMS
    private String whichNation = CykaGame.prefs.getString("nation", "");
    private int amount = 20; //Ships for sale
    private int shopWidth = -320 * ((amount / 2) - 2);

    private Button changeTheType;

    private boolean[] isBought;
    private TextureRegion[] priceText;
    private Texture[] shopItems;
    private ImageButton[] shopButtons;
    private int[] prices = {0, 10, 10, 10, 10, 10, 10, 10, 25, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 500};
    private int[] shipPrice = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    ShopScreen(CykaGame game) {
        this.game = game;
    }

    private void setShopWidth() {
        shopWidth = -320 * ((amount / 2) - 2);
    }

    private int getCurrentAmount() {
        if(whichNation.equals(""))
            return 20;
        else
            return 6;
    }

    @Override
    public void show() {
        //SOUNDS
        sounds = new SoundsBase(2);

        if(CykaGame.prefs.getBoolean("sound", true))
            volume = 1f;
        else
            volume = 0;
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

        //PAGE CHANGE
        Actor actor = new Actor();
        actor.setPosition(0, 0);
        actor.setSize(640, 1024);
        stage.addActor(actor);
        actor.addListener(new ActorGestureListener() {
            public void fling (InputEvent event, float velocityX, float velocityY, int button) {
                shopVelocity += velocityX;
            }
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                shopX += deltaX;
            }
        });

        //SHOP ITEMS
        CykaGame.prefs.putBoolean("isBought_0", true); //FIRST SHIP
        CykaGame.prefs.putBoolean("isBought_0ger", true); //FIRST SHIP
        CykaGame.prefs.flush();
        isBought = new boolean[amount];
        priceText = new TextureRegion[amount];
        shopItems = new Texture[amount];
        shopButtons = new ImageButton[amount];
        //PRICES ANIMATION
        pricesFrames = GameBasic.spriteCutting("prices/prices.png", 4, 4);

        Skin buyUseSkin = new Skin();
        //BUY BUTTON STYLE
        buyUseSkin.add("buy_up", new Texture("buy_button_0.png"));
        buyUseSkin.add("buy_down", new Texture("buy_button_1.png"));
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

        Skin blankSkin = new Skin();
        blankSkin.add("blank", new Texture("blank.png"));

        changeTheType = new Button(blankSkin.getDrawable("blank"));
        changeTheType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(whichNation.equals("ger")) {
                    amount = 20;
                    setShopWidth();
                    whichNation = "";

                } else if(whichNation.equals("")) {
                    amount = 6;
                    setShopWidth();
                    whichNation = "ger";
                }
                CykaGame.prefs.putString("nation", whichNation);
                CykaGame.prefs.flush();
                setItems();
            }
        });
        changeTheType.setBounds(0, 0, 192, 192);
        stage.addActor(changeTheType);
        setItems();
        shopVelocity = 0;

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
                sounds.buttonClickSound.play(volume);
                game.setScreen(new MenuScreen(game));
            }
        });

        //ADVISE
        boolean seenThat = CykaGame.prefs.getBoolean("seenThatShop", false);
        if(!seenThat) {
            Skin adviseSkin = new Skin();
            adviseSkin.add("advise", new Texture("shop_background_advise.png"));

            final Button adviseButton = new Button(adviseSkin.getDrawable("advise"));
            adviseButton.setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            adviseButton.setName("advise");

            adviseButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    CykaGame.prefs.putBoolean("seenThatShop", true);
                    CykaGame.prefs.flush();

                    adviseButton.remove();
                }
            });

            stage.addActor(adviseButton);
            amount = getCurrentAmount();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.845f, 0.845f, 0.845f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        changeX();

        game.batch.begin();
        shopItemsDraw();
        showWallet(thousandthsPlace, hundredthsPlace, tensPlace, onesPlace);
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
        currency.dispose();
        for(int i = 0; i < 20; i++) {
            shopItems[i].dispose();
        }
    }


    private void showWallet(int... places) {
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

    private void cashDecrease(final int cashBefore, final int cashAfter) {
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

    private void changeX() {
        if(shopVelocity != 0) {
            if (shopVelocity > 0) {
                shopVelocity -= 8000 * Gdx.graphics.getDeltaTime();
                if(shopVelocity <= 0)
                    shopVelocity = 0;
            }
            else if (shopVelocity < 0) {
                shopVelocity += 8000 * Gdx.graphics.getDeltaTime();
                if(shopVelocity >= 0)
                    shopVelocity = 0;
            }
        }
        shopX += Gdx.graphics.getDeltaTime()*shopVelocity;

        if(shopX <= shopWidth) {
            shopVelocity = 0;
            shopX = shopWidth;
        }
        if(shopX >= 0) {
            shopVelocity = 0;
            shopX = 0;
        }
    }

    private void shopItemsDraw() {
        int shipSize;
        for(int i = 0; i <= amount /4; i++) {
            game.batch.draw(background, 0 + shopX + 640*i, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }
        for(int i = 0; i < amount; i++) {
            if(isBought[i])
                shipSize = 192;
            else {
                shipSize = 128;
                game.batch.draw(priceText[i], 160 - (135 / 2) + (160 * (i - (i % 2)))+shopX, 728 - (415 * (i % 2)), 135, 72);
            }
            game.batch.draw(shopItems[i], 160-(shipSize/2)+(160*(i-(i%2)))+shopX, 590-(415*(i%2)), shipSize, shipSize);
            shopButtons[i].setPosition(160 - (shopButtons[i].getWidth()/2) + (160*(i-(i%2)))+shopX, 465-(415*(i%2)));
            if(i == 0)
                changeTheType.setPosition(160-(shipSize/2)+(160*(i-(i%2)))+shopX, 590-(415*(i%2)));
        }
    }

    private void setItems() {
        for(int i = 0; i < amount; i++) {
            final int index = i;

            if(shopButtons[index] != null)
                shopButtons[index].remove();

            isBought[index] = CykaGame.prefs.getBoolean("isBought_"+index+whichNation, false);
            if(!isBought[index]) {
                priceText[index] = pricesFrames.getKeyFrame(shipPrice[index]);
                shopItems[index] = new Texture("shop_items/shop_item_not_bought.png");
                shopButtons[index] = new ImageButton(buyButton);
            }
            else {
                priceText[index] = pricesFrames.getKeyFrame(shipPrice[index]);
                shopItems[index] = new Texture("shop_items/rus/shop_ship_" + index + whichNation + ".png");
                shopButtons[index] = new ImageButton(useButton);
            }
            stage.addActor(shopButtons[index]);

            shopButtons[index].clearListeners();
            shopButtons[index].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(!isBought[index]) {
                        //IF NOT BOUGHT
                        if(cash >= prices[index]) {
                            sounds.canBuySound.play(volume);
                            //CASH CHANGE
                            int tmpCash = cash;
                            cash-= prices[index];
                            cashDecrease(tmpCash, cash);
                            CykaGame.prefs.putInteger("cash", cash);
                            //HOLDING STATUS
                            CykaGame.prefs.putBoolean("isBought_"+index+whichNation, true);
                            shopItems[index] = new Texture("shop_items/rus/shop_ship_" + index + whichNation +".png");
                            isBought[index] = true;
                            shopButtons[index].setStyle(useButton);
                            //CHECK CHANGE
                            shopButtons[CykaGame.prefs.getInteger("whichShip"+whichNation, 0)].setChecked(false);
                            CykaGame.prefs.putInteger("whichShip"+whichNation, index);
                            shopButtons[index].setChecked(true);
                            //REMEMBER THAT!
                            CykaGame.prefs.flush();
                        }
                        else
                            sounds.cantBuySound.play(volume);
                    }
                    else {
                        if (!shopButtons[index].isChecked()) {
                            sounds.buttonClickSound.play(volume);
                            shopButtons[CykaGame.prefs.getInteger("whichShip"+whichNation, 0)].setChecked(false);
                            shopButtons[index].setChecked(true);
                            CykaGame.prefs.putInteger("whichShip"+whichNation, index);
                        }
                        else {
                            shopButtons[index].setChecked(true);
                            CykaGame.prefs.putInteger("whichShip"+whichNation, index);
                        }
                        CykaGame.prefs.flush();
                    }

                }
            });
        }
        int checked = CykaGame.prefs.getInteger("whichShip" + whichNation, 0);
        shopButtons[checked].setChecked(true);
        CykaGame.prefs.putInteger("whichShip"+whichNation, checked);
        CykaGame.prefs.flush();
    }

}
