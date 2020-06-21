package com.github.fernthedev.pi_mp3.api.songs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALMusic;
import com.badlogic.gdx.files.FileHandle;
import com.github.fernthedev.lightchat.core.StaticHandler;
import lombok.Getter;

import java.time.Duration;

public class Song {

    /**
     * @deprecated Use internally
     */
    @Getter
    @Deprecated
    private final OpenALMusic music;

    /**
     * TODO: IMPLEMENT
     */
    @Deprecated
    @Getter
    private Duration duration;

    public Song(FileHandle file) {
        this(file, (OpenALMusic) Gdx.audio.newMusic(file));
    }

    public Song(FileHandle file, OpenALMusic music) {
        this.music = music;

        StaticHandler.getCore().getLogger().debug("Duration of sound: " + duration);
    }

    public float getPosition() {
        return music.getPosition();
    }



}
