package com.github.fernthedev.pi_mp3.api.songs;

import com.badlogic.gdx.audio.Music;
import lombok.Getter;

public class Song {

    /**
     * @deprecated Use internally
     */
    @Getter
    @Deprecated
    private Music music;

    public Song(Music music) {
        this.music = music;
    }


}
