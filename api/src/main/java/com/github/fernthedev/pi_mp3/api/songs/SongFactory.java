package com.github.fernthedev.pi_mp3.api.songs;

import com.github.fernthedev.pi_mp3.api.exceptions.song.SongNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.UUID;

/**
 * Returns instances of songs
 */
public interface SongFactory {


    /**
     * Gets a song based on the parameter given
     *
     * This method depends on it's implementation
     *
     * @param file The file of the song
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found or is not a proper song file.
     * @throws FileNotFoundException if the file is not found
     */
    Song getSong(File file) throws FileNotFoundException, SongNotFoundException;

    /**
     * Gets a song based on the parameter given
     *
     * This method depends on it's implementation
     *
     * @param name the ID that the song is based on
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found
     */
    Song getSong(String name) throws SongNotFoundException;


    /**
     * Gets a song based on the parameter given
     *
     * This method depends on it's implementation
     *
     * @param id the ID that the song is based on
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found
     */
    Song getSong(int id) throws SongNotFoundException;

    /**
     * Gets a song based on the parameter given
     *
     * This method depends on it's implementation
     *
     * @param uuid the ID that the song is based on
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found
     */
    Song getSong(UUID uuid) throws SongNotFoundException;

    /**
     * This returns a list of possible methods to get a song instance
     *
     * @return list of methods
     */
    @NotNull
    Set<Class<?>> supportedSongMethods();

}
