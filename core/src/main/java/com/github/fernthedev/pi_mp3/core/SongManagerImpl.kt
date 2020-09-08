package com.github.fernthedev.pi_mp3.core;

import com.github.fernthedev.fernutils.thread.ThreadUtils;
import com.github.fernthedev.pi_mp3.api.MP3Pi;
import com.github.fernthedev.pi_mp3.api.events.SongActionEvent;
import com.github.fernthedev.pi_mp3.api.exceptions.song.NoSongPlayingException;
import com.github.fernthedev.pi_mp3.api.exceptions.song.SongNotFoundException;
import com.github.fernthedev.pi_mp3.api.songs.Song;
import com.github.fernthedev.pi_mp3.api.songs.SongAction;
import com.github.fernthedev.pi_mp3.api.songs.SongManager;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("JavaDoc")
public class SongManagerImpl implements SongManager, Runnable {
    private final LibGDXHackApp audioHandler;
    private final MP3Server server;

    private final LinkedList<@NonNull Song> songHistory = new LinkedList<>();
    private final LinkedList<@NonNull Song> songQueue = new LinkedList<>();

    private Song currentSong;
    private LoopMode loopMode;

    public SongManagerImpl(LibGDXHackApp audioHandler, MP3Server server) {
        this.audioHandler = audioHandler;
        this.server = server;
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
     * @return
     */
    @Override
    public LinkedList<Song> getSongQueue() {
        return songQueue;
    }

    /**
     * Returns the song queue length
     *
     * @return queue length
     */
    @Override
    public int getSongQueueLength() {
        return songQueue.size();
    }

    /**
     * Returns the song history length
     *
     * @return history length
     */
    @Override
    public int getSongHistoryLength() {
        return songHistory.size();
    }

    /**
     * Gets the song from the queue
     *
     * @param index index
     * @return song
     */
    @Override
    public Song getSongInQueue(int index) {
        return songQueue.get(index);
    }

    /**
     * Gets the song from the history
     *
     * @param index index
     * @return song
     */
    @Override
    public Song getSongInHistory(int index) {
        return songHistory.get(index);
    }

    /**
     * Checks if the song is in the queue
     *
     * @param song song
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
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.GET_SONG));
        return currentSong;
    }

    /**
     * Restarts the song
     */
    @Override
    public void replay() {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.REPLAY));
        currentSong.getMusic().setPosition(0);
    }

    /**
     * @param volume volume of current song
     */
    @Override
    public CompletableFuture<Song> setVolume(float volume) {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to set volume");

        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().setVolume(volume);
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return volume of current song
     */
    @Override
    public float getVolume() {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to get volume");

        return currentSong.getMusic().getVolume();
    }

    /**
     * Sets song position to position
     *
     * @param position
     * @return
     */
    @Override
    public CompletableFuture<Song> setPosition(float position) {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to set position");

        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SET_POSITION));
        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().setPosition(position);
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get position of song
     */
    @Override
    public float getPosition() {
        if (currentSong == null)
            throw new NoSongPlayingException("A song must be playing to get volume");

        return currentSong.getMusic().getPosition();
    }

    /**
     * if playing, returns true
     *
     * @return
     */
    @Override
    public boolean isPlaying() {
        return currentSong == null || currentSong.getMusic().isPlaying();
    }

    /**
     * Plays the song instantly
     *
     * @param song
     * @return
     */
    @Override
    public CompletableFuture<Song> play(Song song) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.PLAY));
        try {
            return audioHandler.runOnAudioThread(() -> {
                setCurrentSong(song);
    //            playNext(song);
    //            skip();
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Pauses the song
     * @return
     */
    @Override
    public CompletableFuture<Song> pause() {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.PAUSE));
        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().pause();
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resumes the song
     * @return
     */
    @Override
    public CompletableFuture<Song> resume() {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.RESUME));
        try {
            return audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().play();
                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the song to the first queue
     *
     * @param song
     */
    @Override
    public void playNext(@NonNull Song song) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.ADD_TO_PLAY_NEXT.addParameters(song)));
        songQueue.addFirst(song);
    }

    /**
     * Adds the song to the queue
     *
     * @param song
     */
    @Override
    public void addSongToQueue(@NonNull Song song) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.ADD_TO_QUEUE.addParameters(song)));
        songQueue.addLast(song);
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    @Override
    public void addSongToQueue(Collection<? extends Song> songs) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.ADD_TO_QUEUE.addParameters(songs)));
        songQueue.addAll(songs);
    }

    /**
     * Adds the songs to the queue
     *
     * @param songs
     */
    @Override
    public void addSongToQueue(@NonNull Song... songs) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.ADD_TO_QUEUE.addParameters(songs)));
        songQueue.addAll(Arrays.asList(songs));
    }

    @Override
    public int getPositionInQueue(@NonNull Song song) {
        return songQueue.indexOf(song);
    }

    @Override
    public int getPositionInHistory(@NonNull Song song) {
        return songHistory.indexOf(song);
    }

    /**
     * Rewinds the song to the previous
     * @return
     */
    @Override
    public CompletableFuture<Song> previousSong() {
        return previousSong(1);
//        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.PREVIOUS_SONG));
//
//        try {
//            return audioHandler.runOnAudioThread(() -> {
//                playNext(currentSong);
//
//                if (songHistory.isEmpty()) throw new NoSongsException("Song history is empty");
//
//
//                setCurrentSong(songHistory.pop());
//
//                return currentSong;
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Goes back to the index of the previous song. The index is from {@link #getSongHistory()}
     *
     * @param index
     * @return
     */
    @Override
    public CompletableFuture<Song> previousSong(int index) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.PREVIOUS_SONG.addParameters(index)));

        if (index < 1) throw new IllegalArgumentException("Index cannot be less than 1");
        if (songHistory.size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song history " + songHistory.size());

        try {
            return audioHandler.runOnAudioThread(() -> {
                if (currentSong != null) {
                    playNext(currentSong);
                    currentSong.getMusic().stop();
                }


                Song song;

                for (int i = 0; i < index; i++) {
                    song = songHistory.pop();

                    playNext(song);
                }

                skip().get(); // Since play next adds to the top queue, skip to it.

//                setCurrentSong(song);

                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Goes back to the index of the previous song. The song is from {@link #getSongHistory()}
     *
     * @param song
     */
    @Override
    public CompletableFuture<Song> previousSong(@NonNull Song song) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.PREVIOUS_SONG.addParameters(song)));
        int index = songHistory.indexOf(song);

        if (index < 1) throw new SongNotFoundException("Cannot find song in history");

        return previousSong(index);
    }

    /**
     * Skips to the next song
     */
    @Override
    public CompletableFuture<Song> skip() {
        return skip(1);
//        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SKIP));
//        if (songQueue.isEmpty()) throw new NoSongsException("No songs in queue");
//
//        try {
//            return audioHandler.runOnAudioThread(() -> {
//                Song song = currentSong;
//                setCurrentSong(songQueue.pop());
//                songHistory.push(song);
//
//                return song;
//            });
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Skips to the index of the next song
     * The index is from {@link #getSongQueue()}
     *
     * @param index
     * @return
     */
    @Override
    public CompletableFuture<Song> skip(int index) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SKIP.addParameters(index)));
        if (index < 1) throw new IllegalArgumentException("Index cannot be less than 1");
        if (songQueue.size() < index) throw new IndexOutOfBoundsException("Index " + index + " is larger than song queue " + songQueue.size());

        try {
            return audioHandler.runOnAudioThread(() -> {
                if (currentSong != null)
                    currentSong.getMusic().stop();

                for (int i = 0; i < index; i++) {
                    Song oldSong = currentSong; // Get current song playing
                    currentSong = songQueue.pop(); // Get next song to play and set to play

                    if (oldSong != null)
                        songHistory.push(oldSong); // Put old song in history
                }

                setCurrentSong(currentSong);

                return currentSong;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Skips to the index of the next song
     * The song is from {@link #getSongQueue()}
     *
     * @param song
     * @return
     */
    @Override
    public CompletableFuture<Song> skip(@NonNull Song song) {
        int index = songQueue.indexOf(song);
        if (index < 1) throw new SongNotFoundException("Cannot find song in history");
        return skip(song);
    }

    /**
     * Moves the song from the queue to the index in queue
     *
     * @param song
     * @param index
     */
    @Override
    public void moveSong(@NonNull Song song, int index) {
        int songIndex = songQueue.indexOf(song);
        if (songIndex < 1) throw new SongNotFoundException("Cannot find song in history");

        moveSong(songIndex, index);
    }

    /**
     * Moves the song from the index in the queue to the new index in queue
     *
     * @param song
     * @param index
     */
    @Override
    public void moveSong(@NonNull int song, int index) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.MOVE_SONG.addParameters(song, index)));

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
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.REMOVE_SONG.addParameters(song)));
        songQueue.remove(song);
    }

    /**
     * Removes the song from queue
     *
     * @param song
     * @return true if song removed, false if no song in queue
     */
    @Override
    public boolean remove(@NonNull Song song) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.REMOVE_SONG.addParameters(song)));
        return songQueue.remove(song);
    }

    /**
     * Removes all songs from queue
     */
    @Override
    public void clear() {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.CLEAR_QUEUE));
        songQueue.clear();
    }

    /**
     * Shuffles all songs in queue in random order.
     */
    @Override
    public void shuffle() {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SHUFFLE));
        ArrayList<Song> songArrayList = new ArrayList<>(songQueue);
        Collections.shuffle(songArrayList);
        songQueue.clear();
        songQueue.addAll(songArrayList);
    }

    @Override
    public void loop(LoopMode loopMode) {
        MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SET_LOOP.addParameters(loopMode)));
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
        ThreadUtils.runAsync(
                () -> MP3Server.getServer().getPluginManager().callEvent(new SongActionEvent(currentSong, SongAction.SET_SONG.addParameters(song))),
                server.getExecutorService()
        );
        if (currentSong != null)
            currentSong.getMusic().dispose();

        currentSong = song;
        currentSong.getMusic().setOnCompletionListener(music -> {
            songHistory.push(currentSong);
            if (loopMode == LoopMode.SONG_ONCE) {
                loop(LoopMode.NONE);
            }

            if (!songQueue.isEmpty())
                skip();
            else
                currentSong = null;

            music.stop();
            music.dispose();
        });

        try {
            audioHandler.runOnAudioThread(() -> {
                currentSong.getMusic().play();
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated Internal use
     */
    @Deprecated
    public void update() {
        if (currentSong == null && !songQueue.isEmpty()) {
            skip();
        }
    }

    /**
     * For testing only
     */
    @Deprecated
    public void setNull() {
        if (!MP3Pi.isTestMode()) {
            throw new IllegalStateException("THIS IS A TEST ONLY METHOD. DO NOT USE");
        }

        currentSong = null;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (MP3Server.getServer().isRunning()) {
            update();
            try {
                Thread.sleep(30); // AVOID USING UNNECESSARY CYCLES ON UPDATE
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (currentSong != null) {
            currentSong.getMusic().stop();
            currentSong.getMusic().dispose();
        }
    }
}
