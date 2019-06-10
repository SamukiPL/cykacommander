package me.samuki.cykacommander;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Iterator;

class GameScreen implements Screen {
    private CykaGame game;
    private GameBasic basic;
    //SOUND
    private SoundsBase sounds;
    private float volume;
    //INPUT PROCESSOR
    private Stage stage;
    //BACKGROUND
    private Texture background, dickMars, cykaEarth, cykaPlanet;
    private float marsX, marsY;
    private float earthX, earthY;
    private float planetX, planetY;
    private Actor leftControl, rightControl, shotControl;
    private float gameSpeed, shipSpeed, shipAcceleration;
    private boolean canILeft = false;
    private boolean canIRight = false;
    //CONTROLS TEXTURES
    private Texture[] controlsTexture;
    private Texture joystickTexture;
    private Texture shotButtonTexture;
    private boolean controlsOn;
    //HITBOX COMPONENTS
    private Rectangle bucket;
    //BULLET
    private Rectangle bulletRect;
    private boolean shotFired;
    private int bulletsMagazine;
    //PIPES COORDINATES
    private Array<Rectangle> frontPipes, backPipes;
    private Array<Boolean> pipeGone;
    private boolean makePipe;
    private int pipeY;
    //POINTS COORDINATES
    private Rectangle pointRect;
    private Texture point;
    private boolean pointHit;
    private int howManyCash;
    //POINTS/SCORE
    static int points;

    GameScreen(CykaGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        basic = new GameBasic();
        basic.loadSprites();
        //SOUNDS
        sounds = new SoundsBase(0);

        if(CykaGame.prefs.getBoolean("sound", true))
            volume = 1f;
        else
            volume = 0;
        //VIEWPORT
        FitViewport viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //BACKGROUND
        background = new Texture("stardust_experiment.png");
        dickMars = new Texture("planets/dickmars.png");
        cykaEarth = new Texture("planets/cykaearth.png");
        cykaPlanet = new Texture("planets/cykaplanet.png");
        marsX = CykaGame.prefs.getFloat("mars_x", 950);
        earthX = CykaGame.prefs.getFloat("earth_x", 342);
        planetX = CykaGame.prefs.getFloat("planet_x", 750);
        //CONTROLS SET
        int setControlsPrefs = CykaGame.prefs.getInteger("which_control");
        shotControl = new Actor();
        setControls(setControlsPrefs);
        stage.addActor(shotControl);

        controlsTexture = new Texture[3];
        for(int i = 0; i < 3; i++) {
            Texture tmpControl = new Texture("control_"+i+".png");
            controlsTexture[i] = tmpControl;
        }
        joystickTexture = new Texture("joystick.png");
        shotButtonTexture = new Texture("shot_button.png");

        controlsOn = CykaGame.prefs.getBoolean("controls_on", true);
        if(CykaGame.prefs.getInteger("plays_counter", 0) == 0)
            controlsOn = true;
        //GAME SETTINGS
        gameSpeed = 25000;//12000;
        shipSpeed = 0;
        points = 0;

        //PIPES ARRAYS
        frontPipes = new Array<Rectangle>();
        backPipes = new Array<Rectangle>();
        pipeGone = new Array<Boolean>();
        basic.spawnLine(1086, -220, frontPipes, backPipes, pipeGone);
        makePipe = true;
        pipeY = 1729;
        //POINTS
        pointRect = basic.spawnPoint(1629);
        point = new Texture("coin.png");
        pointHit = false;
        howManyCash = 1;

        bucket = new Rectangle(640/2-128/2,10,128,128);

        basic.shipSpriteChange(1);

        Gdx.input.setCatchBackKey(true);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLogic();
        game.batch.begin();
        game.batch.draw(background,0,0);
        planetDraw();
        if(controlsOn)
            controlsDraw();
        game.batch.draw(basic.shipCurrentFrame,bucket.x,bucket.y, 128, 128);
        if(!pointHit)
            game.batch.draw(point, pointRect.x, pointRect.y, 50, 50);
        basic.pipeDraw(frontPipes, backPipes, game);
        if(shotFired)
            basic.bulletDraw(game, bulletRect.x, bulletRect.y);
        basic.numbersDraw(points, game);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
    }

    @Override
    public void resize(int width, int height) {    }

    @Override
    public void pause() {    }

    @Override
    public void resume() {    }

    @Override
    public void hide() {    }

    @Override
    public void dispose() {
        stage.dispose();
        sounds.dispose();
        basic.dispose();
        for (Texture aControlsTexture : controlsTexture) {
            aControlsTexture.dispose();
        }
        joystickTexture.dispose();
        background.dispose();
        dickMars.dispose();
        cykaEarth.dispose();
        cykaPlanet.dispose();
    }

    private void gameLogic() {
        //HITBOX SETTINGS
        Circle eggLeft = new Circle(bucket.x + 32, bucket.y + 26, 26);
        Circle eggRight = new Circle(bucket.x + 95, bucket.y + 26, 26);
        Rectangle engines = new Rectangle(bucket.x + 16, bucket.y + 52, 95, 11);
        Rectangle body = new Rectangle(bucket.x + 48, bucket.y + 64, 31, 56);
        //SHIP MOVEMENT
        if(bucket.x >= 0 && bucket.x <= 512)
            shipAcceleration += 120 * (Gdx.graphics.getDeltaTime() / 60);
            bucket.x += (shipSpeed*((float)1+shipAcceleration)) * (Gdx.graphics.getDeltaTime() / 60);
        if(bucket.x < 0) {
            bucket.x = 0;
            basic.shipSpriteChange(1);
        }
        if(bucket.x > 512) {
            bucket.x = 512;
            basic.shipSpriteChange(1);
        }

        //PIPES WORK
        Iterator<Rectangle> frontIter = frontPipes.iterator();
        Iterator<Rectangle> backIter = backPipes.iterator();
        Iterator<Boolean> pipeGoneIter = pipeGone.iterator();
        while(frontIter.hasNext()){
            Rectangle frontPipe = frontIter.next();
            Rectangle backPipe = backIter.next();
            boolean isPipeGone = pipeGoneIter.next();
            if(makePipe) {
                basic.spawnLine(pipeY, (int)frontPipe.width, frontPipes, backPipes, pipeGone);
                pipeY = 1222;
                makePipe = false;
            }
            frontPipe.y -= gameSpeed*(Gdx.graphics.getDeltaTime()/60);
            backPipe.y = frontPipe.y;
            if(frontPipe.y < 35 && !isPipeGone) {
                pipeGone.insert(0, true);
                sounds.pointSound.play(volume);
                points++;
                bulletsMagazine = 1;
            }
            if(frontPipe.y < -64) {
                makePipe = true;
                frontIter.remove();
                backIter.remove();
                pipeGoneIter.remove();
                //CASH
                if(points%5 == 0)
                    howManyCash += 1;
            }
            if (basic.hitbox(eggLeft, eggRight, frontPipe, backPipe, engines, body)) {
                sounds.deathSound.play(volume);
                CykaGame.prefs.putFloat("mars_x", marsX);
                CykaGame.prefs.putFloat("earth_x", earthX);
                CykaGame.prefs.putFloat("planet_x", planetX);
                CykaGame.prefs.flush();
                game.setScreen(new DeathScreen(game));
            }

            if(shotFired) {
                bulletRect.y += gameSpeed*3*(Gdx.graphics.getDeltaTime()/60);
                if(Intersector.overlaps(bulletRect, pointRect)) {
                    sounds.coinHitSound.play(volume);
                    pointHit = true;
                    shotFired = false;
                    int cash = CykaGame.prefs.getInteger("cash", 0)+howManyCash;
                    CykaGame.prefs.putInteger("cash", cash);
                    CykaGame.prefs.flush();
                }
                if(Intersector.overlaps(bulletRect, frontPipe) || Intersector.overlaps(bulletRect, backPipe) || bulletRect.y >= 1074) {
                    shotFired = false;
                }


            }
        }
        //POINTS
        pointRect.y -= gameSpeed*(Gdx.graphics.getDeltaTime()/60);
        if(pointRect.y <= -50) {
            pointRect = basic.spawnPoint(1222);
            pointHit = false;
        }
        if(points < 30)
            gameSpeed += 60000*Gdx.graphics.getDeltaTime()/60;
    }

    private void setControls(int whichControls) {
        // 0 = CLASSIC
        // 1 = LEFT JOYSTICK
        // 2 = RIGHT JOYSTICK
        // 3 = CENTER
        if(whichControls == 0) {
            shotControl.setPosition(0,288);
            shotControl.setSize(640,774);

            leftControl = new Actor();
            leftControl.setPosition(0,0);
            leftControl.setSize(320,288);
            stage.addActor(leftControl);

            rightControl = new Actor();
            rightControl.setPosition(320,0);
            rightControl.setSize(321,288);
            stage.addActor(rightControl);
        }
        else if(whichControls == 1) {
            shotControl.setPosition(360, 0);
            shotControl.setSize(280, 280);

            leftControl = new Actor();
            leftControl.setPosition(0,0);
            leftControl.setSize(160,300);
            stage.addActor(leftControl);

            rightControl = new Actor();
            rightControl.setPosition(160,0);
            rightControl.setSize(160,300);
            stage.addActor(rightControl);
        }
        else if(whichControls == 2) {
            shotControl.setPosition(0, 0);
            shotControl.setSize(280, 280);

            leftControl = new Actor();
            leftControl.setPosition(320,0);
            leftControl.setSize(160,300);
            stage.addActor(leftControl);

            rightControl = new Actor();
            rightControl.setPosition(480,0);
            rightControl.setSize(160,300);
            stage.addActor(rightControl);
        }
        else if(whichControls == 3) {
            leftControl = new Actor();
            leftControl.setPosition(0,0);
            leftControl.setSize(320,288);
            stage.addActor(leftControl);

            rightControl = new Actor();
            rightControl.setPosition(320,0);
            rightControl.setSize(321,288);
            stage.addActor(rightControl);

            shotControl.setPosition(220,44);
            shotControl.setSize(200,200);
        }

        rightControl.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                shipAcceleration = 0;
                shipSpeed = gameSpeed;
                basic.shipSpriteChange(0);
                canIRight = true;
            }

            public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(!canILeft) {
                    shipSpeed = 0;
                    basic.shipSpriteChange(1);
                }
                canIRight = false;
            }
        });
        leftControl.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                shipAcceleration = 0;
                shipSpeed = -gameSpeed;
                basic.shipSpriteChange(2);
                canILeft = true;
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(!canIRight) {
                    shipSpeed = 0;
                    basic.shipSpriteChange(1);
                }
                canILeft = false;
            }
        });

        shotFired = false;
        bulletsMagazine = 1;
        shotControl.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!shotFired && bulletsMagazine > 0) {
                    sounds.shotSound.play(volume);
                    bulletRect = basic.spawnBullet(bucket.x);
                    shotFired = true;
                    bulletsMagazine = 0;
                }
            }
        });
    }

    private void controlsDraw() {
        //CONTROLS COLOR...
        Color color = game.batch.getColor();
        float oldAlpha = color.a;
        color.a = 0.4f;
        game.batch.setColor(color);
        game.batch.draw(joystickTexture, leftControl.getX(), leftControl.getY() + 15, leftControl.getWidth()+rightControl.getWidth(), 288 - 15);
        color.a = 0.6f;
        game.batch.setColor(color);
        game.batch.draw(shotButtonTexture, shotControl.getX() + 45, shotControl.getY() + 45, shotControl.getWidth() - 90, shotControl.getHeight() - 90);
        color.a = oldAlpha;
        game.batch.setColor(color);
    }

    private void planetLogic() {
        //MARS MOVING
        if(marsX <= -110)
            marsX = 1490;
        marsX -= Math.ceil(Gdx.graphics.getDeltaTime())/40;
        marsY = (float)(Math.sqrt((900*900) - (marsX - 590)*(marsX - 590)));
        //EARTH MOVING
        if(earthX <= -89) {
            earthX = 1283;
        }
        earthX -= Math.ceil(Gdx.graphics.getDeltaTime())/40;
        earthY = (float)(Math.sqrt((700*700) - (earthX - 597)*(earthX - 597)))-100;
        //PLANET MOVING
        if(planetX <= 157) {
            planetX = 957;
        }
        planetX -= Math.ceil(Gdx.graphics.getDeltaTime())/40;
        planetY = (float)(Math.sqrt((450*450) - (planetX - 607)*(planetX - 607)))-100;
    }

    private void planetDraw() {
        planetLogic();
        game.batch.draw(dickMars, marsX, marsY, 100, 100);
        game.batch.draw(cykaEarth, earthX, earthY, 86, 86);
        game.batch.draw(cykaPlanet, planetX, planetY, 66, 66);
    }
}
