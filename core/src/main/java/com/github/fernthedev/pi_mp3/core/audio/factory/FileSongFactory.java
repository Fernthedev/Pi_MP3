package com.github.fernthedev.pi_mp3.core.audio.factory;

import com.badlogic.gdx.files.FileHandle;
import com.github.fernthedev.pi_mp3.api.exceptions.song.SongNotFoundException;
import com.github.fernthedev.pi_mp3.api.songs.AbstractSongFactory;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.core.audio.songs.FileSong;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;

public class FileSongFactory extends AbstractSongFactory {
    public FileSongFactory() {
        super(new HashSet<>(Collections.singleton(File.class)));
    }

    public static final String NAME = "File";

    /**
     * Gets a song based on the parameter given
     * <p>
     * This method depends on it's implementation
     *
     * @param file The file of the song
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found or is not a proper song file.
     * @throws FileNotFoundException if the file is not found
     */
    @Override
    public Song getSong(File file) throws FileNotFoundException, SongNotFoundException {
        if (!file.exists()) throw new FileNotFoundException("Could not find file " + file.getPath());

        return new FileSong(file);
    }

    /**
     * Gets a song based on the parameter given
     * <p>
     * This method depends on it's implementation
     *
     * @param file The file of the song
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found or is not a proper song file.
     * @throws FileNotFoundException if the file is not found
     */
    public Song getSong(FileHandle file) throws FileNotFoundException, SongNotFoundException {
        if (!file.exists()) throw new FileNotFoundException("Could not find file " + file.path());

        return new FileSong(file);
    }
}
