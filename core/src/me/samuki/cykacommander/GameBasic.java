package me.samuki.cykacommander;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameBasic {
    //SPRITE
    private static final int SPRITE_COLS = 1;
    private static final int SPRITE_ROWS = 3;
    //SHIP ANIMATION
    Animation shipAnimation;
    TextureRegion[] shipFrames;
    public TextureRegion shipCurrentFrame;
    //PIPE SPRITE
    public Animation pipeAnimation;
    TextureRegion[] pipeFrames;
    public TextureRegion pipeCurrentFrame;
    //NUMBERS
    private static final int NUMBERS_COLS = 5;
    private static final int NUMBERS_ROWS = 2;
    public static Animation numbersAnimation;
    TextureRegion[] numbersFrames;
    public static TextureRegion numbersCurrentFrame;
    public Texture hudPoints;
    public static int points;

    public GameBasic() {
        //SHIP ANIMATION

        Texture tmpTexture = new Texture("ship_sprites/cykamove.png");
        TextureRegion[][] tmp = TextureRegion.split(tmpTexture, tmpTexture.getWidth(), tmpTexture.getHeight()/SPRITE_ROWS);
        shipFrames = new TextureRegion[SPRITE_COLS*SPRITE_ROWS];
        for (int i = 0; i < SPRITE_ROWS; i++){
            for (int j = 0; j < SPRITE_COLS; j++){
                shipFrames[i] = tmp[i][j];
            }
        }
        shipAnimation = new Animation(1f,shipFrames);


        //PIPE SPRITE
        tmpTexture = new Texture("cykapipe.png");
        tmp = TextureRegion.split(tmpTexture, tmpTexture.getWidth(), tmpTexture.getHeight()/SPRITE_ROWS);
        pipeFrames = new TextureRegion[SPRITE_COLS*SPRITE_ROWS];
        for (int i = 0; i < SPRITE_ROWS; i++){
            for(int j = 0; j < SPRITE_COLS; j++){
                pipeFrames[i] = tmp[i][j];
            }
        }
        pipeAnimation = new Animation(1f,pipeFrames);

        //NUMBERS BITMAP
        hudPoints = new Texture("frame.png");
        tmpTexture = new Texture("numbers_bitmap.png");
        tmp = TextureRegion.split(tmpTexture, tmpTexture.getWidth()/NUMBERS_COLS, tmpTexture.getHeight()/NUMBERS_ROWS);
        numbersFrames = new TextureRegion[NUMBERS_COLS*NUMBERS_ROWS];
        int index = 0;
        for(int i = 0; i < NUMBERS_ROWS; i++) {
            for(int j = 0; j < NUMBERS_COLS; j++) {
                numbersFrames[index++] = tmp[i][j];
            }
        }
        numbersAnimation = new Animation(1f, numbersFrames);
    }

    public void spawnLine(float y, Array<Rectangle> frontPipes, Array<Rectangle> backPipes) {
        int frontWidth = MathUtils.random(640-128-50);
        Rectangle frontPipe = new Rectangle(0,y,frontWidth,50);
        Rectangle backPipe = new Rectangle(frontWidth+128+50,y,462-frontWidth,50);
        frontPipes.add(frontPipe);
        backPipes.add(backPipe);
    }
    public boolean hitbox(Circle eggLeft, Circle eggRight, Rectangle...rects ) {
        return  Intersector.overlaps(eggLeft, rects[0]) ||
                Intersector.overlaps(eggRight, rects[1]) ||
                Intersector.overlaps(rects[2], rects[0]) ||
                Intersector.overlaps(rects[2], rects[1]) ||
                Intersector.overlaps(rects[3], rects[0]) ||
                Intersector.overlaps(rects[3], rects[1]);
        //rects[0] = frontPipe, rects[1] = backPipe, rects[2] = engines, rects[3] = body
    }
    public void pipeDraw(Array<Rectangle> frontPipes, Array<Rectangle> backPipes, CykaGame game) {
        //PIPES DRAWING
        for (Rectangle frontPipe: frontPipes) {
            pipeCurrentFrame = pipeAnimation.getKeyFrame(2, true);
            game.batch.draw(pipeCurrentFrame, frontPipe.width-64, frontPipe.y-7);
            for(float i = frontPipe.width-128; i > -64 ; i-=64) {
                pipeCurrentFrame = pipeAnimation.getKeyFrame(1, true);
                game.batch.draw(pipeCurrentFrame, i, frontPipe.y-7);
            }
        }
        for(Rectangle backPipe: backPipes) {
            pipeCurrentFrame = pipeAnimation.getKeyFrame(0, true);
            game.batch.draw(pipeCurrentFrame, backPipe.x, backPipe.y-7);
            for(float i = backPipe.x+64; i < 640; i+=64) {
                pipeCurrentFrame = pipeAnimation.getKeyFrame(1, true);
                game.batch.draw(pipeCurrentFrame, i, backPipe.y-7);
            }
        }
    }
    public void shipSpriteChange(int a) {
        shipCurrentFrame = shipAnimation.getKeyFrame(a, true);
    }
    public void numbersDraw(int points, CykaGame game) {
        int tensPlace = points/10;
        int onesPlace = points-(tensPlace*10);
        numbersCurrentFrame = numbersAnimation.getKeyFrame(tensPlace, true);
        game.batch.draw(numbersCurrentFrame, 592, 958, 24, 42);
        numbersCurrentFrame = numbersAnimation.getKeyFrame(onesPlace, true);
        game.batch.draw(numbersCurrentFrame, 616, 958, 24, 42);
        game.batch.draw(hudPoints, 587, 953);
    }
    //DEATH SCREEN
    public void getPoints(int points) {
        GameBasic.points = points;
    }
    public void dispose() {
        hudPoints.dispose();
    }
}
