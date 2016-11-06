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
    private GameBasic basicThings;
    private SoundsBase sounds;
    //INPUT PROCESSOR
    private Stage stage;
    //CONTROLS
    private Actor leftControl, rightControl, shotControl;
    private float gameSpeed, shipSpeed, shipAcceleration;
    private boolean canILeft = false;
    private boolean canIRight = false;
    private Texture[] controlsTexture;
    //HITBOX COMPONENTS
    private Rectangle bucket, engines, body;
    private Circle eggLeft, eggRight;
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
        basicThings = new GameBasic();
        sounds = new SoundsBase();
        //VIEWPORT
        FitViewport viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //CONTROLS
        shotControl = new Actor();
        shotControl.setPosition(0,250);
        shotControl.setSize(640,1024);
        stage.addActor(shotControl);

        leftControl = new Actor();
        leftControl.setPosition(0,0);
        leftControl.setSize(320,250);
        stage.addActor(leftControl);

        rightControl = new Actor();
        rightControl.setPosition(320,0);
        rightControl.setSize(321,250);
        stage.addActor(rightControl);

        controlsTexture = new Texture[3];
        for(int i = 0; i < 3; i++) {
            Texture tmpControl = new Texture("control_"+i+".png");
            controlsTexture[i] = tmpControl;
        }
        //GAME SETTINGS
        gameSpeed = 12000;
        shipSpeed = 0;
        points = 0;

        //PIPES ARRAYS
        frontPipes = new Array<Rectangle>();
        backPipes = new Array<Rectangle>();
        basicThings.spawnLine(1086, -220, frontPipes, backPipes);
        makePipe = true;
        pipeY = 1729;
        //POINTS
        pointRect = basicThings.spawnPoint(1629);
        point = new Texture("on_button.png");
        pointHit = false;
        howManyCash = 1;

        bucket = new Rectangle(640/2-128/2,10,128,128);

        basicThings.shipSpriteChange(1);
        //CONTROLS SET
        shotFired = false;
        shotControl.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!shotFired) {
                    bulletRect = basicThings.spawnBullet(bucket.x);
                    shotFired = true;
                }
            }
        });
        rightControl.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                shipAcceleration = 0;
                shipSpeed = gameSpeed;
                basicThings.shipSpriteChange(0);
                canIRight = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(!canILeft) {
                    shipSpeed = 0;
                    basicThings.shipSpriteChange(1);
                }
                canIRight = false;
            }
        });
        leftControl.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                shipAcceleration = 0;
                shipSpeed = -gameSpeed;
                basicThings.shipSpriteChange(2);
                canILeft = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                if(!canIRight) {
                    shipSpeed = 0;
                    basicThings.shipSpriteChange(1);
                }
                canILeft = false;
            }
        });
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

        gameLogic();
        game.batch.begin();
        if(CykaGame.prefs.getBoolean("controls_on", true))
            controlsDraw();
        game.batch.draw(basicThings.shipCurrentFrame,bucket.x,bucket.y);
        if(!pointHit)
            game.batch.draw(point, pointRect.x, pointRect.y, 50, 50);
        basicThings.pipeDraw(frontPipes, backPipes, game);
        if(shotFired)
            basicThings.bulletDraw(game, bulletRect.x, bulletRect.y);
        basicThings.numbersDraw(points, game);
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
        basicThings.dispose();
        for (Texture aControlsTexture : controlsTexture) {
            aControlsTexture.dispose();
        }
    }

    private void gameLogic() {
        //HITBOX SETTINGS
        eggLeft = new Circle(bucket.x+32, bucket.y+26, 26);
        eggRight = new Circle(bucket.x+95, bucket.y+26, 26);
        engines = new Rectangle(bucket.x+16, bucket.y+52, 95, 11);
        body = new Rectangle(bucket.x+48, bucket.y+64, 31, 56);
        //SHIP MOVEMENT
        if(bucket.x >= 0 && bucket.x <= 512)
            shipAcceleration += 120 * (Gdx.graphics.getDeltaTime() / 60);
            bucket.x += (shipSpeed*((float)1+shipAcceleration)) * (Gdx.graphics.getDeltaTime() / 60);
        if(bucket.x < 0) {
            bucket.x = 0;
            basicThings.shipSpriteChange(1);
        }
        if(bucket.x > 512) {
            bucket.x = 512;
            basicThings.shipSpriteChange(1);
        }

        //PIPES WORK
        Iterator<Rectangle> frontIter = frontPipes.iterator();
        Iterator<Rectangle> backIter = backPipes.iterator();
        while(frontIter.hasNext()){
            Rectangle frontPipe = frontIter.next();
            Rectangle backPipe = backIter.next();
            if(makePipe) {
                basicThings.spawnLine(pipeY, (int)frontPipe.width, frontPipes, backPipes);
                pipeY = 1122;
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
                howManyCash = 1+(points/10);
            }
            if (basicThings.hitbox(eggLeft, eggRight, frontPipe, backPipe, engines, body)) {
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
                }
                if(Intersector.overlaps(bulletRect, frontPipe) || Intersector.overlaps(bulletRect, backPipe) || bulletRect.y >= 1074) {
                    shotFired = false;
                }


            }
        }
        //POINTS
        pointRect.y -= gameSpeed*(Gdx.graphics.getDeltaTime()/60);
        if(pointRect.y <= -50) {
            pointRect = basicThings.spawnPoint(1122);
            pointHit = false;
        }
        gameSpeed += 60000*Gdx.graphics.getDeltaTime()/60;
    }

    private void controlsDraw() {
        //CONTROLS COLOR...
        Color color = game.batch.getColor();
        float oldAlpha = color.a;
        color.a = 0.2f;
        game.batch.setColor(color);
        game.batch.draw(controlsTexture[0], leftControl.getX(), leftControl.getY(), leftControl.getWidth(), leftControl.getHeight());
        game.batch.draw(controlsTexture[1], rightControl.getX(), rightControl.getY(), rightControl.getWidth(), rightControl.getHeight());
        game.batch.draw(controlsTexture[2], shotControl.getX(), shotControl.getY(), shotControl.getWidth(), shotControl.getHeight());
        color.a = oldAlpha;
        game.batch.setColor(color);
    }
}
