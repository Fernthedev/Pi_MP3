package com.github.fernthedev.pi_mp3.api.songs;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public interface SongManager {

    /**
     * Gets the song history list
     * @return history
     */
    LinkedList<Song> getSongHistory();

    /**
     * Returns a modifiable list of songs in the queue.
     * @return queue
     */
    LinkedList<Song> getSongQueue();

    /**
     * Returns the song queue length
     * @return queue length
     */
    int getSongQueueLength();

    /**
     * Returns the song history length
     * @return history length
     */
    int getSongHistoryLength();

    /**
     * Gets the song from the queue
     * @param index index
     * @return song
     */
    Song getSongInQueue(int index);

    /**
     * Gets the song from the history
     * @param index index
     * @return song
     */
    Song getSongInHistory(int index);

    /**
     * Checks if the song is in the queue
     * @param song song
     * @return true if in queue
     */
    boolean isSongInQueue(@NonNull Song song);

    /**
     * Checks whether the song has played before
     * @param song song
     * @return if song is in history
     */
    boolean hasPlayedBefore(@NonNull Song song);

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
     * @param volume volume of current song
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
    void playNext(@NonNull Song song);

    /**
     * Adds the song to the queue
     * @param song
     */
    void addSongToQueue(@NonNull Song song);

    /**
     * Adds the songs to the queue
     * @param songs
     */
    void addSongToQueue(@NonNull Collection<? extends Song> songs);

    /**
     * Adds the songs to the queue
     * @param songs
     */
    void addSongToQueue(Song... songs);

    int getPositionInQueue(Song song);

    int getPositionInHistory(Song song);

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
    void moveSong(@NonNull Song song, int index);

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
    void remove(@NonNull int song);

    /**
     * Removes the song from queue
     * @param song
     * @return true if song removed, false if no song in queue
     */
    boolean remove(@NonNull Song song);

    /**
     * Removes all songs from queue
     */
    void clear();

    /**
     * Shuffles all songs in queue in random order.
     */
    void shuffle();

    /**
     * Set loop mode
     * @param loopMode
     */
    void loop(@NonNull LoopMode loopMode);

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
