package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.pi_mp3.api.SongManager;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SongManagerImpl implements SongManager {
    private final LibGDXHackApp audioHandler;
    private final MP3Server server;

    private final LinkedList<Song> songHistory = new LinkedList<>();
    private final LinkedList<Song> songQueue = new LinkedList<>();

    private Song currentSong;
    private LoopMode loopMode;

    public SongManagerImpl(LibGDXHackApp audioHandler, MP3Server server) {
        this.audioHandler = audioHandler;
        this.server = server;
    }



    /**
     * Gets the song history list
     *
     * @return
     */
    @Override
    public List<Song> getSongHistory() {
        return songHistory;
    }

    /**
     * Returns a modifiable list of songs in the queue.
     *
     * @return
     */
    @Override
    public Queue<Song> getSongQueue() {
        return songQueue;
    }

    /**
     * Returns the song queue length
     *
     * @return
     */
    @Override
    public int getSongQueueLength() {
        return songQueue.size();
    }

    /**
     * Returns the song history length
     *
     * @return
     */
    @Override
    public int getSongHistoryLength() {
        return songHistory.size();
    }

    /**
     * Gets the song from the queue
     *
     * @param index
     * @return
     */
    @Override
    public Song getSongInQueue(int index) {
        return songQueue.get(index);
    }

    /**
     * Gets the song from the history
     *
     * @param index
     * @return
     */
    @Override
    public Song getSongInHistory(int index) {
        return songHistory.get(index);
    }

    /**
     * Checks if the song is in the queue
     *
     * @param song
     * @return
     */
    @Override
    public boolean isSongInQueue(Song song) {
        return songQueue.contains(song);
    }

    /**
     * Checks whether the song has played before
     *
     * @param song
     * @return
     */
    @Override
    public boolean hasPlayedBefore(Song song) {
        return songHistory.contains(song);
    }

    /**
     * Returns the playing song
     *
     * @return
     */
    @Override
    @Nullable
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Restarts the song
     */
    @Override
    public void replay() {
        currentSong.getMusic().setPosition(0);
    }

    /**
     * Sets song position to position
     *
     * @param position
     */
    @Override
    public void setPosition(float position) {
        currentSong.getMusic().setPosition(position);
    }

    /**
     * if playing, returns true
     *
     * @return
     */
    @Override
    public boolean isPlaying() {
        return currentSong.getMusic().isPlaying();
    }

    /**
     * Plays the song instantly
     *
     * @param song
     */
    @Override
    public void play(Song song) {
        audioHandler.runOnAudioThread(() -> {
            playNext(song);
            skip();
        });

    }

    /**
     * Pauses the song
     */
    @Override
    public void pause() {
        audioHandler.runOnAudioThread(() -> currentSong.getMusic().pause());
    }

    /**
     * Resumes the song
     */
    @Override
    public void resume() {
        audioHandler.runOnAudioThread(() -> currentSong.getMusic().pause());
    }

    /**
     * Adds the song to the first queue
     *
     * @param song
     */
    @Override
    public void playNext(Song song) {
        songQueue.addFirst(song);
    }

    /**
     * Adds the song to the queue
     *
     * @param song
     */
    @Override
    public void addSongToQueue(Song song) {
        songQueue.addLast(song);
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    @Override
    public void addSongToQueue(Collection<? extends Song> songs) {
        songQueue.addAll(songs);
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    @Override
    public void addSongToQueue(Song... songs) {
        songQueue.addAll(Arrays.asList(songs));
    }

    /**
     * Rewinds the song to the previous
     */
    @Override
    public void previousSong() {
        playNext(currentSong);

        if (songHistory.isEmpty()) throw new IndexOutOfBoundsException("Song history is empty");


        setCurrentSong(songHistory.pop());
    }

    /**
     * Goes back to the index of the previous song. The index is from {@link #getSongHistory()}
     *
     * @param index
     */
    @Override
    public void previousSong(int index) {
        if (index < 1) throw new IllegalArgumentException("Index cannot be less than 1");

        playNext(currentSong);

        if (songHistory.size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song history " + songHistory.size());

        Song song = null;

        for (int i = 0; i < index; i++) {
            song = songHistory.pop();
        }

        setCurrentSong(song);
    }

    /**
     * Goes back to the index of the previous song. The song is from {@link #getSongHistory()}
     *
     * @param song
     */
    @Override
    public void previousSong(Song song) {
        playNext(currentSong);

        int index = songHistory.indexOf(song);

        if (index < 1) throw new IllegalArgumentException("Cannot find song in history");

        previousSong(index);
    }

    /**
     * Skips to the next song
     */
    @Override
    public void skip() {
        if (songQueue.isEmpty()) throw new IllegalStateException("No songs in queue");


        Song song = currentSong;
        setCurrentSong(songQueue.pop());
        songHistory.push(song);
    }

    /**
     * Skips to the index of the next song
     * The index is from {@link #getSongQueue()}
     *
     * @param index
     */
    @Override
    public void skip(int index) {
        if (index < 1) throw new IllegalArgumentException("Index cannot be less than 1");
        if (songQueue.size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song queue " + songQueue.size());

        for (int i = 0; i < index; i++) {
            Song oldSong = currentSong;
            currentSong = songQueue.pop();
            songHistory.push(oldSong);
        }

        setCurrentSong(currentSong);
    }

    /**
     * Skips to the index of the next song
     * The song is from {@link #getSongQueue()}
     *
     * @param song
     */
    @Override
    public void skip(Song song) {
        int index = songQueue.indexOf(song);
        if (index < 1) throw new IllegalArgumentException("Cannot find song in history");
        skip(song);
    }

    /**
     * Moves the song from the queue to the index in queue
     *
     * @param song
     * @param index
     */
    @Override
    public void moveSong(Song song, int index) {
        int songIndex = songQueue.indexOf(song);
        if (songIndex < 1) throw new IllegalArgumentException("Cannot find song in history");

        moveSong(songIndex, index);
    }

    /**
     * Moves the song from the index in the queue to the new index in queue
     *
     * @param song
     * @param index
     */
    @Override
    public void moveSong(int song, int index) {
        if (songQueue.size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song queue " + songQueue.size());
        if (songQueue.size() < song) throw new IndexOutOfBoundsException("Index " + song + " is larger than song queue " + songQueue.size());

        Song song1 = songQueue.remove(song);

        songQueue.add(index, song1);
    }

    /**
     * Removes the song from queue
     *
     * @param song
     */
    @Override
    public void remove(int song) {
        songQueue.remove(song);
    }

    /**
     * Removes the song from queue
     *
     * @param song
     * @return true if song removed, false if no song in queue
     */
    @Override
    public boolean remove(Song song) {
        return songQueue.remove(song);
    }

    /**
     * Removes all songs from queue
     */
    @Override
    public void clear() {
        songQueue.clear();
    }

    /**
     * Shuffles all songs in queue in random order.
     */
    @Override
    public void shuffle() {
        ArrayList<Song> songArrayList = new ArrayList<>(songQueue);
        Collections.shuffle(songArrayList);
        songQueue.clear();
        songQueue.addAll(songArrayList);
    }

    @Override
    public void loop(LoopMode loopMode) {
        switch (loopMode) {
            case SONG:
                currentSong.getMusic().setLooping(true);
                break;
            case NONE:
                currentSong.getMusic().setLooping(false);
                break;
        }


        this.loopMode = loopMode;
    }


    private void setCurrentSong(@NonNull Song song) {
        currentSong = song;
        currentSong.getMusic().setOnCompletionListener(music -> {
            if (loopMode == LoopMode.SONG_ONCE) {
                loop(LoopMode.NONE);
            }

            if (!songQueue.isEmpty())
                skip();

            music.stop();
        });

        audioHandler.runOnAudioThread(() -> currentSong.getMusic().play());
    }

}
