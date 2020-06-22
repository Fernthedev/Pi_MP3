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

    @Getter
    private final FileHandle fileHandle;

    /**
     * TODO: IMPLEMENT
     */
    @Deprecated
    @Getter
    private Duration duration;

    public Song(FileHandle file) {
        this.fileHandle = file;
        this.music = (OpenALMusic) Gdx.audio.newMusic(file);

        StaticHandler.getCore().getLogger().debug("Duration of sound: " + duration);
    }

    public float getPosition() {
        return music.getPosition();
    }



}
