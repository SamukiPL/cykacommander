package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

class SoundsBase {

    Sound deathSound;

    SoundsBase() {
        deathSound = Gdx.audio.newSound(Gdx.files.internal("cyka.ogg"));
    }

    void dispose() {
        deathSound.dispose();
    }
}
