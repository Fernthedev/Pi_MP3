package com.github.fernthedev.pi_mp3.api.songs;

import com.github.fernthedev.pi_mp3.api.exceptions.song.SongNotFoundException;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public interface SongManager {

    /**
     * Handles updates in audio thread.
     */
    void update();

    /**
     * This initializes the audio and/or connection to the music service.
     */
    void initialize();

    /**
     * Handles the end of the music
     */
    void dispose();

    /**
     * Gets the parent song manager
     * @return parent song manager
     */
    @Nullable
    SongManager getParent();

    /**
     * Gets a copy of the song factories
     * @return song factories
     */
    @NotNull
    Map<String, SongFactory> getSongFactories();

    /**
     *
     * @param name The name of the song factory
     * @return the song factory
     *
     * @throws IllegalArgumentException is thrown if the song manager does not exist
     */
    @NonNull
    SongFactory getSongFactory(@NotNull String name) throws IllegalArgumentException;

    /**
     * Avoid registering the same song factory with multiple names
     *
     * @param name The name of the song factory
     * @param songFactory The song factory instance
     *
     * @throws IllegalArgumentException is thrown if the song manager with the name already exists
     */
    void registerSongFactory(@NotNull String name, @NotNull SongFactory songFactory) throws IllegalArgumentException;

    /**
     * Returns the unique name of the song manager to identify it.
     * @return Unique name
     */
    default String getUniqueId() {
        return getName();
    }

    /**
     * Name of the song manager e.g OpenAL or Online
     * This is shown to the user
     * @return name
     */
    String getName();

    /**
     * Gets the song history list
     *
     * @return history
     */
    LinkedList<Song> getSongHistory();

    /**
     * Returns a modifiable list of songs in the queue.
     *
     * @return queue
     */
    LinkedList<Song> getSongQueue();


    /**
     * Returns the song queue length
     * @return queue length
     */
    default int getSongQueueLength() {
        return getSongQueue().size();
    }

    /**
     * Returns the song history length
     * @return history length
     */
    default int getSongHistoryLength() {
        return getSongHistory().size();
    }

    /**
     * Gets the song from the queue
     * @param index index
     * @return song
     */
    default Song getSongInQueue(int index) {
        return getSongQueue().get(index);
    }

    /**
     * Gets the song from the history
     * @param index index
     * @return song
     */
    default Song getSongInHistory(int index) {
        return getSongHistory().get(index);
    }

    /**
     * Checks if the song is in the queue
     * @param song song
     * @return true if in queue
     */
    default boolean isSongInQueue(@NonNull Song song) {
        return getSongQueue().contains(song);
    }

    /**
     * Checks whether the song has played before
     * @param song song
     * @return if song is in history
     */
    default boolean hasPlayedBefore(@NonNull Song song) {
        return getSongHistory().contains(song);
    }

    /**
     * Returns the playing song
     * @return current song
     */
    @Nullable
    Song getCurrentSong();

    /**
     * Restarts the song
     */
    void replay();

    /**
     *
     * @param volume volume of current song, percent of 0-100. May vary on implementation
     */
    CompletableFuture<Song> setVolume(float volume);

    /**
     *
     * @return volume of current song
     */
    float getVolume();

    /**
     * Sets song position to position
     * @param position position
     */
    CompletableFuture<Song> setPosition(float position);

    /**
     * Get position of song
     */
    float getPosition();

    /**
     * if playing, returns true
     * @return if playing
     */
    boolean isPlaying();

    /**
     * Plays the song instantly
     * Runs on Audio Thread
     * @param song
     */
    CompletableFuture<Song> play(@NonNull Song song);

    /**
     * Pauses the song
     * Runs on audio thread
     */
    CompletableFuture<Song> pause();

    /**
     * Resumes the song
     */
    CompletableFuture<Song> resume();

    /**
     * Adds the song to the first queue
     * @param song
     */
    @OverridingMethodsMustInvokeSuper
    default void playNext(@NonNull Song song) {
        getSongQueue().addFirst(song);
    }

    /**
     * Adds the song to the queue
     * @param song
     */
    @OverridingMethodsMustInvokeSuper
    default void addSongToQueue(@NonNull Song song) {
        getSongQueue().addLast(song);
    }

    /**
     * Adds the songs to the queue
     * @param songs
     */
    @OverridingMethodsMustInvokeSuper
    default void addSongToQueue(@NonNull Collection<? extends Song> songs) {
        getSongQueue().addAll(songs);
    }

    /**
     * Adds the songs to the queue
     * @param songs
     */
    @OverridingMethodsMustInvokeSuper
    default void addSongToQueue(Song... songs) {
        getSongQueue().addAll(Arrays.asList(songs));
    }

    @OverridingMethodsMustInvokeSuper
    default int getPositionInQueue(Song song) {
        return getSongQueue().indexOf(song);
    }

    @OverridingMethodsMustInvokeSuper
    default int getPositionInHistory(Song song) {
        return getSongHistory().indexOf(song);
    }

    /**
     * Rewinds the song to the previous
     */
    CompletableFuture<Song> previousSong();

    /**
     * Goes back to the index of the previous song. The index is from {@link #getSongHistory()}
     * @param index
     */
    CompletableFuture<Song> previousSong(int index);


    /**
     * Goes back to the index of the previous song. The song is from {@link #getSongHistory()}
     * @param song
     */
    CompletableFuture<Song> previousSong(Song song);

    /**
     * Skips to the next song
     */
    CompletableFuture<Song> skip();

    /**
     * Skips to the index of the next song
     * The index is from {@link #getSongQueue()}
     * @param index
     */
    CompletableFuture<Song> skip(int index);

    /**
     * Skips to the index of the next song
     * The song is from {@link #getSongQueue()}
     * @param song
     */
    CompletableFuture<Song> skip(@NonNull Song song);

    /**
     * Moves the song from the queue to the index in queue
     * @param song
     * @param index
     */
    default void moveSong(@NonNull Song song, int index) {
        int songIndex = getSongQueue().indexOf(song);
        if (songIndex < 1) throw new SongNotFoundException("Cannot find song in history");

        moveSong(songIndex, index);
    }

    /**
     * Moves the song from the index in the queue to the new index in queue
     * @param song
     * @param index
     */
    void moveSong(@NonNull int song, @NonNull int index);

    /**
     * Removes the song from queue
     * @param song
     */
    @OverridingMethodsMustInvokeSuper
    default void removeFromQueue(@NonNull int song) {
        getSongQueue().remove(song);
    }

    /**
     * Removes the song from queue
     * @param song
     * @return true if song removed, false if no song in queue
     */
    @OverridingMethodsMustInvokeSuper
    default boolean removeFromQueue(@NonNull Song song) {
        return getSongQueue().remove(song);
    }

    /**
     * Removes all songs from queue
     */
    @OverridingMethodsMustInvokeSuper
    default void clear() {
        getSongQueue().clear();
    }

    /**
     * Shuffles all songs in queue in random order.
     */
    default void shuffle() {
        ArrayList<Song> songArrayList = new ArrayList<>(getSongQueue());
        Collections.shuffle(songArrayList);
        getSongQueue().clear();
        getSongQueue().addAll(songArrayList);
    }

    /**
     * Set loop mode
     * @param loopMode
     */
    void loop(@NonNull LoopMode loopMode);

    /**
     * Used internally
     * @param song
     */
    @Deprecated
    void setCurrentSong(@NonNull Song song);

    /**
     * @deprecated USED FOR TESTING ONLY
     * DO NOT USE
     */
    @Deprecated
    void setNull();

    enum LoopMode {
        SONG,
        SONG_ONCE,
        NONE;

        boolean isSong() {return this == SONG || this == SONG_ONCE; }
    }


}
