package me.samuki.cykacommander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundsBase {

    public Sound deathSound;

    public SoundsBase() {
        deathSound = Gdx.audio.newSound(Gdx.files.internal("cyka.ogg"));
    }

    public void dispose() {
        deathSound.dispose();
    }
}
