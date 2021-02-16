package com.github.fernthedev.pi_mp3.core;

import com.badlogic.gdx.Gdx;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.core.audio.songs.FileSong;

public class Constants {

    private static Song debugSong;
    public static final int PORT = 3422;

    public static Song getDebugSong() {
        if (debugSong == null) debugSong = new FileSong(Gdx.files.classpath("sound.ogg"));

        return debugSong;
    }
}
