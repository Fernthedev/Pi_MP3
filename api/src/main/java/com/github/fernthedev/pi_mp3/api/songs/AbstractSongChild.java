package com.github.fernthedev.pi_mp3.api.songs;

import com.github.fernthedev.pi_mp3.api.MP3Pi;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public abstract class AbstractSongChild implements SongManager {
    protected final LinkedList<@NonNull Song> songHistory = new LinkedList<>();
    protected final LinkedList<@NonNull Song> songQueue = new LinkedList<>();

    protected Song currentSong;
    protected LoopMode loopMode;
    protected final SongManager parent;

    @NonNull
    protected String name;

    protected AbstractSongChild(@NonNull SongManager parent, @NonNull String name) {
        this.parent = parent;
        this.name = name;
    }

    /**
     * Gets the parent song manager
     *
     * @return parent song manager
     */
    @NonNull
    @Override
    public SongManager getParent() {
        return parent;
    }

    /**
     * Name of the song manager e.g OpenAL or Online
     *
     * @return name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the song history list
     *
     * @return history
     */
    @Override
    public LinkedList<Song> getSongHistory() {
        return songHistory;
    }

    /**
     * Returns a modifiable list of songs in the queue.
     *
     * @return queue
     */
    @Override
    public LinkedList<Song> getSongQueue() {
        return songQueue;
    }

    /**
     * Returns the playing song
     *
     * @return current song
     */
    @Nullable
    @Override
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Set loop mode
     *
     * @param loopMode
     */
    @Override
    public void loop(@NonNull LoopMode loopMode) {
        this.loopMode = loopMode;
    }

    /**
     * @deprecated USED FOR TESTING ONLY
     * DO NOT USE
     */
    @Override
    public void setNull() {
        currentSong = null;

        if (!MP3Pi.isTestMode()) {
            throw new IllegalStateException("THIS IS A TEST ONLY METHOD. DO NOT USE");
        }

        currentSong = null;
    }
}
