package com.github.fernthedev.pi_mp3.core.audio.test;

import com.badlogic.gdx.backends.lwjgl3.audio.mock.MockMusic;

public class FakeMusic extends MockMusic {
    private boolean isLooping, isPlaying;
    private float volume;
    private float position;

    @Override
    public void play() {
        isPlaying = true;
    }

    @Override
    public void pause() {
        isPlaying = false;
    }

    @Override
    public void stop() {
        isPlaying = false;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void setLooping (boolean isLooping) {
        this.isLooping = isLooping;
    }

    @Override
    public boolean isLooping () {
        return isLooping;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public float getVolume() {
        return volume;
    }


    @Override
    public void setPosition(float position) {
        this.position = position;
    }

    @Override
    public float getPosition() {
        return position;
    }

    @Override
    public void dispose() {
        stop();
    }
}
