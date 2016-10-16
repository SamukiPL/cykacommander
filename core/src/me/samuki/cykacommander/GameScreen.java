package me.samuki.cykacommander;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Iterator;

public class GameScreen implements Screen {
    CykaGame game;
    GameBasic basicThings;
    SoundsBase sounds;
    //INPUT PROCESSOR
    Stage stage;
    FitViewport viewport;
    //CONTROLS
    Actor leftControl, rightControl;
    float gameSpeed, shipSpeed, shipAcceleration;
    boolean canILeft = false;
    boolean canIRight = false;
    //HITBOX COMPONENTS
    Rectangle bucket, engines, body;
    Circle eggLeft, eggRight;
    //PIPES COORDINATES
    private Array<Rectangle> frontPipes, backPipes;
    //POINTS
    public static int points;
    //SOUND
    float volume;

    public GameScreen(CykaGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        basicThings = new GameBasic();
        sounds = new SoundsBase();
        //VIEWPORT
        viewport = new FitViewport(CykaGame.SCREEN_WIDTH, CykaGame.SCREEN_HEIGHT, game.camera);
        viewport.setScaling(Scaling.stretch);
        //STAGE
        stage = new Stage(viewport,game.batch);
        Gdx.input.setInputProcessor(stage);
        //CONTROLS
        leftControl = new Actor();
        leftControl.setPosition(0,0);
        leftControl.setSize(320,1024);
        rightControl = new Actor();
        rightControl.setPosition(320,0);
        rightControl.setSize(321,1024);
        stage.addActor(leftControl);
        stage.addActor(rightControl);
        //GAME SETTINGS
        gameSpeed = 12000;
        shipSpeed = 0;
        points = 0;

        //PIPES ARRAYS
        frontPipes = new Array<Rectangle>();
        backPipes = new Array<Rectangle>();
        basicThings.spawnLine(1086, frontPipes, backPipes);
        basicThings.spawnLine(1629, frontPipes, backPipes);

        bucket = new Rectangle(640/2-128/2,10,128,128);

        basicThings.shipSpriteChange(1);
        //CONTROLS SET
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
        if(game.prefs.getBoolean("sound", true))
            volume = 1f;
        else
            volume = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLogic();
        basicThings.getPoints(points);

        game.batch.begin();
        game.batch.draw(basicThings.shipCurrentFrame,bucket.x,bucket.y);
        basicThings.pipeDraw(frontPipes, backPipes, game);
        basicThings.numbersDraw(points, game);
        game.batch.end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/60f));
        stage.draw();
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
    }

    public void gameLogic() {
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
            frontPipe.y -= gameSpeed*(Gdx.graphics.getDeltaTime()/60);
            backPipe.y = frontPipe.y;
            if(frontPipe.y < -64) {
                frontIter.remove();
                backIter.remove();
                basicThings.spawnLine(1022, frontPipes, backPipes);
                points++;
            }
            if (basicThings.hitbox(eggLeft, eggRight, frontPipe, backPipe, engines, body)) {
                sounds.deathSound.play(volume);
                game.setScreen(new DeathScreen(game));
            }
        }
        gameSpeed += 60000*Gdx.graphics.getDeltaTime()/60;
    }
}
