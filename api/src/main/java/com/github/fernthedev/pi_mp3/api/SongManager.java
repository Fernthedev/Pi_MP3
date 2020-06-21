package com.github.fernthedev.pi_mp3.api;

import com.github.fernthedev.pi_mp3.api.songs.Song;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

public interface SongManager {

    /**
     * Gets the song history list
     * @return
     */
    List<Song> getSongHistory();

    /**
     * Returns a modifiable list of songs in the queue.
     * @return
     */
    Queue<Song> getSongQueue();

    /**
     * Returns the song queue length
     * @return
     */
    int getSongQueueLength();

    /**
     * Returns the song history length
     * @return
     */
    int getSongHistoryLength();

    /**
     * Gets the song from the queue
     * @param index
     * @return
     */
    Song getSongInQueue(int index);

    /**
     * Gets the song from the history
     * @param index
     * @return
     */
    Song getSongInHistory(int index);

    /**
     * Checks if the song is in the queue
     * @param song
     * @return
     */
    boolean isSongInQueue(@NonNull Song song);

    /**
     * Checks whether the song has played before
     * @param song
     * @return
     */
    boolean hasPlayedBefore(@NonNull Song song);

    /**
     * Returns the playing song
     * @return
     */
    @Nullable
    Song getCurrentSong();

    /**
     * Restarts the song
     */
    void replay();

    /**
     * Sets song position to position
     * @param position
     */
    void setPosition(float position);

    /**
     * if playing, returns true
     * @return
     */
    boolean isPlaying();

    /**
     * Plays the song instantly
     * @param song
     */
    void play(@NonNull Song song);

    /**
     * Pauses the song
     */
    void pause();

    /**
     * Resumes the song
     */
    void resume();

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

    /**
     * Rewinds the song to the previous
     */
    void previousSong();

    /**
     * Goes back to the index of the previous song. The index is from {@link #getSongHistory()}
     * @param index
     */
    void previousSong(int index);


    /**
     * Goes back to the index of the previous song. The song is from {@link #getSongHistory()}
     * @param song
     */
    void previousSong(Song song);

    /**
     * Skips to the next song
     */
    void skip();

    /**
     * Skips to the index of the next song
     * The index is from {@link #getSongQueue()}
     * @param index
     */
    void skip(int index);

    /**
     * Skips to the index of the next song
     * The song is from {@link #getSongQueue()}
     * @param song
     */
    void skip(@NonNull Song song);

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

    void loop(@NonNull LoopMode loopMode);

    enum LoopMode {
        SONG,
        SONG_ONCE,
        NONE;

        boolean isSong() {return this == SONG || this == SONG_ONCE; }
    }
}
