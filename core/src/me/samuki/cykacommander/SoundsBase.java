package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

class SoundsBase {

    Sound buttonClickSound;
    Sound deathSound;
    Sound pointSound;
    Sound shotSound;
    Sound coinHitSound;
    Sound canBuySound;
    Sound cantBuySound;

    SoundsBase(int whichScreen) {
        //0 = GameScreen, 1 = MenuScreen / DeathScreen / SettingsScreen, 2 = ShopScreen
        if(whichScreen == 1 || whichScreen == 2)
            buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.ogg"));
        if(whichScreen == 0) {
            deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/cyka.ogg"));
            pointSound = Gdx.audio.newSound(Gdx.files.internal("sounds/point.ogg"));
            shotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shot.ogg"));
            coinHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin_hit.ogg"));
        }
        if(whichScreen == 2) {
            canBuySound = Gdx.audio.newSound(Gdx.files.internal("sounds/can_buy.ogg"));
            cantBuySound = Gdx.audio.newSound(Gdx.files.internal("sounds/cant_buy.ogg"));
        }
    }

    void dispose() {
        buttonClickSound.dispose();
        deathSound.dispose();
        pointSound.dispose();
        shotSound.dispose();
        coinHitSound.dispose();
        canBuySound.dispose();
        cantBuySound.dispose();
    }
}
