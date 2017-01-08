package me.samuki.cykacommander;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

class GameBasic {
    //SPRITE
    private static final int SPRITE_COLS = 1;
    private static final int SPRITE_ROWS = 3;
    //SHIP ANIMATION
    private Animation shipAnimation;
    TextureRegion shipCurrentFrame;
    //BULLET SPRITE
    private Texture bulletSprite;
    //PIPE SPRITE
    private Animation pipeAnimation;
    //NUMBERS
    private static final int NUMBERS_COLS = 5;
    private static final int NUMBERS_ROWS = 2;
    private static Animation numbersAnimation;
    private Texture hudPoints;

    void loadSprites() {
        //SHIP ANIMATION
        int whichShip = CykaGame.prefs.getInteger("whichShip", 0);
        shipAnimation = spriteCutting("ship_sprites/rus/ship_sprite_"+whichShip+".png", SPRITE_COLS, SPRITE_ROWS);

        //BULLET SPRITE
        bulletSprite = new Texture("use_button_0.png");

        //PIPE SPRITE
        pipeAnimation = spriteCutting("cykapipe.png", SPRITE_COLS, SPRITE_ROWS);

        //NUMBERS BITMAP
        hudPoints = new Texture("frame.png");
        numbersAnimation = spriteCutting("numbers_bitmap.png", NUMBERS_COLS, NUMBERS_ROWS);
    }
    //SPAWNS
    void spawnLine(float y, int lastWidth, Array<Rectangle> frontPipes, Array<Rectangle> backPipes, Array<Boolean> pipeGone) {
        int frontWidth = MathUtils.random(640-128-50);
        while(frontWidth > (lastWidth-200) && frontWidth < (lastWidth+200)) {
            frontWidth = MathUtils.random(640-128-50);
        }
        Rectangle frontPipe = new Rectangle(0,y,frontWidth,50);
        Rectangle backPipe = new Rectangle(frontWidth+128+50,y,462-frontWidth,50);
        frontPipes.add(frontPipe);
        backPipes.add(backPipe);
        pipeGone.add(false);
    }
    Rectangle spawnPoint(float y) {
        int pointPossition = MathUtils.random(300);
        return new Rectangle(pointPossition, y, 50, 50);
    }
    Rectangle spawnBullet(float x) {
     return  new Rectangle(x+(128/2)-(50/2), 128, 50, 50);
    }
    //HITBOXES
    boolean hitbox(Circle eggLeft, Circle eggRight, Rectangle... rects) {
        return  Intersector.overlaps(eggLeft, rects[0]) ||
                Intersector.overlaps(eggRight, rects[1]) ||
                Intersector.overlaps(rects[2], rects[0]) ||
                Intersector.overlaps(rects[2], rects[1]) ||
                Intersector.overlaps(rects[3], rects[0]) ||
                Intersector.overlaps(rects[3], rects[1]);
        //rects[0] = frontPipe, rects[1] = backPipe, rects[2] = engines, rects[3] = body
    }
    //DRAWS
    void pipeDraw(Array<Rectangle> frontPipes, Array<Rectangle> backPipes, CykaGame game) {
        //PIPES DRAWING
        TextureRegion pipeCurrentFrame;
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
    void bulletDraw(CykaGame game, float x, float y) {
            game.batch.draw(bulletSprite, x, y, 50, 50);
    }
    //SPRITES
    static Animation spriteCutting(String textureName, int cols, int rows) {
        Texture tmpTexture = new Texture(textureName);
        TextureRegion[][] tmp = TextureRegion.split(tmpTexture, tmpTexture.getWidth()/ cols, tmpTexture.getHeight()/rows);
        TextureRegion[] tmpFrames = new TextureRegion[cols*rows];
        int index = 0;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                tmpFrames[index] = tmp[i][j];
                index++;
            }
        }
        return new Animation(1f,tmpFrames);
    }
    void shipSpriteChange(int a) {
        shipCurrentFrame = shipAnimation.getKeyFrame(a, true);
    }

    void numbersDraw(int points, CykaGame game) {
        int hundredthsPlace = points / 100;
        int tensPlace = (points - (hundredthsPlace * 100)) / 10;
        int onesPlace = points - ((hundredthsPlace * 100) + (tensPlace * 10));
        if (hundredthsPlace > 0) {
            TextureRegion numbersCurrentFrame = numbersAnimation.getKeyFrame(hundredthsPlace, true);
            game.batch.draw(numbersCurrentFrame, 592-96, 1024-84, 48, 84);
            game.batch.draw(hudPoints, 582-96, 1024-94, 160, 94);
        }
        else
            game.batch.draw(hudPoints, 582-48, 1024-94, 160, 94);
        TextureRegion numbersCurrentFrame = numbersAnimation.getKeyFrame(tensPlace, true);
        game.batch.draw(numbersCurrentFrame, 592-48, 1024-84, 48, 84);
        numbersCurrentFrame = numbersAnimation.getKeyFrame(onesPlace, true);
        game.batch.draw(numbersCurrentFrame, 616-24, 1024-84, 48, 84);
    }
    void dispose() {
        hudPoints.dispose();
        bulletSprite.dispose();
    }
}
