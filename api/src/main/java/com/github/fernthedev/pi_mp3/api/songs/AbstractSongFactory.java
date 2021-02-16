package com.github.fernthedev.pi_mp3.api.songs;

import com.github.fernthedev.pi_mp3.api.exceptions.song.SongNotFoundException;
import com.github.fernthedev.pi_mp3.api.exceptions.song.factory.SongFactoryNotSupportedException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.UUID;

public class AbstractSongFactory implements SongFactory {
    private final Set<Class<?>> supportedTypes;

    public AbstractSongFactory(Set<Class<?>> supportedTypes) {
        this.supportedTypes = supportedTypes;
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
    @Override
    public Song getSong(File file) throws FileNotFoundException, SongNotFoundException {
        if (!supportedTypes.contains(File.class)) throw factoryException(File.class);

        return null;
    }

    /**
     * Gets a song based on the parameter given
     * <p>
     * This method depends on it's implementation
     *
     * @param name the ID that the song is based on
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found
     */
    @Override
    public Song getSong(String name) throws SongNotFoundException {
        if (!supportedTypes.contains(String.class)) throw factoryException(String.class);

        return null;
    }

    /**
     * Gets a song based on the parameter given
     * <p>
     * This method depends on it's implementation
     *
     * @param id the ID that the song is based on
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found
     */
    @Override
    public Song getSong(int id) throws SongNotFoundException {
        if (!supportedTypes.contains(int.class) && !supportedTypes.contains(Integer.class)) throw factoryException(int.class);

        return null;
    }

    /**
     * Gets a song based on the parameter given
     * <p>
     * This method depends on it's implementation
     *
     * @param uuid the ID that the song is based on
     * @return the song, never null
     * @throws SongNotFoundException is thrown if the song is not found
     */
    @Override
    public Song getSong(UUID uuid) throws SongNotFoundException {
        if (!supportedTypes.contains(UUID.class)) throw factoryException(UUID.class);

        return null;
    }

    /**
     * This returns a list of possible methods to get a song instance
     *
     * @return list of methods
     */
    @NotNull
    @Override
    public Set<Class<?>> supportedSongMethods() {
        return supportedTypes;
    }

    private SongFactoryNotSupportedException factoryException(Class<?> type) {
        return new SongFactoryNotSupportedException("The song factory " + getClass().getName() + " does not support " + type.getName());
    }
}
