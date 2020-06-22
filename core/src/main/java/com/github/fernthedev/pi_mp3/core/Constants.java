package com.github.fernthedev.pi_mp3.core;

import com.badlogic.gdx.Gdx;
import com.github.fernthedev.pi_mp3.api.songs.Song;

public class Constants {

    private static Song debugSong;
    public static final int PORT = 3422;

    public static Song getDebugSong() {
        if (debugSong == null) debugSong = new Song(Gdx.files.classpath("sound.ogg"));

        return debugSong;
    }
}
