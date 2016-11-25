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
    private SoundsBase sounds;
    //INPUT PROCESSOR
    private Stage stage;
    //CONTROLS
    private int setControls;
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
    //PIPES COORDINATES
    private Array<Rectangle> frontPipes, backPipes;
    private boolean makePipe;
    private int pipeY;
    //POINTS COORDINATES
    private Rectangle pointRect;
    private Texture point;
    private boolean pointHit;
    private int howManyCash;
    //POINTS/SCORE
    static int points;
    //SOUND
    private float volume;

    GameScreen(CykaGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        basic = new GameBasic();
        basic.loadSprites();
        sounds = new SoundsBase();
        //VIEWPORT
        FitViewport viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //CONTROLS SET
        setControls = CykaGame.prefs.getInteger("which_control");
        shotControl = new Actor();
        controls(setControls);
        stage.addActor(shotControl);

        shotFired = false;
        shotControl.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!shotFired) {
                    bulletRect = basic.spawnBullet(bucket.x);
                    shotFired = true;
                }
            }
        });

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
        basic.spawnLine(1086, -220, frontPipes, backPipes);
        makePipe = true;
        pipeY = 1729;
        //POINTS
        pointRect = basic.spawnPoint(1629);
        point = new Texture("on_button.png");
        pointHit = false;
        howManyCash = 1;

        bucket = new Rectangle(640/2-128/2,10,128,128);

        basic.shipSpriteChange(1);

        Gdx.input.setCatchBackKey(true);

        //SOUNDS
        if(CykaGame.prefs.getBoolean("sound", true))
            volume = 1f;
        else
            volume = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controlsRender(setControls);
        gameLogic();
        game.batch.begin();
        if(controlsOn)
            controlsDraw(setControls);
        game.batch.draw(basic.shipCurrentFrame,bucket.x,bucket.y, 128, 128);
        if(!pointHit)
            game.batch.draw(point, pointRect.x, pointRect.y, 50, 50);
        basic.pipeDraw(frontPipes, backPipes, game);
        if(shotFired)
            basic.bulletDraw(game, bulletRect.x, bulletRect.y);
        basic.numbersDraw(points, game);
        game.batch.end();
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
        while(frontIter.hasNext()){
            Rectangle frontPipe = frontIter.next();
            Rectangle backPipe = backIter.next();
            if(makePipe) {
                basic.spawnLine(pipeY, (int)frontPipe.width, frontPipes, backPipes);
                pipeY = 1222;
                makePipe = false;
            }
            frontPipe.y -= gameSpeed*(Gdx.graphics.getDeltaTime()/60);
            backPipe.y = frontPipe.y;
            if(frontPipe.y < -64) {
                makePipe = true;

                frontIter.remove();
                backIter.remove();
                points++;
                //CASH
                howManyCash = 1+(points/5);
            }
            if (basic.hitbox(eggLeft, eggRight, frontPipe, backPipe, engines, body)) {
                sounds.deathSound.play(volume);
                game.setScreen(new DeathScreen(game));
            }

            if(shotFired) {
                bulletRect.y += gameSpeed*3*(Gdx.graphics.getDeltaTime()/60);
                if(Intersector.overlaps(bulletRect, pointRect)) {
                    pointHit = true;
                    shotFired = false;
                    int cash = CykaGame.prefs.getInteger("cash", 0)+howManyCash;
                    CykaGame.prefs.putInteger("cash", cash);
                    CykaGame.prefs.flush();
                    System.out.println(CykaGame.prefs.getInteger("cash", 0));
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

    private void controlsDraw(int whichControls) {
        //CONTROLS COLOR...
        if(whichControls == 0) {
            Color color = game.batch.getColor();
            float oldAlpha = color.a;
            color.a = 0.2f;
            game.batch.setColor(color);
            game.batch.draw(joystickTexture, 32, 5, 576, 288);
            game.batch.draw(controlsTexture[2], shotControl.getX(), shotControl.getY(), shotControl.getWidth(), shotControl.getHeight());
            color.a = oldAlpha;
            game.batch.setColor(color);
        }
        if(whichControls == 1) {
            Color color = game.batch.getColor();
            float oldAlpha = color.a;
            color.a = 0.2f;
            game.batch.setColor(color);
            game.batch.draw(joystickTexture, 10, 40, 480, 240);
            game.batch.draw(shotButtonTexture, 521, 116, 87, 87);
            color.a = oldAlpha;
            game.batch.setColor(color);
        }
        if(whichControls == 2) {
            Color color = game.batch.getColor();
            float oldAlpha = color.a;
            color.a = 0.2f;
            game.batch.setColor(color);
            game.batch.draw(joystickTexture, 140, 40, 480, 240);
            game.batch.draw(shotButtonTexture, 30, 116, 87, 87);
            color.a = oldAlpha;
            game.batch.setColor(color);
        }
    }

    private void controls(int whichControls) {
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

                rightControl.addListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        shipAcceleration = 0;
                        shipSpeed = gameSpeed;
                        basic.shipSpriteChange(0);
                        canIRight = true;
                        return true;
                    }

                    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                        if(!canILeft) {
                            shipSpeed = 0;
                            basic.shipSpriteChange(1);
                        }
                        canIRight = false;
                    }
                });
                leftControl.addListener(new InputListener() {
                    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                        shipAcceleration = 0;
                        shipSpeed = -gameSpeed;
                        basic.shipSpriteChange(2);
                        canILeft = true;
                        return true;
                    }

                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    if(!canIRight) {
                        shipSpeed = 0;
                        basic.shipSpriteChange(1);
                    }
                    canILeft = false;
                }
                });
        }
        else if(whichControls == 1) {
            shotControl.setPosition(474, 0);
            shotControl.setSize(280, 280);
        }
        else if(whichControls == 2) {
            shotControl.setPosition(0, 0);
            shotControl.setSize(166, 280);
        }
    }
    private void controlsRender(int whichControls) {
        //CONTROLS 1
        if(whichControls == 1) {
            int lastPointer = 0;
            for (int i = 0; i < 6; i++) {
                if (Gdx.input.isTouched(i)) {
                    lastPointer = i;
                }
            }
            if(Gdx.input.justTouched()) {
                if(Gdx.input.getX(lastPointer) <= 800 && Gdx.input.getY(lastPointer) >= 1220)
                    shipAcceleration = 0;
            }
            if(Gdx.input.isTouched()) {
                if(Gdx.input.getX(lastPointer) <= 400 && Gdx.input.getY(lastPointer) >= 1220) {
                    if(canILeft)
                        shipAcceleration = 0;
                    canILeft = false;
                    canIRight = true;
                    shipSpeed = -gameSpeed;
                    basic.shipSpriteChange(2);
                }
                if(Gdx.input.getX(lastPointer) > 400 && Gdx.input.getX(lastPointer) <= 800 && Gdx.input.getY(lastPointer) >= 1220) {
                    if(canIRight)
                        shipAcceleration = 0;
                    canIRight = false;
                    canILeft = true;
                    shipSpeed = gameSpeed;
                    basic.shipSpriteChange(0);
                }
            }
            else {
                shipSpeed = 0;
                basic.shipSpriteChange(1);
            }
        }
        //CONTROLS 2
        else if(whichControls == 2) {
            int lastPointer = 0;
            for (int i = 0; i < 6; i++) {
                if (Gdx.input.isTouched(i)) {
                    lastPointer = i;
                }
            }
            if(Gdx.input.justTouched()) {
                if(Gdx.input.getX(lastPointer) >= 280 && Gdx.input.getY(lastPointer) >= 1220)
                    shipAcceleration = 0;
            }
            if(Gdx.input.isTouched()) {
                if(Gdx.input.getX(lastPointer) >= 280 && Gdx.input.getX(lastPointer) < 680 && Gdx.input.getY(lastPointer) >= 1220) {
                    if(canILeft)
                        shipAcceleration = 0;
                    canILeft = false;
                    canIRight = true;
                    shipSpeed = -gameSpeed;
                    basic.shipSpriteChange(2);
                }
                if(Gdx.input.getX(lastPointer) > 680 && Gdx.input.getX(lastPointer) <= 1080 && Gdx.input.getY(lastPointer) >= 1220) {
                    if(canIRight)
                        shipAcceleration = 0;
                    canIRight = false;
                    canILeft = true;
                    shipSpeed = gameSpeed;
                    basic.shipSpriteChange(0);
                }
            }
            else {
                shipSpeed = 0;
                basic.shipSpriteChange(1);
            }
        }
    }
}
